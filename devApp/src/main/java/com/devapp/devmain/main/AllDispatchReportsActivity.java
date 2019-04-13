package com.devapp.devmain.main;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.devapp.devmain.dao.DaoFactory;
import com.devapp.devmain.dao.DispatchRecordDao;
import com.devapp.devmain.devicemanager.PrinterManager;
import com.devapp.devmain.postentities.CollectionConstants;
import com.devapp.devmain.postentities.dispatchentities.DispatchPostEntity;
import com.devapp.devmain.server.SessionManager;
import com.devapp.devmain.user.DispatchAdapter;
import com.devapp.devmain.user.FormatPrintRecords;
import com.devapp.devmain.user.Util;
import com.devapp.devmain.util.CursorEndOnFocusChangeListener;
import com.devapp.devmain.util.FocusForwardKeyListener;
import com.devapp.smartcc.entityandconstants.SmartCCUtil;
import com.devApp.R;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

public class AllDispatchReportsActivity extends AppCompatActivity {

    static final int DATE_DIALOG_Start_ID = 999;
    static int year, startYear, endYear;
    static int month, startMonth, endMonth;
    static int day, startDay, endDay;
    boolean startDate, endDate;
    DispatchRecordDao dispatchRecordDao;
    AlertDialog alertDialog;
    private Context context = this;
    private EditText etStartDate, etEndDate;
    private ArrayList<DispatchPostEntity> dispatchPostEntities;
    private TextView tvSociety, tvDate, tvTotalCollection,
            tvTotalAmount;
    private DecimalFormat decimalFormatQty;
    private SessionManager sessionManager;
    private long currentTimeMili = 0;
    private ListView listView;
    private long lnStartDate, lnEndDate,
            startDateIimeStamp, endDateTimeStamp;
    private PrinterManager printerManager;
    private DatePickerDialog.OnDateSetListener datePickerListener =
            new DatePickerDialog.OnDateSetListener() {

                // when dialog box is closed, below method will be called.
                public void onDateSet(DatePicker view, int selectedYear,
                                      int selectedMonth, int selectedDay) {

//                    onCreate = true;
                    //   long currentTime=System.currentTimeMillis();
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));


                    year = selectedYear;
                    month = selectedMonth + 1;
                    day = selectedDay;

                    calendar.set(selectedYear, selectedMonth, selectedDay);
                    String _day = String.valueOf(day);
                    String _month = String.valueOf(month);
                    if (day < 10)
                        _day = "0" + String.valueOf(day);
                    if (month < 10)
                        _month = "0" + String.valueOf(month);

                    // set selected date into textview

                    if (startDate) {

                        calendar.set(selectedYear, selectedMonth, selectedDay, 0, 0, 0);
                        long calendarMiliend = calendar.getTimeInMillis();

                        lnStartDate = calendarMiliend;
                        startYear = year;
                        startMonth = month;
                        startDay = day;
                        startDateIimeStamp = Long.parseLong(new StringBuilder()
                                .append(year).append(_month).append(_day).toString());


                        etStartDate.setText(new StringBuilder().append(year)
                                .append("-").append(_month).append("-")
                                .append(_day));


                    } else if (endDate) {

                        calendar.set(selectedYear, selectedMonth, selectedDay, 23, 59, 59);
                        long calendarMiliend = calendar.getTimeInMillis();

                        lnEndDate = calendarMiliend;
                        endYear = year;
                        endMonth = month;
                        endDay = day;
                        endDateTimeStamp = Long.parseLong(new StringBuilder()
                                .append(year).append(_month).append(_day).toString());


                        etEndDate.setText(new StringBuilder()
                                // Month is 0 based, just add 1
                                .append(year).append("-").append(_month)
                                .append("-").append(_day));


                    }
                    initDatePicker(true);

                }
            };
    private Button btnSubmit, btnBack;

    @Override
    protected Dialog onCreateDialog(int id) {

        return new DatePickerDialog(this, datePickerListener, year, month, day);

    }

    private void initDatePicker(boolean onCreat) {

        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        if (currentTimeMili == 0) {
            c.set(year, month, day, 23, 59, 59);
            currentTimeMili = c.getTimeInMillis();
        }

        String _month = String.valueOf(month + 1), _day = String.valueOf(day);

        if (month + 1 < 10)
            _month = "0" + String.valueOf(month + 1);

        if (day < 10)
            _day = "0" + String.valueOf(day);

        if (!onCreat) {

            etStartDate.setText(new StringBuilder()
                    // Month is 0 based, just add 1
                    .append(year).append("-").append(_month).append("-")
                    .append(_day));

            etEndDate.setText(new StringBuilder()
                    // Month is 0 based, just add 1
                    .append(year).append("-").append(_month).append("-")
                    .append(_day));

//            longToday = Util.getDateInLongFormat(Util.getTodayDateAndTime(1, context, false));
        }

    }

    public void displayReports(View v) {
        if (etStartDate.getText().toString().length() > 4
                && etEndDate.getText().toString().length() > 4) {
            if (lnStartDate == 0 || lnEndDate == 0) {

                Calendar c = Calendar.getInstance();
                c.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
                c.set(year, month, day, 0, 0, 0);
                if (lnStartDate == 0)
                    lnStartDate = c.getTimeInMillis();
                if (lnEndDate == 0) {
                    c.set(year, month, day, 23, 59, 59);
                    lnEndDate = c.getTimeInMillis();
                }
            }
            boolean validate = validateAndErrorToast(null, etStartDate.getText().toString().trim()
                    , etEndDate.getText().toString().trim());
            if (!validate) {
                return;
            }
            displayReportsDialog();
        } else {
            Toast.makeText(context,
                    "Please enter start and end date!", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_dispatch_reports);
        etStartDate = (EditText) findViewById(R.id.et_start_date);
        etEndDate = (EditText) findViewById(R.id.et_end_date);
        btnSubmit = (Button) findViewById(R.id.btn_reports);
        btnBack = (Button) findViewById(R.id.btn_back);
        etStartDate.setOnKeyListener(new FocusForwardKeyListener(etEndDate));
        etEndDate.setOnKeyListener(new FocusForwardKeyListener(btnSubmit));

        etStartDate.setOnFocusChangeListener(new CursorEndOnFocusChangeListener());
        etEndDate.setOnFocusChangeListener(new CursorEndOnFocusChangeListener());
        sessionManager = new SessionManager(context);
        decimalFormatQty = new DecimalFormat("#0.00");
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        etStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDate = true;
                endDate = false;
                showDialog(DATE_DIALOG_Start_ID);
            }
        });
        etEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endDate = true;
                startDate = false;
                showDialog(DATE_DIALOG_Start_ID);
            }
        });
        printerManager = new PrinterManager(this);
        initDatePicker(false);
        dispatchRecordDao = (DispatchRecordDao) DaoFactory.getDao(CollectionConstants.REPORT_TYPE_DISPATCH);
    }

    private void displayReportsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        FormatPrintRecords formatPrintRecords = new FormatPrintRecords(context);
        final String strBuild = formatPrintRecords.getDispatchReport(lnStartDate, lnEndDate);
//        detailsCheck = 0;

        LayoutInflater inflater = (LayoutInflater) getApplicationContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dispatch_report, null);

        tvSociety = (TextView) view.findViewById(R.id.tvSociety);
        tvDate = (TextView) view.findViewById(R.id.tv_date);
        tvTotalCollection = (TextView) view.findViewById(R.id.tv_total_collection);
        tvTotalAmount = (TextView) view.findViewById(R.id.tv_total_amount);
        listView = (ListView) view.findViewById(R.id.lv_dispatch_list);
        tvDate.setText("From " + etStartDate.getText().toString() + " to " + etEndDate.getText().toString());
        tvSociety.setText(sessionManager.getSocietyName());
        Button btnSend = (Button) view.findViewById(R.id.btnSend);
        btnSend.setVisibility(View.GONE);
        Button btnPrint = (Button) view.findViewById(R.id.btnPrint);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                populateViews(tvTotalCollection);

            }
        });
        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setView(view);
        alertDialog.show();
        alertDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        alertDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface dialog, int keyCode,
                                 KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    alertDialog.dismiss();
                } else if (keyCode == KeyEvent.KEYCODE_F2) {

                   /* if (SplashActivity.sendCount == 0) {
                        checkForPenDrive(true);
                    } else {
                        Toast.makeText(ReportsActivity.this, "Please wait!",
                                Toast.LENGTH_SHORT).show();
                    }*/

                } else if (keyCode == KeyEvent.KEYCODE_F3) {
                    // getAllRepEntity(detailsCheck);
                    // DailyShiftReport();
                    // isAlert = false;
                    // alertDialog.dismiss();
                } else if (keyCode == KeyEvent.KEYCODE_F4) {

                    alertDialog.dismiss();
                } else if (keyCode == KeyEvent.KEYCODE_ESCAPE) {
                    alertDialog.dismiss();
                } else if (keyCode == KeyEvent.KEYCODE_DEL) {
                    if (event.getAction() == KeyEvent.ACTION_UP) {
                        alertDialog.dismiss();
                        return true;
                    }
                }
                return false;
            }
        });
        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                printerManager.print(strBuild,
                        PrinterManager.printDispatchReport,
                        getDate(etStartDate.getText().toString()),
                        getDate(etEndDate.getText().toString()), null);
                alertDialog.dismiss();
            }
        });
    }

    private String getDate(String date) {
        try {
            date = SmartCCUtil.changeDateFormat(date,
                    "yyyy-MM-dd", "dd-MM-yyyy");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }


    private void populateViews(TextView textView) {
        dispatchPostEntities = (ArrayList<DispatchPostEntity>) dispatchRecordDao.getDispatchEntities(lnStartDate, lnEndDate);
        double qty = 0;
        if (dispatchPostEntities.size() > 0) {
            for (int i = 0; i < dispatchPostEntities.size(); i++)
                qty = qty + dispatchPostEntities.get(i).quantityParams.weighingScaleData.measuredValue;
        }
        textView.setText(decimalFormatQty.format(qty));
        //todo
        DispatchAdapter dispatchAdapter = new DispatchAdapter(AllDispatchReportsActivity.this
                , dispatchRecordDao.getDispatchReports(lnStartDate, lnEndDate));
        listView.setAdapter(dispatchAdapter);
    }


    public boolean validateAndErrorToast(String date, String startDate, String endDate) {
        boolean returnValue = true;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);
        try {
            sdf.parse(startDate);
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(context, "Invalid Date", Toast.LENGTH_SHORT).show();
            etStartDate.requestFocus();
            return false;
        }
        try {
            sdf.parse(endDate);
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(context, "Invalid Date", Toast.LENGTH_SHORT).show();
            etEndDate.requestFocus();
            return false;
        }
        long lnTodayDate = Util.getDateInLongFormat(
                Util.convertDate(Util.getTodayDateAndTime(7, context, false)));

        if (date != null) {
            long lnDate = Util.getDateInLongFormat(date);
            if (lnDate > lnTodayDate) {
                Toast.makeText(context, "Date should be less than today date", Toast.LENGTH_SHORT).show();
                returnValue = false;
            }

        } else {
            long lnStartDate = Util.getDateInLongFormat(startDate);
            long lnEndDate = Util.getDateInLongFormat(endDate);

            if (lnEndDate > lnTodayDate) {
                Toast.makeText(context, "End Date should be less than today date", Toast.LENGTH_SHORT).show();
                returnValue = false;
            } else if (lnEndDate < lnStartDate) {
                Toast.makeText(context, "End Date should be greater than start date", Toast.LENGTH_SHORT).show();
                returnValue = false;
            }

        }

        return returnValue;

    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_F10:
                Util.restartApp(AllDispatchReportsActivity.this);
                return true;
            case KeyEvent.KEYCODE_F11:
                Util.restartTab(AllDispatchReportsActivity.this);
                return true;
            case KeyEvent.KEYCODE_F12:
                Util.shutDownTab(AllDispatchReportsActivity.this, null);
                return true;
            case KeyEvent.KEYCODE_DEL:
                if (!(etStartDate.hasFocus() || etEndDate.hasFocus())) {
                    if (alertDialog == null || !alertDialog.isShowing()) {
                        finish();
                        return true;
                    }
                }
            default:
                return super.onKeyUp(keyCode, event);
        }
    }

}
