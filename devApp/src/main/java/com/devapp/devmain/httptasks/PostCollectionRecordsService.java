package com.devapp.devmain.httptasks;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.devapp.devmain.AmcuException.Exception4XX;
import com.devapp.devmain.ConsolidationPost.AdditionalCollectionRecordSet;
import com.devapp.devmain.ConsolidationPost.ConsolidatedRecordsSynchronizer;
import com.devapp.devmain.ConsolidationPost.DispatchCollectionRecordSet;
import com.devapp.devmain.ConsolidationPost.EditFarmerCollectionRecordSet;
import com.devapp.devmain.ConsolidationPost.EditMCCCollectionRecordSet;
import com.devapp.devmain.ConsolidationPost.FarmerMilkCollectionRecordSet;
import com.devapp.devmain.ConsolidationPost.FarmerSplitCollectionRecordSet;
import com.devapp.devmain.ConsolidationPost.IncompleteMCCRecordSet;
import com.devapp.devmain.ConsolidationPost.MCCMilkCollectionRecordSet;
import com.devapp.devmain.ConsolidationPost.RecordSet;
import com.devapp.devmain.ConsolidationPost.RouteCollectionRecordSet;
import com.devapp.devmain.ConsolidationPost.SalesCollectionRecordSet;
import com.devapp.devmain.ConsolidationPost.SampleCollectionRecordSet;
import com.devapp.devmain.ConsolidationPost.TankerCollectionRecordSet;
import com.devapp.devmain.postentities.CollectionConstants;
import com.devapp.devmain.postentities.ConsolidatedDate;
import com.devapp.devmain.postentities.ConsolidatedId;
import com.devapp.devmain.postentities.ConsolidatedMetadata;
import com.devapp.devmain.postentities.ConsolidatedPostData;
import com.devapp.devmain.postentities.FarmerPostEntity;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.SessionManager;
import com.devapp.devmain.user.Util;
import com.devapp.smartcc.entityandconstants.SmartCCUtil;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by xxx on 10/5/15.
 */
public class PostCollectionRecordsService extends IntentService {

    public static final String END_SHIFT = "ENDSHIFT";

    public static final String FROM_PULL_RECORD = "fromPullRecord";
    public static final String RECORD_ENTITY = "recordEntity";
    private static final String POST_COLLECTION_RECORDS_TASK = "POST_COLLECTION_RECORDS_TASK"; // Task name
    private static final String COLLECTION_ENTRY_URI = "/amcu/v3/collectionentries/";
    private static final String SESSION_INVALID_URI = "/all/invalid_session";
    private static final String TAG = "POST_COLLECTION_RECORDS";
    Context mContext = this;
    boolean isPullSuccess, endShift;
    ConsolidatedRecordsSynchronizer consolidatedRecordsSynchronizer;
    /**
     * @param intent
     */
    AmcuConfig amcuConfig;
    private Map<String, RecordSet> mapRecordFactories;

    /**
     * This is an Intent Service to post collection records to server.
     * Uses the CollectionRecordsHandler to get the list of unsent records and posts them to server
     * After posting the records successfully, updates their status to SENT.
     * This service can be started one time are scheduled through TaskScheduler for sending periodically
     */
    public PostCollectionRecordsService() {
        super(POST_COLLECTION_RECORDS_TASK);
    }

    public static String getTaskName() {
        return POST_COLLECTION_RECORDS_TASK;
    }

    /**
     * Takes PostEndShift object and converts it into a JSON in a string format
     *
     * @param obj
     * @return
     */
    public static String toJson(Object obj) {


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

    //Handle 4XX error
    @Override
    protected void onHandleIntent(Intent intent) {

        consolidatedRecordsSynchronizer =
                ConsolidatedRecordsSynchronizer.getInstance(getApplicationContext());
        amcuConfig = AmcuConfig.getInstance();
        CommunicationService cs = getCommunicationService();
        try {
            endShift = intent.getBooleanExtra(END_SHIFT, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        Util.displayErrorToast("Inside collection Record service", mContext);
        boolean isSuccess = postData(cs);
        if (endShift && isSuccess) {
            amcuConfig.setEndShiftSuccess(true);
        }


    }

    private CommunicationService getCommunicationService() {
        AuthenticationParameters authParams =
                new AuthenticationParameters(null, amcuConfig.getDeviceID(),
                        amcuConfig.getDevicePassword());
        CommunicationService cs = null;
        try {
            cs = new CommunicationService(amcuConfig.getURLHeader() + amcuConfig.getServer(),
                    authParams);
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        } catch (SSLContextCreationException e) {
            Log.d(TAG, e.getMessage());
        }
        return cs;
    }

    private void initializeEmptyMap() {
        mapRecordFactories = new HashMap<>();
        mapRecordFactories.put(CollectionConstants.
                        FARMER_MILK_COLLECTION,
                FarmerMilkCollectionRecordSet.getInstance(mContext));
        mapRecordFactories.put(CollectionConstants.MCC_MILK_COLLECTION,
                MCCMilkCollectionRecordSet.getInstance(mContext));
        mapRecordFactories.put(CollectionConstants.SALES_COLLECTION,
                SalesCollectionRecordSet.getInstance(mContext));
        mapRecordFactories.put(CollectionConstants.DISPATCH_COLLECTION,
                DispatchCollectionRecordSet.getInstance(mContext));
        mapRecordFactories.put(CollectionConstants.SAMPLE_RECORDS,
                SampleCollectionRecordSet.getInstance(mContext));
        mapRecordFactories.put(CollectionConstants.TANKER_MILK_COLLECTION,
                TankerCollectionRecordSet.getInstance(mContext));

        mapRecordFactories.put(CollectionConstants.ROUTE_COLLECTION,
                RouteCollectionRecordSet.getInstance(mContext));

        mapRecordFactories.put(CollectionConstants.EDITED_FARMER_COLLECTION,
                EditFarmerCollectionRecordSet.getInstance(mContext));
        mapRecordFactories.put(CollectionConstants.EDITED_MCC_COLLECTION,
                EditMCCCollectionRecordSet.getInstance(mContext));

        mapRecordFactories.put(CollectionConstants.INCOMPLETE_MCC_RECORDS,
                IncompleteMCCRecordSet.getInstance(mContext));
        mapRecordFactories.put(CollectionConstants.EXTRA_PARAM_DETAILS,
                AdditionalCollectionRecordSet.getInstance(mContext));
        mapRecordFactories.put(CollectionConstants.FARMER_SPLIT_COLLECTION,
                FarmerSplitCollectionRecordSet.getInstance(mContext));
    }

    private boolean postData(CommunicationService cs) {
        boolean returnVale = true;
        ArrayList<ConsolidatedPostData> shiftRecordsList = consolidatedRecordsSynchronizer.getAllUnsentRecords();

//        Util.displayErrorToast("Before sending records ", mContext);
        if (endShift) {
            if (shiftRecordsList == null || shiftRecordsList.size() == 0) {
                shiftRecordsList = new ArrayList<>();
                ConsolidatedPostData consolidatedPostData = new ConsolidatedPostData();
                ConsolidatedMetadata consolidatedMetadata = new ConsolidatedMetadata();
                consolidatedMetadata.concludedShift = consolidatedRecordsSynchronizer.getConcludedShift();

                ConsolidatedDate consolidatedDate = new ConsolidatedDate();

                consolidatedDate.collectionDate =
                        SmartCCUtil.getCollectionDateFromLongTime(
                                new SmartCCUtil(mContext).getDateFromFormat(Util.getCurrentDateTime(), "dd-MM-yyyy").getTime());

                consolidatedDate.startTime = SmartCCUtil.getCollectionDateFromLongTime(System.currentTimeMillis());
                consolidatedDate.endTime = SmartCCUtil.getCollectionDateFromLongTime(System.currentTimeMillis());
                consolidatedMetadata.consolidatedDate = consolidatedDate;
                consolidatedMetadata.shift = Util.getCurrentShift();
                ConsolidatedId consolidatedId = new ConsolidatedId();

                consolidatedId.chillingCenter = new SessionManager(mContext).getCollectionID();
                consolidatedId.collectionCenter = new SessionManager(mContext).getCollectionID();
                consolidatedId.route = SmartCCUtil.getCenterEntity(mContext).route;
                consolidatedMetadata.consolidatedId = consolidatedId;
                consolidatedMetadata.deviceId = amcuConfig.getDeviceID();
                consolidatedPostData.recordEntries = new Hashtable<>();
                initializeEmptyMap();
                for (Map.Entry<String, RecordSet> mapEntry : mapRecordFactories.entrySet()) {
                    consolidatedPostData.recordEntries.put(mapEntry.getKey(),
                            new ArrayList<FarmerPostEntity>());
                    //Need to set start time and end time for collection
                }
                consolidatedPostData.consolidatedMetadata = consolidatedMetadata;
//
//                farmerPostEntity.
                shiftRecordsList.add(consolidatedPostData);
            }
        }

        for (ConsolidatedPostData oneShiftRecords : shiftRecordsList) {

            try {
                boolean returnStatus = postDateShiftData(oneShiftRecords, cs);
                if (returnStatus) {
                    consolidatedRecordsSynchronizer.setUpdateStatus(oneShiftRecords, CollectionConstants.SENT);

                }
            } catch (Exception4XX exception4XX) {
                returnVale = false;
                //We will loop through to send next shift data
            } catch (Exception e) {
                e.printStackTrace();
                returnVale = false;
                break;
            }
        }

        return returnVale;
    }

    private boolean postDateShiftData(ConsolidatedPostData oneShiftRecords, CommunicationService cs) throws Exception {

        String singleShiftRecordsStr = this.toJson(oneShiftRecords);
        //  Util.generateNoteOnSD("testReport", singleShiftRecordsStr, getApplicationContext(), "smartAmcuReports");
        Log.d(TAG, "PostCollectionRecordsService Posting Collection Records - " + singleShiftRecordsStr);
        HttpResponse response = cs.doPost(PostCollectionRecordsService.COLLECTION_ENTRY_URI,
                singleShiftRecordsStr, true);

//        Util.displayErrorToast("After sending records", mContext);
        //     Log.d(TAG, "PostCollectionRecordsService  - POST Response:" + response.getResponseCode());
        if (response.getResponseCode() == HttpURLConnection.HTTP_CREATED
                || response.getResponseCode() == HttpURLConnection.HTTP_OK) {
            Log.v(TAG, "Record posted success");
//            Util.displayErrorToast("Record posted success", mContext);
            // consolidatedRecordsSynchronizer.setUpdateStatus(oneShiftRecords, CollectionConstants.SENT);
            return true;
        } else if (response.getResponseCode() >= 400 && response.getResponseCode() < 500) {
            throw new Exception4XX("4xx Exception");
        } else if (response.getResponseCode() >= 500 && response.getResponseCode() < 600) {
            throw new Exception("Server Exception");
        }

        return false;

    }


}


