package com.johnsoft.product.swing.timer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 定时器类
 * @author john
 */
public class JohnTimer
{
	private Timer timer;
	
	private String name;
  
	//对应JComboBox的顺序
	public static final int TYPE_SHUNDOWN=0;
	public static final int TYPE_SLEEP=1;
	public static final int TYPE_LOCKWINDOW=2;
	public static final int TYPE_OPENURL=3;
	public static final int TYPE_SHOWMESSAGE=4;
	public static final int TYPE_EXEFILE=5;
	public static final int TYPE_DELFILE=6;
	
	public JohnTimer(String name)
	{
		this.name=name;
		timer=new Timer();
	}
	
	public void orderTask(int type,String firstDate,int period,int hour,int minite,String commit)
	{
		switch (type)
		{
			case TYPE_SHUNDOWN:
				exeDeploy(JohnTimerTask.getRuntimeTask("Shundown.exe -s -t 300"), firstDate, period, hour, minite-5);
			break;
			
			case TYPE_SLEEP:	
				exeDeploy(JohnTimerTask.getRuntimeTask("rundll32.exe powrprof.dll SetSuspendState"), firstDate, period, hour, minite);
			break;
			
			case TYPE_LOCKWINDOW:
				exeDeploy(JohnTimerTask.getRuntimeTask("rundll32.exe user32.dll,LockWorkStation"), firstDate, period, hour, minite);
			break;
			
			case TYPE_OPENURL:
				exeDeploy(JohnTimerTask.getOpenBrowserTask(commit), firstDate, period, hour, minite);
			break;
			
			case TYPE_SHOWMESSAGE:
				exeDeploy(JohnTimerTask.getMessageTask(commit), firstDate, period, hour, minite);
			break;
			
			case TYPE_EXEFILE:
				exeDeploy(JohnTimerTask.getExeFileTask(commit), firstDate, period, hour, minite);
			break;
			
			case TYPE_DELFILE:
				exeDeploy(JohnTimerTask.getDelFileTask(commit), firstDate, period, hour, minite);
			break;
		}
	}
	
	/**
	 * 执行部署
	 */
	protected void exeDeploy(TimerTask task,String firstDate,int period,int hour,int minite)
	{
		try
		{
			Date date=new SimpleDateFormat("yyyy-MM-dd").parse(firstDate);
			if(period!=0)
			{
				timer.schedule(task,computeDate(date,period,hour, minite),period*24*60*60*1000);
			}else{
					timer.schedule(task,date);
				}
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * 计算日期，如果设定时间小于当前时间，则推到明天同一时间执行
	 */
	protected Date computeDate(Date date,int period,int hour,int minite)
	{
		Calendar now=Calendar.getInstance();
		now.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DATE), 0, 0, 0);
		Calendar cal=Calendar.getInstance();
		cal.setTime(date);
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE), 0, 0, 0);
		long nowDayCount=now.getTimeInMillis()/(24*60*60*1000);
		long calDayCount=cal.getTimeInMillis()/(24*60*60*1000);
		while(nowDayCount>calDayCount)
		{
			calDayCount+=period;
		}
		cal.setTimeInMillis((calDayCount+1)*(24*60*60*1000));
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE,minite);
		now=Calendar.getInstance();
		if(now.getTimeInMillis()>cal.getTimeInMillis())
		{
			cal.add(Calendar.DATE, period);
		}
		return cal.getTime();
	}
	
	/**
	 * 销毁定时任务
	 */
	public void destroy()
	{
		timer.cancel();
		timer.purge();
		timer=null;
	}
	
	public String getName()
	{
		return name;
	}
	
}
