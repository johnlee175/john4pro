package com.johnsoft.product.swing.filetree;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoManager;

import com.johnsoft.library.swing.component.filetree.JohnFileTree;
import com.johnsoft.library.swing.component.titlepane.JohnTitlePane;
import com.johnsoft.library.util.common.JohnFileUtil;
import com.johnsoft.product.swing.startup.JohnResourcesManager;

public class JohnFileTreeMainFrame extends JFrame
{
	private static final long serialVersionUID = 1L;
	
	public static void main(String[] args)
	{
		EventQueue.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (Exception e)
				{
					JOptionPane.showMessageDialog(null, "没有可选的windows皮肤");
					e.printStackTrace();
				} 
				String filePath=JohnFileUtil.readFile(JohnResourcesManager.getFileTreeRootPath(), "ISO-8859-1");
				new JohnFileTreeMainFrame(new File(filePath));
			}
		});
	}
	
	public JohnFileTreeMainFrame(File file)
	{
		initComponent(file);
		installListeners();
	}
	
	private JohnFileTree fileTree;
	private JTextArea preview;
	private JTextField lookup;
	private JComboBox<String> charset;
	
	private void initComponent(File file)
	{
		fileTree=new JohnFileTree(file);
	 	lookup=new JTextField();
	 	lookup.setColumns(10);
		
		preview=new JTextArea();
		charset=new JComboBox<String>(new String[]{"GBK","UTF-8","GB18030","ISO-8859-1","US-ASCII"});
		preview.setCaretColor(Color.CYAN);
		preview.setTabSize(2);
		
		JSplitPane jsp=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,false,new JScrollPane(fileTree),new JScrollPane(preview));
		JPanel options=new JPanel(new BorderLayout());
		options.add(lookup,BorderLayout.WEST);
		options.add(charset,BorderLayout.EAST);
		JPanel status=new JPanel();
		
		JohnTitlePane titlePane=new JohnTitlePane(this);
		JPanel contentPane=titlePane.asContentPane();
		contentPane.add(jsp);
		contentPane.add(options,BorderLayout.NORTH);
		contentPane.add(status,BorderLayout.SOUTH);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setSize(600, 450);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}
	
	private void installListeners()
	{
		lookup.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyReleased(KeyEvent e)
			{
				String prefix=lookup.getText();
				fileTree.filterNamePrefix(prefix);
			}
		});
		
		fileTree.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if(e.getClickCount()==2)
				{
					String text=fileTree.getSelectedFileContent(null);
					preview.setText(text);
				}
			}
		});
		
		charset.addItemListener(new ItemListener()
		{
			@Override
			public void itemStateChanged(ItemEvent e)
			{
				String text=fileTree.getSelectedFileContent((String)charset.getSelectedItem());
				preview.setText(text);
			}
		});
		preview.getInputMap().put(KeyStroke.getKeyStroke("ctrl shift E"), "showSpace");
		preview.getActionMap().put("showSpace", new AbstractAction()
		{
			private static final long serialVersionUID = 1L;
			@Override
			public void actionPerformed(ActionEvent e)
			{
				String txt=preview.getText();
				txt=txt.replace("\"", "\\\"");
				txt=txt.replace("\n", "\\n");
				txt=txt.replace("\r", "\\r");
				txt=txt.replace("\t", "\\t");
				txt=txt.replaceAll("[ ]+", " ");
				preview.setText(txt);
			}
			
		});
		
		preview.getInputMap().put(KeyStroke.getKeyStroke("ctrl shift X"), "changeCase");
		preview.getActionMap().put("changeCase", new AbstractAction()
		{
			private static final long serialVersionUID = 1L;
			@Override
			public void actionPerformed(ActionEvent e)
			{
				int start=preview.getSelectionStart();
				int end=preview.getSelectionEnd();
				String needCase=preview.getSelectedText();
				if(needCase!=null&&needCase.length()>0)
				{
					int index=0;
					for(int i=0;i<needCase.length();i++)
					{
						if(Character.isLetter(needCase.charAt(i)))
						{
							index=i;
						}
					}
					if(Character.isUpperCase(needCase.charAt(index)))
					{
						preview.replaceSelection(needCase.toLowerCase());
						preview.setCaretColor(Color.BLACK);
					}else{
						preview.replaceSelection(needCase.toUpperCase());
						preview.setCaretColor(Color.RED);
					}
				}
				preview.select(start, end);
			}
		});
		final UndoManager undo=new UndoManager();
		preview.getDocument().addUndoableEditListener(new UndoableEditListener()
		{
			@Override
			public void undoableEditHappened(UndoableEditEvent e)
			{
				undo.addEdit(e.getEdit());
			}
		});
		preview.getInputMap().put(KeyStroke.getKeyStroke("ctrl Z"), "undo");
		preview.getActionMap().put("undo", new AbstractAction()
		{
			private static final long serialVersionUID = 1L;
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if(undo.canUndo())
				{
					undo.undo();
				}
			}
		});
		preview.getInputMap().put(KeyStroke.getKeyStroke("ctrl Y"), "redo");
		preview.getActionMap().put("redo", new AbstractAction()
		{
			private static final long serialVersionUID = 1L;
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if(undo.canRedo())
				{
					undo.redo();
				}
			}
		});
	
	}
	
}
