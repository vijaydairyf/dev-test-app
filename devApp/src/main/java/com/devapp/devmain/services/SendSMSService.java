package com.devapp.devmain.services;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.telephony.SmsManager;

import com.devapp.devmain.dao.DaoFactory;
import com.devapp.devmain.dao.FarmerDao;
import com.devapp.devmain.entity.CollectionEntry;
import com.devapp.devmain.entity.FarmerEntity;
import com.devapp.devmain.entity.PostEndShift;
import com.devapp.devmain.entity.ReportEntity;
import com.devapp.devmain.httptasks.SMSHandler;
import com.devapp.devmain.postentities.CollectionConstants;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.user.Util;

import java.util.ArrayList;

import static com.devapp.devmain.server.AmcuConfig.databaseHandler;

/**
 * Created by Upendra on 8/19/2015.
 */
public class SendSMSService extends IntentService {

    public final static String SEND_SMS_TASK = "SEND_SMS_TASK";
    private static final String EXTRA_NAME = "name";
    private static final String EXTRA_NUMBER = "number";
    private static final String[] names = null;
    private static final String[] numbers = null;
    public final String TAG = "SendSMSService";
    public int count = 0;
    String SENT = "SMS_SENT";
    String DELIVERED = "SMS_DELIVERED";
    SmsManager smsMgr;

    public SendSMSService() {
        super(SEND_SMS_TASK);
    }

    public static String getTaskName() {
        return SEND_SMS_TASK;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ArrayList<ReportEntity> reportEntities = SMSHandler.getInstance(getApplicationContext()).getNWUnsentSMS();
        FarmerDao farmerDao = (FarmerDao) DaoFactory.getDao(CollectionConstants.FARMER);
        try {
            smsMgr = SmsManager.getDefault();
            count = 0;
            outerloop:
            for (int size = 0; size < reportEntities.size(); size++) {

                final ReportEntity reportEntity = reportEntities.get(size);

                String mobNumber = null;
                FarmerEntity farmEnt = null;
                try {
                    farmEnt = (FarmerEntity) farmerDao.findById(reportEntity.farmerId);
                    if (farmEnt != null && farmEnt.farm_mob != null && farmEnt.farm_mob.length() > 9) {
                        mobNumber = farmEnt.farm_mob;
                    } else {
                        mobNumber = databaseHandler.getCenterMobileNumber(reportEntity.farmerId);
                        if (mobNumber != null && mobNumber.length() < 10) {
                            mobNumber = null;
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //in a 2 mins application will try to send sms
                // if count is less than 30 to minimize the generic failure
                if (count < 30) {
                    count++;
                    if (mobNumber != null) {
                        String smsText = Util.createSMSFromReport(
                                reportEntity, farmEnt.farmer_name, getApplicationContext());
                        sendSMSViaBroadCast(mobNumber, smsText, reportEntity.columnId);
                    } else {
                        //mobile number not valid
                        SMSHandler.getInstance(getApplicationContext()).setSMSStatus(
                                DatabaseHandler.COL_REC_SMS_INVALID_NUMBER, (int) reportEntity.columnId);
                    }
                } else {
                    break outerloop;

                }
            }
        } catch (Exception e) {

            e.printStackTrace();
        }

    }

    //Sending sms without broadcast messaged

    public void sendSms(String phoneNumber, String message, final PostEndShift endShiftData, int pos, int sequenceNumber) {

        ArrayList<CollectionEntry> collectionentryList = new ArrayList<CollectionEntry>();
        collectionentryList.add(endShiftData.farmerCollectionEntryList.get(pos));
        endShiftData.farmerCollectionEntryList = collectionentryList;
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            //setting SMS send status to send
            SMSHandler.getInstance(getApplicationContext()).setSMSStatus(DatabaseHandler.COL_REC_SMS_SENT, sequenceNumber);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void sendSMSViaBroadCast(final String phoneNumber, final String message, long smsSeqNum) {

        try {
            Intent intentSent = new Intent(SENT);
            intentSent.putExtra("seqNum", (int) smsSeqNum);
            PendingIntent sentPI = PendingIntent.getBroadcast(getApplicationContext(), (int) smsSeqNum,
                    intentSent, PendingIntent.FLAG_ONE_SHOT);
            PendingIntent deliveredPI = PendingIntent.getBroadcast(getApplicationContext(), (int) smsSeqNum,
                    new Intent(DELIVERED), PendingIntent.FLAG_ONE_SHOT);
            // ---when the SMS has been sent---
            smsMgr.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
            // smsMgr.sendMultipartTextMessage(phoneNumber, null, null, sentPI, deliveredPI);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
