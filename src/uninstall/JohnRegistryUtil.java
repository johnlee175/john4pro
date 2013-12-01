package uninstall;

import java.io.InputStream;

/**
 * 开机自启动设置类
 * 
 * @author john
 */
public class JohnRegistryUtil
{
	public static final String PROGRAM_FILES_KEYPATH="HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion";
	public static final String AUTORUN_KEYPATH="HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run";
	public static final String APPPATHS_KEYPATH="HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\App Paths";
	public static final String COMMONUSER_KEYPATH="HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Shell Folders";
	public static final String CURRENTUSER_KEYPATH="HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Shell Folders";
	public static final String COMMON_PRO_START_MENU_NAME="Common Programs";
	public static final String COMMON_DESKTOP_NAME="Common Desktop";
	public static final String COMMON_DOCUMENT_NAME="Common Documents";
	public static final String CURRENT_PRO_START_MENU_NAME="Programs";
	public static final String CURRENT_DESKTOP_NAME="Desktop";
	public static final String CURRENT_DOCUMENT_NAME="Personal";
	public static final String PROGRAM_FILES_NAME="ProgramFilesDir";
	public static final String COMMON_FILES_NAME="CommonFilesDir";
	public static final String REG_SZ="REG_SZ";
	
	/**
	 * 向注册表开机自启动位置添加指定可执行文件路径
	 * 
	 * @param softName
	 *            可执行文件名称或软件名称
	 * @param softPath
	 *            软件安装或启动路径
	 */
	public static void regAutoStart(String softName, String softPath)
	{
		if(checkAutoStart(softName)!=null)
			cancelAutoStart(softName);
		add(AUTORUN_KEYPATH, softName, REG_SZ, softPath);
	}

	/**
	 * 删除注册表开机自启动位置指定可执行文件路径
	 * 
	 * @param softName
	 *            可执行文件名称或软件名称，为注册表键
	 */
	public static void cancelAutoStart(String softName)
	{
		delete(AUTORUN_KEYPATH, softName);
	}

	/**
	 * 检测是否已经配置开机自启动
	 * 
	 * @param softName
	 *            可执行文件名称或软件名称，为注册表键
	 * @return 如果返回null说明没有配置，否则返回可执行文件路径
	 */
	public static String checkAutoStart(String softName)
	{
		return querySplitType(AUTORUN_KEYPATH, softName, REG_SZ);
	}

	public static String getAppPaths(String appexename)
	{
		return querySplitType(APPPATHS_KEYPATH + "\\" + appexename, null, REG_SZ);
	}
	
	public static String getProgramFilesPath()
	{
		return querySplitType(PROGRAM_FILES_KEYPATH, PROGRAM_FILES_NAME, REG_SZ);
	}
	
	public static String getCommonFilesPath()
	{
		return querySplitType(PROGRAM_FILES_KEYPATH, COMMON_FILES_NAME, REG_SZ);
	}
	
	public static String getCommonDesktopPath()
	{
		return querySplitType(COMMONUSER_KEYPATH, COMMON_DESKTOP_NAME, REG_SZ);
	}
	
	public static String getCurrentDesktopPath()
	{
		return querySplitType(CURRENTUSER_KEYPATH, CURRENT_DESKTOP_NAME, REG_SZ);
	}
	
	public static String getCommonProStartMenuPath()
	{
		return querySplitType(COMMONUSER_KEYPATH, COMMON_PRO_START_MENU_NAME, REG_SZ);
	}
	
	public static String getCurrentProStartMenuPath()
	{
		return querySplitType(CURRENTUSER_KEYPATH, CURRENT_PRO_START_MENU_NAME, REG_SZ);
	}
	
	public static String getCommonDocumentPath()
	{
		return querySplitType(COMMONUSER_KEYPATH, COMMON_DOCUMENT_NAME, REG_SZ);
	}
	
	public static String getCurrentDocumentPath()
	{
		return querySplitType(CURRENTUSER_KEYPATH, CURRENT_DOCUMENT_NAME, REG_SZ);
	}
	
	public static String querySplitType(String keyPath, String name,String... types)
	{
		String temp=query(keyPath, name);
		for(int i=0;i<types.length;i++)
		{
			if (temp.indexOf(types[i]) > 0)
			{
				return temp.split(types[i])[1].trim();
			} 
		}
		return null;
	}

	public static String query(String keyPath, String name)
	{
		String result = null;
		Runtime runtime = Runtime.getRuntime();
		if (keyPath == null || keyPath.isEmpty())
		{
			throw new IllegalArgumentException("key path invalid!");
		}
		String command;
		if (name == null || name.trim().isEmpty())
		{
			command = "reg query \"" + keyPath + "\" /ve";
		} else
		{
			command = "reg query \"" + keyPath + "\" /v \"" + name + "\"";
		}
		try
		{
			Process process = runtime.exec(command);
			InputStream is = process.getInputStream();
			process.waitFor();
			byte[] bytes = new byte[is.available()];
			int len = 0;
			StringBuffer sb = new StringBuffer();
			while ((len = is.read(bytes)) > 0)
			{
				sb.append(new String(bytes, 0, len, System.getProperty("sun.jnu.encoding")));
			}
			result=sb.toString();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return result;
	}

	public static void add(String keyPath, String name, String type, String data)
	{
		Runtime runtime = Runtime.getRuntime();
		String command = "reg add \"" + keyPath + "\" /v \"" + name + "\" /t "
				+ type + " /d \"" + data + "\"";
		try
		{
			runtime.exec(command);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static void delete(String keyPath, String name)
	{
		Runtime runtime = Runtime.getRuntime();
		String command = "reg delete \"" + keyPath + "\" /v \"" + name
				+ "\" /f ";
		try
		{
			runtime.exec(command);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}
