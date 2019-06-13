package wlight.control.excontrol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.fazecast.jSerialComm.SerialPort;

import wlight.control.LightControl;
import wlight.control.LightControlException;
import wlight.control.LightControlListener;

public class ExLightControl implements LightControl{
	private int ttl = 20;
	private boolean runningFlag = true;
	private boolean isRolling = false;
	private int status;
	private long lastAliveTime;
	private LightControlListener listener;
	private ConcurrentLinkedQueue<WaitBean> wait = new ConcurrentLinkedQueue<WaitBean>();
	private SerialPort sp;
	private long clock = 0;
	/*
	private OutputStream os;
	private InputStream is;
	*/
	public ExLightControl(SerialPort sp) throws LightControlException {
		this.sp = sp;
		if(!sp.isOpen() && !sp.openPort()) {
			throw new LightControlException(LightControlException.CAN_NOT_OPEN_PORT);
		}
		/*
		os = sp.getOutputStream();
		is = sp.getInputStream();
		*/
		Thread live = new Thread(new Runnable() {
			
			@Override
			public void run() {
				//long bc = Calendar.getInstance().getTimeInMillis();
				byte[] buffer = new byte[64];
				int t = 0;
				while(runningFlag) {
					//检测响应
					if(clock - lastAliveTime > ttl * 11) {
						noResponse();
					}
					//接收传入并处理
					synchronized (sp) {
						t = sp.readBytes(buffer, 64);
						if(t > 0) {
							lastAliveTime = clock;
							for(int i = 0; i < t; i++) {
								input(buffer[i], clock);
							}
						}
					}
					//检测超时
					if(!wait.isEmpty()) {
						if(wait.peek().time < clock) {
							timeout();
						}
					}
					//发送心跳
					if(clock % (ttl * 1) == 0) {
						synchronized (sp) {
							send((byte)0x40, null);
						}
					}
					

					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					clock = clock + 1;
				}
				runningFlag = true;
			}
		});
		live.start();
		
	}
	
	private void input(byte inp, long time) {
		//System.out.println("0x" + Integer.toHexString(inp));
		WaitBean b = wait.peek();
		
		if(inp > 7||inp <0) {
			System.out.println("0x" + Integer.toHexString(inp) + ":" + "0x" + (b == null ? null:Integer.toHexString(b.msg)));
		}
		
		if(b!= null && inp == b.msg) {
			wait.remove();
		}else {
		
			if(inp <= 7 && inp >= 0) {
				if (b != null && b.msg <= 7 && b.msg >= 0) {
					wait.remove();
				}
				if (status != inp) {
					status = inp;
					update();
				}
			}else if(inp == 0x50 || inp == 0xF0){
				isRolling = false;
				update();
			}else if(inp == 0x52){
				isRolling = true;
				update();
			}
		}
	}
	
	public int getCtStatus() {
		if(isRolling) {
			return status;
		}else {
			return status + 8;
		}
	}
	

	
	private synchronized void send(byte m, Byte wt) {
		sp.writeBytes(new byte[] {m}, 1);
		if(wt != null) {
			wait.add(new WaitBean(wt, Calendar.getInstance().getTimeInMillis() + ttl));
		}
	}
	
	private synchronized void send(byte[] ms, Byte[] wts) {
		sp.writeBytes(ms, ms.length);
		for(int i = 0; i < ms.length; i++) {
			if(wts[i] != null) {
				wait.add(new WaitBean(wts[i], Calendar.getInstance().getTimeInMillis() + ttl));
			}
		}
	}
	
	/**
	 * 无响应
	 */
	private void noResponse() {
		if(listener != null) {
			listener.exceptionCatched(
					new LightControlException(LightControlException.NO_RESPONSE));
		}
	}

	/**
	 * 发送接回超时
	 */
	private void timeout() {
		if(listener != null) {
			listener.exceptionCatched(
					new LightControlException(LightControlException.TIME_OUT));
		}
	}
	
	private void update() {
		if(listener != null) {
			listener.setStatus(getCtStatus());
		}
	}
	
	@Override
	public void setStatus(int status){
		this.status = status;
		send((byte) (status % 7), (byte) this.status);
	}

	@Override
	public void put(int light, int op) {
		send((byte) (light * 0x10 + op), (byte) this.status);
	}

	@Override
	public void play(int[] sts, double delay) {
		int d = (int)(delay * 2 + 0.5);
		int f = 0;
		for(f = 0; f < 7 && d % 2 == 0; f++) {		//32, 
			d /= 2;
		}
		send((byte) (0x60 + f), (byte) (0x60 + f));
		send((byte) d, null);
		send((byte) (d >> 8), (byte) 0xEA);
		send((byte) 0x51,(byte) 0x51);
		for(int i = 0; i < sts.length; i++) {
			send((byte) sts[i], (byte) 0xFF);
		}
		send((byte) 0x52, (byte) 0x52);
	}

	@Override
	public void reset() {
		
	}

	@Override
	public void play() {
		send((byte) 0x52,(byte) 0x52);
	}

	@Override
	public void stop() {
		send((byte) 0x50, (byte) 0x50);
	}

	@Override
	public void setCloseTime(long time) {
		
	}

	@Override
	public void setOpenTime(long time) {
		
	}

	@Override
	public void close() {
		
	}
	
	private static class WaitBean {
		byte msg;
		long time;
		
		public WaitBean(byte msg, long time) {
			this.msg = msg;
			this.time = time;
		}
	}

	@Override
	public void addLightControlListener(LightControlListener listener) {
		this.listener = listener;
	}

	@Override
	public int getStatus() {
		return status;
	}

	@Override
	public boolean isPlaying() {
		return isRolling;
	}

}
