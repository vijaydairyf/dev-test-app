package com.devapp.devmain.configSync;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.devApp.R;

import java.net.URL;


/**
 * Created by u_pendra on 17/1/18.
 * <p>
 * This class is designed for Farmer,Configuration, Rate chart sync
 */


class ConfigSyncActivity extends AppCompatActivity implements View.OnClickListener {


    TextView tvFarmerSync, tvRateChartSync, tvConfigSync, tvHeader;
    SyncFarmerRepo syncFarmerRepo;
    URL url;

    ProgressBar farmerProgressBar, rateChartProgressBar, configProgressBar;
    ImageView ivFarmerStatus, ivRateChartStatus, ivConfigStatus;

    CoordinatorLayout coordinatorLayout;
    ObjectAnimator anim;
    ImageView ivStatus;
    private int SYNCFARMER = 1;
    private int SYNCRATE = 2;
    private int SYNCCONFIG = 3;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_configsync);

        tvFarmerSync = (TextView) findViewById(R.id.tvFarmer);
//        tvConfigSync = (TextView) findViewById(R.id.tvConfigurationSync);
//        tvRateChartSync = (TextView) findViewById(R.id.tvRateChartSync);
        tvHeader = (TextView) findViewById(R.id.tvHeader);

        farmerProgressBar = (ProgressBar) findViewById(R.id.farmer_progress_bar);
        configProgressBar = (ProgressBar) findViewById(R.id.config_progress_bar);
        rateChartProgressBar = (ProgressBar) findViewById(R.id.ratechart_progress_bar);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinateLayout);

        ivFarmerStatus = (ImageView) findViewById(R.id.ivFarmerStatus);
        ivRateChartStatus = (ImageView) findViewById(R.id.ivRatechartStatus);
        ivConfigStatus = (ImageView) findViewById(R.id.ivConfigurationStatus);

        syncFarmerRepo = SyncFarmerRepo.getInstance(this);

        Snackbar.make(coordinatorLayout, "Config sync screen", Snackbar.LENGTH_LONG).show();


    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

        }

    }


    public void getAndSetUpdate(final URL url, final int APPSYNC) {
        new AsyncTask<URL, Integer, Long>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Long doInBackground(URL... urls) {

                if (APPSYNC == SYNCFARMER) {
                    syncFarmerRepo.getJsonFromApi(String.valueOf(url));
                }

                return null;
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
            }

            @Override
            protected void onPostExecute(Long aLong) {
                super.onPostExecute(aLong);
            }
        }.execute(url);

    }


    /**
     * On every update start progress bar and show the status
     *
     * @param progressBar
     * @param ivStatus
     */
    private void startProgressBar(ProgressBar progressBar, ImageView ivStatus) {

        anim = ObjectAnimator.ofInt(progressBar, "progress", 0, 70);
        anim.setDuration(15 * 1000);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.start();
        anim.setRepeatMode(ValueAnimator.RESTART);
        ivStatus.setVisibility(View.VISIBLE);
    }


}
