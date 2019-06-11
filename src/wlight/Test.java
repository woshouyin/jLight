package wlight;

import java.io.IOException;

import javax.imageio.IIOException;

import wlight.control.LightControlException;
import wlight.control.pcontrol.PLightControl;

public class Test {
	public static void main(String[] args) {
		new LFrame();
		try {
			new PLightControl("COM3");
		} catch (LightControlException e) {
			e.printStackTrace();
		}
	}

}
