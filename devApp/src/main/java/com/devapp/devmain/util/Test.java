package com.devapp.devmain.util;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.devapp.devmain.entity.LogEntity;
import com.devapp.devmain.entity.LogId;
import com.devapp.devmain.httptasks.PostCollectionRecordsService;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.smartcc.entityandconstants.SmartCCUtil;

/**
 * Created by Upendra on 9/19/2016.
 */
public class Test {


    Context mContext;

    public Test(Context context) {
        this.mContext = context;
    }

    public boolean isTestVersion() {
        boolean isTestPackage = false;
        String versionName = "";

        try {
            versionName = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (versionName.contains("Test")) {
            isTestPackage = true;
        }

        return isTestPackage;
    }

    public void allowTestConfiguration() {


        AmcuConfig amcuConfig = AmcuConfig.getInstance();
        amcuConfig.setSalesData("1.02", "1.30", true);
//        amcuConfig.setDisableManualForDispatchValue(false);
//        amcuConfig.setSmartCCFeature(true);
//        amcuConfig.setApkFromFileSystem(true);
        amcuConfig.setURLHeader("http://");
        amcuConfig.setIptableRule(true);
        amcuConfig.setMaAndWsManualAndAllowSMS(true, true, true);
//        amcuConfig.setAllowEquipMentBasedCollection(true);
//        amcuConfig.setEnableCenterCollection(true);
//        amcuConfig.setMultipleUser(true);
//        amcuConfig.setDispatchValue(true);


        /*new SessionManager(DevAppApplication.getAmcuContext()).setKeyBluetoothEnable(false);
        amcuConfig.setMaAndWsManualAndAllowSMS(true, true, true);*/
//
////     //        saveSession.setDisplayProteinConfiguration(2);
//        amcuConfig.setMaAndWsManualAndAllowSMS(true, true, true);
//        amcuConfig.setSmartCCFeature(false);
//        amcuConfig.setEnableRejectForRatechart(true);
////        amcuConfig.setDynamicRateChartValue(true);
//        amcuConfig.setKeyAllowProteinValue(false);
//        amcuConfig.setApkFromFileSystem(true);
//        amcuConfig.setEditableRate(false);
//        amcuConfig.setNumberShiftCanBeEdited(6);
//
//        amcuConfig.setDecimalRoundOffWeigh(2);
//        amcuConfig.setDecimalRoundOffRate(2);
//        amcuConfig.setDecimalRoundOffAmount(2);
//        amcuConfig.setEnableSales(true);
//        amcuConfig.setRateFATConfiguration(1);
//
//        amcuConfig.setURLHeader("http://");
//        amcuConfig.setEnableCenterCollection(true);
//        amcuConfig.setDispatchValue(true);
//        amcuConfig.setMultipleUser(true);
//        amcuConfig.setServer("planner.stellapps.com");
//        amcuConfig.setKeyAllowAgentFarmerCollection(true);
//        amcuConfig.setAllowEditReport(true);
//        amcuConfig.setNumberShiftCanBeEdited(1);
//        amcuConfig.setKeyAllowEditCollection(true);
//        amcuConfig.setAllowMultipleCollection(true);
//        amcuConfig.setMultipleUser(false);
//        amcuConfig.setAllowVisiblilityReportEditing(true);
//        amcuConfig.setCreateAndEdit(true);
//        amcuConfig.setEnableFilledOrEmptyCans(true);
//        amcuConfig.setAllowEquipMentBasedCollection(true);
       /*  amcuConfig.setSmartCCFeature(false);

        //  saveSession.setNumberOfShiftEdited(31

        amcuConfig.setKeyEscapeEnableCollection(false);
        //  saveSession.setKeyEnableCollectionConstraints(true);
        amcuConfig.setMultipleMA(false);
        amcuConfig.setCheckboxMultipleMA(false);

        // saveSession.setFarmerIdDigit(3);
        amcuConfig.setEnableFarmerLenghtDisplay(true);
        // saveSession.setDevicePassword("32868783");
        amcuConfig.setKeyCloudSupport(false);
        amcuConfig.setKeyAllowFormerIncrement(false);
        amcuConfig.setWeighingPrefix("N");
        amcuConfig.setWeighingSuffix("=lt");
        amcuConfig.setWeighingSeparator("CRLF");

        amcuConfig.setKeyAllowProteinValue(true);
        amcuConfig.setKeyAllowDisplayRate(true);
        amcuConfig.setKeyAllowFarmerEdit(true);
        amcuConfig.setKeyAllowFarmerCreate(true);
//        saveSession.setDevicePassword("09560897");
        amcuConfig.setEnableSales(true);

        amcuConfig.setApkFromFileSystem(true);*/


//        saveSession.setKeyAllowTwoDeciaml(false);
//        saveSession.setRoundCheckFatAndSnf(false);
    }

    public void checkTransaction() {
        DatabaseHandler db = DatabaseHandler.getDatabaseInstance();

        final Object object = new Object();


        final SQLiteDatabase sqLiteDatabase1 = DatabaseHandler.getPrimaryDatabase();
        final SQLiteDatabase sqLiteDatabase2 = DatabaseHandler.getPrimaryDatabase();

        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {

                try {

                    sqLiteDatabase1.beginTransaction();

                    for (int i = 0; i < 10; i++) {
                        //  synchronized (object) {
                        Log.v("Thread 1: ", String.valueOf(i));
                        sqLiteDatabase2.beginTransaction();

                        ContentValues contentValues = new ContentValues();
                        contentValues.put(DatabaseHandler.KEY_WEIGHINGSCALE_BAUDRATE, 1000 + i);
                        contentValues.put(DatabaseHandler.KEY_WEIGHINGSCALE_NAME, "name " + i);
                        sqLiteDatabase1.insert(DatabaseHandler.TABLE_WEIGHING_SCALE, null, contentValues);
                        Thread.sleep(1000);
                           /*     object.wait();
                                object.notifyAll();
                            }*/
                    }
                    sqLiteDatabase2.setTransactionSuccessful();
                    sqLiteDatabase2.endTransaction();
                    sqLiteDatabase1.setTransactionSuccessful();
                    sqLiteDatabase1.endTransaction();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.v("Thread 1: ", e.getMessage().toString());
                }
            }

        });


        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    // sqLiteDatabase2.beginTransaction();
//
                    for (int i = 0; i < 10; i++) {
                        //  synchronized (object) {

                        /*Log.v("Thread 2: ", String.valueOf(i));
                        String query = "Select max("+DatabaseHandler.KEY_WEIGHINGSCALE_BAUDRATE+")"
                                +" from "+DatabaseHandler.TABLE_WEIGHING_SCALE;
                        Cursor cursor = sqLiteDatabase2.rawQuery(query,null);

                        if(cursor!=null && cursor.moveToFirst())
                        {
                            do {
                                Log.v("Thread 2",
                                        cursor.getString(0));
                            } while (cursor.moveToNext());
                        }


                        Thread.sleep(1000);*/
//                              object.notifyAll();
//                                object.wait();
//
//                            }

                    }

                      /*  sqLiteDatabase2.setTransactionSuccessful();
                        sqLiteDatabase2.endTransaction();*/
                } catch (Exception e) {
                    e.printStackTrace();
                    e.printStackTrace();
                    Log.v("Thread 2: ", e.getMessage().toString());
                }

            }

        });

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }


    public void testRateLogs() {
        LogEntity logEntity = new LogEntity();

        AmcuConfig amcuConfig = AmcuConfig.getInstance();


        logEntity.id = new LogId();
        logEntity.id.collectionId = "SOC123";
        logEntity.mode = "OFFLINE";
        logEntity.type = EntityType.RATE_CHART;
        logEntity.id.deviceId = amcuConfig.getDeviceID();
        logEntity.id.userId = "SMARTOPERATOR";
        logEntity.metaData = " Entity  information";
        logEntity.receivedDate = SmartCCUtil.getCollectionDateFromLongTime(System.currentTimeMillis());

        String json = PostCollectionRecordsService.toJson(logEntity);

        System.out.print(json);


    }


}
