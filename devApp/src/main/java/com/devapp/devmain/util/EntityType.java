package com.devapp.devmain.util;

/**
 * Created by u_pendra on 7/8/18.
 */

public interface EntityType {

    String RATE_CHART = "RATE_CHART";
    String FARMER = "FARMER";
    String USER = "USER";
    String CONFIGURATION = "CONFIGURATION";
    String COLLECTION_CENTER = "COLLECTION_CENTER";
    String APK = "APK";


    interface Mode {
        String ONLINE = "ONLINE";
        String OFFLINE = "OFFLINE";
    }


}
