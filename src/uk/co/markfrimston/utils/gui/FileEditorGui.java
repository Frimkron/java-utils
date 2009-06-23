package uk.co.markfrimston.utils.gui;

import javax.swing.*;

public abstract class FileEditorGui extends JFrame
{
	protected JDesktopPane desktopPane;
	
	public FileEditorGui()
	{
		this.desktopPane = new JDesktopPane();
		this.getContentPane().add(desktopPane);
	}
	
	
}
