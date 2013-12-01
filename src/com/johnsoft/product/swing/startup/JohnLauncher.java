package com.johnsoft.product.swing.startup;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;

import com.johnsoft.library.util.common.JohnFileUtil;
import com.johnsoft.product.swing.filetree.JohnFileTreeMainFrame;
import com.johnsoft.product.swing.sqlmaker.JohnSqlMakerMainFrame;
import com.johnsoft.product.swing.timer.JohnTimerMainFrame;

public class JohnLauncher extends MouseAdapter
{
	private JFrame frame;
	private Point start;
	private Point end;
	private Timer timer;
	private boolean isDrawStroke;

	public static void main(String[] args) throws Exception
	{
		new JohnLauncher();
	}

	public JohnLauncher() throws Exception
	{
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Rectangle cutRect = new Rectangle(screenSize.width / 2 - 400,
				screenSize.height / 2 - 250, 800, 500);
		Robot robot = new Robot();
		final BufferedImage image = robot.createScreenCapture(cutRect);

		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		JPanel panel = new JPanel()
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics g)
			{
				g.drawImage(image, 0, 0, null);
			}
		};
		frame = new JFrame();
		frame.setTitle("johnSoft");
		frame.setIconImage(JohnResourcesManager.getImage(JohnResourcesManager.getTrayIconPng()));
		frame.setContentPane(panel);
		frame.setUndecorated(true);
		frame.setBounds(cutRect);
		frame.setVisible(true);
		frame.addMouseListener(this);
		frame.addMouseMotionListener(this);
		timer = new Timer();
		timer.schedule(new TimerTask()
		{
			@Override
			public void run()
			{
				frame.dispose();
				if(!isDrawStroke)
				{
					new JohnTimerMainFrame();
				}
			}
		}, 3000);
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		start = new Point(e.getX(), e.getY());
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		end = new Point(e.getX(), e.getY());
		Graphics2D g2 = (Graphics2D) frame.getContentPane().getGraphics();
		paintBorderGlow(g2,4,start,end);
		if (end.x - start.x - 30 > end.y - start.y)
		{
			String filePath = JohnFileUtil.readFile(
					JohnResourcesManager.getFileTreeRootPath(), "ISO-8859-1");
			isDrawStroke=true;
			new JohnFileTreeMainFrame(new File(filePath));
		} else if (end.y - start.y - 30 > end.x - start.x)
		{
			isDrawStroke=true;
			new JohnSqlMakerMainFrame();
		} else
		{
			new JohnTimerMainFrame();
		}
		timer.cancel();
		frame.dispose();
	}

	private static final Color clrGlowInnerHi = new Color(255, 39, 0, 148);
	private static final Color clrGlowInnerLo = new Color(255, 209, 0);
	private static final Color clrGlowOuterHi = new Color(255, 39, 0, 124);
	private static final Color clrGlowOuterLo = new Color(255, 179, 0);

	private void paintBorderGlow(Graphics2D g2, int glowWidth,Point start,Point end)
	{
		int gw = glowWidth * 2;
		for (int i = gw; i >= 2; i -= 2)
		{
			float pct = (float) (gw - i) / (gw - 1);
			Color mixHi = getMixedColor(clrGlowInnerHi, pct, clrGlowOuterHi,
					1.0f - pct);
			Color mixLo = getMixedColor(clrGlowInnerLo, pct, clrGlowOuterLo,
					1.0f - pct);
			g2.setPaint(new GradientPaint(start.x,start.y,mixHi,start.x,start.x+10,mixLo));
			g2.setStroke(new BasicStroke(10f, BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
			g2.draw(new Line2D.Float(start.x, start.y, end.x, end.y));
			g2.dispose();
		}
	}

	private static Color getMixedColor(Color c1, float pct1, Color c2, float pct2)
	{
		float[] clr1 = c1.getComponents(null);
		float[] clr2 = c2.getComponents(null);
		for (int i = 0; i < clr1.length; i++)
		{
			clr1[i] = (clr1[i] * pct1) + (clr2[i] * pct2);
		}
		return new Color(clr1[0], clr1[1], clr1[2], clr1[3]);
	}
}
