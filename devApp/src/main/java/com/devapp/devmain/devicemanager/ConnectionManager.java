package com.devapp.devmain.devicemanager;

import android.content.Context;
import android.util.Log;

import com.devapp.devmain.entity.DPNEntity;
import com.devapp.devmain.httptasks.AuthenticationParameters;
import com.devapp.devmain.httptasks.CommunicationService;
import com.devapp.devmain.httptasks.HttpResponse;
import com.devapp.devmain.httptasks.IncompatibleProtocolException;
import com.devapp.devmain.httptasks.SSLContextCreationException;
import com.devapp.devmain.server.AmcuConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import static android.content.ContentValues.TAG;

/**
 * Created by u_pendra on 24/8/17.
 */

public class ConnectionManager {


    private static ConnectionManager mConnectionManager;
    private Context mContext;

    private ConnectionManager(Context mContext) {
        this.mContext = mContext;
    }

    public static ConnectionManager getInstance(Context context) {
        if (mConnectionManager == null) {
            mConnectionManager = new ConnectionManager(context);
        }

        return mConnectionManager;

    }


    public static HttpResponse getResponseFromURL(DPNEntity dpnEntity, Context context) {

        AmcuConfig amcuConfig = AmcuConfig.getInstance();
        AuthenticationParameters authParams =
                new AuthenticationParameters(null, amcuConfig.getDeviceID(),
                        amcuConfig.getDevicePassword());
        CommunicationService cs = null;
        try {
            cs = new CommunicationService(amcuConfig.getURLHeader() +
                    amcuConfig.getServer(), authParams);
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        } catch (SSLContextCreationException e) {
            Log.d(TAG, e.getMessage());
        }
        HttpResponse response = null;
        try {
            response = cs.doGet(dpnEntity.url);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IncompatibleProtocolException e) {
            e.printStackTrace();
        }
        return response;
    }

    public static String getJsonString(JSONObject jsonObject, String key, String defaultValue)
            throws JSONException {
        String returnValue = defaultValue;
        if (jsonObject != null && jsonObject.getString(key) != null) {
            returnValue = jsonObject.getString(key);
        }

        return returnValue;

    }


    public static int getJsonInteger(JSONObject jsonObject, String key, int defaultValue)
            throws JSONException {
        int returnValue = defaultValue;
        if (jsonObject != null) {
            returnValue = jsonObject.getInt(key);
        }

        return returnValue;

    }


}
