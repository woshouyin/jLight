package wlight.control;

public interface LightControl {
	/**
	 * 设置当前led灯的状态
	 * @param status
	 * 状态5位二进制数， 从低到高前三位分别标识1-3号灯， 0为关1为开
	 */
	public void setStatus(int status);
	/**
	 * 设置单个led灯的状态
	 * @param light
	 * 灯号，从1开始数
	 * @param op
	 * 0或1,0关1开
	 */
	public void put(int light, int op);
	/**
	 * 重置控制器
	 */
	public void reset();
	/**
	 * 开始播放动画 
	 */
	public void play();
	/**
	 * 停止播放动画 
	 */
	public void stop();
	/**
	 * 定时关闭
	 * @param time
	 * 关闭时的毫秒数
	 */
	public void setCloseTime(long time);
	/**
	 * 定时开启
	 * @param time
	 * 开启时的毫秒数
	 */
	public void setOpenTime(long time);
	/**
	 * 关闭，释放资源
	 */
	public void close();
	/**
	 * 添加监听器， 监听状态变化和异常传出
	 */
	public void addLightControlListener(LightControlListener listener);
	/**
	 * 播放动画
	 * @param sts
	 * 动画帧
	 * @param delay
	 * 帧间隔， 单位毫秒
	 */
	public void play(int[] sts, double delay);
	/**
	 * 获取当前led灯状态
	 * @return
	 * 状态，三位二进制数
	 */
	public int getStatus();
	/**
	 * 获取当前动画播放状态
	 * @return
	 * 不播放为false， 播放为true
	 */
	public boolean isPlaying();
	
	/**
	 * 取消当前定时
	 */
	public void cancel();

}
