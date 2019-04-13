package com.devapp.kmfcommon;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.user.Util;
import com.devApp.R;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by yyy on 3/2/16.
 */
public class TruckEntryReportActivity extends Activity {
    TextView t_collection, coolection_summary, t_truck_coolection, t_local_sales;
    CardView row1, row2, row3, row4;
    String shift, s_day, s_month, s_year;
    String comingFrom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_truck_entry_report);
        row1 = (CardView) findViewById(R.id.row1);
        row2 = (CardView) findViewById(R.id.row2);
        row3 = (CardView) findViewById(R.id.row3);
        row4 = (CardView) findViewById(R.id.row4);
        t_collection = (TextView) findViewById(R.id.t_collection);
        t_local_sales = (TextView) findViewById(R.id.t_local_sales);
        coolection_summary = (TextView) findViewById(R.id.coolection_summary);
        t_truck_coolection = (TextView) findViewById(R.id.t_truck_coolection);


        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade);
        t_collection.startAnimation(animation);
        coolection_summary.startAnimation(animation);
        t_truck_coolection.startAnimation(animation);
        t_local_sales.startAnimation(animation);

        row1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    t_collection.setTextColor(getResources().getColor(R.color.green));
                    t_local_sales.setTextColor(getResources().getColor(R.color.black));
                    t_truck_coolection.setTextColor(getResources().getColor(R.color.black));
                    coolection_summary.setTextColor(getResources().getColor(R.color.white));
                }
            }
        });
        row2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    t_local_sales.setTextColor(getResources().getColor(R.color.green));
                    t_collection.setTextColor(getResources().getColor(R.color.white));
                    t_truck_coolection.setTextColor(getResources().getColor(R.color.black));
                    coolection_summary.setTextColor(getResources().getColor(R.color.white));
                }
            }
        });
        row3.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    t_truck_coolection.setTextColor(getResources().getColor(R.color.green));
                    t_local_sales.setTextColor(getResources().getColor(R.color.black));
                    t_collection.setTextColor(getResources().getColor(R.color.white));
                    coolection_summary.setTextColor(getResources().getColor(R.color.white));
                }
            }
        });
        row4.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    coolection_summary.setTextColor(getResources().getColor(R.color.green));
                    t_truck_coolection.setTextColor(getResources().getColor(R.color.black));
                    t_local_sales.setTextColor(getResources().getColor(R.color.black));
                    t_collection.setTextColor(getResources().getColor(R.color.white));
                }
            }
        });
        DatabaseHandler dbHandler = DatabaseHandler.getDatabaseInstance();
        parseCursorDataFromSalesRecordTable(dbHandler.getTruckReportDataFromSalesRecordTable());
    }

    public void gotoCollectionDeatilTruckEvent(View view) {
        comingFrom = "TOTAL_COLLECTION";
        gotoShiftAndDateAlert();

    }

    public void gotoSummaryDeatilTruckEvent(View view) {
        comingFrom = "COLLECTION_SUMMARY";
        gotoShiftAndDateAlert();
    }

    public void gotoTruckDeatilTruckEvent(View view) {
        comingFrom = "TRUCK_EVENT";
        gotoShiftAndDateAlert();
    }

    public void gotoLocalSalesDeatilTruckEvent(View view) {
        comingFrom = "LOCAL_SALES";
        gotoShiftAndDateAlert();
    }

    public void gotoShiftAndDateAlert() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(TruckEntryReportActivity.this);

        LayoutInflater inflater = TruckEntryReportActivity.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.shift_dialogbox, null);


        RadioGroup radioMilkStateGroup = (RadioGroup) dialogView.findViewById(R.id.radio_shift);
        shift = "M";
        radioMilkStateGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio_morning:
                        shift = "M";
                        break;
                    case R.id.radio_evening:
                        shift = "E";
                        break;

                }
            }
        });


        // Spinner element
        Spinner spinnerDate = (Spinner) dialogView.findViewById(R.id.dateDD);
        Spinner spinnerMonth = (Spinner) dialogView.findViewById(R.id.monthMON);
        Spinner spinnerYear = (Spinner) dialogView.findViewById(R.id.yearYYYY);

        // Spinner Drop down elements for day
        List<String> daydata = new ArrayList<String>();
        daydata.add("01");
        daydata.add("02");
        daydata.add("03");
        daydata.add("04");
        daydata.add("05");
        daydata.add("06");
        daydata.add("07");
        daydata.add("08");
        daydata.add("09");
        daydata.add("10");
        daydata.add("11");
        daydata.add("12");
        daydata.add("13");
        daydata.add("14");
        daydata.add("15");
        daydata.add("16");
        daydata.add("17");
        daydata.add("18");
        daydata.add("19");
        daydata.add("20");
        daydata.add("21");
        daydata.add("22");
        daydata.add("23");
        daydata.add("24");
        daydata.add("25");
        daydata.add("26");
        daydata.add("27");
        daydata.add("28");
        daydata.add("29");
        daydata.add("30");
        daydata.add("31");
        // Spinner Drop down elements for Month
        List<String> monthdata = new ArrayList<String>();
        monthdata.add("Jan");
        monthdata.add("Feb");
        monthdata.add("Mar");
        monthdata.add("Apr");
        monthdata.add("May");
        monthdata.add("Jun");
        monthdata.add("Jul");
        monthdata.add("Aug");
        monthdata.add("Sep");
        monthdata.add("Oct");
        monthdata.add("Nov");
        monthdata.add("Dcm");
        // Spinner Drop down elements for Month
        List<String> yeardata = new ArrayList<String>();
        yeardata.add("2015");
        yeardata.add("2016");
        yeardata.add("2017");
        yeardata.add("2018");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, daydata);
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, monthdata);
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, yeardata);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        spinnerDate.setAdapter(dataAdapter);

        spinnerMonth.setAdapter(monthAdapter);
        spinnerYear.setAdapter(yearAdapter);

        String date = Util.getTodayDateAndTime(1, TruckEntryReportActivity.this, false);
        StringTokenizer st = new StringTokenizer(date, "-");
        ArrayList parsedata = new ArrayList();
        while (st.hasMoreTokens()) {
            parsedata.add(st.nextToken());
        }
        spinnerDate.setSelection(daydata.indexOf(parsedata.get(0)));
        spinnerMonth.setSelection(monthdata.indexOf(parsedata.get(1)));
        spinnerYear.setSelection(yeardata.indexOf(parsedata.get(2)));
        parsedata = null;
        // Spinner click listener
        spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();


                // Toast.makeText(TruckEntryReportActivity.this, "Position " + item, Toast.LENGTH_LONG).show();
                s_year = item;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        // Spinner click listener
        spinnerDate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();


                // Toast.makeText(TruckEntryReportActivity.this, "Position " + item, Toast.LENGTH_LONG).show();
                s_day = item;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        // Spinner click listener
        spinnerMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();


                //Toast.makeText(TruckEntryReportActivity.this, "Position " + item, Toast.LENGTH_LONG).show();
                s_month = item;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        builder.setView(dialogView);
        builder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String date = s_day + "-" + s_month + "-" + s_year;
                        //Toast.makeText(TruckEntryReportActivity.this, "Final Data Shift" + shift + "DAY:" + date, Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                        Intent intent = new Intent(getApplicationContext(), TruckDetailCommonReport.class);
                        if (comingFrom.equalsIgnoreCase("TOTAL_COLLECTION")) {
                            intent.putExtra("HEAD", "TOTAL_COLLECTION");
                        }
                        if (comingFrom.equalsIgnoreCase("COLLECTION_SUMMARY")) {
                            intent.putExtra("HEAD", "COLLECTION_SUMMARY");
                        }
                        if (comingFrom.equalsIgnoreCase("TRUCK_EVENT")) {
                            intent.putExtra("HEAD", "TRUCK_EVENT");
                        }
                        if (comingFrom.equalsIgnoreCase("LOCAL_SALES")) {
                            intent.putExtra("HEAD", "LOCAL_SALES");
                        }
                        intent.putExtra("S_DATE", date);
                        intent.putExtra("S_SHIFT", shift);
                        startActivity(intent);
                    }
                });
        builder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();

                    }
                });


        builder.setInverseBackgroundForced(true);
        builder.setCancelable(false);
        AlertDialog levelDialog = builder.create();

        levelDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        levelDialog.show();

        Window window = levelDialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    public void parseCursorDataFromSalesRecordTable(Cursor cursor) {
        StringBuilder stringBuilder = new StringBuilder();
        if (null != cursor) {
            if (cursor.moveToFirst()) {
                do {

                    stringBuilder.append("date\n");
                    stringBuilder.append(cursor.getString(cursor.getColumnIndex("date")));
                    stringBuilder.append("rate \n");
                    stringBuilder.append(cursor.getString(cursor.getColumnIndex("rate")));

                } while (cursor.moveToNext());
            }
            //   Toast.makeText(getApplicationContext(),stringBuilder.toString(),Toast.LENGTH_LONG).show();
            System.out.print("==============yyy" + stringBuilder.toString());
        } else {
            //   Toast.makeText(getApplicationContext(), "cursor null nodata", Toast.LENGTH_LONG).show();
        }
    }

}


