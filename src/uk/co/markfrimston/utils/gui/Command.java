package uk.co.markfrimston.utils.gui;

public abstract class Command implements Undoable
{
	public abstract void execute();
	public abstract void undo();
	
	public void redo()
	{		
		execute();
	}	
}
