package com.devapp.kmfcommon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.devapp.devmain.devicemanager.DatabaseManager;
import com.devapp.devmain.entity.ReportEntity;
import com.devapp.devmain.macollection.MCCCollectionActivity;
import com.devapp.devmain.main.FarmerScannerActivity;
import com.devapp.devmain.server.SessionManager;
import com.devapp.devmain.user.Util;
import com.devapp.smartcc.entityandconstants.SmartCCUtil;
import com.devApp.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class UserMilkCollectionActivity extends Activity {
    SessionManager session;
    String cid;
    double totalMilk = 0, totalKgMilk = 0, totalLtrMilk = 0;
    String isAutoOrManual;
    Button btnfindSelected, selectMShift, selectEShift;
    TextView head;
    StringBuffer responseText = new StringBuffer();
    String shiftByTime;
    String sDate;
    TextView tv;
    SmartCCUtil smartCCUtil;
    private MccCustomAdapter dataAdapter;

    private ArrayList<ReportEntity> reportEntities = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_milk_collection);
        isAutoOrManual = getIntent().getStringExtra("isAutoOrManual");
        session = new SessionManager(this);
        cid = session.getFarmerID();
        head = (TextView) findViewById(R.id.Head);
        shiftByTime = Util.getCurrentShift();
        btnfindSelected = (Button) findViewById(R.id.findSelected);
        selectMShift = (Button) findViewById(R.id.selectMShift);
        selectEShift = (Button) findViewById(R.id.selectEShift);
        tv = (TextView) findViewById(R.id.tv);
        tv.setVisibility(View.GONE);

        smartCCUtil = new SmartCCUtil(UserMilkCollectionActivity.this);

        sDate = smartCCUtil.getReportFormatDate();

        btnfindSelected.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    btnfindSelected.setBackgroundColor(getResources().getColor(R.color.focused));

                } else {
                    btnfindSelected.setBackgroundColor(getResources().getColor(R.color.blue));
                }
            }
        });
        displayListView(shiftByTime, sDate);
        if (shiftByTime.equalsIgnoreCase("M")) {
            selectMShift.setTextColor(getResources().getColor(R.color.btnCattlePressed));
            selectEShift.setTextColor(getResources().getColor(R.color.white));
            selectMShift.setFocusable(true);
            selectEShift.setFocusable(false);
        } else {
            selectEShift.setTextColor(getResources().getColor(R.color.btnCattlePressed));
            selectMShift.setTextColor(getResources().getColor(R.color.white));
            selectEShift.setFocusable(true);
            selectMShift.setFocusable(false);
        }
        checkButtonClick();
        head.setText("Milk Analyser (" + shiftByTime + ")");

        selectMShift.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    selectMShift.setTextColor(getResources().getColor(R.color.btnCattlePressed));
                    selectEShift.setTextColor(getResources().getColor(R.color.white));
                    selectEShift.setFocusable(true);
                }
            }
        });
        selectEShift.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    selectEShift.setTextColor(getResources().getColor(R.color.btnCattlePressed));
                    selectMShift.setTextColor(getResources().getColor(R.color.white));
                    selectMShift.setFocusable(true);
                }
            }
        });

    }


    @Override
    protected void onResume() {

        super.onResume();
    }

    public void goToMorningShift(View view) {
        tv = (TextView) findViewById(R.id.tv);
        tv.setVisibility(View.GONE);
        shiftByTime = "M";
        head.setText("Milk Analyser (M)");
        displayListView("M", sDate);
        checkButtonClick();
    }

    public void goToEveningShift(View view) {
        tv = (TextView) findViewById(R.id.tv);
        tv.setVisibility(View.GONE);
        shiftByTime = "E";
        head.setText("Milk Analyser (E)");
        displayListView("E", sDate);
        checkButtonClick();
    }

    private void displayListView(String shift, String sDate) {

      /*  ArrayList<UserMilkCollectionPojo> dataList = new ArrayList<UserMilkCollectionPojo>();
        UserMilkCollectionPojo userMilkCollectionPojo;*/
        DatabaseManager databaseManager = new DatabaseManager(getApplicationContext());
        reportEntities = databaseManager.getIncompleteRecordsForMCC(cid, shift, sDate);

        for (ReportEntity reportEntity : reportEntities) {
            reportEntity.setIsSelected(true);
            session.setMilkType(reportEntity.milkType);
        }

        if (reportEntities.size() < 1) {
            tv = (TextView) findViewById(R.id.tv);
            tv.setVisibility(View.VISIBLE);
        }
        //create an ArrayAdaptar from the String Array
        dataAdapter = new MccCustomAdapter(this,
                R.layout.row_user_milk_collection, reportEntities);
        ListView listView = (ListView) findViewById(R.id.listView1);
        // Assign adapter to ListView
        listView.setAdapter(dataAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

            }
        });

    }

    private void checkButtonClick() {


        Button myButton = (Button) findViewById(R.id.findSelected);

        myButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                ArrayList<ReportEntity> dataList = new ArrayList<ReportEntity>();
                for (int i = 0; i < reportEntities.size(); i++) {
                    ReportEntity reportEntity = reportEntities.get(i);
                    if (reportEntity.getIsSelected()) {
                        dataList.add(reportEntity);
                    }
                }

                if (dataList.size() == 0) {
                    Intent intent = new Intent(getApplicationContext(), FarmerScannerActivity.class);
                    startActivity(intent);
                    finish();
                } else {


                    DecimalFormat decimalFormat = new DecimalFormat("#0.00");
                    totalMilk = Double.valueOf(decimalFormat.format(totalMilk));
                    totalKgMilk = Double.valueOf(decimalFormat.format(totalKgMilk));
                    totalLtrMilk = Double.valueOf(decimalFormat.format(totalLtrMilk));

                    Intent intent = new Intent(getApplicationContext(), MCCCollectionActivity.class);
                    intent.putExtra("COMING_FROM", "UMCA");
                    intent.putExtra("SELECTED_CURSORID", String.valueOf(responseText));


                    intent.putExtra("SELECTED_DATA", dataList);
                    new SmartCCUtil(UserMilkCollectionActivity.this).setRateChart();

                    startActivity(intent);
                    finish();
                }

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), FarmerScannerActivity.class);
        startActivity(intent);
        finish();
    }


    private class MccCustomAdapter extends ArrayAdapter<ReportEntity> {

        private ArrayList<ReportEntity> totalDataList;

        public MccCustomAdapter(Context context, int textViewResourceId,
                                ArrayList<ReportEntity> allList) {
            super(context, textViewResourceId, allList);
            this.totalDataList = new ArrayList<ReportEntity>();
            this.totalDataList.addAll(allList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            Log.v("ConvertView", String.valueOf(position));

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.row_user_milk_collection, null);

                holder = new ViewHolder();
                holder.data = (TextView) convertView.findViewById(R.id.data);
                holder.checkbox = (CheckBox) convertView.findViewById(R.id.checkBox1);
                convertView.setTag(holder);

                holder.checkbox.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v;
                        ReportEntity reportEntity = (ReportEntity) cb.getTag();
                        reportEntity.setIsSelected(cb.isChecked());

                    }
                });
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            ReportEntity reportEntity = totalDataList.get(position);
            holder.data.setText(" (" + reportEntity.getQuantity() + " " +
                    Util.getTheUnit(getApplicationContext(), 0) + "," +

                    reportEntity.getPostDate() + " " +
                    reportEntity.getTime() + ",SID: " + reportEntity.getSampleNumber() + ")");
            holder.checkbox.setText(reportEntity.getFarmerId());
            holder.checkbox.setChecked(reportEntity.getIsSelected());
            holder.checkbox.setTag(reportEntity);

            return convertView;

        }

        private class ViewHolder {
            TextView data;
            CheckBox checkbox;
        }

    }
}