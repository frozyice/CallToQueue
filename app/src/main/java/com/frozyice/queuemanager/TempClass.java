package com.frozyice.queuemanager;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class TempClass  implements Serializable {

    private int id;

    public TempClass(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    @Override
    public String toString() {
        return Integer.toString(id);
    }
}
