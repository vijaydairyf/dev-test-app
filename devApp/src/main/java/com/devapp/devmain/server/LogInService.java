package com.devapp.devmain.server;

import android.app.IntentService;
import android.content.Intent;

import com.devapp.devmain.cloud.APKManager;
import com.devapp.devmain.cloud.AfterLogInTasks;
import com.devapp.devmain.user.Util;

public class LogInService extends IntentService {

    public static boolean isAuthenticated;
    int count = 0;
    AmcuConfig amcuConfig;
    String server;
    AfterLogInTasks alt;

    public LogInService() {
        super("LogInService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        amcuConfig = AmcuConfig.getInstance();
        server = amcuConfig.getURLHeader() + amcuConfig.getServer();

        //To update the rate chart before log in

        isAuthenticated = false;
        alt = new AfterLogInTasks(getApplicationContext(),
                isAuthenticated);
        alt.startTasks();

        if (Util.isNetworkAvailable(getApplicationContext())) {
            if (amcuConfig.getLogInFor() == Util.LOGINFORAPK) {
                APKManager apkManager = new APKManager(getApplicationContext());
                apkManager.getUpdatedApk();

            } else {
                LogIn();
            }
        } else {
            Util.displayErrorToast("Please check the network connectivity!", getApplicationContext());
            isAuthenticated = false;
        }
    }

    public void LogIn() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                LogInUser();
                if (isAuthenticated) {
                    alt = new AfterLogInTasks(getApplicationContext(),
                            isAuthenticated);
                    alt.startTasks();
                } else {
                    if (count < 2) {
                        count = count + 1;
                        LogIn();

                    } else {
                        isAuthenticated = false;
                        alt = new AfterLogInTasks(getApplicationContext(),
                                isAuthenticated);
                        alt.startTasks();
                    }
                }
            }
        }).start();
    }

    public void LogInUser() {
        if (ServerAPI.isNetworkAvailable(getApplicationContext())
                && !isAuthenticated) {
            isAuthenticated = ServerAPI.authenticateUser(getApplicationContext(),
                    amcuConfig.getDeviceID(), amcuConfig.getDevicePassword(),
                    Util.DEVICE_LOGIN, server);
        } else if (ServerAPI.isNetworkAvailable(getApplicationContext())
                && isAuthenticated) {
            isAuthenticated = true;
        } else {
            isAuthenticated = false;

        }
    }


}