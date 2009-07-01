package uk.co.markfrimston.utils.gui;

import java.awt.*;
import java.io.*;
import javax.swing.*;
import java.beans.*;
import java.awt.event.*;

public class Test extends FileEditorGui 
{
	public static class TestFile extends FileEditorFile
	{
		protected int number;
		
		public TestFile(File file, JInternalFrame frame, boolean unsavedChanges, 
				FileEditorFileListener listener, int number) 
		{
			super(file, frame, unsavedChanges, listener);
			this.number = number;
		}
		
		public void incrementNumber()
		{
			number++;
			this.frame.repaint();
		}
		public void decrementNumber()
		{
			number--;
			this.frame.repaint();
		}
	}
	
	public static class TestFrame extends JInternalFrame
		implements MouseListener
	{
		protected TestFile file;
		
		public TestFrame()
		{
			this.setResizable(true);
			this.setMaximizable(true);
			this.setClosable(true);
			this.setIconifiable(true);			
			this.setSize(new Dimension(200,200));
			this.addMouseListener(this);
		}
		
		public void paint(Graphics g)
		{
			super.paint(g);
			if(file!=null)
			{
				g.setColor(Color.BLUE);
				g.drawString("Number: "+String.valueOf(file.number), 50, 50);
			}
		}

		public void mouseClicked(MouseEvent arg0) {}

		public void mouseEntered(MouseEvent arg0) {}

		public void mouseExited(MouseEvent arg0) {}

		public void mousePressed(MouseEvent arg0) {}

		public void mouseReleased(MouseEvent arg0) 
		{
			file.doCommand(new TestCommand(file));
		}
	}
	
	public Test() 
	{
		super();

	}

	@Override
	protected FileEditorFile createNewFile() 
		throws FileEditorFileException 
	{
		TestFrame frame = new TestFrame();
		File file;
		int i = 1;
		do
		{
			file = new File(currentDirectory!=null?currentDirectory:new File("."),
					"Newfile-"+i+".tst");
			i++;
		}
		while(file.exists());
		TestFile f = new TestFile(file,frame,true,this,0);
		frame.file = f;
		return f;
	}

	@Override
	protected FileEditorFile loadFile(File filename)
			throws FileEditorFileException 
	{
		try
		{
			BufferedReader r = new BufferedReader(new FileReader(filename));
			int num = Integer.parseInt(r.readLine());
			TestFrame frame = new TestFrame();
			TestFile f = new TestFile(filename,frame,false,this,num);
			frame.file = f;
			r.close();
			return f;
		}
		catch(NumberFormatException e)
		{
			throw new FileEditorFileException(e);
		}
		catch(IOException e)
		{
			throw new FileEditorFileException(e);
		}
	}

	@Override
	protected void saveFile(FileEditorFile file)
		throws FileEditorFileException 
	{
		try
		{
			TestFile f = (TestFile)file;
			FileWriter w = new FileWriter(file.getFile());
			w.write(String.valueOf(f.number));
			w.close();
		}
		catch(IOException e)
		{
			throw new FileEditorFileException(e);
		}
	}
	
	public static void main(String[] args)
	{
		/*try 
		{
	        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	    } 
	    catch(Exception e) 
	    {}*/

		Test t = new Test();
	}

}
