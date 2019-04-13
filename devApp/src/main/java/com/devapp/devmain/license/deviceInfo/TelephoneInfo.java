package com.devapp.devmain.license.deviceInfo;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

/**
 * Created by yyy on 23/6/16.
 */
public class TelephoneInfo {
    private static TelephoneInfo telephoneInfo = null;
    private static Context mContext = null;
    String imeiNumber;
    String subscriberID;
    String networkCountryISO;
    String simCountryISO;
    String softwareVersion;
    String networkType; // wifi or sim

    private TelephoneInfo() {

    }

    public static TelephoneInfo getInstance(Context context) {
        if (telephoneInfo == null) {
            synchronized (TelephoneInfo.class) {
                if (telephoneInfo == null) {
                    telephoneInfo = new TelephoneInfo();
                    mContext = context;
                }
            }
        }
        return telephoneInfo;
    }


    public String getNetworkCountryISO() {
        return networkCountryISO;
    }

    public void setNetworkCountryISO(String networkCountryISO) {
        this.networkCountryISO = networkCountryISO;
    }

    public String getSubscriberID() {
        return subscriberID;
    }

    public void setSubscriberID(String subscriberID) {
        this.subscriberID = subscriberID;
    }

    public String getSoftwareVersion() {
        return softwareVersion;
    }

    public void setSoftwareVersion(String softwareVersion) {
        this.softwareVersion = softwareVersion;
    }


    public String getSimCountryISO() {
        return simCountryISO;
    }

    public void setSimCountryISO(String simCountryISO) {
        this.simCountryISO = simCountryISO;
    }

    public String getNetworkType() {

        return networkType;
    }

    public void setNetworkType(String networkType) {
        this.networkType = networkType;
    }


    public String getImeiNumber() {
        return imeiNumber;
    }

    public void setIMEINumber(String imeiNumber) {
        this.imeiNumber = imeiNumber;
    }

    public String getNetworkClass(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null || !info.isConnected())
            return "NOT_CONNECTED";
        if (info.getType() == ConnectivityManager.TYPE_WIFI)
            return "WIFI_UDP";
        if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
            int networkType = info.getSubtype();
            switch (networkType) {
                case TelephonyManager.NETWORK_TYPE_GPRS:
                case TelephonyManager.NETWORK_TYPE_EDGE:
                case TelephonyManager.NETWORK_TYPE_CDMA:
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                case TelephonyManager.NETWORK_TYPE_IDEN: //api<8 : replace by 11
                    return "2G";
                case TelephonyManager.NETWORK_TYPE_UMTS:
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                case TelephonyManager.NETWORK_TYPE_HSPA:
                case TelephonyManager.NETWORK_TYPE_EVDO_B: //api<9 : replace by 14
                case TelephonyManager.NETWORK_TYPE_EHRPD:  //api<11 : replace by 12
                case TelephonyManager.NETWORK_TYPE_HSPAP:  //api<13 : replace by 15
                    return "3G";
                case TelephonyManager.NETWORK_TYPE_LTE:    //api<11 : replace by 13
                    return "4G";
                default:
                    return "UNKNOWNTYPE";
            }
        }
        return "UNKNOWNINTERNET";
    }
}
