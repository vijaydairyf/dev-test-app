package com.devapp.devmain.deviceinfo;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by u_pendra on 22/12/17.
 */


public class DeviceStatusPostEntity implements Serializable {
    public String centerId;
    public String deviceId;
    public ArrayList<DeviceStatusEntity> deviceStatusList;
}
