package com.devapp.syncapp;

import java.util.UUID;

public interface SyncAppConstants {

    UUID BT_UUID = UUID.fromString("f5a3382f-4280-4707-8712-788aa023dd86");

    interface TRANSMISSION_FOR {
        int COLLECTION_UPDATE = 1;
        int CONFIGURATION_UPDATE = 2;
    }

    interface PREF {
        String PREF_NAME = "smart_lite";
    }


}
