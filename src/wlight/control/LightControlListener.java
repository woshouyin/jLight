package wlight.control;


public interface LightControlListener {
	/**
	 * 监听控制器捕获的异常
	 */
	public void exceptionCatched(LightControlException e);
	/**
	 * 监听控制器状态变化
	 * @param ctStatus
	 * 四位二进制数
	 * 	1.低3为位为三个灯的状态
	 * 	2.第四位为动画播放状态
	 * 	3.0为关闭，1为开启
	 * 第四位表示播放状态
	 * 第五位表示异常恢复
	 */
	public void setStatus(int ctStatus);
}
