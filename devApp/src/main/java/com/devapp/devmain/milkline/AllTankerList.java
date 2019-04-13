package com.devapp.devmain.milkline;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.devapp.devmain.milkline.entities.TankerAdapter;
import com.devapp.devmain.milkline.entities.TankerCollectionEntity;
import com.devapp.devmain.milkline.service.PostTankerRecords;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.user.Util;
import com.devApp.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by u_pendra on 19/12/16.
 */

public class AllTankerList extends AppCompatActivity implements
        SearchView.OnQueryTextListener, View.OnClickListener {

    public static String INTENT_CLASS = "com.stellapps.smartamcu.milkline.service.PostTankerRecords";
    RecyclerView recycleView;
    ArrayList<TankerCollectionEntity> allTankerCollectionEntity;
    TankerAdapter tankerAdapter;
    DatabaseHandler dbHandler;
    TankerDatabase tankerCCDatabase;
    TextView tvNoDataFound, tvheader;
    Button btnSend, btnCancel, btnDate;
    LocalBroadcastManager localBroadCastManager;
    int year, month, day;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equalsIgnoreCase(INTENT_CLASS)) {
                if (intent.getStringExtra("RESULT").equalsIgnoreCase("SUCCESS")) {
                    Util.displayErrorToast("Sent tanker records successfully!", AllTankerList.this);
                    addTankerInView();
                }
            }
        }
    };
    private DatePickerDialog.OnDateSetListener datePickerListener =
            new DatePickerDialog.OnDateSetListener() {

                public void onDateSet(DatePicker view, int selectedYear,
                                      int selectedMonth, int selectedDay) {

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
                    year = selectedYear;
                    month = selectedMonth + 1;
                    day = selectedDay;

                    tvheader.setText("Tanker records on " + day + "-" + month + "-" + year);

                    calendar.set(Calendar.DATE, day);
                    calendar.set(Calendar.MONTH, selectedMonth);
                    calendar.set(Calendar.YEAR, selectedYear);
                    calendar.set(Calendar.HOUR_OF_DAY, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MILLISECOND, 0);

                    long startTime = calendar.getTimeInMillis();

                    calendar.set(Calendar.DATE, day);
                    calendar.set(Calendar.MONTH, selectedMonth);
                    calendar.set(Calendar.YEAR, selectedYear);
                    calendar.set(Calendar.HOUR_OF_DAY, 23);
                    calendar.set(Calendar.MINUTE, 59);
                    calendar.set(Calendar.SECOND, 59);
                    calendar.set(Calendar.MILLISECOND, 0);

                    long endTime = calendar.getTimeInMillis();

                    addTankerInView(startTime, endTime);

                    initDatePicker();

                }
            };

    public static ArrayList<TankerCollectionEntity> filterTruckDetails(ArrayList<TankerCollectionEntity> allTruckDetails,
                                                                       String query) {
        String searchQuery = query.toLowerCase();
        ArrayList<TankerCollectionEntity> filterDetailsList = new ArrayList<>();
        for (TankerCollectionEntity tankerCollectionEntity : allTruckDetails) {
            if (tankerCollectionEntity.tankerNumber.toLowerCase().contains(searchQuery)) {
                filterDetailsList.add(tankerCollectionEntity);
            }
        }
        return filterDetailsList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_tanker_list);
        recycleView = (RecyclerView) findViewById(R.id.recylerList);
        tvNoDataFound = (TextView) findViewById(R.id.tvNoDataFound);
        tvheader = (TextView) findViewById(R.id.tvheader);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnSend = (Button) findViewById(R.id.btnSend);
        btnDate = (Button) findViewById(R.id.btnDate);
        btnDate.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnSend.setOnClickListener(this);
        localBroadCastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(INTENT_CLASS);
        localBroadCastManager.registerReceiver(broadcastReceiver, intentFilter);
        initDatePicker();

    }

    @Override
    protected void onStart() {
        super.onStart();

        dbHandler = DatabaseHandler.getDatabaseInstance();
        //  truckCcDatabase= dbHandler.getTruckCCDatabase();

        tankerCCDatabase = DatabaseHandler.getDatabaseInstance(
        ).getTankerDatabase();
        addTankerInView();

        int count = tankerCCDatabase.getUnsentCount();
        if (count == 0) {
            btnSend.setVisibility(View.GONE);
        }

    }

    public void addTankerInView() {
        allTankerCollectionEntity = tankerCCDatabase.getAllTankerEntities();
        tankerAdapter = new TankerAdapter(allTankerCollectionEntity, this, recycleView);
        recycleView.setAdapter(tankerAdapter);
        recycleView.setLayoutManager(new LinearLayoutManager(AllTankerList.this,
                OrientationHelper.VERTICAL, false));
        if (allTankerCollectionEntity.size() > 0) {
            setVisiblityNoDataFound(false);
        } else {
            setVisiblityNoDataFound(true);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_recycle_view, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.requestFocus();
        searchView.setOnQueryTextListener(AllTankerList.this);


        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {

        ArrayList<TankerCollectionEntity> filteredArrayList;
        filteredArrayList = filterTruckDetails(allTankerCollectionEntity, query);
        if (filteredArrayList.size() > 0) {
            setVisiblityNoDataFound(false);
        } else {
            setVisiblityNoDataFound(true);
        }
        tankerAdapter = new TankerAdapter(filteredArrayList, AllTankerList.this, recycleView);
        recycleView.setAdapter(tankerAdapter);
        recycleView.setLayoutManager(new LinearLayoutManager(AllTankerList.this, OrientationHelper.VERTICAL, false));
        return false;
    }

    void setVisiblityNoDataFound(boolean enable) {
        if (enable)
            tvNoDataFound.setVisibility(View.VISIBLE);
        else
            tvNoDataFound.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view) {

        if (view == btnCancel) {
            finish();
        } else if (view == btnSend) {
            //Send all the uncommitted tanker records
            Util.displayErrorToast("Sending records...", AllTankerList.this);
            startService(new Intent(AllTankerList.this, PostTankerRecords.class));
        } else if (view == btnDate) {
            showDialog(0);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            unregisterReceiver(broadcastReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        addTankerInView();
    }

    public void initDatePicker() {
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
    }

    @Override
    protected Dialog onCreateDialog(int id) {

        return new DatePickerDialog(this, datePickerListener, year, month, day);

    }

    public void addTankerInView(long startDate, long endDate) {
        allTankerCollectionEntity = tankerCCDatabase.getAllTankerEntitiesBetweenDates(startDate, endDate);
        tankerAdapter = new TankerAdapter(allTankerCollectionEntity, this, recycleView);
        recycleView.setAdapter(tankerAdapter);
        recycleView.setLayoutManager(new LinearLayoutManager(AllTankerList.this, OrientationHelper.VERTICAL, false));
        if (allTankerCollectionEntity.size() > 0) {
            setVisiblityNoDataFound(false);
        } else {
            setVisiblityNoDataFound(true);
        }

    }


}