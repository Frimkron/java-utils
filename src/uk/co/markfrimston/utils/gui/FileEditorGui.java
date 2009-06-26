package uk.co.markfrimston.utils.gui;

import javax.swing.*;
import javax.swing.event.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;

// TODO update internal frame titles when save as changes filename
// TODO disable close, save, save as etc when no file open
// TODO implement undo, redo
// TODO implement cut, copy, paste
// TODO maximise files when created
// TODO window menu
// TODO file filters
// TODO recording unsaved changes

public abstract class FileEditorGui extends JFrame 
	implements InternalFrameListener, WindowListener
{
	protected JDesktopPane desktopPane;
	protected Map<JInternalFrame,FileEditorFile> files = new HashMap<JInternalFrame,FileEditorFile>();
	protected File currentDirectory;
	
	protected JMenuItem miFileNew;
	protected JMenuItem miFileOpen;
	protected JMenuItem miFileClose;
	protected JMenuItem miFileSave;
	protected JMenuItem miFileSaveAs;
	protected JMenuItem miFileExit;
	
	protected JMenuItem miEditUndo;
	protected JMenuItem miEditRedo;
	protected JMenuItem miEditCut;
	protected JMenuItem miEditCopy;
	protected JMenuItem miEditPaste;
	
	public FileEditorGui()
	{
		this.desktopPane = new JDesktopPane();
		this.getContentPane().add(desktopPane);
		
		JMenuBar menuBar = new JMenuBar();
		setupMenu(menuBar);
		this.setJMenuBar(menuBar);
		
		this.setSize(new Dimension(640,480));
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(this);
		this.setVisible(true);
	}
	
	protected void setupMenu(JMenuBar menuBar)
	{
		JMenu mFile = new JMenu("File");
		miFileNew = new JMenuItem("New");
		miFileNew.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				createNewFileAndSetupGui();
			}
		});
		mFile.add(miFileNew);
		miFileOpen = new JMenuItem("Open");
		miFileOpen.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				openFileDialog();
			}
		});
		mFile.add(miFileOpen);
		miFileClose = new JMenuItem("Close");
		miFileClose.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				JInternalFrame frame = desktopPane.getSelectedFrame();
				if(frame!=null){
					saveAndCloseIfConfirmed(frame);
				}
			}
		});
		mFile.add(miFileClose);
		mFile.add(new JSeparator());
		miFileSave = new JMenuItem("Save");
		miFileSave.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				FileEditorFile f = currentlySelectedFile();
				if(f!=null){
					saveOrSaveAsFile(f);
				}
			}
		});
		mFile.add(miFileSave);
		miFileSaveAs = new JMenuItem("Save As");
		miFileSaveAs.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				FileEditorFile f = currentlySelectedFile();
				if(f!=null){
					saveFileAsDialog(f);
				}
			}
		});
		mFile.add(miFileSaveAs);
		miFileExit = new JMenuItem("Exit");
		miFileExit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				saveAllAndExitIfConfirmed();
			}
		});
		mFile.add(miFileExit);
		menuBar.add(mFile);
		
		JMenu mEdit = new JMenu("Edit");
		miEditUndo = new JMenuItem("Undo");
		mEdit.add(miEditUndo);
		miEditRedo = new JMenuItem("Redo");
		mEdit.add(miEditRedo);
		mEdit.add(new JSeparator());
		miEditCut = new JMenuItem("Cut");
		mEdit.add(miEditCut);
		miEditCopy = new JMenuItem("Copy");
		mEdit.add(miEditCopy);
		miEditPaste = new JMenuItem("Paste");
		mEdit.add(miEditPaste);
		menuBar.add(mEdit);
	}
	
	protected abstract FileEditorFile createNewFile() throws FileEditorFileException;
	
	protected abstract void saveFile(FileEditorFile file) throws FileEditorFileException;
	
	protected abstract FileEditorFile loadFile(File filename) throws FileEditorFileException;
	
	protected FileEditorFile currentlySelectedFile()
	{
		JInternalFrame currentFrame = this.desktopPane.getSelectedFrame();
		return files.get(currentFrame);
	}
	
	protected void createNewFileAndSetupGui()
	{
		try
		{
			FileEditorFile f = createNewFile();
			setupFileGui(f);
		}
		catch(FileEditorFileException e)
		{
			showError("Failed to create new file: "+e.getMessage());
		}
	}
	
	protected void setupFileGui(FileEditorFile f)
	{
		this.files.put(f.getFrame(), f);
		this.desktopPane.add(f.getFrame());
		f.getFrame().setTitle(f.getFile().getName());
		f.getFrame().setDefaultCloseOperation(JInternalFrame.DO_NOTHING_ON_CLOSE);
		f.getFrame().addInternalFrameListener(this);
		f.getFrame().setVisible(true);
	}

	protected void saveAllAndExitIfConfirmed()
	{
		boolean doExit = true;
		Set<JInternalFrame> keys = new HashSet<JInternalFrame>();
		keys.addAll(files.keySet());
		for(JInternalFrame frame : keys)
		{
			if(!saveAndCloseIfConfirmed(frame))
			{
				doExit = false;
				break;
			}
		}
		if(doExit)
		{
			exit();
		}
	}
	
	protected void exit()
	{
		this.setVisible(false);
		this.dispose();
		System.exit(0);
	}
	
	protected boolean saveAndCloseIfConfirmed(JInternalFrame frame)
	{
		FileEditorFile file = files.get(frame);
		if(file!=null)
		{
			if(file.hasUnsavedChanges())
			{	
				int resp = JOptionPane.showConfirmDialog(this, 
						"Save changes to "+file.getFile().getName()+"?","Save Changes",
						JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
				if(resp != JOptionPane.CANCEL_OPTION)
				{
					if(resp == JOptionPane.YES_OPTION)
					{
						if(saveOrSaveAsFile(file))
						{
							closeFile(file);
							return true;
						}				
						else
						{
							return false;
						}
					}
					else
					{					
						closeFile(file);
						return true;
					}
				}
				else
				{
					return false;
				}
			}
			else
			{
				closeFile(file);
				return true;
			}
		}
		else
		{
			return true;
		}
	}
	
	protected void closeFile(FileEditorFile file)
	{
		JInternalFrame frame = file.getFrame();
		files.remove(frame);
		frame.setVisible(false);
		frame.dispose();
	}
	
	protected boolean saveOrSaveAsFile(FileEditorFile file)
	{
		if(!file.getFile().exists())
		{
			return saveFileAsDialog(file);
		}
		else
		{
			try{
				saveFile(file);
				file.setUnsavedChanges(false);
				return true;
			}
			catch(FileEditorFileException e)
			{
				showError("Failed to save file: "+e.getMessage());
				return false;
			}			
		}
	}
	
	protected boolean saveFileAsDialog(FileEditorFile file)
	{
		JFileChooser jfc = new JFileChooser();
		jfc.setSelectedFile(file.getFile());
		if(jfc.showSaveDialog(this)==JFileChooser.APPROVE_OPTION)
		{
			File selected = jfc.getSelectedFile();
			currentDirectory = selected.getParentFile();
			if(selected.exists())
			{
				if(JOptionPane.showConfirmDialog(this, "This file already exists. Overwrite?","Overwrite",
						JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE)!=JOptionPane.OK_OPTION)
				{
					return false;
				}
			}
			file.setFile(jfc.getSelectedFile());
			try{
				saveFile(file);
				file.setUnsavedChanges(false);
				return true;
			}
			catch(FileEditorFileException e)
			{
				showError("Failed to save file: "+e.getMessage());
				return false;
			}
		}
		else
		{
			return false;
		}
	}
	
	protected boolean openFileDialog()
	{
		JFileChooser jfc = new JFileChooser();
		if(currentDirectory!=null){
			jfc.setCurrentDirectory(currentDirectory);
		}
		if(jfc.showOpenDialog(this)==JFileChooser.APPROVE_OPTION)
		{
			File selected = jfc.getSelectedFile();
			currentDirectory = selected.getParentFile();
			try
			{
				FileEditorFile f = loadFile(selected);
				setupFileGui(f);
				return true;
			}
			catch(FileEditorFileException e)
			{
				showError("Failed to open file: "+e.getMessage());
				return false;
			}
		}
		else
		{
			return false;
		}
	}
	
	protected void showError(String message)
	{
		JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
	}
	
	public void internalFrameActivated(InternalFrameEvent e)
	{}

	public void internalFrameClosed(InternalFrameEvent e)
	{}

	public void internalFrameClosing(InternalFrameEvent e)
	{
		saveAndCloseIfConfirmed(e.getInternalFrame());
	}

	public void internalFrameDeactivated(InternalFrameEvent e)
	{}

	public void internalFrameDeiconified(InternalFrameEvent e)
	{}

	public void internalFrameIconified(InternalFrameEvent e)
	{}

	public void internalFrameOpened(InternalFrameEvent e)
	{}

	public void windowActivated(WindowEvent arg0) 
	{}

	public void windowClosed(WindowEvent arg0) 
	{}

	public void windowClosing(WindowEvent arg0) 
	{
		saveAllAndExitIfConfirmed();
	}

	public void windowDeactivated(WindowEvent arg0) 
	{}

	public void windowDeiconified(WindowEvent arg0) 
	{}

	public void windowIconified(WindowEvent arg0) 
	{}

	public void windowOpened(WindowEvent arg0) 
	{}
}
