package com.tentacle.callofwild.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.tentacle.callofwild.contract.IEventHandler;

public class Stopwatch {
	private enum TimerStatus { INVALID, INACTIVE, ACTIVE };
	
	private int id = 0;
	private int kind = 0;
	private long startPoint = 0;
	private long interval = 0;
	private boolean isPeriodic = false;
	private long timeArrow = 0;
	private long nextPoint = 0;
	private TimerStatus status = TimerStatus.INVALID;	
	private long ownerId = 0;
	
	
	private List<IEventHandler> triggers = new ArrayList<IEventHandler>();
	//通过ID建立索引
	public ConcurrentHashMap<Long, IEventHandler> eventHandleById = new ConcurrentHashMap<Long, IEventHandler>();
	
	public Stopwatch(int kind, long startPoint, long interval, boolean isPeriodic) {
		assert interval > 0;
		
		this.id = 1;//(int) World.uniIdGen.get();
		this.startPoint = startPoint;
		//时间缩放, 至少也需要100毫秒
		this.interval = Math.max(100L, interval/1);	
		this.isPeriodic = isPeriodic;
		this.kind = kind;
		timeArrow = this.startPoint;
		nextPoint = this.startPoint + this.interval;
		status = TimerStatus.INACTIVE;
	}
	
	public void elapse(long time) {
		if (status != TimerStatus.ACTIVE)
			return ;
	
		timeArrow += time;
		
		if (timeArrow >= nextPoint) {
			for (int i = 0; i < triggers.size(); i++) {
				IEventHandler handler = triggers.get(i);
				// 是否有效事件
				if (handler.isValid()) {
					handler.onTimer(id);
				} else {
					// 无效事件移除掉
					triggers.remove(i);
					// 移除索引
					if (handler.getEventId() > 0) {
						eventHandleById.remove(handler.getEventId());
					}
					i--;
//					System.out.println("Stopwatch::escape=>" + "eventid:" + handler.getEventId());
				}
			}
			if (isPeriodic) {
				nextPoint += interval;
			} else {
				status = TimerStatus.INACTIVE;
			}
		}
	}
	
	public void start() {
		status = TimerStatus.ACTIVE;
	}
	
	public int getEventSize() {
		return triggers.size();
	}
	
	public long getRemainTime() {
		long remain = nextPoint - timeArrow;
		return Math.max(0L, remain);
	}
	
	public long getStartPoint() {
		return startPoint;
	}
	//获取物理时间
	public long getRemainPhysicTime() {
		return (long) Math.ceil((double) getRemainTime() * 1);
	}
 	
	public boolean isStopped() {
		return status == TimerStatus.INACTIVE;
	}
	
	public void stop() {
		status = TimerStatus.INACTIVE;
	}
	
	public long getElapsedPhysicTime() {
		return (long) Math.ceil((double) getElapsedTime() * 1);
	}
	
	public long getElapsedTime() {
		return timeArrow - startPoint;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public int getKind() {
		return kind;
	}

	public void setKind(int kind) {
		this.kind = kind;
	}
	
	public long getInterval() {
		return interval;
	}

	public long getPhysicInterval() {
		return (long) Math.ceil((double) getInterval() * 1);
	}
	
	public void setInterval(long interval) {
		assert interval > 0;
		this.interval = interval;
	}
	
	public long getOwnerId() {
		return ownerId;
	}
	
	public void setOwnerId(long ownerId) {
		this.ownerId = ownerId;
	}
	
	public void addEvent(IEventHandler h) {
		if (h != null) {
			if (h.getEventId() > 0) {
				// 是否有重复的
				if (eventHandleById.get(h.getEventId()) != null) {
					return;
				} else {
					// 加入索引中
					eventHandleById.put(h.getEventId(), h);
				}
			}
			// 加入链表
			triggers.add(h);
		}
	}
	//移除事件
	public void removeEvent(IEventHandler h) {
		if (h != null) {
			if (h.getEventId() > 0) {
				eventHandleById.remove(h.getEventId());
				triggers.remove(h);
			} else {
				triggers.remove(h);
			}
		}
	}
	//通过事件ID 移除事件
	public void removeEvent(long eventId) {
		IEventHandler handler = eventHandleById.remove(eventId);
		if (handler != null) {
			triggers.remove(handler);
		}
	}

	//获取事件
	public IEventHandler getEventHandler(long eventId) {
		IEventHandler handler = eventHandleById.remove(eventId);
		return handler;
	}
	
	public void adjust(long var) {
		timeArrow += var;
	}
    
}
