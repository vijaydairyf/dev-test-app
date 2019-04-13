package com.devapp.devmain.configSync;

import android.content.Context;

/**
 * Created by u_pendra on 17/1/18.
 */

public class SyncFarmerRepo implements SyncApp {


    private static SyncFarmerRepo syncFarmerRepo;
    private Context mContext;


    private SyncFarmerRepo(Context context) {
        this.mContext = context;

    }

    public static SyncFarmerRepo getInstance(Context context) {

        if (syncFarmerRepo == null)
            syncFarmerRepo = new SyncFarmerRepo(context);

        return syncFarmerRepo;
    }


    @Override
    public String getJsonFromApi(String url) {
        return null;
    }

    @Override
    public boolean setDataFromJson(String json) {
        return false;
    }
}
