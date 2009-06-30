package uk.co.markfrimston.utils.gui;

import java.io.*;
import javax.swing.*;

public abstract class FileEditorFile
{
	protected File file; 	
	protected JInternalFrame frame;
	protected UndoStack undoHistory;
	protected boolean unsavedChanges;
	
	public FileEditorFile(File file, JInternalFrame frame, boolean unsavedChanges)
	{
		this.file = file;
		this.frame = frame;
		this.unsavedChanges = unsavedChanges;
		this.undoHistory = new UndoStack();
		updateFrameSettings();
	}
	
	public File getFile()
	{
		return file;
	}
	public JInternalFrame getFrame()
	{
		return frame;
	}
	public UndoStack getUndoHistory()
	{
		return undoHistory;
	}
	public boolean hasUnsavedChanges()
	{
		return unsavedChanges;
	}
	public void setFile(File file)
	{
		this.file = file;
		updateFrameSettings();		
	}
	public void setUnsavedChanges(boolean val)
	{
		this.unsavedChanges = val;
	}
	
	protected void updateFrameSettings()
	{
		if(this.frame!=null && this.file!=null)
		{
			this.frame.setTitle(this.file.getName());
		}
	}
	
	public boolean undoAvailable()
	{
		return undoHistory.canUndo();
	}
	
	public boolean redoAvailable()
	{
		return undoHistory.canRedo();
	}
}
