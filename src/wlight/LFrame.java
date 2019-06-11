package wlight;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.TextArea;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class LFrame extends JFrame{
	private static final long serialVersionUID = 1L;
	
	//top
	JComboBox<String> jcbComs;
	JButton	jbConn;
	JLabel jlbStatus;
	JLabel jlbTime;
	//center
	LButton jbL1;
	LButton jbL2;
	LButton jbL3;
	//bottom1
	JTextField jtfPl;
	JCheckBox jcbWt;
	JTextField jtfPt;
	JButton jbPlay;
	//bottom2
	JTextField jtfTst;
	JTextField jtfTcl;
	JButton jbTset;
	
	public LFrame() {
		JPanel jpTop = new JPanel();
		JPanel jpCenter = new JPanel();
		JPanel jpBottom = new JPanel();
		
		jpCenter.setBackground(Color.white);
		
		//Top |COM|CONNECT|STATUS|		|TIME
		String[] comsStr = new String[15];
		for(int i = 0; i < 15; i++) {
			comsStr[i] = "COM" + i;
		}
		jcbComs = new JComboBox<String>(comsStr);
		jbConn = new JButton("连接");
		jlbStatus = new JLabel("状态:未连接");
		jlbTime = new JLabel("11:11:11");
		
		JPanel jpTopLeft = new JPanel();
		jpTop.setLayout(new BorderLayout());
		jpTop.add(jpTopLeft, BorderLayout.WEST);
		jpTopLeft.add(jcbComs);
		jpTopLeft.add(jbConn);
		jpTopLeft.add(jlbStatus);
		jpTop.add(jlbTime, BorderLayout.EAST);
		//Center |BOTTON1|BOTTON2|BOTTON3|
		File fico = new File(LFrame.class.getResource("resource/switch.png").toString().split(":")[1]);
		BufferedImage imSwitchOff = null;
		try {
			imSwitchOff = ImageIO.read(fico);
		} catch (IOException e) {
			e.printStackTrace();
		}
		ImageIcon switchOff = new ImageIcon();
		jbL1 = new LButton(imSwitchOff);
		jbL2 = new LButton(imSwitchOff);
		jbL3 = new LButton(imSwitchOff);
		
		jpCenter.setLayout(new GridLayout(1, 3));
		jpCenter.add(jbL1);
		jpCenter.add(jbL2);
		jpCenter.add(jbL3);
		//Bottom |"播放："|Text|口流水灯|"间隔："|Text|播放|
		JPanel jpBottomUp = new JPanel();
		JLabel jlB1 = new JLabel("播放:");
		JLabel jlB2 = new JLabel("间隔(ms):");
		jtfPl = new JTextField();
		jcbWt = new JCheckBox("流水灯");
		jtfPt = new JTextField("1000");
		jbPlay = new JButton("播放");
		
		JPanel jpBottomUpLeft = new JPanel();
		JPanel jpBottomUpRight = new JPanel();
		jpBottomUpLeft.setLayout(new BoxLayout(jpBottomUpLeft, BoxLayout.X_AXIS));
		jpBottomUpRight.setLayout(new BoxLayout(jpBottomUpRight, BoxLayout.X_AXIS));
		
		jpBottomUpLeft.add(jlB1);
		jpBottomUpLeft.add(jtfPl);
		jpBottomUpLeft.add(jcbWt);
		//jpBottomUp.add(Box.createHorizontalStrut(200));
		jpBottomUpRight.add(jlB2);
		jpBottomUpRight.add(jtfPt);
		jpBottomUpRight.add(jbPlay);

		jpBottomUp.setLayout(new BoxLayout(jpBottomUp, BoxLayout.LINE_AXIS));
		jpBottomUpLeft.setPreferredSize(new Dimension(600, 0));
		jpBottomUp.add(jpBottomUpLeft);
		jpBottomUp.add(jpBottomUpRight);
		
		jpBottom.setLayout(new BorderLayout());
		jpBottom.add(jpBottomUp, BorderLayout.NORTH);
		//Bottom |"设定开始"|Text|”设定关闭“|Text|BOTTON
		JPanel jpBottomDown = new JPanel();
		JLabel jlB3 = new JLabel("开启时间:");
		JLabel jlB4 = new JLabel("关闭时间");
		jtfTst = new JTextField();
		jtfTcl = new JTextField();
		jbTset = new JButton("设置");
		
		jpBottomDown.setLayout(new BoxLayout(jpBottomDown, BoxLayout.X_AXIS));
		jpBottomDown.add(jlB3);
		jpBottomDown.add(jtfTst);
		jpBottomDown.add(jlB4);
		jpBottomDown.add(jtfTcl);
		jpBottomDown.add(jbTset);
		jpBottom.add(jpBottomDown, BorderLayout.SOUTH);
		
		this.add(jpTop, BorderLayout.NORTH);
		this.add(jpCenter, BorderLayout.CENTER);
		this.add(jpBottom, BorderLayout.SOUTH);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setVisible(true);
		this.setBounds(300, 300, 900, 500);
	
	}
}
