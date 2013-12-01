package com.johnsoft.product.swing.timer.tile;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import com.johnsoft.library.swing.component.tile.JohnBasicTile;
import com.johnsoft.library.swing.component.tile.JohnTile;
import com.johnsoft.library.swing.component.tile.JohnTilePanel;
import com.johnsoft.library.swing.component.titlepane.JohnTitlePane;

public class JohnSkinImageTile extends JohnBasicTile
{
  protected boolean isRollOver;
  protected boolean isPress;
  protected boolean isSelected;
  
  private JFrame frame;
  private String bgImagePath;
  
	public JohnSkinImageTile(ImageIcon icon, String commandName,JFrame frame,String bgImagePath)
	{
		super(icon, commandName);
		this.frame=frame;
		this.bgImagePath=bgImagePath;
	}
	
	@Override
	public void paintTile()
	{
		if(isRollOver)
		{
			g2.setColor(new Color(0,175,225,125));
			g2.fill(r);
		}
		g2.setColor(Color.WHITE);
		g2.fillRect(r.x+2, r.y+2, r.width-4, r.height-4);
		g2.drawImage(icon.getImage(), r.x+2, r.y+2, r.width-4, r.height-4, null);
		if(isPress)
		{
			g2.setColor(new Color(255,50,50,100));
			g2.fill(r);
		}
		if(isSelected)
		{
			g2.setColor(new Color(60,60,60,180));
			g2.fill(r);
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
				setSelected();
				repaint();
				JohnTitlePane titlePane=(JohnTitlePane)frame.getContentPane();
				Image bgImage=new ImageIcon(getClass().getResource(bgImagePath)).getImage();
				titlePane.setBackgroundImage(bgImage);
				frame.repaint();
			}
		});
	}
	
	public void setSelected()
	{
		JohnTilePanel tilePane=(JohnTilePanel)pane.getParent().getParent();
		List<JohnTile> list=tilePane.getTilePanelModel();
		for(JohnTile tile:list)
		{
			if(tile instanceof JohnSkinImageTile)
			{
				JohnSkinImageTile imageTile=(JohnSkinImageTile)tile;
				if(imageTile==this)
				{
					imageTile.setSelect(true);
				}else{
					imageTile.setSelect(false);
				}
			}
		}
	}

	public JFrame getFrame()
	{
		return frame;
	}

	public void setFrame(JFrame frame)
	{
		this.frame = frame;
	}

	public String getBgImage()
	{
		return bgImagePath;
	}

	public void setBgImage(String bgImagePath)
	{
		this.bgImagePath = bgImagePath;
	}

	public boolean isRollOver()
	{
		return isRollOver;
	}

	public boolean isPress()
	{
		return isPress;
	}
	
	public boolean isSelected()
	{
		return isSelected;
	}
	
	public void setSelect(boolean selected)
	{
		this.isSelected=selected;
	}

}
