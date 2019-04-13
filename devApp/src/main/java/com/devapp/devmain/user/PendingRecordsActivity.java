package com.devapp.devmain.user;

/**
 * Created by Upendra on 7/22/2015.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.devapp.devmain.dao.DaoFactory;
import com.devapp.devmain.dao.FarmerDao;
import com.devapp.devmain.entity.FarmerEntity;
import com.devapp.devmain.main.FarmerScannerActivity;
import com.devapp.devmain.postentities.CollectionConstants;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.server.SessionManager;
import com.devapp.devmain.services.SendEmail;
import com.devApp.R;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

//FIXME This Acitivity is not in use
public class PendingRecordsActivity extends Activity implements View.OnClickListener {

    private static final String FTYPE = ".xls";
    private static final int DIALOG_LOAD_FILE = 1000;
    public String TAG = "All farmers";
    ListView lvFarmerDetails;
    FarmerAdapter farmerAdapter;
    DatabaseHandler databaseHelper;
    String[] listMilkType = new String[10];
    Button btnNewUser, btnImportFarmer, btnExport, btnDelete;
    FarmerEntity selectedFarmEntity;
    AlertDialog alertDialog;
    String cattleType = "Cow", fileName;
    SessionManager session;
    Runnable updateRunnable;
    Handler myHandler = new Handler();
    ArrayList<FarmerEntity> allFarmList = new ArrayList<FarmerEntity>();
    RelativeLayout progressLayoutScan;
    private String[] mFileList;
    private File mPath;
    private String mChosenFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.allfarmerdetails);

        // ///////////////////////////////////
        progressLayoutScan = (RelativeLayout) findViewById(R.id.progress_layout);
        progressLayoutScan.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        progressLayoutScan.setVisibility(View.GONE);
        // ///////////////////////////////////

        session = new SessionManager(PendingRecordsActivity.this);

        lvFarmerDetails = (ListView) findViewById(R.id.lvFarmerdetails);
        databaseHelper = DatabaseHandler.getDatabaseInstance();
        btnDelete = (Button) findViewById(R.id.btnDelete);

        btnNewUser = (Button) findViewById(R.id.btnNewUser);
        btnImportFarmer = (Button) findViewById(R.id.btnImportFarmer);
        btnExport = (Button) findViewById(R.id.btnList);

        btnExport.setVisibility(View.VISIBLE);
        btnExport.setText("Export");

        btnImportFarmer.setVisibility(View.VISIBLE);
        btnNewUser.setText("New farmer");

        btnImportFarmer.requestFocus();

        lvFarmerDetails.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {

                if (allFarmList.size() > 0) {
                    selectedFarmEntity = allFarmList.get(arg2);
                    Intent intent = new Intent(PendingRecordsActivity.this,
                            AddFarmerDetails.class);
                    intent.putExtra("SelectedList", selectedFarmEntity);
                    intent.putExtra("NewFarmer", "Farmer");
                    startActivity(intent);
                    // showAlert(allFarmList.get(arg2));
                }

            }
        });


        listMilkType = getApplicationContext().getResources().getStringArray(
                R.array.Milk_type);

        btnNewUser.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                selectedFarmEntity = null;
                AddFarmerData();
            }
        });


        btnDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
            }
        });

    }

    @Override
    protected void onStart() {
        DisplayList();
        super.onStart();
    }

    @Override
    public void onClick(View v) {

    }

    public void getAllFarmerData() {

        databaseHelper = DatabaseHandler.getDatabaseInstance();
        FarmerDao farmerDao = (FarmerDao) DaoFactory.getDao(CollectionConstants.FARMER);

        try {
            allFarmList = (ArrayList<FarmerEntity>) farmerDao.findAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Removed database close statement

        if (allFarmList != null && allFarmList.size() > 0) {
            btnDelete.setVisibility(View.VISIBLE);
        } else {
            btnDelete.setVisibility(View.GONE);
        }
    }

    public void DisplayList() {
        getAllFarmerData();

        databaseHelper = DatabaseHandler.getDatabaseInstance();
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                farmerAdapter = new FarmerAdapter(PendingRecordsActivity.this,
                        databaseHelper.getFarmerDataSociety(String
                                .valueOf(session.getSocietyColumnId())), 0);
                lvFarmerDetails.setAdapter(farmerAdapter);
            }
        });
        //Removed database close statement
    }


    public void AddFarmerData() {
        Intent intent = new Intent(PendingRecordsActivity.this, AddFarmerDetails.class);
        intent.putExtra("SelectedList", selectedFarmEntity);
        intent.putExtra("NewFarmer", "NewFarmer");
        startActivity(intent);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        FarmerScannerActivity.backFromFarmer = true;
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

                    return filename.contains(FTYPE) || sel.isDirectory();
                }
            };
            mFileList = mPath.list(filter);
        } else {
            mFileList = new String[0];
        }

        onCreateDialog(1000);
    }


    // Export farmer


    public void startService() {
        Intent intentService = new Intent(PendingRecordsActivity.this, SendEmail.class);
        startService(intentService);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {

            case KeyEvent.KEYCODE_F9:
                Util.Logout(PendingRecordsActivity.this);
                finish();
                return true;

            case KeyEvent.KEYCODE_F10:
                Util.restartApp(PendingRecordsActivity.this);
                return true;

            case KeyEvent.KEYCODE_F11:

                Util.restartTab(PendingRecordsActivity.this);
                return true;

            case KeyEvent.KEYCODE_F12:
                Util.shutDownTab(PendingRecordsActivity.this, null);
                return true;

            default:
                return super.onKeyUp(keyCode, event);
        }
    }


}
