package com.devapp.devmain.user;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;

import com.devapp.devmain.server.AmcuConfig;
import com.devApp.R;


/**
 * Created by Upendra on 7/22/2015.
 */
public class RestartTabActivity extends Activity {


    Handler myHandler = new Handler();
    Runnable restartRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        try {
            AmcuConfig.getInstance().setBootCompleted(false);
            alertForShutDown();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onStart();
    }

    public void alertForShutDown() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                RestartTabActivity.this);
        // set title
        alertDialogBuilder.setTitle("DevApp upgrade alert!");
        // set dialog messa


        alertDialogBuilder
                .setMessage(getResources().getString(R.string.devAppUpgrad))
                .setCancelable(false);


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                myHandler.post(restartRunnable);
            }
        }).start();

        restartRunnable = new Runnable() {
            @Override
            public void run() {
                Util.restartTab(getApplicationContext());
            }
        };


        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }
}