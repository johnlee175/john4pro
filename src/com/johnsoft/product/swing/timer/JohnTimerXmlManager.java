package com.johnsoft.product.swing.timer;

import java.io.InputStream;
import java.util.Vector;

import org.dom4j.Element;

import com.johnsoft.library.util.common.JohnXmlHelper;


public class JohnTimerXmlManager extends JohnXmlHelper
{
	public JohnTimerXmlManager(InputStream in)
	{
		read(in);
	}

	/**
	 * 解析xml到对象，获取定时任务集合
	 * @return 定时任务集合，每个定时任务又是一层属性集合
	 */
	public Vector<Vector<Object>> getElementsValues()
	{
		Vector<Vector<Object>> vectors=new Vector<Vector<Object>>();
		
		 for(Element timer:getElementsUnderRoot())
		 {
			 Vector<Object> vector=new Vector<Object>();
			 vector.add(timer.attributeValue("id"));
			 vector.add(new Boolean(timer.attributeValue("startup")));
			 vector.add(timer.element("type").getText());
			 vector.add(timer.element("date").getText());
			 vector.add(new Integer(timer.element("period").getText()));
			 vector.add(new Integer(timer.element("hour").getText()));
			 vector.add(new Integer(timer.element("minite").getText()));
			 vector.add(timer.element("commit").getText());
			 vectors.add(vector);
		 }
		return vectors;
	}
	
	/**
	 * 设置定时任务对象属性保存到配置xml，如果已有该任务就修改，没有则创建
	 * @param id 任务id
	 * @param startup 是否启用
	 * @param type 任务类型
	 * @param firstDate 首次执行日
	 * @param period 频次(日)
	 * @param hour 小时
	 * @param minite 分钟
	 * @param commit 配置项批注
	 */
	public void setElementValues(String id,boolean startup,String type,String firstDate,int period,int hour,int minite,String commit)
	{
		for(Element timer:getElementsUnderRoot())
		{
			if(id.equals(timer.attributeValue("id")))
			{
				timer.attributeValue("startup", new Boolean(startup).toString());
				timer.element("type").setText(type);
				timer.element("date").setText(firstDate);
				timer.element("period").setText(new Integer(period).toString());
				timer.element("hour").setText(new Integer(hour).toString());
				timer.element("minite").setText(new Integer(minite).toString());
			  timer.element("commit").setText(commit);
			  return;
			}
		}
		createElement(id, startup, type, firstDate, period, hour, minite, commit);
	}
	
	/**
	 * 在配置xml中，创建定时任务对象实体
	 * @param id 任务id
	 * @param startup 是否启用
	 * @param type 任务类型
	 * @param firstDate 首次执行日
	 * @param period 频次(日)
	 * @param hour 小时
	 * @param minite 分钟
	 * @param commit 配置项批注
	 */
	public void createElement(String id,boolean startup,String type,String firstDate,int period,int hour,int minite,String commit)
	{
		Element timer=getRootElement().addElement("timer");
		timer.addAttribute("id", id);
		timer.addAttribute("startup", new Boolean(startup).toString());
		
		Element timer_type=timer.addElement("type");
		timer_type.setText(type);
		
		Element timer_date=timer.addElement("date");
		timer_date.setText(firstDate);
		
		Element timer_period=timer.addElement("period");
		timer_period.setText(new Integer(period).toString());
		
		Element timer_hour=timer.addElement("hour");
		timer_hour.setText(new Integer(hour).toString());
		
		Element timer_minite=timer.addElement("minite");
		timer_minite.setText(new Integer(minite).toString());
		
		Element timer_commit=timer.addElement("commit");
		timer_commit.setText(commit);
	}
	
	/**
	 * 删除指定id的timer节点
	 * @param id 任务id
	 */
	public void deleteTimer(String id)
	{
		for(Element timer:getElementsUnderRoot())
		{
			if(id.equals(timer.attributeValue("id")))
			{
				getRootElement().remove(timer);
				return;
			}
		}
	}
	
	/**
	 * 清除所有定时器节点
	 */
	public void clearTimers()
	{
		for(Element timer:getElementsUnderRoot())
		{
			getRootElement().remove(timer);
		}
	}
	
}
