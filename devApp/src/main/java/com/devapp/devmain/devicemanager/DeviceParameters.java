package com.devapp.devmain.devicemanager;

/**
 * Created by x on 5/1/18.
 */

public class DeviceParameters {
    String make;
    int baudRate;
    int parity;
    int stopBit;
    int dataBits;

    public DeviceParameters(String make, int baudRate, int parity, int stopBit, int dataBits) {
        this.make = make;
        this.baudRate = baudRate;
        this.parity = parity;
        this.stopBit = stopBit;
        this.dataBits = dataBits;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public int getBaudRate() {
        return baudRate;
    }

    public void setBaudRate(int baudRate) {
        this.baudRate = baudRate;
    }

    public int getParity() {
        return parity;
    }

    public void setParity(int parity) {
        this.parity = parity;
    }

    public int getStopBit() {
        return stopBit;
    }

    public void setStopBit(int stopBit) {
        this.stopBit = stopBit;
    }

    public int getDataBits() {
        return dataBits;
    }

    public void setDataBits(int dataBits) {
        this.dataBits = dataBits;
    }
}
