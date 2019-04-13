package com.devapp.devmain.license.deviceInfo;

/**
 * Created by yyy on 23/6/16.
 */
public class BuildInfo {

    private static BuildInfo buildInfo = null;
    String manufacture;
    String product;
    String cpuAbi;
    String cpuAbi2;
    String brand;
    String sdkVersion;
    String hardwareSerialNumber;
    String buidID;
    String model;
    String user;
    String board;
    String host;
    String fingerprint;
    String androidVersion;
    String hardware;

    private BuildInfo() {

    }

    public static BuildInfo getInstance() {
        if (buildInfo == null) {
            synchronized (BuildInfo.class) {
                if (buildInfo == null) {
                    buildInfo = new BuildInfo();
                }
            }
        }
        return buildInfo;
    }

    public String getHardware() {
        return hardware;
    }

    public void setHardware(String hardware) {
        this.hardware = hardware;
    }

    public String getAndroidVersion() {
        return androidVersion;
    }

    public void setAndroidVersion(String androidVersion) {
        this.androidVersion = androidVersion;
    }

    public String getBoard() {
        return board;
    }

    public void setBoard(String board) {
        this.board = board;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getBuidID() {
        return buidID;
    }

    public void setBuidID(String buidID) {
        this.buidID = buidID;
    }

    public String getCpuAbi2() {
        return cpuAbi2;
    }

    public void setCpuAbi2(String cpuAbi2) {
        this.cpuAbi2 = cpuAbi2;
    }

    public String getCpuAbi() {
        return cpuAbi;
    }

    public void setCpuAbi(String cpuAbi) {
        this.cpuAbi = cpuAbi;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }

    public String getHardwareSerialNumber() {
        return hardwareSerialNumber;
    }

    public void setHardwareSerialNumber(String hardwareSerialNumber) {
        this.hardwareSerialNumber = hardwareSerialNumber;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getManufacture() {
        return manufacture;
    }

    public void setManufacture(String manufacture) {
        this.manufacture = manufacture;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getSdkVersion() {
        return sdkVersion;
    }

    public void setSdkVersion(String sdkVersion) {
        this.sdkVersion = sdkVersion;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }


}
