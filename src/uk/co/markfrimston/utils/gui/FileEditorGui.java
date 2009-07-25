package uk.co.markfrimston.utils.gui;

import javax.swing.*;
import javax.swing.filechooser.*;
import javax.swing.event.*;
import java.beans.*;
import java.awt.geom.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;

public abstract class FileEditorGui extends JFrame 
	implements InternalFrameListener, WindowListener, FileEditorFileListener
{
	protected JDesktopPane desktopPane;
	protected Map<JInternalFrame,FileEditorFile> files = new HashMap<JInternalFrame,FileEditorFile>();
	protected Map<JInternalFrame,JMenuItem> windowMenuItems = new HashMap<JInternalFrame,JMenuItem>();
	protected File currentDirectory;
	
	protected JMenu miFile;
	protected JMenuItem miFileNew;
	protected JMenuItem miFileOpen;
	protected JMenuItem miFileClose;
	protected JMenuItem miFileCloseAll;
	protected JMenuItem miFileSave;
	protected JMenuItem miFileSaveAs;
	protected JMenuItem miFileExit;
	
	protected JMenu miEdit;
	protected JMenuItem miEditUndo;
	protected JMenuItem miEditRedo;
	
	protected JMenu miWindow;
	protected JMenuItem miWindowCascade;
	protected JMenuItem miWindowTileHoriz;
	protected JMenuItem miWindowTileVert;
	protected ButtonGroup windowButtonGroup;
	
	public FileEditorGui()
	{
		this.desktopPane = new JDesktopPane();
		this.getContentPane().add(desktopPane);
		
		JMenuBar menuBar = new JMenuBar();
		setupMenu(menuBar);
		this.setJMenuBar(menuBar);
		
		setOpenFileOptionsEnabled(false);
		
		this.setSize(new Dimension(320,200));
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(this);
		this.setVisible(true);
	}
	
	protected void setupMenu(JMenuBar menuBar)
	{
		miFile = new JMenu("File");
		miFile.setMnemonic('F');		
		miFileNew = new JMenuItem("New");
		miFileNew.setMnemonic('N');
		miFileNew.setAccelerator(ctlKeystroke(KeyEvent.VK_N));
		miFileNew.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				createNewFileAndSetupGui();
			}
		});
		miFile.add(miFileNew);
		miFileOpen = new JMenuItem("Open");
		miFileOpen.setMnemonic('O');
		miFileOpen.setAccelerator(ctlKeystroke(KeyEvent.VK_O));
		miFileOpen.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				openFileDialog();
			}
		});
		miFile.add(miFileOpen);
		miFileClose = new JMenuItem("Close");
		miFileClose.setMnemonic('C');
		miFileClose.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				JInternalFrame frame = desktopPane.getSelectedFrame();
				if(frame!=null){
					saveAndCloseIfConfirmed(frame);
				}
			}
		});
		miFile.add(miFileClose);
		miFileCloseAll = new JMenuItem("Close All");
		miFileCloseAll.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				saveAndCloseAllIfConfirmed();
			}
		});
		miFile.add(miFileCloseAll);
		miFileSave = new JMenuItem("Save");
		miFileSave.setMnemonic('S');
		miFileSave.setAccelerator(ctlKeystroke(KeyEvent.VK_S));
		miFileSave.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				FileEditorFile f = currentlySelectedFile();
				if(f!=null){
					saveOrSaveAsFile(f);
				}
			}
		});
		miFile.add(miFileSave);
		miFileSaveAs = new JMenuItem("Save As");
		miFileSaveAs.setMnemonic('A');
		miFileSaveAs.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				FileEditorFile f = currentlySelectedFile();
				if(f!=null){
					saveFileAsDialog(f);
				}
			}
		});
		miFile.add(miFileSaveAs);
		miFileExit = new JMenuItem("Exit");
		miFileExit.setMnemonic('x');
		miFileExit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				saveAllAndExitIfConfirmed();
			}
		});
		miFile.add(miFileExit);
		menuBar.add(miFile);
		
		miEdit = new JMenu("Edit");
		miEdit.setMnemonic('E');
		miEditUndo = new JMenuItem("Undo");
		miEditUndo.setMnemonic('U');
		miEditUndo.setAccelerator(ctlKeystroke(KeyEvent.VK_Z));
		miEditUndo.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				FileEditorFile f = currentlySelectedFile();
				if(f!=null && f.undoAvailable()){
					f.undo();
				}
			}
		});
		miEdit.add(miEditUndo);
		miEditRedo = new JMenuItem("Redo");
		miEditRedo.setMnemonic('R');
		miEditRedo.setAccelerator(ctlKeystroke(KeyEvent.VK_Y));
		miEditRedo.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				FileEditorFile f = currentlySelectedFile();
				if(f!=null && f.redoAvailable()){
					f.redo();
				}
			}
		});
		miEdit.add(miEditRedo);
		menuBar.add(miEdit);
		
		miWindow = new JMenu("Window");
		miWindow.setMnemonic('W');
		windowButtonGroup = new ButtonGroup();
		miWindowCascade = new JMenuItem("Cascade");
		miWindowCascade.setMnemonic('C');
		miWindowCascade.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				cascadeWindows();
			}
		});
		miWindow.add(miWindowCascade);
		miWindowTileVert = new JMenuItem("Tile Vertical");
		miWindowTileVert.setMnemonic('T');
		miWindowTileVert.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				tileWindowsVertically();
			}
		});
		miWindow.add(miWindowTileVert);
		miWindowTileHoriz = new JMenuItem("Tile Horizontal");
		miWindowTileHoriz.setMnemonic('i');
		miWindowTileHoriz.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				tileWindowsHorizontally();
			}
		});
		miWindow.add(miWindowTileHoriz);		
		miWindow.add(new JSeparator());
		menuBar.add(miWindow);
	}
	
	protected abstract FileEditorFile createNewFile() throws FileEditorFileException;
	
	protected abstract void saveFile(FileEditorFile file) throws FileEditorFileException;
	
	protected abstract FileEditorFile loadFile(File filename) throws FileEditorFileException;
	
	protected FileEditorFile currentlySelectedFile()
	{
		JInternalFrame currentFrame = this.desktopPane.getSelectedFrame();
		return files.get(currentFrame);
	}
	
	protected KeyStroke ctlKeystroke(int withKeyCode)
	{
		return KeyStroke.getKeyStroke(withKeyCode,
			    Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false);
	}
	
	protected void createNewFileAndSetupGui()
	{
		try
		{
			FileEditorFile f = createNewFile();
			if(f != null)
			{
				setupFileGui(f);
				setOpenFileOptionsEnabled(true);
			}
		}
		catch(FileEditorFileException e)
		{
			showError("Failed to create new file: "+e.getMessage());
		}
	}
	
	protected void setupFileGui(FileEditorFile f)
	{
		this.files.put(f.getFrame(), f);
		final JInternalFrame frame = f.getFrame();
		this.desktopPane.add(frame);		
		frame.setDefaultCloseOperation(JInternalFrame.DO_NOTHING_ON_CLOSE);
		frame.addInternalFrameListener(this);
		frame.setVisible(true);
		try{
			frame.setMaximum(true);
		}catch(PropertyVetoException e)
		{}
		JCheckBoxMenuItem menuItem = new JCheckBoxMenuItem(f.getFile().getName());
		menuItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{	
				try{
					frame.setSelected(true);
				}
				catch(PropertyVetoException ex){}
			}
		});
		this.windowMenuItems.put(frame, menuItem);
		this.windowButtonGroup.add(menuItem);
		this.miWindow.add(menuItem);
		menuItem.setSelected(true);
	}

	protected boolean saveAndCloseAllIfConfirmed()
	{
		boolean done = true;
		Set<JInternalFrame> keys = new HashSet<JInternalFrame>();
		keys.addAll(files.keySet());
		for(JInternalFrame frame : keys)
		{
			if(!saveAndCloseIfConfirmed(frame))
			{
				done = false;
				break;
			}
		}
		return done;
	}
	
	protected void saveAllAndExitIfConfirmed()
	{		
		if(saveAndCloseAllIfConfirmed())
		{
			exit();
		}
	}
	
	protected void exit()
	{
		this.setVisible(false);
		this.dispose();
		System.exit(0);
	}
	
	protected boolean saveAndCloseIfConfirmed(JInternalFrame frame)
	{
		FileEditorFile file = files.get(frame);
		if(file!=null)
		{
			if(file.hasUnsavedChanges())
			{	
				int resp = JOptionPane.showConfirmDialog(this, 
						"Save changes to "+file.getFile().getName()+"?","Save Changes",
						JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
				if(resp != JOptionPane.CANCEL_OPTION)
				{
					if(resp == JOptionPane.YES_OPTION)
					{
						if(saveOrSaveAsFile(file))
						{
							closeFile(file);
							return true;
						}				
						else
						{
							return false;
						}
					}
					else
					{					
						closeFile(file);
						return true;
					}
				}
				else
				{
					return false;
				}
			}
			else
			{
				closeFile(file);
				return true;
			}
		}
		else
		{
			return true;
		}
	}
	
	protected void closeFile(FileEditorFile file)
	{
		JInternalFrame frame = file.getFrame();
		files.remove(frame);
		frame.setVisible(false);
		frame.dispose();
		disableOptionsIfNothingOpen();
		JMenuItem mi = this.windowMenuItems.get(frame);
		this.windowButtonGroup.remove(mi);
		this.miWindow.remove(mi);
	}
	
	protected boolean saveOrSaveAsFile(FileEditorFile file)
	{
		if(!file.getFile().exists())
		{
			return saveFileAsDialog(file);
		}
		else
		{
			try{
				saveFile(file);
				file.setUnsavedChanges(false);
				return true;
			}
			catch(FileEditorFileException e)
			{
				showError("Failed to save file: "+e.getMessage());
				return false;
			}			
		}
	}
	
	protected boolean saveFileAsDialog(FileEditorFile file)
	{
		JFileChooser jfc = new JFileChooser();
		jfc.setSelectedFile(file.getFile());
		for(javax.swing.filechooser.FileFilter filter : file.getSaveFileFilters())
		{
			jfc.addChoosableFileFilter(filter);
		}
		if(jfc.showSaveDialog(this)==JFileChooser.APPROVE_OPTION)
		{
			File selected = jfc.getSelectedFile();
			currentDirectory = selected.getParentFile();
			if(selected.exists())
			{
				if(JOptionPane.showConfirmDialog(this, "This file already exists. Overwrite?","Overwrite",
						JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE)!=JOptionPane.OK_OPTION)
				{
					return false;
				}
			}
			file.setFile(jfc.getSelectedFile());
			try{
				saveFile(file);
				file.setUnsavedChanges(false);
				return true;
			}
			catch(FileEditorFileException e)
			{
				showError("Failed to save file: "+e.getMessage());
				return false;
			}
		}
		else
		{
			return false;
		}
	}
	
	protected boolean openFileDialog()
	{
		JFileChooser jfc = new JFileChooser();
		if(currentDirectory!=null){
			jfc.setCurrentDirectory(currentDirectory);
		}
		for(javax.swing.filechooser.FileFilter filter : getOpenFileFilters())
		{
			jfc.addChoosableFileFilter(filter);
		}
		if(jfc.showOpenDialog(this)==JFileChooser.APPROVE_OPTION)
		{
			File selected = jfc.getSelectedFile();
			currentDirectory = selected.getParentFile();
			try
			{
				FileEditorFile f = loadFile(selected);
				if(f != null)
				{
					setupFileGui(f);
					setOpenFileOptionsEnabled(true);
					return true;
				}
				else
				{
					return false;
				}
			}
			catch(FileEditorFileException e)
			{
				showError("Failed to open file: "+e.getMessage());
				return false;
			}
		}
		else
		{
			return false;
		}
	}
	
	protected void showError(String message)
	{
		JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
	}
	
	public void internalFrameActivated(InternalFrameEvent e)
	{
		enableUndoForAvailability();
		enableRedoForAvailability();
		JMenuItem mi = this.windowMenuItems.get(e.getInternalFrame());
		if(mi!=null){
			mi.setSelected(true);
		}
	}

	public void internalFrameClosed(InternalFrameEvent e)
	{}

	public void internalFrameClosing(InternalFrameEvent e)
	{
		saveAndCloseIfConfirmed(e.getInternalFrame());
	}

	public void internalFrameDeactivated(InternalFrameEvent e)
	{}

	public void internalFrameDeiconified(InternalFrameEvent e)
	{}

	public void internalFrameIconified(InternalFrameEvent e)
	{}

	public void internalFrameOpened(InternalFrameEvent e)
	{}

	public void windowActivated(WindowEvent arg0) 
	{}

	public void windowClosed(WindowEvent arg0) 
	{}

	public void windowClosing(WindowEvent arg0) 
	{
		saveAllAndExitIfConfirmed();
	}

	public void windowDeactivated(WindowEvent arg0) 
	{}

	public void windowDeiconified(WindowEvent arg0) 
	{}

	public void windowIconified(WindowEvent arg0) 
	{}

	public void windowOpened(WindowEvent arg0) 
	{}
	
	protected void disableOptionsIfNothingOpen()
	{
		if(this.files.isEmpty())
		{
			setOpenFileOptionsEnabled(false);
		}
	}
	
	protected void setOpenFileOptionsEnabled(boolean enabled)
	{
		miFileClose.setEnabled(enabled);
		miFileCloseAll.setEnabled(enabled);
		miFileSave.setEnabled(enabled);
		miFileSaveAs.setEnabled(enabled);
		miEdit.setEnabled(enabled);
		if(enabled){
			enableUndoForAvailability();
			enableRedoForAvailability();
		}
		miWindow.setEnabled(enabled);
	}
	
	protected void enableUndoForAvailability()
	{
		FileEditorFile f = currentlySelectedFile();
		if(f!=null)
		{
			miEditUndo.setEnabled(f.undoAvailable());
		}
	}
	
	protected void enableRedoForAvailability()
	{
		FileEditorFile f = currentlySelectedFile();
		if(f!=null)
		{
			miEditRedo.setEnabled(f.redoAvailable());
		}
	}
	
	protected void cascadeWindows()
	{
		Rectangle bounds = this.desktopPane.getBounds();
		int frameW = bounds.width / 4 * 3;
		int frameH = bounds.height / 4 * 3;
		int hSpace = bounds.width - frameW;
		int vSpace = bounds.height - frameH;
		int count = 0;
		for(JInternalFrame frame : this.desktopPane.getAllFrames())
		{
			int x = (count * 16) % hSpace;
			int y = (count * 16) % vSpace;
			frame.setBounds(new Rectangle(x,y,frameW,frameH));
			frame.moveToFront();
			count ++;
		}
	}
	
	protected void tileWindowsVertically()
	{
		Rectangle bounds = this.desktopPane.getBounds();
		int total = this.desktopPane.getAllFrames().length;	
		int size = bounds.width / total;
		int lastSize = size + bounds.width % total; 
		int count = 0;
		for(JInternalFrame frame : this.desktopPane.getAllFrames())
		{
			frame.setBounds(new Rectangle(count*size,0,
					count==(total-1)?lastSize:size, bounds.height));
			frame.moveToFront();
			count++;
		}
	}
	
	protected void tileWindowsHorizontally()
	{
		Rectangle bounds = this.desktopPane.getBounds();
		int total = this.desktopPane.getAllFrames().length;	
		int size = bounds.height / total;
		int lastSize = size + bounds.width % total; 
		int count = 0;
		for(JInternalFrame frame : this.desktopPane.getAllFrames())
		{
			frame.setBounds(new Rectangle(0,count*size,
					bounds.width, count==(total-1)?lastSize:size));
			frame.moveToFront();
			count++;
		}
	}
	
	public void fileChanged(FileEditorFile file)
	{
		if(file.getFrame()!=null)
		{
			if(file.getFile()!=null)
			{
				file.getFrame().setTitle(file.getFile().getName());
				if(this.windowMenuItems.containsKey(file.getFrame())){
					this.windowMenuItems.get(file.getFrame()).setText(file.getFile().getName());
				}
			}
			else
			{
				file.getFrame().setTitle("");
				if(this.windowMenuItems.containsKey(file.getFrame())){
					this.windowMenuItems.get(file.getFrame()).setText("");
				}
			}			
		}
	}
	
	public void undoHistoryChanged(FileEditorFile file)
	{
		enableUndoForAvailability();
		miEditUndo.setText("Undo "+file.getUndoName());
		enableRedoForAvailability();
		miEditRedo.setText("Redo "+file.getRedoName());
	}
	
	protected javax.swing.filechooser.FileFilter[] getOpenFileFilters()
	{
		return new javax.swing.filechooser.FileFilter[]{};
	}
}
