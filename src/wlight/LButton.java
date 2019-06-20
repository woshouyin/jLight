package wlight;

import java.awt.Graphics;
import java.awt.Image;
import javax.swing.JButton;

public class LButton extends JButton{
	private static final long serialVersionUID = 1L;
	
	private Image iconOn;
	private Image iconOff;
 	private boolean status;
	
	public LButton(Image iconOff, Image iconOn) {
		this.iconOff = iconOff;
		this.iconOn = iconOn;

		status = false;
	}
	
	public void on() {
		status = true;
		repaint();
	}
	
	public void off() {
		status = false;
		repaint();
	}
	

	public void put(int st) {
		status = (st == 1);
		repaint();
	}
	
	public int flip() {
		status = !status;
		return status ? 1 : 0;
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Image icon;
		if(status) {
			icon = iconOn;
		}else {
			icon = iconOff;
		}
		if(icon != null) {
			int width = getWidth();
			int height = getHeight();
			int edge = (int)(Math.min(width, height) * 0.70);
			g.drawImage(icon.getScaledInstance(edge, edge, Image.SCALE_DEFAULT)
					, (width - edge) / 2, (height - edge) / 2, this);
		}
	}

}
