package uk.co.markfrimston.utils.gui;

import java.io.*;

public interface FileEditorFileListener 
{
	public void fileChanged(FileEditorFile file);
	public void undoHistoryChanged(FileEditorFile file);
}
