package uk.co.markfrimston.utils.gui;

import java.io.*;
import javax.swing.*;

public abstract class FileEditorFile
{
	protected File file; 	
	protected JInternalFrame frame;
	protected UndoStack undoHistory;
	protected boolean unsavedChanges;
	
	public FileEditorFile(File file, JInternalFrame frame)
	{
		this.file = file;
		this.frame = frame;
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
	}
	public void setUnsavedChanges(boolean val)
	{
		this.unsavedChanges = val;
	}
}
