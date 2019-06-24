package wlight;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import com.fazecast.jSerialComm.SerialPort;
import wlight.control.LChoice;
import wlight.control.LightControl;
import wlight.control.LightControlException;
import wlight.control.LightControlListener;
import wlight.control.excontrol.ExLightControl;

public class LFrame extends JFrame{
	private static final long serialVersionUID = 1L;

	BufferedImage imSwitchOff = null;
	BufferedImage imSwitchOn1 = null;
	BufferedImage imSwitchOn2 = null;
	BufferedImage imSwitchOn3 = null;
	
	
	SerialPort[] sps = {};
	LightControl lc = null;
	boolean isConnected = false;
	boolean isRolling = false;
	boolean setted = false;
	String lsts = "";
	double ldelay = -1;
	long lpt = 0;
	
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
		int width = 900;
		int height = 500;
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
		//JPanel jpBottomUp = new JPanel();
		JSplitPane jpBottomUp = new JSplitPane();
		JLabel jlB1 = new JLabel("播放:");
		JLabel jlB2 = new JLabel(" 间隔(ms):");
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
		jpBottomUpRight.add(jlB2);
		jpBottomUpRight.add(jtfPt);
		jpBottomUpRight.add(jbPlay);
		
		jpBottomUp.setLeftComponent(jpBottomUpLeft);
		jpBottomUp.setRightComponent(jpBottomUpRight);
		jpBottomUp.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		jpBottomUp.setOneTouchExpandable(false);
		jpBottomUp.setContinuousLayout(true);
		jpBottomUp.setDividerSize(5);
		jpBottomUp.setResizeWeight(1.0);
		jpBottomUp.setDividerLocation(width - 200);
		jpBottomUp.setPreferredSize(new Dimension(0, 30));
		
		jpBottom.setLayout(new GridLayout(2, 1));
		jpBottom.add(jpBottomUp);
		//Bottom |"设定开始"|Text|”设定关闭“|Text|BOTTON
		JPanel jpBottomDown = new JPanel();
		JPanel jpBottomDownL = new JPanel();
		JPanel jpBottomDownLL = new JPanel();
		JPanel jpBottomDownLR = new JPanel();
		JLabel jlB3 = new JLabel("开启时间:");
		JLabel jlB4 = new JLabel("关闭时间:");
		jtfTst = new JTextField();
		jtfTcl = new JTextField();
		jbTset = new JButton("设置");
		
		jpBottomDown.setLayout(new BoxLayout(jpBottomDown, BoxLayout.X_AXIS));
		jpBottomDownLL.setLayout(new BoxLayout(jpBottomDownLL, BoxLayout.X_AXIS));
		jpBottomDownLR.setLayout(new BoxLayout(jpBottomDownLR, BoxLayout.X_AXIS));
		jpBottomDownL.setLayout(new GridLayout(1, 2));
		jpBottomDownLL.add(jlB3);
		jpBottomDownLL.add(jtfTst);
		jpBottomDownLR.add(jlB4);
		jpBottomDownLR.add(jtfTcl);
		jpBottomDownL.add(jpBottomDownLL);
		jpBottomDownL.add(jpBottomDownLR);
		jpBottomDown.add(jpBottomDownL);
		jpBottomDown.add(jbTset);
		jpBottom.add(jpBottomDown);
		
		this.add(jpTop, BorderLayout.NORTH);
		this.add(jpCenter, BorderLayout.CENTER);
		this.add(jpBottom, BorderLayout.SOUTH);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		int sWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
		int sHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
		this.setBounds((sWidth - width) / 2, (sHeight - height) / 2, width, height);
		

		//连接按钮事件
		jbConn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent even) {
				if(!isConnected) {
					SerialPort sp = sps[jcbComs.getSelectedIndex()];
					connect(sp);
				}else {
					disconnect();
				}
			}
		});;
		//中间三个按钮事件
		jbL1.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(!isConnected) {
					setStatusStr("未连接时开关红灯", Color.RED);
					return;
				}
				int st = lc.getStatus();
				st = 1 - (st >> 0) % 2;
				lc.put(1, st);
			}
		});
		
		jbL2.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(!isConnected) {
					setStatusStr("未连接时开关绿灯", Color.RED);
					return;
				}
				int st = lc.getStatus();
				st = 1 - (st >> 1) % 2;
				lc.put(2, st);
			}
		});
		
		jbL3.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(!isConnected) {
					setStatusStr("未连接时开关蓝灯", Color.RED);
					return;
				}
				int st = lc.getStatus();
				st = 1 - (st >> 2) % 2;
				lc.put(3, st);
			}
		});
		
		jcbWt.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(jcbWt.isSelected()) {
					jtfPl.setText("0,1,3,7");
					jtfPl.setEditable(false);
				}else {
					jtfPl.setEditable(true);
				}
			}
		});
	
		jbPlay.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent even) {
				playOrPause();
			}
		});
		
		jbTset.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				tsetOrCancel();
			}
		});
		
		this.addWindowListener(new WindowListener() {
			@Override
			public void windowOpened(WindowEvent e) {}
			@Override
			public void windowIconified(WindowEvent e) {}
			@Override
			public void windowDeiconified(WindowEvent e) {}
			@Override
			public void windowDeactivated(WindowEvent e) {}
			@Override
			public void windowClosing(WindowEvent e) {
				Thread t = new Thread(new Runnable() {
					@Override
					public void run() {
						disconnect();
					}
				});
				t.setDaemon(true);
				t.start();
			}
			@Override
			public void windowClosed(WindowEvent e) {}
			@Override
			public void windowActivated(WindowEvent e) {}
		});;
		
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
						}
					}
					f = 1 - f;
					
					//时钟
					jlbTime.setText(sdf.format(new Date()));
					
				}
			}
		});
		Font font = new Font("", Font.BOLD, 13);
		Component[] cps = new Component[] {
				jcbComs, jbConn, jbPlay, jbTset,
				jlB1, jlB2, jlB3, jlB4, jlbSf,
				jlbStatus, jlbTime,
				jtfPl,jtfPt, jtfTcl, jtfTst
				,jcbWt};
		
		for(Component cp : cps) {
			cp.setFont(font);
		}
		 
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
			lc = LChoice.getLightControl(sp);
			if(lc instanceof ExLightControl) {
				System.out.println("ExLightControl");
			}else {
				System.out.println("PLightControl");
			}
			this.isConnected = true;
			this.jbConn.setText("断开");
		} catch (LightControlException e) {
			setStatusStr("连接失败", Color.RED);
			sp.closePort();
			e.printStackTrace();
		}
		lc.addLightControlListener(new LightControlListener() {
			
			@Override
			public void sendStatus(int ctStatus) {
				jbL1.put(ctStatus % 2);
				jbL2.put((ctStatus >> 1) % 2);
				jbL3.put((ctStatus >> 2) % 2);
				if((ctStatus >> 3) % 2 == 1) {
					if(!isRolling) {
						isRolling = true;
						setStatusStr("继续播放", MyColor.DARK_GREEN);
					}
					jbPlay.setText("暂停");
				}else {
					if(isRolling) {
						isRolling = false;
						setStatusStr("已暂停", Color.MAGENTA);
					}
					jbPlay.setText("播放");
				}
				if((ctStatus >> 4) % 2 == 1) {
					setStatusStr("恢复正常", MyColor.DARK_GREEN);
				}
				
				if((ctStatus >> 5) % 2 == 1) {
					setted = true;
					jbTset.setText("取消");
				}else {
					setted = false;
					jbTset.setText("设置");
				}
			}
			
			@Override
			public void exceptionCatched(LightControlException e) {
				switch (e.getFlag()) {
				case LightControlException.NO_RESPONSE:
					disconnect();
					break;
				case LightControlException.TIME_OUT:
					setStatusStr("响应可能超时或丢失", Color.yellow);
					break;
				default:
					break;
				}
			}
		});
		

		setStatusStr("连接成功", MyColor.DARK_GREEN);
	}
	
	public void disconnect() {
		if(lc != null) {
			lc.close();
			setStatusStr("已暂停", Color.MAGENTA);
			lc = null;
			isConnected = false;
			isRolling = false;
			setted = false;
			jbPlay.setText("播放");
			jbTset.setText("设置");
			lsts = "";
			ldelay = -1;
			this.setStatusStr("已断开", Color.RED);
			jbConn.setText("连接");
		}
	}
	
	public void playOrPause() {
		if(!isConnected) {
			setStatusStr("未连接时尝试播放", Color.RED);
			return;
		}
		if(!isRolling) {
			String str = jtfPl.getText();
			double dl = 0;
			try {
				dl = Double.parseDouble(jtfPt.getText());
			}catch(NumberFormatException e) {
				setStatusStr("延时格式输入错误", Color.RED);
				return;
			}
			if(dl <= 0) {
				setStatusStr("延时设置为非正", Color.RED);
				return;
			}
			long t = Calendar.getInstance().getTimeInMillis();
			if(!str.equals(lsts) || dl != ldelay || t - lpt < 200) {
				String[] strs = str.replace(" ", "").split(",");
				int[] sts = new int[strs.length];
				for(int i = 0; i < strs.length; i++) {
					try {
						sts[i] = Integer.parseInt(strs[i]);
					} catch (Exception e) {
						setStatusStr("播放帧输入格式错误", Color.RED);
						return;
					}
				}

				lsts = str;
				ldelay = dl;
				lc.play(sts, dl);
				setStatusStr("从头开始播放", MyColor.DARK_GREEN);
			}else {
				lc.play();
				setStatusStr("继续播放", MyColor.DARK_GREEN);
			}
		}else {
			lpt = Calendar.getInstance().getTimeInMillis();
			lc.stop();
			setStatusStr("已暂停", Color.MAGENTA);
		}
	}
	
	private void tsetOrCancel() {
		if(!isConnected) {
			setStatusStr("未连接时设定时间", Color.RED);
			return;
		}
		if(!setted) {
			String[] str = jtfTst.getText().split("[^0-9]+");
			int n = str.length > 6 ? 6 : str.length;
			String strSt = null;
			String strCl = null;
			Calendar ca = Calendar.getInstance();
			long nt = ca.getTimeInMillis();
			int time[] = new int[6];
			//设置开启时间
			time[5] = ca.get(Calendar.YEAR);
			time[4] = ca.get(Calendar.MONTH);
			time[3] = ca.get(Calendar.DAY_OF_MONTH);
			time[2] = ca.get(Calendar.HOUR_OF_DAY);
			time[1] = ca.get(Calendar.MINUTE);
			time[0] = ca.get(Calendar.SECOND);
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			if(n > 0 && !"".equals((str[0]))) {
				for(int i = 0; i < n; i++) {
					int t = Integer.parseInt(str[str.length - i - 1]);
					time[i] = t;
				}
				ca.set(time[5], time[4], time[3], time[2], time[1], time[0]);
				if(ca.getTimeInMillis() <= nt && n <= 5) {
					time[n]++;
					ca.set(time[5], time[4], time[3], time[2], time[1], time[0]);
				}
				lc.setOpenTime(ca.getTimeInMillis() / 1000 * 1000);
				strSt = sdf.format(ca.getTime());
			}
			//设置关闭时间
			ca = Calendar.getInstance();
			time[5] = ca.get(Calendar.YEAR);
			time[4] = ca.get(Calendar.MONTH);
			time[3] = ca.get(Calendar.DAY_OF_MONTH);
			time[2] = ca.get(Calendar.HOUR_OF_DAY);
			time[1] = ca.get(Calendar.MINUTE);
			time[0] = ca.get(Calendar.SECOND);
			str = jtfTcl.getText().split("[^0-9]+");
			n = str.length > 6 ? 6 : str.length;
			if(str.length > 0 && !"".equals((str[0]))) {
				for(int i = 0; i < str.length; i++) {
					int t = Integer.parseInt(str[str.length - i - 1]);
					time[i] = t;
				}
				ca.set(time[5], time[4], time[3], time[2], time[1], time[0]);
				if(ca.getTimeInMillis() <= nt && n <= 5) {
					time[n]++;
					ca.set(time[5], time[4], time[3], time[2], time[1], time[0]);
				}
				lc.setCloseTime(ca.getTimeInMillis() / 1000 * 1000);
				strCl = sdf.format(ca.getTime());
			}
			if(strSt != null && strCl != null) {
				setStatusStr("定时开启:" + strSt + "/定时关闭:" + strCl, MyColor.DARK_GREEN);
			}else if(strSt != null) {
				setStatusStr("定时开启:" + strSt, MyColor.DARK_GREEN);
			}else if(strCl != null) {
				setStatusStr("定时关闭:" + strCl, MyColor.DARK_GREEN);
			}
		}else {
			lc.cancel();
			setStatusStr("取消定时", MyColor.DARK_GREEN);
		}
	
	}
	static class MyColor{
		static final Color DARK_GREEN = new Color(0, 128, 0);
	}
	
}
