package com.devapp.devmain.milkline.service;

/**
 * Created by u_pendra on 20/12/16.
 */

import android.app.IntentService;
import android.content.Intent;

import com.devapp.devmain.server.AmcuConfig;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;


public class PostTankerRecords extends IntentService {

    private static final String POST_TANKER_RECORDS_TASK = "POST_TANKER_RECORDS_TASK";
    private static final String TANKER_POST_URI = "/amcu/tanker/entries/";
    private static final String TAG = "POST_TANKER_RECORDS";
    /**
     * @param intent
     */
    AmcuConfig amcuConfig;

    public PostTankerRecords() {
        super(POST_TANKER_RECORDS_TASK);
    }

    public static String getTaskName() {
        return POST_TANKER_RECORDS_TASK;
    }

    @Override
    protected void onHandleIntent(Intent intent) {

      /*  try {
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

            ArrayList<TankerEntity> truckRecordsDetails =
                    CollectionRecordsHandler.getInstance(getApplicationContext()).getNWUnsentTankerRecords();
            try {
                String tankerRecords = this.toJson(truckRecordsDetails);
                // Log.d(TAG, "Post Tanker Records - " + tankerRecords);
                //    Util.generateNoteOnSD("tanker", tankerRecords, getApplicationContext(), "smartAmcuReports");
                HttpResponse response = cs.doPost(TANKER_POST_URI,
                        tankerRecords, true);
                // Log.d(TAG, "Tanker details  - POST Response:" + response.getResponseCode());
                if (response.getResponseCode() == HttpURLConnection.HTTP_CREATED
                        || response.getResponseCode() == HttpURLConnection.HTTP_OK) {

                    CollectionRecordsHandler.getInstance(getApplicationContext())
                            .updateUnSentTankerRecords(truckRecordsDetails);
                    if (truckRecordsDetails != null && truckRecordsDetails.size() > 0) {
                        Intent RTReturn = new Intent(AllTankerList.INTENT_CLASS);
                        RTReturn.putExtra("RESULT", "SUCCESS");
                        LocalBroadcastManager.getInstance(this).sendBroadcast(RTReturn);
                    }

                } else {

                }
            } catch (IOException ioe) {
                //  writeExceptionToFile(ioe);
            } catch (IncompatibleProtocolException ex) {
                // writeExceptionToFile(ex);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

*/
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



