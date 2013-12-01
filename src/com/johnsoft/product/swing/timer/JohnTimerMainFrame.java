package com.johnsoft.product.swing.timer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.font.TextAttribute;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;

import com.johnsoft.library.swing.component.JohnNotifyIconFactory;
import com.johnsoft.library.swing.component.JohnStartTip;
import com.johnsoft.library.swing.component.JohnTooltip;
import com.johnsoft.library.swing.component.datechooser.JohnDateChooser;
import com.johnsoft.library.swing.component.tile.JohnBasicTile;
import com.johnsoft.library.swing.component.tile.JohnTile;
import com.johnsoft.library.swing.component.tile.JohnTilePanel;
import com.johnsoft.library.swing.component.titlepane.JohnTitlePane;
import com.johnsoft.library.util.common.JohnDateHelper;
import com.johnsoft.library.util.common.JohnPathUtil;
import com.johnsoft.library.util.common.JohnRegistryUtil;
import com.johnsoft.product.swing.startup.JohnResourcesManager;
import com.johnsoft.product.swing.timer.tile.JohnFileTreeTile;
import com.johnsoft.product.swing.timer.tile.JohnLockWindowTile;


/**
 * 主程序窗体
 * @author john
 */
public class JohnTimerMainFrame extends JFrame implements ActionListener
{
	private static final long serialVersionUID = 1L;
	
	private JPanel pane;//主窗口下边面板
	private JButton okButton;//确认按钮，执行新部署并保存配置
	private JLabel more;//附加功能管理器入口
	private JTable table;//主列表
	private JComboBox<String> taskType;//任务类型
	private JPopupMenu jpmenu;//表格弹出菜单：删除本任务,删除未启用任务
	
	private JohnNotifyIconFactory tray;//系统托盘
	private JohnTimerXmlManager helper;//封装dom4j辅助保存读取配置
	private List<JohnTimer> list=new ArrayList<JohnTimer>();//存放执行中的任务
	
	private JohnLockWindowTile lockWindowTile;
	private JohnFileTreeTile fileTreeTile;
	
	private JohnTitlePane titlePane;
	
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
				new JohnTimerMainFrame();
			}
		});
	}
	
	public JohnTimerMainFrame()
	{
		if(new Random().nextBoolean())
		{
			new JohnTooltip("joftSoft已启动！\n正为您执行定时任务！");//气泡提示
		}else{
			new JohnStartTip("joftSoft已启动！\n正为您执行定时任务！");//开始文字提示
		}
		initComponent();
		//注册表映射开机自启动
		String name="johnSoft";
		String value=JohnPathUtil.getSelfJarLaunchAbsolutePath(); 
		String result=JohnRegistryUtil.checkAutoStart(name);
		if(result==null||(result!=null&&!result.equals(value)))
		{
			JohnRegistryUtil.regAutoStart(name,value);
		}
	}
	
	private void initComponent()
	{
  	pane=new JPanel();
  	okButton=new JButton("确定"); 	
  	okButton.addActionListener(this);
  	more=new JLabel("附件功能管理器");
  	final Map<TextAttribute,Object> map=new HashMap<TextAttribute,Object>();
  	map.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
 // 	map.put(TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE); //斜体
  	map.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
  	map.put(TextAttribute.FOREGROUND, Color.BLUE);
  	map.put(TextAttribute.SIZE, 12);
  	map.put(TextAttribute.FAMILY, "微软雅黑");
    more.setFont(new Font(map));
    more.setPreferredSize(new Dimension(240, 30));
    more.setHorizontalAlignment(JLabel.RIGHT);
    ((FlowLayout)pane.getLayout()).setAlignment(FlowLayout.RIGHT);
  	pane.add(okButton);
  	pane.add(more);
  	more.addMouseListener(new MouseAdapter()
		{
  		private JPanel glass=getGlass();
  		
			private JPanel getGlass()
			{
  			final JPanel jpx=new JPanel(){
  				private static final long serialVersionUID = 1L;
  			  @Override
  			  protected void paintComponent(Graphics g)
  			  { 
  			  	super.paintComponent(g);
			  		Rectangle rect=g.getClipBounds();
			  		Graphics2D g2=(Graphics2D)g;
			  		
			  		RoundRectangle2D.Float rr=new RoundRectangle2D.Float(0, 0, rect.width, rect.height-90,20,20);
			  		Rectangle2D.Float r2f=new Rectangle2D.Float(0, rect.height-100, rect.width, 30);
			  		Rectangle2D.Float rdf=new Rectangle2D.Float(rect.width-150, rect.height-70, 150, 40);
			  		Area area=new Area();
			  		area.add(new Area(rr));
			  		area.add(new Area(r2f));
			  		area.add(new Area(rdf));
			  		
			  		g2.setPaint(new Color(0,0,0,150));
			  		g2.fill(area);
  			  }
  			};
  			jpx.addMouseListener(new MouseAdapter(){
  				@Override
  				public void mouseClicked(MouseEvent e)
  				{
  					super.mouseClicked(e);
  				}
  			});
  			jpx.addMouseMotionListener(new MouseAdapter(){
  				@Override
  				public void mouseMoved(MouseEvent e)
  				{
  					Rectangle rect=new Rectangle(0, jpx.getHeight()-70, jpx.getWidth()-150, 70);
  					if(rect.contains(e.getX(),e.getY()))
  					{
  						jpx.setVisible(false);
  					}
  				}
  			});
  			jpx.setOpaque(false);
  			JohnTilePanel tilePane=new JohnTilePanel();
  			tilePane.setOpaque(false);
  			List<JohnTile> tileTist=new ArrayList<JohnTile>();
  			lockWindowTile=new JohnLockWindowTile(JohnResourcesManager.getImageIcon(JohnResourcesManager.getUnLockPng()),JohnResourcesManager.getImageIcon(JohnResourcesManager.getLockPng()),"离开时锁屏");
  			tileTist.add(lockWindowTile);
  			fileTreeTile=new JohnFileTreeTile(JohnResourcesManager.getImageIcon(JohnResourcesManager.getTreePng()), "文件管理器");
  			tileTist.add(fileTreeTile);
  			for(int i=0;i<8;i++)
  			{
  				tileTist.add(new JohnBasicTile(null,"未添加功能"));
  			}
  			tilePane.setTilePanelModel(tileTist);
  			tilePane.setTilePanelLayoutAutoBounds(5, new Dimension(90, 100), 10, false);
  			jpx.add(tilePane);
  			JohnTimerMainFrame.this.setGlassPane(jpx);
  			return jpx;
			}
			
  		@Override
  		public void mouseReleased(MouseEvent e)
  		{
  			if(e.getX()>145)
  			{
  				map.put(TextAttribute.FOREGROUND, Color.BLUE);
   			  more.setFont(new Font(map));
   			  glass.setVisible(true);
  			}
  		}
  		@Override
  		public void mousePressed(MouseEvent e)
  		{
  			if(e.getX()>145)
  			{
  				map.put(TextAttribute.FOREGROUND, Color.RED);
   			  more.setFont(new Font(map));
  			}
  		}
  		@Override
  		public void mouseExited(MouseEvent e)
  		{
  			 more.setCursor(Cursor.getDefaultCursor());
  			 if(!glass.isVisible())
  			 {
  				 map.put(TextAttribute.FOREGROUND, Color.BLUE);
    			 more.setFont(new Font(map));
  			 }
  		}
		});
  	more.addMouseMotionListener(new MouseAdapter()
		{
  		@Override
  		public void mouseMoved(MouseEvent e)
  		{
  			if(e.getX()<145)
  			{
  				 more.setCursor(Cursor.getDefaultCursor());
   				 map.put(TextAttribute.FOREGROUND, Color.BLUE);
     			 more.setFont(new Font(map));
  			}else{
  				 more.setCursor(new Cursor(Cursor.HAND_CURSOR));
      		 map.put(TextAttribute.FOREGROUND, Color.CYAN);
      		 more.setFont(new Font(map));
  			}
  		}
		});
  	
  	jpmenu=getPopupMenu();
  	
  	taskType=new JComboBox<String>(new String[]{"关机","锁屏","待机","打开网址","消息提醒","执行文件","删除文件"});
  	
  	table=new JTable();
  	//读取配置schedule.xml为表格准备数据
  	try
		{
			helper=new JohnTimerXmlManager(new FileInputStream(JohnResourcesManager.getScheduleCofigPath()));
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, e.getMessage());
		}
		Vector<Vector<Object>> data=helper.getElementsValues();
  	Vector<String> columnNames=new Vector<String>();
  	columnNames.addAll(Arrays.asList("任务ID","是否启用","任务类型","首次执行日","频次(日)","小时数","分钟数","配置项"));
  	
  	table.setModel(new DefaultTableModel(data, columnNames)
  	{
			private static final long serialVersionUID = 1L;
			@Override
  		public Class<?> getColumnClass(int columnIndex)
  		{
  			return this.getValueAt(0, columnIndex).getClass();//boolean类型用checkbox显示
  		}
  		@Override
  		public Object getValueAt(int row, int column)
  		{
  			if(column==4&&(Integer)super.getValueAt(row, column)>=63)
  			{//执行间隔不能大于2个月
  				return 0;
  			}else if(column==5&&(Integer)super.getValueAt(row, column)>=24)
  			{//小时数不能大于23
  				return 0;
  			}
  			else if(column==6&&(Integer)super.getValueAt(row, column)>=60)
  			{//分钟数不能大于59
  				return 0;
  			}
  			if(column==3&&!JohnDateHelper.isFormattedDate((String)super.getValueAt(row, column), "yyyy-MM-dd"))
  			{
  				return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
  			}
  			return super.getValueAt(row, column);
  		}
  		@Override
  		public boolean isCellEditable(int row, int column)
  		{
  			if(column==0||column==7)
  			{
  				return false;
  			}
  			return super.isCellEditable(row, column);
  		}
  	});
  	
  	table.addMouseListener(new MouseAdapter()
		{
  		private int row,col;
  		private String type;
  		private JohnTimerMainFrame mainFrame=JohnTimerMainFrame.this;
  		private JTextArea text;
  		private JFileChooser fileChooser;
  		private JDialog dialog;
  		
			private WindowFocusListener wfl=new WindowAdapter()
			{
				@Override
				public void windowGainedFocus(WindowEvent e)
				{
					closeDialog(1);
				}
			};
			
  		@Override
  		public void mouseClicked(MouseEvent e)
  		{
  			if(e.getButton()==MouseEvent.BUTTON1)
  			{
  				closeDialog(0);
  				int r=table.getSelectedRow();
  				int c=table.getSelectedColumn();
  				if(r<0||c<0) return;
    			row=r; col=c;
    			type=(String)table.getValueAt(row, 2);
    			if(col==7)
    			{
    				if("关机".equals(type)||"锁屏".equals(type)|| "待机".equals(type))
    				{
    					copyRowToNext();
    					return;
    				}
    				//转换坐标
    				int x=mainFrame.getLocation().x;
    				Rectangle rect=table.getCellRect(row, col, true);
    				Point point=new Point(rect.x,rect.y);
    				SwingUtilities.convertPointToScreen(point, table);
    				dialog=new JDialog();
      			//主窗口变化时消失
      			mainFrame.addWindowFocusListener(wfl);
    				//如果单元格有值就复制到textarea否则填充默认提示
      			if("打开网址".equals(type)||"消息提醒".equals(type))
      			{
      				showEditDialog();
      			}
      			else if("执行文件".equals(type)||"删除文件".equals(type))
      			{
      				showFileDialog(type);
      			}
      			((JPanel)dialog.getContentPane()).setBorder(BorderFactory.createRaisedBevelBorder());
      			dialog.setUndecorated(true);
      			dialog.setBounds(x+5, point.y+rect.height, 540, 300);//在单元格之下并等宽与主窗口显示
      			dialog.setAlwaysOnTop(true);
      			dialog.setVisible(true);
    			}
  			}
  			else if(e.getButton()==MouseEvent.BUTTON3)
  			{//右键表格任务id列并且不是第一行弹出快捷菜单
  				if(col==0&&row!=0)
  				{
  					jpmenu.show(table, e.getX(), e.getY());
  				}
  			}
  		}
  		
  		private void showFileDialog(String type)
  		{
  			  File[] files=null;String str=null;boolean contain=false;
  			  String value=(String)table.getValueAt(row, col);
  			  String[] values=value.split("@");
  			  if(values.length>0&&values[0].length()>0)
  			  {
  			  	String[] fileNames=values[0].split(";");
  			  	files=new File[fileNames.length];
  			  	for(int i=0;i<fileNames.length;i++)
  			  	{
  			  		files[i]=new File(fileNames[i]);
  			  	}
  			  	if(values.length==3)
  			  	{
  			  		str=values[1];
  			  		contain=new Boolean(values[2]);
  			  	}
  			  }
  			  
  				fileChooser=new JFileChooser();
    			fileChooser.setDialogType(JFileChooser.CUSTOM_DIALOG);
    			fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);//可选文件或目录
    			fileChooser.setMultiSelectionEnabled(true);//可多选
    			fileChooser.setApproveButtonText("确认");
    			if(files!=null&&files.length>0)
    			{//恢复上次选择
    				fileChooser.setSelectedFiles(files);
    			}
    		  //去掉左面板，替换下拉框为文本框,加入checkbox
    			fileChooser.getComponent(0).setVisible(false);
    			JPanel bottomPane=(JPanel)fileChooser.getComponent(2);
    			JPanel twoFieldPane=(JPanel)bottomPane.getComponent(2);
    			JPanel twoField=(JPanel)twoFieldPane.getComponent(2);
    			twoField.getComponent(3).setVisible(false);//去掉原文件类型下拉组合选框
    			if(str==null||"".equals(str))
    			{
    				str="可选输入定期删除的文件类型,类型间以;分隔";
    			}
    			final JTextField field=new JTextField(str);
    			final JCheckBox checkBox=new JCheckBox("是否联同子文件夹下的文件");
    			checkBox.setSelected(contain);
    			twoField.add(field);
    			if("执行文件".equals(type))
    			{
    				field.setEnabled(false);
    				checkBox.setEnabled(false);
    			}
    			dialog.add(fileChooser);
    			JPanel jp=new JPanel();
    			jp.add(checkBox);
    			dialog.add(jp,BorderLayout.SOUTH);
  			  fileChooser.addActionListener(new ActionListener()
					{
						@Override
						public void actionPerformed(ActionEvent e)
						{
							if(e.getActionCommand().equals("ApproveSelection"))
							{
								StringBuffer sb=new StringBuffer();
								File[] files=fileChooser.getSelectedFiles();
								if(files==null||files.length==0)
								{
									return;
								}
								for(File f:files)
								{
									sb.append(f).append(";");
								}
								sb.deleteCharAt(sb.length()-1);
								if(Pattern.matches("^[a-zA-Z][a-zA-Z0-9;]*[a-zA-Z]$", field.getText().trim()))
								{
									sb.append("@").append(field.getText().trim());
									sb.append("@").append(checkBox.isSelected());
								}
								table.setValueAt(sb.toString(), row, col);
		  					copyRowToNext();
								closeDialog(1);
							}else{
								closeDialog(1);
							}
						}
					});
  		}
  		
  		private void showEditDialog()
  		{
  			String str="";
  			String val=(String)table.getValueAt(row, 7);
  			if(val!=null&&!"".equals(val))
  			{
  				str=val;
  			}else{
  				if("打开网址".equals(type))
  				{
  					str="请输入地址";
  				}else{
  					str="请输入消息";
  				}
  			}
  			
  			text=new JTextArea(str);
  			text.setLineWrap(true);//自动换行
  			text.selectAll();
  			JButton butt=new JButton("应用配置");
  			JPanel jp=new JPanel();
  			jp.add(butt);
  			dialog.add(new JScrollPane(text));
  			dialog.add(jp,BorderLayout.SOUTH);
  			
  			butt.addActionListener(new ActionListener()
  			{
  				@Override
  				public void actionPerformed(ActionEvent e)
  				{//如果配置项有效就添加到表格上
  					String test=text.getText().trim();
  					boolean ishttp="打开网址".equals(type)&&!test.startsWith("http://")&&!test.startsWith("https://");
  					if("".equals(test)||"请输入地址".equals(test)||"请输入消息".equals(test)||ishttp)
  					{
  						closeDialog(1);
  						return;
  					}
  					table.setValueAt(text.getText(), row, col);
  					copyRowToNext();
  					closeDialog(1);
  				}
  			});
  		}
  		
  		private void closeDialog(int type)
  		{//关闭释放对话框
  			if(dialog!=null)
  			{
  				dialog.dispose();
  			}
  			if(type==1)
  			{
  				mainFrame.removeWindowFocusListener(wfl);
  			}
  		}
  		
  		@SuppressWarnings("unchecked")
  		private void copyRowToNext()
  		{//复制该行内容到下一行
				if(table.getRowCount()==row+1)
				{
					DefaultTableModel model=(DefaultTableModel)table.getModel();
					Vector<Object> vector=(Vector<Object>)((Vector<Object>)model.getDataVector().elementAt(row)).clone();
					vector.set(0, getRandomStr());
					vector.set(1, false);
					vector.set(7, "");
					model.addRow(vector);
				}
				closeDialog(1);
  		}
  		
		});
  	
  	table.getColumn("任务类型").setCellEditor(new DefaultCellEditor(taskType){
			private static final long serialVersionUID = 1L;
  		@Override
  		public boolean isCellEditable(EventObject anEvent)
  		{
  			if(((MouseEvent)anEvent).getClickCount()==2)
  			{//该单元格双击后才允许编辑，即弹出下拉框
  				return true;
  			}
  			return false;
  		}
  	});
  	//安排日历控件
  	JTextField jtf=new JTextField();
  	JohnDateChooser dateChooser=new JohnDateChooser("yyyy-MM-dd",true);
  	jtf.setEditable(false);
  	dateChooser.register(jtf, this, jtf);
  	table.getColumn("首次执行日").setCellEditor(new DefaultCellEditor(jtf));
  	//调整列宽
  	table.getColumn("任务ID").setMinWidth(60);
  	table.getColumn("是否启用").setMinWidth(65);
  	table.getColumn("任务类型").setMinWidth(100);
  	table.getColumn("首次执行日").setMinWidth(80);
  	table.getColumn("频次(日)").setMinWidth(70);
  	table.getColumn("小时数").setMinWidth(50);
  	table.getColumn("分钟数").setMinWidth(50);
  	table.getColumn("配置项").setMinWidth(85);
  	
  	table.getTableHeader().setReorderingAllowed(false);
    table.getTableHeader().setResizingAllowed(false);
  	table.setShowGrid(false);
  	table.setCellSelectionEnabled(true);
  	table.setIntercellSpacing(new Dimension(0, 0));
  	table.setRowHeight(20);
  	table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
  	table.setFillsViewportHeight(true);
  	table.setSelectionBackground(Color.WHITE);
  	table.setSelectionForeground(Color.BLUE);

  	tray=new JohnNotifyIconFactory();
		tray.installToSystemTray(this, JohnResourcesManager.getImage(JohnResourcesManager.getTrayIconPng()), "johnSoft");
		this.setIconImage(JohnResourcesManager.getImage(JohnResourcesManager.getTrayIconPng()));
		this.setTitle("johnSoft");
		this.add(pane,BorderLayout.SOUTH);
		titlePane=new JohnTitlePane(this);
		titlePane.setIcon(JohnResourcesManager.getImage(JohnResourcesManager.getTrayIconPng()));
		titlePane.setBackgroundImage(JohnResourcesManager.getImage(JohnResourcesManager.getSkinJpg(5)));
		JPanel contentPane=titlePane.asContentPane();
		contentPane.add(new JScrollPane(table));
		contentPane.add(pane,BorderLayout.SOUTH);
		this.setSize(550, 330);
		this.setLocationRelativeTo(null);
		this.setResizable(false);
		//初始化系统托盘
		tray.showNotifyIcon(this, JohnResourcesManager.getImage(JohnResourcesManager.getTrayIconPng()), "johnSoft");
		
		deploy();
		
		addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosed(WindowEvent e)
			{
				try
				{
					File file=new File(JohnResourcesManager.getLockWindowTimerIni());
					FileOutputStream fos=new FileOutputStream(file);
					fos.write(System.getProperty("lock_window_timer").getBytes());
					fos.close();
				} catch (IOException e1)
				{
					e1.printStackTrace();
				}
				super.windowClosed(e);
			}
		});
		
		if(lockWindowTile!=null)
		{
			try
			{
				File file = new File(JohnResourcesManager.getLockWindowTimerIni());
				if(file.exists())
				{
					FileInputStream fis=new FileInputStream(file);
					byte[] bytes=new byte[80]; 
					String bool=new String(bytes, 0, fis.read(bytes));
					lockWindowTile.setToggle(new Boolean(bool));
					fis.close();
				}
			} catch (IOException e1)
			{
				e1.printStackTrace();
			}
		}
	}
	
	private JPopupMenu getPopupMenu()
	{
		JPopupMenu popup=new JPopupMenu();
		popup.add(new AbstractAction("删除本任务")
		{
			private static final long serialVersionUID = 1L;
			@Override
			public void actionPerformed(ActionEvent e)
			{
				 int[] rows=table.getSelectedRows();
				 DefaultTableModel model=(DefaultTableModel)table.getModel();
				 for(int row : rows)
				 {
					 if(row!=0)//第一行不允许删除，否则无法添加下一行
					 {
						 model.removeRow(row);
					 }
				 }
			}
		});
		popup.add(new AbstractAction("删除未启用任务")
		{
			private static final long serialVersionUID = 1L;
			@Override
			public void actionPerformed(ActionEvent e)
			{
				 DefaultTableModel model=(DefaultTableModel)table.getModel();
				 for(int i=1;i<table.getRowCount();i++)
				 {//第一行不允许删除，否则无法添加下一行
					 if(!(Boolean)table.getValueAt(i, 1))
					 {
						 model.removeRow(i);
					 }
				 }
			}
		});
		return popup;
	}
	
	/**
	 * 获取随机7位ID
	 */
	private String getRandomStr()
	{
		Random random=new Random();
		StringBuffer sb=new StringBuffer();
		for(int i=0;i<7;i++)
		{
			sb.append(random.nextInt(10));
		}
		return sb.toString();
	}
	
	/**
	 * 部署定时任务
	 */
	private void deploy()
	{
		for(int i=0;i<table.getRowCount();i++)
		{
			if((Boolean)table.getValueAt(i, 1)==true&&validate(i))
			{//已经启用并且合法
				int type=((DefaultComboBoxModel<String>)taskType.getModel()).getIndexOf((String)table.getValueAt(i, 2));
				JohnTimer timer=new JohnTimer((String)table.getValueAt(i, 0));
				timer.orderTask(type,(String)table.getValueAt(i, 3), (Integer)table.getValueAt(i, 4), (Integer)table.getValueAt(i, 5),(Integer)table.getValueAt(i, 6),(String)table.getValueAt(i, 7));
				list.add(timer);
			}
		}
	}

	/**
	 * 判断配置项内容合法性
	 * @param row 行号
	 * @return 如果有非法的配置项返回false
	 */
	private boolean validate(int row)
	{
		String type=((String)table.getValueAt(row, 2)).trim();
		String commit=((String)table.getValueAt(row, 7)).trim();
		if("打开网址".equals(type)&&!(commit.startsWith("http://")||commit.startsWith("https://")))
		{
			return false;
		}
		else if("消息提醒".equals(type)&&("".equals(commit)||"请输入消息".equals(commit)))
		{
			return false;
		}
		else if(("执行文件".equals(type)&&("".equals(commit)))||("删除文件".equals(type)&&("".equals(commit))))
		{
			return false;
		}
		else{
			return true;
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if(table.getCellEditor()!=null&&!table.getCellEditor().stopCellEditing())
		{//判断是否在频次、小时数和分钟数中输入了非数字
			JOptionPane.showMessageDialog(this, "输入的信息有不合法之处,请确认");
			return;
		}
	  
		for(int i=0;i<table.getRowCount();i++)
		{//如果配置项有非法处提醒处理
			if((Boolean)table.getValueAt(i, 1)==true&&!validate(i))
			{
				int option=JOptionPane.showOptionDialog(this, "您有启用的任务但尚未作出有效的配置,您的选择?", "重要提示", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[]{"配置","删除","跳过"}, "配置");
				if(option==0)
				{
					table.setRowSelectionInterval(i, i);
					table.setColumnSelectionInterval(7, 7);
					return;
				}
				else if(option==1)
				{
					((DefaultTableModel)table.getModel()).removeRow(i);
				}
			}
		}
		
		for(JohnTimer timer:list)//清空列表里的任务
		{
			timer.destroy();
		}
		list.clear();
		JOptionPane.showMessageDialog(pane, "已清除过去的定时任务！", "温馨提示", JOptionPane.INFORMATION_MESSAGE);
		
		deploy();
		
		try//保存配置到schedule.xml
		{
			helper.beginWrite(new FileOutputStream(JohnResourcesManager.getScheduleCofigPath()),"UTF-8");
		} catch (FileNotFoundException e1)
		{
			e1.printStackTrace();
		}
		helper.clearTimers();
		for(int i=0;i<table.getRowCount();i++)
		{
			if(validate(i))
			{
				helper.setElementValues((String)table.getValueAt(i, 0), (Boolean)table.getValueAt(i, 1), (String)table.getValueAt(i, 2), (String)table.getValueAt(i, 3), (Integer)table.getValueAt(i, 4),(Integer)table.getValueAt(i, 5),(Integer)table.getValueAt(i, 6),(String)table.getValueAt(i, 7));
			}
		}
		helper.endWrite();
		JOptionPane.showMessageDialog(pane, "定时任务设置成功！", "温馨提示", JOptionPane.INFORMATION_MESSAGE);
		//最小化到托盘
		pane.getTopLevelAncestor().setVisible(false);
		new JohnNotifyIconFactory().showNotifyIcon((JFrame)(pane.getTopLevelAncestor()), JohnResourcesManager.getImage(JohnResourcesManager.getTrayIconPng()), "johnSoft");
	}
	
}
