package com.devapp.devmain.httptasks;

import android.app.IntentService;
import android.content.Intent;

import com.devapp.devmain.dao.DaoFactory;
import com.devapp.devmain.dao.FarmerDao;
import com.devapp.devmain.entity.FarmerEntity;
import com.devapp.devmain.helper.DatabaseEntity;
import com.devapp.devmain.postentities.CollectionConstants;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.server.SessionManager;
import com.devapp.devmain.user.Util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;

/**
 * Created by Upendra on 10/26/2015.
 */
public class PostFarmerRecords extends IntentService {

    public static final String TAG = "POST_FARMER_RECORDS";
    private final String SUFFIX = "/farmers";
    AmcuConfig amcuConfig;
    private String FARMER_ADD_URI = "/amcu/collectioncenters/";
    private String CENTERID = "/amcu/farmerupdate/";
    private FarmerDao farmerDao;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public PostFarmerRecords() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        initializeClass();
        ArrayList<FarmerEntity> allFarmEnt = getAllUnsentFarmerList();

        if (allFarmEnt == null || allFarmEnt.size() == 0) {
            return;
        }

        try {
            AuthenticationParameters authParameters = new AuthenticationParameters(null,
                    amcuConfig.getDeviceID(),
                    amcuConfig.getDevicePassword());
            CommunicationService communicationService = null;
            communicationService = new CommunicationService(amcuConfig.getURLHeader()
                    + amcuConfig.getServer(),
                    authParameters);
            String jsonString = Util.getJsonFromObject(allFarmEnt);

            //    Log.d("Farmer Entry",jsonString);
            try {
                HttpResponse httpResponse = communicationService.doPut(FARMER_ADD_URI, jsonString);
                if (httpResponse.getResponseCode() == HttpURLConnection.HTTP_CREATED
                        || httpResponse.getResponseCode() == HttpURLConnection.HTTP_OK) {

                    for (int i = 0; i < allFarmEnt.size(); i++) {
                        DatabaseHandler.getDatabaseInstance().updateFarmerEntity(allFarmEnt.get(i),
                                DatabaseEntity.FARMER_SENT_CODE);
                        Util.displayErrorToast("Farmer pushed successfully!", getApplicationContext());
                    }

                } else {
                    return; // Further not attempted to be sent
                }

            } catch (IncompatibleProtocolException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (SSLContextCreationException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<FarmerEntity> getAllUnsentFarmerList() {
        DatabaseHandler dbh = DatabaseHandler.getDatabaseInstance();
        ArrayList<FarmerEntity> allFarmEntity = new ArrayList<FarmerEntity>();
        try {
            allFarmEntity = farmerDao.findAllByStatus(CollectionConstants.UNSENT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return allFarmEntity;
    }

    public void initializeClass() {
        amcuConfig = AmcuConfig.getInstance();
        farmerDao = (FarmerDao) DaoFactory.getDao(CollectionConstants.FARMER);
        CENTERID = new SessionManager(getApplicationContext()).getCollectionID();
        FARMER_ADD_URI = FARMER_ADD_URI + CENTERID + SUFFIX;
    }

}



