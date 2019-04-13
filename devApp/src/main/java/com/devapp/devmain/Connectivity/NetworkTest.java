package com.devapp.devmain.Connectivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.telephony.TelephonyManager;

import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.ServerAPI;
import com.devapp.devmain.user.Util;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Upendra on 5/10/2016.
 */
public class NetworkTest {


    AmcuConfig amcuConfig;
    boolean isAuthenticated;
    boolean isConnected;
    Handler myHandler;
    Runnable updateRunnable;
    int count = 0;
    private Context mContext;

    public NetworkTest(Context ctx) {
        this.mContext = ctx;
        amcuConfig = AmcuConfig.getInstance();

    }

    public String defaultHttpTest() {
        String response = null;
        String s = amcuConfig.getURLHeader() + amcuConfig.getServer().replaceAll(" ", "%20");
        Uri uri = Uri.parse(s);
        HttpPost httpPost = new HttpPost(uri.toString());
        final DefaultHttpClient httpClient = new DefaultHttpClient();
        try {
            HttpResponse httpResponse = httpClient.execute(httpPost);
            Util.displayErrorToast(httpResponse.getStatusLine().getStatusCode() + " "
                            + httpResponse.getStatusLine().getReasonPhrase()

                    , mContext);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }


    public String configurationTest() {

        String response = "No connection";

        try {
            URL url = new URL(amcuConfig.getURLHeader() + amcuConfig.getServer());
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            response = getTextFromResponseCode(urlConnection.getResponseCode());

        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }


    public String getTextFromResponseCode(int responseCode) {
//        if(responseCode<200)
//        {
//            return "Informational";
//        }

        if (responseCode == 200) {
            return "Login Success";
        }
//        else if(responseCode<300)
//        {
//            return "LogIn Success";
//        }
//        else if(responseCode<400)
//        {
//            return "Redirect";
//        }
        else if (responseCode == 401) {
            return "Invalid credentials, please check the login details";
        } else {
            return "Unable to connect to server, please try again";
        }
    }


    public void checkConnectivity() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    checkCredentials();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();
        updateRunnable = new Runnable() {

            @Override
            public void run() {
                if (isAuthenticated) {
                    //"Successfully logged In.";
                } else {
                    if (count < 2) {
                        count = count + 1;
                        checkConnectivity();
                    } else {
                        isAuthenticated = false;
                        if (!isConnected) {
                            //"Please check the connectivity!";
                        } else {
                            //"Log in failed!";
                        }
                    }
                }
            }
        };
    }


    public void checkCredentials() {

        if (ServerAPI.isNetworkAvailable(mContext)) {
            if (!isAuthenticated) {
                isConnected = true;
                isAuthenticated = ServerAPI.authenticateUser(
                        mContext,
                        amcuConfig.getDeviceID(),
                        amcuConfig.getDevicePassword(), Util.DEVICE_LOGIN,
                        amcuConfig.getURLHeader() + amcuConfig.getServer());
                myHandler.post(updateRunnable);

            } else if (isAuthenticated) {

                myHandler.post(updateRunnable);
            } else {
                isAuthenticated = false;
                myHandler.post(updateRunnable);
            }
        } else {
            isConnected = false;
        }

    }

    public boolean toCheckForMobileData() {
        boolean isWifiEnable = isConnectedWiFi(mContext);
        int isSimReadyState = checkSimState();

        return !isWifiEnable && (isSimReadyState == 4);
    }

    public boolean isConnectedWiFi(Context context) {

        NetworkInfo info = getNetworkInfo(context);
        return (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_WIFI);
    }

    public NetworkInfo getNetworkInfo(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }


    public int checkSimState() {

        TelephonyManager telMgr = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        int simState = telMgr.getSimState();
        switch (simState) {
            case TelephonyManager.SIM_STATE_ABSENT:
                simState = 0;
                break;
            case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
                simState = 1;
                break;

            case TelephonyManager.SIM_STATE_PIN_REQUIRED:
                simState = 2;
                break;
            case TelephonyManager.SIM_STATE_PUK_REQUIRED:
                simState = 3;
                break;
            case TelephonyManager.SIM_STATE_READY:
                simState = 4;
                break;
            case TelephonyManager.SIM_STATE_UNKNOWN:
                simState = 5;
                break;

        }
        return simState;
    }


}
