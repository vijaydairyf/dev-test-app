package com.devapp.devmain.services;

import android.app.IntentService;
import android.content.Intent;


/**
 * Created by yy on 29/5/15.
 */
public class PurgeData extends IntentService {

    private static final String TAG = "PurgeData Intent";
    //60 shift change to 120
    private int SHIFTS_PRIOR_TO_CURRENT = 120;
    private int PURGE_DEVICE_DATA_PRIOR = -10;
    private int PURGE_CLEANING_DATA_PRIOR = -30;

    public PurgeData() {
        super("PurgeData");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
       /* Log.d(TAG, "Purging Triggered by End Shift");
        SHIFTS_PRIOR_TO_CURRENT = AmcuConfig.getInstance().getDeleteCollRecordAfterShift();
        DatabaseHandler db = DatabaseHandler.getDatabaseInstance();
        CollectionRecordDao collectionRecordDao = (CollectionRecordDao) DaoFactory.getDao(CollectionConstants.REPORT_TYPE_COLLECTION);
        try {
            collectionRecordDao.deleteByDays(SHIFTS_PRIOR_TO_CURRENT / 2);
        } catch (Exception e) {
            e.printStackTrace();
        }

        SmartCCUtil smartCCUtil = new SmartCCUtil(getApplicationContext());
        long longTime = smartCCUtil.getDateInMillis(PURGE_DEVICE_DATA_PRIOR, false);

        try {
            db.purgeDeviceInfoPiorTheTime(longTime, DatabaseHandler.isPrimary);
            db.purgeDeviceInfoPiorTheTime(longTime, DatabaseHandler.isSecondary);
        } catch (Exception e) {
            e.printStackTrace();
        }
        long time = smartCCUtil.getDateInMillis(PURGE_CLEANING_DATA_PRIOR, false);
        try {
            db.purgeCleaningDataPriorTheTime(time, DatabaseHandler.isPrimary);
            db.purgeCleaningDataPriorTheTime(time, DatabaseHandler.isSecondary);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }
}