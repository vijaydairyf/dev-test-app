package com.devapp.devmain.license.deviceInfo;

/**
 * Created by yyy on 23/6/16.
 */
public class BootImageInfo {

    private static BootImageInfo bootImageInfo = null;
    String bootAnimationZipSize;
    String bootLogoSize;
    String initLogoSize;

    private BootImageInfo() {

    }

    public static BootImageInfo getInstance() {
        if (bootImageInfo == null) {
            synchronized (BootImageInfo.class) {
                if (bootImageInfo == null) {
                    bootImageInfo = new BootImageInfo();
                }
            }
        }
        return bootImageInfo;
    }

    public String getBootAnimationZipSize() {
        return bootAnimationZipSize;
    }

    public void setBootAnimationZipSize(String bootAnimationZipSize) {
        this.bootAnimationZipSize = bootAnimationZipSize;
    }

    public String getBootLogoSize() {
        return bootLogoSize;
    }

    public void setBootLogoSize(String bootLogoSize) {
        this.bootLogoSize = bootLogoSize;
    }

    public String getInitLogoSize() {
        return initLogoSize;
    }

    public void setInitLogoSize(String initLogoSize) {
        this.initLogoSize = initLogoSize;
    }
}
