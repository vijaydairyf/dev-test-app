package com.devapp.devmain.user;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.devapp.kmfcommon.ShowMyRateChart;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.server.SessionManager;
import com.devApp.R;


public class CustomRateChartActivity extends Activity {

    ListView listView;
    DatabaseHandler databaseHelper, dbh, dbhDel;
    MyRateChartAdapter myRateChartAdapter;
    SessionManager sessionManager;
    Cursor cursor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_rate_chart);

        sessionManager = new SessionManager(this);
        listView = (ListView) findViewById(R.id.listMYRate);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                cursor.moveToPosition(position);
                Intent intent = new Intent(CustomRateChartActivity.this, ShowMyRateChart.class);
                intent.putExtra("RN", cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_MY_RATECHART_NAME)));
                startActivity(intent);
                // Toast.makeText(CustomRateChartActivity.this, "RateChartName " + cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_MY_RATECHART_NAME)), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        databaseHelper = DatabaseHandler.getDatabaseInstance();
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                myRateChartAdapter = new MyRateChartAdapter(CustomRateChartActivity.this, databaseHelper.getAllMyRateChartList(), 0);
                cursor = databaseHelper.getAllMyRateChartList();
                listView.setAdapter(myRateChartAdapter);
            }
        });
        //Removed database close statement
    }

    public void gotoAddRateChart(View view) {
        // Toast.makeText(CustomRateChartActivity.this, "CLick", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, AddMyRateChart.class);
        startActivity(intent);
    }
   /* @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_DOWN:

                return true;
            case KeyEvent.KEYCODE_DPAD_UP:

                return true;

            default:
                return super.onKeyUp(keyCode, event);
        }
    }*/

}
