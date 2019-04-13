package com.devapp.devmain.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;

import com.devApp.R;


/**
 * Created by Upendra on 7/22/2015.
 */
public class ManageSpaceActivity extends Activity {


    Handler myHandler = new Handler();
    Runnable restartRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    protected void onStart() {

        try {
            alertForShutDown();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onStart();
    }

    public void alertForShutDown() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                ManageSpaceActivity.this);
        // set title
        alertDialogBuilder.setTitle("smartAmcu");
        // set dialog messa


        alertDialogBuilder
                .setMessage(getResources().getString(R.string.devAppMessage))
                .setCancelable(false);



        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                myHandler.post(restartRunnable);
            }
        }).start();

        restartRunnable = new Runnable() {
            @Override
            public void run() {
                finish();
            }
        };


        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }
}