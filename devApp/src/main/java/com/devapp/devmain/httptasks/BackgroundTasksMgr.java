package com.devapp.devmain.httptasks;

import android.app.ActivityManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.util.Log;

import com.devapp.devmain.deviceinfo.PostDeviceStatusService;
import com.devapp.devmain.milkline.service.PostTankerRecords;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.services.NetworkCheck;
import com.devapp.devmain.services.SendSMSService;
import com.devapp.devmain.services.TabShutDownService;
import com.devapp.devmain.user.Util;
import com.devapp.smartcc.service.PostTruckRecords;

import java.util.HashMap;

/**
 * Created by xxx on 10/5/15.
 */
public class BackgroundTasksMgr {

    static final int TIMER_ONE_SHOT = 1;
    static final int TIMER_PERIODIC = 2;

    private static final String TAG = "BACKGROUND_TASK_MGR";
    // Post Collection Records Intent Service has nothing to do with how it can be scheduled
    // This class can be the place for these config. params till a suitable place is decideds
    private static final int POST_COLLECTION_RECORDS_TASK_CODE = 222201; // 4 digit (some) app code and last two digits for unique task id
    private static final int RATE_CHART_PULL_TASK_CODE = 222202;

    //ShutDownTaskCode
    private static final int TAB_SHUT_DOWN_TASK_CODE = 222203;

    //Sending sms task
    private static final int SEND_SMS_TASK_CODE = 222204;


    //this should be 17 min
    private static final long RATE_CHART_PULL_SCHEDULE_INTERVAL = 13 * 60 * 1000; // millisecond.
    //this should be 3 mins
    private static final long RATE_CHART_PULL_SCHEDULE_DELAY = 3 * 60 * 1000;
    private static final long SEND_SMS_TASK_INTERVAL = 2 * 60 * 1000; // millisecond.
    private static final long SEND_SMS_TASK_SCHEDULE_DELAY = 0;

    // for Update old record
    private static final int POST_UPDATED_TRUCK_TASK_CODE = 222208;
    private static final long POST_UPDATED_TRUCK_RECORDS_SCHEDULE_DELAY = 5 * 60 * 1000;

    //For update truck records
    private static final long POST_UPDATED_TRUCK_RECORDS_SCHEDULE_INTERVAL = 10 * 60 * 1000;
    private static final int POST_UPDATED_TANKER_TASK_CODE = 222209;
    private static final long POST_UPDATED_TANKER_RECORDS_SCHEDULE_DELAY = 5 * 60 * 1000;

    //this is for to sent tanker records
    private static final long POST_UPDATED_TANKER_RECORDS_SCHEDULE_INTERVAL = 5 * 60 * 1000;
    private static final int PERIODIC_NETWORK_CHECK = 222210;
    private static final long PERIODIC_NETWORK_CHECK_SCHEDULE_DELAY = 2 * 60 * 1000;


    //periodically mobile data network check
    private static final long PERIODIC_NETWORK_CHECK_SCHEDULE_INTERVAL = 4 * 60 * 1000;
    private static final int SEND_DEVICE_STATUS_TASK_CODE = 222211;
    private static final long SEND_DEVICE_STATUS_SCHEDULE_DELAY = 1 * 60 * 1000;
    private static BackgroundTasksMgr instance = null;
    private static long SEND_DEVICE_STATUS_SCHEDULE_INTERVAL = 2 * 60 * 1000;
    AmcuConfig amcuConfig;
    private long POST_COLLECTION_RECORDS_SCHEDULE_INTERVAL = 3 * 60 * 1000; // millisecond.
    //it should be 5
    private long POST_COLLECTION_RECORDS_SCHEDULE_DELAY = 5 * 60 * 1000;
    private HashMap<String, Task> tasks = new HashMap<String, Task>();
    private HashMap<String, PendingIntent> pendingTaskIndents = new HashMap<String, PendingIntent>();
    private TaskScheduler scheduler;
    private Context context;

    /**
     * @param context
     */
    private BackgroundTasksMgr(Context context) {
        //   Log.d("Background Task Manager", "Inside Constructor");
        scheduler = new TaskScheduler(context);
        this.context = context;
        amcuConfig = AmcuConfig.getInstance();
        POST_COLLECTION_RECORDS_SCHEDULE_INTERVAL = amcuConfig.getKeyPeriodicDataSent() * 60 * 1000;
        POST_COLLECTION_RECORDS_SCHEDULE_DELAY = amcuConfig.getKeyDataSentStartTime() * 60 * 1000;
        SEND_DEVICE_STATUS_SCHEDULE_INTERVAL = amcuConfig.getPeriodicDeviceDataSend() * 60 * 1000;

        registerTasks();
        scheduleTasks();
    }

    /**
     * @param context
     * @return
     */
    public static synchronized BackgroundTasksMgr getInstance(Context context) {
        if (instance == null || Util.isShutDown) {
            instance = new BackgroundTasksMgr(context);
        }

        return instance;
    }


    /**
     *
     */
    private void registerTasks() {

        // introduce more tasks to be scheduled as per the requirement of the application
        // each task can be scheduled one time or periodically

        tasks.put(PostCollectionRecordsService.getTaskName(),
                new Task(PostCollectionRecordsService.getTaskName(),
                        POST_COLLECTION_RECORDS_TASK_CODE,
                        TIMER_PERIODIC, POST_COLLECTION_RECORDS_SCHEDULE_DELAY,
                        POST_COLLECTION_RECORDS_SCHEDULE_INTERVAL,
                        (Class<? extends IntentService>) PostCollectionRecordsService.class));

        //task to send rateChartPull
        tasks.put(RateChartPullService.getTaskName(),
                new Task(RateChartPullService.getTaskName(), RATE_CHART_PULL_TASK_CODE,
                        TIMER_PERIODIC, RATE_CHART_PULL_SCHEDULE_DELAY, RATE_CHART_PULL_SCHEDULE_INTERVAL,
                        (Class<? extends IntentService>) RateChartPullService.class));

        //task to send sms
        tasks.put(SendSMSService.getTaskName(), new Task(SendSMSService.getTaskName(), SEND_SMS_TASK_CODE,
                TIMER_PERIODIC, SEND_SMS_TASK_SCHEDULE_DELAY, SEND_SMS_TASK_INTERVAL, (Class<? extends IntentService>)
                SendSMSService.class));


        //To sent tanker records periodically
        tasks.put(PostTankerRecords.getTaskName(), new Task(PostTankerRecords.getTaskName(), POST_UPDATED_TANKER_TASK_CODE,
                TIMER_PERIODIC, POST_UPDATED_TANKER_RECORDS_SCHEDULE_DELAY, POST_UPDATED_TANKER_RECORDS_SCHEDULE_INTERVAL
                , (Class<? extends IntentService>)
                PostTankerRecords.class));

        //Periodic Network check
        tasks.put(NetworkCheck.getTaskName(), new Task(NetworkCheck.getTaskName(), PERIODIC_NETWORK_CHECK,
                TIMER_PERIODIC, PERIODIC_NETWORK_CHECK_SCHEDULE_DELAY, PERIODIC_NETWORK_CHECK_SCHEDULE_INTERVAL
                , (Class<? extends IntentService>)
                NetworkCheck.class));

        //TO Send device status
        tasks.put(PostDeviceStatusService.getTaskName(), new Task(PostDeviceStatusService.getTaskName(), SEND_DEVICE_STATUS_TASK_CODE,
                TIMER_PERIODIC, SEND_DEVICE_STATUS_SCHEDULE_DELAY, SEND_DEVICE_STATUS_SCHEDULE_INTERVAL, (Class<? extends IntentService>)
                PostDeviceStatusService.class));

        if (amcuConfig.getEnableCenterCollection()) {

            tasks.put(PostTruckRecords.getTaskName(), new Task(PostTruckRecords.getTaskName(), POST_UPDATED_TRUCK_TASK_CODE,
                    TIMER_PERIODIC, POST_UPDATED_TRUCK_RECORDS_SCHEDULE_DELAY, POST_UPDATED_TRUCK_RECORDS_SCHEDULE_INTERVAL
                    , (Class<? extends IntentService>)
                    PostTruckRecords.class));
        }

        if (Util.isShutDown) {
            tasks.put(TabShutDownService.getTaskName(),
                    new Task(TabShutDownService.getTaskName(), TAB_SHUT_DOWN_TASK_CODE,
                            TIMER_ONE_SHOT, AmcuConfig.getInstance().getShutDownDelay(), 0,
                            (Class<? extends IntentService>) TabShutDownService.class));
        }


    }

    /**
     *
     */
    private void scheduleTasks() {

        for (HashMap.Entry<String, Task> entry : tasks.entrySet()) {

            // checking if the task is already running before scheduling it
            // this may not be necessary as tasks are created on app start

            if (!isTaskRunning(entry.getValue())) {
                PendingIntent intent = scheduler.scheduleTask(entry.getValue());
                if (intent != null) {
                    pendingTaskIndents.put(entry.getKey(), intent);
                }
            }
        }
    }

    /**
     * @param name
     * @return
     */
    public Task getTask(String name) {
        // Task name is the key
        return tasks.get(name);
    }


    /**
     * @param key
     * @return
     */
    public PendingIntent getPendingTaskIntent(String key) {
        return pendingTaskIndents.get(key);
    }

    /**
     * @param name
     */
    public void cancelTask(String name) {
        PendingIntent pendingIntent = pendingTaskIndents.get(name);
        if (pendingIntent != null) {
            scheduler.cancelTask(pendingIntent);
            pendingTaskIndents.remove(name);
        }
    }

    /**
     * @param task
     * @return
     */
    public boolean isTaskRunning(Task task) {

        if (this.context == null) return false;

        ActivityManager manager = (ActivityManager) this.context.getSystemService
                (Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service :
                manager.getRunningServices(Integer.MAX_VALUE)) {

            //  Log.d(TAG,"Task Name:" + task.getService().getName());
            //  Log.d(TAG, "Service Name:" + service.service.getClassName());

            if (task.getService().getName().equals(service.service.getClassName())) {
                Log.d(TAG, "Task:" + task.getCode() + " is running");
                return true;
            }
        }

        // Log.d(TAG, "Task:" + task.getCode() + " is not running");
        return false;
    }
}
