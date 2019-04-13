package com.devapp.devmain.services;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;

import com.devapp.devmain.httptasks.SMSHandler;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.user.Util;

/**
 * Created by Upendra on 9/1/2015.
 */
public class SendSMSBroadcast extends BroadcastReceiver {

    String SENT = "SMS_SENT";
    String DELIVERED = "SMS_DELIVERED";

    @Override
    public void onReceive(Context context, Intent intent) {

        if (SENT.equals(intent.getAction())) {
            int seqNumber = intent.getIntExtra("seqNum", -1);
            switch (getResultCode()) {
                case Activity.RESULT_OK:

                    SMSHandler.getInstance(context).setSMSStatus(
                            DatabaseHandler.COL_REC_SMS_SENT, seqNumber);
                    break;

                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    SMSHandler.getInstance(context).setSMSStatus(
                            DatabaseHandler.COL_REC_SMS_GENERIC_FAILURE, seqNumber);
                    break;

                case SmsManager.RESULT_ERROR_NO_SERVICE:
                    break;

                case SmsManager.RESULT_ERROR_NULL_PDU:
                    break;

                case SmsManager.RESULT_ERROR_RADIO_OFF:
                    Util.displayErrorToast("Enable the mobile network", context);
                    break;
                default:
                    break;
            }
        } else if (DELIVERED.equals(intent.getAction())) {
            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    break;
                case Activity.RESULT_CANCELED:
                    break;
            }
        }
    }

}

