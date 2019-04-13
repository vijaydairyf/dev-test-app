package com.devapp.devmain.user;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.devapp.devmain.main.CustomRateChartUtil;
import com.devapp.devmain.server.DatabaseHandler;
import com.devApp.R;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddMyRateChart extends AppCompatActivity {
    String ratechartname, cattletype, kgfatrate, kgsnfrate, startfat, endfat, startsnf, endsnf, startshift, endshift;
    Button btncow, btnselcow, btnmix, btnselmix, btnbuf, btnselbuf, btnSubmit;
    EditText startDate, endDate, eshift, sshift, etRatechartName, etkgfatrate, etkgsnfrate, etstartfat, etendfat, etstartsnf, etendsnf;
    Date sdate = null, edate = null;
    boolean shiftCheckOk = false;
    long longstartdate, longenddate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_my_rate_chart);
        DecimalFormat decimalFormat = new DecimalFormat("#0.0");
        etRatechartName = (EditText) findViewById(R.id.name);
        etkgfatrate = (EditText) findViewById(R.id.kgfatrate);
        etkgsnfrate = (EditText) findViewById(R.id.kgsnfrate);
        etstartfat = (EditText) findViewById(R.id.sartfatrange);
        etendfat = (EditText) findViewById(R.id.endfatrange);
        etstartsnf = (EditText) findViewById(R.id.sartsnfrange);
        etendsnf = (EditText) findViewById(R.id.endsnfrange);
        eshift = (EditText) findViewById(R.id.eshift);
        sshift = (EditText) findViewById(R.id.sshift);

        btncow = (Button) findViewById(R.id.btncow);
        btnbuf = (Button) findViewById(R.id.btnbuf);
        btnmix = (Button) findViewById(R.id.btnmix);
        btnselcow = (Button) findViewById(R.id.btnselcow);
        btnselmix = (Button) findViewById(R.id.btnselmix);
        btnselbuf = (Button) findViewById(R.id.btnselbuf);
        startDate = (EditText) findViewById(R.id.startDate);
        endDate = (EditText) findViewById(R.id.endDate);
        cattletype = "COW";

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public void gotoSubmit(View view) {

        boolean validratechartname = false, validkgfatrate = false, validkgsnfrate = false, validstartfat = false, validendfat = false, validstartsnf = false, validendsnf = false, validshift = false, validdate = false, validequalfat = false, validequalsnf = false;
        ratechartname = etRatechartName.getText().toString().trim();
        kgfatrate = etkgfatrate.getText().toString().trim();
        kgsnfrate = etkgsnfrate.getText().toString().trim();
        startfat = etstartfat.getText().toString().trim();
        endfat = etendfat.getText().toString().trim();
        startsnf = etstartsnf.getText().toString().trim();
        endsnf = etendsnf.getText().toString().trim();
        startshift = sshift.getText().toString().trim();
        endshift = eshift.getText().toString().trim();

        if (ratechartname.length() >= 1) {
            validratechartname = true;
        } else {
            Toast.makeText(AddMyRateChart.this, "Please Enter unique RateChartName", Toast.LENGTH_SHORT).show();
            validratechartname = false;
        }
        if (kgfatrate.length() >= 1) {
            validkgfatrate = true;
        } else {
            Toast.makeText(AddMyRateChart.this, "Please Enter valid KG Fat Rate", Toast.LENGTH_SHORT).show();
            validkgfatrate = false;
        }
        if (kgsnfrate.length() >= 1) {
            validkgsnfrate = true;
        } else {
            Toast.makeText(AddMyRateChart.this, "Please Enter valid KG Snf Rate", Toast.LENGTH_SHORT).show();
            validkgsnfrate = false;
        }
        if (startfat.length() >= 1 && Double.valueOf(startfat) <= Util.MAX_FAT_LIMIT) {
            validstartfat = true;
        } else {
            Toast.makeText(AddMyRateChart.this, "Please Enter valid starting FAT", Toast.LENGTH_SHORT).show();
            validstartfat = false;
        }
        if (endfat.length() >= 1 && Double.valueOf(endfat) <= Util.MAX_FAT_LIMIT) {
            validendfat = true;
        } else {
            Toast.makeText(AddMyRateChart.this, "Please Enter valid ending FAT", Toast.LENGTH_SHORT).show();
            validendfat = false;
        }
        if (startsnf.length() >= 1 && Double.valueOf(startsnf) <= Util.MAX_SNF_LIMIT) {
            validstartsnf = true;
        } else {
            Toast.makeText(AddMyRateChart.this, "Please Enter valid Starting SNF", Toast.LENGTH_SHORT).show();
            validstartsnf = false;
        }
        if (endsnf.length() >= 1 && Double.valueOf(endsnf) <= Util.MAX_SNF_LIMIT) {
            validendsnf = true;
        } else {
            Toast.makeText(AddMyRateChart.this, "Please Enter valid ending SNF", Toast.LENGTH_SHORT).show();
            validendsnf = false;
        }
        if ((endsnf.length() >= 1 && startsnf.length() >= 1) && (Double.valueOf(endsnf) > Double.valueOf(startsnf))) {
            validequalsnf = true;
        } else {
            validequalsnf = false;
            Toast.makeText(AddMyRateChart.this, "Please Enter valid SNF range", Toast.LENGTH_SHORT).show();
        }
        if ((endfat.length() >= 1 && startfat.length() >= 1) && (Double.valueOf(endfat) > Double.valueOf(startfat))) {
            validequalfat = true;
        } else {
            validequalfat = false;
            Toast.makeText(AddMyRateChart.this, "Please Enter valid FAT range", Toast.LENGTH_SHORT).show();
        }
        if (eshift.getText().toString().trim().length() == 1 && sshift.getText().toString().trim().length() == 1) {
            if (eshift.getText().toString().equalsIgnoreCase("m") || eshift.getText().toString().equalsIgnoreCase("e")) {
                if (sshift.getText().toString().equalsIgnoreCase("m") || sshift.getText().toString().equalsIgnoreCase("e")) {
                    shiftCheckOk = true;
                    validshift = true;
                    startshift = sshift.getText().toString().trim();
                    endshift = eshift.getText().toString().trim();
                } else {
                    Toast.makeText(getApplicationContext(), "Please Enter Valid Shift M or E", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Please Enter Valid Shift M or E", Toast.LENGTH_LONG).show();
            }
        } else {

            Toast.makeText(getApplicationContext(), "Please Enter Valid Shift M or E", Toast.LENGTH_LONG).show();

        }
        if (sdate != null && edate != null) {
            boolean dateCheckOk = sdate.before(edate);
            if (dateCheckOk) {
                validdate = true;
            } else {
                if (sdate.equals(edate)) {
                    validdate = true;
                } else {
                    // handle for equal date and startshift check
                    Toast.makeText(getApplicationContext(), "Please Select Valid Date", Toast.LENGTH_LONG).show();
                }

            }
        } else {
            Toast.makeText(getApplicationContext(), "Please Select Valid Date", Toast.LENGTH_LONG).show();
        }
        if (validratechartname && validkgfatrate && validkgsnfrate && validstartfat && validendfat && validstartsnf && validendsnf && validshift && validdate && validendfat && validendsnf && validequalfat && validequalsnf) {

            if (startEndRangeCheck(startfat, endfat, startsnf, endsnf)) {
                // check if startdate, milktype, and shift are same , if equal the it will not insert
                if (checkForValidStartDate()) {
                    if (checkForSameDateSameShift()) {
                        DatabaseHandler dbh = DatabaseHandler.getDatabaseInstance();
                        Cursor cursor = dbh.getAllMyRateChartList();
                        if (cursor.getCount() <= Util.MAX_MYRATECHAT_LIMIT) {
                            DecimalFormat decimalFormatFS = new DecimalFormat("#0.0");
                            startfat = decimalFormatFS.format(Double.valueOf(startfat));
                            endfat = decimalFormatFS.format(Double.valueOf(endfat));
                            startsnf = decimalFormatFS.format(Double.valueOf(startsnf));
                            endsnf = decimalFormatFS.format(Double.valueOf(endsnf));
                            if (dbh.isRateChartNameAvailable(ratechartname.toUpperCase())) {
                                dbh.insertMyRateChart(ratechartname.toUpperCase(), cattletype.toUpperCase(), kgfatrate, startfat, endfat, kgsnfrate, startsnf, endsnf, startshift.toUpperCase(), longstartdate, endshift.toUpperCase(), longenddate);
                                //Toast.makeText(AddMyRateChart.this, "Inser Record", Toast.LENGTH_SHORT).show();
                                CustomRateChartUtil.getInstance(AddMyRateChart.this).writeMyRatechartLogs(ratechartname.toUpperCase(), longstartdate, longenddate, startshift, endshift, AddMyRateChart.this, "ADD", System.currentTimeMillis());
                                finish();
                            } else {
                                Toast.makeText(AddMyRateChart.this, "Rate Chart with same name exist", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(AddMyRateChart.this, "Maximum Limit 30 Reached,Please delete the unused ratechart", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(AddMyRateChart.this, "StartDate already exist with same milk type and shift record", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(AddMyRateChart.this, "Start and End range cant be same", Toast.LENGTH_SHORT).show();
            }
        }

    }


    public boolean startEndRangeCheck(String startfat, String endfat, String startsnf, String endsnf) {
        if (startfat.equalsIgnoreCase(endfat)) {
            return false;
        }
        if (startsnf.equalsIgnoreCase(endsnf)) {
            return false;
        }
        return true;
    }

    public boolean checkForSameDateSameShift() {

        String editStartDate = startDate.getText().toString().trim();
        String editEndDate = endDate.getText().toString().trim();
        String editStartShift = startshift;
        String editEndShift = endshift;

        if (editStartDate.equalsIgnoreCase(editEndDate)) {
            if (editStartShift.equalsIgnoreCase(editEndShift)) {
                return true;
            } else if (editStartShift.equalsIgnoreCase("M") && editEndShift.equalsIgnoreCase("E")) {
                return true;
            } else {
                Toast.makeText(AddMyRateChart.this, "Please Check the start and end shift!!", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        return true;
    }

    public boolean checkForValidStartDate() {

        String editStartDate = startDate.getText().toString().trim();
        String editMilkType = cattletype;
        String editShift = startshift;

        boolean valid = true;

        DatabaseHandler dbh = DatabaseHandler.getDatabaseInstance();
        Cursor cursor = dbh.getAllMyRateChartList();

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {

                    String cattle = cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_MY_RATECHART_MILK_TYPE));
                    String shift = cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_MY_RATECHART_START_SHIFT));
                    long sDte = cursor.getLong(cursor.getColumnIndex(DatabaseHandler.KEY_MY_RATECHART_START_DATE));
                    Date tmpdate = new Date(sDte);
                    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                    String tempStringDate = df.format(tmpdate);
                    if (tempStringDate.equalsIgnoreCase(editStartDate) && cattle.equalsIgnoreCase(editMilkType) && shift.equalsIgnoreCase(editShift)) {
                        valid = false;
                        break;
                    }

                } while (cursor.moveToNext());
            }
        }

        return valid;
    }

    public void gotoButtonChange(View view) {
        int id = view.getId();
        if (id == R.id.btncow) {
            btncow.setVisibility(View.GONE);
            btnbuf.setVisibility(View.VISIBLE);
            btnmix.setVisibility(View.VISIBLE);
            btnselcow.setVisibility(View.VISIBLE);
            btnselmix.setVisibility(View.GONE);
            btnselbuf.setVisibility(View.GONE);

            Toast.makeText(getBaseContext(), "COW", Toast.LENGTH_LONG).show();
            cattletype = "COW";
        }
        if (id == R.id.btnmix) {
            btncow.setVisibility(View.VISIBLE);
            btnbuf.setVisibility(View.VISIBLE);
            btnmix.setVisibility(View.GONE);
            btnselcow.setVisibility(View.GONE);
            btnselmix.setVisibility(View.VISIBLE);
            btnselbuf.setVisibility(View.GONE);
            Toast.makeText(getBaseContext(), "MIX", Toast.LENGTH_LONG).show();
            cattletype = "MIXED";
        }
        if (id == R.id.btnbuf) {
            btncow.setVisibility(View.VISIBLE);
            btnbuf.setVisibility(View.GONE);
            btnmix.setVisibility(View.VISIBLE);
            btnselcow.setVisibility(View.GONE);
            btnselmix.setVisibility(View.GONE);
            btnselbuf.setVisibility(View.VISIBLE);
            Toast.makeText(getBaseContext(), "BUF", Toast.LENGTH_LONG).show();
            cattletype = "BUFFALO";
        }
    }

    public void gotoSDate(View view) {
        @SuppressLint("ValidFragment")
        class DatePickerFragment extends DialogFragment
                implements DatePickerDialog.OnDateSetListener {

            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                // Use the current date as the default date in the picker
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                // Create a new instance of DatePickerDialog and return it
                return new DatePickerDialog(getActivity(), this, year, month, day);
            }

            public void onDateSet(DatePicker view, int year, int month, int day) {
                // Do something with the date chosen by the user
                month = month + 1;
                String dateString = day + " " + month + " " + year;
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d MM yyyy");


                try {
                    sdate = simpleDateFormat.parse(dateString);
                    longstartdate = sdate.getTime();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                //String st = simpleDateFormat.format(date);
                SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
                String dateText = DATE_FORMAT.format(sdate);


                startDate.setText(dateText);
            }
        }

        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "StartDate");

    }

    public void gotoEDate(View view) {
        @SuppressLint("ValidFragment")
        class DatePickerFragment extends DialogFragment
                implements DatePickerDialog.OnDateSetListener {

            DatePickerFragment() {
            }


            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                // Use the current date as the default date in the picker
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                // Create a new instance of DatePickerDialog and return it
                return new DatePickerDialog(getActivity(), this, year, month, day);
            }

            public void onDateSet(DatePicker view, int year, int month, int day) {
                // Do something with the date chosen by the user
                month = month + 1;
                String dateString = day + " " + month + " " + year;
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d MM yyyy");


                try {
                    edate = simpleDateFormat.parse(dateString);
                    longenddate = edate.getTime();
                    //   longenddate = longenddate + Util.MY_RATECHART_ENDTIME_DILIMITER;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                //String st = simpleDateFormat.format(date);
                SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
                String dateText = DATE_FORMAT.format(edate);
                endDate.setText(dateText);
            }
        }

        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "LastDate");

    }

}
