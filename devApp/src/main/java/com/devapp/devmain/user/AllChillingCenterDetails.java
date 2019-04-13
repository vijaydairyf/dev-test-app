package com.devapp.devmain.user;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.devapp.devmain.dao.ChillingCenterDao;
import com.devapp.devmain.dao.DaoFactory;
import com.devapp.devmain.dao.FarmerDao;
import com.devapp.devmain.entity.CenterEntity;
import com.devapp.devmain.entity.ExcelConstants;
import com.devapp.devmain.main.FarmerScannerActivity;
import com.devapp.devmain.postentities.CollectionConstants;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.server.SessionManager;
import com.devapp.devmain.services.SendEmail;
import com.devapp.devmain.services.WriteRecordReceiver;
import com.devapp.devmain.usb.ReadExcelFarmer;
import com.devapp.devmain.usb.WriteExcel;
import com.devApp.BuildConfig;
import com.devApp.R;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;

import jxl.write.WriteException;

public class AllChillingCenterDetails extends Activity implements OnClickListener {

    private static final String FTYPE = ".xls";
    private static final int DIALOG_LOAD_FILE = 1000;
    public String TAG = "All farmers";
    public String duplicateMsg;
    public WriteRecordReceiver receiverForWrite;
    ListView lvCenterDetails;
    FarmerAdapter farmerAdapter;
    DatabaseHandler databaseHelper;
    String[] listMilkType = new String[10];
    Button btnNewUser, btnImportFarmer, btnExport, btnDelete;
    CenterEntity selecteCenterEntity;
    AlertDialog alertDialog;
    String fileName;
    SessionManager session;
    Runnable updateRunnable;
    Handler myHandler = new Handler();
    ArrayList<CenterEntity> allCenterList = new ArrayList<CenterEntity>();
    RelativeLayout progressLayoutScan;
    TextView tvHeader;
    File gpxfile;
    boolean isWrittenToFile = false;
    private String[] mFileList;
    private File mPath;
    private String mChosenFile;
    private Context mContext;
    private ChillingCenterDao chillingCenterDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.allfarmerdetails);

        mContext = AllChillingCenterDetails.this;
        chillingCenterDao = (ChillingCenterDao) DaoFactory.getDao(CollectionConstants.CHILLING_CENTER);

        // ///////////////////////////////////
        progressLayoutScan = (RelativeLayout) findViewById(R.id.progress_layout);
        progressLayoutScan.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        progressLayoutScan.setVisibility(View.GONE);
        // ///////////////////////////////////

        session = new SessionManager(mContext);

        lvCenterDetails = (ListView) findViewById(R.id.lvFarmerdetails);
        databaseHelper = DatabaseHandler.getDatabaseInstance();
        btnDelete = (Button) findViewById(R.id.btnDelete);

        btnNewUser = (Button) findViewById(R.id.btnNewUser);
        btnImportFarmer = (Button) findViewById(R.id.btnImportFarmer);
        btnExport = (Button) findViewById(R.id.btnList);

        tvHeader = (TextView) findViewById(R.id.tvheader);

        tvHeader.setText("Associated Centers");

        btnExport.setVisibility(View.GONE);
        btnExport.setText("Export");

        btnImportFarmer.setVisibility(View.VISIBLE);
        btnNewUser.setText("New center");

        btnImportFarmer.requestFocus();

        lvCenterDetails.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {

                if (allCenterList.size() > 0) {
                    selecteCenterEntity = allCenterList.get(arg2);
                    Intent intent = new Intent(mContext,
                            AddChillingCenterActivity.class);
                    intent.putExtra("SelectedList", selecteCenterEntity);
                    intent.putExtra("NewFarmer", "Farmer");

                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_left,
                            R.anim.slide_out_left);
                    // showAlert(allFarmList.get(arg2));
                }


            }
        });

        lvCenterDetails
                .setOnItemLongClickListener(new OnItemLongClickListener() {

                    @Override
                    public boolean onItemLongClick(AdapterView<?> arg0,
                                                   View arg1, int arg2, long arg3) {
                        if (!Util.isOperator(AllChillingCenterDetails.this)) {
                            final int pos = arg2;

                            PopupMenu popup = new PopupMenu(mContext,
                                    arg1);
                            popup.getMenuInflater().inflate(R.menu.popup_menu,
                                    popup.getMenu());

                            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                                @Override
                                public boolean onMenuItemClick(MenuItem arg0) {


                                    long deleteCk = 0;
                                    try {
                                        deleteCk = chillingCenterDao.deleteByKey(
                                                DatabaseHandler.KEY_CHILLING_CENTER_ID,
                                                allCenterList.get(pos).centerId);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    if (deleteCk > 0) {
                                        Toast.makeText(
                                                mContext,
                                                "Center details successfully deleted!",
                                                Toast.LENGTH_SHORT).show();
                                        DisplayList();
                                    } else {
                                        Toast.makeText(mContext,
                                                "Not deleted", Toast.LENGTH_SHORT)
                                                .show();
                                    }

                                    return true;
                                }
                            });

                            popup.show();

                        }

                        return true;
                    }
                });

        listMilkType = getApplicationContext().getResources().getStringArray(
                R.array.Milk_type);

        if (Util.isOperator(AllChillingCenterDetails.this)) {
            btnNewUser.setVisibility(View.GONE);
        }

        btnNewUser.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                selecteCenterEntity = null;
                AddFarmerData();
            }
        });

        btnImportFarmer.setVisibility(View.GONE);

        btnImportFarmer.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                fileName = "";

                if (Util.checkForPendrive()) {
                    mPath = new File(Util.rootFileName);
                    loadFileList();
                } else {
                    Toast.makeText(mContext, "Please attach the pendrive and try again!", Toast.LENGTH_LONG).show();
                }


            }
        });

        btnExport.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                checkForPenDrive();

            }
        });

        btnDelete.setVisibility(View.GONE);

        btnDelete.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                deleteAllFarmersAlert();
            }
        });

    }

    @Override
    protected void onStart() {

        if (!Util.isOperator(mContext) || BuildConfig.FLAVOR.equalsIgnoreCase("hatsun")) {
            btnDelete.setVisibility(View.GONE);
        } else {
            btnDelete.setVisibility(View.GONE);
        }
        DisplayList();
        super.onStart();
    }

    @Override
    public void onClick(View v) {

    }

    public void getAllChillingData() {

        databaseHelper = DatabaseHandler.getDatabaseInstance();

        try {
            allCenterList = databaseHelper.getAllCenterEntity();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Removed database close statement

        if (allCenterList != null && allCenterList.size() > 0) {
            if (!Util.isOperator(mContext) || BuildConfig.FLAVOR.equalsIgnoreCase("hatsun")) {
                btnDelete.setVisibility(View.GONE);
            }

        } else {
            btnDelete.setVisibility(View.GONE);
        }
    }

    public void DisplayList() {
        getAllChillingData();

        databaseHelper = DatabaseHandler.getDatabaseInstance();
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                farmerAdapter = new FarmerAdapter(mContext,
                        databaseHelper.getTotalcenterFromChilling(String
                                .valueOf(session.getSocietyColumnId())), 4);
                lvCenterDetails.setAdapter(farmerAdapter);
            }
        });
        //Removed database close statement
    }

    public void AddFarmerData() {
        Intent intent = new Intent(mContext, AddChillingCenterActivity.class);
        intent.putExtra("SelectedList", selecteCenterEntity);
        intent.putExtra("NewFarmer", "NewFarmer");
        startActivity(intent);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        FarmerScannerActivity.backFromFarmer = true;
        finish();
    }

    // Export farmer

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

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        if (id == DIALOG_LOAD_FILE) {

            AlertDialog.Builder builder = new Builder(this);
            builder.setTitle("Choose Farmer List");
            if (mFileList == null) {
                Log.e(TAG, "Showing file picker before loading the file list");
                dialog = builder.create();
            }
            builder.setItems(mFileList, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    mChosenFile = mFileList[which];

                    if (mChosenFile != null && mChosenFile.length() > 1) {
                        if (mChosenFile.contains(".xls")) {
                            fileName = fileName + "/" + mChosenFile;

                            Toast.makeText(mContext,
                                    "Fetching farmers from " + mChosenFile,
                                    Toast.LENGTH_SHORT).show();
                            ReadExcelFile(fileName);
                            dialog.dismiss();

                        } else {
                            mPath = new File(Util.rootFileName + "/"
                                    + mChosenFile);
                            fileName = fileName + "/" + mChosenFile;
                            mFileList = new String[100];
                            loadFileList();
                            dialog.dismiss();
                        }

                    } else {
                        mFileList = new String[100];
                    }

                }
            });

            dialog = builder.show();
            return dialog;

        } else {
            return dialog;
        }

    }

    public void ReadExcelFile(String file) {

        progressLayoutScan.setVisibility(View.VISIBLE);

        final ReadExcelFarmer test = new ReadExcelFarmer();
        File root = new File(Util.rootFileName);
        final File gpxfile = new File(root, file);

        test.setInputFile(gpxfile.toString(), 1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    duplicateMsg = test.read(mContext);
                    myHandler.post(updateRunnable);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        updateRunnable = new Runnable() {

            @Override
            public void run() {
                progressLayoutScan.setVisibility(View.GONE);

                if (ReadExcelFarmer.allFarmEnt == null && duplicateMsg != null) {
                    alertForDuplicateEntries(duplicateMsg, false);
                } else if (duplicateMsg != null && !duplicateMsg.equalsIgnoreCase("\n"
                        + "Already existing farmers" + "\n")) {
                    alertForDuplicateEntries(duplicateMsg, true);
                } else if (ReadExcelFarmer.allFarmEnt != null && ReadExcelFarmer.allFarmEnt.size() > 0) {
                    Util.addFarmersTodatBase(ReadExcelFarmer.allFarmEnt,
                            mContext);
                    Toast.makeText(mContext, "Farmers successfully added!", Toast.LENGTH_LONG).show();
                    DisplayList();
                } else {
                    Toast.makeText(mContext, "Invalid format! please try again.", Toast.LENGTH_LONG).show();
                }

            }
        };
    }

    public void exportFarmerList() {
        setupServiceReceiver();
        final WriteExcel writeExcel = new WriteExcel();
        File root = new File(Util.rootFileName);
        String date = Util.getTodayDateAndTime(1, mContext, false);
        date = date.replace("-", "");

        final File fileSmartAmcu = new File(Util.rootFileName, "smartAmcuReports");

        if (!fileSmartAmcu.exists()) {
            fileSmartAmcu.mkdirs();
        }
        gpxfile = new File(fileSmartAmcu, new SessionManager(mContext)
                .getSocietyName().replace(" ", "") + "_" + date + "_farmerList.xls");

        writeExcel.setOutputFile(gpxfile.toString());

        writeExcel.setWriteCompleteListener(new WriteExcel.OnExcelWriteCompleteListener() {
            @Override
            public void onWriteComplete() {

            }
        });

        try {

            if (allCenterList.size() > 0) {
                writeExcel.write(mContext, ExcelConstants.TYPE_FARMER, allCenterList);
                new SessionManager(mContext).setFileName(gpxfile
                        .toString());
                new SessionManager(mContext).setReportType(Util.sendFarmerList);
                isWrittenToFile = true;
                startService();
            } else {
                Toast.makeText(mContext, "No farmer list to export!",
                        Toast.LENGTH_LONG).show();
            }


        } catch (WriteException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (isWrittenToFile) {
            receiverForWrite.send(Activity.RESULT_OK, null);
        } else {
            receiverForWrite.send(Activity.RESULT_CANCELED, null);
        }
    }

    public void startService() {
        Intent intentService = new Intent(mContext, SendEmail.class);
        startService(intentService);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {

            case KeyEvent.KEYCODE_F9:
                Util.Logout(mContext);
                finish();
                return true;

            case KeyEvent.KEYCODE_F10:
                Util.restartApp(mContext);
                return true;

            case KeyEvent.KEYCODE_F11:

                Util.restartTab(mContext);
                return true;

            case KeyEvent.KEYCODE_F12:
                Util.shutDownTab(mContext, null);
                return true;

            default:
                return super.onKeyUp(keyCode, event);
        }
    }

    public void deleteAllFarmersAlert() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                mContext);

        // set title
        alertDialogBuilder.setTitle("Delete All farmers!");

        // set dialog message
        alertDialogBuilder
                .setMessage("This will delete all existing farmer entries.")
                .setCancelable(false)
                .setPositiveButton("Delete",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                deleteAllFarmers();

                                DisplayList();
                                btnImportFarmer.requestFocus();
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

    public void deleteAllFarmers() {
        FarmerDao farmerDao = (FarmerDao) DaoFactory.getDao(CollectionConstants.FARMER);
        try {
            farmerDao.deleteByCenterId(String.valueOf(session.getSocietyColumnId()));
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void alertForDuplicateEntries(String msg, boolean isDuplicate) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                mContext);
        // set title
        if (isDuplicate) {
            alertDialogBuilder.setTitle("Duplicate farmers!");
        } else {
            alertDialogBuilder.setTitle("Invalid format!");
        }
        // set dialog message
        alertDialogBuilder
                .setMessage(msg)
                .setCancelable(false);

        if (isDuplicate) {
            alertDialogBuilder.setPositiveButton("Insert",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Util.addFarmersTodatBase(
                                    ReadExcelFarmer.allFarmEnt,
                                    mContext);
                            Toast.makeText(mContext, "Farmers successfully added!", Toast.LENGTH_LONG).show();
                            DisplayList();
                            dialog.cancel();

                        }
                    });
        }

        alertDialogBuilder.setNegativeButton("Cancel",
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

    public void checkForPenDrive() {
        if (Util.checkForPendrive()) {
            Toast.makeText(mContext, "Copying the file please wait..", Toast.LENGTH_SHORT).show();
            exportFarmerList();
        } else {
            usbAlert();
        }
    }

    public void usbAlert() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                mContext);
        // set title
        alertDialogBuilder.setTitle("Alert!");
        // set dialog message
        alertDialogBuilder
                .setMessage(getResources().getString(R.string.pendrive_alert))
                .setCancelable(false)
                .setPositiveButton("Continue",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Toast.makeText(mContext, "Copying the file please wait..", Toast.LENGTH_SHORT).show();
                                exportFarmerList();
                                alertDialog.dismiss();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();

    }

    // Setup the callback for when data is received from the service
    public void setupServiceReceiver() {
        receiverForWrite = new WriteRecordReceiver(new Handler());
        // This is where we specify what happens when data is received from the
        // service
        receiverForWrite.setReceiver(new WriteRecordReceiver.Receiver() {
            @Override
            public void onReceiveResult(int resultCode, Bundle resultData) {
                if (resultCode == RESULT_OK) {

                    if (Util.rootFileName.contains("usbhost")) {
                        Toast.makeText(mContext, "File has been successfully written in Pendrive.",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(mContext, "File has been successfully written in Internal storage.",
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(mContext, "Please try again.",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }


}
