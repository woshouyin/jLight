package wlight.control.pcontrol;

import java.io.IOException;
import java.sql.Time;
import java.util.Date;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortPacketListener;

import wlight.control.LightControl;
import wlight.control.LightControlException;

public class PLightControl implements LightControl {
	private int status = 0;
	private SerialPort sp;
	private int[] sts;
	private boolean playing;
	
	public PLightControl(String port) throws LightControlException {
		SerialPort sp = SerialPort.getCommPorts()[0];
		System.out.println(sp.toString() + ":" + sp.getPortDescription());
		if(!sp.openPort()) {
			throw new LightControlException("无法打开串口");
		};
		sp.addDataListener(new SerialPortDataListener() {
			
			@Override
			public void serialEvent(SerialPortEvent env) {
				if (env.getEventType() == SerialPort.LISTENING_EVENT_DATA_AVAILABLE) {
					byte[] buff = new byte[10];
					int t = sp.readBytes(buff, 10);
					for(int i = 0; i < t; i++) {
						System.out.println(new Date().getTime());
						System.out.println(buff[i]);
					}
				}
			}
			@Override
			public int getListeningEvents() {
				return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
			}
		});
		System.out.println(new Date().getTime());
		byte[] buffer = {(byte)1};
		sp.writeBytes(buffer, 1);
		
		
	}
	
	
	@Override
	public void close() {
		sp.closePort();
	}

	@Override
	public void setStatus(int status) {
		
	}

	@Override
	public void put(int light, int op) {
		synchronized (sp) {
			byte[] buff = {(byte) (light * 10 + op)};
			sp.writeBytes(buff, 1);
		}
	}

	@Override
	public void setSts(int[] sts, double delay) {
		stop();
		this.sts = sts;
	}

	@Override
	public void reset() {
		
	}

	@Override
	public void play() {
		playing = true;
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
			}
		});
	}

	@Override
	public void stop() {
		
	}


	@Override
	public void timeOut() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void setCloseTime(long time) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void setOpenTime(long time) {
		// TODO Auto-generated method stub
		
	}

}
