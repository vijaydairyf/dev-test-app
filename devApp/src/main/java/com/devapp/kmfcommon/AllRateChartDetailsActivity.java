package com.devapp.kmfcommon;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.devapp.devmain.agentfarmersplit.AppConstants;
import com.devapp.devmain.dao.DaoFactory;
import com.devapp.devmain.dao.IncentiveRateDao;
import com.devapp.devmain.dao.RateChartNameDao;
import com.devapp.devmain.dao.RateDao;
import com.devapp.devmain.devicemanager.DatabaseManager;
import com.devapp.devmain.encryption.Csv;
import com.devapp.devmain.entity.Entity;
import com.devapp.devmain.entity.RateChartEntity;
import com.devapp.devmain.entity.RateChartPostEntity;
import com.devapp.devmain.entity.RatechartDetailsEnt;
import com.devapp.devmain.entitymanager.RateChartManager;
import com.devapp.devmain.helper.DatabaseEntity;
import com.devapp.devmain.postentities.CollectionConstants;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.server.SessionManager;
import com.devapp.devmain.services.CreateRateChartForSociety;
import com.devapp.devmain.tableEntities.RateChartNameTable;
import com.devapp.devmain.tableEntities.RateTable;
import com.devapp.devmain.usb.ReadExcel;
import com.devapp.devmain.user.AllRateChartActivity;
import com.devapp.devmain.user.RateAdapter;
import com.devapp.devmain.user.RateChartNew;
import com.devapp.devmain.user.Util;
import com.devapp.smartcc.entityandconstants.SmartCCConstants;
import com.devapp.smartcc.entityandconstants.SmartCCUtil;
import com.devApp.R;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class AllRateChartDetailsActivity extends Activity {

    static final int DATE_DIALOG_START_ID = 999;
    private static final int DIALOG_LOAD_FILE = 1000;
    private static final String FTYPE = ".xls";
    static int year;
    static int month;
    static int day;
    public boolean isNormalRateChart = true;
    Button btnSubmt, btnCancel, btnNewRateChart, btnViewArch, btnNonlinearRate,
            btnLinear, btnRateAsso, btnImportRatechart;
    EditText etRateChartName, etValidFrom, etValidTo;
    RateAdapter rateAdapter;
    RelativeLayout progressLayout;
    File mPath;
    ArrayList<RatechartDetailsEnt> allRatechartDetailsEnts = new ArrayList<RatechartDetailsEnt>();
    RatechartDetailsEnt rateChartDetails;
    Button btnCreateNewList;
    ListView lvRateChart;
    DatabaseHandler dbh;
    boolean isValidFrom, isValidTo;
    long validFromIimeStamp, validToTimeStamp;
    AlertDialog alertDialog;
    SessionManager session;
    Spinner spCattleType;
    String fileName;
    AmcuConfig amcuConfig;
    String TAG = "AllRateChartDetailsActivity";
    String milktype = "Cow";
    Runnable rateRunnable;
    Handler rateHandler = new Handler();
    ArrayList<ReadExcel.RateEntityExcel> rateEntity;
    // Rate Chart import from excel

    RateChartNameDao rateChartNameDao;
    RateDao rateDao;
    IncentiveRateDao incentiveRateDao;

    private SmartCCUtil smartCCUtil;

    // display alert
    private String[] mFileList;
    private String mChosenFile;
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

            if (isValidFrom) {
//                validFromIimeStamp = Long.parseLong(new StringBuilder()
//                        .append(year).append(_month).append(_day)
//                        .toString());

                validFromIimeStamp = smartCCUtil.getCalendarTime(
                        year, Integer.valueOf(_day), selectedMonth, 0, 0);

                etValidFrom.setText(new StringBuilder()
                        // Month is 0 based, just add 1
                        .append(_day).append("-").append(_month).append("-")
                        .append(year));

            } else if (isValidTo) {

                validToTimeStamp = smartCCUtil.getCalendarTime(
                        year, Integer.valueOf(_day), selectedMonth, 0, 0);

//                validToTimeStamp = Long.parseLong(new StringBuilder()
//                        .append(year).append(_month).append(_day)
//                        .toString());

                etValidTo.setText(new StringBuilder()
                        // Month is 0 based, just add 1
                        .append(_day).append("-").append(_month).append("-")
                        .append(year));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.rate_chart_details);

        amcuConfig = AmcuConfig.getInstance();
        session = new SessionManager(AllRateChartDetailsActivity.this);
        rateChartNameDao = (RateChartNameDao) DaoFactory.getDao(CollectionConstants.RATECHART_NAME);
        rateDao = (RateDao) DaoFactory.getDao(CollectionConstants.RATES);
        incentiveRateDao = (IncentiveRateDao) DaoFactory.getDao(CollectionConstants.INCENTIVE_RATES);
        smartCCUtil = new SmartCCUtil(AllRateChartDetailsActivity.this);

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

        getAllDatabaseRatechart();

        lvRateChart = (ListView) findViewById(R.id.lvRateChartDetails);
        btnCreateNewList = (Button) findViewById(R.id.btnCreateNewList);

        disPlayList();
        lvRateChart.setSmoothScrollbarEnabled(true);
        lvRateChart.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {

                if (amcuConfig.getCreateAndEdit()) {
                    rateChartDetails = allRatechartDetailsEnts.get(arg2);
                    String rateName = rateChartDetails.rateChartName;

                    Intent intent = new Intent(new Intent(
                            AllRateChartDetailsActivity.this,
                            AllRateChartActivity.class));

                    amcuConfig.setRateChartName(rateName);
                    startActivity(intent);
                }

            }
        });

        lvRateChart.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int arg2, long arg3) {
                final int pos = arg2;
                if (amcuConfig.getCreateAndEdit()) {

                    PopupMenu popup = new PopupMenu(
                            AllRateChartDetailsActivity.this, arg1);
                    popup.getMenuInflater().inflate(R.menu.popup_menu,
                            popup.getMenu());

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                        @Override
                        public boolean onMenuItemClick(MenuItem arg0) {

                            long deleteCk = 0;
                            try {
                                deleteCk = rateChartNameDao.deleteByKey(RateChartNameTable.NAME,
                                        allRatechartDetailsEnts
                                                .get(pos).rateChartName);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            try {
                                if (allRatechartDetailsEnts
                                        .get(pos).ratechartType.equalsIgnoreCase("PROTEIN")) {
                                    incentiveRateDao.deleteByKey(DatabaseEntity.InCentive.RATE_CHART_NAME, allRatechartDetailsEnts.get(pos).rateChartName);
                                } else {

                                    long refId = rateChartNameDao.findRateRefIdFromName(allRatechartDetailsEnts.get(pos).rateChartName);

                                    rateDao.deleteByKey(RateTable.RATE_REF_ID,
                                            String.valueOf(refId));

                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                //DB close removed;
                            }

                            if (deleteCk > 0) {
                                Toast.makeText(
                                        AllRateChartDetailsActivity.this,
                                        "Rate Chart details successfully deleted!",
                                        Toast.LENGTH_SHORT).show();
                                disPlayList();
                            } else {
                                Toast.makeText(
                                        AllRateChartDetailsActivity.this,
                                        "Not deleted", Toast.LENGTH_SHORT)
                                        .show();
                            }

                            return false;
                        }
                    });

                    popup.show();
                }

                return false;
            }
        });

        if (amcuConfig.getImportExcelRateEnable()) {
            btnCreateNewList.setText("Create New Ratechart");
        } else {
            btnCreateNewList.setText("Import Encrypted Ratechart");
        }


        btnCreateNewList.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (amcuConfig.getImportExcelRateEnable()) {
                    alertRatechart();
                } else {
                    isNormalRateChart = false;
                    ImportExcel();
                }

            }
        });
    }

    public void getAllDatabaseRatechart() {
        progressLayout.setVisibility(View.VISIBLE);

        try {
            allRatechartDetailsEnts = rateChartNameDao.findRateChartFromInputs(null, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        progressLayout.setVisibility(View.GONE);
        //DB close removed;
    }

    public void disPlayList() {
        dbh = DatabaseHandler.getDatabaseInstance();
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                rateAdapter = new RateAdapter(AllRateChartDetailsActivity.this,
                        dbh.getRateChartDetailsCursor(), 1);
                lvRateChart.setAdapter(rateAdapter);
            }
        });
    }

    public void alertRatechart() {

        Builder builder = new Builder(
                AllRateChartDetailsActivity.this);

        LayoutInflater inflater = (LayoutInflater) getApplicationContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.alert_rate_chart, null);

        btnLinear = (Button) view.findViewById(R.id.btnLinearRatechart);
        btnNonlinearRate = (Button) view.findViewById(R.id.btnNonLinear);
        btnRateAsso = (Button) view.findViewById(R.id.btnAssignRatchart);
        btnViewArch = (Button) view.findViewById(R.id.btnViewfiles);
        btnImportRatechart = (Button) view.findViewById(R.id.btnImport);
        btnNewRateChart = (Button) view.findViewById(R.id.btnNewRateChart);

        final Button btnEncrypted = (Button) view.findViewById(R.id.btnEncryptedRateChart);

        etRateChartName = (EditText) view.findViewById(R.id.etNameOfRatechart);
        etValidFrom = (EditText) view.findViewById(R.id.etValidFrom);
        etValidTo = (EditText) view.findViewById(R.id.etValidTo);
        spCattleType = (Spinner) view.findViewById(R.id.spCattleType);

        final LinearLayout lnStartDate = (LinearLayout) view.findViewById(R.id.rlStartDate);
        final LinearLayout lnEndDate = (LinearLayout) view.findViewById(R.id.lnValidtoLimit);
        final LinearLayout lnRateChartName = (LinearLayout) view.findViewById(R.id.rlRateChartName);
        final LinearLayout lnMilkType = (LinearLayout) view.findViewById(R.id.rlMlkType);

        final TextView rateNote = (TextView) view.findViewById(R.id.tvrateNote);
        rateNote.setVisibility(View.VISIBLE);

        btnViewArch.setVisibility(View.GONE);
        btnRateAsso.setVisibility(View.GONE);

        btnLinear.setText("Create Rate Chart");
        btnNonlinearRate.setVisibility(View.GONE);

        btnNewRateChart.setText("Create file");
        btnNewRateChart.setVisibility(View.GONE);

        btnImportRatechart.setText("Import Excel Rate Chart");

        if (!amcuConfig.getCreateAndEdit()) {
            btnLinear.setEnabled(amcuConfig.getCreateAndEdit());
            btnLinear.setVisibility(View.GONE);
        }

        spCattleType.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {

                milktype = spCattleType.getItemAtPosition(arg2).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                milktype = "Cow";

            }
        });

        if (!isValidFrom && !isValidTo) {
            initDatePicker();
        }

        btnLinear.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (checkValidation()) {
                    saveRateChartDetails();
                    startActivity(new Intent(AllRateChartDetailsActivity.this,
                            RateChartNew.class));
                    disPlayList();
                    getAllDatabaseRatechart();
                }

                alertDialog.dismiss();

                // if (session.getUserRole().equalsIgnoreCase("manager")) {
                // Intent intent = new Intent(AllRateChartActivity.this,
                // CreateRateChartActivity.class);
                // intent.putExtra("FarmerScanner", "Linear");
                // startActivity(intent);
                // } else {
                // ShowRateList();
                // }
                // alertDialog.dismiss();

            }
        });

        etValidFrom.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                isValidFrom = true;
                isValidTo = false;
                showDialog(DATE_DIALOG_START_ID);

            }
        });

        etValidTo.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                isValidFrom = false;
                isValidTo = true;
                showDialog(DATE_DIALOG_START_ID);
            }
        });

        btnRateAsso.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        });

        btnViewArch.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                AlertChoosenFile();
                alertDialog.dismiss();

            }
        });

        btnImportRatechart.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                isNormalRateChart = true;
                ImportExcel();
            }
        });

        //  btnEncrypted.setVisibility(View.GONE);
        btnEncrypted.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                isNormalRateChart = false;
                ImportExcel();
            }
        });

        btnNewRateChart.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                AfterCreateAndUpdate();
                alertDialog.dismiss();
            }
        });
        alertDialog = builder.create();
        final Dialog finalDialog = alertDialog;
        alertDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_DEL && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                    finalDialog.dismiss();
                    return true;
                }
                return false;
            }
        });
        alertDialog.setView(view);
        alertDialog.show();

    }

    public boolean checkValidation() {

        boolean retbool = false;
        long lnToday = System.currentTimeMillis();
        //  long lnToday = Util.getLong(Util.getTodayDateAndTime(7, AllRateChartDetailsActivity.this, false));
        if (etRateChartName.getText().toString().replace(" ", "").length() > 0
                && etValidFrom.getText().toString().replace(" ", "").length() > 0
                && etValidTo.getText().toString().replace(" ", "").length() > 0) {

            if (validToTimeStamp >= validFromIimeStamp &&
                    validToTimeStamp >= lnToday) {
                retbool = true;
            } else {
                Toast.makeText(AllRateChartDetailsActivity.this,
                        "Please check the validity!", Toast.LENGTH_SHORT)
                        .show();
            }

        } else {
            Toast.makeText(AllRateChartDetailsActivity.this,
                    "Fill all the ratechart details!", Toast.LENGTH_SHORT)
                    .show();
        }

        return retbool;
    }

    RatechartDetailsEnt saveRateChartDetails() {

        RatechartDetailsEnt ratechEnt = new RatechartDetailsEnt();
        ratechEnt.rateChartName = etRateChartName.getText().toString().toUpperCase(Locale.ENGLISH);
        ratechEnt.rateValidityFrom = etValidFrom.getText().toString();
        ratechEnt.rateValidityTo = etValidTo.getText().toString();
        ratechEnt.rateSocId = String.valueOf(session.getSocietyColumnId());
        ratechEnt.rateLvalidityFrom = validFromIimeStamp;

        if (validToTimeStamp > 0) {
            ratechEnt.rateLvalidityTo = validToTimeStamp;
        } else {
            ratechEnt.rateLvalidityTo = validToTimeStamp;
        }

        ratechEnt.isActive = "true";

        ratechEnt.rateMilkType = milktype.toUpperCase(Locale.ENGLISH);
        ratechEnt.rateOther1 = String.valueOf(Util.getLongDateAndTime());
        //Excel support

        ratechEnt.ratechartType = Util.RATECHART_TYPE_COLLECTION;

        DatabaseHandler dbh = DatabaseHandler.getDatabaseInstance(
        );
        try {
            DatabaseManager rcm = new DatabaseManager(AllRateChartDetailsActivity.this);
            rcm.deleteRatechart(ratechEnt);
            rcm.addRateChartDetails(ratechEnt, Util.USB);
            //Removed database close;
            return ratechEnt;
        } catch (Exception e) {
            //Removed database close;
            e.printStackTrace();
            return null;
        }
    }

    private void initDatePicker() {

        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        String _month = String.valueOf(month + 1), _day = String.valueOf(day);

        if (month + 1 < 10)
            _month = "0" + String.valueOf(month + 1);

        if (day < 10)
            _day = "0" + String.valueOf(day);

        etValidFrom.setText(new StringBuilder()
                // Month is 0 based, just add 1
                .append(_day).append("-").append(_month).append("-")
                .append(year));

        validFromIimeStamp = smartCCUtil.getCalendarTime(
                year, Integer.valueOf(_day), month, 0, 0);

//        validFromIimeStamp = Util.getDateInLongFormat(Util
//                .getTodayDateAndTime(1, AllRateChartDetailsActivity.this, false));

    }

    private void loadFileList() {
        try {
            mPath.mkdirs();
        } catch (SecurityException e) {
            Log.e(TAG, "unable to write on the sd card " + e.toString());
        }
        if (mPath.exists()) {
            FilenameFilter filter = new FilenameFilter() {
                public boolean accept(File dir, String filename) {
                    File sel = new File(dir, filename);

                    if (isNormalRateChart) {
                        return filename.contains(FTYPE) || sel.isDirectory();
                    } else {
                        return filename.contains(".txt") || sel.isDirectory() || filename.contains(".stpl") || filename.contains(".jstpl");
                    }

                }
            };
            mFileList = mPath.list(filter);
        } else {
            mFileList = new String[0];
        }

        onCreateDialog(1000);
    }

    @Override
    protected Dialog onCreateDialog(int id) {

        if (id == DIALOG_LOAD_FILE) {
            Dialog dialog = null;
            Builder builder = new Builder(this);
            builder.setTitle("Choose Rate chart");
            if (mFileList == null) {
                Log.e(TAG, "Showing file picker before loading the file list");
                dialog = builder.create();
            }
            builder.setItems(mFileList, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    mChosenFile = mFileList[which];

                    if (mChosenFile != null && mChosenFile.length() > 1) {
                        if (isNormalRateChart) {
                            if (mChosenFile.contains(".xls")) {
                                fileName = fileName + "/" + mChosenFile;

                                readExcelFile(fileName);
                                dialog.dismiss();
                                Toast.makeText(AllRateChartDetailsActivity.this,
                                        "Fetching rate chart from " + mChosenFile,
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                mPath = new File(Util.rootFileName + "/"
                                        + mChosenFile);
                                fileName = fileName + "/" + mChosenFile;
                                mFileList = new String[100];
                                loadFileList();
                                dialog.dismiss();
                            }
                        } else {

                            if (mChosenFile.contains(".txt") || mChosenFile.contains(".stpl")) {
                                fileName = fileName + "/" + mChosenFile;
                                dialog.dismiss();
                                Toast.makeText(AllRateChartDetailsActivity.this,
                                        "Please wait, fetching the rate chart data in progress..",
                                        Toast.LENGTH_LONG).show();
                                readEncryptedRatechart(fileName, AppConstants.TYPE_COLLECTION);

                            } else if (mChosenFile.contains(".jstpl")) {
                                fileName = fileName + "/" + mChosenFile;
                                dialog.dismiss();
                                Toast.makeText(AllRateChartDetailsActivity.this,
                                        "Please wait, fetching the rate chart data in progress..",
                                        Toast.LENGTH_LONG).show();
                                readEncryptedRatechart(fileName, SmartCCConstants.RATECHART_TYPE_PROTEIN);


                            } else {
                                mPath = new File(Util.rootFileName + "/"
                                        + mChosenFile);
                                fileName = fileName + "/" + mChosenFile;
                                mFileList = new String[100];
                                loadFileList();
                                dialog.dismiss();
                            }
                        }

                    } else {
                        mFileList = new String[100];
                    }

                }
            });

            dialog = builder.show();
            final Dialog finalDialog = dialog;
            dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                    if (i == KeyEvent.KEYCODE_DEL && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                        finalDialog.dismiss();
                        return true;
                    }
                    return false;
                }
            });
            return dialog;
        }

        if (id == DATE_DIALOG_START_ID) {
            return new DatePickerDialog(this, datePickerListener, year, month,
                    day);
        }
        return alertDialog;

    }

    public void AlertChoosenFile() {
//        viewXCEL(Uri.fromFile(Environment.getExternalStorageDirectory()));
        viewXCEL(Uri.fromFile(new File(Util.getSDCardPath())));
    }

    private void viewXCEL(Uri file) {
        Intent intent;
        intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(file, "application/vnd.ms-excel");
        try {

            intent.putExtra("RESULT_OK", RESULT_OK);
            startActivityForResult(intent, 0);
        } catch (ActivityNotFoundException e) {
            // No application to view, ask to download one
            Builder builder = new Builder(this);
            builder.setTitle("No Application Found");
            builder.setMessage("No reports and files available.");

            builder.setNegativeButton("Cancel", null);
            builder.create().show();
        }
    }

    public void readExcelFile(String file) {
        final ReadExcel test = new ReadExcel();
        File root = new File(Util.rootFileName);
        final File gpxfile = new File(root, file);
        test.setInputFile(gpxfile.toString(), 1);
        progressLayout.setVisibility(View.VISIBLE);

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    rateEntity = test.read(AllRateChartDetailsActivity.this);
                    rateHandler.post(rateRunnable);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        rateRunnable = new Runnable() {

            @Override
            public void run() {
                if (rateEntity != null) {
                    DatabaseManager rcm = new DatabaseManager(
                            AllRateChartDetailsActivity.this);
                    RatechartDetailsEnt ratechartDetailsEnt = saveRateChartDetails();
                    ArrayList<RateChartEntity> allRateChartEnt = rcm.getAllRateChartEnt(rateEntity,
                            milktype.toUpperCase(Locale.ENGLISH)
                            , etRateChartName.getText().toString().toUpperCase(Locale.ENGLISH));
//                    rcm.addCompleteRateChart(ratechartDetailsEnt, allRateChartEnt, false);
//                    rcm.manageRateChart();
                    ArrayList<RateChartPostEntity> allPostEnt = new ArrayList<>();
                    RateChartPostEntity rateChartPostEntity = new RateChartPostEntity();
                    rateChartPostEntity.setRateChartEntity(ratechartDetailsEnt, allRateChartEnt);
                    allPostEnt.add(rateChartPostEntity);
                    RateChartManager rateChartManager = new RateChartManager(AllRateChartDetailsActivity.this);
                    try {
                        rateChartManager.saveAll((List<? extends Entity>) allPostEnt, false);
                        rcm.manageRateChart();
                        Util.displayErrorToast("Rate chart updated successfully.", AllRateChartDetailsActivity.this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    disPlayList();
                    getAllDatabaseRatechart();

                } else {
                    alertForInvalidExcel();
                }

                progressLayout.setVisibility(View.GONE);
            }
        };
    }

    public void readEncryptedRatechart(String file, String type) {

        File root = new File(Util.rootFileName);
        final File gpxfile = new File(root, file);
        Csv csv = null;
        if (type.equalsIgnoreCase(SmartCCConstants.RATECHART_TYPE_PROTEIN)) {
            csv = new Csv(AllRateChartDetailsActivity.this, Util.INCENTIVE_RATE_CHART);

        } else {
            csv = new Csv(AllRateChartDetailsActivity.this, Util.GETRATECHART);

        }
        try {
            if (csv != null) {
                csv.generateCsvDcryptFormat(gpxfile.toString());
                disPlayList();
                getAllDatabaseRatechart();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void AfterCreateAndUpdate() {
        Toast.makeText(AllRateChartDetailsActivity.this, "Reading rate chart!",
                Toast.LENGTH_SHORT).show();

        int vers = session.getRateChartVersion();
        vers = vers + 1;
        String version = Util.rateChartVersion(vers);
        boolean alreadyPresent = false;
        String fileName = new SessionManager(getApplicationContext())
                .getSocietyName() + "_RateChart" + version + ".xls";
        session.setRecentRateChart(fileName);
        ArrayList<String> listFileName = new ArrayList<String>();
        listFileName = Util.ReadRateChartNAME(AllRateChartDetailsActivity.this,
                0);

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

        Util.WriteRateChartName(AllRateChartDetailsActivity.this, listFileName,
                0);
        session.setRateChartVersion(vers);
        startService(new Intent(AllRateChartDetailsActivity.this,
                CreateRateChartForSociety.class));

    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {

            case KeyEvent.KEYCODE_F9:
                Util.Logout(AllRateChartDetailsActivity.this);
                finish();
                return true;

            case KeyEvent.KEYCODE_F10:
                Util.restartApp(AllRateChartDetailsActivity.this);
                return true;

            case KeyEvent.KEYCODE_F11:

                Util.restartTab(AllRateChartDetailsActivity.this);
                return true;

            case KeyEvent.KEYCODE_F12:
                Util.shutDownTab(AllRateChartDetailsActivity.this, null);
                return true;
            case KeyEvent.KEYCODE_DEL:
                finish();
                return true;

            default:
                return super.onKeyUp(keyCode, event);
        }
    }

    public void ImportExcel() {

        if (!Util.checkForPendrive()) {
            Toast.makeText(AllRateChartDetailsActivity.this,
                    "Please attach the pendrive and try again!",
                    Toast.LENGTH_LONG).show();
        } else if (isNormalRateChart && checkValidation()) {
            fileName = "";
            mPath = new File(Util.rootFileName);
            loadFileList();
            if (alertDialog != null) {
                alertDialog.dismiss();
            }

        } else if (!isNormalRateChart) {
            fileName = "";
            mPath = new File(Util.rootFileName);
            loadFileList();
            if (alertDialog != null) {
                alertDialog.dismiss();
            }
        } else {
            //To do
        }

    }

    public void alertForInvalidExcel() {

        Builder alertDialogBuilder = new Builder(
                AllRateChartDetailsActivity.this);
        // set title
        alertDialogBuilder.setTitle("Invalid excel");
        // set dialog message
        alertDialogBuilder
                .setMessage(
                        "Invalid excel format! Press continue to read again.")
                .setCancelable(false)
                .setPositiveButton("Continue",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                ImportExcel();
                                dialog.cancel();

                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }


}
