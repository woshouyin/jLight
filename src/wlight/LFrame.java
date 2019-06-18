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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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

import com.fazecast.jSerialComm.SerialPort;

import wlight.control.LChoice;
import wlight.control.LightControl;
import wlight.control.LightControlException;
import wlight.control.LightControlListener;
import wlight.control.LoseConnectException;
import wlight.control.excontrol.ExLightControl;
import wlight.control.pcontrol.PLightControl;

public class LFrame extends JFrame{
	private static final long serialVersionUID = 1L;

	BufferedImage imSwitchOff = null;
	BufferedImage imSwitchOn1 = null;
	BufferedImage imSwitchOn2 = null;
	BufferedImage imSwitchOn3 = null;
	
	
	SerialPort[] sps = {};
	LightControl lc = null;
	
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
	
	//
	boolean connect;
	public LFrame() {
		JPanel jpTop = new JPanel();
		JPanel jpCenter = new JPanel();
		JPanel jpBottom = new JPanel();
		
		//jpCenter.setBackground(Color.white);
		
		//加载资源
		try {
			imSwitchOff = ImageIO.read(LFrame.class.getResource("resource/switchOff.png"));
			imSwitchOn1 = ImageIO.read(LFrame.class.getResource("resource/switchOn1.png"));
			imSwitchOn2 = ImageIO.read(LFrame.class.getResource("resource/switchOn2.png"));
			imSwitchOn3 = ImageIO.read(LFrame.class.getResource("resource/switchOn3.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		JLabel jlbSf = new JLabel("状态:");
		jcbComs = new JComboBox<String>();
		jbConn = new JButton("连接");
		jlbStatus = new JLabel("未连接");
		jlbTime = new JLabel("11:11:11");
		
		JPanel jpTopLeft = new JPanel();
		jpTop.setLayout(new BorderLayout());
		jpTop.add(jpTopLeft, BorderLayout.WEST);
		jpTopLeft.add(jcbComs);
		jpTopLeft.add(jbConn);
		jpTopLeft.add(jlbSf);
		jpTopLeft.add(jlbStatus);
		jpTop.add(jlbTime, BorderLayout.EAST);
		//Center |BOTTON1|BOTTON2|BOTTON3|
		jbL1 = new LButton(imSwitchOff, imSwitchOn1);
		jbL2 = new LButton(imSwitchOff, imSwitchOn2);
		jbL3 = new LButton(imSwitchOff, imSwitchOn3);
		
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
		this.setBounds(300, 300, 900, 500);
		
		connect = false;
		//连接按钮事件
		jbConn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent even) {
				SerialPort sp = sps[jcbComs.getSelectedIndex()];
				connect(sp);
			}
		});;
		//中间三个按钮事件
		jbL1.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int st = lc.getStatus();
				st = 1 - (st >> 0) % 2;
				lc.put(1, jbL1.flip());				
			}
		});
		
		jbL2.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int st = lc.getStatus();
				st = 1 - (st >> 1) % 2;
				lc.put(2, jbL2.flip());			
			}
		});
		
		jbL3.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int st = lc.getStatus();
				st = 1 - (st >> 2) % 2;
				lc.put(3, jbL3.flip());			
			}
		});
	
		jbPlay.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent even) {
				String[] strs = jtfPl.getText().replace(" ", "").split(",");
				int[] sts = new int[strs.length];
				for(int i = 0; i < strs.length; i++) {
					sts[i] = Integer.parseInt(strs[i]);
				}
				double delay = Double.parseDouble(jtfPt.getText());
				if(!lc.isPlaying()) {
					lc.play(sts, delay);
					jbPlay.setText("暂停");
				}else {
					lc.stop();
					jbPlay.setText("播放");
				}
			}
		});
		
		Thread ft = new Thread(new Runnable() {
			
			@Override
			public void run() {
				String[][] names = {new String[32], new String[32]};
				int f = 0;
				
				SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
				
				while(true) {
					//搜索端口
					SerialPort[] tsps = SerialPort.getCommPorts();
					
					boolean flag = (tsps.length == sps.length);
					for (int i = 0; i < tsps.length; i++) {
						names[f][i] = tsps[i].getSystemPortName() + ":" + tsps[i].getDescriptivePortName();
						if(flag && !names[f][i].equals(names[1-f][i])) {
							flag = false;
						}
					}
					
					if(!flag) {
						sps = tsps;
						jcbComs.removeAllItems();
						for (int i = 0; i < sps.length; i++) {
							jcbComs.addItem(names[f][i]);
							System.out.println(names[f][i]);
						}
					}
					f = 1 - f;
					
					//时钟
					jlbTime.setText(sdf.format(new Date()));
					
				}
			}
		});
		
		ft.start();
		this.setVisible(true);
	}
	
	public void setStatusStr(String str, Color c) {
		jlbStatus.setForeground(c);
		jlbStatus.setText(str);
	}
	
	public void connect(SerialPort sp) {
		try {
			if(lc != null) {
				lc.close();
			}
			//lc = LChoice.getLightControl(sp);
			//lc = new ExLightControl(sp);
			lc = new PLightControl(sp);
		} catch (LightControlException e) {
			setStatusStr("连接失败", Color.RED);
			e.printStackTrace();
		}
		
		lc.addLightControlListener(new LightControlListener() {
			
			@Override
			public void setStatus(int ctStatus) {
				jbL1.put(ctStatus % 2);
				jbL2.put((ctStatus >> 1) % 2);
				jbL3.put((ctStatus >> 2) % 2);
			}
			
			@Override
			public void exceptionCatched(LightControlException e) {
				switch (e.getFlag()) {
				case LightControlException.NO_RESPONSE:
					System.out.println("无响应");
					setStatusStr("无响应", Color.RED);
					break;

				default:
					break;
				}
			}
		});
		

		setStatusStr("连接成功", Color.GREEN);
		connect = true;
		new Thread(new  Runnable() {
			public void run() {
				try {
					while(connect) {
						connect = sp.isOpen();
						if(connect == false) {
							LoseConnectException lce =  
									new LoseConnectException(connect); 
							throw(lce);
						}
					}
				} catch (LoseConnectException e) {
					// TODO: handle exception
					System.out.println(e.getMessage());
					setStatusStr("连接中断", Color.RED);
				}
			}
		}).start(); 
	}
}
