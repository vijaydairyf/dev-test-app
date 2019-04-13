package com.devapp.devmain.peripherals.interfaces;

import com.devapp.devmain.usb.DeviceEntity;

import java.util.ArrayList;

/**
 * Created by xx on 24/1/18.
 * DeviceManager will handle both the probers and activity can access only the devicemanager
 * Only 2 types of IDevice - wireddevice and wirelessdevice
 * MilkAnalyzerManager will contain a device type. (same for WS, rdu and printer)
 * MilkcollectionActivity will register observer for MaManager/WsManager.
 * MAManager/WsManager will notify the changes to milkcollectionactivity after parsing the specific ma type of data.
 * MaManager/WsManager will return an MaEntity/WsEntity to milkcollectionactivity
 */

public interface Prober {
    void startProbing(String deviceName);

    void stopProbing();

    //    HashMap<String, String> getDevices();
    ArrayList<DeviceEntity> getDevices();
}
