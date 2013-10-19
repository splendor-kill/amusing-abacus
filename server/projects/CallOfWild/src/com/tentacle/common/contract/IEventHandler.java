package com.tentacle.common.contract;

public interface IEventHandler {
    static final int EVENT_MARK_VALID = 1;
    static final int EVENT_MARK_INVALID = 0;

    void onTimer(int timerId);
    long getEventId();
    void setEventId(long id);
    void mark(int status);
    boolean isValid();
}
