package com.hoho.android.usbserial.driver;

import android.hardware.usb.UsbDevice;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xx on 12/2/18.
 */

public class TVSSerialDriver implements UsbSerialDriver {
    UsbDevice mDevice;

    public TVSSerialDriver(UsbDevice device) {
        mDevice = device;
    }

    @Override
    public UsbDevice getDevice() {
        return mDevice;
    }

    @Override
    public List<UsbSerialPort> getPorts() {
        return null;
    }

    public static Map<Integer, int[]> getSupportedDevices() {
        final Map<Integer, int[]> supportedDevices = new LinkedHashMap<Integer, int[]>();
        supportedDevices.put(Integer.valueOf(UsbId.VENDOR_TVS),
                new int[]{UsbId.PRODUCT_TVS,});
        return supportedDevices;
    }
}
