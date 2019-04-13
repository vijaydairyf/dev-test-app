package com.devapp.devmain.user;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ipaulpro.afilechooser.utils.FileUtils;
import com.devapp.devmain.dao.DaoFactory;
import com.devapp.devmain.dao.FarmerDao;
import com.devapp.devmain.dao.RateChartNameDao;
import com.devapp.devmain.dao.RateDao;
import com.devapp.devmain.entity.FarmerEntity;
import com.devapp.devmain.entity.RateChartEntity;
import com.devapp.devmain.postentities.CollectionConstants;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.server.SessionManager;
import com.devapp.devmain.services.CreateRateChartForSociety;
import com.devApp.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class CreateRateChartActivity extends Activity implements
        OnClickListener {

    static final int DATE_DIALOG_Start_ID = 999;
    private static final int REQUEST_CODE = 6384;
    static int year;
    static int month;
    static int day;
    boolean starDate, endDate;
    EditText etFatStart, etFatEnd, etSnfStart, etSnfEnd, etRate, etStartDate,
            etEndDate, etRatePerSNF, etRatePerFat, etClrStart, etClrEnd,
            etRatePerClr;
    Spinner spMilkType, spFarmer;
    ListView lvRateChart;
    DatabaseHandler databaseHelper;
    LinearLayout lnIncreaseFat, lnIncreaseSnf, lnStartDate, lnClr,
            lnIncreaseClr;
    TextView tvUserName, tvFarmer;
    Button btnCreate, btnCancel, btnList, btnSelect, btnAddDetails;
    String strMilkType, stFarmer, farmerScanner;
    RateAdapter rateAdapter;
    AlertDialog alertDialog;
    boolean isUnregistered, isButtonDetails;
    RelativeLayout progressLayout;
    ArrayList<RateChartEntity> allRateChartEntity, allDatabaseRatechart;
    long StartDateIimeStamp, EndDateTimeStamp;
    RateChartEntity UpdaterateChartEnt, getRateChartEnt;
    String[] listMilkType = new String[10];

    boolean isUpdate;
    boolean isManagerActivity, onClickNext;
    ArrayList<String> allFarmerId = new ArrayList<String>();
    ArrayList<FarmerEntity> allFarmerntity = new ArrayList<FarmerEntity>();

    int rateID = 0;
    SessionManager session;
    AmcuConfig amcuConfig;
    String rateChartname;

    DecimalFormat decimalFormatAMT = new DecimalFormat("#0.00");
    DecimalFormat decimalFormatFS = new DecimalFormat("#0.0");
    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            year = selectedYear;
            month = selectedMonth + 1;
            day = selectedDay;
            String _day = String.valueOf(day);
            String _month = String.valueOf(month);
            if (day < 10)
                _day = "0" + String.valueOf(day);
            if (month < 10)
                _month = "0" + String.valueOf(month);

            // set selected date into textview

            if (starDate) {
                StartDateIimeStamp = Long.parseLong(new StringBuilder()
                        .append(year).append(_month).append(_day)
                        .append("000000").toString());

                etStartDate.setText(new StringBuilder()
                        // Month is 0 based, just add 1
                        .append(_day).append("-").append(_month).append("-")
                        .append(year));

            } else {
                EndDateTimeStamp = Long.parseLong(new StringBuilder()
                        .append(year).append(_month).append(_day)
                        .append("000000").toString());

                etEndDate.setText(new StringBuilder()
                        // Month is 0 based, just add 1
                        .append(_day).append("-").append(_month).append("-")
                        .append(year));

            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.create_ratechart);

        amcuConfig = AmcuConfig.getInstance();
        rateChartname = amcuConfig.getRateChartName();

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

        try {
            isUpdate = getIntent().getBooleanExtra("isUpdate", false);
            getRateChartEnt = (RateChartEntity) getIntent()
                    .getSerializableExtra("rcEntity");
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        etFatEnd = (EditText) findViewById(R.id.etFatLimit);
        etFatStart = (EditText) findViewById(R.id.etFatstart);
        etRate = (EditText) findViewById(R.id.etRate);
        etSnfEnd = (EditText) findViewById(R.id.etSnfLimit);
        etSnfStart = (EditText) findViewById(R.id.etSnfstart);
        etStartDate = (EditText) findViewById(R.id.etStartdate);
        etEndDate = (EditText) findViewById(R.id.etEndDate);
        etRatePerFat = (EditText) findViewById(R.id.etRatePerFat);
        etRatePerSNF = (EditText) findViewById(R.id.etRatePerSNF);
        etRatePerClr = (EditText) findViewById(R.id.etRatePerClr);
        etClrStart = (EditText) findViewById(R.id.etClRStart);
        etClrEnd = (EditText) findViewById(R.id.etCLRLimit);
        etRatePerClr = (EditText) findViewById(R.id.etRatePerClr);

        lnIncreaseFat = (LinearLayout) findViewById(R.id.lnRatePerFat);
        lnIncreaseSnf = (LinearLayout) findViewById(R.id.lnRatePerSNF);
        lnStartDate = (LinearLayout) findViewById(R.id.lnstartdate);
        lnClr = (LinearLayout) findViewById(R.id.lnClR);
        lnIncreaseClr = (LinearLayout) findViewById(R.id.lnRateperClr);

        spMilkType = (Spinner) findViewById(R.id.spMilktype);
        spFarmer = (Spinner) findViewById(R.id.spFarmer);
        tvFarmer = (TextView) findViewById(R.id.tvFarmer);

        session = new SessionManager(CreateRateChartActivity.this);

        try {
            isManagerActivity = getIntent().getBooleanExtra("ManagerRateChart",
                    false);
            farmerScanner = getIntent().getStringExtra("FarmerScanner");
        } catch (Exception e) {
            e.printStackTrace();
        }

        etStartDate.setOnClickListener(this);
        etEndDate.setOnClickListener(this);

        listMilkType = getApplicationContext().getResources().getStringArray(
                R.array.Milk_type);

        tvFarmer.setVisibility(View.GONE);

        getFarmerList();

        spFarmer.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {

                stFarmer = spFarmer.getItemAtPosition(arg2).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        spMilkType.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                strMilkType = spMilkType.getItemAtPosition(arg2).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        tvUserName = (TextView) findViewById(R.id.tvUser);

        tvUserName.setText(new SessionManager(CreateRateChartActivity.this)
                .getSocietyName());

        btnCreate = (Button) findViewById(R.id.btnCreate);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnList = (Button) findViewById(R.id.btnList);
        btnSelect = (Button) findViewById(R.id.btnSelect);
        btnAddDetails = (Button) findViewById(R.id.btnAddClr);

        btnAddDetails.setText("Add CLR");

        btnCreate.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnList.setOnClickListener(this);
        btnSelect.setOnClickListener(this);
        btnAddDetails.setOnClickListener(this);

        lnClr.setVisibility(View.GONE);
        lnIncreaseClr.setVisibility(View.GONE);
        btnList.setVisibility(View.GONE);

        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        if (isManagerActivity) {

            managerActivity();
        }
        allRateChartEntity = new ArrayList<RateChartEntity>();

        // /

        if (isUpdate && getRateChartEnt != null) {
            setVisibility();
            setSelectedData(getRateChartEnt);

        }
    }

    public void getEditValidation() {
        Util.alphabetValidation(etSnfStart, Util.ONLY_DECIMAL, CreateRateChartActivity.this, 5);
        Util.alphabetValidation(etSnfEnd, Util.ONLY_DECIMAL, CreateRateChartActivity.this, 5);
        Util.alphabetValidation(etFatStart, Util.ONLY_DECIMAL, CreateRateChartActivity.this, 5);
        Util.alphabetValidation(etFatEnd, Util.ONLY_DECIMAL, CreateRateChartActivity.this, 5);
        Util.alphabetValidation(etRate, Util.ONLY_DECIMAL, CreateRateChartActivity.this, 6);
        Util.alphabetValidation(etRatePerClr, Util.ONLY_DECIMAL, CreateRateChartActivity.this, 6);

        Util.alphabetValidation(etRatePerFat, Util.ONLY_DECIMAL, CreateRateChartActivity.this, 6);
        Util.alphabetValidation(etRatePerSNF, Util.ONLY_DECIMAL, CreateRateChartActivity.this, 6);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnCreate:
                onClickNext = false;
                progressLayout.setVisibility(View.VISIBLE);
                if (isUpdate) {
                    Toast.makeText(CreateRateChartActivity.this,
                            "Table is updating..", Toast.LENGTH_SHORT).show();

                    UpdateData();
                } else {
                    if (Double.parseDouble(etSnfEnd.getText().toString()) > 25
                            && Double.parseDouble(etFatEnd.getText().toString()) > 25) {
                        Toast.makeText(CreateRateChartActivity.this,
                                "Please check fat and snf values..",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        ratechartCreate();
                    }

                }
                break;

            case R.id.btnCancel: {

                if (btnCancel.getText().toString().equalsIgnoreCase("Next")) {
                    progressLayout.setVisibility(View.VISIBLE);
                    onClickNext = true;
                    ratechartCreate();
                } else {
                    finish();
                }
            }
            break;

            case R.id.btnList: {
                isUpdate = false;
                rateID = 0;
                getAllDatabaseRatechart();
                ShowRateList();
            }
            break;

            case R.id.etStartdate:
                starDate = true;
                endDate = false;
                showDialog(DATE_DIALOG_Start_ID);

                break;
            case R.id.etEndDate:
                starDate = false;
                endDate = true;
                showDialog(DATE_DIALOG_Start_ID);
                break;

            case R.id.btnAddClr: {
                if (isButtonDetails) {
                    isButtonDetails = false;
                    btnAddDetails.setText("Add CLR");
                } else {
                    isButtonDetails = true;
                    btnAddDetails.setText("Remove CLR");
                }

                onClickButtonDetails();
            }
            break;

            default:
                break;
        }

    }

    @Override
    protected Dialog onCreateDialog(int id) {

        return new DatePickerDialog(this, datePickerListener, year, month, day);
    }

    public void Save() {

        double dblRateChangePerCLR = 0, dblClrStart = 0, dblClrEnd = 0, dblClrDifference = 0;
        double dblRateChangePerSNF = 0, dblRateChangePerFat = 0, dblTotalRate, dblSnfStart = 0, dblFatStart = 0, dblSnfEnd = 0, dblFatEnd = 0, dblRate = 0, dblFatDifference, dblSnfDifference;

        if (!etFatStart.getText().toString().replace(" ", "")
                .equalsIgnoreCase("")) {
            dblFatStart = Double.parseDouble(etFatStart.getText().toString());
        } else {

            progressLayout.setVisibility(View.GONE);
            Toast.makeText(CreateRateChartActivity.this,
                    "Please fill the details", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!etSnfStart.getText().toString().replace(" ", "")
                .equalsIgnoreCase("")) {
            dblSnfStart = Double.parseDouble(etSnfStart.getText().toString());
        } else {
            progressLayout.setVisibility(View.GONE);
            Toast.makeText(CreateRateChartActivity.this,
                    "Please fill the details", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!etClrStart.getText().toString().replace(" ", "")
                .equalsIgnoreCase("")) {
            dblClrStart = Double.parseDouble(etClrStart.getText().toString());
        }

        if (!etRate.getText().toString().replace(" ", "").equalsIgnoreCase("")) {
            dblRate = Double.parseDouble(etRate.getText().toString());
        } else {
            progressLayout.setVisibility(View.GONE);
            Toast.makeText(CreateRateChartActivity.this,
                    "Please fill the details", Toast.LENGTH_SHORT).show();
            return;
        }

        if (etFatEnd.getText().toString().replace(" ", "").equalsIgnoreCase("")) {

            dblFatEnd = dblFatStart;

        } else {
            dblFatEnd = Double.parseDouble(etFatEnd.getText().toString());
        }
        if (etSnfEnd.getText().toString().replace(" ", "").equalsIgnoreCase("")) {
            dblSnfEnd = dblSnfStart;

        } else {
            dblSnfEnd = Double.parseDouble(etSnfEnd.getText().toString());
        }
        if (etRatePerSNF.getText().toString().replace(" ", "")
                .equalsIgnoreCase("")) {
            dblRateChangePerSNF = 0;

        } else {
            dblRateChangePerSNF = Double.parseDouble(etRatePerSNF.getText()
                    .toString());
        }
        if (etRatePerFat.getText().toString().replace(" ", "")
                .equalsIgnoreCase("")) {
            dblRateChangePerFat = 0;
        } else {
            dblRateChangePerFat = Double.parseDouble(etRatePerFat.getText()
                    .toString());
        }

        if (isButtonDetails) {
            if (etClrEnd.getText().toString().replace(" ", "")
                    .equalsIgnoreCase("")) {
                dblClrEnd = dblClrStart;
            } else {
                dblClrEnd = Double.parseDouble(etClrEnd.getText().toString());
            }
            if (etRatePerClr.getText().toString().replace(" ", "")
                    .equalsIgnoreCase("")) {
                dblRateChangePerCLR = 0;

            } else {
                dblRateChangePerCLR = Double.parseDouble(etRatePerClr.getText()
                        .toString());
            }
            dblClrDifference = dblClrEnd - dblClrStart;
        }

        dblTotalRate = dblRate;

        dblFatDifference = dblFatEnd - dblFatStart;
        dblSnfDifference = dblSnfEnd - dblSnfStart;

        int snfCount = 0, fatCount = 0, ClrCount = 0;
        double snfIncrease = 0, snfRate = 0, clrRate = 0;

        if (dblFatDifference >= 0 && dblSnfDifference >= 0) {

            for (double i = dblFatStart; i <= dblFatEnd; ) {

                snfCount = 0;

                snfIncrease = fatCount * dblRateChangePerFat;

                snfRate = dblRate + snfIncrease;

                for (double k = dblSnfStart; k <= dblSnfEnd; ) {
                    dblTotalRate = snfRate + (snfCount * dblRateChangePerSNF);

                    dblTotalRate = Double.valueOf(decimalFormatAMT
                            .format(dblTotalRate));

                    RateChartEntity RCEntity = new RateChartEntity();
                    RCEntity.snf = Double.valueOf(decimalFormatFS.format(k));
                    RCEntity.fat = Double.valueOf(decimalFormatFS.format(i));

                    if (isUnregistered) {
                        RCEntity.farmerId = "UNREGISTERED";
                    } else {
                        RCEntity.farmerId = stFarmer;
                    }

                    RCEntity.societyId = String.valueOf(session
                            .getSocietyColumnId());
                    RCEntity.milkType = strMilkType;
                    RCEntity.managerID = tvUserName.getText().toString();
                    RCEntity.rate = dblTotalRate;
                    RCEntity.rateChartName = rateChartname;

                    allRateChartEntity.add(RCEntity);
                    k = k + 0.1;
                    snfCount = snfCount + 1;

                }
                i = i + 0.1;
                fatCount = fatCount + 1;
            }

        } else {
            Toast.makeText(CreateRateChartActivity.this,
                    "Please check the fat and snf values!", Toast.LENGTH_SHORT)
                    .show();
        }

        if (!onClickNext) {
            if (allRateChartEntity.size() > 0) {
                CreateDatabaseTable();
            } else {

                Toast.makeText(CreateRateChartActivity.this,
                        "Please check the parameters!", Toast.LENGTH_SHORT)
                        .show();
                allRateChartEntity = new ArrayList<RateChartEntity>();
                progressLayout.setVisibility(View.GONE);

            }
        } else {
            onClickNext();
        }
        progressLayout.setVisibility(View.GONE);

    }

    public void CreateDatabaseTable() {
        DatabaseHandler db = DatabaseHandler.getDatabaseInstance();

        if (isUpdate) {
            try {
                db.updateRateChart(UpdaterateChartEnt);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Toast.makeText(CreateRateChartActivity.this,
                    "Rate chart updated successfully!", Toast.LENGTH_SHORT)
                    .show();
            finish();
        } else {

            if (allRateChartEntity != null && allRateChartEntity.size() > 0) {
                try {
                    db.insertRateChartFromExcel(allRateChartEntity, DatabaseHandler.isPrimary);
                    db.insertRateChartFromExcel(allRateChartEntity, DatabaseHandler.isSecondary);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            Toast.makeText(CreateRateChartActivity.this,
                    "Rate chart created successfully!", Toast.LENGTH_SHORT)
                    .show();
            finish();
            allRateChartEntity = new ArrayList<RateChartEntity>();
        }
        //DB close removed;
        progressLayout.setVisibility(View.GONE);
        AfterCreateAndUpdate();
    }

    public void Cancel() {
    }

    public void ShowRateList() {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                CreateRateChartActivity.this);

        LayoutInflater inflater = (LayoutInflater) getApplicationContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.alert_rate, null);
        databaseHelper = DatabaseHandler.getDatabaseInstance();
        lvRateChart = (ListView) view.findViewById(R.id.lvFSnf);
        Button btnCreateRate = (Button) view
                .findViewById(R.id.btnCreateRateChart);

        // Database query can be a time consuming task ..
        // so its safe to call database query in another thread
        // Handler, will handle this stuff for you <img
        // src="http://s0.wp.com/wp-includes/images/smilies/icon_smile.gif?m=1129645325g"
        // alt=":)" class="wp-smiley">

        btnCreateRate.setVisibility(View.GONE);
        setVisibility();

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                RateDao rateDao = (RateDao) DaoFactory.getDao(CollectionConstants.RATES);
                RateChartNameDao rateChartNameDao = (RateChartNameDao) DaoFactory.getDao(CollectionConstants.RATECHART_NAME);

                long refId = rateChartNameDao.findRateRefIdFromName(amcuConfig.getRateChartName());

                rateAdapter = new RateAdapter(CreateRateChartActivity.this,
                        databaseHelper.getAllData(
                                refId), 0);
                lvRateChart.setAdapter(rateAdapter);
            }
        });

        lvRateChart.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {

                if (arg2 <= allDatabaseRatechart.size()) {
                    isUpdate = true;
                    RateChartEntity rcEntity = allDatabaseRatechart.get(arg2);
                    setSelectedData(rcEntity);
                    alertDialog.cancel();
                } else {
                    alertDialog.dismiss();
                }

            }
        });

        alertDialog = builder.create();

        alertDialog.setView(view);
        alertDialog.show();

    }

    public void setSelectedData(RateChartEntity RCEntity) {
        btnCreate.setText("Update");
        setVisibility();
        rateID = RCEntity.columnId;
        etFatStart.setText(String.valueOf(RCEntity.fat));
        etSnfStart.setText(String.valueOf(RCEntity.snf));
        etRate.setText(String.valueOf(RCEntity.rate));
        tvUserName.setText(RCEntity.societyId);
        etEndDate.setText(RCEntity.endDate);
        etStartDate.setText(RCEntity.startDate);

        if (RCEntity.farmerId.equalsIgnoreCase("Unregistered")) {
            isUnregistered = true;
            tvFarmer.setText(RCEntity.farmerId);
            spFarmer.setVisibility(View.GONE);
            tvFarmer.setVisibility(View.VISIBLE);

        } else {
            isUnregistered = false;
            spFarmer.setVisibility(View.VISIBLE);
            tvFarmer.setVisibility(View.GONE);
            ArrayAdapter myAdap = (ArrayAdapter) spFarmer.getAdapter();
            int spinnerPosition = myAdap.getPosition(RCEntity.farmerId);

            spFarmer.setSelection(spinnerPosition);
            myAdap = (ArrayAdapter) spMilkType.getAdapter();
        }

        for (int j = 0; j < listMilkType.length; j++) {
            if (listMilkType[j].equalsIgnoreCase(RCEntity.milkType)) {
                spMilkType.setSelection(j);
                // It will return from the function
                return;
            }
        }

    }

    public void setVisibility() {
        if (isUpdate) {
            etFatEnd.setVisibility(View.GONE);
            etSnfEnd.setVisibility(View.GONE);
            lnIncreaseFat.setVisibility(View.GONE);
            lnIncreaseSnf.setVisibility(View.GONE);
            etFatStart.setEnabled(false);
            etSnfStart.setEnabled(false);
            etRate.requestFocus();
        } else {
            btnCreate.setText("Create");
            etFatEnd.setVisibility(View.VISIBLE);
            etSnfEnd.setVisibility(View.VISIBLE);
            lnIncreaseFat.setVisibility(View.VISIBLE);
            lnIncreaseSnf.setVisibility(View.VISIBLE);
            etFatStart.setEnabled(true);
            etSnfStart.setEnabled(true);
        }

    }

    public void UpdateData() {

        double dblSnfStart, dblFatStart, dblRate;
        dblFatStart = Double.parseDouble(etFatStart.getText().toString());
        dblSnfStart = Double.parseDouble(etSnfStart.getText().toString());
        dblRate = Double.parseDouble(etRate.getText().toString());

        RateChartEntity RCEntity = new RateChartEntity();
        RCEntity.snf = dblSnfStart;
        RCEntity.fat = dblFatStart;
        RCEntity.endDate = etEndDate.getText().toString();
        RCEntity.startDate = etStartDate.getText().toString();

        if (isUnregistered) {
            RCEntity.farmerId = "UNREGISTERED";
        } else {
            RCEntity.farmerId = stFarmer;
        }
        RCEntity.milkType = strMilkType;
        RCEntity.societyId = String.valueOf(session.getSocietyColumnId());
        RCEntity.managerID = tvUserName.getText().toString();
        RCEntity.rate = dblRate;
        RCEntity.columnId = rateID;

        UpdaterateChartEnt = RCEntity;
        CreateDatabaseTable();
    }

    public void getAllDatabaseRatechart() {
        allDatabaseRatechart = new ArrayList<RateChartEntity>();
        RateDao rateDao = (RateDao) DaoFactory.getDao(CollectionConstants.RATES);
        RateChartNameDao rateChartNameDao = (RateChartNameDao) DaoFactory.getDao(CollectionConstants.RATECHART_NAME);

        long refId = rateChartNameDao.findRateRefIdFromName(amcuConfig.getRateChartName());
        try {
            allDatabaseRatechart = rateDao.findAllByName(refId);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void AfterCreateAndUpdate() {

        int vers = 1;
        String version = Util.rateChartVersion(vers);
        boolean alreadyPresent = false;
        String fileName = new SessionManager(getApplicationContext())
                .getSocietyName() + "_RateChart" + version + ".xls";
        session.setRecentRateChart(fileName);

        ArrayList<String> listFileName = new ArrayList<String>();

        listFileName = Util.ReadRateChartNAME(CreateRateChartActivity.this, 0);

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

        isUpdate = false;
        setVisibility();

        spFarmer.setSelection(0);

        spMilkType.setSelection(0);
        etFatStart.setText("3.50");
        etSnfStart.setText("8.50");
        etFatEnd.setText("");
        etSnfEnd.setText("");
        etRate.setText("21.00");
        etRatePerFat.setText("");
        etRatePerSNF.setText("");

        Util.WriteRateChartName(CreateRateChartActivity.this, listFileName, 0);

        startService(new Intent(CreateRateChartActivity.this,
                CreateRateChartForSociety.class));

    }

    public void ratechartCreate() {

        if (!onClickNext) {
            Toast.makeText(CreateRateChartActivity.this, "Table is creating..",
                    Toast.LENGTH_SHORT).show();
        }

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                Save();
            }
        });
    }

    public void managerActivity() {
        String strSocietyId = getIntent().getExtras().getString("centerId");
        String strFarmerId = getIntent().getExtras().getString("FarmerId");
        String strMilkType = getIntent().getExtras().getString("MilkType");
        isUnregistered = getIntent().getExtras().getBoolean("Unregistered",
                false);

        if (isUnregistered) {
            tvFarmer.setVisibility(View.VISIBLE);
            spFarmer.setVisibility(View.GONE);
            tvFarmer.setText("Unregistered");
        } else {
            tvFarmer.setVisibility(View.GONE);
            spFarmer.setVisibility(View.VISIBLE);
        }

        if (farmerScanner != null
                && farmerScanner.equalsIgnoreCase("NonLinear")) {
            btnCancel.setText("Next");

            etEndDate.setVisibility(View.GONE);
            etStartDate.setVisibility(View.GONE);
            btnSelect.setVisibility(View.GONE);
        }

        tvUserName.setText(String.valueOf(session.getSocietyColumnId()));

        ArrayAdapter myAdap = (ArrayAdapter) spFarmer.getAdapter();
        int spinnerPosition = myAdap.getPosition(strFarmerId);

        spFarmer.setSelection(spinnerPosition);
        myAdap = (ArrayAdapter) spMilkType.getAdapter();

        spinnerPosition = myAdap.getPosition(strMilkType);
        spMilkType.setSelection(spinnerPosition);

        spFarmer.setEnabled(false);
        spMilkType.setEnabled(false);

    }

    public void onClickNext() {
        etSnfEnd.setText("");
        etSnfStart.setText("");
        etFatEnd.setText("");
        etFatStart.setText("");
        etRate.setText("");
        etRatePerFat.setText("");
        etRatePerSNF.setText("");
    }

    public void getFarmerList() {
        DatabaseHandler db = DatabaseHandler.getDatabaseInstance();
        FarmerDao farmerDao = (FarmerDao) DaoFactory.getDao(CollectionConstants.FARMER);
        try {
            allFarmerntity = (ArrayList<FarmerEntity>) farmerDao.findAll();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (allFarmerntity != null && allFarmerntity.size() > 0) {
            for (int i = 0; i < allFarmerntity.size(); i++) {
                allFarmerId.add(allFarmerntity.get(i).farmer_id);
            }
        }

        //DB close removed;
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
                CreateRateChartActivity.this,
                android.R.layout.simple_spinner_item, allFarmerId);
        dataAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFarmer.setAdapter(dataAdapter);

    }

    private void showChooser() {
        // Use the GET_CONTENT intent from the utility class
        Intent target = FileUtils.createGetContentIntent();
        // Create the chooser Intent
        Intent intent = Intent.createChooser(target,
                getString(R.string.chooser_title));
        try {
            startActivityForResult(intent, REQUEST_CODE);
        } catch (ActivityNotFoundException e) {
            // The reason for the existence of aFileChooser
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE:
                // If the file selection was successful
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        // Get the URI of the selected file
                        final Uri uri = data.getData();
                        try {
                            // Get the file path from the URI
                            final String path = FileUtils.getPath(this, uri);
                            Toast.makeText(CreateRateChartActivity.this,
                                    "File Selected: " + path, Toast.LENGTH_LONG)
                                    .show();
                        } catch (Exception e) {

                        }
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void onClickButtonDetails() {
        if (isButtonDetails) {
            lnClr.setVisibility(View.VISIBLE);
            lnIncreaseClr.setVisibility(View.VISIBLE);
        } else {
            lnClr.setVisibility(View.GONE);
            lnIncreaseClr.setVisibility(View.GONE);
        }
    }
}
