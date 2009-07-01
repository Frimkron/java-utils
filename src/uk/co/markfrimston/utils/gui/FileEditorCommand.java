package uk.co.markfrimston.utils.gui;

public abstract class FileEditorCommand extends Command
{
	protected FileEditorFile file;
	
	public FileEditorCommand(FileEditorFile file)
	{
		this.file = file;
	}
	
	public FileEditorFile getFile()
	{
		return file;
	}
}
