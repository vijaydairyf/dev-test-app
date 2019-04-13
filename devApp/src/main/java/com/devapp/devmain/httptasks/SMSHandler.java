package com.devapp.devmain.httptasks;

import android.content.Context;

import com.devapp.devmain.dao.CollectionRecordDao;
import com.devapp.devmain.dao.DaoFactory;
import com.devapp.devmain.entity.ReportEntity;
import com.devapp.devmain.postentities.CollectionConstants;
import com.devapp.devmain.server.DatabaseHandler;

import java.util.ArrayList;

/**
 * Created by Upendra on 8/19/2015.
 */
public class SMSHandler {

    public final static boolean SMS_SENT = true;
    public final static boolean SMS_UNSENT = false;
    private static Context mContext;
    private static SMSHandler smsHandler;
    int unsentMessageCount;

    public static SMSHandler getInstance(Context ctx) {
        mContext = ctx;
        if (smsHandler == null) {
            smsHandler = new SMSHandler();
        }
        return smsHandler;
    }

    private SMSHandler SMSHandler() {

        return smsHandler;
    }

    public ArrayList<ReportEntity> getNWUnsentSMS() {
        DatabaseHandler dbHandler = DatabaseHandler.getDatabaseInstance();
        CollectionRecordDao collectionRecordDao = (CollectionRecordDao) DaoFactory.getDao(CollectionConstants.REPORT_TYPE_COLLECTION);
        ArrayList<ReportEntity> reportEntities = new ArrayList<>();
        reportEntities = collectionRecordDao.findByUnsentSms();
        return reportEntities;

    }


    public void setSMSStatus(int sentStatus, int sequenceNumber) {
        CollectionRecordDao collectionRecordDao = (CollectionRecordDao) DaoFactory.getDao(CollectionConstants.REPORT_TYPE_COLLECTION);
        collectionRecordDao.updateSmsStatus(sequenceNumber, sentStatus);

    }

}
