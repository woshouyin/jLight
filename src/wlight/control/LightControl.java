package wlight.control;

import java.io.Closeable;
import java.io.IOException;
import java.util.Calendar;

import com.fazecast.jSerialComm.SerialPort;

import wlight.control.excontrol.ExLightControl;
import wlight.control.pcontrol.PLightControl;

public interface LightControl {
	public void setStatus(int status);
	public void put(int light, int op);
	public void reset();
	public void play();
	public void stop();
	public void setCloseTime(long time);
	public void setOpenTime(long time);
	public void close();
	public void addLightControlListener(LightControlListener listener);
	public void play(int[] sts, double delay);
	public int getStatus();
	public boolean isPlaying();
	

}
