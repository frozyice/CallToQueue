package com.frozyice.queuemanager;

import java.io.Serializable;

public class Settings implements Serializable{
    private boolean IsAcceptingNewPersons;
    private boolean IsEndingCalls;
    private int EstimatedQueueTime;


    public Settings(){
        IsAcceptingNewPersons=false;
        IsEndingCalls=true;
        EstimatedQueueTime=5;
    }

    public boolean isAcceptingNewPersons() {
        return IsAcceptingNewPersons;
    }

    public void setAcceptingNewPersons(boolean acceptingNewPersons) {
        IsAcceptingNewPersons = acceptingNewPersons;
    }

    public boolean isEndingCalls() {
        return IsEndingCalls;
    }

    public void setEndingCalls(boolean endingCalls) {
        IsEndingCalls = endingCalls;
    }

    public int getEstimatedQueueTime() {
        return EstimatedQueueTime;
    }

    public void setEstimatedQueueTime(int estimatedQueueTime) {
        EstimatedQueueTime = estimatedQueueTime;
    }
}
