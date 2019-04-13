package com.devapp.devmain.encryption;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.devapp.devmain.ConsolidationPost.DateShiftEntry;
import com.devapp.devmain.agentfarmersplit.AppConstants;
import com.devapp.devmain.helper.ReportHelper;
import com.devapp.devmain.main.FarmerScannerActivity;
import com.devapp.devmain.tabs.DatePickerFragment;
import com.devapp.devmain.user.Util;
import com.devapp.devmain.util.CursorEndOnFocusChangeListener;
import com.devapp.devmain.util.FocusForwardKeyListener;
import com.devapp.smartcc.adapters.AutoTextAdapter;
import com.devapp.smartcc.entityandconstants.SmartCCConstants;
import com.devapp.smartcc.entityandconstants.SmartCCUtil;
import com.devapp.smartcc.report.ReportHintConstant;
import com.devApp.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

public class EncryptedReportActivity extends AppCompatActivity implements
        View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private Button btnCancel, btnExport;
    private Spinner collectionSpinner;
    private int FROMDATE = 0, TODATE = 1, SETDATE = 0;
    private String[] collectionTypeArray = null;
    long startMilliDate, endMilliDate;
    private Date startDate, endDate, tempDate;
    private AutoCompleteTextView tvStartDate, tvEndDate, tvSelectShift;
    private SmartCCUtil smartCCUtil;
    private int thresHold = 0;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encrypted);
        DatePickerFragment.mActivity = 2;
        initializeView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setDrawable();
//        setOnKeyClickListerner();
        tvStartDate.setText(Util.getDateDDMMYY(smartCCUtil.getReportDate(0), 11));
        tvEndDate.setText(Util.getDateDDMMYY(smartCCUtil.getReportDate(0), 11));
        startDate = new Date(System.currentTimeMillis());
        endDate = new Date(System.currentTimeMillis());
    }

    private void setDrawable() {
        smartCCUtil.toSetDrawableOnEdit(tvEndDate, ReportHintConstant.DATE_TO);
        smartCCUtil.toSetDrawableOnEdit(tvStartDate, ReportHintConstant.DATE_FROM);
        smartCCUtil.toSetDrawableOnEdit(tvSelectShift, ReportHintConstant.SHIFT);
    }

    private void initializeView() {
        smartCCUtil = new SmartCCUtil(EncryptedReportActivity.this);
        btnCancel = (Button) findViewById(R.id.btCancel);
        btnExport = (Button) findViewById(R.id.btnExport);
        tvStartDate = (AutoCompleteTextView) findViewById(R.id.tvStartDate);
        tvEndDate = (AutoCompleteTextView) findViewById(R.id.tvEndDate);
        tvSelectShift = (AutoCompleteTextView) findViewById(R.id.tvSelectShift);
        collectionSpinner = (Spinner) findViewById(R.id.spCollectionType);
        collectionTypeArray = getResources().getStringArray(R.array.collection_type);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        btnCancel.setOnClickListener(this);
        btnExport.setOnClickListener(this);
        tvStartDate.setOnClickListener(this);
        tvEndDate.setOnClickListener(this);

        tvStartDate.setOnKeyListener(new FocusForwardKeyListener(tvEndDate));
        tvEndDate.setOnKeyListener(new FocusForwardKeyListener(btnExport));
        tvStartDate.setOnFocusChangeListener(new CursorEndOnFocusChangeListener());
        tvEndDate.setOnFocusChangeListener(new CursorEndOnFocusChangeListener());

        String[] shifts = {AppConstants.Shift.MORNING,
                AppConstants.Shift.EVENING, SmartCCConstants.SELECT_ALL};
        int adapterLayoutId = getResources().getIdentifier("auto_text_item", "res", getPackageName());

        AutoTextAdapter autoTextAdapter = new AutoTextAdapter(EncryptedReportActivity.this,
                adapterLayoutId, Arrays.asList(shifts));
        tvSelectShift.setThreshold(thresHold);
        tvSelectShift.setAdapter(autoTextAdapter);
        tvSelectShift.setText(shifts[0]);


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnExport:
                if (isValidDate()) {
                    onExport();
                }

                break;
            case R.id.btCancel:
                onFinish();
                break;
            case R.id.tvStartDate:
                SETDATE = FROMDATE;
//                showFragment();
                break;
            case R.id.tvEndDate:
                SETDATE = TODATE;
//                showFragment();
                break;

            default:
        }

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        if (SETDATE == FROMDATE) {
            startMilliDate = smartCCUtil.getDateInMilli(dayOfMonth, month, year);
            startDate = new Date(startMilliDate);
            tvStartDate.setText(Util.padding(dayOfMonth) + "-" + Util.padding(month + 1) + "-" + year);

        } else if (SETDATE == TODATE) {
            endMilliDate = smartCCUtil.getDateInMilli(dayOfMonth, month, year);
            endDate = new Date(endMilliDate);
            tvEndDate.setText(Util.padding(dayOfMonth) + "-" + Util.padding(month + 1) + "-" + year);
        }

    }

    private Handler encryptHandler = new Handler();

    public void setOnKeyClickListerner() {
        tvStartDate.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_ENTER ||
                        keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER) {
                    if (keyEvent.getAction() == keyEvent.ACTION_UP) {
                        tvStartDate.setSelection(tvStartDate.getText().toString().trim().length());
                        SETDATE = FROMDATE;
                        showFragment();
                    }
                }

                return false;
            }
        });

        tvEndDate.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_ENTER ||
                        keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER) {
                    if (keyEvent.getAction() == keyEvent.ACTION_UP) {
                        tvEndDate.setSelection(tvEndDate.getText().toString().trim().length());
                        SETDATE = TODATE;
                        showFragment();
                    }
                }
                return false;
            }
        });

        collectionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void showFragment() {
        DatePickerFragment dpf = new DatePickerFragment();
        dpf.show(getFragmentManager(), "FilterDateFragment");
    }

    TreeSet<DateShiftEntry> dateShiftEntries = new TreeSet<>();

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_PAGE_DOWN: {
                btnExport.requestFocus();
                break;
            }
            case KeyEvent.KEYCODE_PAGE_UP: {
                tvStartDate.requestFocus();
                break;
            }
        }
        return super.onKeyUp(keyCode, event);
    }
    private Runnable encryptRunnable;

    private void onExport() {
        progressBar.setVisibility(View.VISIBLE);
        btnExport.setEnabled(false);
        tempDate = startDate;
        new Thread(new Runnable() {
            @Override
            public void run() {

                getDateAndShiftEntry(dateShiftEntries, getYYYYMMDDFormat(tempDate.getTime()));

                while (endDate.compareTo(tempDate) > 0) {
                    setNextDate(tempDate.getTime());
                    dateShiftEntries = getDateAndShiftEntry(dateShiftEntries, getYYYYMMDDFormat(tempDate.getTime()));
                }

                encryptHandler.post(encryptRunnable);
            }
        }).start();

        encryptRunnable = new Runnable() {
            @Override
            public void run() {

                if (Util.checkForPendrive()) {
                    ReportHelper reportHelper = new ReportHelper(EncryptedReportActivity.this);
                    reportHelper.createEncryptedRecords(dateShiftEntries, progressBar);
                } else {
                    progressBar.setVisibility(View.GONE);
                    btnExport.setEnabled(true);
                    Util.displayErrorToast("Please connect a pendrive to export records!",
                            EncryptedReportActivity.this);
                }
                encryptHandler.removeCallbacks(encryptRunnable);
            }
        };


    }

    private boolean isValidDate() {
        if (tvStartDate.getText().toString().trim().isEmpty() ||
                tvEndDate.getText().toString().trim().isEmpty()) {
            Util.displayErrorToast("Please enter valid date", EncryptedReportActivity.this);
            return false;
        } else {
        }
        if (!smartCCUtil.dateValidator(tvStartDate.getText().toString(), "dd-MM-yyyy")
                || !smartCCUtil.dateValidator(tvEndDate.getText().toString(), "dd-MM-yyyy")) {
            Util.displayErrorToast("Please enter valid date", EncryptedReportActivity.this);
            return false;
        }
        {

        }

        startMilliDate = smartCCUtil.getDateInMilliSecondsFromFormat(tvStartDate.getText().toString(), "dd-MM-yyyy");
        endMilliDate = smartCCUtil.getDateInMilliSecondsFromFormat(tvEndDate.getText().toString(), "dd-MM-yyyy");
        endDate = new Date(endMilliDate);
        startDate = new Date(startMilliDate);

        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        Calendar startCal = Calendar.getInstance();
        startCal.setTime(startDate);
        Calendar endCal = Calendar.getInstance();
        endCal.setTime(endDate);

        if (startMilliDate > endMilliDate || endMilliDate > System.currentTimeMillis()) {
            Util.displayErrorToast("Please enter valid date", EncryptedReportActivity.this);
            return false;
        }
        if ((daysBetween(startCal, endCal) > 29)) {
            Util.displayErrorToast("Please select a date range not more than 30 days", EncryptedReportActivity.this);
            return false;
        }
        return true;

    }

    private long daysBetween(Calendar from, Calendar to) {
        long start = from.getTimeInMillis();
        long end = to.getTimeInMillis();
        return TimeUnit.MILLISECONDS.toDays(Math.abs(start - end));
    }


    private String setNextDate(long miliTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(miliTime);
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        tempDate = new Date(calendar.getTimeInMillis());
        return getYYYYMMDDFormat(calendar.getTimeInMillis());
    }

    private String getYYYYMMDDFormat(long milliTime) {
        DateFormat simple = new SimpleDateFormat("yyyy-MM-dd");
        return simple.format(new Date(milliTime));
    }


    public TreeSet<DateShiftEntry> getDateAndShiftEntry(TreeSet<DateShiftEntry> dateShiftEntries,
                                                        String date) {

        DateShiftEntry dateShiftEntry = new DateShiftEntry();
        dateShiftEntry.setDate(date);
        dateShiftEntry.setShift(AppConstants.Shift.MORNING);
        dateShiftEntries.add(dateShiftEntry);
        dateShiftEntry = new DateShiftEntry();
        dateShiftEntry.setDate(date);
        dateShiftEntry.setShift(AppConstants.Shift.EVENING);
        dateShiftEntries.add(dateShiftEntry);

        return dateShiftEntries;
    }


    @Override
    public void onBackPressed() {
        onFinish();
    }

    public void onFinish() {

        startActivity(new Intent(EncryptedReportActivity.this, FarmerScannerActivity.class));
        overridePendingTransition(R.anim.slide_in_right,
                R.anim.slide_out_right);
        finish();
    }

}
