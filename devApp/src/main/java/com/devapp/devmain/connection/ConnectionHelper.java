/*
package com.stellapps.smartamcu.connection;

import android.content.Context;
import android.util.Log;

import com.stellapps.smartamcu.agentfarmersplit.AppConstants;
import com.stellapps.smartamcu.entity.CenterEntity;
import com.stellapps.smartamcu.entity.FarmerEntity;
import com.stellapps.smartamcu.entity.UpdateRatechartEntity;
import com.stellapps.smartamcu.entity.UpdateStatus;
import com.stellapps.smartamcu.helper.DPNType;
import com.stellapps.smartamcu.httptasks.AuthenticationParameters;
import com.stellapps.smartamcu.httptasks.CommunicationService;
import com.stellapps.smartamcu.httptasks.HttpResponse;
import com.stellapps.smartamcu.httptasks.IncompatibleProtocolException;
import com.stellapps.smartamcu.httptasks.SSLContextCreationException;
import com.stellapps.smartamcu.json.JSONParser;
import com.stellapps.smartamcu.server.DatabaseHandler;
import com.stellapps.smartamcu.server.LogInService;
import com.stellapps.smartamcu.server.SaveSession;
import com.stellapps.smartamcu.server.ServerResponse;
import com.stellapps.smartamcu.server.SessionManager;
import com.stellapps.smartamcu.user.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Locale;

import static android.content.ContentValues.TAG;
import static com.stellapps.smartamcu.server.SaveSession.saveSession;

*/
/**
 * Created by u_pendra on 27/12/17.
 * <p>
 * Check the dpn status for success and failure
 *
 * @param status
 * @param dpnEntity
 * <p>
 * Check the dpn status for success and failure
 * @param status
 * @param dpnEntity
 * <p>
 * Check the dpn status for success and failure
 * @param status
 * @param dpnEntity
 * <p>
 * Check the dpn status for success and failure
 * @param status
 * @param dpnEntity
 * <p>
 * Check the dpn status for success and failure
 * @param status
 * @param dpnEntity
 *//*


public class ConnectionHelper {

    private Context mContext;
    SessionManager sessionManager;
    DatabaseHandler databaseHandler;

    public ConnectionHelper(Context context) {

        this.mContext = context;
        sessionManager = new SessionManager(mContext);
        databaseHandler = DatabaseHandler.getDatabaseInstance(mContext);
    }


    public static ServerResponse putData(String url, Object obj, Context context) {

        ServerResponse sr = new ServerResponse();
        System.out.println("Update URL:  " + url);
        String putBody = JSONParser.getAllJsonBmcId(obj);
        AmcuConfig amcuConfig = AmcuConfig.getInstance(context);
        AuthenticationParameters authParams =
                new AuthenticationParameters(null, amcuConfig.getDeviceID(), amcuConfig.getDevicePassword());
        CommunicationService cs = null;
        try {
            cs = new CommunicationService(amcuConfig.getURLHeader() + amcuConfig.getServer(), authParams);
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        } catch (SSLContextCreationException e) {
            Log.d(TAG, e.getMessage());
        }
        HttpResponse response = null;
        try {
            response = cs.doPut(url, putBody);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (IncompatibleProtocolException ipe) {
            ipe.printStackTrace();
        }
        String responseBody = null;
        if (response != null) {
            responseBody = response.getResponseBody();
            if (response.getResponseCode() == 200) {
                sr.setSuccess(true);
                sr.setResponseString(response.toString());
            } else if (response.getResponseCode() == 201) {
                sr.setSuccess(true);
                sr.setResponseString(response.getResponseBody());

            } else {
                sr.setSuccess(false);
                sr.setErrorMessage(response.toString());
                LogInService.isAuthenticated = false;
            }
        } else {
            sr.setSuccess(false);
            sr.setErrorMessage("Null response");
            LogInService.isAuthenticated = false;
        }
        System.out.println(" Update response body: " + responseBody);
        return sr;
    }


    public static HttpResponse getResponseFromURL(DpnEntity dpnEntity, Context context) {

        AmcuConfig amcuConfig = AmcuConfig.getInstance(context);
        AuthenticationParameters authParams =
                new AuthenticationParameters(null, amcuConfig.getDeviceID(),
                        amcuConfig.getDevicePassword());
        CommunicationService cs = null;
        try {
            cs = new CommunicationService(amcuConfig.getURLHeader() +
                    amcuConfig.getServer(), authParams);
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        } catch (SSLContextCreationException e) {
            Log.d(TAG, e.getMessage());
        }
        HttpResponse response = null;
        try {
            response = cs.doGet(dpnEntity.url);
        } catch (IOException e) {
            e.printStackTrace();

        } catch (IncompatibleProtocolException e) {
            e.printStackTrace();
        }
        return response;
    }


    ServerResponse serverResponse;

    */
/**
 * Check the dpn status for success and failure
 *
 * @param status
 * @param dpnEntity
 *//*

    public ServerResponse sendUpdationStatus(int status, final DpnEntity dpnEntity) {
        if (status == UpdateStatus.FAILURE) {
            Util.displayErrorToast("Invalid response " + dpnEntity.dpnType, mContext);
        }
        final UpdateRatechartEntity urc = new UpdateRatechartEntity();
        urc.status = status;
        urc.imeiNumber = amcuConfig.getDeviceID();
        new Thread(new Runnable() {
            @Override
            public void run() {
                serverResponse = putData(dpnEntity.url, urc, mContext);
                if (serverResponse.getSuccess()) {
                    updateDPN(dpnEntity);
                }
            }
        }).start();

        return serverResponse;
    }

    private void updateDPN(DpnEntity dpnEntity) {

        dpnEntity.dpnStatus = DPNType.SUCCESS_STATUS;
        databaseHandler.insertorUpdateDpnEntity(dpnEntity);

    }

    public Object getJsonObject(JSONObject jsonData, String type) throws JSONException {

        Object obj = null;
        if (type.equalsIgnoreCase(DPNType.FARMER_DPN)) {
            String farmerId = Util.validateFarmerId(jsonData.getString("farmer_id"),
                    mContext, amcuConfig.getFarmerIdDigit(), false);
            String barcode = jsonData.getString("farmer_barcode");
            if (barcode == null) {
                barcode = "";
            } else if (barcode.equalsIgnoreCase("null")) {
                barcode = "";
            } else if (barcode.length() < 1) {
                barcode = "";
            } else {
                barcode = barcode.toUpperCase(Locale.ENGLISH);
            }
            if (farmerId != null) {
                farmerId = farmerId.toUpperCase(Locale.ENGLISH);
            }
            String duplicateMessage = null;
            duplicateMessage = Util.getDuplicateIdOrBarCode(farmerId, barcode, mContext);
            if (farmerId != null && (jsonData.getString("farmer_name") != null &&
                    !jsonData.getString("farmer_name").equalsIgnoreCase("null")) && (duplicateMessage == null
                    || (duplicateMessage != null && duplicateMessage.contains("Member")))) {

                FarmerEntity farmerEntity = new FarmerEntity();
                if (jsonData.getString("farm_mob") == null) {
                    farmerEntity.farm_mob = "";
                } else if (jsonData.getString("farm_mob").equalsIgnoreCase("null")) {
                    farmerEntity.farm_mob = "";
                } else {
                    farmerEntity.farm_mob = jsonData.getString("farm_mob");

                }

                farmerEntity.farm_email = jsonData.getString("farm_email");

                if (barcode == null || barcode.replace(" ", "").length() < 2) {
                    farmerEntity.farmer_barcode = null;
                } else {
                    farmerEntity.farmer_barcode = barcode;
                }

                if (jsonData.getString("farmer_cans") != null
                        && jsonData.getString("farmer_cans").equalsIgnoreCase("null")) {
                    farmerEntity.farmer_cans = String.valueOf(1);
                } else if (jsonData.getString("farmer_cans") == null) {
                    farmerEntity.farmer_cans = String.valueOf(1);
                } else {
                    try {
                        farmerEntity.farmer_cans = String.valueOf(Integer.parseInt(jsonData.getString("farmer_cans")));
                    } catch (NumberFormatException e) {

                        e.printStackTrace();
                        farmerEntity.farmer_cans = String.valueOf(1);
                        Util.displayErrorToast("Invalid cans for farmer", mContext);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                farmerEntity.farmer_id = Util.paddingFarmerId(String.valueOf(Integer.parseInt(farmerId))
                        , amcuConfig.getFarmerIdDigit());

                if (jsonData.getString("farmer_cattle") == null) {
                    farmerEntity.farmer_cattle = "COW";
                } else if (jsonData.getString("farmer_cattle").equalsIgnoreCase("null")) {
                    farmerEntity.farmer_cattle = "COW";
                } else {
                    farmerEntity.farmer_cattle = jsonData.getString("farmer_cattle");
                }

                farmerEntity.farmer_name = jsonData.getString("farmer_name");


                try {
                    farmerEntity.farmer_regDate = jsonData.getLong("farmer_regDate");
                } catch (JSONException e) {
                    farmerEntity.farmer_regDate = System.currentTimeMillis();
                    e.printStackTrace();
                }
                farmerEntity.soc_code = jsonData.getString("soc_code");

                if (jsonData.getString("numBuff") == null) {
                    farmerEntity.numBuff = String.valueOf(Integer.parseInt("0"));
                } else if (jsonData.getString("numBuff").equalsIgnoreCase("null")) {
                    farmerEntity.numBuff = String.valueOf(0);
                } else {
                    try {
                        farmerEntity.numBuff = String.valueOf(Integer.parseInt(jsonData.getString("numBuff")));
                    } catch (NumberFormatException e) {
                        farmerEntity.numBuff = String.valueOf(0);
                        Util.displayErrorToast("Invalid number of Buffalo", mContext);
                        e.printStackTrace();
                    }
                }

                if (jsonData.getString("numCow") == null) {
                    farmerEntity.numCow = String.valueOf(Integer.parseInt("0"));
                } else if (jsonData.getString("numCow").equalsIgnoreCase("null")) {
                    farmerEntity.numCow = String.valueOf(0);
                } else {
                    try {
                        farmerEntity.numCow = String.valueOf(Integer.parseInt(jsonData.getString("numCow")));
                    } catch (NumberFormatException e) {
                        farmerEntity.numCow = String.valueOf(0);
                        Util.displayErrorToast("Invalid number of cow", mContext);
                        e.printStackTrace();
                    }
                }
                if (jsonData.getString("isMultipleCans") != null && !jsonData.getString("isMultipleCans").equalsIgnoreCase("null")) {
                    farmerEntity.isMultipleCans = Boolean.parseBoolean(jsonData.getString("isMultipleCans"));
                } else {
                    farmerEntity.isMultipleCans = false;
                }

                farmerEntity.agentId = jsonData.optString("agentId", AppConstants.NA);
                farmerEntity.farmerType = jsonData.optString("producerType", AppConstants.FARMER_TYPE_FARMER);
                obj = farmerEntity;
            } else {
                Util.displayErrorToast(duplicateMessage, mContext);
            }
        } else if (type.equalsIgnoreCase(DPNType.RATECHART_DPN)) {

        } else if (type.equalsIgnoreCase(DPNType.MCC_DPN)) {

            CenterEntity centerEntity = new CenterEntity();


            String centerId = jsonData.getString("centerId");
            String centerBarcode = jsonData.getString("centerBarcode");

            if (centerBarcode != null) {
                centerBarcode = centerBarcode.toUpperCase(Locale.ENGLISH);
            }

            if (centerId != null) {

                centerId = centerId.toUpperCase(Locale.ENGLISH);
            }

            String duplicateMessage = null;

            duplicateMessage = Util.checkForRegisteredCode(centerId,
                    AmcuConfig.getInstance(mContext).getFarmerIdDigit(), true);
            if (duplicateMessage == null) {
                duplicateMessage = Util.getDuplicateIdOrBarCode(centerId, centerBarcode, mContext);
            }

            if (centerId != null &&
                    !centerId.equalsIgnoreCase("null") && (duplicateMessage == null
                    || (duplicateMessage != null && duplicateMessage.contains("Center")))) {

                if ((duplicateMessage != null && duplicateMessage.contains("Center"))) {
                    centerEntity.centerId = centerId.toUpperCase(Locale.ENGLISH);
                    int id = -1;
                    try {
                        id = Integer.parseInt(centerId);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }

                    if (id != -1) {
                        DatabaseHandler databaseHandler = DatabaseHandler.getDatabaseInstance(mContext);

                        String centerCode = databaseHandler.getDuplicateCenterId(id);

                        if (centerCode != null) {
                            centerId = centerCode;
                            centerEntity.centerId = centerId.toUpperCase(Locale.ENGLISH);
                        }

                    }
                } else {
                    centerEntity.centerId = centerId.toUpperCase(Locale.ENGLISH);
                }

                if (jsonData.getString("contactNumber") == null) {
                    centerEntity.contactNumber = "";
                } else if (jsonData.getString("contactNumber").equalsIgnoreCase("null")) {
                    centerEntity.contactNumber = "";
                } else {
                    centerEntity.contactNumber = jsonData.getString("contactNumber");

                }

                if (jsonData.getString("contactEmailId") != null &&
                        !jsonData.getString("contactEmailId").equalsIgnoreCase("null")) {
                    centerEntity.contactEmailId = jsonData.getString("contactEmailId");
                }
                centerEntity.chillingCenterId = sessionManager.getCollectionID();
                if (centerBarcode != null && !centerBarcode.equalsIgnoreCase("null")) {
                    centerEntity.centerBarcode = centerBarcode.toUpperCase(Locale.ENGLISH);
                }

                if (jsonData.getString("contactPerson") == null) {
                    centerEntity.contactPerson = "";
                } else if (jsonData.getString("contactPerson").equalsIgnoreCase("null")) {
                    centerEntity.contactPerson = "";
                } else {
                    centerEntity.contactPerson = jsonData.getString("contactPerson");
                }

                try {
                    centerEntity.registerDate = jsonData.getLong("lastModified");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (jsonData.getString("centerName") == null) {
                    centerEntity.centerName = "";
                } else if (jsonData.getString("centerName").equalsIgnoreCase("null")) {
                    centerEntity.centerName = "";
                } else {
                    centerEntity.centerName = jsonData.getString("centerName").toUpperCase(Locale.ENGLISH);

                }
                //adding route
                if (jsonData.getString("centerRoute") == null) {
                    centerEntity.centerRoute = "NA";
                } else if (jsonData.getString("centerRoute").equalsIgnoreCase("null")) {
                    centerEntity.centerRoute = "NA";
                } else {
                    centerEntity.centerRoute = jsonData.getString("centerRoute");
                }

                if (jsonData.getString("isMultipleCans") != null && jsonData.getBoolean("isMultipleCans") != true) {
                    centerEntity.singleOrMultiple = Util.SINGLE;
                } else {
                    centerEntity.singleOrMultiple = Util.MULTIPLE;
                }

                if (jsonData.getString("milkType") == null ||
                        jsonData.getString("milkType").equalsIgnoreCase("null")) {
                    centerEntity.cattleType = "COW";
                } else {
                    centerEntity.cattleType = jsonData.getString("milkType").toUpperCase(Locale.ENGLISH);
                }
            }
            obj = centerEntity;
        }
        return obj;
    }


}
*/
