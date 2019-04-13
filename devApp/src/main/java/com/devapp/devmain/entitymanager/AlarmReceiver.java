package com.devapp.devmain.entitymanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.devapp.devmain.httptasks.Task;
import com.devapp.devmain.httptasks.TaskScheduler;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Task task = (Task) intent.getSerializableExtra(TaskScheduler.TASK_OBJECT);
        if (task != null && context != null) {
            Intent i = new Intent(context, task.getService());
            context.startService(i);
            //   Toast.makeText(context, "Invoking the service:" + task.getCode(), Toast.LENGTH_SHORT).show();
        }
    }
}
