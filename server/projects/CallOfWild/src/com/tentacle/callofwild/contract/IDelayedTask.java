package com.tentacle.callofwild.contract;

import com.tentacle.callofwild.logic.Stopwatch;

public interface IDelayedTask {
	public final static int task_type_universal 				= 0;
	
	public long getTaskId();
	public int getTaskType();
	public Stopwatch getTimer();
	public void setTimer(Stopwatch sw);
	public void setCityId(int cityId);
	public long getCityId();
	public Object getDat();
	public Object getDat2();
	
}
