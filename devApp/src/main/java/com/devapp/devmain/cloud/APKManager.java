package com.devapp.devmain.cloud;

import android.app.DownloadManager;
import android.content.Context;
import android.util.Log;

import com.devapp.devmain.httptasks.AuthenticationParameters;
import com.devapp.devmain.httptasks.CommunicationService;
import com.devapp.devmain.httptasks.HttpResponse;
import com.devapp.devmain.httptasks.IncompatibleProtocolException;
import com.devapp.devmain.httptasks.SSLContextCreationException;
import com.devapp.devmain.json.JSONParser;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.user.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class APKManager {
    private static final String TAG = "APK_MANAGER";
    public static boolean apkDownloadInprogress = false;
    static JSONParser jParser;
    AmcuConfig amcuConfig;
    int count = 0;
    String server;
    String url;
    JSONObject json;
    private Context mContext;

    public APKManager(Context ctx) {
        this.mContext = ctx;

        amcuConfig = AmcuConfig.getInstance();
        jParser = new JSONParser();
        server = amcuConfig.getURLHeader() + amcuConfig.getServer();

    }

    public void getUpdatedApk() {

        int versionCode = Util.getVersionCode(mContext);

        url = this.amcuConfig.getURLHeader() + this.amcuConfig.getServer() + "/amcu/apk/updatecheck?curVersion="
                + versionCode;
        System.out.println("Sent ulr " + url.toString());


        AmcuConfig amcuConfig = AmcuConfig.getInstance();
        AuthenticationParameters authParams =
                new AuthenticationParameters(null, amcuConfig.getDeviceID(), amcuConfig.getDevicePassword());

        CommunicationService cs = null;

        Log.d(TAG, "-------------- Checking for APK -----------");
        try {
            cs = new CommunicationService(amcuConfig.getURLHeader() + amcuConfig.getServer(), authParams);
        } catch (IOException e) {
            APKManager.apkDownloadInprogress = false;
            if (Util.isCheckForUpdate && !Util.isOperator(mContext)) {
                Util.displayErrorToast("LogIn failed for server!", mContext);
            }
            Log.d(TAG, e.getMessage());
            return;
        } catch (SSLContextCreationException e) {
            APKManager.apkDownloadInprogress = false;
            if (Util.isCheckForUpdate && !Util.isOperator(mContext)) {
                Util.displayErrorToast("LogIn failed for server!", mContext);
            }

            Log.d(TAG, e.getMessage());
            return;
        }

        HttpResponse response = null;
        try {
            response = cs.doGet("/amcu/apk/updatecheck?curVersion=" + versionCode);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IncompatibleProtocolException e) {
            e.printStackTrace();
        }

        String responseBody = null;
        if (response != null) {
            responseBody = response.getResponseBody();
            try {
                json = new JSONObject(responseBody);
            } catch (NullPointerException e1) {

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            APKManager.apkDownloadInprogress = false;
            if (Util.isCheckForUpdate && !Util.isOperator(mContext)) {
                Util.displayErrorToast("SmartAmcu update is not available!", mContext);
            }
            return;
        }

        if (json != null) {
            // To do get URL form JSON
            try {
                String uri = json.getString("uri");
                String version = json.getString("version");
                if (uri != null && !uri.equalsIgnoreCase("null") && (version != null && !version.equalsIgnoreCase("null"))) {
                    String url = "/amcu" + uri;
                    Util.APK_URI = url;
                    Util.APK_DOWNLODED_VERSION = Integer.parseInt(version);
                    updateAPK(Util.APK_URI, Util.APK_DOWNLODED_VERSION);
                } else {
                    APKManager.apkDownloadInprogress = false;
                    if (Util.isCheckForUpdate && !Util.isOperator(mContext)) {
                        Util.displayErrorToast("SmartAmcu update is not available!", mContext);
                    }
                    return;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                APKManager.apkDownloadInprogress = false;
                if (Util.isCheckForUpdate && !Util.isOperator(mContext)) {
                    Util.displayErrorToast("SmartAmcu update is not available!", mContext);
                }
                return;
            } catch (NumberFormatException ne) {
                ne.printStackTrace();
                APKManager.apkDownloadInprogress = false;
                if (Util.isCheckForUpdate && !Util.isOperator(mContext)) {
                    Util.displayErrorToast("Invalid SmartAmcu update!", mContext);
                }
                return;
            }

        } else {
            APKManager.apkDownloadInprogress = false;
            if (Util.isCheckForUpdate && !Util.isOperator(mContext)) {
                Util.displayErrorToast("SmartAmcu update is not available!", mContext);
            }

            return;
        }
    }

    public void updateAPK(String urlUpload, int version) {

        if (Util.checkForAPKValidation(version, mContext)) {
            com.devapp.devmain.cloud.UpdateAPK updateApk;
            updateApk = new com.devapp.devmain.cloud.UpdateAPK();
            updateApk.setContext(mContext, urlUpload);
        } else {

            Util.APK_DOWNLODED_VERSION = 0;
            amcuConfig.setDownloadId(0);
            Util.APK_URI = null;
            amcuConfig.setAPKUri(null);

            try {
                DownloadManager mDownloadManager = (DownloadManager) mContext
                        .getSystemService(Context.DOWNLOAD_SERVICE);
                mDownloadManager.remove(amcuConfig.getDownloadId());
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
