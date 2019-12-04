package com.frozyice.queuemanager;

import java.io.Serializable;

public class Settings implements Serializable{
    private boolean IsAcceptingNewPersons ;

    public Settings(){
        IsAcceptingNewPersons=false;
    }

    public boolean IsAcceptingNewPersons() {
        return IsAcceptingNewPersons;
    }

    public void setAcceptingNewPersons(boolean acceptingNewPersons) {
        IsAcceptingNewPersons = acceptingNewPersons;
    }
}
