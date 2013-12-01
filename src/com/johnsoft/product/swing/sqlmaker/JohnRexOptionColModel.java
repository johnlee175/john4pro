package com.johnsoft.product.swing.sqlmaker;

public class JohnRexOptionColModel
{
	  private int colIndex=-1;//列号
    private String prefix="";//前缀
    private String suffix="";//后缀
    private String fixColStr="";//固定列值
    private int chlen=0;//中文字符串长度
    private boolean chfix=false;//中文字符长度是否固定
    private int enlen=4;//英文字符串长度
    private boolean enfix=false;//英文字符长度是否固定
    private int numlen=1;//数值字符长度
    private boolean numfix=false;//数值字符长度是否固定
    private int intlen=1;//整个数字长度
    private boolean intfix=true;//整个数字长度是否固定
    private int floatlen=0;//数字小数部分长度
    private boolean floatfix=false;//数字小数部分长度是否固定
    
    public JohnRexOptionColModel(int colIndex)
    {
    	this.colIndex=colIndex;
    }
    
		public int getColIndex()
		{
			return colIndex;
		}
		public void setColIndex(int colIndex)
		{
			this.colIndex = colIndex;
		}
		public String getPrefix()
		{
			return prefix;
		}
		public void setPrefix(String prefix)
		{
			this.prefix = prefix;
		}
		public String getSuffix()
		{
			return suffix;
		}
		public void setSuffix(String suffix)
		{
			this.suffix = suffix;
		}
		public String getFixColStr()
		{
			return fixColStr;
		}
		public void setFixColStr(String fixColStr)
		{
			this.fixColStr = fixColStr;
		}
		public int getChlen()
		{
			return chlen;
		}
		public void setChlen(int chlen)
		{
			this.chlen = chlen;
		}
		public boolean isChfix()
		{
			return chfix;
		}
		public void setChfix(boolean chfix)
		{
			this.chfix = chfix;
		}
		public int getEnlen()
		{
			return enlen;
		}
		public void setEnlen(int enlen)
		{
			this.enlen = enlen;
		}
		public boolean isEnfix()
		{
			return enfix;
		}
		public void setEnfix(boolean enfix)
		{
			this.enfix = enfix;
		}
		public int getNumlen()
		{
			return numlen;
		}
		public void setNumlen(int numlen)
		{
			this.numlen = numlen;
		}
		public boolean isNumfix()
		{
			return numfix;
		}
		public void setNumfix(boolean numfix)
		{
			this.numfix = numfix;
		}
		public int getIntlen()
		{
			return intlen;
		}
		public void setIntlen(int intlen)
		{
			this.intlen = intlen;
		}
		public boolean isIntfix()
		{
			return intfix;
		}
		public void setIntfix(boolean intfix)
		{
			this.intfix = intfix;
		}
		public int getFloatlen()
		{
			return floatlen;
		}
		public void setFloatlen(int floatlen)
		{
			this.floatlen = floatlen;
		}
		public boolean isFloatfix()
		{
			return floatfix;
		}
		public void setFloatfix(boolean floatfix)
		{
			this.floatfix = floatfix;
		}
    
    
}
