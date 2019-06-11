package wlight.control;

public class LightControlException extends Exception{
	private static final long serialVersionUID = 1L;
	
	private String msg;
	
	public LightControlException(String msg) {
		this.msg = msg;
	}
	
	@Override
	public String getMessage() {
		return msg;
	}
}
