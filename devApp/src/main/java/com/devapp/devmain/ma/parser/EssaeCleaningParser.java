package com.devapp.devmain.ma.parser;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.devapp.devmain.DevAppApplication;
import com.devapp.devmain.encryption.CleaningConsolidatedData;
import com.devapp.devmain.entity.essae.Datum;
import com.devapp.devmain.entity.essae.EssaeCleaningEntity;
import com.devapp.devmain.entity.essae.Fat;
import com.devapp.devmain.entity.essae.MaLogEntry;
import com.devapp.devmain.entity.essae.Snf;
import com.devapp.devmain.entity.essae.Temperature;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.server.SessionManager;
import com.devapp.devmain.user.Util;
import com.devapp.smartcc.entityandconstants.SmartCCConstants;

import java.util.ArrayList;
import java.util.List;

import static com.devapp.smartcc.entityandconstants.SmartCCConstants.ESSAE;

/**
 * Created by xx on 12/3/18.
 */

public class EssaeCleaningParser {
    private EssaeCleaningEntity entity;
    private CleaningConsolidatedData cleaningConsolidatedData;
    private MaLogEntry maLogEntry;
    private boolean parseCorrectionData, parseCleaningData;
    private List<MaLogEntry> maLogEntryList;
    private List<Datum> dataList;
    private DatabaseHandler databaseHandler;
    private SQLiteDatabase db;
    private AmcuConfig amcuConfig;
    private SessionManager session;
    private JsonDataListener mJsonDataListener;
    private int status = 1;
    private long currentTime;

    public EssaeCleaningParser() {
        amcuConfig = AmcuConfig.getInstance();
        session = new SessionManager(DevAppApplication.getAmcuContext());
        databaseHandler = DatabaseHandler.getDatabaseInstance();
        db = databaseHandler.getWritableDatabase();
    }

    private void initializeEntities() {
        entity = new EssaeCleaningEntity();
        entity.centerId = session.getCollectionID();
        entity.deviceId = amcuConfig.getDeviceID();
        entity.queriedTime = Util.formatTimeWithTimeZone();
        cleaningConsolidatedData = new CleaningConsolidatedData();
        cleaningConsolidatedData.records = new ArrayList<EssaeCleaningEntity>();
        maLogEntryList = new ArrayList<MaLogEntry>();

    }

    public void setListener(JsonDataListener jsonDataListener) {
        mJsonDataListener = jsonDataListener;
    }

    public void splitIntoRecords(String data) {
        initializeEntities();
        Log.v(ESSAE, "Data in cleaning parser:" + data);
        String[] records;
        records = data.split("\r\n");
        currentTime = Util.getCurrentTimeInMillis();
        if (data.contains("LFAT")) {
            storeCalibrationData(records);
        } else if (data.contains("TEMP")) {
            storeCorrectionData(records);
        } else if (data.contains("CLEANING")) {
            storeCleaningData(records);
        }
        if (parseCorrectionData)
            storeCorrectionData(records);
        if (parseCleaningData)
            storeCleaningData(records);
        fetchUnsentRecords();
    }

    private void storeCalibrationData(String[] records) {
        String regex = "\\d+-\\d+-\\d+,\\d+:\\d+:\\d+,\\w,\\d+\\.\\d+,\\d+\\.\\d+,\\d+.\\d+,\\d+\\.\\d+";
        for (int i = 0; i < records.length; i++) {
            if (records[i].matches(regex)) {
                if (!records[i].contains("TIME") && !records[i].contains("DATE") && !records[i].contains("TEMP") &&
                        !records[i].contains("+") && !records[i].contains("RINSE")) {
                    String[] fields = records[i].split(",");
                    String value = fields[2] + "," + fields[3] + "," + fields[4] + "," + fields[5] + "," + fields[6];
                    databaseHandler.insertCleaningDataIntoEssaeTable(db, currentTime, fields[0], fields[1], SmartCCConstants.CALIBRATION, value, status);
                }
            } else if (records[i].contains("TEMP")) {
                parseCorrectionData = true;
                break;
            } else if (records[i].contains("CLEANING")) {
                parseCleaningData = true;
                break;
            }
        }
    }

    private void storeCorrectionData(String[] records) {
        String regex = "\\d+-\\d+-\\d+,\\d+:\\d+:\\d+,\\w,[\\+|-]\\d+\\.\\d+,[\\+|-]\\d+\\.\\d+,[\\+|-]\\d+\\.\\d+,[\\+|-]\\d+\\.\\d+";
        for (int i = 0; i < records.length; i++) {
            if (records[i].matches(regex)) {
                if (isCorrectionData(records[i])) {
                    String[] fields = records[i].split(",");
                    String value = fields[2] + "," + fields[3] + "," + fields[4] + "," + fields[5] + "," + fields[6];
                    databaseHandler.insertCleaningDataIntoEssaeTable(db, currentTime, fields[0], fields[1], SmartCCConstants.CORRECTION, value, status);
                }
            } else if (records[i].contains("CLEANING")) {
                parseCleaningData = true;
                break;
            }
        }
        parseCorrectionData = false;
    }

    private boolean isCorrectionData(String row) {
        boolean retValue = false;
        String[] fields = row.split(",");
        if (!row.contains("CLEANING")) {
            if (row.contains("+") || ((fields[3].contains("-") || fields[4].contains("-") ||
                    fields[5].contains("-") || fields[6].contains("-")))) {
                retValue = true;
            }
        }
        return retValue;
    }

    private void storeCleaningData(String[] records) {
        String regex = "\\d+-\\d+-\\d+,\\d+:\\d+:\\d+,\\w+\\s*\\w*";
        for (int i = 0; i < records.length; i++) {
            if (records[i].matches(regex)) {
                if (records[i].contains("RINSE")) {
                    String[] fields = records[i].split(",");
                    String value = fields[2];
                    databaseHandler.insertCleaningDataIntoEssaeTable(db, currentTime, fields[0], fields[1], SmartCCConstants.CLEANING, value, status);
                }
            }
        }
        parseCleaningData = false;
    }

    private void fetchUnsentRecords() {
        String[] dbRecords = new String[0];
        Cursor cursor = databaseHandler.getUnsentEssaeRecords();
        if (cursor != null && cursor.moveToFirst()) {
            dbRecords = new String[cursor.getCount()];
            int ct = 0;
            do {
                dbRecords[ct] = cursor.getString(2) + "," + cursor.getString(3) + "," + cursor.getString(4) + "," + cursor.getString(5);
                ct++;
                Log.v(ESSAE, "Unsent records: " + cursor.getString(1) + "," + cursor.getString(2) + "," +
                        cursor.getString(3) + "," + cursor.getString(4) + "," + cursor.getString(5));
            } while (cursor.moveToNext());
        }
        if (cursor != null)
            cursor.close();
        if (dbRecords.length > 0) {
            parseCalibrationData(dbRecords);
            if (parseCorrectionData)
                parseCorrectionData(dbRecords);
            if (parseCleaningData)
                parseCleaningData(dbRecords);
            cleaningConsolidatedData.records.add(entity);
            cleaningConsolidatedData.csvVersion = "1.0";
            String jsonStr = Util.toJson(entity);
            String jsonEncrypt = Util.toJson(cleaningConsolidatedData);
            Log.v(ESSAE, "DB JSON :" + jsonStr);
            Log.v(ESSAE, "Encrypt JSON :" + jsonEncrypt);
//            SmartCCConstants.unsentCleaningData = jsonEncrypt;
            amcuConfig.setUnsentCleaningData(jsonEncrypt);
            mJsonDataListener.onJsonDataReceived(jsonStr);
        } else
            Log.v(ESSAE, "0 records ");
    }


    private void parseCalibrationData(String[] records) {
        maLogEntry = new MaLogEntry();
        dataList = new ArrayList<Datum>();
        maLogEntry.type = SmartCCConstants.CALIBRATION;
        for (int i = 0; i < records.length; i++) {
            if (records[i].contains(SmartCCConstants.CALIBRATION)) {
                String[] fields = records[i].split(",");
                Datum datum = new Datum();
                datum.date = fields[0];
                datum.time = fields[1];
                datum.milkType = fields[3];
                Fat fat = new Fat();
                Snf snf = new Snf();
                try {
                    fat.lowerFat = Double.valueOf(fields[4]);
                    fat.higherFat = Double.valueOf(fields[6]);
                    datum.fat = fat;
                    snf.lowerSnf = Double.valueOf(fields[5]);
                    snf.higherSnf = Double.valueOf(fields[7]);
                    datum.snf = snf;
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                dataList.add(datum);
            } else if (records[i].contains(SmartCCConstants.CORRECTION)) {
                parseCorrectionData = true;
                break;
            } else if (records[i].contains(SmartCCConstants.CLEANING)) {
                parseCleaningData = true;
                break;
            }
        }
        maLogEntry.dataList = dataList;
        maLogEntryList.add(maLogEntry);
        entity.maLogEntryList = maLogEntryList;
    }


    private void parseCorrectionData(String[] records) {
        maLogEntry = new MaLogEntry();
        dataList = new ArrayList<Datum>();
        maLogEntry.type = SmartCCConstants.CORRECTION;
        for (int i = 0; i < records.length; i++) {
            if (records[i].contains(SmartCCConstants.CORRECTION)) {
                String[] fields = records[i].split(",");
                Datum datum = new Datum();
                datum.date = fields[0];
                datum.time = fields[1];
                datum.milkType = fields[3];
                Fat fat = new Fat();
                String fatValue = fields[4];
                fatValue = fatValue.replace("+", "");
                Snf snf = new Snf();
                String snfValue = fields[5];
                snfValue = snfValue.replace("+", "");
                String awValue = fields[6];
                awValue = awValue.replace("+", "");
                Temperature temperature = new Temperature();
                String tempValue = fields[7];
                tempValue = tempValue.replace("+", "");
                try {
                    datum.awm = Double.valueOf(awValue);
                    fat.fat = Double.valueOf(fatValue);
                    snf.snf = Double.valueOf(snfValue);
                    temperature.value = Double.valueOf(tempValue);
                    datum.fat = fat;
                    datum.snf = snf;
                    datum.temperature = temperature;

                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                temperature.unit = "C";
                dataList.add(datum);
            } else if (records[i].contains(SmartCCConstants.CLEANING)) {
                parseCleaningData = true;
                break;
            }
        }
        maLogEntry.dataList = dataList;
        maLogEntryList.add(maLogEntry);
        entity.maLogEntryList = maLogEntryList;
        parseCorrectionData = false;
    }

    private void parseCleaningData(String[] records) {
        maLogEntry = new MaLogEntry();
        dataList = new ArrayList<Datum>();
        maLogEntry.type = SmartCCConstants.CLEANING;
        for (int i = 0; i < records.length; i++) {
            if (records[i].contains(SmartCCConstants.CLEANING)) {
                String[] fields = records[i].split(",");
                Datum datum = new Datum();
                datum.date = fields[0];
                datum.time = fields[1];
                dataList.add(datum);
            }
        }
        maLogEntry.dataList = dataList;
        maLogEntryList.add(maLogEntry);
        entity.maLogEntryList = maLogEntryList;
        parseCleaningData = false;
    }

    public interface JsonDataListener {
        void onJsonDataReceived(String jsonData);
    }
}
