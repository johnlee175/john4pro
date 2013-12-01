package setup;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import setup.JohnInstallUtil.MainPane;
import setup.JohnInstallUtil.StatePane;

/**
 * 用java实现的简易安装界面
 * 实质是将已建好的软件，批处理文件，配置文件，快捷方式 和所需图片放置到其他路径中
 * 并没有用jni实现快捷方式，对桌面文件，开始菜单文件，启动栏文件的路径判断也比较初步
 * 由于以上原因，java风格的FileChooser和windows风格下的组件构成又不同，所以仅适用于winXP, win7
 * @author john
 */
public class WinSetup 
{
	public static final ImageIcon IMAGE=new ImageIcon(WinSetup.class.getResource("bg.jpg"));
	public static final ImageIcon ICON=new ImageIcon(WinSetup.class.getResource("tray_icon.png"));
	public static final String SOFTWARE_NAME="johnSoft";
	
	public static void main(String[] args) throws Exception
	{
		if(!System.getProperty("os.name").toLowerCase().contains("windows")
				/*||!"x86".equals(System.getProperty("os.arch"))*/)
		{
			JOptionPane.showMessageDialog(null, "未知的环境,无法继续安装!");
			return;
		}
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		JButton next=new JButton("下一步");
		JButton exe=new JButton("确认");
	  //第一步，声明
		String state="声明与注意\n这是一款简易工具集,默认开机自启动.由于本人才疏学浅,安装程序有点简陋,并没来得及做成exe文件,请谅解.如果使用中发现问题或建议,请及时联系,(联系人)李哲浩敬上";
		final StatePane statement=JohnInstallUtil.getStatePane(next, state, IMAGE, ICON);
	  //第二步，安装设置
		final MainPane setup=JohnInstallUtil.getMainPane(exe, SOFTWARE_NAME, ICON);
		
		next.addActionListener(new ActionListener()
		{//声明面板和安装面板并不是在同一个窗口中，而是用两个窗口平缓过渡
			public void actionPerformed(ActionEvent e)
			{
				setup.setBounds(statement.getBounds());//获取声明面板位置和大小
				statement.dispose();
				setup.setVisible(true);
			}
		});
		
		exe.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{ //检查安装路径
				String path=setup.getChooser().getPath();
				if(path==null||path.trim().isEmpty()) return;
				if(path.endsWith("\\"))
				{
					path=new StringBuffer(path).deleteCharAt(path.length()-1).toString();
				}
				
				boolean isSuccess=JohnInstallUtil.copyJarToPath(path, SOFTWARE_NAME+".jar", getClass().getResourceAsStream(SOFTWARE_NAME+".jar"));
				if(!isSuccess)
				{//判断主jar是否安装到位
					JOptionPane.showMessageDialog(setup, "安装失败,请联系工具提供者!");
					setup.dispose();
					return;
				}
				
				try
				{//如果jar包很大,可能需要等待其复制到位,因为后面需要该jar
					Thread.sleep(1000);
				} catch (InterruptedException e1)
				{
					e1.printStackTrace();
				}
				
				String icoName=SOFTWARE_NAME+".ico";
				//复制图标到安装路径
				JohnInstallUtil.copyFileToPath(path, icoName, getClass().getResourceAsStream(SOFTWARE_NAME+".ico"));
				//创建快捷方式
				String icoPath=path+"\\"+icoName;
				String lnkName=SOFTWARE_NAME+".lnk";
				String sourceLnk=path+"\\"+lnkName;
				boolean lnkCreated=JohnInstallUtil.createLnkAsVbs(path+"\\"+SOFTWARE_NAME+".jar", sourceLnk, icoPath, SOFTWARE_NAME, "Alt+J");
				
				try
				{//运行vb脚本创建出快捷方式需要时间
					Thread.sleep(1000);
				} catch (InterruptedException e1)
				{
					e1.printStackTrace();
				}
				
				if(lnkCreated)
				{//创建快捷方式成功
					if(setup.getStart().isSelected())
					{//如果有添加到开始菜单需求
						boolean isStarted=JohnInstallUtil.copyFileToPath(JohnRegistryUtil.getCommonProStartMenuPath()+"\\"+SOFTWARE_NAME, lnkName, sourceLnk);
						if(!isStarted)
						{
								JOptionPane.showMessageDialog(setup, "\"添加到开始菜单\"任务失败!");
						}
						else
						{
							if(isSuccess)
							{//将卸载工具也如法炮制
								try
								{
									boolean uninstallerAdded=JohnInstallUtil.copyJarToPath(path, "uninstaller.jar", getClass().getResourceAsStream("uninstaller.jar"));
									try
									{
										Thread.sleep(500);
									} catch (InterruptedException e2)
									{
										e2.printStackTrace();
									}
									if(uninstallerAdded)
									{
										String uninstallIco=icoPath;
										String uninstallLnk=path+"\\uninstall.lnk";
										boolean uninstallLnkCreated=JohnInstallUtil.createLnkAsVbs(path+"\\uninstaller.jar", uninstallLnk, uninstallIco, "uninstall", "Alt+U");
										try
										{
											Thread.sleep(1000);
										} catch (InterruptedException e2)
										{
											e2.printStackTrace();
										}
										if(uninstallLnkCreated)
										{
											JohnInstallUtil.copyFileToPath(JohnRegistryUtil.getCommonProStartMenuPath()+"\\"+SOFTWARE_NAME, "uninstall.lnk", uninstallLnk);
										}
									}
								} catch (Exception ex)
								{ /*do nothing*/ }
							}
						}
					}
					if(setup.getDesk().isSelected())
					{//有桌面快捷方式需求
						boolean isDesked=JohnInstallUtil.copyFileToPath(JohnRegistryUtil.getCommonDesktopPath(), lnkName, sourceLnk);
						if(!isDesked)
						{
								JOptionPane.showMessageDialog(setup, "\"创建桌面快捷方式\"任务失败!");
						}
					}
				}
				else
				{
					JOptionPane.showMessageDialog(setup, "生成快捷方式出错!");
				}
				
				if(isSuccess)
				{
					JOptionPane.showMessageDialog(setup, "已安装完毕!");
				}
				setup.dispose();
			}
		});
		statement.setVisible(true);
	}
}
