package com.devapp.devmain.license.deviceInfo;

/**
 * Created by yyy on 23/6/16.
 */
public class CpuInfo {

    private static CpuInfo cpuInfo = null;
    String processor;
    String serialNumber;

    private CpuInfo() {

    }

    public static CpuInfo getInstance() {
        if (cpuInfo == null) {
            synchronized (CpuInfo.class) {
                if (cpuInfo == null) {
                    cpuInfo = new CpuInfo();
                }
            }
        }
        return cpuInfo;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getProcessor() {
        return processor;
    }

    public void setProcessor(String processor) {
        this.processor = processor;
    }

}
