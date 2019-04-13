package com.devapp.devmain.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.devapp.devmain.agentfarmersplit.AppConstants;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.user.Util;
import com.devapp.devmain.util.ValidationHelper;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by u_pendra on 17/5/17.
 */

public class NetworkCheck extends IntentService {


    private static final String TAG = "NetworkCheck";

    Handler networkHandler = new Handler();
    Runnable networkRunnable;

    public NetworkCheck() {
        super("NetworkCheck");
    }

    public static String getTaskName() {
        return TAG;
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        Log.v(TAG, "Starting network check service");

        ConnectivityManager connectivityManager = (ConnectivityManager)
                this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        TelephonyManager telMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        int simState = telMgr.getSimState();
        boolean isAirplaneModeEnable = Util.isAirplaneModeOn(getApplicationContext());
        boolean isMoblieDataEnable = Util.mobileDataStatus(getApplicationContext());

        if (isAirplaneModeEnable) {
            Util.disableOrEnableAirplaneMode(false);
        } else if (simState == TelephonyManager.SIM_STATE_ABSENT) {
            //Do nothing
        } else if (simState == TelephonyManager.SIM_STATE_UNKNOWN) {
            Util.disableOrEnableAirplaneMode(false);
        } else if (simState == TelephonyManager.SIM_STATE_READY && !isMoblieDataEnable) {
            disableOrEnableMobileData(true);
        } else if (simState == TelephonyManager.SIM_STATE_READY && isMoblieDataEnable) {
            ValidationHelper validationHelper = new ValidationHelper();
            String ipAddress = validationHelper.getIpAddressFromServer(AppConstants.DIG_DNS);
            if (ipAddress == null && AmcuConfig.getInstance().getKeyCloudSupport()) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        disableOrEnableMobileData(false);
                        networkHandler.postDelayed(networkRunnable, 30000);
                    }
                }).start();

                networkRunnable = new Runnable() {
                    @Override
                    public void run() {
                        disableOrEnableMobileData(true);

                    }
                };
            }

        }
       /* if (activeNetworkInfo != null) {
            CheckValidation checkValidation = new CheckValidation();
            String ipAddress = checkValidation.getIpAddress(getApplicationContext());

            if (ipAddress == null && SaveSession.getInstance().getKeyCloudSupport()) {
                Util util = new Util();

                if (util.checkMobileNetwork(getApplicationContext())) {
                    //Turning on and turing off the mobile data
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            disableOrEnableMobileData(false);
                            networkHandler.postDelayed(networkRunnable, 15000);
                        }
                    }).start();

                    networkRunnable = new Runnable() {
                        @Override
                        public void run() {
                            disableOrEnableMobileData(true);

                        }
                    };

                }
            }
        }*/
    }

    public void disableOrEnableMobileData(boolean reset) {


        try {
            Util.setMobileDataEnabled(reset, getApplicationContext());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Util.displayErrorToast(e.getMessage(), getApplicationContext());
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            Util.displayErrorToast(e.getMessage(), getApplicationContext());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            Util.displayErrorToast(e.getMessage(), getApplicationContext());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            Util.displayErrorToast(e.getMessage(), getApplicationContext());
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            Util.displayErrorToast(e.getMessage(), getApplicationContext());
        }
    }


}
