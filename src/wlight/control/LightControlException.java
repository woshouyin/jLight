package wlight.control;

/**
 * 控制器异常类，使用Flag标记不同的错误类型
 */
public class LightControlException extends Exception{
	private static final long serialVersionUID = 1L;
	public static final int TIME_OUT = 0;
	public static final int CAN_NOT_OPEN_PORT = 1;
	public static final int NO_RESPONSE = 2;
	
	private String msg;
	private int flag;
	/**
	 * 初始化异常
	 * @param flag
	 * 错误类型，可用的有：
	 * 		TIME_OUT
	 * 		CAN_NOT_OPEN_PORT 
	 * 		NO_RESPONSE
	 */
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
	
	/**
	 * 获取当前错误类型
	 */
	public int getFlag() {
		return flag;
	}
}
