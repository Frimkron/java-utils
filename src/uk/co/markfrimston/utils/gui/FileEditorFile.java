package uk.co.markfrimston.utils.gui;

import java.io.*;
import javax.swing.*;

public abstract class FileEditorFile
{
	protected File file; 	
	protected JInternalFrame frame;
	protected UndoStack undoHistory;
	protected boolean unsavedChanges;
	protected FileEditorFileListener listener;
	
	public FileEditorFile(File file, JInternalFrame frame, boolean unsavedChanges,
			FileEditorFileListener listener)
	{
		this.file = file;
		this.frame = frame;
		this.unsavedChanges = unsavedChanges;
		this.listener = listener;
		this.undoHistory = new UndoStack();
		fileChanged();
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
		fileChanged();		
	}
	public void setUnsavedChanges(boolean val)
	{
		this.unsavedChanges = val;
	}
	
	protected void fileChanged()
	{
		if(this.listener!=null)
		{
			this.listener.fileChanged(this);
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
	
	public void undo()
	{
		if(undoAvailable())
		{
			undoHistory.undo();
			undoChanged();
		}
	}
	
	public void redo()
	{
		if(redoAvailable())
		{
			undoHistory.redo();
			undoChanged();
		}
	}
	
	public void doCommand(FileEditorCommand command)
	{
		command.execute();
		undoHistory.add(command);
		undoChanged();
	}
	
	protected void undoChanged()
	{
		if(listener!=null){
			listener.undoHistoryChanged(this);
		}
	}
}
