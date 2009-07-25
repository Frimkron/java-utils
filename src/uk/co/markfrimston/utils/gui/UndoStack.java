package uk.co.markfrimston.utils.gui;

import java.util.*;

public class UndoStack
{
	protected Stack<Undoable> undoStack = new Stack<Undoable>();
	protected Stack<Undoable> redoStack = new Stack<Undoable>();
	protected int maxSize = 0;
	
	public UndoStack()
	{}
	
	public UndoStack(int maxSize)
	{
		this.maxSize = maxSize;
	}
	
	public void add(Undoable undoable)
	{
		undoStack.push(undoable);
		redoStack.clear();
		while(maxSize>0 && undoStack.size()>maxSize)
		{
			undoStack.remove(0);
		}
	}
	
	public boolean canUndo()
	{
		return !undoStack.isEmpty();
	}
	
	public boolean canRedo()
	{
		return !redoStack.isEmpty();
	}
	
	public void undo()
	{
		if(!canUndo()){
			return;
		}
		Undoable u = undoStack.pop();
		u.undo();
		redoStack.push(u);
	}
	
	public void redo()
	{
		if(!canRedo()){
			return;
		}
		Undoable u = redoStack.pop();
		u.redo();
		undoStack.push(u);
	}
	
	public String getUndoName()
	{
		if(!canUndo()){
			return "";
		}
		return undoStack.peek().getName();
	}
	
	public String getRedoName()
	{
		if(!canRedo()){
			return "";
		}
		return redoStack.peek().getName();
	}
}
