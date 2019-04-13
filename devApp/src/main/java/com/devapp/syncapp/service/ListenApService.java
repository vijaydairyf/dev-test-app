package com.devapp.syncapp.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;


public class ListenApService extends Service {
    public static boolean IS_RUNNING = false;

    public ListenApService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        IS_RUNNING = true;
      /*  CollectionRecordTransmitter transmitter = new CollectionRecordTransmitter(this);
        transmitter.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);*/
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        IS_RUNNING = false;
    }
}
