package com.devapp.devmain.devicemanager;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import com.devapp.devmain.dao.CollectionRecordDao;
import com.devapp.devmain.dao.DaoFactory;
import com.devapp.devmain.dao.IncentiveRateDao;
import com.devapp.devmain.dao.RateChartNameDao;
import com.devapp.devmain.dao.RateDao;
import com.devapp.devmain.entity.CenterEntity;
import com.devapp.devmain.entity.Entity;
import com.devapp.devmain.entity.MilkAnalyserEntity;
import com.devapp.devmain.entity.PostEndShift;
import com.devapp.devmain.entity.RateChartEntity;
import com.devapp.devmain.entity.RateChartPostEntity;
import com.devapp.devmain.entity.RatechartDetailsEnt;
import com.devapp.devmain.entity.ReportEntity;
import com.devapp.devmain.entity.SampleDataEntity;
import com.devapp.devmain.entitymanager.RateChartManager;
import com.devapp.devmain.helper.CattleType;
import com.devapp.devmain.helper.DatabaseEntity;
import com.devapp.devmain.main.CustomRateChartUtil;
import com.devapp.devmain.main.SplashActivity;
import com.devapp.devmain.postentities.CollectionConstants;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.server.SessionManager;
import com.devapp.devmain.tableEntities.RateChartNameTable;
import com.devapp.devmain.tableEntities.RateTable;
import com.devapp.devmain.usb.ReadExcel;
import com.devapp.devmain.user.Util;
import com.devapp.smartcc.entityandconstants.SmartCCConstants;
import com.devapp.smartcc.entityandconstants.SmartCCUtil;
import com.devApp.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static android.content.Context.NOTIFICATION_SERVICE;

public class DatabaseManager {

    public int deleteRateChart = 3;
    public int addRateChartCow = 4;
    public int addRateChartBuffalo = 5;
    public int addRateChartMix = 6;

    AmcuConfig amcuConfig;
    SessionManager session;

    ArrayList<RateChartEntity> allRatechartEntityForExcel = new ArrayList<RateChartEntity>();
    RateChartNameDao rateChartNameDao;
    RateDao rateDao;
    CollectionRecordDao collectionRecordDao;
    IncentiveRateDao incentiveRateDao;
    private Context mContext;

    public DatabaseManager(Context context) {
        mContext = context;
        amcuConfig = AmcuConfig.getInstance();
        session = new SessionManager(mContext);
        rateChartNameDao = (RateChartNameDao) DaoFactory.getDao(CollectionConstants.RATECHART_NAME);
        rateDao = (RateDao) DaoFactory.getDao(CollectionConstants.RATES);
        collectionRecordDao = (CollectionRecordDao)
                DaoFactory.getDao(CollectionConstants.REPORT_TYPE_COLLECTION);
        incentiveRateDao = (IncentiveRateDao) DaoFactory.getDao(CollectionConstants.INCENTIVE_RATES);

    }

    public synchronized void manageRateChart() {
        DatabaseHandler dbh = DatabaseHandler.getDatabaseInstance();

        if (amcuConfig.getMyRateChartEnable()) {

            if ((amcuConfig.getCollectionEndShift() || Util.isThisNewShift(mContext)) ||
                    (!amcuConfig.getCurrentSessionStartedWithCow() && !amcuConfig.getCurrentSessionStartedWithBuff()
                            && !amcuConfig.getCurrentSessionStartedWithMix())) {
                try {
                    CustomRateChartUtil.getInstance(mContext).deleteExpireRateChart();
                } catch (Exception e) {
                }
                CustomRateChartUtil.getInstance(mContext).setMyrateChart();

            } else {
                if ((!amcuConfig.getCurrentSessionStartedWithCow() ||
                        amcuConfig.getRateChartForCow() == null)) {

                    amcuConfig.setRateChartForCow(dbh.getSelectedMyRateChart(CattleType.COW));

                } else if (
                        (!amcuConfig.getCurrentSessionStartedWithBuff() ||
                                amcuConfig.getRateChartForBuffalo() == null
                        )) {

                    amcuConfig.setRateChartForBuffalo(dbh.getSelectedMyRateChart(CattleType.BUFFALO));

                } else if ((!amcuConfig.getCurrentSessionStartedWithMix() ||
                        amcuConfig.getRateChartForMixed() == null)) {
                    amcuConfig.setRateChartForMixed(dbh.getSelectedMyRateChart(CattleType.MIXED));

                }
            }
        } else {
            ArrayList<RatechartDetailsEnt> allRateChartDetailsEnt = getAllRateChart();
            deleteExpiredRateCharts(allRateChartDetailsEnt);
            setOperationalRateChart(allRateChartDetailsEnt);
        }
    }

    private void setOperationalRateChart(ArrayList<RatechartDetailsEnt> ratechartDetailsEnts) {
        if (ratechartDetailsEnts == null || ratechartDetailsEnts.size() == 0) {
            Util.displayErrorToast("Rate charts not available for this center.", mContext);
            return;
        }

        String currentDate = new SmartCCUtil(mContext).getReportFormatDate();
        String currentShift = Util.getCurrentShift();

        CollectionRecordDao collectionRecordDao = (CollectionRecordDao)
                DaoFactory.getDao(CollectionConstants.REPORT_TYPE_COLLECTION);
        ReportEntity reportEntity = collectionRecordDao.findCollectionForDateShiftAndType(
                currentDate, currentShift, null);

        if (amcuConfig.getCollectionEndShift() || reportEntity == null) {
            setNewRateChart();
        } else {
            setRatechartForCattleType(currentDate, currentShift,
                    CattleType.COW, amcuConfig.getRateChartForCow());
            setRatechartForCattleType(currentDate, currentShift, CattleType.BUFFALO,
                    amcuConfig.getRateChartForBuffalo());
            setRatechartForCattleType(currentDate, currentShift, CattleType.MIXED,
                    amcuConfig.getRateChartForMixed());
        }

    }


    private void setRatechartForCattleType(String currentDate,
                                           String currentShift,
                                           String milkType,
                                           String currentRateChart) {
        ReportEntity reportEntity = collectionRecordDao.findCollectionForDateShiftAndType(
                currentDate, currentShift, milkType);

        if (reportEntity == null || currentRateChart == null) {
            setLatestRateChart(milkType, Util.RATECHART_TYPE_COLLECTION);
            setLatestRateChart(milkType, Util.RATECHART_TYPE_DYNAMIC);
            setLatestRateChart(milkType, Util.RATECHART_TYPE_BONUS);
            setLatestRateChart(milkType, SmartCCConstants.RATECHART_TYPE_SOUR);
            setLatestRateChart(milkType, SmartCCConstants.RATECHART_TYPE_PROTEIN);
        }
    }


    /**
     * Delete the expiry the Rate chart
     *
     * @param allRateChartDetailsEnt
     */
    private synchronized void deleteExpiredRateCharts(ArrayList<RatechartDetailsEnt> allRateChartDetailsEnt) {

        if (allRateChartDetailsEnt == null) {
            return;
        }

        for (RatechartDetailsEnt ratechartDetailsEnt : allRateChartDetailsEnt) {
            if (ratechartDetailsEnt.rateLvalidityTo < SmartCCUtil.getTodayTimeInMilli()) {
                deleteRatechart(ratechartDetailsEnt);
            }
        }


    }

    public ArrayList<RatechartDetailsEnt> getAllRateChart() {
        ArrayList<RatechartDetailsEnt> allRateChartDetailsEnt = null;
        RateChartNameDao rateChartNameDao = (RateChartNameDao) DaoFactory.getDao(CollectionConstants.RATECHART_NAME);
        try {
            allRateChartDetailsEnt = rateChartNameDao.findRateChartFromInputs(null, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return allRateChartDetailsEnt;
    }

    public synchronized void deleteRatechart(RatechartDetailsEnt rateChartDetailsEnt) {

        try {
            long rowAffected =
                    rateChartNameDao.deleteByKey(RateChartNameTable.NAME, rateChartDetailsEnt.rateChartName);
            if (rowAffected > 0) {
                Util.writeRatechartLogs(rateChartDetailsEnt, mContext, Util.DELETE, Util.DEVICE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        DatabaseHandler dbh = DatabaseHandler.getDatabaseInstance();

        try {
            long deleted_row;
            if (rateChartDetailsEnt.ratechartType.contains("PROTEIN")) {
                deleted_row = incentiveRateDao.deleteByKey(DatabaseEntity.InCentive.RATE_CHART_NAME,
                        rateChartDetailsEnt.rateChartName);

            } else {
                long refId = rateChartNameDao.findRateRefIdFromName(rateChartDetailsEnt.rateChartName);
                deleted_row = rateDao.deleteByKey(RateTable.RATE_REF_ID, String.valueOf(refId));
            }
            if (deleted_row > 0) {
                displayToast(rateChartDetailsEnt.rateChartName + " "
                        + "RateChart Successfully deleted!", deleteRateChart);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * For the given collection type and milk type, save the latest active ratechart
     * and delete all other ratecharts
     *
     * @param milkType
     * @param rateChartType
     */
    private synchronized void setLatestRateChart(String milkType, String rateChartType) {
        boolean isRateChartSaved = false;
        ArrayList<RatechartDetailsEnt> allRateChartforMilkType = new ArrayList<RatechartDetailsEnt>();
        RateChartNameDao rateChartNameDao = (RateChartNameDao) DaoFactory.getDao(CollectionConstants.RATECHART_NAME);
        try {
            allRateChartforMilkType = rateChartNameDao.findLatestRateChartForGivenTime(milkType, rateChartType,
                    SmartCCUtil.getTodayTimeInMilli(), Util.getCurrentShift());
            for (RatechartDetailsEnt ratechartDetailsEnt : allRateChartforMilkType) {
                if (ratechartDetailsEnt.isActive.equalsIgnoreCase("true") && !isRateChartSaved) {
                    setRateChartForSession(milkType,
                            ratechartDetailsEnt.rateChartName, rateChartType);
                    isRateChartSaved = true;
                } else {
                    deleteRatechart(ratechartDetailsEnt);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!isRateChartSaved) {
            setRateChartForSession(milkType, null, rateChartType);
        }
    }

    /**
     * @param milkType
     * @param rateChart     Current ratechartName
     * @param rateChartType this can be collection, bonus,or sour
     */

    private synchronized void setRateChartForSession(String milkType, String rateChart, String rateChartType) {
        if (milkType.equalsIgnoreCase(CattleType.COW)) {
            if (rateChartType.equalsIgnoreCase(Util.RATECHART_TYPE_COLLECTION)) {
                amcuConfig.setRateChartForCow(rateChart);
            } else if (rateChartType.equalsIgnoreCase(Util.RATECHART_TYPE_BONUS)) {
                amcuConfig.setBonusRateChartCow(rateChart);
            } else if (rateChartType.equalsIgnoreCase(SmartCCConstants.RATECHART_TYPE_SOUR)) {
                amcuConfig.setSourRateChartCow(rateChart);
            } else if (rateChartType.equalsIgnoreCase(SmartCCConstants.RATECHART_TYPE_PROTEIN)) {
                amcuConfig.setIncentiveRateChartForCow(rateChart);
            } else if (rateChartType.equalsIgnoreCase(Util.RATECHART_TYPE_DYNAMIC)) {
                amcuConfig.setDynamicRateChartCow(rateChart);
            }

            if (rateChart == null && rateChartType.equalsIgnoreCase(Util.RATECHART_TYPE_COLLECTION)) {
                displayToast("No rate chart available for Cow",
                        addRateChartCow);
            } else {
                displayToast(rateChart
                                + " " + rateChartType + ", rateChart for Cow milk!",
                        addRateChartCow);
            }
        } else if (milkType.equalsIgnoreCase(CattleType.BUFFALO)) {

            if (rateChartType.equalsIgnoreCase(Util.RATECHART_TYPE_COLLECTION)) {
                amcuConfig.setRateChartForBuffalo(rateChart);
            } else if (rateChartType.equalsIgnoreCase(Util.RATECHART_TYPE_BONUS)) {
                amcuConfig.setBonusRateChartBuffalo(rateChart);
            } else if (rateChartType.equalsIgnoreCase(SmartCCConstants.RATECHART_TYPE_SOUR)) {
                amcuConfig.setSourRateChartBuffalo(rateChart);
            } else if (rateChartType.equalsIgnoreCase(SmartCCConstants.RATECHART_TYPE_PROTEIN)) {
                amcuConfig.setIncentiveRateChartForBuffalo(rateChart);
            } else if (rateChartType.equalsIgnoreCase(Util.RATECHART_TYPE_DYNAMIC)) {
                amcuConfig.setDynamicRateChartBuffalo(rateChart);
            }

            if (rateChart == null && rateChartType.equalsIgnoreCase(Util.RATECHART_TYPE_COLLECTION)) {
                displayToast("No rate chart available for Buffalo",
                        addRateChartBuffalo);
            } else {
                displayToast(rateChart
                                + " " + rateChartType + ", rateChart for Buffalo milk!",
                        addRateChartBuffalo);
            }

        } else if (milkType.equalsIgnoreCase(CattleType.MIXED)) {


            if (rateChartType.equalsIgnoreCase(Util.RATECHART_TYPE_COLLECTION)) {
                amcuConfig.setRateChartForMixed(rateChart);
            } else if (rateChartType.equalsIgnoreCase(Util.RATECHART_TYPE_BONUS)) {
                amcuConfig.setBonusRateChartMixed(rateChart);
            } else if (rateChartType.equalsIgnoreCase(SmartCCConstants.RATECHART_TYPE_SOUR)) {
                amcuConfig.setSourRateChartMixed(rateChart);
            } else if (rateChartType.equalsIgnoreCase(SmartCCConstants.RATECHART_TYPE_PROTEIN)) {
                amcuConfig.setRateIncentiveChartForMixed(rateChart);
            } else if (rateChartType.equalsIgnoreCase(Util.RATECHART_TYPE_DYNAMIC)) {
                amcuConfig.setDynamicRateChartMixed(rateChart);
            }

            if (rateChart == null && rateChartType.equalsIgnoreCase(Util.RATECHART_TYPE_COLLECTION)) {
                displayToast("No rate chart available for milk type Mixed",
                        addRateChartMix);
            } else {
                displayToast(rateChart
                                + " " + rateChartType + ", rateChart for Mixed milk!",
                        addRateChartMix);
            }

        }
    }

    public void displayToast(final String msg, int SERVER_DATA_RECEIVED) {

        int icon = R.drawable.icon_notification;
        CharSequence notiText = "smartAmcu Notification";
        long meow = System.currentTimeMillis();
        CharSequence contentTitle = "Rate Chart Notification";
        CharSequence contentText = msg;
        Intent notificationIntent = new Intent(mContext,
                SplashActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0,
                notificationIntent, 0);
        NotificationManager manager = (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
        Notification myNotication;
        Notification.Builder builder = new Notification.Builder(mContext.getApplicationContext());
        builder.setAutoCancel(false);
        builder.setTicker(notiText);
        builder.setContentTitle("Rate Chart Notification");
        builder.setContentText(contentText);
        builder.setSmallIcon(R.drawable.icon_notification);
        builder.setContentIntent(contentIntent);
        builder.setOngoing(true);
        builder.setSubText(msg);   //API level 16
        builder.build();

        myNotication = builder.getNotification();
        manager.notify(SERVER_DATA_RECEIVED, myNotication);

    }


    public synchronized void addRateChartDetails(RatechartDetailsEnt rde, String usbOrCloud) {

        RateChartNameDao rateChartNameDao = (RateChartNameDao) DaoFactory.getDao(CollectionConstants.RATECHART_NAME);
        Util.writeRatechartLogs(rde, mContext, Util.ADD, usbOrCloud);
        try {
            rateChartNameDao.saveOrUpdate(rde);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // Here adding only weight collection records
    public long addCollectionRecord(ReportEntity repEntity) {
        DatabaseHandler db = DatabaseHandler.getDatabaseInstance();
        long collRecIndex = -1;
        long colRecStatusIndex = -1;
        CollectionRecordDao collectionRecordDao = (CollectionRecordDao) DaoFactory.getDao(CollectionConstants.REPORT_TYPE_COLLECTION);
        try {
            repEntity.farmerId = repEntity.farmerId.toUpperCase(Locale.ENGLISH);
            collRecIndex = collectionRecordDao.saveOrUpdate(repEntity);
        } catch (Exception e) {

        } finally {
            ////DB close removed;
        }
        return colRecStatusIndex;
    }


    public void setNewRateChart() {

        setLatestRateChart(CattleType.COW, Util.RATECHART_TYPE_COLLECTION);
        setLatestRateChart(CattleType.BUFFALO, Util.RATECHART_TYPE_COLLECTION);
        setLatestRateChart(CattleType.MIXED, Util.RATECHART_TYPE_COLLECTION);

        setLatestRateChart(CattleType.COW, Util.RATECHART_TYPE_BONUS);
        setLatestRateChart(CattleType.BUFFALO, Util.RATECHART_TYPE_BONUS);
        setLatestRateChart(CattleType.MIXED, Util.RATECHART_TYPE_BONUS);

        setLatestRateChart(CattleType.COW, SmartCCConstants.RATECHART_TYPE_SOUR);
        setLatestRateChart(CattleType.BUFFALO, SmartCCConstants.RATECHART_TYPE_SOUR);
        setLatestRateChart(CattleType.MIXED, SmartCCConstants.RATECHART_TYPE_SOUR);

        setLatestRateChart(CattleType.COW, SmartCCConstants.RATECHART_TYPE_PROTEIN);
        setLatestRateChart(CattleType.BUFFALO, SmartCCConstants.RATECHART_TYPE_PROTEIN);
        setLatestRateChart(CattleType.MIXED, SmartCCConstants.RATECHART_TYPE_PROTEIN);

        setLatestRateChart(CattleType.COW, Util.RATECHART_TYPE_DYNAMIC);
        setLatestRateChart(CattleType.BUFFALO, Util.RATECHART_TYPE_DYNAMIC);
        setLatestRateChart(CattleType.MIXED, Util.RATECHART_TYPE_DYNAMIC);

    }


    public ArrayList<SampleDataEntity> getAllSampleEntity() {
        ArrayList<SampleDataEntity> allSampleEntity = new ArrayList<SampleDataEntity>();
        DatabaseHandler dbh = DatabaseHandler.getDatabaseInstance();

        try {
            allSampleEntity = dbh.getSampleData();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //Removed database close;
        }

        return allSampleEntity;
    }


    public void updateEarlierSampleList(ArrayList<SampleDataEntity> allSampleEnt, int paddingLength) {
        ArrayList<SampleDataEntity> allSampleList = new ArrayList<SampleDataEntity>();

        for (int i = 0; i < allSampleEnt.size(); i++) {
            SampleDataEntity sampleEnt = new SampleDataEntity();

            sampleEnt.sampleBarcode = allSampleEnt.get(i).sampleBarcode;
            sampleEnt.sampleId = Util.paddingFarmerId(allSampleEnt.get(i).sampleId, paddingLength);
            sampleEnt.sampleSocId = allSampleEnt.get(i).sampleSocId;
            sampleEnt.sampleAmount = allSampleEnt.get(i).sampleAmount;
            sampleEnt.sampleFat = allSampleEnt.get(i).sampleFat;
            sampleEnt.sampleMode = allSampleEnt.get(i).sampleMode;
            sampleEnt.sampleIsFat = allSampleEnt.get(i).sampleIsFat;
            sampleEnt.sampleIsSnf = allSampleEnt.get(i).sampleIsSnf;
            sampleEnt.sampleIsWeigh = allSampleEnt.get(i).sampleIsWeigh;

            allSampleList.add(sampleEnt);

        }

        DatabaseHandler dbh = DatabaseHandler.getDatabaseInstance();

        try {
            dbh.insertSampleFromExcel(allSampleList);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //Removed database close;
        }
    }

    public CenterEntity getCenterDetails(String centerId, int checkCode) {
        DatabaseHandler dbh = DatabaseHandler.getDatabaseInstance();

        CenterEntity centerEntity = null;

        try {
            centerEntity = dbh.getCenterEntity(centerId, checkCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return centerEntity;
    }

    public String getRateForGivenParams(double fat, double snf, double clr, String rateChartName) {
        if (amcuConfig.getMyRateChartEnable()) {
            return String.valueOf(CustomRateChartUtil.getInstance(mContext).getRateFromMyRateChart(fat, snf, mContext));
        } else {
            String rate = "0.00";
            if (clr == 0) {
                clr = Util.getCLR(fat, snf);
            }
            RateChartNameDao rateChartNameDao = (RateChartNameDao) DaoFactory.getDao(CollectionConstants.RATECHART_NAME);
            long refId = rateChartNameDao.findRateRefIdFromName(rateChartName);
            RateDao rateDao = (RateDao) DaoFactory.getDao(CollectionConstants.RATES);

            //In Ratechart SNF we are treating as CLR
            if (amcuConfig.getRateChartType().equalsIgnoreCase("FATCLR")) {
                rate = rateDao.findRateFromInput(String.valueOf(fat), String.valueOf(clr), null, refId);
            } else if (amcuConfig.getRateChartType().equalsIgnoreCase("SNFCLR")) {
                rate = rateDao.findRateFromInput(null, String.valueOf(snf), String.valueOf(clr), refId);
            } else {
                rate = rateDao.findRateFromInput(String.valueOf(fat), String.valueOf(snf), null, refId);
            }

            return rate;
        }
    }

    public ArrayList<ReportEntity> getIncompleteRecordsForMCC(String centerId, String shift, String sDate) {
        DatabaseHandler dbh = DatabaseHandler.getDatabaseInstance();

        Cursor cursor = dbh.getUserMilkColectionWeingScale(centerId, shift, sDate);
        ArrayList<ReportEntity> reportEntities = new ArrayList<>();


        if (cursor != null && cursor.moveToFirst()) {
            do {
                ReportEntity reportEntity = SmartCCUtil.getReportFromCursor(cursor);
                reportEntity.setIsSelected(true);
                reportEntities.add(reportEntity);
            } while (cursor.moveToNext());
        }


        return reportEntities;

    }

    public String getRouteFromChillingCenterTable(String centerId) {
        DatabaseHandler dbh = DatabaseHandler.getDatabaseInstance();

        return dbh.getRouteFromChillingCenterTable(centerId);

    }

    public void deleteSelectedColumnIds(String columnId) {
        String[] columnIds = columnId.split("=");
        DatabaseHandler dbh = DatabaseHandler.getDatabaseInstance();
        dbh.deleteUncommittedWSData(columnIds);
    }


    public PostEndShift getPostEndShift(ReportEntity repEnt) {

        PostEndShift poEndShift = new PostEndShift();

        // Added all manual entries for post data
        poEndShift.collectionDate = repEnt.postDate;
        poEndShift.shift = repEnt.postShift.toUpperCase(Locale.ENGLISH);
        poEndShift.startTime = Calendar.getInstance().getTimeInMillis();
        poEndShift.endTime = Calendar.getInstance().getTimeInMillis();
        poEndShift.endShift = false;
        poEndShift.societyId = session.getCollectionID();
        return poEndShift;
    }

    public void addCompleteRateChart(RatechartDetailsEnt rateChartDetailsEnt
            , ArrayList<RateChartEntity> allRateChartEnt, boolean isCloud) {

        RateChartPostEntity rateChartPostEntity = new RateChartPostEntity();
        rateChartPostEntity.setRateChartEntity(rateChartDetailsEnt, allRateChartEnt);

        Util.WRITE_RATE_ADD_LOG = true;
        DatabaseHandler dbh = DatabaseHandler.getDatabaseInstance();
        //To update in primary database
        //dbh.addCompleteRateChart(rateChartDetailsEnt, allRateChartEnt, DatabaseHandler.isPrimary, usbOrCloud);
        RateChartManager rateChartManager = new RateChartManager(mContext);
        rateChartManager.saveAll((List<? extends Entity>) rateChartPostEntity, isCloud);
    }


    public ArrayList<RateChartEntity> getAllRateChartEnt(
            ArrayList<ReadExcel.RateEntityExcel> rateEnt, String milkType, String rateChartName) {
        RateChartNameDao rateChartNameDao = (RateChartNameDao) DaoFactory.getDao(CollectionConstants.RATECHART_NAME);

        long refId = rateChartNameDao.findRateRefIdFromName(rateChartName);
        if (rateEnt != null) {
            for (int i = 0; i < rateEnt.size(); i++) {
                RateChartEntity rce = new RateChartEntity();
                rce.fat = rateEnt.get(i).fat;
                rce.snf = rateEnt.get(i).snf;
                rce.rate = rateEnt.get(i).rate;
                rce.farmerId = "All farmers";
                rce.societyId = String.valueOf(session.getSocietyColumnId());
                rce.rateReferenceId = refId;
                rce.milkType = milkType;
                rce.isActive = true;
                allRatechartEntityForExcel.add(rce);
            }
        }
        return allRatechartEntityForExcel;
    }


    public String getDynamicRate(MilkAnalyserEntity maEntity, String rateChartName) {

        long refId = rateChartNameDao.findRateRefIdFromName(rateChartName);
        return rateDao.findRateFromInput(String.valueOf(maEntity.fat)
                , String.valueOf(maEntity.snf), null, refId);
    }


}