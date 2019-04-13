package com.devapp.devmain.services;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import com.devapp.devmain.user.Util;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Upendra on 9/15/2015.
 */
public class DeleteSentSMS extends IntentService {
    public DeleteSentSMS() {
        super("DELETESENTSMS");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (Util.checkForRootedTab()) {
            deleteSMS();
        }

    }

    public void deleteSMS() {

        ArrayList<Long> arrayListId = new ArrayList<Long>();

        try {

            Uri uriSms = Uri.parse("content://sms/sent");
            Cursor c = getApplicationContext().getContentResolver().query(uriSms,
                    null, null, null, null);
            StringBuilder strBuilder = new StringBuilder();

            if (c != null && c.moveToFirst()) {
                do {
                    long id = c.getLong(0);
                    long threadId = c.getLong(1);
                    String address = c.getString(2);
                    String body = c.getString(5);

                    strBuilder.append(c.getString(c.getColumnIndexOrThrow("_id")) + "\n");
                    strBuilder.append(c.getString(c
                            .getColumnIndexOrThrow("address")) + "\n");
                    strBuilder.append(c.getString(c.getColumnIndexOrThrow("body")) + "\n");
                    strBuilder.append(c.getString(c.getColumnIndex("read")) + "\n");
                    strBuilder.append(c.getString(c.getColumnIndexOrThrow("date")) + "\n");


                    if (Util.readyToDelete(Long.parseLong(c.getString(c.getColumnIndexOrThrow("date"))))) {
                        arrayListId.add(id);
                    }
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (arrayListId.size() > 0) {
            deleteSentSms(arrayListId);
        }


    }

    public void deleteSentSms(ArrayList<Long> arrayIdList) {
        ArrayList<String> cmds = new ArrayList<String>();

        cmds.add("cd data/data/com.android.providers.telephony/databases");
        cmds.add("sqlite3 mmssms.db");

        for (Long id : arrayIdList) {
            cmds.add("DELETE FROM sms WHERE _id = " + id + ";");
        }
        Process process = null;

        try {
            process = Runtime.getRuntime().exec("su");
            DataOutputStream dataos = new DataOutputStream(process.getOutputStream());
            for (String cmd : cmds) {
                dataos.writeBytes(cmd + "\n");
            }
            dataos.writeBytes("exit\n");
            dataos.flush();
            dataos.close();
            int i = process.waitFor();

            String is = i + "";
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {

        } finally {
            {
                process.destroy();
            }
        }

    }

}