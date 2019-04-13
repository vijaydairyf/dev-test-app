package com.devapp.devmain.httptasks;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by xxx on 10/5/15.
 */
public class TaskScheduler {

    public static final String TASK_OBJECT = "TASK";


    private Context context;
    private Class<? extends BroadcastReceiver> alarmReceiver;


    public TaskScheduler(Context context) {
        this.context = context;
        this.alarmReceiver = (Class<? extends BroadcastReceiver>) AlarmReceiver.class;
    }

    public TaskScheduler(Context context, Class<? extends BroadcastReceiver> cls) {
        this.context = context;
        this.alarmReceiver = cls;
        //Log.d("Task Scheduler", "Inside Constructor");
    }


    public PendingIntent scheduleTask(Task task) {

        if (this.context == null) return null;

        // Construct an intent that will execute the AlarmReceiver
        Intent intent = new Intent(this.context, alarmReceiver);
//        intent.putExtra(TASK_OBJECT, task);

        Bundle bundle = new Bundle();
        bundle.putSerializable(TASK_OBJECT, task);
        intent.putExtras(bundle);


        // Create a PendingIntent to be triggered when the alarm goes off
        final PendingIntent pIntent = PendingIntent.getBroadcast(context, task.getCode(),
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        long startTime = System.currentTimeMillis() + task.getDelay();
        if (task.getType() == BackgroundTasksMgr.TIMER_ONE_SHOT) {
            alarm.set(AlarmManager.RTC_WAKEUP, startTime, pIntent);
        } else if (task.getType() == BackgroundTasksMgr.TIMER_PERIODIC) {
            long interval = task.getInterval();
            alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, startTime, interval, pIntent);
        }

        return pIntent;
    }

    public void cancelTask(PendingIntent pendingIntent) {

        if (this.context != null) {
            AlarmManager alarmManager = (AlarmManager) this.context
                    .getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);
        }
    }


    // Android is not allowing the instantiation of non-static through Manifest file
    public static class AlarmReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {


            try {
                Bundle bundle = intent.getExtras();
                Task task = (Task) bundle.getSerializable(TaskScheduler.TASK_OBJECT);
                Intent i = new Intent(context, task.getService());
                context.startService(i);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
