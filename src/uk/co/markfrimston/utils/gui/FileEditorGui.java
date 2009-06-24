package uk.co.markfrimston.utils.gui;

import javax.swing.*;
import javax.swing.event.*;

import java.awt.event.*;
import java.util.*;
import java.io.*;

public abstract class FileEditorGui extends JFrame implements InternalFrameListener
{
	protected JDesktopPane desktopPane;
	protected Map<JInternalFrame,FileEditorFile> files = new HashMap<JInternalFrame,FileEditorFile>(); 
	
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
		mFile.add(miFileOpen);
		miFileClose = new JMenuItem("Close");
		mFile.add(miFileClose);
		mFile.add(new JSeparator());
		miFileSave = new JMenuItem("Save");
		mFile.add(miFileSave);
		miFileSaveAs = new JMenuItem("Save As");
		mFile.add(miFileSaveAs);
		miFileExit = new JMenuItem("Exit");
		mFile.add(miFileExit);
		
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
	}
	
	protected abstract FileEditorFile createNewFile();
	
	protected abstract void saveFile(FileEditorFile file);
	
	protected abstract FileEditorFile loadFile(File filename);
	
	protected void createNewFileAndSetupGui()
	{
		FileEditorFile f = createNewFile();
		this.files.put(f.getFrame(), f);
		this.desktopPane.add(f.getFrame());
		f.getFrame().setTitle(f.getFile().getName());
		f.getFrame().setDefaultCloseOperation(JInternalFrame.DO_NOTHING_ON_CLOSE);
		f.getFrame().addInternalFrameListener(this);
		f.getFrame().setVisible(true);
	}

	protected void saveAndCloseIfConfirmed(JInternalFrame frame)
	{
		FileEditorFile file = files.get(frame);
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
					}						
				}
				else
				{
					closeFile(file);
				}
			}
		}
		else
		{
			closeFile(file);
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
			saveFile(file);
			return true;
		}
	}
	
	protected boolean saveFileAsDialog(FileEditorFile file)
	{
		JFileChooser jfc = new JFileChooser();
		
		if(jfc.showSaveDialog(this)==JFileChooser.APPROVE_OPTION)
		{
			
			
		}
		else
		{
			return false;
		}
	}
	
	public void internalFrameActivated(InternalFrameEvent e)
	{}

	public void internalFrameClosed(InternalFrameEvent e)
	{
		saveAndCloseIfConfirmed(e.getInternalFrame());
	}

	public void internalFrameClosing(InternalFrameEvent e)
	{}

	public void internalFrameDeactivated(InternalFrameEvent e)
	{}

	public void internalFrameDeiconified(InternalFrameEvent e)
	{}

	public void internalFrameIconified(InternalFrameEvent e)
	{}

	public void internalFrameOpened(InternalFrameEvent e)
	{}
}
