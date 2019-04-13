package com.devapp.devmain.helper;

import com.devapp.devmain.server.AmcuConfig;

import java.util.ArrayList;

/**
 * Created by u_pendra on 24/1/17.
 */

public class DeviceName {

    public static final String MILK_ANALYSER = "MA1";
    public static final String MA1 = "MA1";
    public static final String MA2 = "MA2";
    public static final String RDU = "RDU";
    public static final String WS = "WEIGHING MACHINE";
    public static final String PRINTER = "PRINTER";
    public static final String YELLOW = "YELLOW";
    public static final String RED = "RED";
    public static final String BLUE = "BLUE";
    public static final String GREEN = "GREEN";
    public static final String NO_CONNECTION = "NO CONNECTION";

    public static AmcuConfig amcuConfig = AmcuConfig.getInstance();
    public static String[] deviceNames = {MA1, RDU, WS, PRINTER};
    public static String[] modules = {YELLOW, BLUE, RED, GREEN};

    public static String[] setDeviceNames() {
        ArrayList<String> deviceList = new ArrayList<>();

        deviceList.add(amcuConfig.getKeyDevicePort1());
        deviceList.add(amcuConfig.getKeyDevicePort2());
        deviceList.add(amcuConfig.getKeyDevicePort3());
        deviceList.add(amcuConfig.getKeyDevicePort4());
        deviceList.add(amcuConfig.getKeyDevicePort5());

        int size = 0;
        for (String device : deviceList) {
            if (!device.equalsIgnoreCase(NO_CONNECTION))
                size++;
        }
        deviceNames = new String[size];
        int ct = 0;
        for (String device : deviceList) {
            if (!device.equalsIgnoreCase(NO_CONNECTION)) {
                deviceNames[ct++] = device;
            }
        }
        return deviceNames;
    }
}
