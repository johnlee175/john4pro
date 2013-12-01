package uninstall;

import java.io.File;
import java.io.UnsupportedEncodingException;

/**
 * 路径查找类
 * @author john
 */
public class JohnPathUtil
{
	/**
	 * 用于获取可执行jar启动时自身所在的绝对路径
	 */
	public static String getSelfJarLaunchAbsolutePath() 
	{
	  try
		{
	   String path=JohnPathUtil.class.getProtectionDomain().getCodeSource()  
	        .getLocation().getFile(); 
		 path = java.net.URLDecoder.decode(path, "UTF-8");//转换处理中文及空格    
		 File file = new File(path);  
     if(file!=null)
     {
    	return file.getAbsolutePath();
     }
		} catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
	  return null;
	}
	
}
