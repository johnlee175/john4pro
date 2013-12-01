package com.johnsoft.product.swing.timer;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JOptionPane;

import com.johnsoft.library.swing.component.JohnStartTip;
import com.johnsoft.library.swing.component.JohnTooltip;
import com.johnsoft.library.util.common.JohnRegistryUtil;

/**
 * 定时任务类
 * 
 * @author john
 */
public class JohnTimerTask
{
	/**
	 * 打开浏览器任务
	 */
	public static TimerTask getOpenBrowserTask(final String url)
	{
		return new TimerTask()
		{
			@Override
			public void run()
			{
				if (new Random().nextBoolean())
				{
					new JohnTooltip("正在打开网页！\n请稍后...");// 气泡提示
				} else
				{
					new JohnStartTip("正在打开网页！\n请稍后...");// 开始文字提示
				}
				int x = JOptionPane.showOptionDialog(null, "该报工了！是否立即打开网页？", "温馨提示",
						JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
						null, new String[] { "是", "过10分钟再提醒", "今天就算了" }, "是");
				if (x == 0)
				{
					openBrowser(url);
				} else if (x == 1)
				{
					new Timer().schedule(JohnTimerTask.getOpenBrowserTask(url),
							10 * 60 * 1000);
				}
			}
		};
	}

	/**
	 * 消息提醒任务
	 */
	public static TimerTask getMessageTask(final String message)
	{
		return new TimerTask()
		{
			@Override
			public void run()
			{
				JOptionPane.showMessageDialog(null, message);
			}
		};
	}

	/**
	 * 简单本地方法任务
	 */
	public static TimerTask getRuntimeTask(final String command)
	{
		return new TimerTask()
		{
			@Override
			public void run()
			{
				try
				{
					Runtime.getRuntime().exec(command);
				} catch (IOException e)
				{
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, e.getMessage());
				}
			}
		};
	}
	

	/**
	 * 执行或打开 、一个或多个 、文件或文件夹;cmd命令本地方法实现
	 */
	public static TimerTask getExeFileTask(final String path)
	{
		return new TimerTask()
		{
			@Override
			public void run()
			{
				Runtime driver=Runtime.getRuntime();
				String[] fileNames=path.split(";");
				for(String name:fileNames)
				{
					File file=new File(name);
					if(file.exists())
					{
						try
						{
							driver.exec("cmd.exe /c start \"\" \""+name+"\"");
						} catch (IOException e)
						{
							e.printStackTrace();
						}
					}
				}
			}
		};
	}
	
	/**
	 * 删除一个或多个文件夹 或特定类型或名称的文件;cmd命令本地方法实现
	 */
	public static TimerTask getDelFileTask(final String pathAndType)
	{
		return new TimerTask()
		{
			@Override
			public void run()
			{
				Runtime driver=Runtime.getRuntime();
				String[] strs=pathAndType.split("@");
				if(strs.length<=0)
				{
					return;
				}
				String[] fileNames=strs[0].split(";");
				if(strs.length==3)
				{
					String[] fileTypes=strs[1].split(";");
				  boolean contain=new Boolean(strs[2]);
				  for(String name:fileNames)
					{
						File file=new File(name);
						if(file.exists())
						{
							if(file.isFile())
							{
								try
								{
									driver.exec("cmd.exe /c del \""+name+"\" /F ");
								} catch (IOException e)
								{
									e.printStackTrace();
								}
							}else{
								for(String type:fileTypes)
								{
									try
									{
										driver.exec("cmd.exe /c del \""+name+"\\*."+type+(contain==true?"\" /F /S ":" "));
									} catch (IOException e)
									{
										e.printStackTrace();
									}
								}
							}
						}
					}
				}
				else if(strs.length==1)
				{
					for(String name:fileNames)
					{
						File file=new File(name);
						if(file.exists())
						{
							try
							{
								driver.exec("cmd.exe /c del \""+name+"\" /F ");
							} catch (IOException e)
							{
								e.printStackTrace();
							}
						}
					}
				}
			}
		};
	}
	
	/**
	 * 打开默认浏览器
	 */
	protected static void openBrowser(String url)
	{
		if (Desktop.isDesktopSupported())
		{
			Desktop desktop = Desktop.getDesktop();
			if (desktop.isSupported(Desktop.Action.BROWSE))
			{
				try
				{
					desktop.browse(new URI(url));
				} catch (Exception e)
				{
					JOptionPane.showMessageDialog(null, e.getMessage());
				}
			} else
			{
				JOptionPane.showMessageDialog(null, "Browse isn't supported!");
			}

		} else
		{
			JOptionPane.showMessageDialog(null, "Desktop isn't supported!");
		}
	}
	
	//打开非默认浏览器之ie浏览器 
	protected static void openIE(String url)
	{
		Runtime runtime = Runtime.getRuntime();
		String exePath=JohnRegistryUtil.getAppPaths("IEXPLORE.EXE");
		try
		{
			runtime.exec("cmd.exe /c start \"\" \""+exePath+"\" "+url);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
}
