package wlight.control;

public class LoseConnectException extends Exception{
	private boolean connect;
	private String msg; 
	public LoseConnectException(boolean connect) {
		this.connect = connect;
		msg = "连接中断";
	}
	public String getMessage() {
		return msg;
	}
}
