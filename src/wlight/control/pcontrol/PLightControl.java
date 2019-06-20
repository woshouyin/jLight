package wlight.control.pcontrol;

import com.fazecast.jSerialComm.SerialPort;
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
	private LightControlListener listener;
	private boolean timingOpen;
	private boolean timingClose;
	
	public PLightControl(SerialPort sp) throws LightControlException {
		this.sp = sp;
		System.out.println(sp.toString() + ":" + sp.getPortDescription());
		if(!sp.isOpen() && !sp.openPort()) {
			throw new LightControlException(LightControlException.CAN_NOT_OPEN_PORT);
		};
//		sp.addDataListener(new SerialPortDataListener() {
//			
//			@Override
//			public void serialEvent(SerialPortEvent env) {
//				if (env.getEventType() == SerialPort.LISTENING_EVENT_DATA_AVAILABLE) {
//					byte[] buff = new byte[10];
//					int t = sp.readBytes(buff, 10);
//					for(int i = 0; i < t; i++) {
////						System.out.println(new Date().getTime());
//						System.out.println(buff[i]);
//					}
//				}
//			}
//			@Override
//			public int getListeningEvents() {
//				return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
//			}
//		});
//		System.out.println(new Date().getTime());
		
		new Thread(new Runnable() {
			int s = 0;
			@Override
			public void run() {
				while(true) {
					
					byte[] buffw = {0x40};
					byte[] buffr = new byte[10];
					sp.writeBytes(buffw, 1);
					try {
						Thread.sleep(300);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					int t = sp.readBytes(buffr, 10);
					if(t == 0) {
						s++;
					}else {
						s = 0;
					}
//					System.out.println("r:"+buffr[0]);
//					System.out.println("t:"+t);
//					System.out.println("s:"+s);
					if(s>10) {
						LightControlException en = 
								new LightControlException(LightControlException.NO_RESPONSE);
						listener.exceptionCatched(en);
						close();
						break;
					}
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
	
	
	@Override
	public void close() {
		timingOpen = false;
		timingClose = false;
		playing = false;
		sp.closePort();
	}

	public int getCtStatus() {
		int ctStatus = status;
		if(playing) {
			ctStatus += 8;
		}
		if(timingOpen || timingClose) {
			ctStatus += 32;
		}
		return ctStatus;
	}
	
	@Override
	public void setStatus(int status) {
		this.status = status;
		if(listener!=null) {
			byte[] buff = {(byte) (status)};
//			System.out.println();
			listener.sendStatus(getCtStatus());
			sp.writeBytes(buff, 1);
		}
	}

	@Override
	public void put(int light, int op) {
//		byte[] buff = {(byte) (light * 16 + op)};		
		int mask = 1 << (light - 1);
		int status = 0;
		if(op == 1) {
			status = this.status | mask;
		}else {
			status = this.status & ~mask;
		}
		setStatus(status);
	}
	
	public void put(int status) {
		this.status = status;
		setStatus(status);
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
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(playing && sts != null) {
					put(sts[a]);
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
		listener.sendStatus(getCtStatus());
	}


	@Override
	public void setCloseTime(long time) {
		if(time>=0) {
			timingClose = true;
			listener.sendStatus(getCtStatus());
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					
					while(System.currentTimeMillis()<time&&timingClose) {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					if(timingClose) {
						setStatus(0x00);
						timingClose = false;
						listener.sendStatus(getCtStatus());
					}
				}
			}).start();
		}
		
	}


	@Override
	public void setOpenTime(long time) {
		if(time>=0) {
			timingOpen = true;
			listener.sendStatus(getCtStatus());
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					
					while(System.currentTimeMillis()<time&&timingOpen) {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					if(timingOpen) {
						setStatus(0x07);
						timingOpen = false;
						listener.sendStatus(getCtStatus());
					}
				}
			}).start();
		}
	}
	
	public void cancel() {
		timingOpen = false;
		timingClose = false;
		listener.sendStatus(getCtStatus());
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
		return playing;
	}

}
