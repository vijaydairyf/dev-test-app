package com.devapp.devmain.usb;

import android.support.annotation.NonNull;

import com.hoho.android.usbserial.driver.UsbSerialDriver;

import java.io.Serializable;
import java.net.DatagramSocket;
import java.net.Socket;

public class DeviceEntity implements Serializable, Comparable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    public UsbSerialDriver driver;
    public String deviceName;
    public int portAddress;
    public String ipAddress;
    public String deviceType;
    public DatagramSocket deviceSocket;
    public Socket tcpSocket;
    public int deviceMode;

    @Override
    public int compareTo(@NonNull Object o) {
        DeviceEntity deviceEntity = (DeviceEntity) o;
        if (this.portAddress < deviceEntity.portAddress)
            return -1;
        else if (this.portAddress > deviceEntity.portAddress)
            return 1;
        else
            return 0;
    }

}