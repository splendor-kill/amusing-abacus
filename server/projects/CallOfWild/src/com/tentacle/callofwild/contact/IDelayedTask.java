package com.tentacle.callofwild.contact;

import com.tentacle.callofwild.protocol.ProtoBasis.eTaskStatus;
import com.tentacle.callofwild.server.Stopwatch;

public interface IDelayedTask {
	public final static int task_type_universal 				= 0;
	
	public long getTaskId();
	public int getTaskType();
	public eTaskStatus getTaskStatus();
	public void setTaskStatus(eTaskStatus status);
	public Stopwatch getTimer();
	public void setTimer(Stopwatch sw);
	public void setCityId(int cityId);
	public long getCityId();
	public Object getDat();
	public Object getDat2();
	
}
