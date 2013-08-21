package com.tentacle.callofwild.contract;

public interface IEventHandler {
    public static final int EVENT_MARK_VALID = 1;
    public static final int EVENT_MARK_INVALID = 0;

    public void onTimer(int timerId);
    public long getEventId();
    public void setEventId(long id);
    public void mark(int status);
    public boolean isValid();
}


