package com.devapp.devmain.Connectivity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;

/**
 * Created by Upendra on 5/9/2016.
 */
public class SMSVarification {

    private static final int defaultSeqNumber = -2;
    public static boolean smsTest = false;
    SmsManager smsMgr;
    String SENT = "SMS_SENT";
    String DELIVERED = "SMS_DELIVERED";
    private Context mContext;
    private String mPhoneNumber;
    private String mSmsBody;


    public SMSVarification(Context ctx, String mobNumber, String smsBody) {
        this.mContext = ctx;
        this.mPhoneNumber = mobNumber;
        this.mSmsBody = smsBody;
        smsMgr = SmsManager.getDefault();
    }

    public void sendSMSViaBroadCast(final String phoneNumber, final String message) {

        Intent intentSent = new Intent(SENT);
        intentSent.putExtra("seqNum", defaultSeqNumber);
        PendingIntent sentPI = PendingIntent.getBroadcast(mContext, defaultSeqNumber, intentSent, PendingIntent.FLAG_ONE_SHOT);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(mContext, defaultSeqNumber,
                new Intent(DELIVERED), PendingIntent.FLAG_ONE_SHOT);
        // ---when the SMS has been sent---
        smsMgr.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
    }

}
