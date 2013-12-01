package uninstall;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.UIManager;



public class JohnInstallUtil
{
	public static void deleteProgramFiles(String programfilesdir)
	{
		if(programfilesdir==null||programfilesdir.trim().isEmpty()) return;
		File file = new File(programfilesdir);
		if (file.exists())
		{
			for (File f : file.listFiles())
			{
				f.delete();
			}
			file.delete();
		}
	}
	
	public static void deleteMenuLnk(String folderName)
	{
		if(folderName==null||folderName.trim().isEmpty()) return;
		File file = new File(JohnRegistryUtil.getCommonProStartMenuPath()+ "\\" + folderName);
		if (file.exists())
		{
			for (File f : file.listFiles())
			{
				f.delete();
			}
			file.delete();
		}
	}

	public static void deleteDeskLnk(String lnkName)
	{
		if (lnkName != null && !lnkName.trim().isEmpty())
		{
			File file = new File(JohnRegistryUtil.getCommonDesktopPath()+ "\\" + lnkName);
			if (file.exists())
			{
				file.delete();
			}
		}
	}

	/**
	 * 复制jar文件到指定位置
	 * 
	 * @param jarToPath
	 *            jar要被复制到的指定目录路径,不要以分隔符"/"或"\\"结尾
	 * @param jarName
	 * 			  jar的名字,包括扩展名
	 * @param ips
	 *            作为源的jar流
	 */
	public static boolean copyJarToPath(String jarToPath, String jarName, InputStream ips)
	{
		try
		{
			// 判断目录是否存在，不存在就创建
			File dic = new File(jarToPath);
			if (!dic.exists())
			{
				dic.mkdirs();
			}
			// 边读边写，一个条目一个条目
			JarInputStream in = new JarInputStream(ips);
			JarOutputStream out = new JarOutputStream(new FileOutputStream(
					jarToPath+"/"+jarName), in.getManifest());
			byte[] buf = new byte[1024];
			while (true)
			{
				JarEntry entry = in.getNextJarEntry();
				if (entry == null)
				{
					break;
				}
				out.putNextEntry(entry);
				int size = 0;
				while ((size = in.read(buf, 0, buf.length)) != -1)
				{
					out.write(buf, 0, size);
				}
				in.closeEntry();
				out.closeEntry();
			}
			in.close();
			out.flush();
			out.finish();
			out.close();
			return true;

		} catch (Exception e1)
		{
			return showExceptionMessage(e1);
		}
	}
	
	/**
	 * 复制指定文件到指定位置，不适用于zip,jar,rar等压缩文件的复制
	 * 
	 * @param fileToPath
	 *            文件要被复制到的指定目录路径,不要以分隔符"/"或"\\"结尾
	* @param fileName
	 *            文件的名字,包括扩展名
	 * @param sourceFile
	 * 			  作为源的文件流
	 */
	public static boolean copyFileToPath(String fileToPath, String fileName, InputStream is)
	{
		try
		{
			File dic = new File(fileToPath);
			if (!dic.exists())
			{
				dic.mkdirs();
			}
			OutputStream os = new BufferedOutputStream(new FileOutputStream(fileToPath+"/"+fileName));
			byte[] bytes = new byte[1024];
			int len = 0;
			while ((len = is.read(bytes)) != -1)
			{
				os.write(bytes, 0, len);
			}
			os.flush();
			is.close();
			os.close();
			return true;
		} catch (Exception e)
		{
			return showExceptionMessage(e);
		}
	}

	/**
	 * 复制指定文件到指定位置，不适用于zip,jar,rar等压缩文件的复制
	 * 
	 * @param fileToPath
	 *            文件要被复制到的指定目录路径,不要以分隔符"/"或"\\"结尾
	* @param fileName
	 *            文件的名字,包括扩展名
	 * @param sourceFile
	 * 			  作为源的文件的完整路径,包括带扩展名的文件名
	 */
	public static boolean copyFileToPath(String fileToPath, String fileName, String sourceFile)
	{
		try
		{
			File dic = new File(fileToPath);
			if (!dic.exists())
			{
				dic.mkdirs();
			}
			InputStream is = new BufferedInputStream(new FileInputStream(sourceFile));
			OutputStream os = new BufferedOutputStream(new FileOutputStream(fileToPath+"/"+fileName));
			byte[] bytes = new byte[1024];
			int len = 0;
			while ((len = is.read(bytes)) != -1)
			{
				os.write(bytes, 0, len);
			}
			os.flush();
			is.close();
			os.close();
			return true;
		} catch (Exception e)
		{
			return showExceptionMessage(e);
		}
	}
	
	/**
	 * 调用vbs命令创建快捷方式lnk
	 * @param sourcePath 要创建快捷方式的可执行文件
	 * @param targetPath lnk文件名称
	 * @param iconPath 图标名称
	 * @param toolTip 提示信息
	 * @param hotKey 快捷键,如"Alt + J"
	 */
	public static boolean createLnkAsVbs(String sourcePath,String targetPath,String iconPath,String toolTip,String hotKey)
	{
		StringBuilder sb=new StringBuilder();
		sb.append("set WshShell = WScript.CreateObject(\"WScript.Shell\")\n");
		sb.append("set Shortcut = WshShell.CreateShortcut(\"").append(targetPath).append("\")\n");
		sb.append("Shortcut.TargetPath = \"").append(sourcePath).append("\"\n");
		sb.append("Shortcut.IconLocation = \"").append(iconPath).append("\"\n");
		sb.append("Shortcut.Description = \"").append(toolTip).append("\"\n");
		sb.append("Shortcut.HotKey = \"").append(hotKey).append("\"\n");
		sb.append("Shortcut.Save");
		String result=sb.toString();
		
		try
		{
			File file=new File(System.getProperty("java.io.tmpdir")+"\\temp.vbs");
			if(file.exists())
			{
				file.delete();
				file.createNewFile();
			}
			FileOutputStream fos=new FileOutputStream(file);
			fos.write(result.getBytes());
			fos.close();
			
			Runtime.getRuntime().exec("cmd.exe /c start %temp%\\temp.vbs");
			
			return true;
		} catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 用于非控制台程序，本地外观窗口显示异常信息，并返回false
	 * @param e 异常
	 * @return
	 */
	public static boolean showExceptionMessage(Exception e)
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception el)
		{
			JOptionPane.showMessageDialog(new JDialog(), e.getMessage());
			el.printStackTrace();
		}
		JOptionPane.showMessageDialog(new JDialog(), e.getMessage());
		e.printStackTrace();
		return false;
	}
	
	public static MainPane getMainPane(JButton button, String software, ImageIcon titleIcon)
	{
		return new MainPane(button,software,titleIcon);
	}
	
	public static SetupPathChooser getSetupPathChooser(String software)
	{
		return new SetupPathChooser(software);
	}
	
	public static StatePane getStatePane(JComponent jcomp, String statement, ImageIcon bgImg, ImageIcon titleIcon)
	{
		return new StatePane(jcomp,statement,bgImg,titleIcon);
	}
	
	/**
	 * 用户设置安装路径、开始菜单、桌面快捷、快速启动的面板
	 * @author john
	 */
	public static class MainPane extends JDialog
	{
		private static final long serialVersionUID = 1L;
		
		private SetupPathChooser chooser;//安装路径选择器
		private JPanel bottomBar;
		private JLabel forstart;//开始菜单
		private JLabel fordesk;//桌面快捷
		private JCheckBox start;//开始菜单
		private JCheckBox desk;//桌面快捷
		
		public MainPane(JButton button,String software,ImageIcon titleIcon)
		{
			bottomBar=new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
			forstart=new JLabel("是否添加到开始菜单:");
			fordesk=new JLabel("是否创建桌面快捷方式:");
			chooser=new SetupPathChooser(software);
			start=new JCheckBox();
			desk=new JCheckBox();
			
			this.add(chooser,BorderLayout.CENTER);
			bottomBar.add(forstart);
			bottomBar.add(start);
			bottomBar.add(fordesk);
			bottomBar.add(desk);
			bottomBar.add(button);
			this.add(bottomBar,BorderLayout.SOUTH);
			
			start.setSelected(true);
			this.setIconImage(titleIcon.getImage());
			this.setTitle("安装设置面板");
			this.setResizable(false);
			this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			this.setVisible(false);
		}

		public SetupPathChooser getChooser()
		{
			return chooser;
		}

		public JCheckBox getStart()
		{
			return start;
		}

		public JCheckBox getDesk()
		{
			return desk;
		}
	};
	
	/**
	 * 版权声明、安装进度的面板
	 * @author john
	 */
	public static class StatePane extends JDialog
	{
		private static final long serialVersionUID = 1L;
		private JPanel jpane;
		private JLabel jlabel;//放置图片
		private JEditorPane editor;//放置版权声明文字 但是未实现标题文章格式
		private JPanel bottom;
		/**jcomp可以是"下一步"的按钮或进度条*/
		public StatePane(JComponent jcomp, String statement, ImageIcon bgImg, ImageIcon titleIcon)
		{
			jpane=new JPanel(new BorderLayout());
			editor=new JEditorPane();
			jlabel=new JLabel();
			bottom=new JPanel(new FlowLayout(FlowLayout.RIGHT));
			
			editor.setText(statement);
			editor.setEditable(false);
			jlabel.setIcon(bgImg);
			bottom.setBackground(Color.WHITE);
			
			jpane.add(editor,BorderLayout.NORTH);
			jpane.add(jlabel,BorderLayout.CENTER);
			bottom.add(jcomp);
			this.add(jpane,BorderLayout.CENTER);
			this.add(bottom,BorderLayout.SOUTH);
			
			this.setIconImage(titleIcon.getImage());
			this.setTitle("安装声明面板");
			//居中显示
			int w=Toolkit.getDefaultToolkit().getScreenSize().width;
			int h=Toolkit.getDefaultToolkit().getScreenSize().height;
			this.setBounds((w-570)/2, (h-322)/2, 570, 322);
			this.setResizable(false);
			this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			this.setVisible(false);
		}
	}
	
	/**
	 * 安装路径选择器，通过分解FileChooser实现
	 * @author john
	 */
	public static class SetupPathChooser extends JFileChooser
	{
		private static final long serialVersionUID = 1L;
		
		private JToolBar leftBar;
		private JToolBar topBar;
		private JPanel mainPane;
		private JPanel bottomPane;
		private JPanel twoLabel;
		private JPanel twoField;
		private JLabel filePathLab;
		private JLabel fileTypeLab;
		private JTextField filePathText;
		private JComboBox fileTypeCombox;
	
		public SetupPathChooser(String software)
		{
			super();
			setDialogType(JFileChooser.CUSTOM_DIALOG);
			setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);//只许选目录
			setMultiSelectionEnabled(false);//不许多选
			setCurrentDirectory(new File(JohnRegistryUtil.getProgramFilesPath()));//当前位置
			setControlButtonsAreShown(false);//去掉确认和取消按钮
			getComps();//分解FileChooser
			initComposite(software);
		}
		
		private void getComps()
		{
			leftBar=(JToolBar)this.getComponent(0);
			topBar=(JToolBar)this.getComponent(1);
			mainPane=(JPanel)this.getComponent(2);
			bottomPane=(JPanel)mainPane.getComponent(2);
			twoLabel=(JPanel)bottomPane.getComponent(0);
			twoField=(JPanel)bottomPane.getComponent(2);
			filePathLab=(JLabel)twoLabel.getComponent(1);
			fileTypeLab=(JLabel)twoLabel.getComponent(3);
			filePathText=(JTextField)twoField.getComponent(1);
			fileTypeCombox=(JComboBox)twoField.getComponent(3);
		}
		
		private void initComposite(String software)
		{
			filePathLab.setText("安装路径:");
			leftBar.setVisible(false);
			fileTypeLab.setVisible(false);
			fileTypeCombox.setVisible(false);
			topBar.getComponent(7).setVisible(false);
			bottomPane.getComponent(3).setVisible(false);
			filePathText.setText(JohnRegistryUtil.getProgramFilesPath()+"\\"+software);
		}
		
		/**
		 * 获得安装路径
		 * @return
		 */
		public String getPath()
		{
			return filePathText.getText();
		}
	}
}
