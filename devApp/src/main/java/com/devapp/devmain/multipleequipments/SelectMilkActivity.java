package com.devapp.devmain.multipleequipments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.devapp.devmain.macollection.MultipleMilkAnalyser;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.SessionManager;
import com.devApp.R;


/**
 * Created by Upendra on 6/3/2016.
 */
public class SelectMilkActivity extends Activity {


    String selectMilkType = "COW";
    Button btnCancel, btnSubmita, btnCow, btnBuffalo, btnMixed;
    TextView tvHeader;
    SessionManager session;
    AmcuConfig amcuConfig;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.alert_select_milktype_center);

        tvHeader = (TextView) findViewById(R.id.tvHeader);
        tvHeader.setText("Select cattle type");

        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnSubmita = (Button) findViewById(R.id.btnSubmit);
        btnCow = (Button) findViewById(R.id.btnCow);
        btnBuffalo = (Button) findViewById(R.id.btnBuffalo);
        btnMixed = (Button) findViewById(R.id.btnMixed);


    }

    public int pxFromDp(final Context ctx, final float dp) {

        float f = dp * ctx.getResources().getDisplayMetrics().density;
        int i = (int) f;
        return i;
    }


    @Override
    protected void onStart() {
        super.onStart();

        session = new SessionManager(SelectMilkActivity.this);
        amcuConfig = AmcuConfig.getInstance();
        selectCenterMilkType();
    }

    public void selectCenterMilkType() {


        btnSubmita.requestFocus();

        btnCow.requestFocus();
        btnBuffalo.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_button));
        btnCow.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_button_selected));
        btnCow.setTextColor(getResources().getColor(R.color.white));


        btnCow.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (((event.getAction() == KeyEvent.ACTION_UP) && (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT))) {
                    btnCow.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_button));
                    btnCow.setTextColor(getResources().getColor(R.color.black));

                    btnBuffalo.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_button_selected));
                    btnBuffalo.setTextColor(getResources().getColor(R.color.white));

                    btnMixed.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_button));
                    btnMixed.setTextColor(getResources().getColor(R.color.black));

                    btnBuffalo.requestFocus();
                    selectMilkType = "BUFFALO";
                    return true;
                } else if (((event.getAction() == KeyEvent.ACTION_UP) && (keyCode == KeyEvent.KEYCODE_DPAD_LEFT))) {
                    btnCow.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_button));
                    btnCow.setTextColor(getResources().getColor(R.color.black));

                    btnBuffalo.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_button));
                    btnBuffalo.setTextColor(getResources().getColor(R.color.black));


                    btnMixed.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_button_selected));
                    btnMixed.setTextColor(getResources().getColor(R.color.white));

                    btnMixed.requestFocus();
                    selectMilkType = "MIXED";
                    return true;
                } else if (((event.getAction() == KeyEvent.ACTION_UP) && (keyCode == KeyEvent.KEYCODE_DPAD_DOWN))) {
                    btnSubmita.requestFocus();
                } else if (selectMilkType != null && ((event.getAction() == KeyEvent.ACTION_UP) && (keyCode == KeyEvent.KEYCODE_ENTER))
                        || ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER))) {

                    selectMilkType = "COW";
                    setRateChart();
                    return true;
                }
                return true;
            }
        });

        btnBuffalo.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (((event.getAction() == KeyEvent.ACTION_UP) && (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT))) {
                    btnBuffalo.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_button));
                    btnBuffalo.setTextColor(getResources().getColor(R.color.black));

                    btnMixed.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_button_selected));
                    btnMixed.setTextColor(getResources().getColor(R.color.white));

                    btnCow.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_button));
                    btnCow.setTextColor(getResources().getColor(R.color.black));

                    btnMixed.requestFocus();
                    selectMilkType = "MIXED";
                    return true;
                } else if (((event.getAction() == KeyEvent.ACTION_UP) && (keyCode == KeyEvent.KEYCODE_DPAD_DOWN))) {
                    btnSubmita.requestFocus();
                } else if (((event.getAction() == KeyEvent.ACTION_UP) && (keyCode == KeyEvent.KEYCODE_DPAD_LEFT))) {
                    btnBuffalo.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_button));
                    btnBuffalo.setTextColor(getResources().getColor(R.color.black));

                    btnMixed.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_button));
                    btnMixed.setTextColor(getResources().getColor(R.color.black));


                    btnCow.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_button_selected));
                    btnCow.setTextColor(getResources().getColor(R.color.white));

                    btnCow.requestFocus();
                    selectMilkType = "COW";
                    return true;
                } else if (selectMilkType != null && ((event.getAction() == KeyEvent.ACTION_UP) && (keyCode == KeyEvent.KEYCODE_ENTER))
                        || ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER))) {
                    selectMilkType = "BUFFALO";
                    setRateChart();
                    return true;
                }
                return true;
            }
        });


        btnMixed.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (((event.getAction() == KeyEvent.ACTION_UP) && (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT))) {
                    btnMixed.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_button));
                    btnMixed.setTextColor(getResources().getColor(R.color.black));

                    btnCow.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_button_selected));
                    btnCow.setTextColor(getResources().getColor(R.color.white));

                    btnBuffalo.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_button));
                    btnBuffalo.setTextColor(getResources().getColor(R.color.black));

                    btnCow.requestFocus();
                    selectMilkType = "COW";
                    return true;
                } else if (((event.getAction() == KeyEvent.ACTION_UP) && (keyCode == KeyEvent.KEYCODE_DPAD_LEFT))) {
                    btnMixed.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_button));
                    btnMixed.setTextColor(getResources().getColor(R.color.black));

                    btnCow.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_button));
                    btnCow.setTextColor(getResources().getColor(R.color.black));


                    btnBuffalo.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_button_selected));
                    btnBuffalo.setTextColor(getResources().getColor(R.color.white));

                    btnBuffalo.requestFocus();
                    selectMilkType = "BUFFALO";
                    return true;
                } else if (((event.getAction() == KeyEvent.ACTION_UP) && (keyCode == KeyEvent.KEYCODE_DPAD_DOWN))) {
                    btnSubmita.requestFocus();
                } else if (selectMilkType != null && ((event.getAction() == KeyEvent.ACTION_UP) && (keyCode == KeyEvent.KEYCODE_ENTER))
                        || ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER))) {
                    selectMilkType = "MIXED";
                    setRateChart();
                    return true;
                }
                return true;
            }
        });


        btnCow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnBuffalo.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_button));
                btnBuffalo.setTextColor(getResources().getColor(R.color.black));
                btnMixed.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_button));
                btnMixed.setTextColor(getResources().getColor(R.color.black));
                btnCow.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_button_selected));
                btnCow.setTextColor(getResources().getColor(R.color.white));
                btnCow.requestFocus();
                selectMilkType = "COW";
            }
        });

        btnBuffalo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnCow.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_button));
                btnCow.setTextColor(getResources().getColor(R.color.black));
                btnMixed.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_button));
                btnMixed.setTextColor(getResources().getColor(R.color.black));
                btnBuffalo.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_button_selected));
                btnBuffalo.setTextColor(getResources().getColor(R.color.white));
                btnBuffalo.requestFocus();
                selectMilkType = "BUFFALO";
            }
        });

        btnMixed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnCow.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_button));
                btnCow.setTextColor(getResources().getColor(R.color.black));
                btnBuffalo.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_button));
                btnBuffalo.setTextColor(getResources().getColor(R.color.black));
                btnMixed.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_button_selected));
                btnMixed.setTextColor(getResources().getColor(R.color.white));
                btnMixed.requestFocus();
                selectMilkType = "MIXED";
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnSubmita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRateChart();

            }
        });

    }

    public void setRateChart() {
        session.setMilkType(selectMilkType);
        if (session.getMilkType().equalsIgnoreCase("Buffalo")) {
            amcuConfig.setRateChartName(amcuConfig.getRateChartForBuffalo());
        } else if (session.getMilkType().equalsIgnoreCase("Mixed")) {
            amcuConfig.setRateChartName(amcuConfig.getRateChartForMixed());

        } else {
            amcuConfig.setRateChartName(amcuConfig.getRateChartForCow());
        }
        goToNextActivity();
    }

    public void goToNextActivity() {
        startActivity(new Intent(SelectMilkActivity.this, MultipleMilkAnalyser.class));
        finish();
    }

}
