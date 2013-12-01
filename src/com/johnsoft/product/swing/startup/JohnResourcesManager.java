package com.johnsoft.product.swing.startup;

import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;


public class JohnResourcesManager
{
	 public static String basePath=System.getProperty("user.dir")+"/res/";
   
   public static String getFileTreeRootPath()
   {
  	 return basePath+"config/file_tree_root.ini";
   }
   
   public static String getSqlMakerConfigPath()
   {
  	 return basePath+"config/global.properties";
   }
   
   public static String getWordLibPath()
   {
  	 return basePath+"data/wordlib.txt";
   }
   
   public static String getScheduleCofigPath()
   {
  	 return basePath+"config/schedule.xml";
   }
   
   public static String getLockWindowTimerIni()
   {
  	 return basePath+"config/lock_window_timer.ini";
   }

   public static ImageIcon getImageIcon(String path)
   {
  	 return new ImageIcon(path);
   }
   
   public static Image getImage(String path)
   {
  	 try
		{
			return ImageIO.read(new File(path));
		} catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
   }
   
   public static String getRightPngPath()
   {
  	 return basePath+"images/right.png";
   }
   
   public static String getMoveNodePngPath()
   {
  	 return basePath+"images/moveNode.png";
   }
   
   public static String getPastePng()
   {
  	 return basePath+"images/paste.png";
   }
   
   public static String getSafariPng()
   {
  	 return basePath+"images/safari.png";
   }
   
   public static String getRemoveNodePng()
   {
  	 return basePath+"images/removeNode.png";
   }
   
   public static String getExpandNodePng()
   {
  	 return basePath+"images/expandNode.png";
   }
   
   public static String getBooksPng()
   {
  	 return basePath+"images/books.png";
   }
   
   public static String getWordsPng()
   {
  	 return basePath+"images/words.png";
   }
   
   public static String getExePng()
   {
  	 return basePath+"images/exe.png";
   }
   
   public static String getTrayIconPng()
   {
  	 return basePath+"images/tray_icon.png";
   }
   
   public static String getLockPng()
   {
  	 return basePath+"images/lock.png";
   }
   
   public static String getUnLockPng()
   {
  	 return basePath+"images/unlock.png";
   }
   
   public static String getSkinJpg(int i)
   {
  	 return basePath+"images/skin"+i+".jpg";
   }
   
   public static String getPreSkinJpg(int i)
   {
  	 return basePath+"images/preskin"+i+".jpg";
   }
   
   public static String getTreePng()
   {
  	 return basePath+"images/tree.png";
   }
   
   public static String getSavePng()
   {
  	 return basePath+"images/save.png";
   }
   
   public static String getRefreshPng()
   {
  	 return basePath+"images/refresh.png";
   }
   
}
