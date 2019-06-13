package wlight.control;

import java.util.Calendar;
import com.fazecast.jSerialComm.SerialPort;
import wlight.control.excontrol.ExLightControl;
import wlight.control.pcontrol.PLightControl;

/**
 * 用于对不同的串口选择不同的控制器
 */
public class LChoice {
	public static LightControl getLightControl(SerialPort sp) throws LightControlException {
		byte[] buffer = new byte[64];
		
		if(!sp.openPort()) {
			throw new LightControlException(LightControlException.CAN_NOT_OPEN_PORT);
		}
		sp.readBytes(buffer, 64);
		
		sp.writeBytes(new byte[] {0x50}, 1);
		
		int t = 0;
		long ts = Calendar.getInstance().getTimeInMillis();
		do {
			t = sp.readBytes(buffer, 64);
			if (Calendar.getInstance().getTimeInMillis() - ts > 500) {
				throw new LightControlException(LightControlException.CAN_NOT_OPEN_PORT);
			}
		}while(t > 0);
		
		boolean flag = false;
		for(byte b : buffer) {
			if(b == 0x50) {
				flag = true;
			}
		}
		if(flag) {
			return new ExLightControl(sp);
		}else {
			return new PLightControl(sp);
		}
	}
}
