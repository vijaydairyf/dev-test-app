package com.devapp.devmain.helper;

import android.content.Context;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextClock;
import android.widget.TextView;

import com.devapp.devmain.devicemanager.PrinterManager;
import com.devapp.devmain.entity.AverageReportDetail;
import com.devapp.devmain.entity.ReportEntity;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.user.Util;
import com.devapp.smartcc.entityandconstants.SmartCCUtil;
import com.devApp.R;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by u_pendra on 11/1/17.
 */

public class ShiftSummaryActivity extends AppCompatActivity implements View.OnClickListener {


    DatabaseHandler databaseHandler;
    SmartCCUtil smartCCUtil;
    UsbManager mUsbManager;
    PrinterManager printerManager;
    AmcuConfig amcuConfig;
    private android.support.v7.widget.CardView cardTotalSummary,
            cardFarmerSummary, cardCenterSummary, cardEndShift, cardExit;
    private TextView tvRecordSummary, tvCurrentDate, tvCurrentShift, tvMemberData;
    private TextClock tvTime;
    private Spinner spId;
    private RelativeLayout rlTotalSummary, rlFarmerSummary, rlCenterSummary, rlEndShift, rlExit;
    private LinearLayout lnHeader, lnData;
    private Button btnReportPrint, btnReceiptPrint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shift_summary_main);
        initializeView();
        onSelectCardView();

        btnReportPrint.setText("Print shift report");
    }

    @Override
    protected void onStart() {
        super.onStart();
        databaseHandler = DatabaseHandler.getDatabaseInstance();
        smartCCUtil = new SmartCCUtil(getApplicationContext());
        amcuConfig = AmcuConfig.getInstance();
        printerManager = new PrinterManager(ShiftSummaryActivity.this);
        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);

        tvCurrentDate.setText(smartCCUtil.getReportDate(0));
        tvCurrentShift.setText(Util.getCurrentShift());
        setAverageData(null, null);
    }

    public void initializeView() {
        cardTotalSummary = (android.support.v7.widget.CardView) findViewById(R.id.cardTotalSummary);
        cardFarmerSummary = (android.support.v7.widget.CardView) findViewById(R.id.cardFarmerSummary);
        cardCenterSummary = (android.support.v7.widget.CardView) findViewById(R.id.cardCenterSummary);
        cardEndShift = (android.support.v7.widget.CardView) findViewById(R.id.cardEndShift);
        cardExit = (android.support.v7.widget.CardView) findViewById(R.id.cardExit);


        tvRecordSummary = (TextView) findViewById(R.id.tvRecordSummary);
        tvCurrentDate = (TextView) findViewById(R.id.tvCurrentDate);
        tvCurrentShift = (TextView) findViewById(R.id.tvCurrentShift);
        tvMemberData = (TextView) findViewById(R.id.tvMemberData);
        tvTime = (TextClock) findViewById(R.id.tvTime);
        spId = (Spinner) findViewById(R.id.spId);

        rlTotalSummary = (RelativeLayout) findViewById(R.id.rlTotalSummary);
        rlFarmerSummary = (RelativeLayout) findViewById(R.id.rlFarmerSummary);
        rlCenterSummary = (RelativeLayout) findViewById(R.id.rlCenterSummary);
        rlEndShift = (RelativeLayout) findViewById(R.id.rlEndShift);
        rlExit = (RelativeLayout) findViewById(R.id.rlExit);

        lnHeader = (LinearLayout) findViewById(R.id.lnHeader);
        lnData = (LinearLayout) findViewById(R.id.lnData);
        btnReportPrint = (Button) findViewById(R.id.btnReportPrint);
        btnReceiptPrint = (Button) findViewById(R.id.btnReceiptPrint);

        btnReportPrint.setOnClickListener(this);
        btnReceiptPrint.setOnClickListener(this);

        cardTotalSummary.setOnClickListener(this);
        cardCenterSummary.setOnClickListener(this);
        cardFarmerSummary.setOnClickListener(this);
        cardEndShift.setOnClickListener(this);
        cardExit.setOnClickListener(this);

    }

    public void onSelectCardView() {
        cardTotalSummary.requestFocus();
        rlTotalSummary.setBackgroundResource(R.color.focused);


        cardTotalSummary.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean isFocus) {

                if (isFocus) {
                    rlTotalSummary.setBackgroundResource(R.color.focused);
                    rlCenterSummary.setBackgroundResource(R.drawable.btn_bg_normal_positive);
                    rlFarmerSummary.setBackgroundResource(R.drawable.btn_bg_normal_positive);
                    rlEndShift.setBackgroundResource(R.drawable.btn_bg_normal_positive);
                    rlExit.setBackgroundResource(R.drawable.btn_bg_normal_positive);
                } else {
                    rlTotalSummary.setBackgroundResource(R.drawable.btn_bg_normal_positive);
                }

            }
        });

        cardFarmerSummary.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean isFocus) {

                if (isFocus) {
                    rlFarmerSummary.setBackgroundResource(R.color.focused);
                    rlCenterSummary.setBackgroundResource(R.drawable.btn_bg_normal_positive);
                    rlTotalSummary.setBackgroundResource(R.drawable.btn_bg_normal_positive);
                    rlEndShift.setBackgroundResource(R.drawable.btn_bg_normal_positive);
                    rlExit.setBackgroundResource(R.drawable.btn_bg_normal_positive);
                } else {
                    rlFarmerSummary.setBackgroundResource(R.drawable.btn_bg_normal_positive);
                }

            }
        });


        cardCenterSummary.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean isFocus) {

                if (isFocus) {
                    rlCenterSummary.setBackgroundResource(R.color.focused);
                    rlTotalSummary.setBackgroundResource(R.drawable.btn_bg_normal_positive);
                    rlFarmerSummary.setBackgroundResource(R.drawable.btn_bg_normal_positive);
                    rlEndShift.setBackgroundResource(R.drawable.btn_bg_normal_positive);
                    rlExit.setBackgroundResource(R.drawable.btn_bg_normal_positive);
                } else {
                    rlCenterSummary.setBackgroundResource(R.drawable.btn_bg_normal_positive);
                }

            }
        });

        cardEndShift.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean isFocus) {

                if (isFocus) {
                    rlEndShift.setBackgroundResource(R.color.focused);
                    rlTotalSummary.setBackgroundResource(R.drawable.btn_bg_normal_positive);
                    rlFarmerSummary.setBackgroundResource(R.drawable.btn_bg_normal_positive);
                    rlCenterSummary.setBackgroundResource(R.drawable.btn_bg_normal_positive);
                    rlExit.setBackgroundResource(R.drawable.btn_bg_normal_positive);

                } else {
                    rlEndShift.setBackgroundResource(R.drawable.btn_bg_normal_positive);
                }

            }
        });

        cardExit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean isFocus) {

                if (isFocus) {
                    rlExit.setBackgroundResource(R.color.focused);
                    rlTotalSummary.setBackgroundResource(R.drawable.btn_bg_normal_positive);
                    rlFarmerSummary.setBackgroundResource(R.drawable.btn_bg_normal_positive);
                    rlCenterSummary.setBackgroundResource(R.drawable.btn_bg_normal_positive);
                    rlEndShift.setBackgroundResource(R.drawable.btn_bg_normal_positive);

                } else {
                    rlExit.setBackgroundResource(R.drawable.btn_bg_normal_positive);
                }

            }
        });

        spId.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                setMemberData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        cardExit.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (((event.getAction() == KeyEvent.ACTION_UP)
                        && (keyCode == KeyEvent.KEYCODE_DPAD_DOWN))) {

                    spId.requestFocus();
                    return true;
                }
                return false;
            }
        });

//        spId.setOnKeyListener(new View.OnKeyListener() {
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                if (((event.getAction() == KeyEvent.ACTION_UP)
//                        && (keyCode == KeyEvent.KEYCODE_DPAD_UP))) {
//                    cardExit.requestFocus();
//                    return true;
//                }
//                return false;
//            }
//        });

        cardTotalSummary.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (((event.getAction() == KeyEvent.ACTION_UP)
                        && (keyCode == KeyEvent.KEYCODE_DPAD_DOWN))) {
                    btnReportPrint.requestFocus();
                    return true;
                }
                return false;
            }
        });

        btnReportPrint.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (((event.getAction() == KeyEvent.ACTION_UP)
                        && (keyCode == KeyEvent.KEYCODE_DPAD_UP))) {

                    cardTotalSummary.requestFocus();
                    return true;
                }
                return false;
            }
        });


    }


    public void onClick(View v) {
        if (v == btnReportPrint) {
            EndShiftHelper endShiftHelper = new EndShiftHelper(ShiftSummaryActivity.this);
            endShiftHelper.printShiftReport(tvCurrentDate.getText().toString().trim(),
                    tvCurrentShift.getText().toString().trim());

        } else if (v == btnReceiptPrint) {

            printReceipt(setMemberData());
        } else if (v == cardCenterSummary) {
            setAverageData(Util.REPORT_TYPE_MCC, null);
        } else if (v == cardFarmerSummary) {
            setAverageData(Util.REPORT_TYPE_FARMER, null);
        } else if (v == cardExit) {
            finish();
        } else if (v == cardEndShift) {
            setAverageData(null, null);
            EndShiftHelper endShiftHelper = new EndShiftHelper(ShiftSummaryActivity.this);
            endShiftHelper.doEndShift();

        } else if (v == cardTotalSummary) {
            setAverageData(null, null);
        }
    }

    public void setAverageData(String type, String milkType) {
        AverageReportDetail ard = databaseHandler.getAverageReportData(type, null);
        tvRecordSummary.setText(smartCCUtil.getSummary(ard));

        ArrayList<String> allFarmerList = databaseHandler.getAllMemberList(type);
        Collections.reverse(allFarmerList);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this
                , R.layout.custom_spinner_item, allFarmerList);
        spId.setAdapter(arrayAdapter);

        setMemberData();

    }

    public ReportEntity setMemberData() {
        if (spId.getSelectedItem() == null) {
            tvMemberData.setText("Individual report");
            return null;
        }
        ReportEntity reportEntity =
                databaseHandler.getReportEntFromId(spId.getSelectedItem().toString());
        if (reportEntity != null) {
            tvMemberData.setText(" name: " + reportEntity.farmerName + "\n"
                    + "Fat:  " + reportEntity.fat + "\n"
                    + "SNF:  " + reportEntity.snf + "\n"
                    + "Qty:  " + reportEntity.quantity + "\n"
                    + "Time: " + reportEntity.time);
        }
        return reportEntity;
    }


    public void printReceipt(ReportEntity reportEntity) {
        if (reportEntity == null) {
            return;
        }

        printerManager.print(smartCCUtil.receiptFormat(reportEntity),
                PrinterManager.printReciept, null, null, null);

    }


}
