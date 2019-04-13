package com.devapp.devmain.services;


import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.devapp.devmain.cloud.APKManager;
import com.devapp.devmain.cloud.UpdateAPK;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.user.Util;
import com.devApp.R;

public class DownloadManagerReceiver extends BroadcastReceiver {

    public long id;
    AmcuConfig amcuConfig;
    DownloadManager mDownloadManager;
    long downloadId;
    Runnable updateRunnable;
    Handler myHandler = new Handler();

    public DownloadManagerReceiver() {

    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        String action = intent.getAction();
        amcuConfig = AmcuConfig.getInstance();

        if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
            downloadId = intent.getLongExtra(
                    DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(downloadId);
            mDownloadManager = (DownloadManager) context
                    .getSystemService(Context.DOWNLOAD_SERVICE);
            Cursor c = mDownloadManager.query(query);
            if (c.moveToFirst()) {

                int columnIndex = c
                        .getColumnIndex(DownloadManager.COLUMN_STATUS);
                APKManager.apkDownloadInprogress = false;
                // if completed successfully
                if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)) {

                    if (downloadId != amcuConfig.getDownloadId())
                        return;

                    amcuConfig.setKeySetDownloadedApkPath(mDownloadManager
                            .getUriForDownloadedFile(downloadId).getPath());

                    Toast toast = Toast.makeText(
                            context,
                            "Downloading of smartAmcu update just finished",
                            Toast.LENGTH_LONG);
                    toast.show();
                    amcuConfig.setDownloadId(0);
                    Util.APK_URI = null;
                    amcuConfig.setAPKUri(null);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {

                                Util.displayErrorToast(context.getResources().getString(R.string.installed_message), context);

                                UpdateAPK.installDownload(mDownloadManager
                                        .getUriForDownloadedFile(downloadId).getPath(), context);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }).start();

                    // here you get file path so you can move
                    // it to other location if you want


                    // notify your app that download was completed
                    // with local broadcast receiver
                    Intent localReceiver = new Intent();
                    localReceiver.putExtra("ID", id);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(
                            localReceiver);
                } else if (DownloadManager.STATUS_FAILED == c
                        .getInt(columnIndex)) {
                    Toast.makeText(context, "Failed to download APK.",
                            Toast.LENGTH_LONG).show();
                    mDownloadManager.remove(amcuConfig.getDownloadId());
                    amcuConfig.setDownloadId(0);
                    Util.APK_URI = null;
                    amcuConfig.setAPKUri(null);
                    //Re attempting for fresh download.
//                    UpdateAPK updateApk = new UpdateAPK();
//                    updateApk.setContext(context, Util.updatedAPKURI);
                }
            }
        }
    }
}
