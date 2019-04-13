package com.devapp.kmfcommon;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.devapp.devmain.server.DatabaseHandler;
import com.devApp.R;

import java.util.ArrayList;

/**
 * Created by yyy on 23/2/16.
 */
public class AllTruckEventActivity extends Activity {

    RecyclerView otherRecyclerView;
    ArrayList<ListAllDataItem> dataItems;
    ListAllDashboardAdapter listAllDashboardAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_truck_summary_report);
        TextView textView = (TextView) findViewById(R.id.coolection_summary1);
        textView.setText("All Truck Event Data");
        otherRecyclerView = (RecyclerView) findViewById(R.id.data);
        setOtherData();
    }

    public void setOtherData() {
        try {
            dataItems = new ArrayList<>();
            DatabaseHandler dbHandler = DatabaseHandler.getDatabaseInstance();
            Cursor cursor = dbHandler.getTruckReportDataFromSalesRecordTable();
            if (cursor.moveToFirst()) {
                do {
                    ListAllDataItem item = new ListAllDataItem();
                    item.setTruckNumber(cursor.getString(cursor.getColumnIndex("salesId")));
                    item.setQuantity(cursor.getString(cursor.getColumnIndex("quantity")));
                    item.setIssueDate(cursor.getString(cursor.getColumnIndex("date")));
                    item.setShift(cursor.getString(cursor.getColumnIndex("shift")));
                    item.setAmount(cursor.getString(cursor.getColumnIndex("amount")));
                    item.setPurity(cursor.getString(cursor.getColumnIndex("salesTxnSubType")));
                    item.setMilkType(cursor.getString(cursor.getColumnIndex("milkType")));
                    item.setAvgFat(cursor.getString(cursor.getColumnIndex("fat")));
                    item.setAvgSnf(cursor.getString(cursor.getColumnIndex("snf")));
                    item.setRate(cursor.getString(cursor.getColumnIndex("rate")));

                    dataItems.add(item);
                } while (cursor.moveToNext());
            }

            listAllDashboardAdapter = new ListAllDashboardAdapter(AllTruckEventActivity.this, dataItems);
            otherRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            otherRecyclerView.setAdapter(listAllDashboardAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
