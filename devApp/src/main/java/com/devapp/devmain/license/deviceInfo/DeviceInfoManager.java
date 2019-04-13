package com.devapp.devmain.license.deviceInfo;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;

/**
 * Created by yyy on 23/6/16.
 */
public class DeviceInfoManager {

    private static Context mContext;
    private static DeviceInfoManager deviceInfoManager = null;
    BuildInfo buildInfo;
    BootImageInfo bootImageInfo;
    CpuInfo cpuInfo;
    KernalInfo kernalInfo;
    TelephoneInfo telephoneInfo;

    private DeviceInfoManager() {

    }

    public static DeviceInfoManager getInstance(Context context) {

        if (deviceInfoManager == null) {
            synchronized (DeviceInfoManager.class) {
                if (deviceInfoManager == null) {
                    deviceInfoManager = new DeviceInfoManager();
                    mContext = context;
                }
            }
        }
        return deviceInfoManager;
    }

    public void setAllData() {
        setBuildInfoData();
        setKernalInfoData();
        setTelephoneInfoData();
    }

    public void setBuildInfoData() {
        BuildInfo.getInstance().setAndroidVersion(Build.VERSION.RELEASE);
        BuildInfo.getInstance().setBoard(Build.BOARD);
        BuildInfo.getInstance().setBrand(Build.BRAND);
        BuildInfo.getInstance().setBuidID(Build.ID);
        BuildInfo.getInstance().setCpuAbi(Build.CPU_ABI);
        BuildInfo.getInstance().setCpuAbi2(Build.CPU_ABI2);
        BuildInfo.getInstance().setFingerprint(Build.FINGERPRINT);
        BuildInfo.getInstance().setHardwareSerialNumber(Build.SERIAL);
        BuildInfo.getInstance().setUser(Build.USER);
        BuildInfo.getInstance().setManufacture(Build.MANUFACTURER);
        BuildInfo.getInstance().setProduct(Build.PRODUCT);
        BuildInfo.getInstance().setSdkVersion(Build.VERSION.SDK);
        BuildInfo.getInstance().setModel(Build.MODEL);
        BuildInfo.getInstance().setHost(Build.HOST);
        BuildInfo.getInstance().setHardware(Build.HARDWARE);
    }

    public void setKernalInfoData() {
        KernalInfo.getInstance().setLinuxVersion();
        KernalInfo.getInstance().setInstallDate();
    }

    public void setTelephoneInfoData() {
        TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        TelephoneInfo.getInstance(mContext).setSoftwareVersion(telephonyManager.getDeviceSoftwareVersion());
        TelephoneInfo.getInstance(mContext).setIMEINumber(telephonyManager.getDeviceId());
        TelephoneInfo.getInstance(mContext).setNetworkCountryISO(telephonyManager.getNetworkCountryIso());
        TelephoneInfo.getInstance(mContext).setSubscriberID(telephonyManager.getSubscriberId());
        TelephoneInfo.getInstance(mContext).setSimCountryISO(telephonyManager.getSimCountryIso());
        TelephoneInfo.getInstance(mContext).setNetworkType(TelephoneInfo.getInstance(mContext).getNetworkClass(mContext));

    }

}
