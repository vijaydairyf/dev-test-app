package com.devapp.devmain.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.devapp.devmain.services.BootReceiver;
import com.devapp.devmain.user.Util;
import com.devApp.R;

public class LauncherActivity extends AppCompatActivity {

    public static final String BOOT_COMPLETE = "bootComplete";
    boolean isBootComplete = false;
    Handler handler = new Handler();
    Runnable updateRunnable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        BootReceiver.onBootCompleteReceiver = new BootReceiver.OnBootCompleteReceiver() {
            @Override
            public void onBootComplete() {
                isBootComplete = true;
            }
        };


    }

    @Override
    protected void onResume() {
        super.onResume();
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (isBootComplete) {
                    handler.post(updateRunnable);
                }
            }
        }).start();

        updateRunnable = new Runnable() {
            @Override
            public void run() {
                startSplashActivity();
            }
        };


    }

    private void startSplashActivity() {

        Util.displayErrorToast("Boot completed successfully!", LauncherActivity.this);
        Intent intent = new Intent(LauncherActivity.this, SplashActivity.class);
        intent.putExtra(BOOT_COMPLETE, "From launcher Activity");

        startActivity(intent);
        finish();
    }

}
