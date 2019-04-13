package com.devapp.devmain.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by u_pendra on 18/3/17.
 */

public class TimeChangeReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

//        long sysTime = System.currentTimeMillis();
//        long calTime = Calendar.getInstance().getTimeInMillis();
//
//        Date date = new Date(sysTime);
//        String sysDate = DateFormat.getDateInstance().format(date);
//        String calDate = DateFormat.getDateInstance().format(new Date(calTime));
//        Toast.makeText(context,"Time changed, Sys date "+sysDate
//                +" cal date "+calDate,Toast.LENGTH_SHORT).show();
//
//        if(Util.isNetworkAvailable(context.getApplicationContext()))
//        {
//            Util.restartApp(context.getApplicationContext());
//        }
//        else
//        {
////            Intent mStartActivity = new Intent(context.getApplicationContext(), TimeAlertActivity.class);
////
////            mStartActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
////            PendingIntent mPendingIntent = PendingIntent.getActivity(context.getApplicationContext(),
////                    0, mStartActivity,
////                    PendingIntent.FLAG_CANCEL_CURRENT);
////            AlarmManager mgr = (AlarmManager) context.getApplicationContext()
////                    .getSystemService(Context.ALARM_SERVICE);
////            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100,
////                    mPendingIntent);
////            System.exit(0);
//
//            Toast.makeText(context,"Please check the network!",Toast.LENGTH_SHORT).show();
//        }

    }
}
