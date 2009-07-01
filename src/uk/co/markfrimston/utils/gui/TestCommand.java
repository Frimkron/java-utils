package uk.co.markfrimston.utils.gui;

public class TestCommand extends FileEditorCommand 
{	
	public TestCommand(FileEditorFile file) 
	{
		super(file);
	}

	@Override
	public void execute() 
	{
		Test.TestFile f = (Test.TestFile)file;
		f.incrementNumber();
		f.setUnsavedChanges(true);
	}

	@Override
	public void undo() 
	{
		Test.TestFile f = (Test.TestFile)file;
		f.decrementNumber();
	}

}
