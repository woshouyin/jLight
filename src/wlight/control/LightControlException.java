package wlight.control;

public class LightControlException extends Exception{
	private static final long serialVersionUID = 1L;
	public static final int TIME_OUT = 0;
	public static final int CAN_NOT_OPEN_PORT = 1;
	public static final int NO_RESPONSE = 2;
	
	private String msg;
	private int flag;
	
	public LightControlException(int flag) {
		this.flag = flag;
		switch (flag) {
		case TIME_OUT:
			msg = "响应超时";
			break;
		case CAN_NOT_OPEN_PORT:
			msg = "无法打开端口";
			break;
		default:
			msg = "异常标志错误";
			break;
		}
	}
	
	@Override
	public String getMessage() {
		return msg;
	}
	
	public int getFlag() {
		return flag;
	}
}
