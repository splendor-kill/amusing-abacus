package com.tentacle.common.contract;

import com.tentacle.common.util.Stopwatch;

public interface IDelayedTask {
    long getTaskId();
    int getTaskType();
    Stopwatch getTimer();
    void setTimer(Stopwatch sw);
    void setCityId(int cityId);
    long getCityId();
    Object getDat();
    Object getDat2();
}
