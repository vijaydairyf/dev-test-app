package com.devapp.devmain.user;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.widget.Toast;

import com.devapp.devmain.dao.DaoFactory;
import com.devapp.devmain.dao.FarmerDao;
import com.devapp.devmain.devicemanager.PrinterManager;
import com.devapp.devmain.entity.Entity;
import com.devapp.devmain.entity.ExcelConstants;
import com.devapp.devmain.entity.FarmerEntity;
import com.devapp.devmain.main.FarmerScannerActivity;
import com.devapp.devmain.postentities.CollectionConstants;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.server.SessionManager;
import com.devapp.devmain.services.SendEmail;
import com.devapp.devmain.services.WriteRecordReceiver;
import com.devapp.devmain.usb.ReadExcelFarmer;
import com.devapp.devmain.usb.WriteExcel;
import com.devapp.devmain.util.AppConstants;
import com.devapp.devmain.util.ThinLineDividerDecoration;
import com.devApp.BuildConfig;
import com.devApp.R;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jxl.write.WriteException;


public class AllFarmerData extends Activity implements OnClickListener {

    private static final String FTYPE = ".xls";
    private static final int DIALOG_LOAD_FILE = 1000;
    public String TAG = "All farmers";
    public String duplicateMsg;
    public WriteRecordReceiver receiverForWrite;
    ListView lvFarmerDetails;
    RecyclerView rvFarmerList;
    FarmerListAdapter farmerListAdapter;
    DatabaseHandler databaseHelper;
    String[] listMilkType = new String[10];
    Button btnNewUser, btnImportFarmer, btnExport, btnDelete;
    FarmerEntity selectedFarmEntity;
    AlertDialog alertDialog;
    String fileName;
    SessionManager session;
    Runnable updateRunnable;
    Handler myHandler = new Handler();
    ArrayList<FarmerEntity> allFarmList = new ArrayList<FarmerEntity>();
    RelativeLayout progressLayoutScan;
    File gpxfile;
    boolean isWrittenToFile = false;
    AmcuConfig amcuConfig;
    private String[] mFileList;
    private File mPath;
    private String mChosenFile;
    private FarmerDao farmerDao;

    private Handler farmerExportHandler;
    private Runnable farmerExportRunnable;

    @Override
    protected void onStart() {

        if (!Util.isOperator(AllFarmerData.this) || BuildConfig.FLAVOR.equalsIgnoreCase("hatsun")) {
            btnDelete.setVisibility(View.VISIBLE);
        } else {
            btnDelete.setVisibility(View.GONE);
        }
        displayList();
        super.onStart();
    }

    @Override
    public void onClick(View v) {

    }

    public void getAllFarmerData() {

        try {
            allFarmList = (ArrayList<FarmerEntity>) farmerDao.findAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Removed database close statement

        if (allFarmList != null && allFarmList.size() > 0) {
            if (!Util.isOperator(AllFarmerData.this) || BuildConfig.FLAVOR.equalsIgnoreCase("hatsun")) {
                btnDelete.setVisibility(View.VISIBLE);
            }

        } else {
            btnDelete.setVisibility(View.GONE);
        }
    }

    public void displayList() {
        getAllFarmerData();

        databaseHelper = DatabaseHandler.getDatabaseInstance();
//        TODO USE DAO and change adapter
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                /*farmerAdapter = new FarmerAdapter(AllFarmerData.this,
                        databaseHelper.getFarmerDataSociety(String
                                .valueOf(session.getSocietyColumnId())), 0);
                lvFarmerDetails.setAdapter(farmerAdapter);*/
                farmerListAdapter = new FarmerListAdapter(farmerDao.findAllOrderByFarmerId());
                rvFarmerList.setAdapter(farmerListAdapter);
                farmerListAdapter.setOnItemClickListener(new FarmerListAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClicked(int position, Entity entity) {
                        if (allFarmList.size() > 0) {
                            goToAddFarmer(position);
                        }
                    }
                });
                farmerListAdapter.setOnItemLongClickListener(new FarmerListAdapter.OnItemLongLongClickListener() {
                    @Override
                    public boolean onItemLongClicked(int position, Entity entity, View v) {
                        final int pos = position;
                        PopupMenu popup = new PopupMenu(AllFarmerData.this,
                                v);
                        popup.getMenuInflater().inflate(R.menu.popup_menu,
                                popup.getMenu());

                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                            @Override
                            public boolean onMenuItemClick(MenuItem arg0) {

                                DatabaseHandler dbh = DatabaseHandler.getDatabaseInstance(
                                );
                                boolean isCheckAggerateFarmer;

                                // For deleting Aggerate farmer
                                if (allFarmList.get(pos).farmerType != null && allFarmList.get(pos).farmerType.equalsIgnoreCase("Aggregate Farmer")) {
                                    if (allFarmList.get(pos).farmer_id != null && (farmerDao.findAllByAgentId(allFarmList.get(pos).farmer_id).size() > 0)) {
                                        isCheckAggerateFarmer = false;

                                    } else {
                                        isCheckAggerateFarmer = true;
                                    }

                                } else {
                                    isCheckAggerateFarmer = true;
                                }
                                deleteFarmerAndAggerateFarmer(pos, isCheckAggerateFarmer, dbh);


                                return true;
                            }
                        });

                        if (!allFarmList.get(pos).farmer_id.equals(DatabaseHandler.DEFAULT_FARMER_ID)) {
                            if (Util.isOperator(AllFarmerData.this)) {
                                if ((AmcuConfig.getInstance().getKeyAllowFarmerEdit() && AmcuConfig.getInstance().getKeyAllowFarmerCreate())) {
                                    popup.show();
                                }
                            } else {
                                popup.show();
                            }
                        } else
                            Toast.makeText(AllFarmerData.this, "You cannot delete this farmer.", Toast.LENGTH_SHORT).show();

                        //popup.show();

                        return true;
                    }
                });
            }
        });
        //Removed database close statement
    }

    public void addFarmerData() {
        Intent intent = new Intent(AllFarmerData.this, AddFarmerDetails.class);
        intent.putExtra("SelectedList", selectedFarmEntity);
        intent.putExtra("NewFarmer", "NewFarmer");
        startActivity(intent);

    }

    // Export farmer

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        FarmerScannerActivity.backFromFarmer = true;
        finish();
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

                            Toast.makeText(AllFarmerData.this,
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
                    duplicateMsg = test.read(AllFarmerData.this);
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
                            AllFarmerData.this);
                    Toast.makeText(AllFarmerData.this, "Farmers successfully added!", Toast.LENGTH_LONG).show();
                    displayList();
                } else {

                    if (amcuConfig.getKeyAllowFarmerEdit() && !amcuConfig.getKeyAllowFarmerCreate()) {
                        if (ReadExcelFarmer.allFarmEnt != null && ReadExcelFarmer.allFarmEnt.size() == 0) {
                            Util.displayErrorToast("Invalid configration! please check", AllFarmerData.this);
                        } else {
                            Util.displayErrorToast("Invalid format! please try again.", AllFarmerData.this);

                        }
                    } else {

                        Util.displayErrorToast("Invalid format! please try again.", AllFarmerData.this);
                    }
                }

            }
        };
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.allfarmerdetails);

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

        session = new SessionManager(AllFarmerData.this);
        farmerDao = (FarmerDao) DaoFactory.getDao(CollectionConstants.FARMER);

        lvFarmerDetails = (ListView) findViewById(R.id.lvFarmerdetails);
        lvFarmerDetails.setVisibility(View.GONE);
        rvFarmerList = (RecyclerView) findViewById(R.id.rv_farmerList);
        rvFarmerList.setLayoutManager(new LinearLayoutManager(this));
        rvFarmerList.addItemDecoration(new ThinLineDividerDecoration(this));
        rvFarmerList.setVisibility(View.VISIBLE);
        databaseHelper = DatabaseHandler.getDatabaseInstance();
        btnDelete = (Button) findViewById(R.id.btnDelete);

        btnNewUser = (Button) findViewById(R.id.btnNewUser);
        btnImportFarmer = (Button) findViewById(R.id.btnImportFarmer);
        btnExport = (Button) findViewById(R.id.btnList);

        btnExport.setVisibility(View.VISIBLE);
        btnExport.setText("Export & Print");

        btnImportFarmer.setVisibility(View.VISIBLE);
        btnNewUser.setText("New farmer");

        btnImportFarmer.requestFocus();


        inVisibleImportAndNewFarmerButton();

        lvFarmerDetails.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {

                if (allFarmList.size() > 0) {
                    goToAddFarmer(arg2);
                }

            }
        });

        lvFarmerDetails
                .setOnItemLongClickListener(new OnItemLongClickListener() {

                    @Override
                    public boolean onItemLongClick(AdapterView<?> arg0,
                                                   View arg1, int arg2, long arg3) {

                        final int pos = arg2;
                        PopupMenu popup = new PopupMenu(AllFarmerData.this,
                                arg1);
                        popup.getMenuInflater().inflate(R.menu.popup_menu,
                                popup.getMenu());

                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                            @Override
                            public boolean onMenuItemClick(MenuItem arg0) {

                                DatabaseHandler dbh = DatabaseHandler.getDatabaseInstance(
                                );
                                boolean isCheckAggerateFarmer;

                                // For deleting Aggerate farmer
                                if (allFarmList.get(pos).farmerType != null && allFarmList.get(pos).farmerType.equalsIgnoreCase("Aggregate Farmer")) {
                                    if (allFarmList.get(pos).farmer_id != null && (farmerDao.findAllByAgentId(allFarmList.get(pos).farmer_id).size() > 0)) {
                                        isCheckAggerateFarmer = false;

                                    } else {
                                        isCheckAggerateFarmer = true;
                                    }

                                } else {
                                    isCheckAggerateFarmer = true;
                                }
                                deleteFarmerAndAggerateFarmer(pos, isCheckAggerateFarmer, dbh);


                                return true;
                            }
                        });


                        if (Util.isOperator(AllFarmerData.this)) {
                            if ((AmcuConfig.getInstance().getKeyAllowFarmerEdit() && AmcuConfig.getInstance().getKeyAllowFarmerCreate())) {
                                popup.show();
                            }
                        } else {
                            popup.show();
                        }


                        //popup.show();

                        return true;
                    }
                });

        listMilkType = getApplicationContext().getResources().getStringArray(
                R.array.Milk_type);

        btnNewUser.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (Util.isOperator(AllFarmerData.this)) {
                    if (AmcuConfig.getInstance().getKeyAllowFarmerCreate()) {
                        selectedFarmEntity = null;
                        addFarmerData();
                    }
                } else {
                    selectedFarmEntity = null;
                    addFarmerData();

                }
            }
        });

        btnImportFarmer.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                fileName = "";

                if (Util.isOperator(AllFarmerData.this)) {
                    if (AmcuConfig.getInstance().getKeyAllowFarmerEdit() || AmcuConfig.getInstance().getKeyAllowFarmerCreate()) {
                        checkPenDriveStatus();
                    }
                } else {
                    checkPenDriveStatus();
                }


            }
        });

        btnExport.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                checkForPenDrive();
                //   startService(new Intent(AllFarmerData.this,PostFarmerRecords.class));

            }
        });

        btnDelete.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                deleteAllFarmersAlert();

            }
        });

    }

    public void exportFarmerList() {
        setupServiceReceiver();
        final WriteExcel writeExcel = new WriteExcel();
        File root = new File(Util.rootFileName);
        String date = Util.getTodayDateAndTime(1, AllFarmerData.this, false);
        date = date.replace("-", "");

        final File fileSmartAmcu = new File(Util.rootFileName, "smartAmcuReports");

        if (!fileSmartAmcu.exists()) {
            fileSmartAmcu.mkdirs();
        }

        farmerExportHandler = new Handler();
        writeExcel.setWriteCompleteListener(new WriteExcel.OnExcelWriteCompleteListener() {
            @Override
            public void onWriteComplete() {
                isWrittenToFile = true;
            }
        });
        gpxfile = new File(fileSmartAmcu, new SessionManager(AllFarmerData.this)
                .getSocietyName().replace(" ", "") + "_" + date + "_farmerList.xls");

        new Thread(new Runnable() {
            @Override
            public void run() {
                writeExcel.setOutputFile(gpxfile.toString());

                Util.displayErrorToast("Farmer's are getting exported!",
                        AllFarmerData.this);

                try {

                    if (allFarmList.size() > 0) {
                        writeExcel.write(AllFarmerData.this, ExcelConstants.TYPE_FARMER, allFarmList);
                        new SessionManager(AllFarmerData.this).setFileName(gpxfile
                                .toString());
                        new SessionManager(AllFarmerData.this).setReportType(Util.sendFarmerList);
                        isWrittenToFile = true;

                        //startService();
                    } else {
                        Toast.makeText(AllFarmerData.this, "No farmer list to export!",
                                Toast.LENGTH_LONG).show();
                    }

                } catch (WriteException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                farmerExportHandler.post(farmerExportRunnable);
            }
        }).start();


        farmerExportRunnable = new Runnable() {
            @Override
            public void run() {
                if (isWrittenToFile) {
                    receiverForWrite.send(Activity.RESULT_OK, null);

                    PrinterManager parsingPrinterData = new PrinterManager(AllFarmerData.this);
                    parsingPrinterData.print(getFarmerPrintData(allFarmList),
                            PrinterManager.FARMER_EXPORT,
                            null, null, null);
                } else {
                    receiverForWrite.send(Activity.RESULT_CANCELED, null);
                }
            }
        };


    }

    public void startService() {

        Intent intentService = new Intent(AllFarmerData.this, SendEmail.class);
        startService(intentService);

    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {

            case KeyEvent.KEYCODE_F9:
                Util.Logout(AllFarmerData.this);
                finish();
                return true;

            case KeyEvent.KEYCODE_F10:
                Util.restartApp(AllFarmerData.this);
                return true;

            case KeyEvent.KEYCODE_F11:

                Util.restartTab(AllFarmerData.this);
                return true;

            case KeyEvent.KEYCODE_F12:
                Util.shutDownTab(AllFarmerData.this, null);
                return true;
            case KeyEvent.KEYCODE_DEL:
                finish();

                return true;
            case KeyEvent.KEYCODE_PAGE_UP:
                if (btnExport.getVisibility() == View.VISIBLE) {
                    btnExport.requestFocus();
                } else if (btnDelete.getVisibility() == View.VISIBLE) {
                    btnDelete.requestFocus();
                }
                return true;

            case KeyEvent.KEYCODE_PAGE_DOWN:
                btnImportFarmer.requestFocus();
                return true;
            default:
                return super.onKeyUp(keyCode, event);
        }
    }

    public void deleteAllFarmersAlert() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                AllFarmerData.this);

        // set title
        alertDialogBuilder.setTitle("Delete All farmers!");

        // set dialog message
        alertDialogBuilder
                .setMessage("This will delete all existing farmer entries.")
                .setCancelable(false)
                .setPositiveButton("Delete",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                farmerDao.deleteByCenterId(String.valueOf(session.getSocietyColumnId()));

                                displayList();
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


    public void alertForDuplicateEntries(String msg, boolean isDuplicate) {
        String btnText;
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                AllFarmerData.this);

        AmcuConfig amcuConfig = AmcuConfig.getInstance();

        // set title
        if (isDuplicate) {
            alertDialogBuilder.setTitle("Farmers");
        } else {
            alertDialogBuilder.setTitle("Invalid format!");
        }
        // set dialog message
        alertDialogBuilder
                .setMessage(msg)
                .setCancelable(false);

        if (AmcuConfig.getInstance().getKeyAllowFarmerEdit() && !AmcuConfig.getInstance().getKeyAllowFarmerCreate()) {
            btnText = "Update";
        } else {
            btnText = "Insert";

        }

        if (isDuplicate) {
            if (!amcuConfig.getKeyAllowFarmerIncrement()) {
                alertDialogBuilder.setPositiveButton(btnText,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Util.addFarmersTodatBase(
                                        ReadExcelFarmer.allFarmEnt,
                                        AllFarmerData.this);
                                Toast.makeText(AllFarmerData.this, "Farmers successfully added!", Toast.LENGTH_LONG).show();
                                displayList();
                                dialog.cancel();

                            }
                        });
            }
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
            Toast.makeText(AllFarmerData.this, "Copying the file please wait..", Toast.LENGTH_SHORT).show();
            exportFarmerList();
        } else {
            usbAlert();
        }
    }

    public void usbAlert() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                AllFarmerData.this);
        // set title
        alertDialogBuilder.setTitle("Alert!");
        // set dialog message
        alertDialogBuilder
                .setMessage(getResources().getString(R.string.pendrive_alert))
                .setCancelable(false)
                .setPositiveButton("Continue",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Toast.makeText(AllFarmerData.this, "Copying the file please wait..", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(AllFarmerData.this, "File has been successfully written in Pendrive.",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(AllFarmerData.this, "File has been successfully written in Internal storage.",
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(AllFarmerData.this, "Please try again.",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void goToAddFarmer(int arg2) {
        selectedFarmEntity = allFarmList.get(arg2);
        Intent intent = new Intent(AllFarmerData.this,
                AddFarmerDetails.class);
        intent.putExtra("SelectedList", selectedFarmEntity);
        intent.putExtra("NewFarmer", "Farmer");
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left,
                R.anim.slide_out_left);
        // showAlert(allFarmList.get(arg2));


    }

    private void deleteFarmerAndAggerateFarmer(int pos, boolean isCheckAggerateFarmer, DatabaseHandler dbh) {
        if (isCheckAggerateFarmer) {

            long deleteCk = 0;
            try {
                deleteCk = farmerDao.deleteByFarmerId(allFarmList.get(pos).farmer_id);
            } catch (Exception e) {
                e.printStackTrace();
            }


            if (deleteCk > 0) {
                Toast.makeText(
                        AllFarmerData.this,
                        "Farmer details successfully deleted!",
                        Toast.LENGTH_SHORT).show();
                displayList();
            } else {
                Toast.makeText(AllFarmerData.this,
                        "Not deleted", Toast.LENGTH_SHORT)
                        .show();
            }
        } else {
            Toast.makeText(AllFarmerData.this,
                    "Agent id belong to farmer id", Toast.LENGTH_SHORT)
                    .show();

        }

    }

    private void checkPenDriveStatus() {
        if (Util.checkForPendrive()) {
            mPath = new File(Util.rootFileName);
            loadFileList();
        } else {
            Toast.makeText(AllFarmerData.this, "Please attach the pendrive and try again!", Toast.LENGTH_LONG).show();
        }

    }

    private void inVisibleImportAndNewFarmerButton() {
        if (Util.isOperator(AllFarmerData.this)) {
            if (!AmcuConfig.getInstance().getKeyAllowFarmerEdit() && !AmcuConfig.getInstance().getKeyAllowFarmerCreate()) {
                btnNewUser.setVisibility(View.GONE);
                btnImportFarmer.setVisibility(View.GONE);
            } else if (AmcuConfig.getInstance().getKeyAllowFarmerEdit() && !AmcuConfig.getInstance().getKeyAllowFarmerCreate()) {

                btnNewUser.setVisibility(View.GONE);
                btnImportFarmer.setVisibility(View.VISIBLE);

            } else {
                btnNewUser.setVisibility(View.VISIBLE);
                btnImportFarmer.setVisibility(View.VISIBLE);

            }
        } else {
            btnNewUser.setVisibility(View.VISIBLE);
            btnImportFarmer.setVisibility(View.VISIBLE);

        }
    }


    private String getFarmerPrintData(ArrayList<FarmerEntity> allFarmList) {
        StringBuilder printData = new StringBuilder().append("\n");
        for (FarmerEntity farmerEntity : allFarmList) {
            String name = farmerEntity.farmer_name;

            if (!(AmcuConfig.getInstance().getPrinter().equalsIgnoreCase(
                    AppConstants.PRINTER_HINDI)
                    || AmcuConfig.getInstance().getPrinter().equalsIgnoreCase(
                    AppConstants.PRINTER_TAMIL))) {
                Pattern p = Pattern.compile("[^a-z 0-9]", Pattern.CASE_INSENSITIVE);
                Matcher m = p.matcher(name);
                if (m.find()) {

                    name = "";

                } else {
                    if (name.length() > 15) {
                        name = name.substring(0, 15);
                    }

                }
            } else {
                if (name.length() > 25) {
                    name = name.substring(0, 25);
                }
            }

            printData.append(farmerEntity.farmer_id).append("   ").
                    append(getCattleType(farmerEntity.farmer_cattle)).append("   ").
                    append(name)
                    .append("\n");

        }

        return printData.toString();
    }


    private String getCattleType(String name) {
        StringBuilder returnName = new StringBuilder(name);
        int lenght = 10;


        for (int i = name.length(); i < lenght; i++) {
            returnName = returnName.append(" ");
        }

        return returnName.toString();
    }


    @Override
    protected void onStop() {
        super.onStop();

        if (farmerExportHandler != null) {
            farmerExportHandler.removeCallbacks(farmerExportRunnable);
        }

    }
}
