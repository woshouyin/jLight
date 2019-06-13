package wlight.control;

public interface LightControlListener {
	public void exceptionCatched(LightControlException e);
	public void setStatus(int ctStatus);
}
