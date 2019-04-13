package com.devapp.devmain.milkline;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextClock;
import android.widget.TextView;

import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.server.SessionManager;
import com.devapp.smartcc.entityandconstants.SmartCCUtil;
import com.devApp.R;


/**
 * Created by u_pendra on 21/12/16.
 */


public class SupervisorActivity extends AppCompatActivity implements View.OnClickListener {


    private android.support.v7.widget.CardView cardRecords, cardEnterData, cardLogout;
    private TextView textUser, textDate, tvEnterTankerDetails, tvTankerRecords, tvLogout;
    private TextClock textClock;
    private RelativeLayout rlEnterTankerData, rlTankerRecords, rlLogout;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supervisor);
        initializeView();
        purgeOldTankerRecords();


    }

    public void purgeOldTankerRecords() {
        TankerDatabase tankerDatabase =
                DatabaseHandler.getDatabaseInstance().getTankerDatabase();
        tankerDatabase.deleteOnYearOldTankerRecords();

    }

    public void initializeView() {
        cardRecords = (android.support.v7.widget.CardView) findViewById(R.id.cardRecords);
        cardEnterData = (android.support.v7.widget.CardView) findViewById(R.id.cardEnterData);
        cardLogout = (android.support.v7.widget.CardView) findViewById(R.id.cardLogout);

        textUser = (TextView) findViewById(R.id.textUser);
        textDate = (TextView) findViewById(R.id.textDate);
        tvEnterTankerDetails = (TextView) findViewById(R.id.tvEnterTankerDetails);
        tvTankerRecords = (TextView) findViewById(R.id.tvTankerRecords);
        tvLogout = (TextView) findViewById(R.id.tvLogout);
        textClock = (TextClock) findViewById(R.id.textClock);
        rlEnterTankerData = (RelativeLayout) findViewById(R.id.rlEnterTankerData);
        rlTankerRecords = (RelativeLayout) findViewById(R.id.rlTankerRecords);
        rlLogout = (RelativeLayout) findViewById(R.id.rlLogout);


        cardEnterData.requestFocus();
        rlEnterTankerData.setBackgroundResource(R.color.focused);

        cardRecords.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean isFocus) {

                if (isFocus) {
                    rlTankerRecords.setBackgroundResource(R.color.focused);
                    rlEnterTankerData.setBackgroundResource(R.drawable.btn_bg_normal_positive);
                    rlLogout.setBackgroundResource(R.drawable.btn_bg_normal_positive);

                }

            }
        });

        cardEnterData.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean isFocus) {

                if (isFocus) {
                    rlEnterTankerData.setBackgroundResource(R.color.focused);
                    rlTankerRecords.setBackgroundResource(R.drawable.btn_bg_normal_positive);
                    rlLogout.setBackgroundResource(R.drawable.btn_bg_normal_positive);

                }

            }
        });


        cardLogout.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean isFocus) {

                if (isFocus) {
                    rlLogout.setBackgroundResource(R.color.focused);
                    rlEnterTankerData.setBackgroundResource(R.drawable.btn_bg_normal_positive);
                    rlTankerRecords.setBackgroundResource(R.drawable.btn_bg_normal_positive);

                }

            }
        });

        SmartCCUtil smartCCUtil = new SmartCCUtil(this);
        textDate.setText(smartCCUtil.getReportDate(0));
        textUser.setText(new SessionManager(this).getUserId());

        cardEnterData.setOnClickListener(this);
        cardLogout.setOnClickListener(this);
        cardRecords.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {

        if (view == cardEnterData) {
            startActivity(new Intent(SupervisorActivity.this, EnterTankerDetails.class));
        } else if (view == cardRecords) {
            startActivity(new Intent(SupervisorActivity.this, AllTankerList.class));
        } else if (view == cardLogout) {
            new SessionManager(SupervisorActivity.this).logoutUser();
            finish();
        }

    }
}
