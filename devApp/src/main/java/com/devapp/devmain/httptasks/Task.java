package com.devapp.devmain.httptasks;

import android.app.IntentService;

import java.io.Serializable;

/**
 * Created by xxx on 10/5/15.
 */
public class Task implements Serializable {


    private String name;
    private int code;
    private int type;
    private long delay;
    private long interval;
    private Class<? extends IntentService> service;


    Task(String name, int code, int type, long delay, long interval,
         Class<? extends IntentService> service) {
        this.name = name;
        this.code = code;
        this.type = type;
        this.delay = delay;
        this.interval = interval;
        this.service = service;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getDelay() {
        return delay;
    }

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public Class<? extends IntentService> getService() {
        return service;
    }

    public void setService(Class<? extends IntentService> service) {
        this.service = service;
    }
}