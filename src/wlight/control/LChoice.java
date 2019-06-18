package wlight.control;

import java.util.Calendar;
import com.fazecast.jSerialComm.SerialPort;
import wlight.control.excontrol.ExLightControl;
import wlight.control.pcontrol.PLightControl;

/**
 * 用于对不同的串口选择不同的控制器
 * 1.清空读入缓存
 * 2.发送0x50
 * 3.若超过500ms则抛出异常
 * 4.若传回有0x50字样则选择Ex，否则PL
 */
public class LChoice {
	public static LightControl getLightControl(SerialPort sp) throws LightControlException {
		byte[] buffer = new byte[64];
		
		if(!sp.openPort()) {
			throw new LightControlException(LightControlException.CAN_NOT_OPEN_PORT);
		}
		//清除缓冲
		sp.readBytes(buffer, 64);
		//发送0x50
		sp.writeBytes(new byte[] {0x50}, 1);
		
		//限时读取
		int t = 0;
		long ts = Calendar.getInstance().getTimeInMillis();
		do {
			t = sp.readBytes(buffer, 64);
			if (Calendar.getInstance().getTimeInMillis() - ts > 500) {
				throw new LightControlException(LightControlException.CAN_NOT_OPEN_PORT);
			}
		}while(t == 0);
		
		//判断是否含有0x50
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
