package com.devapp.devmain.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;

import com.devapp.devmain.dao.DaoFactory;
import com.devapp.devmain.dao.RateChartNameDao;
import com.devapp.devmain.dao.RateDao;
import com.devapp.devmain.entity.ExcelConstants;
import com.devapp.devmain.entity.RateChartEntity;
import com.devapp.devmain.postentities.CollectionConstants;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.SessionManager;
import com.devapp.devmain.usb.WriteExcel;
import com.devapp.devmain.user.Util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import jxl.write.WriteException;

public class CreateRateChartForSociety extends IntentService {

    File gpxfile;
    ArrayList<RateChartEntity> allRateChartEnt;
    String version = "001";
    Runnable updateRunnable;
    Handler myHandler = new Handler();
    HashMap<String, ArrayList<RateChartEntity>> map = new HashMap<String, ArrayList<RateChartEntity>>();
    String farmerId;
    SessionManager session;
    AmcuConfig amcuConfig;

    public CreateRateChartForSociety() {
        super("Service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        session = new SessionManager(getApplicationContext());
        amcuConfig = AmcuConfig.getInstance();
        farmerId = session.getFarmerID();
//        File root = Environment.getExternalStorageDirectory();
        File root = new File(Util.getSDCardPath());

        // DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        // String date = df.format(Calendar.getInstance().getTime());
        //
        // String date1 = date.replace("-", "_");
        // String date2 = date1.replace("+", "_");
        // String date3 = date2.replace(":", "_");

        gpxfile = new File(root,
                new SessionManager(getApplicationContext())
                        .getRecentRateChart());

        new Thread(new Runnable() {

            @Override
            public void run() {
                getRateChartList();

                myHandler.post(updateRunnable);
            }
        }).start();

        updateRunnable = new Runnable() {

            @Override
            public void run() {
                writeExcelFile();
            }
        };

    }

    public void writeExcelFile() {
        final WriteExcel writeExcel = new WriteExcel();

        map = (HashMap<String, ArrayList<RateChartEntity>>) Util
                .ReadSettingsMapGroup(getApplicationContext(),
                        gpxfile.toString());

        writeExcel.setOutputFile(gpxfile.toString());
        writeExcel.setWriteCompleteListener(new WriteExcel.OnExcelWriteCompleteListener() {
            @Override
            public void onWriteComplete() {

            }
        });
        try {
            writeExcel.write(getApplicationContext(), ExcelConstants.TYPE_RATE_CHART, allRateChartEnt);

        } catch (WriteException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void getRateChartList() {

        RateDao rateDao = (RateDao) DaoFactory.getDao(CollectionConstants.RATES);
        RateChartNameDao rateChartNameDao = (RateChartNameDao) DaoFactory.getDao(CollectionConstants.RATECHART_NAME);

        long refId = rateChartNameDao.findRateRefIdFromName(amcuConfig.getRateChartName());
        try {
            allRateChartEnt = rateDao.findAllByName(refId);
            map.put(farmerId, allRateChartEnt);

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (map != null) {
            Util.WriteHashMapGroup(getApplicationContext(), map,
                    gpxfile.toString());
        }
    }

}
