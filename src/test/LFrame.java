package test;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.PseudoColumnUsage;

import javax.swing.JButton;
import javax.swing.JFrame;
import com.fazecast.jSerialComm.SerialPort;


public class LFrame extends JFrame{
	public static void main(String[] args) {
		new LFrame();
	}
	SerialPort sp;
	OutputStream os;
	boolean status[] = {false, false, false};
	
	public LFrame() {
		sp = SerialPort.getCommPorts()[0];
		System.out.println(sp.openPort());
		os = sp.getOutputStream();
		this.setLayout(new GridLayout(1, 4));
		JButton jb1 = new JButton("1");
		System.out.println(sp.isOpen());
		jb1.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					if(status[0]) {
						os.write(0x10);
						status[0] = false;
					}else {
						os.write(0x11);	
						status[0] = true;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		JButton jb2 = new JButton("2");
		jb2.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					if(status[1]) {
						os.write(0x20);
						status[1] = false;
					}else {
						os.write(0x21);	
						status[1] = true;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		JButton jb3 = new JButton("3");
		jb3.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					if(status[2]) {
						os.write(0x30);
						status[2] = false;
					}else {
						os.write(0x31);	
						status[2] = true;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		JButton jbc = new JButton("c");
		jbc.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				sp.closePort();
			}
		});
		this.add(jb1);
		this.add(jb2);
		this.add(jb3);
		this.add(jbc);
		this.setBounds(300, 300, 500, 300);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setVisible(true);
		System.out.println(sp.isOpen());
	}
}
