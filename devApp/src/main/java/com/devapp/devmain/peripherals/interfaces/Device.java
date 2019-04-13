package com.devapp.devmain.peripherals.interfaces;

import com.devapp.devmain.peripherals.devices.DeviceParameters;
import com.devapp.devmain.usb.DeviceEntity;

/**
 * Created by xx on 24/1/18.
 */

public interface Device {
    void read();

    void unregisterObserver();

    void closeConnection();

    void write(String msg);

    void write(byte[] msg);

    void writeAsync(byte[] msg);

    void setParameters(DeviceParameters parameters);

    void registerObserver(DataObserver dataObserver);

    void registerListener(WriteDataListener dataListener);

    DeviceEntity getDeviceEntity();

    DeviceParameters getDeviceParams();

    interface ErrorListener {
        void onError(Exception e, DeviceEntity deviceEntity);

        void onError(String errorMessage, DeviceEntity deviceEntity);
    }
}
