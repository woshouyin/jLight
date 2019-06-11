package wlight.control;

import java.io.Closeable;

public interface LightControl {
	public void setStatus(int status);
	public void put(int light, int op);
	public void setSts(int[] sts, double delay);
	public void timeOut();
	public void reset();
	public void play();
	public void stop();
	public void setCloseTime(long time);
	public void setOpenTime(long time);
	public void close();
}
