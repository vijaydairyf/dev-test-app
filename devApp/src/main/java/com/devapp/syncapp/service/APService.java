package com.devapp.syncapp.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.devapp.devmain.ConsolidationPost.ConsolidatedRecordsSynchronizer;
import com.devapp.devmain.encryption.ConsolidatedData;
import com.devapp.devmain.encryption.Csv;
import com.devapp.devmain.helper.ReportHelper;
import com.devapp.devmain.postentities.CollectionConstants;
import com.devapp.devmain.postentities.ConsolidatedPostData;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.syncapp.EntityCreator;
import com.devapp.syncapp.SyncAppConstants;
import com.devapp.syncapp.json.receive.SyncAppIdentity;
import com.devapp.syncapp.json.send.AmcuIdentity;
import com.devapp.syncapp.json.send.UnSentRecordDetails;
import com.devApp.R;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class APService extends Service {
    private static final String TAG = "APService";

    private Context context;
    private ServerSocket serverSocket;
    private Socket client;
    private ObjectMapper mapper;
    private DatabaseHandler dbh;
    private InputStream input;
    private OutputStream output;
    private PrintWriter writer;
    private BufferedReader reader;
    private ConsolidatedRecordsSynchronizer consolidatedRecordsSynchronizer;
    private String imeiNumber;
    private SmartLiteTask smartLiteTask;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Perform();
        return START_REDELIVER_INTENT;
    }

    private void Perform() {
        if (smartLiteTask == null || smartLiteTask.getStatus() == AsyncTask.Status.FINISHED) {
            smartLiteTask = new SmartLiteTask();
            smartLiteTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
        }
    }

    private void ListenIncomingRequest() {
        try {
            this.context = this;
            this.mapper = new ObjectMapper();
            this.dbh = DatabaseHandler.getDatabaseInstance();
            this.consolidatedRecordsSynchronizer = ConsolidatedRecordsSynchronizer.getInstance(context.getApplicationContext());
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            this.imeiNumber = telephonyManager.getDeviceId() != null ? telephonyManager.getDeviceId() : AmcuConfig.getInstance().getDeviceID();

            serverSocket = new ServerSocket();
            serverSocket.setReuseAddress(true);
            serverSocket.bind(new InetSocketAddress(context.getResources().getInteger(R.integer.ap_port)));
            while (true) {
                try {
                    Log.i(TAG, " is Waiting...");
                    client = serverSocket.accept();
                    input = client.getInputStream();
                    output = client.getOutputStream();
                    writer = new PrintWriter(output, true);
                    reader = new BufferedReader(new InputStreamReader(input));

                    // 1. Validate SmartLite app User
                    SyncAppIdentity identity = mapper.readValue(reader.readLine(), SyncAppIdentity.class);
                    if (isValidSyncAppUser(identity)) {

                        // Share Center Details
                        String[] societyIdAndName = dbh.getSocietyIdAndName();
                        AmcuIdentity details = new AmcuIdentity();
                        details.setSocietyCode(societyIdAndName[0]);
                        details.setSocietyName(societyIdAndName[1]);
                        details.setImei(this.imeiNumber);

                        writer.println(mapper.writeValueAsString(details));

                        switch (Integer.parseInt(reader.readLine())) {
                            case SyncAppConstants.TRANSMISSION_FOR.COLLECTION_UPDATE:
                                Log.i(TAG, "COLLECTION_UPDATE EXECUTING");
                                readSentRecords();
                                writeUnsentRecords();
                                break;
                            case SyncAppConstants.TRANSMISSION_FOR.CONFIGURATION_UPDATE:
                                Log.i(TAG, "CONFIGURATION_UPDATE NOT YET IMPLEMENTED");
                                break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                closeClientSockets();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        closeServerSocket();
        ListenIncomingRequest();
    }


    private void readSentRecords() throws Exception {
        try {
            int totalCount = Integer.parseInt(reader.readLine());
            if (totalCount != 0) {
                long encDataLength = Long.parseLong(reader.readLine());
                String encData = reader.readLine();
                String decryptedJson = Csv.DecryptS(encData);
                //To acknowledge Sync App that it was received completely.
                boolean allBytesCame = encDataLength == encData.length();
                writer.println(String.valueOf(allBytesCame));
                if (allBytesCame) {
                    EntityCreator creator = new EntityCreator();
                    ConsolidatedData consolidatedData = creator.getEntityCreated(decryptedJson);

                    for (ConsolidatedPostData oneShiftRecords : consolidatedData.records) {
                        consolidatedRecordsSynchronizer.setUpdateStatus(oneShiftRecords, CollectionConstants.SENT);
                    }
                    Log.i(TAG, "All Updates received from syncApp.");
                } else {
                    Log.i(TAG, "Update was not applied because of some data missed during transmission from sync app.");
                }
            }
        } catch (Exception e) {
            throw e;
        }
    }

    private void writeUnsentRecords() throws Exception {
        try {
            ReportHelper reportHelper = new ReportHelper(context);
            UnSentRecordDetails unSentRecordDetails = reportHelper.createUnsentRecordsS();
            writer.println(mapper.writeValueAsString(unSentRecordDetails));
        } catch (IOException e) {
            throw e;
        }

    }

    private void closeServerSocket() {
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.i(TAG, "closeServerSocket: ");
        }
    }

    private void closeClientSockets() {
        try {
            if (input != null)
                input.close();
            if (output != null)
                output.close();
            if (client != null && !client.isClosed())
                client.close();
            Log.i(TAG, "closeClientSockets: ");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isValidSyncAppUser(SyncAppIdentity identity) {
        if (identity == null)
            return false;
        if (identity.getImei() == null)
            return false;
        if (identity.getName() == null)
            return false;

        return identity.getName().equals("@dmin");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: ");
    }

    private class SmartLiteTask extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {
            ListenIncomingRequest();
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            smartLiteTask = null;
            Perform();
        }
    }
}
