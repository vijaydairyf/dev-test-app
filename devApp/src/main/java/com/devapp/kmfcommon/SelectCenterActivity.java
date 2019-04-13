package com.devapp.kmfcommon;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.devapp.devmain.devicemanager.DatabaseManager;
import com.devapp.devmain.entity.CenterEntity;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.SessionManager;
import com.devapp.devmain.user.Util;
import com.devApp.R;

/**
 * Created by Upendra on 1/8/2016.
 */
public class SelectCenterActivity extends Activity {

    String selectedMilkType = "COW";
    String selectedShift = "M";
    SessionManager session;
    AmcuConfig amcuConfig;
    EditText etCenterBarcode;
    Spinner spMilkType, spShift;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.alert_collection_activity);
        session = new SessionManager(SelectCenterActivity.this);
        amcuConfig = AmcuConfig.getInstance();

        final Button btnSave = (Button) findViewById(R.id.btnSubmit);
        btnSave.setText("Save");
        final Button btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCancel.setText("Cancel");

        etCenterBarcode = (EditText) findViewById(R.id.etBarcode);
        spMilkType = (Spinner) findViewById(R.id.spSelectMilkTypeColl);
        spShift = (Spinner) findViewById(R.id.spSelectShiftColl);
        spMilkType.setVisibility(View.VISIBLE);
        spShift.setVisibility(View.VISIBLE);
        spMilkType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                selectedMilkType = spMilkType.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        spShift.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                selectedShift = spShift.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {


            }
        });

        btnSave.setText("Next");
        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                onSubmit();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                finish();
            }
        });

        spMilkType.requestFocus();
    }

    @Override
    protected void onStart() {
        super.onStart();

        onClickViews();
    }

    public void setRateChart() {
        if (session.getMilkType().equalsIgnoreCase("Buffalo")) {
            amcuConfig.setRateChartName(amcuConfig.getRateChartForBuffalo());
            if (amcuConfig.getKeyAllowProteinValue()) {
                amcuConfig.setInCentiveRateChartname(amcuConfig.getIncentiveRateChartForBuffalo());
            }
        } else if (session.getMilkType().equalsIgnoreCase("Mixed")) {
            amcuConfig.setRateChartName(amcuConfig.getRateChartForMixed());
            if (amcuConfig.getKeyAllowProteinValue()) {
                amcuConfig.setInCentiveRateChartname(amcuConfig.getIncentiveRateChartForMixed());
            }

        } else {
            amcuConfig.setRateChartName(amcuConfig.getRateChartForCow());
            if (amcuConfig.getKeyAllowProteinValue()) {
                amcuConfig.setInCentiveRateChartname(amcuConfig.getIncentiveRateChartForCow());
            }
        }
    }

    public void onSubmit() {

        DatabaseManager dbm = new DatabaseManager(SelectCenterActivity.this);
        CenterEntity centerEntity =
                dbm.getCenterDetails(etCenterBarcode.getText().toString().replace(" ", ""), Util.CHECK_DUPLICATE_CENTERCODE);

        if (centerEntity != null) {
            finish();
        } else {
            Util.displayErrorToast("Invalid center id", SelectCenterActivity.this);
        }
    }


    public void onClickViews() {
        etCenterBarcode.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (((event.getAction() == KeyEvent.ACTION_UP) && (keyCode == KeyEvent.KEYCODE_ENTER))
                        || ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER))) {
                    onSubmit();
                    return true;
                }
                return false;
            }
        });

        spShift.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (((event.getAction() == KeyEvent.ACTION_UP) && (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT))
                        || ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_SOFT_RIGHT))) {
                    etCenterBarcode.requestFocus();

                    return true;
                }
                return false;
            }
        });
    }

}
