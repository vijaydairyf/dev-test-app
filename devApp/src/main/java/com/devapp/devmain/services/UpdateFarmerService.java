package com.devapp.devmain.services;

import android.app.IntentService;
import android.content.Intent;

import com.devapp.devmain.dao.DaoFactory;
import com.devapp.devmain.dao.FarmerDao;
import com.devapp.devmain.devicemanager.DatabaseManager;
import com.devapp.devmain.entity.ExcelConstants;
import com.devapp.devmain.entity.FarmerEntity;
import com.devapp.devmain.postentities.CollectionConstants;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.SessionManager;
import com.devapp.devmain.usb.WriteExcel;
import com.devapp.devmain.user.Util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import jxl.write.WriteException;


/**
 * Created by Upendra on 10/13/2015.
 */
public class UpdateFarmerService extends IntentService {


    public static final String FROM_CONFIGURATION = "fromConfiguration";
    File gpxfile;
    private boolean fromConfiguration = false;

    private DatabaseManager databaseManager;
    private FarmerDao farmerDao;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public UpdateFarmerService() {
        super("UpdateFarmerService");
    }


    // Export farmer

    @Override
    protected void onHandleIntent(Intent intent) {

        AmcuConfig amcuConfig = AmcuConfig.getInstance();
        farmerDao = (FarmerDao) DaoFactory.getDao(CollectionConstants.FARMER);

        ArrayList<FarmerEntity> arrayListFarmer = new ArrayList<FarmerEntity>();

        int digitLength = 4;
        try {
            digitLength = intent.getIntExtra("DigitLength", 4);
            fromConfiguration = intent.getBooleanExtra(FROM_CONFIGURATION, false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        databaseManager = new DatabaseManager(getApplicationContext());

        arrayListFarmer = (ArrayList<FarmerEntity>) farmerDao.findAll();


        int len = amcuConfig.getFarmerIdDigit();

        if (arrayListFarmer.size() > 0) {
            farmerDao.deleteAll();
            ArrayList<FarmerEntity> modifiedFarmers =
                    getFarmerEntityToModify(arrayListFarmer, amcuConfig.getFarmerIdDigit());
            farmerDao.saveAll(modifiedFarmers);
            /*if (digitLength < saveSession.getFarmerIdDigit()) {

                databaseManager.updateEarlierFarmerList(arrayListFarmer, saveSession.getFarmerIdDigit());
            }
            else if(digitLength == saveSession.getFarmerIdDigit())
            {

            }
            else if(digitLength > saveSession.getFarmerIdDigit())
            {

                ArrayList<FarmerEntity> modifiedFarmers =
                        getFarmerEntityToModify(arrayListFarmer,saveSession.getFarmerIdDigit());
                databaseManager.updateEarlierFarmerList(modifiedFarmers, saveSession.getFarmerIdDigit());

            }*/

            exportFarmerList(arrayListFarmer);
        }


        if (fromConfiguration) {
            Util.displayErrorToast("Press F10 to refresh the app", getApplicationContext());
        }

        //  updateSampleIds(digitLength);

    }

    public void exportFarmerList(ArrayList<FarmerEntity> allFarmList) {

        String path = Util.getSDCardPath();

        final WriteExcel writeExcel = new WriteExcel();
        String date = Util.getTodayDateAndTime(1, getApplicationContext(), false);
        date = date.replace("-", "");
        final File fileSmartAmcu = new File(path, "smartAmcuReports");
        if (!fileSmartAmcu.exists()) {
            fileSmartAmcu.mkdirs();
        }

        writeExcel.setWriteCompleteListener(new WriteExcel.OnExcelWriteCompleteListener() {
            @Override
            public void onWriteComplete() {

            }
        });

        gpxfile = new File(fileSmartAmcu, new SessionManager(getApplicationContext())
                .getSocietyName().replace(" ", "") + "_" + date + "_farmerList.xls");

        writeExcel.setOutputFile(gpxfile.toString());
        try {

            if (allFarmList.size() > 0) {
                writeExcel.write(getApplicationContext(), ExcelConstants.TYPE_FARMER, allFarmList);
                new SessionManager(getApplicationContext()).setFileName(gpxfile
                        .toString());
                new SessionManager(getApplicationContext()).setReportType(Util.UPDATE_FARMERLIST);
                startService();
            }
        } catch (WriteException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startService() {
        Intent intentService = new Intent(getApplicationContext(), SendEmail.class);
        startService(intentService);
    }


    private int farmerIdMaxLimit(int farmerIdDigitLength) {
        String limit = "";
        for (int i = 1; i <= farmerIdDigitLength; i++) {
            limit = limit + 9;
        }
        return Integer.parseInt(limit);
    }

    private ArrayList<FarmerEntity> getFarmerEntityToModify(
            ArrayList<FarmerEntity> allFarmerEntity, int farmerIdDigitLength) {
        ArrayList<FarmerEntity> modifiedFarmerList = new ArrayList<>();
        int limit = farmerIdMaxLimit(farmerIdDigitLength);

        for (FarmerEntity farmerEntity : allFarmerEntity) {

            if (Integer.parseInt(farmerEntity.farmer_id) <= limit) {
                farmerEntity.farmer_id = Util.paddingFarmerId(
                        String.valueOf(Integer.parseInt(farmerEntity.farmer_id)), farmerIdDigitLength);
                if (farmerEntity.agentId != null) {
                    int agentid = -1;
                    try {
                        agentid = Integer.parseInt(farmerEntity.agentId);
                        farmerEntity.agentId = Util.paddingFarmerId(farmerEntity.agentId, farmerIdDigitLength);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
                modifiedFarmerList.add(farmerEntity);
            }

        }

        return modifiedFarmerList;

    }


    /**
     * As per 11.6.3 Changes not update the Sample data as
     * per farmer Id length configuration
     *
     * @param digitLength
     */
/*
    private void updateSampleIds(int digitLength) {
        ArrayList<SampleDataEntity> arraySampleData = null;
        arraySampleData = databaseManager.getAllSampleEntity();
        databaseManager.deleteAllSampleId();
        if (digitLength < config.getFarmerIdDigit()) {
            databaseManager.updateEarlierSampleList(arraySampleData, config.getFarmerIdDigit());
        } else {
            Util.displayErrorToast("Press F10 to refresh the app", getApplicationContext());
        }

    }

*/

}
