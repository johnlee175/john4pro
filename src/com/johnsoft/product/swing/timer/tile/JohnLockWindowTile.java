package com.johnsoft.product.swing.timer.tile;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.lang.reflect.Method;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import com.johnsoft.library.swing.component.tile.JohnBasicTile;

public class JohnLockWindowTile extends JohnBasicTile
{
	protected boolean isToggle = false;
	protected ImageIcon toggleIcon;
	private Object obj;
	private int time = 60*20;
	private boolean isRollOver=false;
	
	private int x=0,y=0;
	private Popup popup;
	private Point popupLoc;
	private JLabel tooltip=new JLabel("hello");
	private Timer timer=new Timer(10, new ActionListener()
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			if(popupLoc==null)
			{
				popupLoc=new Point(r.x,r.y);
			}
			SwingUtilities.convertPointToScreen(popupLoc, pane);
			if(x<popupLoc.x)
			{
				x++;
			}
			if(y<popupLoc.y+15)
			{
				y++;
			}
			if(x<popupLoc.x&&y<popupLoc.y+15)
			{
				animateShowTooltip(x, y);
			}else{
				timer.stop();
			}
		}
	});

	public JohnLockWindowTile(ImageIcon icon,ImageIcon toggleIcon, String commandName)
	{
		super(icon, commandName);
		this.toggleIcon=toggleIcon;
	}
	
	@Override
	public void paintTile()
	{
		if(isRollOver)
		{
			g2.setColor(Color.YELLOW);
		}else{
			g2.setColor(new Color(175,255,175));
		}
		Font font = new Font("微软雅黑", Font.BOLD, 12);
		Rectangle2D r2 = g2.getFontMetrics(font).getStringBounds(commandName,
				g2);
		g2.setFont(font);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.drawString(commandName, new Double((r.width - r2.getWidth()) / 2)
				.intValue(), new Double(r2.getHeight()).intValue());
		if (isToggle)
		{
			g2.drawImage(toggleIcon.getImage(), r.x, r.y, r.width, r.height, null);
			g2.setColor(new Color(175, 175, 175, 60));
			g2.fill(new RoundRectangle2D.Float(r.x, r.y, r.width, r.height, arc, arc));
			
		}else{
			g2.drawImage(icon.getImage(), r.x, r.y, r.width, r.height, null);
		}
	}

	@Override
	public void installListeners()
	{
		pane.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseEntered(MouseEvent e)
			{
				pane.setCursor(new Cursor(Cursor.HAND_CURSOR));
				isRollOver=true;
				timer.start();
				repaint();
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				pane.setCursor(Cursor.getDefaultCursor());
				isRollOver=false;
				repaint();
			}

			@Override
			public void mouseClicked(MouseEvent e)
			{
				isRollOver=false;
				try
				{
					Class<?> clazz = Class.forName("JohnInputListenTimer");
					if (obj == null)
					{
						obj = clazz.newInstance();
					}
					if (!isToggle)
					{
						if (obj != null)
						{
							Method method1 = clazz.getDeclaredMethod("start", int.class);
							method1.invoke(obj, time);
							System.setProperty("lock_window_timer", "true");
						}
						isToggle = true;
					} else
					{
						if (obj != null)
						{
							Method method2 = clazz.getDeclaredMethod("stop");
							method2.invoke(obj);
							System.setProperty("lock_window_timer", "false");
							obj = null;
						}
						isToggle = false;
					}
				} catch (Exception e1)
				{
					e1.printStackTrace();
				}
				repaint();
			}
		});
	}
	
	public void animateShowTooltip(int x,int y)
	{
		if(popup!=null)
		{
			popup.hide();
		}
		popup=PopupFactory.getSharedInstance().getPopup(null, tooltip,x,y);
		popup.show();
	}
	
	public boolean isToggle()
	{
		return isToggle;
	}

	public void setToggle(boolean isToggle)
	{
		this.isToggle = isToggle;
		if (isToggle)
		{
			try
			{
				Class<?> clazz = Class.forName("JohnInputListenTimer");
				obj = clazz.newInstance();
				Method method = clazz.getDeclaredMethod("start", int.class);
				method.invoke(obj, time);
				System.setProperty("lock_window_timer", "True");
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public ImageIcon getToggleIcon()
	{
		return toggleIcon;
	}

	public void setToggleIcon(ImageIcon toggleIcon)
	{
		this.toggleIcon = toggleIcon;
	}

	public int getTime()
	{
		return time;
	}

	public void setTime(int time)
	{
		this.time = time;
	}

	public boolean isRollOver()
	{
		return isRollOver;
	}

}
