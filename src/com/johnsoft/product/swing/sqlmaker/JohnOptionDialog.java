package com.johnsoft.product.swing.sqlmaker;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

public class JohnOptionDialog extends JDialog implements ActionListener
{
	private static final long serialVersionUID = 1L;
	
	private JTextField prefix;
	private JTextField suffix;
	private JTextField fixColStr;
	private JTable ownerTable;
	private JTable jTable;
	private List<JohnRexOptionColModel> rms;
	private JohnRexOptionColModel rm;
	
  public JohnOptionDialog(JTable ownerTable,List<JohnRexOptionColModel> rms,int sx,int sy,final int colIndex)
	{
  	this.rms=rms;
  	this.rm=rms.get(colIndex);
  	this.ownerTable=ownerTable;
		
  	JPanel jPanel=new JPanel();
	
		JLabel tip0=new JLabel("前缀:");
		prefix=new JTextField(rm.getPrefix());
		tip0.setLabelFor(prefix);
		prefix.setPreferredSize(new Dimension(50,20));
		
		JLabel tip1=new JLabel("后缀:");
		suffix=new JTextField(rm.getSuffix());
		tip1.setLabelFor(suffix);
		suffix.setPreferredSize(new Dimension(50,20));
		
		JLabel tip2=new JLabel("固定此列值为:");
		fixColStr=new JTextField(rm.getFixColStr());
		tip2.setLabelFor(fixColStr);
		fixColStr.setPreferredSize(new Dimension(100,20));
		
		Object[][] data=new Object[][]{
				{1,"对于字符串类型,您希望的汉字个数",rm.getChlen(),rm.isChfix()},
				{2,"对于字符串类型,您希望的英字个数",rm.getEnlen(),rm.isEnfix()},
				{3,"对于字符串类型,您希望的数字个数",rm.getNumlen(),rm.isNumfix()},
				{4,"对于数值类型,您希望的总长度",rm.getIntlen(),rm.isIntfix()},
				{5,"对于数值类型,您希望的小数长度",rm.getFloatlen(),rm.isFloatfix()}
		};
		String[] columnNames=new String[]{"序号","描述","长度","是否固定个数"};
		DefaultTableModel dm=new DefaultTableModel(data, columnNames){
			private static final long serialVersionUID = 1L;
			@Override
			public Class<?> getColumnClass(int columnIndex)
			{
				return this.getValueAt(1, columnIndex).getClass();
			}
			
			@Override
			public Object getValueAt(int row, int column)
			{
				Object obj=super.getValueAt(row, column);
				if(obj==null&&column==2)
				{
					return 0;
				}
				return obj;
			}
		};
	
	  jTable=new JTable(dm){
			private static final long serialVersionUID = 1L;
			@Override
			public boolean isCellEditable(int row, int column)
			{
				return (column==0||column==1)?false:true;
			}
		};
		jTable.setShowGrid(false);
		jTable.setIntercellSpacing(new Dimension(0, 0));
		jTable.setFocusable(false);
		jTable.getTableHeader().setReorderingAllowed(false);
		jTable.getColumn("描述").setPreferredWidth(220);
		jTable.getColumn("序号").setPreferredWidth(30);
		jTable.getColumn("长度").setPreferredWidth(30);
		JScrollPane jsp=new JScrollPane(jTable);
		jsp.setPreferredSize(new Dimension(400, 105));
		
		JButton ok=new JButton("确定");
		JButton cancel=new JButton("取消");
		
		JLabel jl=new JLabel("您现在选择的是第"+(colIndex+1)+"列");
		jl.setPreferredSize(new Dimension(400, 20));
		jl.setHorizontalAlignment(JLabel.RIGHT);
		
		jPanel.add(tip0);
		jPanel.add(prefix);
		jPanel.add(tip1);
		jPanel.add(suffix);
		jPanel.add(tip2);
		jPanel.add(fixColStr);
		jPanel.add(jsp);
		jPanel.add(ok);
		jPanel.add(cancel);
		jPanel.add(jl);
		
		this.add(jPanel);
		this.setResizable(false);
		this.setTitle("仅适用于随机数据生成");
		this.setAlwaysOnTop(true);
		this.setBounds(sx, sy, 420, 235);
		this.setVisible(true);
		this.addComponentListener(new ComponentAdapter()
		{
			@Override
			public void componentHidden(ComponentEvent e)
			{
				TableColumn tc=JohnOptionDialog.this.ownerTable.getColumnModel().getColumn(colIndex);
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
		    JohnOptionDialog.this.ownerTable.repaint();
			}
			
		});
		
		ok.addActionListener(this);
		cancel.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				JohnOptionDialog.this.setVisible(false);
			}
		});
	}
  
		@Override
		public void actionPerformed(ActionEvent e)
		{
			rm.setPrefix(prefix.getText());
			rm.setSuffix(suffix.getText());
			rm.setFixColStr(fixColStr.getText());
			int ch=(Integer)jTable.getValueAt(0, 2);
			rm.setChlen(ch);
			rm.setChfix((Boolean)jTable.getValueAt(0, 3));
			int en=(Integer)jTable.getValueAt(1, 2);
			rm.setEnlen(en);
			rm.setEnfix((Boolean)jTable.getValueAt(1, 3));
			int num=(Integer)jTable.getValueAt(2, 2);
			rm.setNumlen(num);
			rm.setNumfix((Boolean)jTable.getValueAt(2, 3));
			int in=(Integer)jTable.getValueAt(3, 2);
			rm.setIntlen(in);
			rm.setIntfix((Boolean)jTable.getValueAt(3, 3));
			int fl=(Integer)jTable.getValueAt(4, 2);
			rm.setFloatlen(fl);
			rm.setFloatfix((Boolean)jTable.getValueAt(4, 3));
			rms.set(rm.getColIndex(), rm);
			this.setVisible(false);
		}
}
