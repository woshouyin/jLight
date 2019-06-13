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
import wlight.control.LightControlListener;

public class PLightControl implements LightControl {
	private int status = 0;
	private SerialPort sp;
	private int[] sts;
	private boolean playing;
	
	public PLightControl(SerialPort sp) throws LightControlException {
		System.out.println(sp.toString() + ":" + sp.getPortDescription());
		if(!sp.isOpen() && !sp.openPort()) {
			throw new LightControlException(LightControlException.CAN_NOT_OPEN_PORT);
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
	public void play(int[] sts, double delay) {
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
	public void setCloseTime(long time) {
	}


	@Override
	public void setOpenTime(long time) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void addLightControlListener(LightControlListener listener) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public int getStatus() {
		return 0;
	}


	@Override
	public boolean isPlaying() {
		return false;
	}

}
