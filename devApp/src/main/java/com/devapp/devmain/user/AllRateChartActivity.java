package com.devapp.devmain.user;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.devapp.devmain.dao.DaoFactory;
import com.devapp.devmain.dao.RateChartNameDao;
import com.devapp.devmain.dao.RateDao;
import com.devapp.devmain.entity.RateChartEntity;
import com.devapp.devmain.entity.RatechartDetailsEnt;
import com.devapp.devmain.postentities.CollectionConstants;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.server.SessionManager;
import com.devapp.devmain.tableEntities.RateTable;
import com.devApp.R;

import java.util.ArrayList;

public class AllRateChartActivity extends Activity {

    ArrayList<RateChartEntity> allDatabaseRatechart;

    DatabaseHandler databaseHelper;
    ListView lvRateChart;
    RateAdapter rateAdapter;
    RelativeLayout progressLayout;
    Button btnCreateRate, btnDelete;
    RateChartEntity rcEntity;
    AlertDialog alertDialog;
    SessionManager session;
    AmcuConfig amcuConfig;
    boolean isUpdate, isOnCreate;

    String TAG = "AllRateChartActivity";
    String rateChartname;
    long rateRefId = -1;

    RateDao rateDao;
    RateChartNameDao rateChartNameDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.alert_rate);
        isOnCreate = false;
        amcuConfig = AmcuConfig.getInstance();

        rateChartname = amcuConfig.getRateChartName();
        rateDao = (RateDao) DaoFactory.getDao(CollectionConstants.RATES);
        rateChartNameDao = (RateChartNameDao) DaoFactory.getDao(CollectionConstants.RATECHART_NAME);

        RatechartDetailsEnt ratechartDetailsEnt = rateChartNameDao.findByName(rateChartname);
        rateRefId = ratechartDetailsEnt.columnId;


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
        session = new SessionManager(AllRateChartActivity.this);
        btnDelete = (Button) findViewById(R.id.btnDelete);
        getAllDatabaseRatechart();
        databaseHelper = DatabaseHandler.getDatabaseInstance();
        lvRateChart = (ListView) findViewById(R.id.lvFSnf);
        btnCreateRate = (Button) findViewById(R.id.btnCreateRateChart);

        btnCreateRate.setText("Back");
        btnCreateRate.requestFocus();
        btnDelete.setVisibility(View.GONE);

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                RateChartNameDao rateChartNameDao = (RateChartNameDao) DaoFactory.getDao(CollectionConstants.RATECHART_NAME);
                long refId = rateChartNameDao.findRateRefIdFromName(amcuConfig.getRateChartName());
                rateAdapter = new RateAdapter(AllRateChartActivity.this,
                        databaseHelper.getAllData(refId), 0);
                lvRateChart.setAdapter(rateAdapter);
            }
        });

        if (amcuConfig.getCreateAndEdit()) {
            lvRateChart.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                                        int arg2, long arg3) {

                    if (arg2 <= allDatabaseRatechart.size()) {
                        isUpdate = true;
                        rcEntity = allDatabaseRatechart.get(arg2);
                        // setSelectedData(rcEntity);

                        Intent intentRate = new Intent(
                                AllRateChartActivity.this,
                                CreateRateChartActivity.class);
                        intentRate.putExtra("isUpdate", isUpdate);
                        intentRate.putExtra("rcEntity", rcEntity);
                        startActivity(intentRate);
                        overridePendingTransition(R.anim.slide_in_left,
                                R.anim.slide_out_left);

                    }

                }
            });

            lvRateChart
                    .setOnItemLongClickListener(new OnItemLongClickListener() {

                        @Override
                        public boolean onItemLongClick(AdapterView<?> arg0,
                                                       View arg1, int arg2, long arg3) {

                            return false;
                        }
                    });
        } else {
            lvRateChart.setFocusable(false);
        }

        btnDelete.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                try {
                    long refId = rateChartNameDao.findRateRefIdFromName(amcuConfig.getRateChartName());
                    rateDao.deleteByKey(RateTable.RATE_REF_ID, String.valueOf(refId));
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    //DB close removed;
                }
                DisplayList();

                Toast.makeText(AllRateChartActivity.this,
                        "Table data deleted successfully!", Toast.LENGTH_SHORT)
                        .show();
            }
        });

        btnCreateRate.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    public void getAllDatabaseRatechart() {
        progressLayout.setVisibility(View.VISIBLE);
        allDatabaseRatechart = new ArrayList<RateChartEntity>();
        try {
            allDatabaseRatechart = rateDao.findAllByName(rateRefId);

        } catch (Exception e) {
            e.printStackTrace();
        }
        progressLayout.setVisibility(View.GONE);

    }


    public void DisplayList() {
        if (isOnCreate) {

            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    RateChartNameDao rateChartNameDao = (RateChartNameDao) DaoFactory.getDao(CollectionConstants.RATECHART_NAME);

                    long refId = rateChartNameDao.findRateRefIdFromName(amcuConfig.getRateChartName());
                    rateAdapter = new RateAdapter(AllRateChartActivity.this,
                            databaseHelper.getAllData(refId), 0);
                    lvRateChart.setAdapter(rateAdapter);
                }
            });
            getAllDatabaseRatechart();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        DisplayList();

        isOnCreate = true;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {

            case KeyEvent.KEYCODE_F9:
                Util.Logout(AllRateChartActivity.this);
                finish();
                return true;

            case KeyEvent.KEYCODE_F10:
                Util.restartApp(AllRateChartActivity.this);
                return true;

            case KeyEvent.KEYCODE_F11:

                Util.restartTab(AllRateChartActivity.this);
                return true;

            case KeyEvent.KEYCODE_F12:
                Util.shutDownTab(AllRateChartActivity.this, null);
                return true;
            case KeyEvent.KEYCODE_DEL:
                finish();
                return true;

            default:
                return super.onKeyUp(keyCode, event);
        }
    }

}
