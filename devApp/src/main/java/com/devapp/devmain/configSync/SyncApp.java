package com.devapp.devmain.configSync;

/**
 * Created by u_pendra on 17/1/18.
 */

public interface SyncApp {


    String getJsonFromApi(String url);

    boolean setDataFromJson(String json);

}
