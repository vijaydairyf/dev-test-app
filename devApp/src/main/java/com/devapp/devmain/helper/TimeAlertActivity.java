package com.devapp.devmain.helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextClock;
import android.widget.TextView;

import com.devapp.devmain.entity.ReportEntity;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.user.Util;
import com.devapp.smartcc.entityandconstants.SmartCCUtil;
import com.devApp.R;

/**
 * Created by u_pendra on 12/4/17.
 */

public class TimeAlertActivity extends Activity {

    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        alertForTimeChange();
    }


    public void alertForTimeChange() {
        final SmartCCUtil smartCCUtil = new SmartCCUtil(TimeAlertActivity.this);
        AlertDialog.Builder builder = new AlertDialog.Builder(TimeAlertActivity.this);
        LayoutInflater layoutInflater = LayoutInflater.from(TimeAlertActivity.this);


        View view = layoutInflater.inflate(R.layout.alert_time_change, null);

        TextClock tvCurrentTime;
        LinearLayout linearLayout;
        Button btnSetTime, btnCancel;
        CardView cardCurrentDate1, cardCollDate, cardButton;
        TextView tvCurrentDate, tvLastCollDate, tvLastCollectionTime;

        cardCurrentDate1 = (android.support.v7.widget.CardView) view.findViewById(R.id.cardCurrentDate1);
        cardCollDate = (android.support.v7.widget.CardView) view.findViewById(R.id.cardCollDate);
        cardButton = (android.support.v7.widget.CardView) view.findViewById(R.id.cardButton);
        tvCurrentDate = (TextView) view.findViewById(R.id.tvCurrentDate);
        tvCurrentTime = (TextClock) view.findViewById(R.id.tvCurrentTime);
        linearLayout = (LinearLayout) view.findViewById(R.id.linearLayout);

        tvLastCollDate = (TextView) view.findViewById(R.id.tvCollectionDate);
        tvLastCollectionTime = (TextView) view.findViewById(R.id.tvCollectionTime);

        tvCurrentDate.setText(new SmartCCUtil(TimeAlertActivity.this).getReportDate(0));


        btnSetTime = (Button) view.findViewById(R.id.btnSetTime);
        btnCancel = (Button) view.findViewById(R.id.btnCancel);

        ReportEntity reportEntity = DatabaseHandler.getDatabaseInstance().
                getReportEntity(DatabaseEntity.getQueryForLatestRecord());

        if (reportEntity != null) {
            tvLastCollDate.setText(reportEntity.postDate);
            tvLastCollectionTime.setText(reportEntity.time);
        }

        btnSetTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Util.restartTab(mContext);
                if (Util.isNetworkAvailable(TimeAlertActivity.this)) {
                    try {
                        smartCCUtil.checkForNtpTime();
                    } catch (Exception e) {
                        e.printStackTrace();
                        startActivity(new Intent(Settings.ACTION_DATE_SETTINGS));
                    }
                } else {
                    startActivity(new Intent(Settings.ACTION_DATE_SETTINGS));
                }

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Util.restartApp(TimeAlertActivity.this);
                alertDialog.dismiss();
            }
        });

        builder.setView(view);
        alertDialog = builder.create();
        alertDialog.show();
        alertDialog.setCancelable(false);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(alertDialog.getWindow().getAttributes());
        lp.width = 650;
        lp.height = 450;
        lp.gravity = Gravity.CENTER | Gravity.CENTER;
        alertDialog.getWindow().setAttributes(lp);

    }

}
