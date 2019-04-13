package com.devapp.devmain.dao;

/**
 * Created by x on 24/5/18.
 */

public class Key {
    String keyName;
    int filterType;

    public Key(String keyName, int filterType) {
        this.keyName = keyName;
        this.filterType = filterType;
    }

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public int getFilterType() {
        return filterType;
    }

    public void setFilterType(int filterType) {
        this.filterType = filterType;
    }
}
