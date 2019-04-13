package com.devapp.devmain.cloud;

import android.content.Context;
import android.content.Intent;

import com.devapp.devmain.devicemanager.DatabaseManager;
import com.devapp.devmain.httptasks.RateChartPullService;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.user.Util;

public class AfterLogInTasks {

    Context mContext;
    boolean isAuth;
    AmcuConfig amcuConfig;
    String server;


    public AfterLogInTasks(Context ctx, boolean isAuthenticated) {
        this.mContext = ctx;
        this.isAuth = isAuthenticated;
        amcuConfig = AmcuConfig.getInstance();
        server = amcuConfig.getURLHeader() + amcuConfig.getServer();


    }

    public void startTasks() {
        if (isAuth) {
                    /*
                    if (saveSession.getLogInFor() == Util.LOGINFORAPK) {
						APKManager apkManager = new APKManager(mContext);
						apkManager.getUpdatedApk();
					} else*/
            if (amcuConfig.getLogInFor() == Util.LOGINRATECHART) {
                Intent intent = new Intent(mContext, RateChartPullService.class);
                mContext.startService(intent);
            }

        } else {
            if (amcuConfig.getLogInFor() == Util.LOGINRATECHART) {

                DatabaseManager rateChartManager = new DatabaseManager(
                        mContext);
                rateChartManager.manageRateChart();

            } else if (amcuConfig.getLogInFor() == Util.LOGINFORAPK) {
                APKManager.apkDownloadInprogress = true;

            }
        }
    }
}
