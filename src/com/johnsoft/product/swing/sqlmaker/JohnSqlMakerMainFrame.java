package com.johnsoft.product.swing.sqlmaker;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Insets;
import java.awt.Point;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import com.johnsoft.library.swing.component.JohnJDBCTable;
import com.johnsoft.library.swing.component.JohnPastableTable;
import com.johnsoft.product.swing.startup.JohnResourcesManager;

public class JohnSqlMakerMainFrame 
{
	private JDesktopPane window;
	private JInternalFrame tableContainer;
	private JInternalFrame textContainer;
	
	private JohnPastableTable sheet1;
	private JTable table;
	private JTable rowHeader;
	private JTextPane jTextPane;
	private JSplitPane jSplit;
	private ActionFactory actionFactory=new ActionFactory();
	
	private JTextField tableName=new JTextField();
	private JButton createTable;
	private JButton searchDataType;
	private JTextField dateFormat=new JTextField();
	private JTextField rowCount=new JTextField();
	private JTextField IPAndPort=new JTextField();
	private JTextField databaseName=new JTextField();
	private JTextField username=new JTextField();
	private JPasswordField password=new JPasswordField();
	private JRadioButton mysql=new JRadioButton("mysql");
	private JRadioButton oracle=new JRadioButton("oracle");
	private ButtonGroup bg=new ButtonGroup();
	
	private JSpinner chlen=new JSpinner(new SpinnerNumberModel(0, 0, 400, 1));
	private JSpinner enlen=new JSpinner(new SpinnerNumberModel(4, 0, 400, 1));
	private JSpinner numlen=new JSpinner(new SpinnerNumberModel(1, 0, 400, 1));
	private JSpinner intlen=new JSpinner(new SpinnerNumberModel(1, 0, 40, 1));
	private JSpinner floatlen=new JSpinner(new SpinnerNumberModel(0, 0, 20, 1));
	private JCheckBox chfix=new JCheckBox("是否固定");
	private JCheckBox enfix=new JCheckBox("是否固定");
	private JCheckBox numfix=new JCheckBox("是否固定");
	private JCheckBox intfix=new JCheckBox("是否固定");
	private JCheckBox floatfix=new JCheckBox("是否固定");
	private JButton save;
	
	private JDialog setCol;
	private List<JohnRexOptionColModel> rms=new ArrayList<JohnRexOptionColModel>();
	
	private Style keywords;
	private Style defaults;
	private StyledDocument sdoc;
	private int docStart;
	private String[] keyword=new String[]{"INSERT","INTO","VALUES","CREATE","DROP","TABLE",
			"SELECT","FROM","WHERE","JOIN","INNER","LEFT","RIGHT","BETWEEN","AND","GROUP","BY","ON",
			"DELETE","UPDATE","SET"};
	
	public static void main(String[] args)
	{
		//不必判断前提环境
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
				new JohnSqlMakerMainFrame();
			}
		});
	}
	
	public JohnSqlMakerMainFrame()
	{
		initComponent();
		//没有可以启动的其他线程
	}
	
	private void initComponent()
	{
		sheet1=new JohnPastableTable();
		table=sheet1.getJTable();
		rowHeader=sheet1.getJRowHeader();
		rowHeader.setValueAt("数据类型", 0, 0);
		rowHeader.setValueAt("数据列名", 1, 0);
		table.setComponentPopupMenu(getPopupMenu());
		
		table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("ctrl V"), "paste");
		table.getActionMap().put("paste", actionFactory.getPasteAction());
		table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("alt X"), "rotate");
		table.getActionMap().put("rotate", actionFactory.getRotateAction());
		table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("ctrl D"), "clear");
		table.getActionMap().put("clear", actionFactory.getClearAction());
		table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("F8"), "test");
		table.getActionMap().put("test", actionFactory.getTestAction());
		table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("ctrl enter"), "random");
		table.getActionMap().put("random", actionFactory.getRandomAction());
		table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE,0), "delete");
		table.getActionMap().put("delete", actionFactory.getDeleteAction());
		
		table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer(){
			private static final long serialVersionUID = 1L;
			@Override
			public Component getTableCellRendererComponent(JTable table,
					Object value, boolean isSelected, boolean hasFocus, int row,
					int column)
			{
				JLabel jl=(JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
						row, column);
				jl.setOpaque(true);
				if(!isSelected&&!hasFocus)
				{
					if(row==0||row==1)
					{
						
						jl.setBackground(Color.ORANGE);
					}else{
						jl.setBackground(Color.WHITE);
					}
				}else{
					if(row==0||row==1)
					{
						
						jl.setBackground(Color.GREEN);
					}else{
						jl.setBackground(Color.PINK);
					}
				}
				return jl;
			}
		});
		
		table.getTableHeader().setCursor(new Cursor(Cursor.HAND_CURSOR));
		table.getTableHeader().addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseReleased(MouseEvent e)
			{  
				if(table.getTableHeader().getCursor().getType()==Cursor.HAND_CURSOR)
				{
					 if(setCol!=null)
					  {
					  	setCol.dispose();
					  }
						setCol=getPopupSet(e.getXOnScreen(),e.getYOnScreen()+20,e.getX());
				    TableColumn tc=table.getColumnModel().getColumn(table.columnAtPoint(new Point(e.getX(), 10)));
				    tc.setCellRenderer(new DefaultTableCellRenderer(){
							private static final long serialVersionUID = 1L;
							@Override
				    	public Component getTableCellRendererComponent(JTable table,
				    			Object value, boolean isSelected, boolean hasFocus, int row,
				    			int column)
				    	{
				    		JLabel jl=(JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
				    				row, column);
				    		jl.setOpaque(true);
				    		jl.setBackground(Color.PINK);
				    		return jl;
				    	}
				    });
				    table.repaint();
				}
			}
		});
		
		jTextPane=new JTextPane();
		
		sdoc=jTextPane.getStyledDocument();
		defaults=StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
		StyleConstants.setForeground(defaults, Color.BLACK);
		keywords=sdoc.addStyle("keywords", defaults);
		StyleConstants.setForeground(keywords, Color.BLUE);
		sdoc.addDocumentListener(new DocumentListener()
		{
			@Override
			public void removeUpdate(DocumentEvent e){
			}
			@Override
			public void insertUpdate(DocumentEvent e)
			{
				docStart=e.getOffset();
			}
			@Override
			public void changedUpdate(DocumentEvent e){
			}
		});
		jTextPane.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyReleased(KeyEvent e)
			{
				Element root=sdoc.getDefaultRootElement();
				int cursorPos=jTextPane.getCaretPosition();
				int line=root.getElementIndex(cursorPos);
				Element para=root.getElement(line);
				int start=para.getStartOffset();
				if(start>docStart)
				{
					start=docStart;
				}
				int length=para.getEndOffset()-start;
			 try
			 {
					String text=sdoc.getText(start, length);
					setKeywordColor(text, start);
			} catch (BadLocationException e1)
			{
				e1.printStackTrace();
			}
			}
		});
		
		JScrollPane jsp=new JScrollPane(jTextPane);
		jSplit=new JSplitPane(JSplitPane.VERTICAL_SPLIT, false);
		jSplit.setTopComponent(jsp);
	  
		window=new JDesktopPane();
		window.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
		window.setBackground(SystemColor.controlShadow);
		tableContainer=getWorkbook(sheet1.getJScrollPane(),"测试数据", 20, 20, 600, 400);
		tableContainer.setDefaultCloseOperation(JInternalFrame.HIDE_ON_CLOSE);
		textContainer=getWorkbook(jSplit,"输出的SQL语句", 10, 10, 600, 400);
		textContainer.setDefaultCloseOperation(JInternalFrame.HIDE_ON_CLOSE);
		window.add(tableContainer);
		window.add(textContainer);
		
	  JTabbedPane jTabbedPane=new JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);
		jTabbedPane.addTab("选项设置", createForm());
		jTabbedPane.addTab("全局设置", createGlobalRex());
		
		JSplitPane jSplitPane=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, false, jTabbedPane,window);
		jSplitPane.setDividerLocation(150);
		
		JToolBar jToolBar=new JToolBar(JToolBar.HORIZONTAL);
		addMenuItems(jToolBar);
		
		JPanel jPanel=new JPanel(new BorderLayout());
    jPanel.add(jToolBar,BorderLayout.NORTH);
		jPanel.add(jSplitPane);
		
		JMenuBar jMenuBar=new JMenuBar();
		jMenuBar.setBackground(SystemColor.control);
		addMenuItems(jMenuBar);
		
		JFrame jFrame=new JFrame();
		jFrame.setTitle("johnSoft");
		jFrame.setIconImage(JohnResourcesManager.getImage(JohnResourcesManager.getTrayIconPng()));
		jFrame.add(jMenuBar,BorderLayout.NORTH);
		jFrame.add(jPanel);
		Dimension screenSize=Toolkit.getDefaultToolkit().getScreenSize();
		jFrame.setBounds(100, 100, screenSize.width-100*2, screenSize.height-100*2);
		jFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		jFrame.setVisible(true);
		
		readGlobalOption();
		for(int i=0;i<table.getColumnCount();i++)
		{
			JohnRexOptionColModel rm=new JohnRexOptionColModel(i);
			rm.setChfix(chfix.isSelected());
			rm.setChlen(new Integer(chlen.getValue().toString()));
			rm.setEnfix(enfix.isSelected());
			rm.setEnlen(new Integer(enlen.getValue().toString()));
			rm.setNumfix(numfix.isSelected());
			rm.setNumlen(new Integer(numlen.getValue().toString()));
			rm.setIntfix(intfix.isSelected());
			rm.setIntlen(new Integer(intlen.getValue().toString()));
			rm.setFloatfix(floatfix.isSelected());
			rm.setFloatlen(new Integer(floatlen.getValue().toString()));
			rms.add(rm);
		}
	}
	
	private void setKeywordColor(String text,int start)
	{
		String[] txts=text.split("\\s+");
		int currStart=0;
		for(String txt:txts)
		{
			int txtPos=text.indexOf(txt, currStart);
			sdoc.setCharacterAttributes(txtPos+start, txt.length(), defaults, false);
			for(String key:keyword)
			{
				if(key.equals(txt.toUpperCase()))
				{
					sdoc.setCharacterAttributes(txtPos+start, txt.length(), keywords, false);
				}
			}
			currStart=txtPos+txt.length();
		}
	}
	
	private void readGlobalOption()
	{
		try{
		Properties prop=new Properties();
		FileInputStream fis=new FileInputStream(JohnResourcesManager.getSqlMakerConfigPath());
		prop.load(fis);
		chlen.setValue(new Integer(prop.getProperty("chlen")));
		enlen.setValue(new Integer(prop.getProperty("enlen")));
		numlen.setValue(new Integer(prop.getProperty("numlen")));
		intlen.setValue(new Integer(prop.getProperty("intlen")));
		floatlen.setValue(new Integer(prop.getProperty("floatlen")));
		chfix.setSelected(new Boolean(prop.getProperty("chfix")));
		enfix.setSelected(new Boolean(prop.getProperty("enfix")));
		numfix.setSelected(new Boolean(prop.getProperty("numfix")));
		intfix.setSelected(new Boolean(prop.getProperty("intfix")));
		floatfix.setSelected(new Boolean(prop.getProperty("floatfix")));
		IPAndPort.setText(prop.getProperty("url"));
		username.setText(prop.getProperty("username"));
		databaseName.setText(prop.getProperty("databasename"));
		dateFormat.setText(prop.getProperty("dateformat"));
		if(prop.getProperty("databasetype").equals("mysql"))
		{
			mysql.setSelected(true);
		}else{
			oracle.setSelected(true);
		}
		fis.close();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private JInternalFrame getWorkbook(JComponent jComponent,String title,int x,int y,int width,int height)
	{
		JInternalFrame jInternalFrame=new JInternalFrame(title, true, true, true, true);
		jInternalFrame.add(jComponent);
		jInternalFrame.setBounds(x, y, width, height);
		jInternalFrame.show();
		return jInternalFrame;
	}
	
	private void addMenuItems(JMenuBar jMenuBar)
	{
		JMenu jm0=new JMenu("文件");
		jm0.setBackground(SystemColor.control);
		jm0.add(actionFactory.getOpenTableAction());
		jm0.add(actionFactory.getOpenTextAction());
		jm0.add(actionFactory.getFontLibAction());
		JMenu jm1=new JMenu("编辑");
		jm1.setBackground(SystemColor.control);
		jm1.add(actionFactory.getClearAction());
		JMenu jm2=new JMenu("运行");
		jm2.setBackground(SystemColor.control);
		jm2.add(actionFactory.getRandomAction());
		jm2.add(actionFactory.getTestAction());
		jm2.add(actionFactory.getJDBCSearchAction());
		jMenuBar.add(jm0);
		jMenuBar.add(jm1);
		jMenuBar.add(jm2);
	}
	
	private void addMenuItems(JToolBar jToolBar)
	{
		jToolBar.add(actionFactory.getRandomAction());
		jToolBar.add(actionFactory.getTestAction());
		jToolBar.add(actionFactory.getClearAction());
		jToolBar.add(actionFactory.getOpenTableAction());
		jToolBar.add(actionFactory.getOpenTextAction());
		jToolBar.add(actionFactory.getFontLibAction());
		jToolBar.add(actionFactory.getJDBCSearchAction());
	}
	
	private JPopupMenu getPopupMenu()
	{
		JPopupMenu jPopupMenu=new JPopupMenu();
		jPopupMenu.add(actionFactory.getRotateAction());
		jPopupMenu.add(actionFactory.getPasteAction());
		return jPopupMenu;
	}
	
	private JDialog getPopupSet(int sx,int sy,final int cx)
	{
		int colIndex=table.columnAtPoint(new Point(cx, 10));
		JDialog jDialog=new JohnOptionDialog(table,rms, sx, sy, colIndex);
		return jDialog;
	}
	
	private JScrollPane createForm()
	{
		JPanel jPanel=new JPanel();
		
		JLabel tip0=new JLabel("请输入表名:");
		tip0.setLabelFor(tableName);
		tableName.setPreferredSize(new Dimension(100,25));
		
		createTable=new JButton(actionFactory.getCreateTableAction());
		createTable.setPreferredSize(new Dimension(60, 25));
		createTable.setMargin(new Insets(0, 0, 0, 0));
		searchDataType=new JButton(actionFactory.getSearchDataTypeAction());
		searchDataType.setPreferredSize(new Dimension(60, 25));
		searchDataType.setMargin(new Insets(0, 0, 0, 0));
		
		JLabel tip1=new JLabel("转换日期类型数据为:");
		tip1.setLabelFor(dateFormat);
		dateFormat.setPreferredSize(new Dimension(100,25));
	
		JLabel tip2=new JLabel("请输入随机数据行数:");
		tip2.setLabelFor(rowCount);
		rowCount.setPreferredSize(new Dimension(100,25));
		
		JLabel tip3=new JLabel("服务器地址:");
		tip3.setLabelFor(IPAndPort);
		IPAndPort.setPreferredSize(new Dimension(100,25));
		
		JLabel tip4=new JLabel("数据库名:");
		tip4.setLabelFor(databaseName);
		databaseName.setPreferredSize(new Dimension(100,25));
		
		JLabel tip5=new JLabel("用户名:");
		tip5.setLabelFor(username);
		username.setPreferredSize(new Dimension(100,25));
		
		JLabel tip6=new JLabel("密    码:");
		tip6.setLabelFor(password);
		password.setPreferredSize(new Dimension(100,25));
	
		bg.add(mysql);
		bg.add(oracle);
		
		jPanel.add(tip0);
		jPanel.add(tableName);
		jPanel.add(createTable);
		jPanel.add(searchDataType);
		jPanel.add(tip1);
		jPanel.add(dateFormat);
		jPanel.add(tip2);
		jPanel.add(rowCount);
		jPanel.add(tip3);
		jPanel.add(IPAndPort);
		jPanel.add(tip4);
		jPanel.add(databaseName);
		jPanel.add(tip5);
		jPanel.add(username);
		jPanel.add(tip6);
		jPanel.add(password);
		jPanel.add(mysql);
		jPanel.add(oracle);
		
		jPanel.setPreferredSize(new Dimension(120, 400));
		JScrollPane jsp=new JScrollPane(jPanel);
		return jsp;
	}
	
	private JScrollPane createGlobalRex()
	{
		JPanel jPanel=new JPanel();
		JPanel jPanel1=new JPanel();
		JPanel jPanel2=new JPanel();
		jPanel1.setBorder(BorderFactory.createTitledBorder("字符串类型"));
		jPanel2.setBorder(BorderFactory.createTitledBorder("数值类型"));
		
		intfix.setSelected(true);
		
		JLabel tip0=new JLabel("中文字符个数:");
		tip0.setLabelFor(chlen);
		
		JLabel tip1=new JLabel("英文字符个数:");
		tip1.setLabelFor(enlen);
	
		JLabel tip2=new JLabel("数字字符个数:");
		tip2.setLabelFor(numlen);
		
		JLabel tip3=new JLabel("数值总长度:");
		tip3.setLabelFor(intlen);
		
		JLabel tip4=new JLabel("小数位数:");
		tip4.setLabelFor(floatlen);
		
		jPanel1.add(tip0);
		jPanel1.add(chlen);
		jPanel1.add(chfix);
		jPanel1.add(tip1);
		jPanel1.add(enlen);
		jPanel1.add(enfix);
		jPanel1.add(tip2);
		jPanel1.add(numlen);
		jPanel1.add(numfix);
		
		jPanel2.add(tip3);
		jPanel2.add(intlen);
		jPanel2.add(intfix);
		jPanel2.add(tip4);
		jPanel2.add(floatlen);
		jPanel2.add(floatfix);
		
		jPanel.add(jPanel1);
		jPanel.add(jPanel2);
		save=new JButton(actionFactory.getSaveGlobalRexAction());
		jPanel.add(save);
		JScrollPane jsp=new JScrollPane(jPanel);
		jPanel1.setPreferredSize(new Dimension(140, 250));
		jPanel2.setPreferredSize(new Dimension(140, 140));
		save.setPreferredSize(new Dimension(100, 20));
		jPanel.setPreferredSize(new Dimension(140, 420));
		jsp.setPreferredSize(new Dimension(140, 420));
		
		return jsp;
	}
	
	private boolean checkOption(boolean isJDBC)
	{
		if(tableName.getText().trim().equals(""))
		{
			JOptionPane.showMessageDialog(null, "表名不能为空！","警告",JOptionPane.WARNING_MESSAGE);
			return false;
		}
		if(isJDBC)
		{
			if(IPAndPort.getText().trim().equals("")||databaseName.getText().trim().equals("")||username.getText().trim().equals("")||new String(password.getPassword()).equals(""))
			{
				JOptionPane.showMessageDialog(null, "无法取得有效的数据库连接！","警告",JOptionPane.WARNING_MESSAGE);
				return false;
			}
		}
		return true;
	}
	
	public class ActionFactory
	{
		private JohnDataProvider johnDataProvider=new JohnDataProvider();

		public Action getTestAction()
		{
			AbstractAction action=new AbstractAction()
			{
				private static final long serialVersionUID = 1L;
				@Override
				public void actionPerformed(ActionEvent e)
				{
					if(!checkOption(false))
					{
						return;
					}
					String result=johnDataProvider.createTestInsert(table,tableName.getText(),dateFormat.getText());
					result=jTextPane.getText()+result;
					jTextPane.setText(result);
					setKeywordColor(result,0);
				}
			};
			action.putValue(Action.NAME,"执行测试数据");
			action.putValue(Action.SHORT_DESCRIPTION, "执行测试数据");
			action.putValue(Action.SMALL_ICON, JohnResourcesManager.getImageIcon(JohnResourcesManager.getRightPngPath()));
			action.putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke("F8"));
			return action;
		}
		
		public Action getRandomAction()
		{
			AbstractAction action=new AbstractAction()
			{
				private static final long serialVersionUID = 1L;
				@Override
				public void actionPerformed(ActionEvent e)
				{
					int rows=0;
					String count=rowCount.getText();
					if(count!=null&&!count.equals(""))
					{
						rows=Integer.parseInt(count);
					}
					String tableNames=tableName.getText();
					String dateFormats=dateFormat.getText();
					if(!checkOption(false))
					{
						return;
					}
					String result=johnDataProvider.createRandomInsert(table,tableNames,dateFormats,rms,rows);
					result=jTextPane.getText()+result;
					jTextPane.setText(result);
					setKeywordColor(result,0);
				}
			};
			action.putValue(Action.NAME,"执行随机数据");
			action.putValue(Action.SHORT_DESCRIPTION, "执行随机数据");
			action.putValue(Action.SMALL_ICON, JohnResourcesManager.getImageIcon(JohnResourcesManager.getMoveNodePngPath()));
			action.putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.CTRL_DOWN_MASK));
			return action;
		}
		
		public Action getPasteAction()
		{
			AbstractAction action=new AbstractAction()
			{
				private static final long serialVersionUID = 1L;
				@Override
				public void actionPerformed(ActionEvent e)
				{
					int row=table.getSelectedRow();
					int col=table.getSelectedColumn();
					if(sheet1.likeExcel())
					{
						sheet1.pasteFromExcel(row,col);
					}else{
						sheet1.pasteFilePath(row, col);
					}
				}
			};
			action.putValue(Action.NAME,"粘贴");
			action.putValue(Action.SHORT_DESCRIPTION, "粘贴");
			action.putValue(Action.SMALL_ICON, JohnResourcesManager.getImageIcon(JohnResourcesManager.getPastePng()));
			action.putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke("ctrl V"));
			return action;
		}
		
		public Action getRotateAction()
		{
			AbstractAction action=new AbstractAction()
			{
				private static final long serialVersionUID = 1L;
				@Override
				public void actionPerformed(ActionEvent e)
				{
					if(sheet1.isCross())
					{
						sheet1.setCross(false);
					}else{
						sheet1.setCross(true);
					}
				}
			};
			action.putValue(Action.NAME,"转置");
			action.putValue(Action.SHORT_DESCRIPTION, "转置");
			action.putValue(Action.SMALL_ICON, JohnResourcesManager.getImageIcon(JohnResourcesManager.getSafariPng()));
			action.putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke("alt X"));
			return action;
		}
		
		public Action getClearAction()
		{
			AbstractAction action=new AbstractAction()
			{
				private static final long serialVersionUID = 1L;
				@Override
				public void actionPerformed(ActionEvent e)
				{
					sheet1.removeAllData();
				}
			};
			action.putValue(Action.NAME,"清空数据");
			action.putValue(Action.SHORT_DESCRIPTION, "清空数据");
			action.putValue(Action.SMALL_ICON, JohnResourcesManager.getImageIcon(JohnResourcesManager.getRemoveNodePng()));
			action.putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke("ctrl D"));
			return action;
		}
		
		public Action getOpenTableAction()
		{
			AbstractAction action=new AbstractAction()
			{
				private static final long serialVersionUID = 1L;
				@Override
				public void actionPerformed(ActionEvent e)
				{
						tableContainer.show();
				}
			};
			action.putValue(Action.NAME,"打开表格");
			action.putValue(Action.SHORT_DESCRIPTION, "打开表格以填写生成测试数据规则");
			action.putValue(Action.SMALL_ICON, JohnResourcesManager.getImageIcon(JohnResourcesManager.getExpandNodePng()));
			action.putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke("ctrl O"));
			return action;
		}
		
		public Action getOpenTextAction()
		{
			AbstractAction action=new AbstractAction()
			{
				private static final long serialVersionUID = 1L;
				@Override
				public void actionPerformed(ActionEvent e)
				{
						textContainer.show();
				}
			};
			action.putValue(Action.NAME,"打开输入面板");
			action.putValue(Action.SHORT_DESCRIPTION, "打开输入面板以查看sql语句");
			action.putValue(Action.SMALL_ICON, JohnResourcesManager.getImageIcon(JohnResourcesManager.getBooksPng()));
			action.putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke("ctrl P"));
			return action;
		}
		
		public Action getDeleteAction()
		{
			AbstractAction action=new AbstractAction()
			{
				private static final long serialVersionUID = 1L;
				@Override
				public void actionPerformed(ActionEvent e)
				{
					sheet1.removeSelectedData();
				}
			};
			return action;
		}
		
		public Action getFontLibAction()
		{
			AbstractAction action=new AbstractAction()
			{
				private static final long serialVersionUID = 1L;
				@Override
				public void actionPerformed(ActionEvent e)
				{
					final JTextArea jTextArea=new JTextArea();
					jTextArea.setLineWrap(true);
					jTextArea.setText(JohnDataProvider.getStrs());
					final JDialog jDialog=new JDialog();
					jDialog.add(new JScrollPane(jTextArea));
					JPanel jPanel=new JPanel();
					final JButton jButton=new JButton("保存");
					jButton.addActionListener(new ActionListener()
					{
						@Override
						public void actionPerformed(ActionEvent e)
						{
							boolean isSuccess=JohnDataProvider.setStrs(jTextArea.getText().replaceAll("\\s+||\t||\n", ""));
							if(isSuccess)
							{
								JOptionPane.showMessageDialog(jDialog, "保存成功！");
								jDialog.dispose();
							}
						}
					});
					jPanel.add(jButton);
					jDialog.add(jPanel, BorderLayout.SOUTH);
					Dimension screenSize=Toolkit.getDefaultToolkit().getScreenSize();
					jDialog.setBounds(200, 150, screenSize.width-200*2, screenSize.height-150*2);
				  jDialog.setVisible(true);
				}
			};
			action.putValue(Action.NAME,"字库设置");
			action.putValue(Action.SHORT_DESCRIPTION, "设置随机生成汉字的字库");
			action.putValue(Action.SMALL_ICON, JohnResourcesManager.getImageIcon(JohnResourcesManager.getWordsPng()));
			action.putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke("ctrl alt W"));
			return action;
		}
		
		public Action getJDBCSearchAction()
		{
			AbstractAction action=new AbstractAction()
			{
				private static final long serialVersionUID = 1L;
				@Override
				public void actionPerformed(ActionEvent e)
				{
					String command="";
					Enumeration<AbstractButton> enums=bg.getElements();
					while(enums.hasMoreElements())
					{
						AbstractButton button=enums.nextElement();
						if(button.isSelected())
						{
							command=button.getActionCommand();
							break;
						}
					}
					if(!checkOption(true))
					{
						return;
					}
					JohnJDBCTable jjt=new JohnJDBCTable(command,IPAndPort.getText()+"/"+databaseName.getText(), username.getText(), new String(password.getPassword()));
				  jjt.insertAll(jTextPane.getText());
					jjt.getResultSet(tableName.getText());
				  jSplit.setBottomComponent(jjt.getJScrollPane());
				}
			};
			action.putValue(Action.NAME,"执行插入并查询");
			action.putValue(Action.SHORT_DESCRIPTION, "执行插入并查询");
			action.putValue(Action.SMALL_ICON, JohnResourcesManager.getImageIcon(JohnResourcesManager.getExePng()));
			action.putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke("ctrl shift U"));
			return action;
		}
		
		public Action getSaveGlobalRexAction()
		{
			AbstractAction action=new AbstractAction()
			{
				private static final long serialVersionUID = 1L;
				@Override
				public void actionPerformed(ActionEvent e)
				{
					save.setEnabled(false);
					try
					{
						Properties prop=new Properties();
						FileInputStream fis = new FileInputStream(JohnResourcesManager.getSqlMakerConfigPath());
						prop.load(fis);
						prop.setProperty("chlen",chlen.getValue().toString());
						prop.setProperty("enlen", enlen.getValue().toString());
						prop.setProperty("numlen", numlen.getValue().toString());
						prop.setProperty("intlen", intlen.getValue().toString());
						prop.setProperty("floatlen", floatlen.getValue().toString());
						prop.setProperty("chfix", new Boolean(chfix.isSelected()).toString());
						prop.setProperty("enfix", new Boolean(enfix.isSelected()).toString());
						prop.setProperty("numfix", new Boolean(numfix.isSelected()).toString());
						prop.setProperty("intfix", new Boolean(intfix.isSelected()).toString());
						prop.setProperty("floatfix", new Boolean(floatfix.isSelected()).toString());
						prop.setProperty("dateformat", dateFormat.getText());
						prop.setProperty("url", IPAndPort.getText());
						prop.setProperty("username", username.getText());
						prop.setProperty("databasename", databaseName.getText());
						if(mysql.isSelected())
						{
							prop.setProperty("databasetype", "mysql");
						}else{
							prop.setProperty("databasetype", "oracle");
						}
						FileOutputStream fos=new FileOutputStream(JohnResourcesManager.getSqlMakerConfigPath());
						prop.store(fos,"");
						for(JohnRexOptionColModel rm:rms)
						{
							rm.setChfix(chfix.isSelected());
							rm.setChlen(new Integer(chlen.getValue().toString()));
							rm.setEnfix(enfix.isSelected());
							rm.setEnlen(new Integer(enlen.getValue().toString()));
							rm.setNumfix(numfix.isSelected());
							rm.setNumlen(new Integer(numlen.getValue().toString()));
							rm.setIntfix(intfix.isSelected());
							rm.setIntlen(new Integer(intlen.getValue().toString()));
							rm.setFloatfix(floatfix.isSelected());
							rm.setFloatlen(new Integer(floatlen.getValue().toString()));
						}
						JOptionPane.showMessageDialog(null, "设置已保存！");
						fis.close();
					} catch (Exception e1)
					{
						e1.printStackTrace();
					}
					save.setEnabled(true);
				}
			};
			action.putValue(Action.NAME,"储存设置");
			action.putValue(Action.SHORT_DESCRIPTION, "储存设置");
			action.putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke("ctrl S"));
			return action;
		}
		
		public Action getSearchDataTypeAction()
		{
			AbstractAction action=new AbstractAction()
			{
				private static final long serialVersionUID = 1L;
				@Override
				public void actionPerformed(ActionEvent e)
				{
					searchDataType.setEnabled(false);
					String command="";
					Enumeration<AbstractButton> enums=bg.getElements();
					while(enums.hasMoreElements())
					{
						AbstractButton button=enums.nextElement();
						if(button.isSelected())
						{
							command=button.getActionCommand();
							break;
						}
					}
					if(!checkOption(true))
					{
						searchDataType.setEnabled(true);
						return;
					}
					JohnJDBCTable jjt=new JohnJDBCTable(command,IPAndPort.getText()+"/"+databaseName.getText(), username.getText(), new String(password.getPassword()));
				  jjt.searchDataType(table, tableName.getText());
					searchDataType.setEnabled(true);
				}
			};
			action.putValue(Action.NAME,"查询结构");
			action.putValue(Action.SHORT_DESCRIPTION, "查询结构");
			action.putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke("shift S"));
			return action;
		}
		
		public Action getCreateTableAction()
		{
			AbstractAction action=new AbstractAction()
			{
				private static final long serialVersionUID = 1L;
				@Override
				public void actionPerformed(ActionEvent e)
				{
					createTable.setEnabled(false);
					if(!checkOption(false))
					{
						createTable.setEnabled(true);
						return;
					}
					StringBuilder sb=new StringBuilder("CREATE TABLE ");
					sb.append(tableName.getText());
					sb.append(" (\n");
					for(int i=0;i<table.getColumnCount();i++)
					{
						if(table.getValueAt(1, i)==null||table.getValueAt(1, i).toString().equals(""))
						{
							continue;
						}
						if(table.getValueAt(0, i)==null||table.getValueAt(0, i).toString().equals(""))
						{
							continue;
						}
						sb.append(table.getValueAt(1, i).toString().toUpperCase())
						  .append(" ")
						  .append(table.getValueAt(0, i).toString().toUpperCase())
						  .append(",\n");
					}
					sb.deleteCharAt(sb.lastIndexOf(","));
					sb.append(");");
					jTextPane.setText(sb.toString());
					setKeywordColor(sb.toString(),0);
					createTable.setEnabled(true);
				}
			};
			action.putValue(Action.NAME,"建表语句");
			action.putValue(Action.SHORT_DESCRIPTION, "根据列名和类型生成建表sql");
			action.putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke("shift C"));
			return action;
		}
	}
}
