package uk.co.markfrimston.utils.gui;

import javax.swing.*;
import javax.swing.event.*;

import java.awt.geom.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;

// TODO implement undo, redo
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
	
	protected JMenu miFile;
	protected JMenuItem miFileNew;
	protected JMenuItem miFileOpen;
	protected JMenuItem miFileClose;
	protected JMenuItem miFileCloseAll;
	protected JMenuItem miFileSave;
	protected JMenuItem miFileSaveAs;
	protected JMenuItem miFileExit;
	
	protected JMenu miEdit;
	protected JMenuItem miEditUndo;
	protected JMenuItem miEditRedo;
	
	protected JMenu miWindow;
	protected JMenuItem miWindowCascade;
	protected JMenuItem miWindowTileHoriz;
	protected JMenuItem miWindowTileVert;
	protected JMenuItem miWindowArrange;
	
	public FileEditorGui()
	{
		this.desktopPane = new JDesktopPane();
		this.getContentPane().add(desktopPane);
		
		JMenuBar menuBar = new JMenuBar();
		setupMenu(menuBar);
		this.setJMenuBar(menuBar);
		
		setOpenFileOptionsEnabled(false);
		
		this.setSize(new Dimension(320,200));
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(this);
		this.setVisible(true);
	}
	
	protected void setupMenu(JMenuBar menuBar)
	{
		miFile = new JMenu("File");
		miFileNew = new JMenuItem("New");
		miFileNew.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				createNewFileAndSetupGui();
			}
		});
		miFile.add(miFileNew);
		miFileOpen = new JMenuItem("Open");
		miFileOpen.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				openFileDialog();
			}
		});
		miFile.add(miFileOpen);
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
		miFile.add(miFileClose);
		miFileCloseAll = new JMenuItem("Close All");
		miFileCloseAll.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				saveAndCloseAllIfConfirmed();
			}
		});
		miFile.add(miFileCloseAll);
		miFile.add(new JSeparator());
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
		miFile.add(miFileSave);
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
		miFile.add(miFileSaveAs);
		miFileExit = new JMenuItem("Exit");
		miFileExit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				saveAllAndExitIfConfirmed();
			}
		});
		miFile.add(miFileExit);
		menuBar.add(miFile);
		
		miEdit = new JMenu("Edit");
		miEditUndo = new JMenuItem("Undo");
		miEdit.add(miEditUndo);
		miEditRedo = new JMenuItem("Redo");
		miEdit.add(miEditRedo);
		menuBar.add(miEdit);
		
		miWindow = new JMenu("Window");
		miWindowCascade = new JMenuItem("Cascade");
		miWindowCascade.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				cascadeWindows();
			}
		});
		miWindow.add(miWindowCascade);
		miWindowTileHoriz = new JMenuItem("Tile Horizontally");
		miWindow.add(miWindowTileHoriz);
		miWindowTileVert = new JMenuItem("Tile Vertically");
		miWindow.add(miWindowTileVert);
		miWindowArrange = new JMenuItem("Arrange");
		miWindow.add(miWindowArrange);
		menuBar.add(miWindow);
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
			setOpenFileOptionsEnabled(true);
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
		f.getFrame().setDefaultCloseOperation(JInternalFrame.DO_NOTHING_ON_CLOSE);
		f.getFrame().addInternalFrameListener(this);
		f.getFrame().setVisible(true);
	}

	protected boolean saveAndCloseAllIfConfirmed()
	{
		boolean done = true;
		Set<JInternalFrame> keys = new HashSet<JInternalFrame>();
		keys.addAll(files.keySet());
		for(JInternalFrame frame : keys)
		{
			if(!saveAndCloseIfConfirmed(frame))
			{
				done = false;
				break;
			}
		}
		return done;
	}
	
	protected void saveAllAndExitIfConfirmed()
	{		
		if(saveAndCloseAllIfConfirmed())
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
		disableOptionsIfNothingOpen();
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
				setOpenFileOptionsEnabled(true);
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
	{
		enableUndoForAvailability();
		enableRedoForAvailability();
	}

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
	
	protected void disableOptionsIfNothingOpen()
	{
		if(this.files.isEmpty())
		{
			setOpenFileOptionsEnabled(false);
		}
	}
	
	protected void setOpenFileOptionsEnabled(boolean enabled)
	{
		miFileClose.setEnabled(enabled);
		miFileCloseAll.setEnabled(enabled);
		miFileSave.setEnabled(enabled);
		miFileSaveAs.setEnabled(enabled);
		miEdit.setEnabled(enabled);
		if(enabled){
			enableUndoForAvailability();
			enableRedoForAvailability();
		}
		miWindow.setEnabled(enabled);
	}
	
	protected void enableUndoForAvailability()
	{
		FileEditorFile f = currentlySelectedFile();
		if(f!=null)
		{
			miEditUndo.setEnabled(f.undoAvailable());
		}
	}
	
	protected void enableRedoForAvailability()
	{
		FileEditorFile f = currentlySelectedFile();
		if(f!=null)
		{
			miEditRedo.setEnabled(f.redoAvailable());
		}
	}
	
	protected void cascadeWindows()
	{
		Rectangle bounds = this.desktopPane.getBounds();
		int frameW = bounds.width / 4 * 3;
		int frameH = bounds.height / 4 * 3;
		int hSpace = bounds.width - frameW;
		int vSpace = bounds.height - frameH;
		int count = 0;
		for(JInternalFrame frame : this.desktopPane.getAllFrames())
		{
			int x = (count * 16) % hSpace;
			int y = (count * 16) % vSpace;
			frame.setBounds(new Rectangle(x,y,frameW,frameH));
			frame.moveToFront();
			count ++;
		}
	}
}
