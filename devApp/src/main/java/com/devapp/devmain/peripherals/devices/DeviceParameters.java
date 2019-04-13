package com.devapp.devmain.peripherals.devices;

/**
 * Created by xx on 12/2/18.
 */

public class DeviceParameters {

    private int baudRate;
    private int parity;
    private int stopBits;
    private int dataBits;

    public DeviceParameters() {

    }

    public DeviceParameters(int baudRate, int dataBits, int parity, int stopBits) {
        this.baudRate = baudRate;
        this.parity = parity;
        this.stopBits = stopBits;
        this.dataBits = dataBits;
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

    public int getStopBits() {
        return stopBits;
    }

    public void setStopBits(int stopBits) {
        this.stopBits = stopBits;
    }

    public int getDataBits() {
        return dataBits;
    }

    public void setDataBits(int dataBits) {
        this.dataBits = dataBits;
    }
}
