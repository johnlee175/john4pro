package com.johnsoft.product.swing.sqlmaker;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import javax.swing.JTable;

import com.johnsoft.product.swing.startup.JohnResourcesManager;

public class JohnDataProvider
{
	public String createRandomInsert(JTable jTable, String tableName,
			String dateFormat, List<JohnRexOptionColModel> rms, int rowCount)
	{
		StringBuilder sbX = new StringBuilder();
		for (int i = 1; i < rowCount; i++)
		{
			StringBuilder sb = new StringBuilder("INSERT INTO ");
			sb.append(tableName + " VALUES ( ");
			for (int j = 0; j < jTable.getColumnCount(); j++)
			{
				String type = (String) jTable.getValueAt(0, j);
				if (type == null || type.equals(""))
				{
					continue;
				}
				if (!rms.get(j).getFixColStr().equals(""))
				{
					sb.append(rms.get(j).getFixColStr()).append(",");
					continue;
				}
				if (type.toLowerCase().indexOf("char") >= 0)
				{
					String name = getRandomStr(rms.get(j).getChlen(), rms.get(j)
							.isChfix(), rms.get(j).getEnlen(), rms.get(j).isEnfix(),
							rms.get(j).getNumlen(), rms.get(j).isNumfix());
					String fname = rms.get(j).getPrefix() + name + rms.get(j).getSuffix();
					int l = Integer.parseInt(type.substring(type.indexOf("(") + 1,
							type.indexOf(")")));
					if (fname.length() > l)
					{
						l = l - rms.get(j).getPrefix().length()
								- rms.get(j).getSuffix().length();
						if (l <= 0)
						{
							l = 0;
						}
						fname = rms.get(j).getPrefix() + name.substring(0, l)
								+ rms.get(j).getSuffix();
					}
					sb.append("'").append(fname).append("',");
				} else if (type.toLowerCase().indexOf("date") >= 0)
				{
					String name = getLastMonthDate(i);
					sb.append("TO_DATE('").append(name).append("','").append(dateFormat)
							.append("'),");
				} else
				{
					String name = null;
					if (type.toLowerCase().indexOf("num") >= 0
							|| type.toLowerCase().indexOf("dec") >= 0)
					{
						int n = Integer.parseInt(type.substring(type.indexOf("(") + 1,
								type.indexOf(",")));
						if (n <= 0)
						{
							n = 1;
						}
						if (rms.get(j).getIntlen() > 0 && rms.get(j).getIntlen() < n)
						{
							n = rms.get(j).getIntlen();
						}
						int f = Integer.parseInt(type.substring(type.indexOf(",") + 1,
								type.indexOf(")")));
						if (rms.get(j).getFloatlen() > 0 && rms.get(j).getFloatlen() < f)
						{
							f = rms.get(j).getFloatlen();
						}
						name = getRandomNum(n-f, rms.get(j).isIntfix(), f, rms.get(j)
								.isFloatfix());
					} else if (type.toLowerCase().indexOf("int") >= 0
							|| type.toLowerCase().indexOf("long") >= 0)
					{
						name = getRandomNum(rms.get(j).getIntlen(), rms.get(j).isIntfix(),
								0, true);
					} else if (type.toLowerCase().indexOf("float") >= 0
							|| type.toLowerCase().indexOf("double") >= 0)
					{
						int n = rms.get(j).getIntlen();
						int f = rms.get(j).getFloatlen();
						if (f <= 0)
						{
							f = 1;
							n = n + 1;
						}
						name = getRandomNum(n-f, rms.get(j).isIntfix(), f, rms.get(j)
								.isFloatfix());
					} else
					{
					}
					sb.append(name).append(",");
				}
			}
			sb.deleteCharAt(sb.length() - 1);
			String result = sb.append(" );\n").toString();
			sbX.append(result);
		}
		return sbX.toString();
	}

	public String createTestInsert(JTable jTable, String tableName,
			String dateFormat)
	{
		StringBuilder sbX = new StringBuilder();
		for (int i = 1; i < jTable.getSelectedRowCount(); i++)
		{
			StringBuilder sb = new StringBuilder("INSERT INTO ");
			sb.append(tableName + " VALUES(");
			for (int j = 0; j < jTable.getColumnCount(); j++)
			{
				String name = (String) jTable.getValueAt(i, j);
				String type = (String) jTable.getValueAt(0, j);
				if (type == null || type.equals(""))
				{
					continue;
				} else if (type.toLowerCase().indexOf("char") >= 0)
				{
					if (name == null || name.equals(""))
					{
						sb.append("null,");
						continue;
					}
					sb.append("'").append(name).append("',");
				} else if (type.toLowerCase().indexOf("date") >= 0)
				{
					if (name == null || name.equals(""))
					{
						sb.append("null,");
						continue;
					}
					sb.append("TO_DATE('").append(name).append("','").append(dateFormat)
							.append("'),");
				} else
				{
					if (name == null || name.equals(""))
					{
						sb.append("null,");
						continue;
					}
					sb.append(name).append(",");
				}
			}
			sb.deleteCharAt(sb.length() - 1);
			String result = sb.append(");\n").toString();
			sbX.append(result);
		}
		return sbX.toString();
	}
	
	private String getLastMonthDate(int i)
	{
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -(1 + i));
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(cal.getTime());
	}

	private String getRandomNum(int intlen, boolean intfix, int floatlen,
			boolean floatfix)
	{
		StringBuilder sb = new StringBuilder();
		Random random = new Random();
	
		if (!floatfix)
		{
			floatlen = random.nextInt(floatlen==0?1:floatlen);
		}
	  if (!intfix)
		{
			intlen = random.nextInt(intlen==0?1:intlen);
		}
		for (int i = 0; i < intlen; i++)
		{
			sb.append(random.nextInt(10));
		}
		if (sb.length()==0)
		{
			sb.append("0");
		}
		sb.append(".");
		for (int i = 0; i < floatlen; i++)
		{
			sb.append(random.nextInt(10));
		}
		if (sb.indexOf(".")==sb.length()-1)
		{
			sb.deleteCharAt(sb.length() - 1);
		}
		if (sb.toString().startsWith("0")&&!sb.toString().startsWith("0."))
		{
			sb.replace(0, 1, "1");
		}
		return sb.toString();
	}

	private String getRandomStr(int chlen, boolean chfix, int enlen,
			boolean enfix, int numlen, boolean numfix)
	{
		StringBuilder sb = new StringBuilder();
		String[] charStr = new String[] { "a", "b", "c", "d", "e", "f", "g", "h",
				"i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v",
				"w", "x", "y", "z", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
				"K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X",
				"Y", "Z" };
		String[] numStr = new String[] { "1", "2", "3", "4", "5", "6", "7", "8",
				"9", "0" };
		char[] strs=getStrs().toCharArray();
		Random random = new Random();
		if (chlen > 0)
		{
			int z = chlen;
			if (!chfix)
			{
				z = random.nextInt(chlen);
				if (z == 0)
				{
					z++;
				}
			}
			for (int i = 0; i < z; i++)
			{
				sb.append(strs[random.nextInt(1200)]);
			}
		}
		if (enlen > 0)
		{
			int z = enlen;
			if (!enfix)
			{
				z = random.nextInt(enlen);
				if (z == 0)
				{
					z++;
				}
			}
			for (int i = 0; i < z; i++)
			{
				sb.append(charStr[random.nextInt(52)]);
			}
		}
		if (numlen > 0)
		{
			int z = numlen;
			if (!numfix)
			{
				z = random.nextInt(numlen);
				if (z == 0)
				{
					z++;
				}
			}
			for (int i = 0; i < z; i++)
			{
				sb.append(numStr[random.nextInt(10)]);
			}
		}
		return sb.toString();
	}
	
	public static String getStrs()
	{
		StringBuffer sb=new StringBuffer();
	  try
		{
	  	InputStream is=new FileInputStream(JohnResourcesManager.getWordLibPath());
		  byte[] bytes=new byte[1024];
		  int len;
			while((len=is.read(bytes))>0)
			{
			  sb.append(new String(bytes, 0, len));
			}
			is.close();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	  return sb.toString();
	}
	
	public static boolean setStrs(String strs)
	{
		try
		{
			OutputStream os=new FileOutputStream("E:\\wordlib.txt");
			os.write(strs.getBytes());
			os.close();
			return true;
		} catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
}