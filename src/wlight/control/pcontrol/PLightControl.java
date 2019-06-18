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
	byte[] buffer;
	private boolean playing;
	private long delay;
	private int a = 0;
	
	public PLightControl(SerialPort sp) throws LightControlException {
		this.sp = sp;
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
		this.status = status;
		put(status);
	}

	@Override
	public void put(int light, int op) {
		byte[] buff = {(byte) (light * 16 + op)};
		sp.writeBytes(buff, 1);
	}
	
	public void put(int status) {
		byte[] buff = {(byte) (status)};
		sp.writeBytes(buff, 1);
//		try {
//			Thread.sleep(300);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		sp.readBytes(buffer, 1);
	}

	@Override
	public void play(int[] sts, double delay) {
		stop();
		playing = true;
		this.delay = (long) (delay + 0.5);
		this.sts = sts;
		a = 0;
		play();
	}

	@Override
	public void reset() {
		setStatus(0);
	}

	@Override
	public void play() {
		reset();
		playing = true;
		//int[] stsdefault = {1,2,4};
		//long delay = 1000;
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				while(playing) {
					setStatus(sts[a]);
					a = (a + 1) % sts.length;
					try {
						Thread.sleep(delay);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	@Override
	public void stop() {
		playing = false;
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
		return status;
	}


	@Override
	public boolean isPlaying() {
		return playing;
	}

}
