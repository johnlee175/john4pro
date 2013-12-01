package com.johnsoft.product.swing.timer.tile;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;

import com.johnsoft.library.swing.component.tile.JohnBasicTile;
import com.johnsoft.product.swing.filetree.JohnFileTreeMainFrame;

public class JohnFileTreeTile extends JohnBasicTile
{
	private boolean isRollOver;
	private boolean isPress;
	public JohnFileTreeTile(ImageIcon icon,String commandName)
	{
		super(icon, commandName);
	}
	
	public void paintTile()
	{
		if (isRollOver)
		{
			g2.setColor(new Color(0, 255, 255, 60));
			g2.fill(new RoundRectangle2D.Float(r.x, r.y, r.width, r.height, arc, arc));	
		}
		if(isPress)
		{
			g2.setColor(new Color(0, 255, 255, 150));
			g2.fill(new RoundRectangle2D.Float(r.x, r.y, r.width, r.height, arc, arc));	
		}
		g2.drawImage(icon.getImage(), r.x, r.y, r.width, r.height, null);
		
		g2.setColor(new Color(175,255,175));
		Font font = new Font("微软雅黑", Font.BOLD, 12);
		g2.setFont(font);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		Rectangle2D r2 = g2.getFontMetrics(font).getStringBounds(commandName,
				g2);
		g2.drawString(commandName, new Double((r.width - r2.getWidth()) / 2)
		.intValue(), new Double(r2.getHeight()).intValue());
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
				isPress=false;
				repaint();
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				pane.setCursor(Cursor.getDefaultCursor());
				isRollOver=false;
				isPress=false;
				repaint();
			}
			
			@Override
			public void mousePressed(MouseEvent e)
			{
				pane.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				isRollOver=false;
				isPress=true;
				repaint();
			}
			
			@Override
			public void mouseReleased(MouseEvent e)
			{
				pane.setCursor(Cursor.getDefaultCursor());
				isRollOver=false;
				isPress=false;
				repaint();
				EventQueue.invokeLater(new Runnable()
				{
					@Override
					public void run()
					{
						final JDialog jd=new JDialog();
						final JFileChooser jfc=new JFileChooser();
						jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
						jfc.setMultiSelectionEnabled(false);
						jd.setSize(500, 400);
						jd.setLocationRelativeTo(null);
						jd.add(jfc);
						jd.setVisible(true);
						jfc.addActionListener(new ActionListener()
						{
							@Override
							public void actionPerformed(ActionEvent e)
							{
								if(e.getActionCommand().equals("ApproveSelection"))
								{
									File file=jfc.getSelectedFile();
									new JohnFileTreeMainFrame(file);
								}
								jd.dispose();
							}
						});
						
					}
				});
			}

		});
	}

	public boolean isRollOver()
	{
		return isRollOver;
	}

	public boolean isPress()
	{
		return isPress;
	}

}
