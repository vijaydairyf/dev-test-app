package com.devapp.devmain.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;

import com.devapp.devmain.cloud.UpdateAPK;
import com.devapp.devmain.helper.OnStartTask;
import com.devapp.devmain.httptasks.PostFarmerRecords;
import com.devapp.devmain.main.LauncherActivity;
import com.devapp.devmain.main.SplashActivity;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.user.Util;
import com.devapp.devmain.util.ValidationHelper;
import com.devapp.devmain.util.logger.Log;

import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;

import static android.os.Looper.getMainLooper;

/**
 * Created by Upendra on 8/11/2015.
 */
public class NetworkReciever extends BroadcastReceiver {

    Handler networkHandler = new Handler();
    Runnable networkRunnable;
    private Context mContext;

    @Override
    public void onReceive(final Context context, Intent intent) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        mContext = context;
        Log.v("NetworkReceiver", "Inside network receiver");
        AmcuConfig amcuConfig = AmcuConfig.getInstance();
        amcuConfig.setBootCompleted(true);

        if (activeNetworkInfo != null) {
            if (AmcuConfig.getInstance().getFirstTimeConnection() &&
                    activeNetworkInfo.isConnected()) {
                AmcuConfig.getInstance().setFirstConnection(false);
                downLoadLogo(context);
                try {
                    boolean isRooted = Util.checkForRootedTab();
                    if (isRooted) {
                        try {

                            Thread obj = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Thread.sleep(3000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    Util.validateServerName(context);
                                    setIpTable();
                                    ValidationHelper validationHelper = new ValidationHelper();
                                    String ipAddress = validationHelper.getIpAddress(mContext);
                                    if (ipAddress != null) {
                                        licenseValidation();
                                        doOnStartTask();
                                    }

                                }
                            });
                            obj.start();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            AmcuConfig.getInstance().setFirstConnection(true);
        }
    }


    public void downLoadLogo(Context ctx) {
        boolean isDuplicateAPK = Util.checkForDuplicateDownLoadNetwork(ctx);
        if (isDuplicateAPK) {
            UpdateAPK updateApk;
            updateApk = new UpdateAPK();
            updateApk.setContextForCache(ctx, Util.TEST_URL_STELLAPPS);
        }
    }


    public void licenseValidation() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        AmcuConfig amcuConfig = AmcuConfig.getInstance();
        if (day == calendar.SUNDAY) {
            if (!amcuConfig.getSundayLicenseCheck()) {
                try {
                    final Handler handler = new Handler(getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //Do something after 100ms
                            AmcuConfig amcuConfig = AmcuConfig.getInstance();
                            SplashActivity splashActivity = new SplashActivity();
                            splashActivity.networkReceiverForLic = true;
                            splashActivity.networkReceiverForSundayLic = true;
                            splashActivity.networkReceiverLicAsyncTask(mContext);
                            if (!amcuConfig.getSundayLicenseCheck()) {
                                handler.postDelayed(this, 200000);
                            } else {
                                handler.removeCallbacks(this);
                            }

                        }
                    }, 30000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (amcuConfig.getFirstTimeLicenseEntry() && !amcuConfig.getonCreateDbCall()) {
            try {
                Handler mHandler = new Handler(getMainLooper());
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {

                        SplashActivity splashActivity = new SplashActivity();
                        splashActivity.networkReceiverForLic = true;
                        splashActivity.networkReceiverLicAsyncTask(mContext);
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (amcuConfig.getFirstTimeLicenseEntry()) {
            //TODO commented for testing purpose
        /*    Intent intent = new Intent(mContext, SplashActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
            mContext.startActivity(intent);
            System.exit(0);*/
        }
    }

    public boolean setIpTable() {
        boolean returnValue = false;

        ValidationHelper validationHelper = new ValidationHelper();
        String ipAddress = validationHelper.getIpAddress(mContext);


        if (ipAddress != null) {
            try {
                Util.doCmdsForIptables(mContext);
                returnValue = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (AmcuConfig.getInstance().getKeyCloudSupport()) {
            Util util = new Util();

            if (util.checkMobileNetwork(mContext)) {
                //Turning on and turing off the Mobile data
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

        return returnValue;
    }

    public void disableOrEnableMobileData(boolean reset) {


        try {
            Util.setMobileDataEnabled(reset, mContext);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Util.displayErrorToast(e.getMessage(), mContext);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            Util.displayErrorToast(e.getMessage(), mContext);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            Util.displayErrorToast(e.getMessage(), mContext);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            Util.displayErrorToast(e.getMessage(), mContext);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            Util.displayErrorToast(e.getMessage(), mContext);
        }
    }


    public void doOnStartTask() {
        OnStartTask onStartTask = new OnStartTask(mContext.getApplicationContext());
        onStartTask.doConfigurationTask();
        mContext.startService(new Intent(mContext, PostFarmerRecords.class));

    }

    private void startLauncherActivity(Context context) {
        try {
            Intent launcherIntent = new Intent(context, LauncherActivity.class);
            launcherIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(launcherIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
