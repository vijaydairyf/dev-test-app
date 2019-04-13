package com.devapp.smartcc.service;

import android.app.IntentService;
import android.content.Intent;

import com.devapp.devmain.server.AmcuConfig;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;


public class PostTruckRecords extends IntentService {

    private static final String POST_TRUCK_RECORDS_TASK = "POST_TRUCK_RECORDS_TASK";
    private static final String TRUCK_ENTRY_URI = "/amcu/trucks/entries/";
    private static final String TAG = "POST_TRUCK_RECORDS";
    /**
     * @param intent
     */
    AmcuConfig amcuConfig;

    public PostTruckRecords() {
        super(POST_TRUCK_RECORDS_TASK);
    }

    public static String getTaskName() {
        return POST_TRUCK_RECORDS_TASK;
    }

    @Override
    protected void onHandleIntent(Intent intent) {

       /* Log.d(TAG, "--------- Handling the Intent ---------");

        try {
            saveSession = SaveSession.getInstance(getApplicationContext());
            AuthenticationParameters authParams =
                    new AuthenticationParameters(null, saveSession.getDeviceID(),
                            saveSession.getDevicePassword());

            CommunicationService cs = null;
            try {
                cs = new CommunicationService(saveSession.getURLHeader() + saveSession.getServer(),
                        authParams);
            } catch (IOException e) {
                Log.d(TAG, e.getMessage());
            } catch (SSLContextCreationException e) {
                Log.d(TAG, e.getMessage());
            }


            ArrayList<TruckDetailsEntity> truckRecordsDetails =
                    CollectionRecordsHandler.getInstance(getApplicationContext()).getNWUnsentTruckRecords();
            //   Log.d(TAG, "PostCollectionRecordsService Received:" + shiftRecordsList.size() + " records for sending to network");


            try {
                String truckRcords = this.toJson(truckRecordsDetails);

                Log.d(TAG, "Post Truck Records - " + truckRcords);

                //   Util.generateNoteOnSD("truckReport", truckRcords, getApplicationContext(), "smartAmcuReports");

                HttpResponse response = cs.doPost(TRUCK_ENTRY_URI,
                        truckRcords, true);

                Log.d(TAG, "Truck details  - POST Response:" + response.getResponseCode());

                if (response.getResponseCode() == HttpURLConnection.HTTP_CREATED
                        || response.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    CollectionRecordsHandler.getInstance(getApplicationContext())
                            .updateUnSentTruckRecords(truckRecordsDetails);

                } else {

                }
            } catch (IOException ioe) {
                //  writeExceptionToFile(ioe);
            } catch (IncompatibleProtocolException ex) {
                // writeExceptionToFile(ex);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    /**
     * Takes PostEndShift object and converts it into a JSON in a string format
     *
     * @param obj
     * @return
     */
    private String toJson(Object obj) {


        String jsonString = null;
        ObjectMapper mapper = new ObjectMapper();
        try {
            jsonString = mapper.writeValueAsString(obj);

        } catch (JsonGenerationException ex) {
            ex.printStackTrace();
        } catch (JsonMappingException ex) {
            ex.printStackTrace();

        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return jsonString;
    }


}


