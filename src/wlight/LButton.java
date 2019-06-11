package wlight;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JButton;

public class LButton extends JButton{
	private static final long serialVersionUID = 1L;
	
	Image icon;
	public LButton(Image icon) {
		this.icon = icon;
	}
	
	
	@Override
	public void paint(Graphics g) {
		if(icon != null) {
			int width = getWidth();
			int height = getHeight();
			int edge = Math.min(width, height);
			g.drawImage(icon.getScaledInstance(edge, edge, Image.SCALE_DEFAULT)
					, (width - edge) / 2, (height - edge) / 2, this);
		}
	}
}
