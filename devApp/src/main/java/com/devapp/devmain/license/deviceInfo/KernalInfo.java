package com.devapp.devmain.license.deviceInfo;

/**
 * Created by yyy on 23/6/16.
 */
public class KernalInfo {

    private static KernalInfo kernalInfo = null;
    String linuxVersion;
    String installDate;

    private KernalInfo() {

    }

    public static KernalInfo getInstance() {
        if (kernalInfo == null) {
            synchronized (KernalInfo.class) {
                if (kernalInfo == null) {
                    kernalInfo = new KernalInfo();
                }
            }

        }
        return kernalInfo;
    }

    public String getInstallDate() {
        return installDate;
    }

    public void setInstallDate() {
        this.installDate = KernalInfoParser.getVersionTime();
    }

    public String getLinuxVersion() {
        return linuxVersion;
    }

    public void setLinuxVersion() {
        this.linuxVersion = KernalInfoParser.getLinuxVersion();
    }

}
