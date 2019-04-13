package com.devapp.devmain.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.devapp.devmain.entity.EntityRateChart;
import com.devapp.devmain.entity.RateChartEntity;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.server.SessionManager;
import com.devapp.devmain.services.CreateRateChartForSociety;
import com.devApp.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RateChartNew extends Activity implements OnClickListener {

    public static Set<Double> allDblFat = new HashSet<Double>();
    public static Set<Double> allDblSnf = new HashSet<Double>();
    public static ArrayList<EntityRateChart> tempEntRateChart = new ArrayList<EntityRateChart>();
    public static ArrayList<EntityRateChart> allEntRateChart = new ArrayList<EntityRateChart>();
    public static Map<Integer, ArrayList<RateChartEntity>> MixedList = new HashMap<Integer, ArrayList<RateChartEntity>>();
    static RelativeLayout progressLayout;
    Button btnCreate, btnCancel, btnAddNewRule, btnRemoveNewRule;
    ArrayList<RateChartEntity> allArrayRateChart = new ArrayList<RateChartEntity>();

    RateCharAdapterEx rateAdapter;
    int count = 1;
    boolean addData;


    ImageView ivAddButton, ivRemoveButton;

    RateChartEntity rateCharEnt;
    EntityRateChart entRateChart;
    ListView lvRateChart;

    Handler myHandler = new Handler();
    Runnable updateRunnable;
    SessionManager session;
    AmcuConfig amcuConfig;
    String rateChartName, milkType;

    DecimalFormat decimalFormatAMT = new DecimalFormat("#0.00");
    DecimalFormat decimalFormatFS = new DecimalFormat("#0.0");

    public static void ShowProgressBAr() {
        progressLayout.setVisibility(View.VISIBLE);

    }

    public static void HideProgressBAr() {
        progressLayout.setVisibility(View.GONE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rate_chartnew_example);
        amcuConfig = AmcuConfig.getInstance();

        rateChartName = amcuConfig.getRateChartName();
        milkType = amcuConfig.getMilkType();

        // ///////////////////////////////////
        progressLayout = (RelativeLayout) findViewById(R.id.progress_layout);
        progressLayout.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        progressLayout.setVisibility(View.GONE);
        // ///////////////////////////////////

        session = new SessionManager(RateChartNew.this);

        lvRateChart = (ListView) findViewById(R.id.lvRateChartList);

        ivAddButton = (ImageView) findViewById(R.id.ivAddRule);
        ivRemoveButton = (ImageView) findViewById(R.id.ivRemoveRule);
        btnCreate = (Button) findViewById(R.id.btnSave);
        btnCancel = (Button) findViewById(R.id.btnCancel);

        ivAddButton.setOnClickListener(this);
        ivRemoveButton.setOnClickListener(this);

        btnCreate.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        deleteTable();

    }

    @Override
    protected void onStart() {
        super.onStart();
        allEntRateChart = new ArrayList<EntityRateChart>();
        count = 1;
        entRateChart = new EntityRateChart();
        allEntRateChart.add(entRateChart);

        allDblFat = new HashSet<Double>();
        allDblSnf = new HashSet<Double>();
        setAdapter();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.ivAddRule:
                if (count < 25) {
                    addData = true;
                    count++;
                    getCount(count);

                }
                break;

            case R.id.ivRemoveRule:
                if (count > 1) {
                    addData = false;
                    getCount(count);

                    count--;
                }

                break;
            case R.id.btnSave:

                progressLayout.setVisibility(View.VISIBLE);

                if (count > 1) {

                    new Handler().post(new Runnable() {

                        @Override
                        public void run() {
                            if (allEntRateChart.size() > 1) {
                                if (dataValidation()) {
                                    CreateDatabaseTable();
                                } else {
                                    progressLayout.setVisibility(View.GONE);
                                }

                            } else {
                                CreateDatabaseTable();
                            }
                        }
                    });
                } else if (count == 1) {
                    new Handler().post(new Runnable() {

                        @Override
                        public void run() {
                            CreateDatabaseTable();

                        }
                    });

                } else {
                    Toast.makeText(RateChartNew.this,
                            "Please enter the valid data!", Toast.LENGTH_SHORT)
                            .show();
                    progressLayout.setVisibility(View.GONE);
                }

                break;
            case R.id.btnCancel:

                finish();
                break;
            default:
                break;
        }

    }

    public void getCount(int cont) {
        entRateChart = new EntityRateChart();

        if (addData) {

            if (count > 1) {
                if (allEntRateChart.size() > 0
                        && allEntRateChart.get(count - 2).etFatEnd != null
                        && !allEntRateChart.get(count - 2).etFatEnd
                        .equalsIgnoreCase("")) {
                    allEntRateChart.add(entRateChart);

                } else {
                    Toast.makeText(RateChartNew.this,
                            "Please fill all the fields", Toast.LENGTH_SHORT)
                            .show();
                    count = count - 1;
                }
            } else {
                allEntRateChart.add(entRateChart);
            }

        } else {
            allEntRateChart.remove(count - 1);
            MixedList.remove(count);
        }
        setAdapter();
    }

    public void setAdapter() {

        rateAdapter = new RateCharAdapterEx(RateChartNew.this, 0,
                allEntRateChart, 1, progressLayout);
        lvRateChart.setAdapter(rateAdapter);

    }

    public void setRateChartList(EntityRateChart entRate, int cont) {

        progressLayout.setVisibility(View.VISIBLE);

        ArrayList<RateChartEntity> tempRateCharEnt = new ArrayList<RateChartEntity>();

        double dblRateChangePerCLR = 0, dblClrStart = 0, dblClrEnd = 0, dblClrDifference = 0;
        double dblRateChangePerSNF = 0, dblRateChangePerFat = 0, dblTotalRate, dblSnfStart = 0, dblFatStart = 0, dblSnfEnd = 0, dblFatEnd = 0, dblRate = 0, dblFatDifference, dblSnfDifference;

        if (!entRate.etFatStart.replace(" ", "").equalsIgnoreCase("")) {
            dblFatStart = Double.parseDouble(entRate.etFatStart);
        } else {

            progressLayout.setVisibility(View.GONE);
            Toast.makeText(RateChartNew.this, "Please fill the details",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!entRate.etSnfStart.replace(" ", "").equalsIgnoreCase("")) {
            dblSnfStart = Double.parseDouble(entRate.etSnfStart);
        } else {
            progressLayout.setVisibility(View.GONE);
            Toast.makeText(RateChartNew.this, "Please fill the details",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!entRate.etBaseRate.replace(" ", "").equalsIgnoreCase("")) {
            dblRate = Double.parseDouble(entRate.etBaseRate);
        } else {
            progressLayout.setVisibility(View.GONE);
            Toast.makeText(RateChartNew.this, "Please fill the details",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (entRate.etFatEnd.replace(" ", "").equalsIgnoreCase("")) {

            dblFatEnd = dblFatStart;

        } else {
            dblFatEnd = Double.parseDouble(entRate.etFatEnd);
        }
        if (entRate.etSnfEnd.replace(" ", "").equalsIgnoreCase("")) {
            dblSnfEnd = dblSnfStart;

        } else {
            dblSnfEnd = Double.parseDouble(entRate.etSnfEnd);
        }
        if (entRate.etSnfRateIn.replace(" ", "").equalsIgnoreCase("")) {
            dblRateChangePerSNF = 0;

        } else {
            dblRateChangePerSNF = Double.parseDouble(entRate.etSnfRateIn);
        }
        if (entRate.etFatRateIn.replace(" ", "").equalsIgnoreCase("")) {
            dblRateChangePerFat = 0;
        } else {
            dblRateChangePerFat = Double.parseDouble(entRate.etFatRateIn);
        }

        dblTotalRate = dblRate;

        dblFatDifference = dblFatEnd - dblFatStart;
        dblSnfDifference = dblSnfEnd - dblSnfStart;

        int snfCount = 0, fatCount = 0, ClrCount = 0;
        double snfIncrease = 0, snfRate = 0, clrRate = 0;

        if (dblFatDifference >= 0 && dblSnfDifference >= 0) {

            for (double i = dblFatStart; i < dblFatEnd; ) {

                snfCount = 0;

                snfIncrease = fatCount * dblRateChangePerFat;

                snfRate = dblRate + snfIncrease;

                for (double k = dblSnfStart; k < dblSnfEnd; ) {
                    dblTotalRate = snfRate + (snfCount * dblRateChangePerSNF);

                    dblTotalRate = Double.valueOf(decimalFormatAMT
                            .format(dblTotalRate));

                    RateChartEntity RCEntity = new RateChartEntity();
                    RCEntity.snf = Double.valueOf(decimalFormatFS.format(k));
                    RCEntity.fat = Double.valueOf(decimalFormatFS.format(i));

                    RCEntity.societyId = String.valueOf(new SessionManager(
                            RateChartNew.this).getSocietyColumnId());

                    RCEntity.managerID = new SessionManager(RateChartNew.this)
                            .getManagerEmailID();
                    RCEntity.rate = dblTotalRate;
                    RCEntity.farmerId = "All farmers";

                    RCEntity.rateChartName = rateChartName;
                    RCEntity.milkType = milkType;

                    tempRateCharEnt.add(RCEntity);
                    k = k + 0.1;
                    snfCount = snfCount + 1;
                }
                i = i + 0.1;
                fatCount = fatCount + 1;
            }
        } else {
            Toast.makeText(RateChartNew.this,
                    "Please check the fat and snf values!", Toast.LENGTH_SHORT)
                    .show();
        }
        MixedList.put(cont, tempRateCharEnt);

        if (count != 1) {
            progressLayout.setVisibility(View.GONE);
        }

    }

    public void CreateDatabaseTable() {
        DatabaseHandler db = DatabaseHandler.getDatabaseInstance();

        if (count == 1) {

            if (MixedList.get(0) != null && MixedList.get(0).size() > 0) {

                try {
                    db.insertRateChartFromExcel(MixedList.get(0), DatabaseHandler.isPrimary);
                    db.insertRateChartFromExcel(MixedList.get(0), DatabaseHandler.isSecondary);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //DB close removed;
                Toast.makeText(RateChartNew.this,
                        "Rate chart created successfully!", Toast.LENGTH_SHORT)
                        .show();
                AfterCreateAndUpdate();

                MixedList = new HashMap<Integer, ArrayList<RateChartEntity>>();

                finish();

            }
        } else {
            for (int i = 0; i < count; i++) {

                if (MixedList.get(i) != null) {
                    try {
                        db.insertRateChartFromExcel(MixedList.get(i), DatabaseHandler.isPrimary);
                        db.insertRateChartFromExcel(MixedList.get(i), DatabaseHandler.isSecondary);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
            //DB close removed;
            Toast.makeText(RateChartNew.this,
                    "Rate chart created successfully!", Toast.LENGTH_SHORT)
                    .show();
            AfterCreateAndUpdate();

            finish();

        }

        progressLayout.setVisibility(View.GONE);
    }

    public void AfterCreateAndUpdate() {

        int vers = session.getRateChartVersion();
        vers = vers + 1;
        String version = Util.rateChartVersion(vers);
        boolean alreadyPresent = false;
        String fileName = new SessionManager(getApplicationContext())
                .getSocietyName() + "_RateChart" + version + ".xls";
        session.setRecentRateChart(fileName);

        ArrayList<String> listFileName = new ArrayList<String>();

        listFileName = Util.ReadRateChartNAME(RateChartNew.this, 0);

        if (listFileName != null && listFileName.size() > 0) {
            for (int i = 0; i < listFileName.size(); i++) {
                if (listFileName.get(i).equalsIgnoreCase(fileName)) {
                    alreadyPresent = true;
                }
            }
        }

        if (!alreadyPresent) {
            listFileName = new ArrayList<String>();
            listFileName.add(fileName);

        }

        Util.WriteRateChartName(RateChartNew.this, listFileName, 0);

        startService(new Intent(RateChartNew.this,
                CreateRateChartForSociety.class));
        session.setRateChartVersion(vers);

    }

    public boolean dataValidation() {

        boolean dataValid = true;

        // To get from entity
        ArrayList<Double> dblArrayListFat = new ArrayList<Double>();
        ArrayList<Double> dblArrayListSnf = new ArrayList<Double>();

        DatabaseHandler dbh = DatabaseHandler.getDatabaseInstance();

        // To get from adapter
        ArrayList<Double> dblArrListSnf = new ArrayList<Double>(allDblSnf);
        ArrayList<Double> dblArrListFat = new ArrayList<Double>(allDblFat);

        for (int i = 0; i < allEntRateChart.size(); i++) {

            if (allEntRateChart.get(i).etFatStart != null
                    && !allEntRateChart.get(i).etFatStart.equalsIgnoreCase("")) {
                dblArrayListFat
                        .add(Double.parseDouble(allEntRateChart.get(i).etFatStart));
                dblArrayListSnf
                        .add(Double.parseDouble(allEntRateChart.get(i).etSnfStart));
            }

        }

        Collections.sort(dblArrayListFat);
        Collections.sort(dblArrayListSnf);

        Collections.sort(dblArrListSnf);
        Collections.sort(dblArrListFat);

        double snf = dblArrListSnf.get(0);
        double fat = dblArrListFat.get(0);

        for (int i = 0; i < dblArrListFat.size(); i++) {

            for (int j = 0; j < dblArrListSnf.size(); j++) {
                boolean check = false;
                snf = Double.parseDouble(decimalFormatFS.format(snf));
                fat = Double.parseDouble(decimalFormatFS.format(fat));
                try {
                    check = dbh.checkVacantPosition(String.valueOf(snf),
                            String.valueOf(fat));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (check) {
                    snf = snf + .1;
                } else {
                    dataValid = false;
                    Toast.makeText(
                            RateChartNew.this,
                            "No rate found for fat " + fat + " and snf " + snf
                                    + "", Toast.LENGTH_SHORT).show();
                    return dataValid;
                }

            }
            snf = dblArrListSnf.get(0);
            fat = fat + .1;

        }

        return dataValid;

    }

    public void deleteTable() {
        //TODO
    }
}
