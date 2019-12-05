package com.frozyice.queuemanager;

import java.io.Serializable;

public class Settings implements Serializable{
    private boolean IsAcceptingNewPersons;
    private boolean IsEndingCalls;

    public Settings(){
        IsAcceptingNewPersons=false;
        IsEndingCalls=true;
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
}
