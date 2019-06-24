package wlight.control.excontrol;

import java.util.Calendar;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.fazecast.jSerialComm.SerialPort;

import wlight.control.LightControl;
import wlight.control.LightControlException;
import wlight.control.LightControlListener;

public class ExLightControl implements LightControl{
	private int ttl = 10;
	private boolean runningFlag = true;
	private boolean isRolling = false;
	private double delay = 0;
	private int[] sts;
	private int maxRct = 5;
	private int rct = maxRct;
	private boolean lossFlag = false;
	private boolean recFlag = false;
	private int status;
	private long lastAliveTime;
	private LightControlListener listener;
	private ConcurrentLinkedQueue<WaitBean> wait = new ConcurrentLinkedQueue<WaitBean>();
	private SerialPort sp;
	private long clock = 0;
	private long closeTime = -1;
	private long openTime = -1;
	
	
	public ExLightControl(SerialPort sp) throws LightControlException {
		this.sp = sp;
		if(!sp.isOpen() && !sp.openPort()) {
			throw new LightControlException(LightControlException.CAN_NOT_OPEN_PORT);
		}
		
		Thread live = new Thread(new Runnable() {
			byte[] buffer;
			int t = 0;
			long rckt = 0;
			@Override
			public void run() {
				buffer = new byte[64];
				t = 0;
				while(runningFlag) {
					if(closeTime != -1) {
						//System.out.println("close:\t" + ( closeTime - Calendar.getInstance().getTimeInMillis()));
						if(Calendar.getInstance().getTimeInMillis() > closeTime) {
							stop();
							setStatus(0x00);
							closeTime = -1;
							update();
						}
					}
					
					if(openTime != -1) {
						//System.out.println("open:\t" + (openTime - Calendar.getInstance().getTimeInMillis()));
						if(Calendar.getInstance().getTimeInMillis() > openTime) {
							stop();
							setStatus(0x07);
							openTime = -1;
							update();
						}
					}
					
					
					try {
						next();
					} catch (LightControlException e) {
						//处理异常
						e.printStackTrace();
						if(listener != null) {
							if(e.getFlag() == LightControlException.TIME_OUT) {
								setStatus(status);
							}
							listener.exceptionCatched(e);
						}
					}
				}
				runningFlag = true;
			}
			
			private void next() throws LightControlException {
				//检测响应
				if(clock - lastAliveTime > ttl * 50) {
					throw new LightControlException(LightControlException.NO_RESPONSE);
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
				/*
				if(!wait.isEmpty()) {
					if(wait.peek().time < clock) {
						throw new LightControlException(LightControlException.TIME_OUT);
					}
				}*/
				//发送心跳
				if(clock % (ttl * 25) == 0) {
					send((byte)0x40, null);
				}
				//滚动播放时状态检测
				if(isRolling && (delay > 10 || delay == 0)) {
					long nt = Calendar.getInstance().getTimeInMillis();
					if(nt - rckt > 50) {
						rckt = nt;
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
		});
		live.start();
		
	}
	
	/**
	 * 处理接收的数据
	 */
	private void input(byte inp, long time) throws LightControlException {
		/*
		System.out.print("0x" + Integer.toHexString(inp) + ":");
		for(WaitBean wb : wait) {
			System.out.print(Integer.toHexString(wb.msg) + ", ");
		}
		System.out.println();
		*/
		if(wait.isEmpty()) {
			switch(inp) {
			case (byte) 0x50:
				updateStatus(status, false);
				break;
			case (byte) 0x52:
			case (byte) 0xFF:
				updateStatus(status, true);
				break;
			default:
				if(inp >= 0x00 && inp <= 0x07) {
					updateStatus(inp, isRolling);
				}
				break;
			}
			
		}else {
			WaitBean b = wait.peek();
			if(b.msg != inp) {
				if(inp == (byte) 0xF0 && b.msg >= 0x00 && b.msg <= 0x07) {
					wait.remove();
					updateStatus(status, true);
				}else if(inp >= 0x00 && inp <= 0x07) {
					if(b.msg >= 0x00 && b.msg <= 0x07) {
						wait.remove();
					}
					updateStatus(inp, isRolling);
				}else if(b.msg >= 0x60 && b.msg < 0x70 || b.msg == (byte)0xEA 
						|| b.msg == 0x51 || b.msg == 0x52 || b.msg == (byte)0xFF){
					send((byte) 0x40, null);
					while(!wait.isEmpty() && wait.remove().msg != inp);
					if(rct == 0) {
						rct = maxRct;
						lossFlag = true;
						throw new LightControlException(LightControlException.TIME_OUT);
					}else {
						rct--;
						System.out.println("重试中, 剩余" + rct + "次");
						//新建线程延迟重试
						new Thread(new Runnable() {
							@Override
							public void run() {
								try {
									Thread.sleep(10);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
								play(null, 0);
							}
						}).start();
					}
				}else {
					wait.clear();
					System.out.println("0x" + Integer.toHexString(inp) + ">>0x" + Integer.toHexString(b.msg));
					throw new LightControlException(LightControlException.TIME_OUT);
				}
			}else {
				wait.remove();
				if(inp == 0x52) {
					if(lossFlag) {
						lossFlag = false;
						recFlag = true;
					}
					rct = maxRct;
				}
			}
		}
	
	}
	
	public int getCtStatus() {
		int ctStatus = status;
		if(isRolling) {
			ctStatus += 8;
		}
		if(recFlag) {
			ctStatus += 16;
			recFlag = false;
		}
		if(openTime != -1 || closeTime != -1) {
			ctStatus += 32;
		}
		return ctStatus;
	}
	

	
	private void send(byte m, Byte wt) {
		synchronized (sp) {
			sp.writeBytes(new byte[] {m}, 1);
			if(wt != null) {
				wait.add(new WaitBean(wt, Calendar.getInstance().getTimeInMillis() + ttl));
			}
		}
	}
	
	private void send(byte[] ms, Byte[] wts) {
		synchronized (sp) {
			sp.writeBytes(ms, ms.length);
			for(int i = 0; i < ms.length; i++) {
				if(wts[i] != null) {
					wait.add(new WaitBean(wts[i], Calendar.getInstance().getTimeInMillis() + ttl));
				}
			}
		}
	}
	

	
	/**
	 * 调用监听器更新外部状态 
	 */
	private void update() {
		if(listener != null) {
			listener.sendStatus(getCtStatus());
		}
	}
	
	@Override
	public void setStatus(int status){
		this.status = status;
		this.update();
		send((byte) (status % 8), (byte) this.status);
	}

	@Override
	public void put(int light, int op) {
		int mask = 1 << (light - 1);
		if(op == 1) {
			this.status = this.status | mask;
		}else {
			this.status = this.status & ~mask;
		}
		this.update();
		send((byte) (light * 0x10 + op), (byte) this.status);
	}

	@Override
	public synchronized void play(int[] sts, double delay) {
		if(delay != 0) {
			this.delay = delay;
		}
		if(sts != null){
			this.sts = sts;
		}
		
		byte[] bfs = new byte[3 + this.sts.length + 2];
		Byte[] bfr = new Byte[3 + this.sts.length + 2];
		int i = 0;
		int d = (int)(this.delay * 2 + 0.5);
		d = d >= 1 ? d : 1;
		int f = 0;
		for(f = 0; f < 7 && d % 2 == 0; f++) {		//32, 
			d /= 2;
		}
		bfs[i] = (byte) (0x60 + f);
		bfr[i] = (byte) (0x60 + f);
		bfs[++i] = (byte) d;
		bfr[i] = null;
		bfs[++i] = (byte) (d >> 8);
		bfr[i] = (byte) 0xEA;
		bfs[++i] = (byte) 0x51;
		bfr[i] = (byte) 0x51;
		for(int j = 0; j < this.sts.length; j++) {
			bfs[++i] = (byte) this.sts[j];
			bfr[i] = (byte) 0xFF;
		}
		bfs[++i] = (byte) 0x52;
		bfr[i] = (byte) 0x52;
		send(bfs, bfr);
		
		this.isRolling = true;
		update();
	}

	@Override
	public void reset() {
		
	}

	@Override
	public void play() {
		isRolling = true;
		update();
		send((byte) 0x52,(byte) 0x52);
	}

	@Override
	public void stop() {
		isRolling = false;
		update();
		send((byte) 0x50, (byte) 0x50);
	}

	@Override
	public void setCloseTime(long time) {
		this.closeTime = time;
		update();
	}

	@Override
	public void setOpenTime(long time) {
		this.openTime = time;
		update();
	}

	@Override
	public void close() {
		runningFlag = false;
		sp.closePort();
	}
	
	private static class WaitBean {
		byte msg;
		@SuppressWarnings("unused")
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
	
	/**设置状态并更新状态*/
	private void updateStatus(int status, boolean isRolling) {
		this.isRolling = isRolling;
		if(isRolling && delay <= 10 && delay != 0) {
				this.status = 0;
			if(sts != null) {
				for(int s : sts) {
					this.status = this.status | s;
				}
			}
		}else {
			this.status = status;
		}
		update();
	}

	@Override
	public boolean isPlaying() {
		return isRolling;
	}

	@Override
	public void cancel() {
		this.openTime = -1;
		this.closeTime = -1;
	}

}
