package test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.sql.rowset.serial.SerialDatalink;

import com.fazecast.jSerialComm.SerialPort;

public class Test {
	String a = "Hello";
	public static void main(String[] args) {
		/*
		SerialPort sp = SerialPort.getCommPorts()[0];
		sp.setBaudRate(9600);
		sp.openPort();
		InputStream is = sp.getInputStream();
		OutputStream os = sp.getOutputStream();
		int i = 0;
		byte[] buffer = new byte[5];
		while (true) {
			i = (i + 1) % 8;
			try {
				os.write(i);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		*/
		Test t = new Test();
		t.da();
	}
	
	void da() {
		synchronized (a) {
			System.out.println("da:" + a);
			db();
		}
	}
	void db() {
		synchronized (a) {
			System.out.println("db:" + a);
		}
	}
}
