package com.devapp.devmain.devicemanager;

/**
 * Created by Upendra on 9/8/2015.
 */

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.devapp.devmain.additionalRecords.CustomFieldEntity;
import com.devapp.devmain.additionalRecords.Database.AddtionalDatabase;
import com.devapp.devmain.encryption.Csv;
import com.devapp.devmain.entity.ConfigurationConstants;
import com.devapp.devmain.entity.ConfigurationElement;
import com.devapp.devmain.entity.ConfigurationEntity;
import com.devapp.devmain.entity.ShiftEntity;
import com.devapp.devmain.entity.SocietyEntity;
import com.devapp.devmain.entity.UpdateRatechartEntity;
import com.devapp.devmain.entity.UserEntity;
import com.devapp.devmain.helper.DatabaseEntity;
import com.devapp.devmain.httptasks.AuthenticationParameters;
import com.devapp.devmain.httptasks.CommunicationService;
import com.devapp.devmain.httptasks.ConfigurationPush;
import com.devapp.devmain.httptasks.HttpResponse;
import com.devapp.devmain.httptasks.IncompatibleProtocolException;
import com.devapp.devmain.httptasks.SSLContextCreationException;
import com.devapp.devmain.json.JSONParser;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.server.LogInService;
import com.devapp.devmain.server.ServerResponse;
import com.devapp.devmain.server.SessionManager;
import com.devapp.devmain.services.UpdateFarmerService;
import com.devapp.devmain.user.UserDetails;
import com.devapp.devmain.user.Util;
import com.devapp.devmain.util.ValidationHelper;
import com.devapp.smartcc.entityandconstants.SmartCCUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class ConfigurationManager {

    private static final String TAG = "CONFIGURATION_MANAGER";
    static JSONParser jParser;
    private final int SAVE_SESSIONMANAGER = 0;
    private final int SAVE_SAVESESSION = 1;
    private final int SAVE_USERENTITY = 2;
    private final int SAVE_SETTING = 3;
    JSONArray jsonArray;
    SessionManager session;
    AmcuConfig amcuConfig;
    String url;
    String server;
    ValidationHelper validationHelper;
    ShiftEntity shiftEntity;
    SmartCCUtil smartCCUtil;
    ServerResponse serverResponse;
    String bonusStartDate = null;
    String bonusEndDate = null;
    String bonus = null;
    boolean isBonusUpdated;
    private Context mContext;
    private boolean isChangeCollectionTiming;

    private boolean isFarmerUpdated = false;
    private int FARMER_ID_DIGIT = 4;

    public ConfigurationManager(Context context) {
        mContext = context;
        session = new SessionManager(mContext);
        jParser = new JSONParser();
        amcuConfig = AmcuConfig.getInstance();
        server = amcuConfig.getURLHeader() + amcuConfig.getServer();
        validationHelper = new ValidationHelper();
        smartCCUtil = new SmartCCUtil(context);
        shiftEntity = new ShiftEntity();
        initializeShiftEntity();
    }

    public void parseUrlToGetConfiguration() {
        if (Util.CONFIGURATION_URI != null && Util.CONFIGURATION_URI.contains("amcu")) {
            amcuConfig.setConfigurationURI(Util.CONFIGURATION_URI);
            Util.CONFIGURATION_URI = null;
        }
        if (amcuConfig.getConfigurationURI() != null) {
            getConfigurationData();
        }
    }

    public void setSimPassword() {
        if (amcuConfig.getAllowSimlockPassword()) {

            if (amcuConfig.getTempSimPin() != null && amcuConfig.getSimUnlockPassword() == null) {
                amcuConfig.setSimlockPassword(amcuConfig.getTempSimPin());
                amcuConfig.setTempSimPin(null);
            } else if (amcuConfig.getTempSimPin() != null) {
                Util.displayActivity(mContext);
            }
        } else {
            amcuConfig.setTempSimPin(null);
        }
    }

    public void getConfigurationData() {
        url = amcuConfig.getConfigurationURI();
        new Thread(new Runnable() {

            @Override
            public void run() {

                AuthenticationParameters authParams =
                        new AuthenticationParameters(null, amcuConfig.getDeviceID(),
                                amcuConfig.getDevicePassword());

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
                    response = cs.doGet(amcuConfig.getConfigurationURI());
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (IncompatibleProtocolException e) {
                    e.printStackTrace();
                }

                if (response == null) {

                    Util.displayErrorToast("Invalid response please try again ", mContext);
                    sendUpdationStatus();
                } else if (response != null && response.getResponseCode() >= 400 && response.getResponseCode() < 500) {
                    Util.displayErrorToast("Invalid response " + response.getResponseCode(), mContext);
                    sendUpdationStatus();
                } else if (response != null && response.getResponseBody() != null) {
                    Log.d(TAG, "Configuration");
                    Log.d(TAG, response.getResponseBody());
                    System.out.println(response.getResponseBody());
                    try {
                        jsonArray = new JSONArray(response.getResponseBody());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (jsonArray != null) {
                        try {
                            getElementFromJSON(jsonArray);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        }).start();


    }

    public void sendUpdationStatus() {
        final UpdateRatechartEntity urc = new UpdateRatechartEntity();
        urc.status = 1;
        urc.imeiNumber = amcuConfig.getDeviceID();
        serverResponse = putData(url, urc);

        if (serverResponse.getSuccess()) {
            amcuConfig.setConfigurationURI(null);
            setSimPassword();
        }

        if (isBonusUpdated && validateBonusDate()) {
            setBonusDetails();
            Util.displayErrorToast("Configuration parameters updated successfully.", mContext);
        } else if (isChangeCollectionTiming) {
            if (shiftEntity.morningCollectionStartTime >= shiftEntity.morningCollectionEndTime
                    || shiftEntity.morningCollectionStartTime <= shiftEntity.morningStartTime) {

                Util.displayErrorToast("Invalid collection time!", mContext);
            } else if (shiftEntity.eveningCollectionStartTime <= shiftEntity.morningCollectionEndTime
                    || shiftEntity.eveningCollectionStartTime >= shiftEntity.eveningCollectionEndtime) {
                Util.displayErrorToast("Invalid collection time!", mContext);
            } else if (shiftEntity.morningCollectionEndTime >= shiftEntity.eveningStartTime
                    || shiftEntity.eveningCollectionEndtime >= smartCCUtil.getTimeinLong("23:59")) {
                Util.displayErrorToast("Invalid collection time!", mContext);
            } else {
                updateConfigurationData(ConfigurationConstants.collectionStartMorningshift,
                        String.valueOf(smartCCUtil.getTimeFromMiliSecond(shiftEntity.morningCollectionStartTime)), amcuConfig.getKeyCollStartMrnShift());
                updateConfigurationData(ConfigurationConstants.collectionEndMorningShift,
                        String.valueOf(smartCCUtil.getTimeFromMiliSecond(shiftEntity.morningCollectionEndTime)), amcuConfig.getKeyCollEndMrnShift());
                updateConfigurationData(ConfigurationConstants.collectionStartEveningShift,
                        String.valueOf(smartCCUtil.getTimeFromMiliSecond(shiftEntity.eveningCollectionStartTime)), amcuConfig.getKeyCollStartEvnShift());
                updateConfigurationData(ConfigurationConstants.collectionEndEveningShift,
                        String.valueOf(smartCCUtil.getTimeFromMiliSecond(shiftEntity.eveningCollectionEndtime)), amcuConfig.getKeyCollEndEvnShift());

                amcuConfig.setKeyCollStartMrnShift(
                        smartCCUtil.getTimeFromMiliSecond(shiftEntity.morningCollectionStartTime));
                amcuConfig.setKeyCollEndMrnShift(
                        smartCCUtil.getTimeFromMiliSecond(shiftEntity.morningCollectionEndTime));
                amcuConfig.setKeyCollStartEvnShift(
                        smartCCUtil.getTimeFromMiliSecond(shiftEntity.eveningCollectionStartTime));
                amcuConfig.setKeyCollEndEvnShift(
                        smartCCUtil.getTimeFromMiliSecond(shiftEntity.eveningCollectionEndtime));
                Util.displayErrorToast("Configuration parameters updated successfully.", mContext);

            }
        } else if (isFarmerUpdated) {
            Intent intent = new Intent(mContext, UpdateFarmerService.class);
            intent.putExtra(UpdateFarmerService.FROM_CONFIGURATION, true);
            intent.putExtra("DigitLength", FARMER_ID_DIGIT);
            mContext.startService(intent);
            Util.displayErrorToast("Farmers length configuration getting updated!.", mContext);
        } else if (!isBonusUpdated) {
            Util.displayErrorToast("Configuration parameters updated successfully.", mContext);
        }


        mContext.getApplicationContext().startService(new Intent(mContext.getApplicationContext(),
                ConfigurationPush.class));
    }

    public ServerResponse putData(String url, Object obj) {

        ServerResponse sr = new ServerResponse();
        System.out.println("URL for put configuration update: " + url);
        String putBody = JSONParser.getAllJsonBmcId(obj);
        AmcuConfig amcuConfig = AmcuConfig.getInstance();

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

        com.devapp.devmain.httptasks.HttpResponse response = null;
        try {
            response = cs.doPut(url, putBody);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (IncompatibleProtocolException ipe) {
            ipe.printStackTrace();
        }

        if (response != null) {

            if (response.getResponseCode() == 200 || response.getResponseCode() == 201) {
                sr.setSuccess(true);
                sr.setResponseString(response.getResponseBody());
            }
        } else {
            sr.setSuccess(false);
            if (null != response) {
                sr.setErrorMessage(response.toString());

            }
            LogInService.isAuthenticated = false;
        }
//
//        if (response != null)
//            System.out.println("JSON parser---Put response: "
//                    + String.valueOf(response.getResponseCode())
//                    + "  Response Body: "
//                    + response.getResponseBody());

        return sr;
    }

    public void getElementFromJSON(JSONArray jsonArray) throws JSONException {


        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jObj = jsonArray.getJSONObject(i);
            if (jObj.getString("name").equalsIgnoreCase("morningSessionStartTime") ||
                    jObj.getString("name").equalsIgnoreCase("eveningSessionStartTime") ||
                    jObj.getString("name").equalsIgnoreCase("morningSessionEndTime") ||
                    jObj.getString("name").equalsIgnoreCase("eveningSessionEndTime") ||
                    jObj.getString("name").equalsIgnoreCase("perDaySMSLimit")) {
                //Need to add this data in session manager
                try {
                    setDataInSessionManager(jObj.getString("name"), jObj.getString("value"));
                } catch (JSONException e1) {
                    Util.displayErrorToast("Invalid configuration", mContext);
                } catch (NumberFormatException e) {
                    Util.displayErrorToast("Invalid value for " + jObj.getString("name"), mContext);
                } catch (Exception e2) {
                    Util.displayErrorToast("Invalid configuration", mContext);
                }

            } else if (jObj.getString("name").equalsIgnoreCase("minFat") || jObj.getString("name").equalsIgnoreCase("minSnf")
                    || jObj.getString("name").equalsIgnoreCase("minClr") || jObj.getString("name").equalsIgnoreCase("maxFat")
                    || jObj.getString("name").equalsIgnoreCase("maxSnf") || jObj.getString("name").equalsIgnoreCase("maxClr") ||
                    jObj.getString("name").equalsIgnoreCase("supportOnlyExternalKeyboard") ||
                    jObj.getString("name").equalsIgnoreCase("allowUnregisterUser")) {

                // Need to add this data in setting table

                // setDataInSetting(jObj.getString("name"), jObj.getString("value"));

            } else if (jObj.getString("name").equalsIgnoreCase("managerPassword") || jObj.getString("name").equalsIgnoreCase("managerEmailID")
                    || jObj.getString("name").equalsIgnoreCase("managerMobileNumber")) {

                // Need to add this data in User Manager table
                try {
                    setUserEntity(UserDetails.MANAGER, jObj.getString("name"), jObj.getString("value"));
                } catch (JSONException e1) {
                    Util.displayErrorToast("Invalid configuration", mContext);
                } catch (NumberFormatException e) {
                    Util.displayErrorToast("Invalid value for " + jObj.getString("name"), mContext);
                } catch (Exception e2) {
                    Util.displayErrorToast("Invalid configuration", mContext);
                }

            } else if (jObj.getString("name").equalsIgnoreCase("operatorPassword") || jObj.getString("name").equalsIgnoreCase("operatorMobileNumber")) {

                // Need to add this data in User Operator table
                try {
                    setUserEntity("SmartOperator", jObj.getString("name"), jObj.getString("value"));
                } catch (JSONException e1) {
                    Util.displayErrorToast("Invalid configuration", mContext);
                } catch (NumberFormatException e) {
                    Util.displayErrorToast("Invalid value for " + jObj.getString("name"), mContext);
                } catch (Exception e2) {
                    Util.displayErrorToast("Invalid configuration", mContext);
                }

            } else if (jObj.getString("name") != null && jObj.getString("name").length() > 8 &&
                    jObj.getString("name").substring(0, 7).contains("center")) {

                // Need to add this data in Society
                try {
                    setDataForSociety(jObj.getString("name"), jObj.getString("value"));
                } catch (JSONException e1) {
                    Util.displayErrorToast("Invalid configuration", mContext);
                } catch (NumberFormatException e) {
                    Util.displayErrorToast("Invalid value for " + jObj.getString("name"), mContext);
                } catch (Exception e2) {
                    Util.displayErrorToast("Invalid configuration", mContext);
                }

            } else {
                try {
                    setDataInSaveSession(jObj.getString("name"), jObj.getString("value"));
                } catch (JSONException e1) {
                    Util.displayErrorToast("Invalid configuration", mContext);
                } catch (NumberFormatException e) {

                } catch (Exception e2) {
                    Util.displayErrorToast("Invalid configuration", mContext);
                }
            }
        }
        sendUpdationStatus();
    }

    public void setDataInSaveSession(String data, String value) {
        if (data.equalsIgnoreCase("milkType")) {
            updateConfigurationData(data, value, amcuConfig.getMilkType());
        } else if (data.equalsIgnoreCase("rateChart")) {
            updateConfigurationData(data, value, amcuConfig.getRateChartName());
        } else if (data.equalsIgnoreCase("milkAnalyzerName")) {
            updateConfigurationData(data, value, amcuConfig.getMA());
            amcuConfig.setMA(value);
        } else if (data.equalsIgnoreCase("printerName")) {
            updateConfigurationData(data, value, amcuConfig.getPrinter());
        } else if (data.equalsIgnoreCase("rduName")) {
            updateConfigurationData(data, value, amcuConfig.getRDU());
        } else if (data.equalsIgnoreCase("weighingScale")) {
            updateConfigurationData(data, value, amcuConfig.getWeighingScale());
        } else if (data.equalsIgnoreCase("secondMAType")) {
            updateConfigurationData(data, String.valueOf(value), amcuConfig.getSecondMilkAnalyser());
        } else if (data.equalsIgnoreCase("milkAnalyzerBaudrate")) {
            updateConfigurationData(data, String.valueOf(value), String.valueOf(amcuConfig.getMABaudRate()));
        } else if (data.equalsIgnoreCase("printerBaudrate")) {
            updateConfigurationData(data, String.valueOf(value), String.valueOf(amcuConfig.getPrinterBaudRate()));
        } else if (data.equalsIgnoreCase("rduBaudrate")) {
            updateConfigurationData(data, String.valueOf(value), String.valueOf(amcuConfig.getRdubaudrate()));
        } else if (data.equalsIgnoreCase("WeighingBaudrate")) {
            updateConfigurationData(data, String.valueOf(value), String.valueOf(amcuConfig.getWeighingbaudrate()));
        } else if (data.equalsIgnoreCase("secondMABaudrate")) {
            updateConfigurationData(data, String.valueOf(value), String.valueOf(amcuConfig.getSecondMilkBaud()));
        } else if (data.equalsIgnoreCase("fatConstantForClr")) {

            DecimalFormat decimalFormatConversion = new DecimalFormat("#0.00");
            value = Util.convertStringtoDecimal(value, decimalFormatConversion, "-1");
            float val = Float.parseFloat(value);
            if (val > -1) {
                updateConfigurationData(data, String.valueOf(value), String.valueOf(amcuConfig.getFatCons()));
            } else {
                Util.displayErrorToast("Invalid clr formual cons.", mContext);
            }

        } else if (data.equalsIgnoreCase("snfConstantForClr")) {
            DecimalFormat decimalFormatConversion = new DecimalFormat("#0.00");
            value = Util.convertStringtoDecimal(value, decimalFormatConversion, "-1");
            float val = Float.parseFloat(value);
            if (val > -1) {
                updateConfigurationData(data, String.valueOf(value), String.valueOf(amcuConfig.getSnfCons()));
            } else {
                Util.displayErrorToast("Invalid clr formulal cons.", mContext);
            }
        } else if (data.equalsIgnoreCase("constantForClr")) {
            DecimalFormat decimalFormatConversion = new DecimalFormat("#0.00");
            value = Util.convertStringtoDecimal(value, decimalFormatConversion, "-1");
            float val = Float.parseFloat(value);
            if (val > -1) {
                updateConfigurationData(data, String.valueOf(value), String.valueOf(amcuConfig.getConstant()));
            } else {
                Util.displayErrorToast("Invalid clr formula cons.", mContext);
            }
        } else if (data.equalsIgnoreCase("weightRoundOff")) {
            updateConfigurationData(data, String.valueOf(value), String.valueOf(amcuConfig.getDecimalRoundOffWeigh()));
        } else if (data.equalsIgnoreCase("amountRoundOff")) {
            updateConfigurationData(data, String.valueOf(value), String.valueOf(amcuConfig.getDecimalRoundOffAmount()));
        } else if (data.equalsIgnoreCase("rateRoundOff")) {
            updateConfigurationData(data, String.valueOf(value), String.valueOf(amcuConfig.getDecimalRoundOffRate()));
        } else if (data.equalsIgnoreCase("weightRoundOffCheck")) {
            updateConfigurationData(data, String.valueOf(value), String.valueOf(amcuConfig.getDecimalRoundOffWeightCheck()));
        } else if (data.equalsIgnoreCase("amountRoundOffCheck")) {
            updateConfigurationData(data, String.valueOf(value), String.valueOf(amcuConfig.getDecimalRoundOffAmountCheck()));
        } else if (data.equalsIgnoreCase("rateRoundOffCheck")) {
            updateConfigurationData(data, String.valueOf(value), String.valueOf(amcuConfig.getDecimalRoundOffRateCheck()));
        } else if (data.equalsIgnoreCase("isAddedWater")) {
            updateConfigurationData(data, String.valueOf(value), String.valueOf(amcuConfig.getIsAddedWater()));
        } else if (data.equalsIgnoreCase("isRateEditCheck")) {
            updateConfigurationData(data, String.valueOf(value), String.valueOf(amcuConfig.getEditableRate()));
        } else if (data.equalsIgnoreCase("changeMilktypeBuffalo")) {
            updateConfigurationData(data, String.valueOf(value), (amcuConfig.getChangeFat()));
        } else if (data.equalsIgnoreCase("changeSnfMilkTypeBuffalo")) {
            updateConfigurationData(data, String.valueOf(value), amcuConfig.getChangeSnf());
        } else if (data.equalsIgnoreCase("shutDownDelay")) {
            updateConfigurationData(data, String.valueOf(Util.getLongFromTime(value)), String.valueOf(amcuConfig.getShutDownDelay()));

        } else if (data.equalsIgnoreCase("isMaManual")) {
            updateConfigurationData(data, String.valueOf(value), String.valueOf(amcuConfig.getMaManual()));
        } else if (data.equalsIgnoreCase("isWsManual")) {
            updateConfigurationData(data, String.valueOf(value), String.valueOf(amcuConfig.getWsManual()));
        } else if (data.equalsIgnoreCase("bonusStartDate")) {
            bonusStartDate = value;
            isBonusUpdated = true;

        } else if (data.equalsIgnoreCase("bonusEndDate")) {
            bonusEndDate = value;
            isBonusUpdated = true;
        } else if (data.equalsIgnoreCase("bonus")) {
            isBonusUpdated = true;
            bonus = value;
        } else if (data.equalsIgnoreCase("isBonusEnable")) {
            isBonusUpdated = true;
            updateConfigurationData(data, String.valueOf(value), String.valueOf(amcuConfig.getBonusEnable()));
            // saveSession.setBonusDetails(saveSession.getBonusStartDate(), saveSession.getBonusEndDate(),
            //       saveSession.getBonusRate(),
            //     Boolean.parseBoolean(value), saveSession.getOperatorRateAccess());

        } else if (data.equalsIgnoreCase("sendShiftMails")) {
            updateConfigurationData(data, String.valueOf(value), String.valueOf(amcuConfig.getSendEmailToConfigureIDs()));
            amcuConfig.setSendMailToConfigureEmailIDs(Boolean.parseBoolean(value), amcuConfig.getImportExcelRateEnable());
        } else if (data.equalsIgnoreCase("enableSMS")) {
            updateConfigurationData(data, String.valueOf(value), String.valueOf(amcuConfig.getAllowSMS()));
            amcuConfig.setMaAndWsManualAndAllowSMS(amcuConfig.getMaManual(), amcuConfig.getWsManual(), Boolean.parseBoolean(value));
        } else if (data.equalsIgnoreCase("isRateImportAccessOperator")) {
            updateConfigurationData(data, String.valueOf(value), String.valueOf(amcuConfig.getOperatorRateAccess()));
            // saveSession.setBonusDetails(saveSession.getBonusStartDate(), saveSession.getBonusEndDate(),
            //       saveSession.getBonusRate(),
            //     saveSession.getBonusEnable(), Boolean.parseBoolean(value));
        } else if (data.equalsIgnoreCase("isImportExcelSheetAccess")) {
            updateConfigurationData(data, String.valueOf(value), String.valueOf(amcuConfig.getImportExcelRateEnable()));
            amcuConfig.setSendMailToConfigureEmailIDs(amcuConfig.getSendEmailToConfigureIDs(), Boolean.parseBoolean(value));
        } else if (data.equalsIgnoreCase("simlockPassword")) {
            updateConfigurationData(data, value, amcuConfig.getTempSimPin());
            //saveSession.setTempSimPin(value);
        } else if (data.equalsIgnoreCase("allowSimlock")) {
            updateConfigurationData(data, String.valueOf(value), String.valueOf(amcuConfig.getAllowSimlockPassword()));
            // saveSession.setAllowSimLockPassword(Boolean.parseBoolean(value));
        } else if (data.equalsIgnoreCase("smartAmcuEmail")) {
            updateConfigurationData(data, value, amcuConfig.getClientConfiguredEmail());
            //  saveSession.setConfiguredClient(value,null)
        } else if (data.equalsIgnoreCase("smartAmcuPassword")) {
            Csv csv = new Csv(mContext, 0);
            value = csv.getEncryptedKEY(value);
            updateConfigurationData(data, String.valueOf(value), String.valueOf(
                    csv.getDcryptedKEY(amcuConfig.getClientConfiguredPassword())));
            // saveSession.setConfiguredClient(null,value);
        } else if (data.equalsIgnoreCase("smsFooter")) {
            updateConfigurationData(data, value, amcuConfig.getSMSFooter());
        } else if (data.equalsIgnoreCase("collectionSMSFooter")) {
            updateConfigurationData(data, value, amcuConfig.getCollectionSMSFooter());
        } else if (data.equalsIgnoreCase("enableRejectToDevice")) {
            updateConfigurationData(data, String.valueOf(value), String.valueOf(amcuConfig.getEnableRejectForRateChart()));
        } else if (data.equalsIgnoreCase("enableManualToDevice")) {
            updateConfigurationData(data, String.valueOf(value), String.valueOf(amcuConfig.getManualDisplay()));
        } else if (data.equalsIgnoreCase("enableSimlockToDevice")) {
            updateConfigurationData(data, String.valueOf(value), String.valueOf(amcuConfig.getSimlockDisplay()));
        } else if (data.equalsIgnoreCase("enableBonusToDevice")) {
            updateConfigurationData(data, String.valueOf(value), String.valueOf(amcuConfig.getBonusDisplay()));
        } else if (data.equalsIgnoreCase("enableMultipleCollection")) {
            updateConfigurationData(data, String.valueOf(value), String.valueOf(amcuConfig.getAllowMultipeCollection()));
        } else if (data.equalsIgnoreCase("enableConfigurableFarmerIdSize")) {
            updateConfigurationData(data, String.valueOf(value), String.valueOf(amcuConfig.getEnableFarmerLenghtConfiguration()));
        } else if (data.equalsIgnoreCase("acceptMilkInKgFormat")) {
            if (value != null && value.equalsIgnoreCase("Kg")) {
                updateConfigurationData(data, String.valueOf(true), String.valueOf(amcuConfig.getAllowInKgformat()));


            } else {
                updateConfigurationData(data, String.valueOf(false), String.valueOf(amcuConfig.getAllowInKgformat()));


            }

        } else if (data.equalsIgnoreCase("enableConversionFactor")) {
            updateConfigurationData(data, String.valueOf(value), String.valueOf(amcuConfig.getEnableConversionFactorDisplay()));
        } else if (data.equalsIgnoreCase("conversionFactor")) {
            updateConfigurationData(data, value, String.valueOf(amcuConfig.getConversionFactor()));
            DecimalFormat decimalFormatConversion = new DecimalFormat("#0.0000");
            value = Util.convertStringtoDecimal(value, decimalFormatConversion, "1");
        } else if (data.equalsIgnoreCase("weighingScalePrefix")) {
            if (value == null || value.equalsIgnoreCase("NULL")) {
                // saveSession.setWeighingPrefix("");

                updateConfigurationData(data, "", String.valueOf(amcuConfig.getWeighingPrefix()));

            } else {

                updateConfigurationData(data, value, String.valueOf(amcuConfig.getWeighingPrefix()));

            }

        } else if (data.equalsIgnoreCase("weighingScaleSuffix")) {
            if (value == null || value.equalsIgnoreCase("NULL")) {
                updateConfigurationData(data, "", String.valueOf(amcuConfig.getWeighingSuffix()));
            } else {
                updateConfigurationData(data, String.valueOf(value), String.valueOf(amcuConfig.getWeighingSuffix()));
            }

        } else if (data.equalsIgnoreCase("weighingScaleSeparator")) {
            if (value != null && value.equalsIgnoreCase("CRLF")) {
                updateConfigurationData(data, "CRLF", String.valueOf(amcuConfig.getWeighingSeperator()));
            } else {
                updateConfigurationData(data, value, String.valueOf(amcuConfig.getWeighingSeperator()));
            }

        } else if (data.equalsIgnoreCase("displayKgToDevice")) {
            updateConfigurationData(data, String.valueOf(value), String.valueOf(amcuConfig.getDisplayInKg()));
        } else if (data.equalsIgnoreCase("isRateChartInKg")) {
//
            if (value != null && value.equalsIgnoreCase("Kg")) {
                updateConfigurationData(data, String.valueOf(true), String.valueOf(amcuConfig.getKeyRateChartInKg()));
            } else {
                updateConfigurationData(data, String.valueOf(false), String.valueOf(amcuConfig.getKeyRateChartInKg()));
            }

        } else if (data.equalsIgnoreCase("weighingDivisionFactor")) {

            DecimalFormat decimalFormatConversion = new DecimalFormat("#0.00");
            value = Util.convertStringtoDecimal(value, decimalFormatConversion, "1");
            float val = Float.parseFloat(value);
            if (val > 0) {
                updateConfigurationData(data, String.valueOf(value), String.valueOf(amcuConfig.getWeighingDivisionFactor()));
            } else {
                Util.displayErrorToast("Invalid division factor for WS.", mContext);
            }

        } else if (data.equalsIgnoreCase("enableSalesFS")) {
            updateConfigurationData(data, String.valueOf(value), String.valueOf(amcuConfig.getSalesFSEnable()));

        } else if (data.equalsIgnoreCase("salesBuffRate")) {
            DecimalFormat decimalFormatConversion = new DecimalFormat("#0.00");
            value = Util.convertStringtoDecimal(value, decimalFormatConversion, "0");
            float val = Float.parseFloat(value);
            if (val > 0) {
                updateConfigurationData(data, value, String.valueOf(amcuConfig.getSalesBuffRate()));
            } else {
                Util.displayErrorToast("Invalid buffalo rate for sales.", mContext);
            }
        } else if (data.equalsIgnoreCase("salesCowRate")) {
            DecimalFormat decimalFormatConversion = new DecimalFormat("#0.00");
            value = Util.convertStringtoDecimal(value, decimalFormatConversion, "0");
            float val = Float.parseFloat(value);
            if (val > 0) {
                updateConfigurationData(data, value, String.valueOf(amcuConfig.getSalesCowRate()));
            } else {
                Util.displayErrorToast("Invalid cow rate for sales.", mContext);
            }
        }

//        else if(data.equalsIgnoreCase("isImportExcelSheetAccess"))
//        {
//            updateConfigurationData(data,value,String.valueOf(saveSession.getWeighingDivisionFactor()));
//            saveSession.setSendMailToConfigureEmailIDs(saveSession.getSendEmailToConfigureIDs(),Boolean.parseBoolean(value));
//        }

        else if (data.equalsIgnoreCase("enableSales")) {
            updateConfigurationData(data, String.valueOf(value), String.valueOf(amcuConfig.getEnableSales()));
        } else if (data.equalsIgnoreCase("enableBonusDisplayRDU")) {
            updateConfigurationData(data, String.valueOf(value), String.valueOf(amcuConfig.getEnableBonusRDU()));
        } else if (data.equalsIgnoreCase("enableIncentiveOnRdu")) {
            updateConfigurationData(data, String.valueOf(value), String.valueOf(amcuConfig.getEnableIncentiveRDU()));
        } else if (data.equalsIgnoreCase("enableIpTableRule")) {
            updateConfigurationData(data, String.valueOf(value), String.valueOf(amcuConfig.getIpTableRule()));
        } else if (data.equalsIgnoreCase("enableSalesFarmerId")) {
            updateConfigurationData(data, String.valueOf(value), String.valueOf(amcuConfig.getEnableFarmerIdForSales()));
        } else if (data.equalsIgnoreCase("urlHeader")) {
            updateConfigurationData(data, value, String.valueOf(amcuConfig.getURLHeader()));
        }
        // Incentive percentage
        else if (data.equalsIgnoreCase("incentivePercentage")) {
            DecimalFormat decimalFormatConversion = new DecimalFormat("#0.00");
            value = Util.convertStringtoDecimal(value, decimalFormatConversion, "-1");
            float val = Float.parseFloat(value);
            if (val > -1) {
                updateConfigurationData(data, String.valueOf(value), String.valueOf(amcuConfig.getIncentivePercentage()));
            } else {
                Util.displayErrorToast("Invalid incentive percentage.", mContext);
            }
        }
        //Enable incentive in report

        else if (data.equalsIgnoreCase("enableIncentiveInReport")) {
            updateConfigurationData(data, String.valueOf(value), String.valueOf(amcuConfig.getEnableIncentiveInReport()));
        }

        //Enable sequence number in report


        else if (data.equalsIgnoreCase("enableSequenceNumberReport")) {
            updateConfigurationData(data, String.valueOf(value), String.valueOf(amcuConfig.getEnableSequenceNumberInReport()));
        }
//Enable clr report
        else if (data.equalsIgnoreCase("enableClrInReport")) {
            updateConfigurationData(data, String.valueOf(value), String.valueOf(amcuConfig.getEnableClrInReport()));
        } else if (data.equalsIgnoreCase("enableTruckEntry")) {
            updateConfigurationData(data, String.valueOf(value), String.valueOf(amcuConfig.getEnableTruckEntry()));
        } else if (data.equalsIgnoreCase("enableCenterCollection")) {
            updateConfigurationData(data, String.valueOf(value), String.valueOf(amcuConfig.getEnableCenterCollection()));
        } else if (data.equalsIgnoreCase("enableDairyReport")) {
            updateConfigurationData(data, String.valueOf(value), String.valueOf(amcuConfig.getEnableDairyReport()));
        } else if (data.equalsIgnoreCase("enableFilledOrEmptyCans")) {
            updateConfigurationData(data, String.valueOf(value), String.valueOf(amcuConfig.getEnableFilledOrEmptyCans()));
        }

        //Tare command

        else if (data.equalsIgnoreCase("tareCommand")) {
            if (value != null) {
                updateConfigurationData(data, value, String.valueOf(amcuConfig.getTareCommand()));
            } else {

                updateConfigurationData(data, "T", String.valueOf(amcuConfig.getTareCommand()));
            }
        } else if (data.equalsIgnoreCase("selectRateChartType")) {
            updateConfigurationData(data, value, String.valueOf(amcuConfig.getRateChartType()));

        } else if (data.equalsIgnoreCase("enableBonusForPrint")) {
            updateConfigurationData(data, String.valueOf(value), String.valueOf(amcuConfig.getBonusEnableForPrint()));
        } else if (data.equalsIgnoreCase("enableEditableRate")) {
            updateConfigurationData(data, String.valueOf(value), String.valueOf(amcuConfig.getEditableRate()));
        } else if (data.equalsIgnoreCase("enableFarmerExportMail")) {
            updateConfigurationData(data, String.valueOf(value), String.valueOf(amcuConfig.getAllowMailExportedFarmer()));
        } else if (data.equalsIgnoreCase("enableMilkQuality")) {
            updateConfigurationData(data, String.valueOf(value), String.valueOf(amcuConfig.getAllowMilkquality()));
        }
        //As per milma requirement

        else if (data.equalsIgnoreCase("enableEquipmentBasedCollection")) {
            updateConfigurationData(data, String.valueOf(value), String.valueOf(amcuConfig.getAllowEquipmentBasedCollection()));
        }

        //To set amcu server
        else if (data.equalsIgnoreCase("setAmcuServer")) {
            updateConfigurationData(data, value, String.valueOf(amcuConfig.getTemporaryServer()));
        } else if (data.equalsIgnoreCase("allowNumberOfCansInReport")) {
            updateConfigurationData(data, String.valueOf(value), String.valueOf(amcuConfig.getAllowNumberOfCans()));
        } else if (data.equalsIgnoreCase("allowCollectionRouteInReport")) {
            updateConfigurationData(data, String.valueOf(value), String.valueOf(this.amcuConfig.getAllowRouteInReport()));
        } else if (data.equalsIgnoreCase("enableFatSnfViewForCollection")) {
            updateConfigurationData(data, value, String.valueOf(amcuConfig.getCollectionFATSNFCLR()));
        } else if (data.equalsIgnoreCase("enableFatClrViewForChilling")) {
            updateConfigurationData(data, value, String.valueOf(amcuConfig.getChillingFATSNFCLR()));
        } else if (data.equalsIgnoreCase("enableAllFatSnfClrView")) {
            updateConfigurationData(data, String.valueOf(value), String.valueOf(amcuConfig.getFATSNFCLR()));
        }

        //Added new configuration
        else if (data.equalsIgnoreCase("visiblityReportEditing")) {
            updateConfigurationData(data, value, String.valueOf(amcuConfig.getAllowVisibilityReportEditing()));
        } else if (data.equalsIgnoreCase("allowOperatorToReportEditing")) {
            updateConfigurationData(data, value, String.valueOf(amcuConfig.getPermissionToEditReport()));
        } else if (data.equalsIgnoreCase("numberOfEditableShift")) {
            if (validationHelper.validateNonZeroPositiveInteger(value)) {
                updateConfigurationData(data, value, String.valueOf(amcuConfig.getNumberShiftCanBeEdited()));
            }

        } else if (data.equalsIgnoreCase("visibilityCanToggling")) {
            updateConfigurationData(data, value, String.valueOf(amcuConfig.getAllowVisiblityForCanToggle()));
        } else if (data.equalsIgnoreCase("allowCanToggling")) {
            updateConfigurationData(data, value, String.valueOf(amcuConfig.getCansToggling()));
        } else if (data.equalsIgnoreCase("maxLimitEmptyCan")) {
            if (validationHelper.validateNonZeroPositiveInteger(value)) {
                updateConfigurationData(data, value, String.valueOf(amcuConfig.getMaxLimitEmptyCans()));
            }
        } else if (data.equalsIgnoreCase("minLimitFilledCan")) {
            if (validationHelper.validateNonZeroPositiveInteger(value)) {
                updateConfigurationData(data, value, String.valueOf(amcuConfig.getMinLimitFilledCans()));
            }
        }

        //Clr round off configuration

        else if (data.equalsIgnoreCase("clrRoundOffUpto")) {
            if (validationHelper.validateNonZeroPositiveInteger(value)) {
                updateConfigurationData(data, value, String.valueOf(amcuConfig.getClrRoundOffUpto()));
            }
        }
        // overruling the fcs setting in configutaion screen
        else if (data.equalsIgnoreCase("allowFSCinSMS")) {
            updateConfigurationData(data, value, String.valueOf(amcuConfig.getAllowFSCInSMS()));
        } else if (data.equalsIgnoreCase("allowFSCinPrint")) {
            updateConfigurationData(data, value, String.valueOf(amcuConfig.getAllowFSCInPrint()));
        } else if (data.equalsIgnoreCase("multipleMA")) {
            updateConfigurationData(data, value, String.valueOf(amcuConfig.getMultipleMA()));
        } else if (data.equalsIgnoreCase("customMyRateChart")) {
            updateConfigurationData(data, value, String.valueOf(amcuConfig.getMyRateChartEnable()));
        } else if (data.equalsIgnoreCase("allowMaxLimitFromRC")) {
            updateConfigurationData(data, value, String.valueOf(amcuConfig.getAllowMaxLimitFromRateChart()));
        } else if (data.equalsIgnoreCase("allowRoundOffFatAndSnf")) {
            updateConfigurationData(data, value, String.valueOf(amcuConfig.getRoundOffCheckFatAndSnf()));
        }

        //To set position for clr and Added water

        else if (data.equalsIgnoreCase("ekoMilkClrPosition")) {
            updateConfigurationData(data, value, String.valueOf(amcuConfig.getClrPosition()));
            //  saveSession.setClrPosition(Integer.valueOf(value));
        } else if (data.equalsIgnoreCase("ekoMilkAddedWaterPosition")) {
            updateConfigurationData(data, value, String.valueOf(amcuConfig.getAddedWaterPos()));
            //  saveSession.setAddedWaterPos(Integer.valueOf(value));
        } else if (data.equalsIgnoreCase("maxWeighingScaleLimit")) {
            updateConfigurationData(data, value, String.valueOf(amcuConfig.getMaxlimitOfWeight()));
            DecimalFormat decimalFormatConversion = new DecimalFormat("#0.0000");
            value = Util.convertStringtoDecimal(value, decimalFormatConversion, "1000");
            // saveSession.setMaxLimitOfWeight(Double.valueOf(value));
        } else if (data.equalsIgnoreCase("enableOrDisableIpTable")) {
            updateConfigurationData(data, value, String.valueOf(amcuConfig.getMobileData()));
            //  saveSession.setMobileDataEnable(Boolean.parseBoolean(value));
        } else if (data.equalsIgnoreCase("smartCCFeature")) {
            updateConfigurationData(data, String.valueOf(value), String.valueOf(amcuConfig.getSmartCCFeature()));
            //saveSession.setSmartCCFeature(Boolean.parseBoolean(value));
        } else if (data.equalsIgnoreCase("allowWSTare")) {
            updateConfigurationData(data, String.valueOf(value), String.valueOf(amcuConfig.getTareEnable()));
            //  saveSession.setTareEnable(Boolean.parseBoolean(value));
        } else if (data.equalsIgnoreCase("createMultipleUsers")) {
            updateConfigurationData(data, String.valueOf(value), String.valueOf(amcuConfig.getMultipleUser()));
            //saveSession.setMultipleUser(Boolean.parseBoolean(value));
        } else if (data.equalsIgnoreCase("materialCode")) {
            updateConfigurationData(data, String.valueOf(value), String.valueOf(amcuConfig.getMaterialCode()));
            // saveSession.setMaterialCode(value);
        } else if (data.equalsIgnoreCase("tareDeviationWeight")) {
            DecimalFormat decimalFormatConversion = new DecimalFormat("#0.0000");
            value = Util.convertStringtoDecimal(value, decimalFormatConversion, "0.10");
            updateConfigurationData(data, value, String.valueOf(amcuConfig.getDeviationWeight()));
            // saveSession.setDeviationWeight(Float.valueOf(value));
        } else if (data.equalsIgnoreCase("deviationAlertForWeight")) {
            updateConfigurationData(data, String.valueOf(value), String.valueOf(amcuConfig.getDeviationWeightAlert()));
            //saveSession.setDeviationWeightAlert(Boolean.parseBoolean(value));
        } else if (data.equalsIgnoreCase("isRateCalculatedFromDevice")) {
            updateConfigurationData(data, String.valueOf(value), String.valueOf(amcuConfig.getRateCalculatedFromDevice()));
            // saveSession.setRateCalculatedFromDevice(Boolean.parseBoolean(value));
        }

        //this is for milk lane phase 2

        else if (data.equalsIgnoreCase("ignoreZeroFatSNF")) {
            updateConfigurationData(data, String.valueOf(value), String.valueOf(amcuConfig.getKeyIgnoreZeroFatSnf()));
            //  saveSession.setKeyIgnoreZeroFatSnf(Boolean.parseBoolean(value));
        } else if (data.equalsIgnoreCase("enableCollectionContraints")) {
            updateConfigurationData(data, String.valueOf(value), String.valueOf(amcuConfig.getKeyEnableCollectionConstraints()));
            //  saveSession.setKeyEnableCollectionConstraints(Boolean.parseBoolean(value));
        } else if (data.equalsIgnoreCase("collectionStartMorningshift")) {
            isChangeCollectionTiming = true;
            if (value != null && value.contains(":")) {
                shiftEntity.morningCollectionStartTime = smartCCUtil.getTimeinLong(value);
            }

        } else if (data.equalsIgnoreCase("collectionEndMorningShift")) {
            isChangeCollectionTiming = true;
            if (value != null && value.contains(":")) {
                shiftEntity.morningCollectionEndTime = smartCCUtil.getTimeinLong(value);
            }
        } else if (data.equalsIgnoreCase("collectionStartEveningShift")) {
            isChangeCollectionTiming = true;
            if (value != null && value.contains(":")) {
                shiftEntity.eveningCollectionStartTime = smartCCUtil.getTimeinLong(value);
            }
        } else if (data.equalsIgnoreCase("collectionEndEveningShift")) {
            isChangeCollectionTiming = true;
            if (value != null && value.contains(":")) {
                shiftEntity.eveningCollectionEndtime = smartCCUtil.getTimeinLong(value);
            }
        }

        //to display amount rate and quantity As per Osam dairy requirement
        else if (data.equalsIgnoreCase("isDisplayAmount")) {
            updateConfigurationData(data, value, String.valueOf(amcuConfig.getKeyDisplayAmount()));
            // saveSession.setKeyDisplayAmount(Boolean.parseBoolean(value));
        } else if (data.equalsIgnoreCase("isDisplayQuantity")) {
            updateConfigurationData(data, value, String.valueOf(amcuConfig.getKeyDisplayQuantity()));
            //  saveSession.setKeyDisplayQuantity(Boolean.parseBoolean(value));
        } else if (data.equalsIgnoreCase("isDisplayRate")) {
            updateConfigurationData(data, value, String.valueOf(amcuConfig.getKeyDisplayRate()));
            //  saveSession.setKeyDisplayRate(Boolean.parseBoolean(value));
        } else if (data.equalsIgnoreCase("userConfig")) {
            UserManager userManager = new UserManager(mContext);
            userManager.afterGettingJSONArray(value);
        } else if (data.equalsIgnoreCase("weighingParity")) {
            updateConfigurationData(data, value, String.valueOf(amcuConfig.getWSParity()));
            // saveSession.setWSparityStopAndDataBits(value.trim(),saveSession.getWSStopBits()
            //       ,saveSession.getWSDataBits());
        } else if (data.equalsIgnoreCase("weighingDatbits")) {
            updateConfigurationData(data, value, String.valueOf(amcuConfig.getWSDataBits()));
            // saveSession.setWSparityStopAndDataBits(saveSession.getWSParity(),saveSession.getWSStopBits()
            //       ,value.trim());
        } else if (data.equalsIgnoreCase("weighingStopbits")) {
            updateConfigurationData(data, value, String.valueOf(amcuConfig.getWSStopBits()));
            //saveSession.setWSparityStopAndDataBits(saveSession.getWSParity(),value.trim()
            //      ,saveSession.getWSDataBits());
        } else if (data.equalsIgnoreCase("rduQuantityRoundOff")) {
            updateConfigurationData(data, value, String.valueOf(amcuConfig.getRDUWeightRoundOff()));
            //saveSession.setRDUWeightRoundOff(Boolean.parseBoolean(value));
        } else if (data.equalsIgnoreCase("snfOrClrForTanker")) {
            updateConfigurationData(data, value, String.valueOf(amcuConfig.getSNFOrCLRFromTanker()));
            amcuConfig.setSNFORCLRforTanker(value);
        } else if (data.equalsIgnoreCase(ConfigurationConstants.weightRecordLength)) {
            updateConfigurationData(data, value, String.valueOf(amcuConfig.getWeightRecordLenght()));
            //  saveSession.setWeightRecordLenght(Integer.parseInt(value.trim()));
        } else if (data.equalsIgnoreCase(AmcuConfig.KEY_IS_CLOUD_SUPPORT)) {
            updateConfigurationData(data, value, String.valueOf(amcuConfig.getKeyCloudSupport()));
            //  saveSession.setKeyCloudSupport(Boolean.parseBoolean(value.trim()));
        } else if (data.equalsIgnoreCase(AmcuConfig.KEY_UNSENT_ALERT_LIMIT)) {
            updateConfigurationData(data, value, String.valueOf(amcuConfig.getUnsentAlertLimit()));
            // saveSession.setUnsentAlertLimit(Integer.parseInt(value.trim()));
        } else if (data.equalsIgnoreCase(AmcuConfig.KEY_SHOW_UNSENT_ALERT)) {
            updateConfigurationData(data, value, String.valueOf(amcuConfig.getShowUnsentAlert()));
            amcuConfig.setShowUnsentAlert(Boolean.parseBoolean(value.trim()));
        } else if (data.equalsIgnoreCase(AmcuConfig.KEY_PERIODIC_DATA_SENT)) {
            updateConfigurationData(data, value, String.valueOf(amcuConfig.getKeyPeriodicDataSent()));
            //  saveSession.setKeyPeriodicDataSent(Integer.parseInt(value.trim()));
        } else if (data.equalsIgnoreCase(AmcuConfig.KEY_DATA_SENT_START_TIME)) {
            updateConfigurationData(data, value, String.valueOf(amcuConfig.getKeyDataSentStartTime()));
            //  saveSession.setKeyDataSentStartTime(Integer.parseInt(value.trim()));
        } else if (data.equalsIgnoreCase(AmcuConfig.KEY_DEFAULT_MILK_TYPE_BOTH)) {
            updateConfigurationData(data, value, String.valueOf(amcuConfig.getDefaultMilkTypeForBoth()));
            // saveSession.setDefaultMilkTypeForBoth(value.trim());
        } else if (data.equalsIgnoreCase(AmcuConfig.KEY_MIN_VALID_WEIGHT)) {
            DecimalFormat decimalFormatConversion = new DecimalFormat("#0.0000");
            value = Util.convertStringtoDecimal(value, decimalFormatConversion, "0");
            updateConfigurationData(data, value, String.valueOf(amcuConfig.getKeyMinValidWeight()));
            amcuConfig.setKeyMinValidWeight(Double.valueOf(value));
        } else if (data.equalsIgnoreCase(AmcuConfig.KEY_DELETE_COLL_SHIFT_RECORD)) {
            updateConfigurationData(data, value, String.valueOf(amcuConfig.getDeleteCollRecordAfterShift()));
            amcuConfig.setDeleteCollRecordAfterShift(Integer.parseInt(value.trim()));
        } else if (data.equalsIgnoreCase(AmcuConfig.KEY_DELETE_FILES_DAY)) {
            updateConfigurationData(data, value, String.valueOf(amcuConfig.getDeleteFilesAfterDays()));
            amcuConfig.setDeleteFilesAfterDays(Integer.parseInt(value.trim()));
        } else if (data.equalsIgnoreCase(AmcuConfig.KEY_ESCAPE_ENABLE_COLLECTION)) {
            updateConfigurationData(data, value, String.valueOf(amcuConfig.getKeyEscapeEnableCollection()));
            amcuConfig.setKeyEscapeEnableCollection(Boolean.parseBoolean(value.trim()));
        } else if (data.equalsIgnoreCase(AmcuConfig.KEY_DEVICE_Password)) {
            updateConfigurationData(data, value, String.valueOf(amcuConfig.getDevicePassword()));
        } else if (data.equalsIgnoreCase(ConfigurationConstants.ALLOW_EDIT_REPORT)) {
            updateConfigurationData(data, value, String.valueOf(amcuConfig.getAllowReportEdit()));
        } else if (data.equalsIgnoreCase(ConfigurationConstants.NUMBER_OF_EDITABLE_SHIFT)) {
            updateConfigurationData(data, value, String.valueOf(amcuConfig.getNumberShiftCanBeEdited()));
        } else if (data.equalsIgnoreCase(ConfigurationConstants.ADDITIONAL_PARAMS)) {
            setCustomFieldEntitiesFromJson(value);
        } else if (data.equalsIgnoreCase(AmcuConfig.KEY_DEVICE_Password)) {
            updateConfigurationData(data, value, String.valueOf(amcuConfig.getDevicePassword()));
        } else if (data.equalsIgnoreCase(AmcuConfig.KEY_ALLOW_FAMER_INCREMENT)) {
            updateConfigurationData(data, String.valueOf(value), String.valueOf(amcuConfig.getKeyAllowFarmerIncrement()));
            //  saveSession.setKeyAllowFormerIncrement(Boolean.parseBoolean(value));
        } else if (data.equalsIgnoreCase(ConfigurationConstants.KEY_MANDATORY_RATE_CHART)) {
            updateConfigurationData(data, value, String.valueOf(amcuConfig.getIsRateChartMandatory()));
            //  saveSession.setIsRateChartMandatory(Boolean.parseBoolean(value));
        } else if (data.equalsIgnoreCase("mrnStart")) {
            updateConfigurationData(data, value, String.valueOf(amcuConfig.getMorningStart()));
            //  saveSession.setIsRateChartMandatory(Boolean.parseBoolean(value));
        } else if (data.equalsIgnoreCase("evnStart")) {
            updateConfigurationData(data, value, String.valueOf(amcuConfig.getEveningStart()));
            //  saveSession.setIsRateChartMandatory(Boolean.parseBoolean(value));
        } else if (data.equalsIgnoreCase("enableFatClrViewForChilling")) {
            updateConfigurationData(data, String.valueOf(value), String.valueOf(amcuConfig.getChillingFATSNFCLR()));
            //  saveSession.setIsRateChartMandatory(Boolean.parseBoolean(value));
        } else if (data.equalsIgnoreCase(ConfigurationConstants.KEY_ALLOW_AGENT_SPLIT)) {
            updateConfigurationData(data, String.valueOf(value), String.valueOf(amcuConfig.getKeyAllowAgentFarmerCollection()));

        } else if (data.equalsIgnoreCase(ConfigurationConstants.ALLOW_EDIT_COLLECTION)) {
            updateConfigurationData(data, String.valueOf(value), String.valueOf(amcuConfig.getKeyAllowEditCollection()));
        } else if (data.equalsIgnoreCase(ConfigurationConstants.KEY_ALLOW_PROTEIN_VALUE)) {
            updateConfigurationData(data, String.valueOf(value), String.valueOf(amcuConfig.getKeyAllowProteinValue()));
        } else if (data.equalsIgnoreCase(ConfigurationConstants.KEY_ALLOW_TWO_DECIMAL)) {
            updateConfigurationData(data, String.valueOf(value), String.valueOf(amcuConfig.getKeyAllowTwoDeciaml()));
        }

        //FAT/SNF/CLR Configuration

        else if (data.equalsIgnoreCase(ConfigurationConstants.KEY_DISPLAY_FAT_CONFIGURATION)) {
            updateConfigurationData(data, String.valueOf(value),
                    String.valueOf(amcuConfig.getDisplayFATConfiguration()));
        } else if (data.equalsIgnoreCase(ConfigurationConstants.KEY_DISPLAY_SNF_CONFIGURATION)) {
            updateConfigurationData(data, String.valueOf(value),
                    String.valueOf(amcuConfig.getDisplaySNFConfiguration()));
        } else if (data.equalsIgnoreCase(ConfigurationConstants.KEY_DISPLAY_CLR_CONFIGURATION)) {
            updateConfigurationData(data, String.valueOf(value),
                    String.valueOf(amcuConfig.getDisplayCLRConfiguration()));
        } else if (data.equalsIgnoreCase(ConfigurationConstants.KEY_DISPLAY_PROTEIN_CONFIGURATION)) {
            updateConfigurationData(data, String.valueOf(value),
                    String.valueOf(amcuConfig.getDisplayProteinConfiguration()));
        } else if (data.equalsIgnoreCase(ConfigurationConstants.KEY_RATE_FAT_CONFIGURATION)) {
            updateConfigurationData(data, String.valueOf(value),
                    String.valueOf(amcuConfig.getRateFATConfiguration()));
        } else if (data.equalsIgnoreCase(ConfigurationConstants.KEY_RATE_SNF_CONFIGURATION)) {
            updateConfigurationData(data, String.valueOf(value),
                    String.valueOf(amcuConfig.getRateSNFConfiguration()));
        } else if (data.equalsIgnoreCase(ConfigurationConstants.KEY_RATE_CLR_CONFIGURATION)) {
            updateConfigurationData(data, String.valueOf(value),
                    String.valueOf(amcuConfig.getRateCLRConfiguration()));
        } else if (data.equalsIgnoreCase(ConfigurationConstants.KEY_RATE_PROTEIN_CONFIGURATION)) {
            updateConfigurationData(data, String.valueOf(value),
                    String.valueOf(amcuConfig.getRateProteinConfiguration()));
        } else if (data.equalsIgnoreCase(ConfigurationConstants.KEY_ROUND_FAT_CONFIGURATION)) {
            updateConfigurationData(data, String.valueOf(value),
                    String.valueOf(amcuConfig.getRoundFATConfiguration()));
        } else if (data.equalsIgnoreCase(ConfigurationConstants.KEY_ROUND_SNF_CONFIGURATION)) {
            updateConfigurationData(data, String.valueOf(value),
                    String.valueOf(amcuConfig.getRoundSNFConfiguration()));
        } else if (data.equalsIgnoreCase(ConfigurationConstants.KEY_ROUND_CLR_CONFIGURATION)) {
            updateConfigurationData(data, String.valueOf(value),
                    String.valueOf(amcuConfig.getRoundCLRConfiguration()));
        } else if (data.equalsIgnoreCase(ConfigurationConstants.KEY_ROUND_PROTEIN_CONFIGURATION)) {
            updateConfigurationData(data, String.valueOf(value),
                    String.valueOf(amcuConfig.getRoundProteinConfiguration()));
        } else if (data.equalsIgnoreCase(ConfigurationConstants.KEY_RDU_PASSWORD)) {
            updateConfigurationData(data, String.valueOf(value),
                    String.valueOf(amcuConfig.getRduPassword()));
        } else if (data.equalsIgnoreCase(ConfigurationConstants.KEY_HOTSPOT_SSID)) {
            updateConfigurationData(data, String.valueOf(value),
                    String.valueOf(amcuConfig.getHotspotSsid()));
        } else if (data.equalsIgnoreCase(ConfigurationConstants.KEY_ENABLE_HOTSPOT)) {
            updateConfigurationData(data, String.valueOf(value),
                    String.valueOf(amcuConfig.getHotspotValue()));
        } else if (data.equalsIgnoreCase(ConfigurationConstants.ENABLE_LACTOSE_BASED_REJECTION)) {
            updateConfigurationData(data, String.valueOf(value),
                    String.valueOf(amcuConfig.getLactoseBasedRejection()));
        } else if (data.equalsIgnoreCase(ConfigurationConstants.ENABLE_DYNAMIC_RATE_CHART)) {
            updateConfigurationData(data, String.valueOf(value),
                    amcuConfig.getDynamicRateChartValue());
        } else if (data.equalsIgnoreCase(ConfigurationConstants.KEY_MAX_LACTOSE_REJECT_COW)) {
            updateConfigurationData(data, String.valueOf(value),
                    String.valueOf(amcuConfig.getKeyMaxFatRejectCow()));
        } else if (data.equalsIgnoreCase(ConfigurationConstants.KEY_MAX_LACTOSE_REJECT_BUFF)) {
            updateConfigurationData(data, String.valueOf(value),
                    String.valueOf(amcuConfig.getKeyMaxFatRejectBuff()));
        } else if (data.equalsIgnoreCase(ConfigurationConstants.KEY_MAX_LACTOSE_REJECT_MIX)) {
            updateConfigurationData(data, String.valueOf(value),
                    String.valueOf(amcuConfig.getKeyMaxFatRejectMix()));
        } else if (data.equalsIgnoreCase(ConfigurationConstants.PERIODIC_DEVICE_DATA_SEND)) {
            updateConfigurationData(data, value, String.valueOf(amcuConfig.getPeriodicDeviceDataSend()));
        } else if (data.equalsIgnoreCase(ConfigurationConstants.FARM_ID_DIGIT)) {
            isFarmerUpdated = true;
            FARMER_ID_DIGIT = amcuConfig.getFarmerIdDigit();
            updateConfigurationData(ConfigurationConstants.FARM_ID_DIGIT
                    , value, String.valueOf(amcuConfig.getFarmerIdDigit()));
        } else if (data.equalsIgnoreCase(ConfigurationConstants.KEY_ALLOW_DISPLAY_RATE)) {
            updateConfigurationData(data, value, String.valueOf(amcuConfig.getKeyAllowDisplayRate()));
        } else if (data.equalsIgnoreCase(ConfigurationConstants.KEY_ALLOW_FARMER_CREATE)) {
            updateConfigurationData(data, value, String.valueOf(amcuConfig.getKeyAllowFarmerCreate()));
        } else if (data.equalsIgnoreCase(ConfigurationConstants.KEY_ALLOW_FARMER_EDIT)) {
            updateConfigurationData(data, value, String.valueOf(amcuConfig.getKeyAllowFarmerEdit()));
        } else if (data.equalsIgnoreCase(ConfigurationConstants.KEY_WS_IGNORE_THRESHOLD)) {
            updateConfigurationData(data, String.valueOf(value),
                    String.valueOf(amcuConfig.getWsIgnoreThreshold()));
        } else if (data.equalsIgnoreCase(ConfigurationConstants.KEY_WS_KG_COMMAND)) {
            updateConfigurationData(data, String.valueOf(value),
                    String.valueOf(amcuConfig.getWsKgCommand()));
        } else if (data.equalsIgnoreCase(ConfigurationConstants.KEY_WS_LITRE_COMMAND)) {
            updateConfigurationData(data, String.valueOf(value),
                    String.valueOf(amcuConfig.getWsLitreCommand()));
        } else if (data.equalsIgnoreCase(ConfigurationConstants.KEY_ALLOW_DISPLAY_NAME_IN_REPORT)) {
            updateConfigurationData(data, value, String.valueOf(amcuConfig.getDisplayNameInReport()));
        } else if (data.equalsIgnoreCase(ConfigurationConstants.KEY_DISPLAY_DAIRY_NAME)) {
            updateConfigurationData(data, value, String.valueOf(amcuConfig.getDisplayDairyName()));
        } else if (data.equalsIgnoreCase(ConfigurationConstants.ALLOW_APK_UPGRADE_FROM_FILE_SYSTEM)) {
            updateConfigurationData(data, value, String.valueOf(amcuConfig.getApkFromFileSystem()));
        }


    }

    public void setUserEntity(String user, String data, String value) {
        UserEntity userEnt = Util.getAndUpdateTheUserInfo(mContext, user, null, Util.GET_USER);

        if (data.equalsIgnoreCase("managerPassword") || data.equalsIgnoreCase("userPassword") ||
                data.equalsIgnoreCase("operatorPassword")) {
            userEnt.password = value;

            updateConfigurationData(data, value, userEnt.password);
            if (data.equalsIgnoreCase("managerPassword")) {
                amcuConfig.setKeyManagerPassword(value);
            } else {
                amcuConfig.setKeyOperatorPassword(value);
            }

        } else if (data.equalsIgnoreCase("managerEmailID")) {
            userEnt.emailId = value;
            updateConfigurationData(data, value, userEnt.emailId);

        } else if (data.equalsIgnoreCase("managerMobileNumber") || data.equalsIgnoreCase("operatorMobileNumber")) {
            userEnt.mobile = value;
            updateConfigurationData(data, value, userEnt.mobile);
        }

        Util.getAndUpdateTheUserInfo(mContext, null, userEnt, Util.UPDATE_USER);
    }

    public void setDataForSociety(String data, String value) {
        SocietyEntity socEntity = Util.getAndUpdateCenterInfo(mContext, null, Util.GET_SOCIETY);

        if (data.equalsIgnoreCase("centerName")) {
            socEntity.name = value;
            updateConfigurationData(data, value, socEntity.name);


        } else if (data.equalsIgnoreCase("centerCode")) {
            socEntity.socCode = value;
            updateConfigurationData(data, value, socEntity.socCode);

        } else if (data.equalsIgnoreCase("centerLocation")) {
            socEntity.location = value;
            updateConfigurationData(data, value, socEntity.location);

        } else if (data.equalsIgnoreCase("centerRoute")) {
            socEntity.route = value;
            updateConfigurationData(data, value, socEntity.route);

        } else if (data.equalsIgnoreCase("centerBMCID")) {
            socEntity.bmcId = value;
            updateConfigurationData(data, value, socEntity.bmcId);

        } else if (data.equalsIgnoreCase("centerAddress")) {
            socEntity.address = value;
            updateConfigurationData(data, value, socEntity.address);

        } else if (data.equalsIgnoreCase("centerContactPerson1")) {
            socEntity.conPerson1 = value;
            updateConfigurationData(data, value, socEntity.conPerson1);

        } else if (data.equalsIgnoreCase("centerContactPerson2")) {
            socEntity.conPerson2 = value;
            updateConfigurationData(data, value, socEntity.conPerson2);

        } else if (data.equalsIgnoreCase("centerContactEmail1")) {
            socEntity.socEmail1 = value;
            updateConfigurationData(data, value, socEntity.socEmail1);

        } else if (data.equalsIgnoreCase("centerContactEmail2")) {
            socEntity.socEmail2 = value;
            updateConfigurationData(data, value, socEntity.socEmail2);

        } else if (data.equalsIgnoreCase("centerContactMobile1")) {
            socEntity.contactNum1 = value;
            updateConfigurationData(data, value, socEntity.contactNum1);

        } else if (data.equalsIgnoreCase("centerContactMobile2")) {
            socEntity.contactNum2 = value;
            updateConfigurationData(data, value, socEntity.contactNum2);

        }
        Util.getAndUpdateCenterInfo(mContext, socEntity, Util.UPDATE_SOCIETY);
    }

    public void setDataInSessionManager(String data, String value) {

        if (data.equalsIgnoreCase("morningSessionStartTime")) {
            updateConfigurationData(data, value, amcuConfig.getMorningStart());
            amcuConfig.setMorningStart(value);

        } else if (data.equalsIgnoreCase("eveningSessionStartTime")) {
            updateConfigurationData(data, value, amcuConfig.getEveningStart());
            amcuConfig.setEveningStart(value);
        } else if (data.equalsIgnoreCase("morningSessionEndTime")) {
            updateConfigurationData(data, value, amcuConfig.getMorningEndTime());
            amcuConfig.setMorningEndTime(value);
        } else if (data.equalsIgnoreCase("eveningSessionEndTime")) {
            updateConfigurationData(data, value, amcuConfig.getEveningEndTime());
            amcuConfig.setEveningEndTime(value);
        } else if (data.equalsIgnoreCase("perDaySMSLimit")) {
            updateConfigurationData(data, value, String.valueOf(session.getPerDayMessageLimit()));
            session.setPerDayMessageLimit(Integer.parseInt(value));
        }
    }


    public String getDouble(String value) {
        String retValue = "0.0";
        DecimalFormat decimalFormat = new DecimalFormat("#0.0");
        try {
            retValue = decimalFormat.format(Double.parseDouble(value));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return retValue;

    }

    public float getFloatRate(String str) {
        try {
            float f = Float.parseFloat(str);
            return f;
        } catch (NumberFormatException e) {

            e.printStackTrace();
            return 0.0f;
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0f;
        }
    }

    public boolean validateBonusDate() {

        // Not require as per new bonus requirement

//         long lnToday = Util.getLong(Util.getTodayDateAndTime(7));
//
//        if(bonusEndDate==null)
//        {
//            bonusEndDate=saveSession.getBonusEndDate();
//        }
//        if(bonusStartDate==null)
//        {
//            bonusStartDate=saveSession.getBonusStartDate();
//        }
//
//        if(bonus==null)
//        {
//            try {
//                bonus=String.valueOf(saveSession.getBonusRate());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//         long startDate=0,endDate= 0;
//         try {
//             startDate=Util.getLong(bonusStartDate);
//             endDate = Util.getLong(bonusEndDate);
//         } catch (Exception e) {
//
//             Util.displayErrorToast("Invalid bonus start or end date!", mContext);
//             e.printStackTrace();
//             return false;
//         }
//
//         if(startDate<=0)
//         {
//             Util.displayErrorToast("Invalid bonus start date!",mContext);
//             return false;
//         }
//
//        else if(endDate>=lnToday&&endDate>=startDate)
//         {
//
//             float bounsRate=getFloatRate(bonus);
//             if(bounsRate>0)
//             {
//                 return true;
//             }
//             else
//             {
//                 Util.displayErrorToast("Bouns should be greator than 0.",mContext);
//                 return false;
//             }
//         }
//
//         else
//         {
//             Util.displayErrorToast("Invalid bonus start or end date!",mContext);
//             return false;
//         }

        return true;

    }


    public void setBonusDetails() {
        updateConfigurationData(ConfigurationConstants.bonusStartDate,
                bonusStartDate, amcuConfig.getBonusStartDate());
        updateConfigurationData(ConfigurationConstants.bonusEndDate,
                bonusEndDate, amcuConfig.getBonusEndDate());
        updateConfigurationData(ConfigurationConstants.bonus,
                bonus, String.valueOf(amcuConfig.getBonusRate()));


        amcuConfig.setBonusDetails(bonusStartDate,
                bonusEndDate, getFloatRate(bonus),
                amcuConfig.getBonusEnable(), amcuConfig.getOperatorRateAccess());

    }

    public ShiftEntity initializeShiftEntity() {
        shiftEntity.morningStartTime = smartCCUtil.getTimeinLong(amcuConfig.getMorningStart());
        shiftEntity.eveningStartTime = smartCCUtil.getTimeinLong(amcuConfig.getEveningStart());
        shiftEntity.morningCollectionStartTime = smartCCUtil.getTimeinLong(amcuConfig.getKeyCollStartMrnShift());
        shiftEntity.morningCollectionEndTime = smartCCUtil.getTimeinLong(amcuConfig.getKeyCollEndMrnShift());
        shiftEntity.eveningCollectionStartTime = smartCCUtil.getTimeinLong(amcuConfig.getKeyCollStartEvnShift());
        shiftEntity.eveningCollectionEndtime = smartCCUtil.getTimeinLong(amcuConfig.getKeyCollEndEvnShift());
        return shiftEntity;
    }


    public ConfigurationEntity addConfiguration() {

        ConfigurationEntity configurationEntity = new ConfigurationEntity();

        configurationEntity.acceptMilkInKgFormat = String.valueOf(amcuConfig.getAllowInKgformat());
        configurationEntity.allowSimlock = String.valueOf(amcuConfig.getAllowSimlockPassword());
        configurationEntity.amountRoundOff = String.valueOf(amcuConfig.getDecimalRoundOffAmount());
        configurationEntity.allowCanToggling = String.valueOf(amcuConfig.getCansToggling());
        configurationEntity.allowCollectionRouteInReport = String.valueOf(amcuConfig.getAllowRouteInReport());
        configurationEntity.allowFSCinPrint = String.valueOf(amcuConfig.getAllowFSCInPrint());
        configurationEntity.allowFSCinSMS = String.valueOf(amcuConfig.getAllowFSCInSMS());
        configurationEntity.allowMaxLimitFromRC = String.valueOf(amcuConfig.getAllowMaxLimitFromRateChart());
        configurationEntity.allowNumberOfCansInReport = String.valueOf(amcuConfig.getAllowNumberOfCans());
        configurationEntity.allowOperatorToReportEditing = String.valueOf(amcuConfig.getPermissionToEditReport());
        configurationEntity.allowSimlock = String.valueOf(amcuConfig.getAllowSimlockPassword());
        configurationEntity.allowRoundOffFatAndSnf = String.valueOf(amcuConfig.getRoundOffCheckFatAndSnf());
        configurationEntity.allowWSTare = String.valueOf(amcuConfig.getTareEnable());
        configurationEntity.amountRoundOffCheck = String.valueOf(amcuConfig.getDecimalRoundOffAmountCheck());
        configurationEntity.bonus = String.valueOf(amcuConfig.getBonusRate());
        configurationEntity.bonusEndDate = String.valueOf(amcuConfig.getBonusEndDate());
        configurationEntity.bonusStartDate = String.valueOf(amcuConfig.getBonusEndDate());

        configurationEntity.changeMilktypeBuffalo = String.valueOf(amcuConfig.getChangeFat());
        configurationEntity.changeSnfMilkTypeBuffalo = String.valueOf(amcuConfig.getChangeSnf());

        configurationEntity.clrRoundOffUpto = String.valueOf(amcuConfig.getClrRoundOffUpto());
        configurationEntity.collectionEndEveningShift = String.valueOf(amcuConfig.getKeyCollEndEvnShift());
        configurationEntity.collectionEndMorningShift = amcuConfig.getKeyCollEndMrnShift();
        configurationEntity.collectionStartMorningshift = amcuConfig.getKeyCollStartMrnShift();
        configurationEntity.collectionStartEveningShift = amcuConfig.getKeyCollStartEvnShift();
        configurationEntity.collectionSMSFooter = amcuConfig.getCollectionSMSFooter();

        configurationEntity.constantForClr = String.valueOf(amcuConfig.getConstant());
        configurationEntity.conversionFactor = String.valueOf(amcuConfig.getConversionFactor());
        configurationEntity.createMultipleUsers = String.valueOf(amcuConfig.getMultipleUser());
        configurationEntity.customMyRateChart = String.valueOf(amcuConfig.getMyRateChartEnable());

        configurationEntity.deviationAlertForWeight = String.valueOf(amcuConfig.getDeviationWeightAlert());
        configurationEntity.displayKgToDevice = String.valueOf(amcuConfig.getDisplayInKg());
        configurationEntity.ekoMilkAddedWaterPosition = String.valueOf(amcuConfig.getAddedWaterPos());
        configurationEntity.ekoMilkClrPosition = String.valueOf(amcuConfig.getClrPosition());
        configurationEntity.enableAllFatSnfClrView = String.valueOf(amcuConfig.getFATSNFCLR());
        configurationEntity.enableBonusDisplayRDU = String.valueOf(amcuConfig.getEnableBonusRDU());
        configurationEntity.enableBonusForPrint = String.valueOf(amcuConfig.getBonusEnableForPrint());
        configurationEntity.enableBonusToDevice = String.valueOf(amcuConfig.getBonusEnable());
        configurationEntity.enableCenterCollection = String.valueOf(amcuConfig.getEnableCenterCollection());
        configurationEntity.enableClrInReport = String.valueOf(amcuConfig.getEnableClrInReport());
        configurationEntity.enableCollectionContraints = String.valueOf(amcuConfig.getKeyEnableCollectionConstraints());
        configurationEntity.enableConfigurableFarmerIdSize = String.valueOf(amcuConfig.getEnableFarmerLenghtConfiguration());
        configurationEntity.enableConversionFactor = String.valueOf(amcuConfig.getEnableConversionFactorDisplay());
        configurationEntity.enableDairyReport = String.valueOf(amcuConfig.getEnableDairyReport());
        configurationEntity.enableEditableRate = String.valueOf(amcuConfig.getEditableRate());
        configurationEntity.enableEquipmentBasedCollection = String.valueOf(amcuConfig.getAllowEquipmentBasedCollection());
        configurationEntity.enableFarmerExportMail = String.valueOf(amcuConfig.getAllowMailExportedFarmer());
        configurationEntity.enableFatClrViewForChilling = String.valueOf(amcuConfig.getChillingFATSNFCLR());
        configurationEntity.enableFilledOrEmptyCans = String.valueOf(amcuConfig.getEnableFilledOrEmptyCans());
        configurationEntity.enableIncentiveInReport = String.valueOf(amcuConfig.getEnableIncentiveInReport());
        configurationEntity.enableIncentiveOnRdu = String.valueOf(amcuConfig.getEnableIncentiveRDU());
        configurationEntity.enableIpTableRule = String.valueOf(amcuConfig.getIpTableRule());
        configurationEntity.enableManualToDevice = String.valueOf(amcuConfig.getManualDisplay());
        configurationEntity.enableMilkQuality = String.valueOf(amcuConfig.getAllowMilkquality());
        configurationEntity.enableMultipleCollection = String.valueOf(amcuConfig.getAllowMultipeCollection());
        configurationEntity.enableRejectToDevice = String.valueOf(amcuConfig.getEnableRejectForRateChart());
        configurationEntity.enableSales = String.valueOf(amcuConfig.getEnableSales());
        configurationEntity.enableSMS = String.valueOf(amcuConfig.getAllowSMS());
        configurationEntity.enableSalesFarmerId = String.valueOf(amcuConfig.getEnableFarmerIdForSales());
        configurationEntity.enableSalesFS = String.valueOf(amcuConfig.getSalesFSEnable());
        configurationEntity.enableSimlockToDevice = String.valueOf(amcuConfig.getSimlockDisplay());
        configurationEntity.enableSequenceNumberReport = String.valueOf(amcuConfig.getEnableSequenceNumberInReport());
        configurationEntity.enableTruckEntry = String.valueOf(amcuConfig.getEnableTruckEntry());


        configurationEntity.fatConstantForClr = String.valueOf(amcuConfig.getFatCons());
        configurationEntity.ignoreZeroFatSNF = String.valueOf(amcuConfig.getKeyIgnoreZeroFatSnf());
        configurationEntity.incentivePercentage = String.valueOf(amcuConfig.getIncentivePercentage());
        configurationEntity.isAddedWater = String.valueOf(amcuConfig.getIsAddedWater());
        configurationEntity.isBonusEnable = String.valueOf(amcuConfig.getBonusEnable());

        configurationEntity.isDisplayAmount = String.valueOf(amcuConfig.getKeyDisplayAmount());
        configurationEntity.isDisplayQuantity = String.valueOf(amcuConfig.getKeyDisplayQuantity());
        configurationEntity.isDisplayRate = String.valueOf(amcuConfig.getKeyDisplayRate());
        configurationEntity.isImportExcelSheetAccess = String.valueOf(amcuConfig.getImportExcelRateEnable());
        configurationEntity.isMaManual = String.valueOf(amcuConfig.getMaManual());
        configurationEntity.isRateCalculatedFromDevice = String.valueOf(amcuConfig.getRateCalculatedFromDevice());
        configurationEntity.isRateChartInKg = String.valueOf(amcuConfig.getKeyRateChartInKg());
        configurationEntity.isRateEditCheck = String.valueOf(amcuConfig.getEditableRate());
        configurationEntity.isRateImportAccessOperator = String.valueOf(amcuConfig.getOperatorRateAccess());
        configurationEntity.isWsManual = String.valueOf(amcuConfig.getWsManual());

        configurationEntity.materialCode = String.valueOf(amcuConfig.getMaterialCode());
        configurationEntity.maxLimitEmptyCan = String.valueOf(amcuConfig.getMaxLimitEmptyCans());
        configurationEntity.milkAnalyzerBaudrate = String.valueOf(amcuConfig.getMABaudRate());
        configurationEntity.milkAnalyzerName = String.valueOf(amcuConfig.getMA());
        // configurationEntity.milkType = saveSession.getMilkType();
        configurationEntity.minLimitFilledCan = String.valueOf(amcuConfig.getMinLimitFilledCans());

        configurationEntity.multipleMA = String.valueOf(amcuConfig.getMultipleMA());

        configurationEntity.numberOfEditableShift = String.valueOf(amcuConfig.getNumberShiftCanBeEdited());
        configurationEntity.perDaySMSLimit = String.valueOf(session.getPerDayMessageLimit());
        configurationEntity.printerBaudrate = String.valueOf(amcuConfig.getPrinterBaudRate());
        configurationEntity.printerName = String.valueOf(amcuConfig.getPrinter());
        configurationEntity.rateChart = String.valueOf(amcuConfig.getRateChartName());
        configurationEntity.rateRoundOff = String.valueOf(amcuConfig.getDecimalRoundOffRate());
        configurationEntity.rateRoundOffCheck = String.valueOf(amcuConfig.getDecimalRoundOffRateCheck());
        configurationEntity.rduBaudrate = String.valueOf(amcuConfig.getRdubaudrate());
        configurationEntity.rduName = String.valueOf(amcuConfig.getRDU());

        configurationEntity.salesBuffRate = String.valueOf(amcuConfig.getSalesBuffRate());
        configurationEntity.salesCowRate = String.valueOf(amcuConfig.getSalesCowRate());
        configurationEntity.secondMABaudrate = String.valueOf(amcuConfig.getSecondMilkBaud());
        configurationEntity.secondMAType = String.valueOf(amcuConfig.getSecondMilkAnalyser());
        configurationEntity.selectRateChartType = String.valueOf(amcuConfig.getRateChartType());
        configurationEntity.sendShiftMails = String.valueOf(amcuConfig.getSendEmailToConfigureIDs());
        configurationEntity.setAmcuServer = String.valueOf(amcuConfig.getServer());
        configurationEntity.shutDownDelay = String.valueOf(amcuConfig.getShutDownDelay());
        configurationEntity.simlockPassword = String.valueOf(amcuConfig.getSimUnlockPassword());
        configurationEntity.smartAmcuEmail = String.valueOf(amcuConfig.getClientConfiguredEmail());
        configurationEntity.smartAmcuPassword = String.valueOf(amcuConfig.getDevicePassword());
        configurationEntity.smartCCFeature = String.valueOf(amcuConfig.getSmartCCFeature());
        configurationEntity.smsFooter = String.valueOf(amcuConfig.getSMSFooter());
        configurationEntity.snfConstantForClr = String.valueOf(amcuConfig.getSnfCons());

        configurationEntity.tareCommand = String.valueOf(amcuConfig.getTareCommand());
        configurationEntity.tareDeviationWeight = String.valueOf(amcuConfig.getDeviationWeight());

        configurationEntity.urlHeader = String.valueOf(amcuConfig.getURLHeader());

        configurationEntity.visibilityCanToggling = String.valueOf(amcuConfig.getAllowVisiblityForCanToggle());
        configurationEntity.visiblityReportEditing = String.valueOf(amcuConfig.getAllowVisibilityReportEditing());
        configurationEntity.weighingDatbits = String.valueOf(amcuConfig.getWSDataBits());
        configurationEntity.weighingDivisionFactor = String.valueOf(amcuConfig.getWeighingDivisionFactor());
        configurationEntity.weighingParity = String.valueOf(amcuConfig.getWSParity());
        configurationEntity.weighingScale = String.valueOf(amcuConfig.getWeighingScale());
        configurationEntity.weighingScalePrefix = String.valueOf(amcuConfig.getWeighingPrefix());
        configurationEntity.weighingScaleSeparator = String.valueOf(amcuConfig.getWeighingSeperator());
        configurationEntity.weighingScaleSuffix = String.valueOf(amcuConfig.getWeighingSuffix());
        configurationEntity.weighingStopbits = String.valueOf(amcuConfig.getWSStopBits());
        configurationEntity.weightRoundOff = String.valueOf(amcuConfig.getDecimalRoundOffWeigh());
        configurationEntity.weightRoundOffCheck = String.valueOf(amcuConfig.getDecimalRoundOffRateCheck());

        configurationEntity.maxWeighingScaleLimit = String.valueOf(amcuConfig.getMaxlimitOfWeight());
        configurationEntity.WeighingBaudrate = String.valueOf(amcuConfig.getWeighingbaudrate());
        configurationEntity.enableFatSnfViewForCollection = String.valueOf(amcuConfig.getCollectionFATSNFCLR());

        DatabaseHandler dbh = DatabaseHandler.getDatabaseInstance();

        //Add manager details
        UserEntity userEnt = dbh.getUserByUserId(UserDetails.MANAGER);

        configurationEntity.managerEmailID = userEnt.emailId;
        configurationEntity.managerMobileNumber = userEnt.mobile;
        configurationEntity.managerPassword = userEnt.password;

        //Add operator details
        userEnt = dbh.getUserByUserId("SMARTOPERATOR");

        configurationEntity.operatorMobileNumber = userEnt.mobile;
        configurationEntity.operatorPassword = userEnt.password;


        //Add center details


        SocietyEntity centerEntity = dbh.getSocietyDetails(0);

        configurationEntity.centerAddress = centerEntity.address;
        configurationEntity.centerCode = centerEntity.socCode;
        configurationEntity.centerBMCID = centerEntity.bmcId;
        configurationEntity.centerContactEmail1 = centerEntity.socEmail1;
        configurationEntity.centerContactEmail2 = centerEntity.socEmail2;
        configurationEntity.centerContactMobile1 = centerEntity.contactNum1;
        configurationEntity.centerContactMobile2 = centerEntity.contactNum2;
        configurationEntity.centerContactPerson1 = centerEntity.conPerson1;
        configurationEntity.centerContactPerson2 = centerEntity.conPerson2;
        configurationEntity.centerRoute = centerEntity.route;
        configurationEntity.centerLocation = centerEntity.location;
        configurationEntity.centerName = centerEntity.name;

        configurationEntity.enableOrDisableIpTable = String.valueOf(amcuConfig.getMobileData());
        configurationEntity.rduQuantityRoundOff = String.valueOf(amcuConfig.getRDUWeightRoundOff());
        configurationEntity.snfOrClrForTanker = String.valueOf(amcuConfig.getSNFOrCLRFromTanker());
        configurationEntity.weightRecordLength = String.valueOf(amcuConfig.getWeightRecordLenght());

        configurationEntity.isCloudSupport = String.valueOf(amcuConfig.getKeyCloudSupport());
        configurationEntity.unsentAlertLimit = String.valueOf(amcuConfig.getUnsentAlertLimit());
        configurationEntity.showUnsentAlert = String.valueOf(amcuConfig.getShowUnsentAlert());
        configurationEntity.periodicDataSent = String.valueOf(amcuConfig.getKeyPeriodicDataSent());
        configurationEntity.startTimeDataSent = String.valueOf(amcuConfig.getKeyDataSentStartTime());
        configurationEntity.defalutMilkTypeBoth = String.valueOf(amcuConfig.getDefaultMilkTypeForBoth());
        configurationEntity.minValidWeightForCollection = String.valueOf(amcuConfig.getKeyMinValidWeight());

        configurationEntity.morningSessionEndTime = amcuConfig.getMorningEndTime();
        configurationEntity.morningSessionStartTime = amcuConfig.getMorningStart();
        configurationEntity.eveningSessionEndTime = String.valueOf(amcuConfig.getEveningEndTime());
        configurationEntity.eveningSessionStartTime = String.valueOf(amcuConfig.getEveningStart());

        configurationEntity.allowAgentSplit = String.valueOf(amcuConfig.getKeyAllowAgentFarmerCollection());
        configurationEntity.allowEditCollection = String.valueOf(amcuConfig.getKeyAllowEditCollection());

        configurationEntity.ma1stopbits = String.valueOf(amcuConfig.getMa1StopBits());
        configurationEntity.ma1Databits = String.valueOf(amcuConfig.getMa1DataBits());

        return configurationEntity;

    }


    public void addConfigurationPushList() {
        ArrayList<ConfigurationElement> allConfigPushList = getAllConfigPushList(addConfiguration());
        DatabaseHandler dbh = DatabaseHandler.getDatabaseInstance();
        dbh.insertConfigurationEntity(allConfigPushList, DatabaseHandler.isPrimary);
        dbh.insertConfigurationEntity(allConfigPushList, DatabaseHandler.isSecondary);
    }

    /**
     * To get and add the data first time all the old value will be consider as null
     *
     * @param configEntity
     * @return
     */

    public ArrayList<ConfigurationElement> getAllConfigPushList(ConfigurationEntity configEntity) {
        ArrayList<ConfigurationElement> allConfigPushList = new ArrayList<>();

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.acceptMilkInKgFormat,
                configEntity.acceptMilkInKgFormat, null));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.allowCanToggling,
                configEntity.allowCanToggling, null));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.allowCollectionRouteInReport,
                configEntity.allowCollectionRouteInReport, null));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.amountRoundOff,
                configEntity.amountRoundOff, null));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.allowFSCinPrint,
                configEntity.allowFSCinPrint, null));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.allowFSCinSMS,
                configEntity.allowFSCinSMS, null));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.allowMaxLimitFromRC,
                configEntity.allowMaxLimitFromRC, null));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.allowNumberOfCansInReport,
                configEntity.allowNumberOfCansInReport, null));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.allowOperatorToReportEditing,
                configEntity.allowOperatorToReportEditing, null));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.allowRoundOffFatAndSnf,
                configEntity.allowRoundOffFatAndSnf, null));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.allowSimlock,
                configEntity.allowSimlock, null));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.allowWSTare,
                configEntity.allowWSTare, null));


        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.bonus, configEntity.bonus, null));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.bonusEndDate, configEntity.bonusEndDate, null));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.bonusStartDate,
                configEntity.bonusStartDate, null));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.changeMilktypeBuffalo,
                configEntity.changeMilktypeBuffalo, null));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.changeSnfMilkTypeBuffalo,
                configEntity.changeSnfMilkTypeBuffalo, null));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.clrRoundOffUpto, configEntity.clrRoundOffUpto, null));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.collectionEndEveningShift
                , configEntity.collectionEndEveningShift, null));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.collectionEndMorningShift
                , configEntity.collectionEndMorningShift, null));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.collectionSMSFooter
                , configEntity.collectionSMSFooter, null));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.collectionStartEveningShift
                , configEntity.collectionStartEveningShift, null));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.collectionStartMorningshift
                , configEntity.collectionStartMorningshift, null));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.constantForClr
                , configEntity.constantForClr, null));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.conversionFactor
                , configEntity.conversionFactor, null));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.createMultipleUsers
                , configEntity.createMultipleUsers, null));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.customMyRateChart
                , configEntity.customMyRateChart, null));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.deviationAlertForWeight
                , configEntity.deviationAlertForWeight, null));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.displayKgToDevice
                , configEntity.displayKgToDevice, null));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.ekoMilkAddedWaterPosition
                , configEntity.ekoMilkAddedWaterPosition, null));


        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.enableBonusDisplayRDU
                , configEntity.enableBonusDisplayRDU, null));


        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.enableConfigurableFarmerIdSize
                , configEntity.enableConfigurableFarmerIdSize, null));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.enableConversionFactor
                , configEntity.enableConversionFactor, null));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.ekoMilkClrPosition
                , configEntity.ekoMilkClrPosition, null));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.enableAllFatSnfClrView
                , configEntity.enableAllFatSnfClrView, null));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.enableBonusForPrint
                , configEntity.enableBonusForPrint, null));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.enableBonusToDevice
                , configEntity.enableBonusToDevice, null));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.enableClrInReport
                , configEntity.enableClrInReport, null));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.enableCollectionContraints
                , configEntity.enableCollectionContraints, null));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.enableCenterCollection
                , configEntity.enableCenterCollection, null));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.enableDairyReport
                , configEntity.enableDairyReport, null));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.enableEditableRate
                , configEntity.enableEditableRate, null));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.enableEquipmentBasedCollection
                , configEntity.enableEquipmentBasedCollection, null));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.enableFarmerExportMail
                , configEntity.enableFarmerExportMail, null));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.enableFatClrViewForChilling
                , configEntity.enableFatClrViewForChilling, null));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.enableFatSnfViewForCollection
                , configEntity.enableFatSnfViewForCollection, null));


        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.enableIncentiveInReport
                , configEntity.enableIncentiveInReport, null));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.enableIncentiveOnRdu
                , configEntity.enableIncentiveOnRdu, null));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.enableIpTableRule
                , configEntity.enableIpTableRule, null));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.enableManualToDevice
                , configEntity.enableManualToDevice, null));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.enableMilkQuality
                , configEntity.enableMilkQuality, null));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.enableMultipleCollection
                , configEntity.enableMultipleCollection, null));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.enableSales
                , configEntity.enableSales, null));


        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.enableSalesFarmerId
                , configEntity.enableSalesFarmerId, null));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.enableSalesFS
                , configEntity.enableSalesFS, null));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.enableSMS
                , configEntity.enableSMS, null));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.enableTruckEntry
                , configEntity.enableTruckEntry, null));


        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.fatConstantForClr
                , configEntity.fatConstantForClr, null));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.isAddedWater
                , configEntity.isAddedWater, null));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.isMaManual,
                configEntity.isMaManual, null));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.isWsManual,
                configEntity.isWsManual, null));


        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.isBonusEnable
                , configEntity.isBonusEnable, null));


        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.isDisplayAmount,
                configEntity.isDisplayAmount, null));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.isDisplayQuantity,
                configEntity.isDisplayQuantity, null));


        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.isDisplayRate
                , configEntity.isDisplayRate, null));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.isImportExcelSheetAccess
                , configEntity.isImportExcelSheetAccess, null));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.isRateImportAccessOperator,
                configEntity.isRateImportAccessOperator, null));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.isRateChartInKg,
                configEntity.isRateChartInKg, null));


        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.isRateEditCheck
                , configEntity.isRateEditCheck, null));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.materialCode
                , configEntity.materialCode, null));


        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.maxLimitEmptyCan
                , configEntity.maxLimitEmptyCan, null));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.milkAnalyzerBaudrate
                , configEntity.milkAnalyzerBaudrate, null));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.milkAnalyzerName,
                configEntity.milkAnalyzerName, null));
//        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.milkType,
//                configEntity.milkType,null));


        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.minLimitFilledCan
                , configEntity.minLimitFilledCan, null));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.multipleMA
                , configEntity.multipleMA, null));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.numberOfEditableShift,
                configEntity.numberOfEditableShift, null));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.perDaySMSLimit,
                configEntity.perDaySMSLimit, null));


        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.printerBaudrate
                , configEntity.printerBaudrate, null));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.printerName
                , configEntity.printerName, null));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.rateChart,
                configEntity.rateChart, null));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.rateRoundOff,
                configEntity.rateRoundOff, null));


        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.rateRoundOffCheck
                , configEntity.rateRoundOffCheck, null));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.rduBaudrate
                , configEntity.rduBaudrate, null));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.rduName,
                configEntity.rduName, null));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.secondMAType,
                configEntity.secondMAType, null));


        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.salesBuffRate
                , configEntity.salesBuffRate, null));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.salesCowRate
                , configEntity.salesCowRate, null));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.secondMABaudrate,
                configEntity.secondMABaudrate, null));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.sendShiftMails,
                configEntity.sendShiftMails, null));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.selectRateChartType
                , configEntity.selectRateChartType, null));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.setAmcuServer,
                configEntity.setAmcuServer, null));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.smartAmcuEmail
                , configEntity.smartAmcuEmail, null));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.tareCommand
                , configEntity.tareCommand, null));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.tareDeviationWeight,
                configEntity.tareDeviationWeight, null));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.urlHeader,
                configEntity.urlHeader, null));


        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.userConfig
                , configEntity.userConfig, null));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.visiblityReportEditing
                , configEntity.visiblityReportEditing, null));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.visibilityCanToggling,
                configEntity.visibilityCanToggling, null));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.weighingScale,
                configEntity.weighingScale, null));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.weighingDatbits
                , configEntity.weighingDatbits, null));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.weighingDivisionFactor
                , configEntity.weighingDivisionFactor, null));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.weighingScalePrefix,
                configEntity.weighingScalePrefix, null));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.weighingParity,
                configEntity.weighingParity, null));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.weighingScaleSeparator
                , configEntity.weighingScaleSeparator, null));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.weighingScaleSuffix
                , configEntity.weighingScaleSuffix, null));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.weightRoundOff,
                configEntity.weightRoundOff, null));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.weightRoundOffCheck,
                configEntity.weightRoundOffCheck, null));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.weighingStopbits,
                configEntity.weighingStopbits, null));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.ignoreZeroFatSNF
                , configEntity.ignoreZeroFatSNF, null));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.managerMobileNumber,
                configEntity.managerMobileNumber, null));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.managerPassword,
                configEntity.managerPassword, null));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.managerEmailID,
                configEntity.managerEmailID, null));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.operatorPassword,
                configEntity.operatorPassword, null));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.operatorMobileNumber,
                configEntity.operatorMobileNumber, null));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.centerAddress,
                configEntity.centerAddress, null));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.centerBMCID,
                configEntity.centerBMCID, null));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.centerCode,
                configEntity.centerCode, null));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.centerName,
                configEntity.centerName, null));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.centerContactEmail1,
                configEntity.centerContactEmail1, null));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.centerContactEmail2,
                configEntity.centerContactEmail2, null));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.centerContactMobile1,
                configEntity.centerContactMobile1, null));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.centerContactMobile2,
                configEntity.centerContactMobile2, null));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.centerContactPerson1,
                configEntity.centerContactPerson1, null));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.centerContactPerson2,
                configEntity.centerContactPerson2, null));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.maxWeighingScaleLimit,
                configEntity.maxWeighingScaleLimit, null));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.WeighingBaudrate,
                configEntity.WeighingBaudrate, null));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.shutDownDelay,
                configEntity.shutDownDelay, null));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.centerLocation,
                configEntity.centerLocation, null));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.centerRoute,
                configEntity.centerRoute, null));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.isRateCalculatedFromDevice, configEntity.isRateCalculatedFromDevice,
                null));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.smsFooter, configEntity.smsFooter, null));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.smartCCFeature, configEntity.smartCCFeature,
                null));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.enableSimlockToDevice, configEntity.enableSimlockToDevice,
                null));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.enableRejectToDevice, configEntity.enableRejectToDevice,
                null));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.simlockPassword, configEntity.simlockPassword,
                null));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.amountRoundOffCheck, configEntity.amountRoundOffCheck,
                null));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.incentivePercentage, configEntity.incentivePercentage,
                null));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.enableSequenceNumberReport, configEntity.enableSequenceNumberReport,
                null));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.snfConstantForClr, configEntity.snfConstantForClr,
                null));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.enableFilledOrEmptyCans, configEntity.enableFilledOrEmptyCans,
                null));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.enableOrDisableIpTable, configEntity.enableOrDisableIpTable,
                null));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.rduQuantityRoundOff,
                configEntity.rduQuantityRoundOff, null));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.snfOrClrForTanker,
                configEntity.snfOrClrForTanker, null));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.weightRecordLength,
                configEntity.weightRecordLength, null));

        allConfigPushList.add(getConfigurationPushList(AmcuConfig.KEY_PERIODIC_DATA_SENT,
                configEntity.periodicDataSent, null));
        allConfigPushList.add(getConfigurationPushList(AmcuConfig.KEY_DATA_SENT_START_TIME,
                configEntity.startTimeDataSent, null));

        allConfigPushList.add(getConfigurationPushList(AmcuConfig.KEY_IS_CLOUD_SUPPORT,
                configEntity.isCloudSupport, null));
        allConfigPushList.add(getConfigurationPushList(AmcuConfig.KEY_SHOW_UNSENT_ALERT,
                configEntity.showUnsentAlert, null));
        allConfigPushList.add(getConfigurationPushList(AmcuConfig.KEY_UNSENT_ALERT_LIMIT,
                configEntity.unsentAlertLimit, null));
        allConfigPushList.add(getConfigurationPushList(AmcuConfig.KEY_DEFAULT_MILK_TYPE_BOTH,
                configEntity.defalutMilkTypeBoth, null));

        allConfigPushList.add(getConfigurationPushList(AmcuConfig.KEY_MIN_VALID_WEIGHT,
                configEntity.minValidWeightForCollection, null));

        allConfigPushList.add(getConfigurationPushList(AmcuConfig.KEY_DELETE_COLL_SHIFT_RECORD,
                configEntity.deleteCollRecordShift, null));
        allConfigPushList.add(getConfigurationPushList(AmcuConfig.KEY_DELETE_FILES_DAY,
                configEntity.deleteFilesAfterDay, null));

        allConfigPushList.add(getConfigurationPushList(AmcuConfig.KEY_ESCAPE_ENABLE_COLLECTION,
                configEntity.escapeEnableForCollection, null));

        allConfigPushList.add(getConfigurationPushList(AmcuConfig.KEY_PRINT_ENABLE_A4,
                configEntity.enablehpPrinter, null));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.KEY_MORNING_START,
                configEntity.morningSessionStartTime, null));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.KEY_MORNING_END_TIME,
                configEntity.morningSessionEndTime, null));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.KEY_EVENING_START,
                configEntity.eveningSessionStartTime, null));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.KEY_EVENING_END_TIME,
                configEntity.eveningSessionEndTime, null));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.KEY_ALLOW_AGENT_SPLIT,
                configEntity.allowAgentSplit, null));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.ALLOW_EDIT_COLLECTION,
                configEntity.allowEditCollection, null));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.KEY_ALLOW_PROTEIN_VALUE,
                configEntity.allowProteinValue, null));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.KEY_ALLOW_INCENTIVE_RATE_CHART_NAME,
                configEntity.incentiveRateChartName, null));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.KEY_ALLOW_TWO_DECIMAL,
                configEntity.allowTwoDecimal, null));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.ALLOW_APK_UPGRADE_FROM_FILE_SYSTEM,
                configEntity.allowApkUpgradeFromFileSystem
                , null));
        return allConfigPushList;

    }


    public ConfigurationElement getConfigurationPushList(String key, String value, String oldValue) {
        ConfigurationElement cpl = new ConfigurationElement();
        cpl.key = key;
        cpl.value = value;
        cpl.status = DatabaseEntity.FARMER_UNSENT_CODE;
        cpl.userName = session.getUserId();
        if (oldValue == null) {
            cpl.lastValue = value;
        } else {
            cpl.lastValue = oldValue;
        }

        cpl.modifiedDate = Calendar.getInstance().getTimeInMillis();
        return cpl;


    }


    public void updateConfigurationValue(String key, String oldValue, String value) {
        if (oldValue == null || value == null || !oldValue.equals(value)) {
            DatabaseHandler dbh = DatabaseHandler.getDatabaseInstance();
            ConfigurationElement cpl = getConfigurationPushList(key, value, oldValue);

            dbh.updateConfigEntity(cpl, DatabaseEntity.FARMER_UNSENT_CODE);
        }

    }

    public ArrayList<ConfigurationElement> getDefaultConfigurationData() {
        ArrayList<ConfigurationElement> allConfigPushList = getAllConfigPushList(ConfigurationConstants.getDefaultConfiguration(mContext),
                new ConfigurationEntity());
        return allConfigPushList;

    }

    public void updateConfigurationData(String key, String value, String oldValue) {
        if (oldValue == null || value == null || !oldValue.equals(value)) {
            DatabaseHandler dbh = DatabaseHandler.getDatabaseInstance();
            ConfigurationElement cpl = getConfigurationPushList(key, value, oldValue);

            dbh.updateConfigEntity(cpl, DatabaseEntity.FARMER_UNSENT_CODE);
        }


    }


    /**
     * Value will be get updated in any change in the configuration
     *
     * @param configEntity
     */

    public void getSavedConfigurationList(ConfigurationEntity configEntity) {
        if (configEntity != null) {
            updateConfigurationValue(ConfigurationConstants.acceptMilkInKgFormat,
                    configEntity.acceptMilkInKgFormat, String.valueOf(amcuConfig.getAllowInKgformat()));
            updateConfigurationValue(ConfigurationConstants.allowCanToggling,
                    configEntity.allowCanToggling, String.valueOf(amcuConfig.getCansToggling()));
            updateConfigurationValue(ConfigurationConstants.allowCollectionRouteInReport,
                    configEntity.allowCollectionRouteInReport, String.valueOf(this.amcuConfig.getAllowRouteInReport()));

            updateConfigurationValue(ConfigurationConstants.amountRoundOff,
                    configEntity.amountRoundOff, String.valueOf(amcuConfig.getDecimalRoundOffAmount()));
            updateConfigurationValue(ConfigurationConstants.allowFSCinPrint,
                    configEntity.allowFSCinPrint, String.valueOf(amcuConfig.getAllowFSCInPrint()));
            updateConfigurationValue(ConfigurationConstants.allowFSCinSMS,
                    configEntity.allowFSCinSMS, String.valueOf(amcuConfig.getAllowFSCInSMS()));
            updateConfigurationValue(ConfigurationConstants.allowMaxLimitFromRC,
                    configEntity.allowMaxLimitFromRC, String.valueOf(amcuConfig.getAllowMaxLimitFromRateChart()));
            updateConfigurationValue(ConfigurationConstants.allowNumberOfCansInReport,
                    configEntity.allowNumberOfCansInReport, String.valueOf(amcuConfig.getAllowNumberOfCans()));
            updateConfigurationValue(ConfigurationConstants.allowOperatorToReportEditing,
                    configEntity.allowOperatorToReportEditing, String.valueOf(amcuConfig.getPermissionToEditReport()));

            updateConfigurationValue(ConfigurationConstants.allowRoundOffFatAndSnf,
                    configEntity.allowRoundOffFatAndSnf, String.valueOf(amcuConfig.getRoundOffCheckFatAndSnf()));
            updateConfigurationValue(ConfigurationConstants.allowSimlock,
                    configEntity.allowSimlock, String.valueOf(amcuConfig.getAllowSimlockPassword()));
            updateConfigurationValue(ConfigurationConstants.allowWSTare,
                    configEntity.allowWSTare, String.valueOf(amcuConfig.getTareEnable()));


            updateConfigurationValue(ConfigurationConstants.bonus, configEntity.bonus, String.valueOf(amcuConfig.getBonusRate()));
            updateConfigurationValue(ConfigurationConstants.bonusEndDate, configEntity.bonusEndDate,
                    String.valueOf(amcuConfig.getBonusEndDate()));
            updateConfigurationValue(ConfigurationConstants.bonusStartDate,
                    configEntity.bonusStartDate, String.valueOf(amcuConfig.getBonusStartDate()));

            updateConfigurationValue(ConfigurationConstants.changeMilktypeBuffalo,
                    configEntity.changeMilktypeBuffalo, String.valueOf(amcuConfig.getChangeFat()));
            updateConfigurationValue(ConfigurationConstants.changeSnfMilkTypeBuffalo,
                    configEntity.changeSnfMilkTypeBuffalo, String.valueOf(amcuConfig.getChangeSnf()));
            updateConfigurationValue(ConfigurationConstants.clrRoundOffUpto,
                    configEntity.clrRoundOffUpto, String.valueOf(amcuConfig.getClrRoundOffUpto()));

            updateConfigurationValue(ConfigurationConstants.collectionEndEveningShift
                    , configEntity.collectionEndEveningShift, String.valueOf(amcuConfig.getKeyCollEndEvnShift()));

            updateConfigurationValue(ConfigurationConstants.collectionEndMorningShift
                    , configEntity.collectionEndMorningShift, String.valueOf(amcuConfig.getKeyCollEndMrnShift()));
            updateConfigurationValue(ConfigurationConstants.collectionSMSFooter
                    , configEntity.collectionSMSFooter, String.valueOf(amcuConfig.getCollectionSMSFooter()));
            updateConfigurationValue(ConfigurationConstants.collectionStartEveningShift
                    , configEntity.collectionStartEveningShift, String.valueOf(amcuConfig.getKeyCollStartEvnShift()));
            updateConfigurationValue(ConfigurationConstants.collectionStartMorningshift
                    , configEntity.collectionStartMorningshift, String.valueOf(amcuConfig.getKeyCollStartMrnShift()));
            updateConfigurationValue(ConfigurationConstants.constantForClr
                    , configEntity.constantForClr, String.valueOf(amcuConfig.getConstant()));

            updateConfigurationValue(ConfigurationConstants.conversionFactor
                    , configEntity.conversionFactor, String.valueOf(amcuConfig.getConversionFactor()));
            updateConfigurationValue(ConfigurationConstants.createMultipleUsers
                    , configEntity.createMultipleUsers, String.valueOf(amcuConfig.getMultipleUser()));
            updateConfigurationValue(ConfigurationConstants.customMyRateChart
                    , configEntity.customMyRateChart, String.valueOf(amcuConfig.getMyRateChartEnable()));

            updateConfigurationValue(ConfigurationConstants.deviationAlertForWeight
                    , configEntity.deviationAlertForWeight, String.valueOf(amcuConfig.getDeviationWeightAlert()));
            updateConfigurationValue(ConfigurationConstants.displayKgToDevice
                    , configEntity.displayKgToDevice, String.valueOf(amcuConfig.getDisplayInKg()));

            updateConfigurationValue(ConfigurationConstants.ekoMilkAddedWaterPosition
                    , configEntity.ekoMilkAddedWaterPosition, String.valueOf(amcuConfig.getAddedWaterPos()));


            updateConfigurationValue(ConfigurationConstants.enableBonusDisplayRDU
                    , configEntity.enableBonusDisplayRDU, String.valueOf(amcuConfig.getEnableBonusRDU()));


            updateConfigurationValue(ConfigurationConstants.enableConfigurableFarmerIdSize
                    , configEntity.enableConfigurableFarmerIdSize, String.valueOf(amcuConfig.getEnableFarmerLenghtConfiguration()));

            updateConfigurationValue(ConfigurationConstants.enableConversionFactor
                    , configEntity.enableConversionFactor, String.valueOf(amcuConfig.getEnableConversionFactorDisplay()));

            updateConfigurationValue(ConfigurationConstants.ekoMilkClrPosition
                    , configEntity.ekoMilkClrPosition, String.valueOf(amcuConfig.getClrPosition()));

            updateConfigurationValue(ConfigurationConstants.enableAllFatSnfClrView
                    , configEntity.enableAllFatSnfClrView, String.valueOf(amcuConfig.getFATSNFCLR()));

            updateConfigurationValue(ConfigurationConstants.enableBonusForPrint
                    , configEntity.enableBonusForPrint, String.valueOf(amcuConfig.getBonusEnableForPrint()));

            updateConfigurationValue(ConfigurationConstants.enableBonusToDevice
                    , configEntity.enableBonusToDevice, String.valueOf(amcuConfig.getBonusDisplay()));

            updateConfigurationValue(ConfigurationConstants.enableClrInReport
                    , configEntity.enableClrInReport, String.valueOf(amcuConfig.getEnableClrInReport()));

            updateConfigurationValue(ConfigurationConstants.enableCollectionContraints
                    , configEntity.enableCollectionContraints, String.valueOf(amcuConfig.getKeyEnableCollectionConstraints()));

            updateConfigurationValue(ConfigurationConstants.enableCenterCollection
                    , configEntity.enableCenterCollection, String.valueOf(amcuConfig.getEnableCenterCollection()));

            updateConfigurationValue(ConfigurationConstants.enableDairyReport
                    , configEntity.enableDairyReport, String.valueOf(amcuConfig.getEnableDairyReport()));

            updateConfigurationValue(ConfigurationConstants.enableEditableRate
                    , configEntity.enableEditableRate, String.valueOf(amcuConfig.getEditableRate()));

            updateConfigurationValue(ConfigurationConstants.enableEquipmentBasedCollection
                    , configEntity.enableEquipmentBasedCollection, String.valueOf(amcuConfig.getAllowEquipmentBasedCollection()));

            updateConfigurationValue(ConfigurationConstants.enableFarmerExportMail
                    , configEntity.enableFarmerExportMail, String.valueOf(amcuConfig.getAllowMailExportedFarmer()));

            updateConfigurationValue(ConfigurationConstants.enableFatClrViewForChilling
                    , configEntity.enableFatClrViewForChilling, String.valueOf(amcuConfig.getChillingFATSNFCLR()));
            updateConfigurationValue(ConfigurationConstants.enableFatSnfViewForCollection
                    , configEntity.enableFatSnfViewForCollection, String.valueOf(amcuConfig.getCollectionFATSNFCLR()));

            updateConfigurationValue(ConfigurationConstants.enableIncentiveInReport
                    , configEntity.enableIncentiveInReport, String.valueOf(amcuConfig.getEnableIncentiveInReport()));

            updateConfigurationValue(ConfigurationConstants.enableIncentiveOnRdu
                    , configEntity.enableIncentiveOnRdu, String.valueOf(amcuConfig.getEnableIncentiveRDU()));

            updateConfigurationValue(ConfigurationConstants.enableIpTableRule
                    , configEntity.enableIpTableRule, String.valueOf(amcuConfig.getIpTableRule()));

            updateConfigurationValue(ConfigurationConstants.enableManualToDevice
                    , configEntity.enableManualToDevice, String.valueOf(amcuConfig.getManualDisplay()));

            updateConfigurationValue(ConfigurationConstants.enableMilkQuality
                    , configEntity.enableMilkQuality, String.valueOf(amcuConfig.getAllowMilkquality()));

            updateConfigurationValue(ConfigurationConstants.enableMultipleCollection
                    , configEntity.enableMultipleCollection, String.valueOf(amcuConfig.getAllowMultipeCollection()));
            updateConfigurationValue(ConfigurationConstants.enableSales
                    , configEntity.enableSales, String.valueOf(amcuConfig.getEnableSales()));


            updateConfigurationValue(ConfigurationConstants.enableSalesFarmerId
                    , configEntity.enableSalesFarmerId, String.valueOf(amcuConfig.getEnableFarmerIdForSales()));

            updateConfigurationValue(ConfigurationConstants.enableSalesFS
                    , configEntity.enableSalesFS, String.valueOf(amcuConfig.getSalesFSEnable()));
            updateConfigurationValue(ConfigurationConstants.enableSMS
                    , configEntity.enableSMS, String.valueOf(amcuConfig.getAllowSMS()));

            updateConfigurationValue(ConfigurationConstants.enableTruckEntry
                    , configEntity.enableTruckEntry, String.valueOf(amcuConfig.getEnableTruckEntry()));


            updateConfigurationValue(ConfigurationConstants.fatConstantForClr
                    , configEntity.fatConstantForClr, String.valueOf(amcuConfig.getFatCons()));

            updateConfigurationValue(ConfigurationConstants.isAddedWater
                    , configEntity.isAddedWater, String.valueOf(amcuConfig.getIsAddedWater()));

            updateConfigurationValue(ConfigurationConstants.isMaManual,
                    configEntity.isMaManual, String.valueOf(amcuConfig.getMaManual()));
            updateConfigurationValue(ConfigurationConstants.isWsManual,
                    configEntity.isWsManual, String.valueOf(amcuConfig.getWsManual()));


            updateConfigurationValue(ConfigurationConstants.isBonusEnable
                    , configEntity.isBonusEnable, String.valueOf(amcuConfig.getBonusEnable()));


            updateConfigurationValue(ConfigurationConstants.isDisplayAmount,
                    configEntity.isDisplayAmount, String.valueOf(amcuConfig.getKeyDisplayAmount()));
            updateConfigurationValue(ConfigurationConstants.isDisplayQuantity,
                    configEntity.isDisplayQuantity, String.valueOf(amcuConfig.getKeyDisplayQuantity()));


            updateConfigurationValue(ConfigurationConstants.isDisplayRate
                    , configEntity.isDisplayRate, String.valueOf(amcuConfig.getKeyDisplayRate()));

            updateConfigurationValue(ConfigurationConstants.isImportExcelSheetAccess
                    , configEntity.isImportExcelSheetAccess, String.valueOf(amcuConfig.getImportExcelRateEnable()));

            updateConfigurationValue(ConfigurationConstants.isRateImportAccessOperator,
                    configEntity.isRateImportAccessOperator, String.valueOf(amcuConfig.getOperatorRateAccess()));
            updateConfigurationValue(ConfigurationConstants.isRateChartInKg,
                    configEntity.isRateChartInKg, String.valueOf(amcuConfig.getKeyRateChartInKg()));


            updateConfigurationValue(ConfigurationConstants.isRateEditCheck
                    , configEntity.isRateEditCheck, String.valueOf(amcuConfig.getEditableRate()));

            updateConfigurationValue(ConfigurationConstants.materialCode
                    , configEntity.materialCode, String.valueOf(amcuConfig.getMaterialCode()));


            updateConfigurationValue(ConfigurationConstants.maxLimitEmptyCan
                    , configEntity.maxLimitEmptyCan, String.valueOf(amcuConfig.getMaxLimitEmptyCans()));

            updateConfigurationValue(ConfigurationConstants.milkAnalyzerBaudrate
                    , configEntity.milkAnalyzerBaudrate, String.valueOf(amcuConfig.getMABaudRate()));

            updateConfigurationValue(ConfigurationConstants.milkAnalyzerName,
                    configEntity.milkAnalyzerName, String.valueOf(amcuConfig.getMA()));
//        updateConfigurationValue(ConfigurationConstants.milkType,
//                configEntity.milkType,String.valueOf(saveSession.getMilkType()));


            updateConfigurationValue(ConfigurationConstants.minLimitFilledCan
                    , configEntity.minLimitFilledCan, String.valueOf(amcuConfig.getMinLimitFilledCans()));

            updateConfigurationValue(ConfigurationConstants.multipleMA
                    , configEntity.multipleMA, String.valueOf(amcuConfig.getMultipleMA()));

            updateConfigurationValue(ConfigurationConstants.numberOfEditableShift,
                    configEntity.numberOfEditableShift, String.valueOf(amcuConfig.getNumberShiftCanBeEdited()));

            updateConfigurationValue(ConfigurationConstants.perDaySMSLimit,
                    configEntity.perDaySMSLimit, String.valueOf(session.getPerDayMessageLimit()));


            updateConfigurationValue(ConfigurationConstants.printerBaudrate
                    , configEntity.printerBaudrate, String.valueOf(amcuConfig.getPrinterBaudRate()));

            updateConfigurationValue(ConfigurationConstants.printerName
                    , configEntity.printerName, String.valueOf(amcuConfig.getPrinter()));

            updateConfigurationValue(ConfigurationConstants.rateChart,
                    configEntity.rateChart, String.valueOf(amcuConfig.getRateChartName()));

            updateConfigurationValue(ConfigurationConstants.rateRoundOff,
                    configEntity.rateRoundOff, String.valueOf(amcuConfig.getDecimalRoundOffRate()));


            updateConfigurationValue(ConfigurationConstants.rateRoundOffCheck
                    , configEntity.rateRoundOffCheck, String.valueOf(amcuConfig.getDecimalRoundOffRateCheck()));

            updateConfigurationValue(ConfigurationConstants.rduBaudrate
                    , configEntity.rduBaudrate, String.valueOf(amcuConfig.getRdubaudrate()));

            updateConfigurationValue(ConfigurationConstants.rduName,
                    configEntity.rduName, String.valueOf(amcuConfig.getRDU()));

            updateConfigurationValue(ConfigurationConstants.secondMAType,
                    configEntity.secondMAType, String.valueOf(amcuConfig.getSecondMilkAnalyser()));


            updateConfigurationValue(ConfigurationConstants.salesBuffRate
                    , configEntity.salesBuffRate, String.valueOf(amcuConfig.getSalesBuffRate()));

            updateConfigurationValue(ConfigurationConstants.salesCowRate
                    , configEntity.salesCowRate, String.valueOf(amcuConfig.getSalesCowRate()));

            updateConfigurationValue(ConfigurationConstants.secondMABaudrate,
                    configEntity.secondMABaudrate, String.valueOf(amcuConfig.getSecondMilkBaud()));

            updateConfigurationValue(ConfigurationConstants.sendShiftMails,
                    configEntity.sendShiftMails, String.valueOf(amcuConfig.getSendEmailToConfigureIDs()));

            updateConfigurationValue(ConfigurationConstants.selectRateChartType
                    , configEntity.selectRateChartType, String.valueOf(amcuConfig.getRateChartType()));

            updateConfigurationValue(ConfigurationConstants.setAmcuServer,
                    configEntity.setAmcuServer, String.valueOf(amcuConfig.getServer()));


            updateConfigurationValue(ConfigurationConstants.smartAmcuEmail
                    , configEntity.smartAmcuEmail, String.valueOf(amcuConfig.getClientConfiguredEmail()));

            updateConfigurationValue(ConfigurationConstants.tareCommand
                    , configEntity.tareCommand, String.valueOf(amcuConfig.getTareCommand()));

            updateConfigurationValue(ConfigurationConstants.tareDeviationWeight,
                    configEntity.tareDeviationWeight, String.valueOf(amcuConfig.getDeviationWeight()));

            updateConfigurationValue(ConfigurationConstants.urlHeader,
                    configEntity.urlHeader, String.valueOf(amcuConfig.getURLHeader()));


//        updateConfigurationValue(ConfigurationConstants.userConfig
//                ,configEntity.userConfig,String.valueOf(saveSession.us);

            updateConfigurationValue(ConfigurationConstants.visiblityReportEditing
                    , configEntity.visiblityReportEditing, String.valueOf(amcuConfig.getAllowVisibilityReportEditing()));

            updateConfigurationValue(ConfigurationConstants.visibilityCanToggling,
                    configEntity.visibilityCanToggling, String.valueOf(amcuConfig.getAllowVisiblityForCanToggle()));

            updateConfigurationValue(ConfigurationConstants.weighingScale,
                    configEntity.weighingScale, String.valueOf(amcuConfig.getWeighingScale()));


            updateConfigurationValue(ConfigurationConstants.weighingDatbits
                    , configEntity.weighingDatbits, String.valueOf(amcuConfig.getWSDataBits()));

            updateConfigurationValue(ConfigurationConstants.weighingDivisionFactor
                    , configEntity.weighingDivisionFactor, String.valueOf(amcuConfig.getWeighingDivisionFactor()));
            updateConfigurationValue(ConfigurationConstants.weighingScalePrefix,
                    configEntity.weighingScalePrefix, String.valueOf(amcuConfig.getWeighingPrefix()));

            updateConfigurationValue(ConfigurationConstants.weighingParity,
                    configEntity.weighingParity, String.valueOf(amcuConfig.getWSParity()));


            updateConfigurationValue(ConfigurationConstants.weighingScaleSeparator
                    , configEntity.weighingScaleSeparator, String.valueOf(amcuConfig.getWeighingSeperator()));

            updateConfigurationValue(ConfigurationConstants.weighingScaleSuffix
                    , configEntity.weighingScaleSuffix, String.valueOf(amcuConfig.getWeighingSuffix()));

            updateConfigurationValue(ConfigurationConstants.weightRoundOff,
                    configEntity.weightRoundOff, String.valueOf(amcuConfig.getDecimalRoundOffWeigh()));

            updateConfigurationValue(ConfigurationConstants.weightRoundOffCheck,
                    configEntity.weightRoundOffCheck, String.valueOf(amcuConfig.getDecimalRoundOffWeightCheck()));

            updateConfigurationValue(ConfigurationConstants.weighingStopbits,
                    configEntity.weighingStopbits, String.valueOf(amcuConfig.getWSStopBits()));

            updateConfigurationValue(ConfigurationConstants.maxWeighingScaleLimit, configEntity.maxWeighingScaleLimit,
                    String.valueOf(amcuConfig.getMaxlimitOfWeight()));
            updateConfigurationValue(ConfigurationConstants.ignoreZeroFatSNF
                    , configEntity.ignoreZeroFatSNF, String.valueOf(amcuConfig.getKeyIgnoreZeroFatSnf()));
            updateConfigurationValue(ConfigurationConstants.WeighingBaudrate
                    , configEntity.WeighingBaudrate, String.valueOf(amcuConfig.getWeighingbaudrate()));
            updateConfigurationValue(ConfigurationConstants.shutDownDelay, configEntity.shutDownDelay,
                    String.valueOf(amcuConfig.getShutDownDelay()));


            updateConfigurationValue(ConfigurationConstants.isRateCalculatedFromDevice, configEntity.isRateCalculatedFromDevice,
                    String.valueOf(amcuConfig.getRateCalculatedFromDevice()));
            updateConfigurationValue(ConfigurationConstants.smsFooter, configEntity.smsFooter,
                    amcuConfig.getSMSFooter());
            updateConfigurationValue(ConfigurationConstants.smartCCFeature, configEntity.smartCCFeature,
                    String.valueOf(amcuConfig.getSmartCCFeature()));
            updateConfigurationValue(ConfigurationConstants.enableSimlockToDevice, configEntity.enableSimlockToDevice,
                    String.valueOf(amcuConfig.getSimlockDisplay()));
            updateConfigurationValue(ConfigurationConstants.enableRejectToDevice, configEntity.enableRejectToDevice,
                    String.valueOf(amcuConfig.getEnableRejectForRateChart()));
            updateConfigurationValue(ConfigurationConstants.simlockPassword, configEntity.simlockPassword,
                    amcuConfig.getSimUnlockPassword());
            updateConfigurationValue(ConfigurationConstants.amountRoundOffCheck, configEntity.amountRoundOffCheck,
                    String.valueOf(amcuConfig.getDecimalRoundOffAmountCheck()));
            updateConfigurationValue(ConfigurationConstants.incentivePercentage, configEntity.incentivePercentage,
                    String.valueOf(amcuConfig.getIncentivePercentage()));
            updateConfigurationValue(ConfigurationConstants.enableSequenceNumberReport, configEntity.enableSequenceNumberReport,
                    String.valueOf(amcuConfig.getEnableSequenceNumberInReport()));
            updateConfigurationValue(ConfigurationConstants.snfConstantForClr, configEntity.snfConstantForClr,
                    String.valueOf(amcuConfig.getSnfCons()));
            updateConfigurationValue(ConfigurationConstants.enableFilledOrEmptyCans, configEntity.enableFilledOrEmptyCans,
                    String.valueOf(amcuConfig.getEnableFilledOrEmptyCans()));
            updateConfigurationValue(ConfigurationConstants.enableOrDisableIpTable, configEntity.enableOrDisableIpTable,
                    String.valueOf(amcuConfig.getMobileData()));

            updateConfigurationValue(ConfigurationConstants.rduQuantityRoundOff,
                    configEntity.rduQuantityRoundOff, String.valueOf(amcuConfig.getRDUWeightRoundOff()));
            updateConfigurationValue(ConfigurationConstants.snfOrClrForTanker,
                    configEntity.snfOrClrForTanker, amcuConfig.getSNFOrCLRFromTanker());
            updateConfigurationValue(ConfigurationConstants.weightRecordLength,
                    configEntity.weightRecordLength, String.valueOf(amcuConfig.getWeightRecordLenght()));

            updateConfigurationValue(AmcuConfig.KEY_PERIODIC_DATA_SENT,
                    configEntity.periodicDataSent, String.valueOf(amcuConfig.getKeyPeriodicDataSent()));
            updateConfigurationValue(AmcuConfig.KEY_DATA_SENT_START_TIME,
                    configEntity.startTimeDataSent, String.valueOf(amcuConfig.getKeyDataSentStartTime()));
            updateConfigurationValue(AmcuConfig.KEY_IS_CLOUD_SUPPORT,
                    configEntity.isCloudSupport, String.valueOf(amcuConfig.getKeyCloudSupport()));

            updateConfigurationValue(AmcuConfig.KEY_SHOW_UNSENT_ALERT,
                    configEntity.showUnsentAlert, String.valueOf(amcuConfig.getShowUnsentAlert()));
            updateConfigurationValue(AmcuConfig.KEY_UNSENT_ALERT_LIMIT,
                    configEntity.unsentAlertLimit, String.valueOf(amcuConfig.getUnsentAlertLimit()));

            updateConfigurationValue(AmcuConfig.KEY_DEFAULT_MILK_TYPE_BOTH,
                    configEntity.defalutMilkTypeBoth, String.valueOf(amcuConfig.getDefaultMilkTypeForBoth()));
            updateConfigurationValue(AmcuConfig.KEY_MIN_VALID_WEIGHT,
                    configEntity.minValidWeightForCollection, String.valueOf(amcuConfig.getKeyMinValidWeight()));

            updateConfigurationValue(AmcuConfig.KEY_DELETE_COLL_SHIFT_RECORD,
                    configEntity.deleteCollRecordShift, String.valueOf(amcuConfig.getDeleteCollRecordAfterShift()));

            updateConfigurationValue(AmcuConfig.KEY_DELETE_FILES_DAY,
                    configEntity.deleteFilesAfterDay, String.valueOf(amcuConfig.getDeleteFilesAfterDays()));

            updateConfigurationValue(AmcuConfig.KEY_ESCAPE_ENABLE_COLLECTION,
                    configEntity.escapeEnableForCollection, String.valueOf(amcuConfig.getKeyEscapeEnableCollection()));


            updateConfigurationValue(ConfigurationConstants.KEY_MORNING_START,
                    configEntity.morningSessionStartTime, String.valueOf(amcuConfig.getMorningStart()));
            updateConfigurationValue(ConfigurationConstants.KEY_MORNING_END_TIME,
                    configEntity.morningSessionEndTime, String.valueOf(amcuConfig.getMorningEndTime()));
            updateConfigurationValue(ConfigurationConstants.KEY_EVENING_START,
                    configEntity.eveningSessionStartTime, String.valueOf(amcuConfig.getEveningStart()));
            updateConfigurationValue(ConfigurationConstants.KEY_EVENING_END_TIME,
                    configEntity.eveningSessionEndTime, String.valueOf(amcuConfig.getEveningEndTime()));

            updateConfigurationValue(ConfigurationConstants.KEY_ALLOW_AGENT_SPLIT,
                    configEntity.allowAgentSplit, String.valueOf(amcuConfig.getKeyAllowAgentFarmerCollection()));

            updateConfigurationValue(ConfigurationConstants.ALLOW_EDIT_COLLECTION,
                    configEntity.allowEditCollection, String.valueOf(amcuConfig.getKeyAllowEditCollection()));

            updateConfigurationValue(ConfigurationConstants.KEY_ALLOW_PROTEIN_VALUE,
                    configEntity.allowProteinValue, String.valueOf(amcuConfig.getKeyAllowProteinValue()));

            updateConfigurationValue(ConfigurationConstants.KEY_ALLOW_INCENTIVE_RATE_CHART_NAME,
                    configEntity.incentiveRateChartName, String.valueOf(amcuConfig.getInCentiveRateChartname()));
            updateConfigurationValue(ConfigurationConstants.KEY_ALLOW_TWO_DECIMAL,
                    configEntity.allowTwoDecimal, String.valueOf(amcuConfig.getKeyAllowTwoDeciaml()));


            //FAT,SNF,CLR Configuration
            updateConfigurationValue(ConfigurationConstants.KEY_DISPLAY_FAT_CONFIGURATION,
                    configEntity.displayFATConfiguration, String.valueOf(amcuConfig.getDisplayFATConfiguration()));
            updateConfigurationValue(ConfigurationConstants.KEY_DISPLAY_SNF_CONFIGURATION,
                    configEntity.displaySNFConfiguration, String.valueOf(amcuConfig.getDisplaySNFConfiguration()));
            updateConfigurationValue(ConfigurationConstants.KEY_DISPLAY_CLR_CONFIGURATION,
                    configEntity.displayCLRConfiguration, String.valueOf(amcuConfig.getDisplayCLRConfiguration()));
            updateConfigurationValue(ConfigurationConstants.KEY_DISPLAY_PROTEIN_CONFIGURATION,
                    configEntity.displayProteinConfiguration, String.valueOf(amcuConfig.getDisplayProteinConfiguration()));

            updateConfigurationValue(ConfigurationConstants.KEY_RATE_FAT_CONFIGURATION,
                    configEntity.rateFATConfiguration, String.valueOf(amcuConfig.getRateFATConfiguration()));
            updateConfigurationValue(ConfigurationConstants.KEY_RATE_SNF_CONFIGURATION,
                    configEntity.rateSNFConfiguration, String.valueOf(amcuConfig.getRateSNFConfiguration()));
            updateConfigurationValue(ConfigurationConstants.KEY_RATE_CLR_CONFIGURATION,
                    configEntity.rateCLRConfiguration, String.valueOf(amcuConfig.getRateCLRConfiguration()));
            updateConfigurationValue(ConfigurationConstants.KEY_RATE_PROTEIN_CONFIGURATION,
                    configEntity.rateProteinConfiguration, String.valueOf(amcuConfig.getRateProteinConfiguration()));

            updateConfigurationValue(ConfigurationConstants.KEY_ROUND_FAT_CONFIGURATION,
                    configEntity.roundFATConfiguration, String.valueOf(amcuConfig.getRoundFATConfiguration()));
            updateConfigurationValue(ConfigurationConstants.KEY_ROUND_SNF_CONFIGURATION,
                    configEntity.roundSNFConfiguration, String.valueOf(amcuConfig.getRoundSNFConfiguration()));
            updateConfigurationValue(ConfigurationConstants.KEY_ROUND_CLR_CONFIGURATION,
                    configEntity.roundCLRConfiguration, String.valueOf(amcuConfig.getRoundCLRConfiguration()));
            updateConfigurationValue(ConfigurationConstants.KEY_ROUND_PROTEIN_CONFIGURATION,
                    configEntity.roundProteinConfiguration, String.valueOf(amcuConfig.getRoundProteinConfiguration()));
            updateConfigurationValue(ConfigurationConstants.KEY_RDU_PASSWORD,
                    configEntity.rduPassword, String.valueOf(amcuConfig.getRduPassword()));
            updateConfigurationValue(ConfigurationConstants.KEY_HOTSPOT_SSID,
                    configEntity.hotspotSsid, String.valueOf(amcuConfig.getHotspotSsid()));
            updateConfigurationValue(ConfigurationConstants.KEY_ENABLE_HOTSPOT,
                    configEntity.enableHotspot, String.valueOf(amcuConfig.getHotspotValue()));
            updateConfigurationValue(ConfigurationConstants.ENABLE_LACTOSE_BASED_REJECTION,
                    configEntity.enableLactoseBasedRejection, String.valueOf(amcuConfig.getLactoseBasedRejection()));
            updateConfigurationValue(ConfigurationConstants.ENABLE_DYNAMIC_RATE_CHART,
                    configEntity.enableDynamicRateChart, amcuConfig.getDynamicRateChartValue());
            updateConfigurationValue(ConfigurationConstants.KEY_MAX_LACTOSE_REJECT_COW,
                    configEntity.keyMaxLactoseRejectCow, String.valueOf(amcuConfig.getKeyMaxLactoseRejectCow()));
            updateConfigurationValue(ConfigurationConstants.KEY_MAX_LACTOSE_REJECT_BUFF,
                    configEntity.keyMaxLactoseRejectBuff, String.valueOf(amcuConfig.getKeyMaxLactoseRejectBuff()));
            updateConfigurationValue(ConfigurationConstants.KEY_MAX_LACTOSE_REJECT_MIX,
                    configEntity.keyMaxLactoseRejectMix, String.valueOf(amcuConfig.getKeyMaxLactoseRejectMix()));
            updateConfigurationValue(ConfigurationConstants.KEY_ALLOW_DISPLAY_RATE,
                    configEntity.allowDisplayRate, String.valueOf(amcuConfig.getKeyAllowDisplayRate()));
            updateConfigurationValue(ConfigurationConstants.KEY_ALLOW_FARMER_EDIT,
                    configEntity.allowFarmerEdit, String.valueOf(amcuConfig.getKeyAllowFarmerEdit()));
            updateConfigurationValue(ConfigurationConstants.KEY_ALLOW_FARMER_CREATE,
                    configEntity.allowFarmerCreate, String.valueOf(amcuConfig.getKeyAllowFarmerCreate()));
            updateConfigurationValue(ConfigurationConstants.KEY_WS_IGNORE_THRESHOLD,
                    configEntity.wsIgnoreThreshold, String.valueOf(amcuConfig.getWsIgnoreThreshold()));
            updateConfigurationValue(ConfigurationConstants.KEY_WS_KG_COMMAND,
                    configEntity.wsKgCommand, String.valueOf(amcuConfig.getWsKgCommand()));
            updateConfigurationValue(ConfigurationConstants.KEY_WS_LITRE_COMMAND,
                    configEntity.wsLitreCommand, String.valueOf(amcuConfig.getWsLitreCommand()));
            updateConfigurationValue(ConfigurationConstants.KEY_ALLOW_DISPLAY_NAME_IN_REPORT,
                    configEntity.allowNameInReport, String.valueOf(amcuConfig.getDisplayNameInReport()));

            updateConfigurationValue(ConfigurationConstants.KEY_DISPLAY_DAIRY_NAME,
                    configEntity.displayDairyname, String.valueOf(amcuConfig.getDisplayDairyName()));
            updateConfigurationValue(ConfigurationConstants.ALLOW_APK_UPGRADE_FROM_FILE_SYSTEM,
                    configEntity.allowApkUpgradeFromFileSystem, String.valueOf(amcuConfig.getApkFromFileSystem()));

        }
    }

    public void updateConfigurationPushList(ArrayList<ConfigurationElement> allConfigPushList) {

    }

    /**
     * To get and add the data first time all the old value will be consider as null
     *
     * @param configEntity
     * @return
     */

    public ArrayList<ConfigurationElement> getAllConfigPushList(ConfigurationEntity configEntity,
                                                                ConfigurationEntity oldConfiEntity) {
        ArrayList<ConfigurationElement> allConfigPushList = new ArrayList<>();

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.ACCEPT_MILK_IN_KG_FORMAT,
                configEntity.acceptMilkInKgFormat, oldConfiEntity.acceptMilkInKgFormat));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.ALLOW_CAN_TOGGLING,
                configEntity.allowCanToggling, oldConfiEntity.allowCanToggling));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.ALLOW_COLLECTION_ROUTE_IN_REPORT,
                configEntity.allowCollectionRouteInReport, oldConfiEntity.allowCollectionRouteInReport));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.AMOUNT_ROUND_OFF,
                configEntity.amountRoundOff, oldConfiEntity.amountRoundOff));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.ALLOW_FSC_IN_PRINT,
                configEntity.allowFSCinPrint, oldConfiEntity.allowFSCinPrint));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.ALLOW_FSC_IN_SMS,
                configEntity.allowFSCinSMS, oldConfiEntity.allowFSCinSMS));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.ALLOW_MAX_LIMIT_FROM_RC,
                configEntity.allowMaxLimitFromRC, oldConfiEntity.allowMaxLimitFromRC));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.ALLOW_NUMBER_OF_CANS_IN_REPORT,
                configEntity.allowNumberOfCansInReport, oldConfiEntity.allowNumberOfCansInReport));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.ALLOW_EDIT_REPORT,
                configEntity.allowOperatorToReportEditing, oldConfiEntity.allowOperatorToReportEditing));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.ALLOW_ROUND_OFF_FAT_AND_SNF,
                configEntity.allowRoundOffFatAndSnf, oldConfiEntity.allowRoundOffFatAndSnf));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.ALLOW_SIMLOCK,
                configEntity.allowSimlock, oldConfiEntity.allowSimlock));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.ALLOW_WS_TARE,
                configEntity.allowWSTare, oldConfiEntity.allowWSTare));


        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.BONUS, configEntity.bonus, oldConfiEntity.bonus));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.BONUS_END_DATE, configEntity.bonusEndDate, oldConfiEntity.bonusEndDate));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.BONUS_START_DATE,
                configEntity.bonusStartDate, oldConfiEntity.bonusStartDate));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.CHANGE_MILKTYPE_BUFFALO,
                configEntity.changeMilktypeBuffalo, oldConfiEntity.changeMilktypeBuffalo));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.CHANGE_SNF_MILK_TYPE_BUFFALO,
                configEntity.changeSnfMilkTypeBuffalo, oldConfiEntity.changeSnfMilkTypeBuffalo));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.CLR_ROUND_OFF_UPTO, configEntity.clrRoundOffUpto, oldConfiEntity.clrRoundOffUpto));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.COLLECTION_END_EVENING_SHIFT
                , configEntity.collectionEndEveningShift, oldConfiEntity.collectionEndEveningShift));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.COLLECTION_END_MORNING_SHIFT
                , configEntity.collectionEndMorningShift, oldConfiEntity.collectionEndMorningShift));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.COLLECTION_SMS_FOOTER
                , configEntity.collectionSMSFooter, oldConfiEntity.collectionSMSFooter));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.COLLECTION_START_EVENING_SHIFT
                , configEntity.collectionStartEveningShift, oldConfiEntity.collectionStartEveningShift));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.COLLECTION_START_MORNINGSHIFT
                , configEntity.collectionStartMorningshift, oldConfiEntity.collectionStartMorningshift));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.CONSTANT_FOR_CLR
                , configEntity.constantForClr, oldConfiEntity.constantForClr));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.CONVERSION_FACTOR
                , configEntity.conversionFactor, oldConfiEntity.conversionFactor));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.CREATE_MULTIPLE_USERS
                , configEntity.createMultipleUsers, oldConfiEntity.createMultipleUsers));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.CUSTOM_MY_RATE_CHART
                , configEntity.customMyRateChart, oldConfiEntity.customMyRateChart));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.DEVIATION_ALERT_FOR_WEIGHT
                , configEntity.deviationAlertForWeight, oldConfiEntity.deviationAlertForWeight));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.DISPLAY_KG_TO_DEVICE
                , configEntity.displayKgToDevice, oldConfiEntity.displayKgToDevice));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.EKO_MILK_ADDED_WATER_POSITION
                , configEntity.ekoMilkAddedWaterPosition, oldConfiEntity.ekoMilkAddedWaterPosition));


        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.ENABLE_BONUS_DISPLAY_RDU
                , configEntity.enableBonusDisplayRDU, oldConfiEntity.enableBonusDisplayRDU));


        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.ENABLE_CONFIGURABLE_FARMER_ID_SIZE
                , configEntity.enableConfigurableFarmerIdSize, oldConfiEntity.enableConfigurableFarmerIdSize));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.ENABLE_CONVERSION_FACTOR
                , configEntity.enableConversionFactor, oldConfiEntity.enableConversionFactor));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.EKO_MILK_CLR_POSITION
                , configEntity.ekoMilkClrPosition, oldConfiEntity.ekoMilkClrPosition));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.ENABLE_ALL_FAT_SNF_CLR_VIEW
                , configEntity.enableAllFatSnfClrView, oldConfiEntity.enableAllFatSnfClrView));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.ENABLE_BONUS_FOR_PRINT
                , configEntity.enableBonusForPrint, oldConfiEntity.enableBonusForPrint));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.ENABLE_BONUS_TO_DEVICE
                , configEntity.enableBonusToDevice, oldConfiEntity.enableBonusToDevice));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.ENABLE_CLR_IN_REPORT
                , configEntity.enableClrInReport, oldConfiEntity.enableClrInReport));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.ENABLE_COLLECTION_CONTRAINTS
                , configEntity.enableCollectionContraints, oldConfiEntity.enableCollectionContraints));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.ENABLE_CENTER_COLLECTION
                , configEntity.enableCenterCollection, oldConfiEntity.enableCenterCollection));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.ENABLE_DAIRY_REPORT
                , configEntity.enableDairyReport, oldConfiEntity.enableDairyReport));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.ENABLE_EDITABLE_RATE
                , configEntity.enableEditableRate, oldConfiEntity.enableEditableRate));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.ENABLE_EQUIPMENT_BASED_COLLECTION
                , configEntity.enableEquipmentBasedCollection, oldConfiEntity.enableEquipmentBasedCollection));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.ENABLE_FARMER_EXPORT_MAIL
                , configEntity.enableFarmerExportMail, oldConfiEntity.enableFarmerExportMail));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.ENABLE_FAT_CLR_VIEW_FOR_CHILLING
                , configEntity.enableFatClrViewForChilling, oldConfiEntity.enableFatClrViewForChilling));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.ENABLE_FAT_SNF_VIEW_FOR_COLLECTION
                , configEntity.enableFatSnfViewForCollection, oldConfiEntity.enableFatSnfViewForCollection));


        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.ENABLE_INCENTIVE_IN_REPORT
                , configEntity.enableIncentiveInReport, oldConfiEntity.enableIncentiveInReport));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.ENABLE_INCENTIVE_ON_RDU
                , configEntity.enableIncentiveOnRdu, oldConfiEntity.enableIncentiveOnRdu));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.ENABLE_IP_TABLE_RULE
                , configEntity.enableIpTableRule, oldConfiEntity.enableIpTableRule));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.ENABLE_MANUAL_TO_DEVICE
                , configEntity.enableManualToDevice, oldConfiEntity.enableManualToDevice));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.ENABLE_MILK_QUALITY
                , configEntity.enableMilkQuality, oldConfiEntity.enableMilkQuality));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.ENABLE_MULTIPLE_COLLECTION
                , configEntity.enableMultipleCollection, oldConfiEntity.enableMultipleCollection));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.ENABLE_SALES
                , configEntity.enableSales, oldConfiEntity.enableSales));


        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.ENABLE_SALES_FARMER_ID
                , configEntity.enableSalesFarmerId, oldConfiEntity.enableSalesFarmerId));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.ENABLE_SALES_FS
                , configEntity.enableSalesFS, oldConfiEntity.enableSalesFS));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.ENABLE_SMS
                , configEntity.enableSMS, oldConfiEntity.enableSMS));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.ENABLE_TRUCK_ENTRY
                , configEntity.enableTruckEntry, oldConfiEntity.enableTruckEntry));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.FAT_CONSTANT_FOR_CLR
                , configEntity.fatConstantForClr, oldConfiEntity.fatConstantForClr));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.IS_ADDED_WATER
                , configEntity.isAddedWater, oldConfiEntity.isAddedWater));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.IS_MA_MANUAL,
                configEntity.isMaManual, oldConfiEntity.isMaManual));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.IS_WS_MANUAL,
                configEntity.isWsManual, oldConfiEntity.isWsManual));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.IS_BONUS_ENABLE
                , configEntity.isBonusEnable, oldConfiEntity.isBonusEnable));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.IS_DISPLAY_AMOUNT,
                configEntity.isDisplayAmount, oldConfiEntity.isDisplayAmount));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.IS_DISPLAY_QUANTITY,
                configEntity.isDisplayQuantity, oldConfiEntity.isDisplayQuantity));


        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.IS_DISPLAY_RATE
                , configEntity.isDisplayRate, oldConfiEntity.isDisplayRate));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.IS_IMPORT_EXCEL_SHEET_ACCESS
                , configEntity.isImportExcelSheetAccess, oldConfiEntity.isImportExcelSheetAccess));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.IS_RATE_IMPORT_ACCESS_OPERATOR,
                configEntity.isRateImportAccessOperator, oldConfiEntity.isRateImportAccessOperator));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.IS_RATE_CHART_IN_KG,
                configEntity.isRateChartInKg, oldConfiEntity.isRateChartInKg));


        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.IS_RATE_EDIT_CHECK
                , configEntity.isRateEditCheck, oldConfiEntity.isRateEditCheck));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.MATERIAL_CODE
                , configEntity.materialCode, oldConfiEntity.materialCode));


        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.MAX_LIMIT_EMPTY_CAN
                , configEntity.maxLimitEmptyCan, oldConfiEntity.maxLimitEmptyCan));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.MILK_ANALYZER_BAUDRATE
                , configEntity.milkAnalyzerBaudrate, oldConfiEntity.milkAnalyzerBaudrate));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.MILK_ANALYZER_NAME,
                configEntity.milkAnalyzerName, oldConfiEntity.milkAnalyzerName));
//        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.milkType,
//                configEntity.milkType,null));


        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.MIN_LIMIT_FILLED_CAN
                , configEntity.minLimitFilledCan, oldConfiEntity.minLimitFilledCan));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.MULTIPLE_MA
                , configEntity.multipleMA, oldConfiEntity.multipleMA));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.NUMBER_OF_EDITABLE_SHIFT,
                configEntity.numberOfEditableShift, oldConfiEntity.numberOfEditableShift));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.PER_DAY_SMS_LIMIT,
                configEntity.perDaySMSLimit, oldConfiEntity.perDaySMSLimit));


        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.PRINTER_BAUDRATE
                , configEntity.printerBaudrate, oldConfiEntity.printerBaudrate));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.PRINTER_NAME
                , configEntity.printerName, oldConfiEntity.printerName));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.RATE_CHART,
                configEntity.rateChart, oldConfiEntity.rateChart));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.RATE_ROUND_OFF,
                configEntity.rateRoundOff, oldConfiEntity.rateRoundOff));


        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.RATE_ROUND_OFF_CHECK
                , configEntity.rateRoundOffCheck, oldConfiEntity.rateRoundOffCheck));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.RDU_BAUDRATE
                , configEntity.rduBaudrate, oldConfiEntity.rduBaudrate));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.RDU_NAME,
                configEntity.rduName, oldConfiEntity.rduName));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.SECOND_MA_TYPE,
                configEntity.secondMAType, oldConfiEntity.secondMAType));


        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.SALES_BUFF_RATE
                , configEntity.salesBuffRate, oldConfiEntity.salesBuffRate));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.SALES_COW_RATE
                , configEntity.salesCowRate, oldConfiEntity.salesCowRate));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.SECOND_MA_BAUDRATE,
                configEntity.secondMABaudrate, oldConfiEntity.secondMABaudrate));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.SEND_SHIFT_MAILS,
                configEntity.sendShiftMails, oldConfiEntity.sendShiftMails));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.SELECT_RATE_CHART_TYPE
                , configEntity.selectRateChartType, oldConfiEntity.selectRateChartType));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.SET_AMCU_SERVER,
                configEntity.setAmcuServer, oldConfiEntity.setAmcuServer));


        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.SMART_AMCU_EMAIL
                , configEntity.smartAmcuEmail, oldConfiEntity.smartAmcuEmail));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.TARE_COMMAND
                , configEntity.tareCommand, oldConfiEntity.tareCommand));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.TARE_DEVIATION_WEIGHT,
                configEntity.tareDeviationWeight, oldConfiEntity.tareDeviationWeight));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.URL_HEADER,
                configEntity.urlHeader, oldConfiEntity.urlHeader));


        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.USER_CONFIG
                , configEntity.userConfig, oldConfiEntity.userConfig));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.VISIBLITY_REPORT_EDITING
                , configEntity.visiblityReportEditing, oldConfiEntity.visiblityReportEditing));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.VISIBILITY_CAN_TOGGLING,
                configEntity.visibilityCanToggling, oldConfiEntity.visibilityCanToggling));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.WEIGHING_SCALE,
                configEntity.weighingScale, oldConfiEntity.weighingScale));


        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.WEIGHING_DATBITS
                , configEntity.weighingDatbits, oldConfiEntity.weighingDatbits));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.WEIGHING_DIVISION_FACTOR
                , configEntity.weighingDivisionFactor, oldConfiEntity.weighingDivisionFactor));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.WEIGHING_SCALE_PREFIX,
                configEntity.weighingScalePrefix, oldConfiEntity.weighingScalePrefix));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.WEIGHING_PARITY,
                configEntity.weighingParity, oldConfiEntity.weighingParity));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.WEIGHING_SCALE_SEPARATOR
                , configEntity.weighingScaleSeparator, oldConfiEntity.weighingScaleSeparator));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.WEIGHING_SCALE_SUFFIX
                , configEntity.weighingScaleSuffix, oldConfiEntity.weighingScaleSuffix));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.WEIGHT_ROUND_OFF,
                configEntity.weightRoundOff, oldConfiEntity.weightRoundOff));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.WEIGHT_ROUND_OFF_CHECK,
                configEntity.weightRoundOffCheck, oldConfiEntity.weightRoundOffCheck));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.WEIGHING_STOPBITS,
                configEntity.weighingStopbits, oldConfiEntity.weighingStopbits));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.IGNORE_ZERO_FAT_SNF
                , configEntity.ignoreZeroFatSNF, oldConfiEntity.ignoreZeroFatSNF));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.MANAGER_MOBILE_NUMBER,
                configEntity.managerMobileNumber, oldConfiEntity.managerMobileNumber));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.MANAGER_PASSWORD,
                configEntity.managerPassword, oldConfiEntity.managerPassword));
        ///
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.MANAGER_EMAIL_ID,
                configEntity.managerEmailID, oldConfiEntity.managerEmailID));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.OPERATOR_PASSWORD,
                configEntity.operatorPassword, oldConfiEntity.operatorPassword));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.OPERATOR_MOBILE_NUMBER,
                configEntity.operatorMobileNumber, oldConfiEntity.operatorMobileNumber));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.CENTER_ADDRESS,
                configEntity.centerAddress, oldConfiEntity.centerAddress));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.CENTER_BMCID,
                configEntity.centerBMCID, oldConfiEntity.centerBMCID));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.CENTER_CODE,
                configEntity.centerCode, oldConfiEntity.centerCode));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.CENTER_NAME,
                configEntity.centerName, oldConfiEntity.centerName));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.CENTER_CONTACT_EMAIL_1,
                configEntity.centerContactEmail1, oldConfiEntity.centerContactEmail1));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.CENTER_CONTACT_EMAIL_2,
                configEntity.centerContactEmail2, oldConfiEntity.centerContactEmail2));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.CENTER_CONTACT_MOBILE_1,
                configEntity.centerContactMobile1, oldConfiEntity.centerContactMobile1));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.CENTER_CONTACT_MOBILE_2,
                configEntity.centerContactMobile2, oldConfiEntity.centerContactMobile2));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.CENTER_CONTACT_PERSON_1,
                configEntity.centerContactPerson1, oldConfiEntity.centerContactPerson1));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.CENTER_CONTACT_PERSON_2,
                configEntity.centerContactPerson2, oldConfiEntity.centerContactPerson2));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.MAX_WEIGHING_SCALE_LIMIT,
                configEntity.maxWeighingScaleLimit, oldConfiEntity.maxWeighingScaleLimit));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.WEIGHING_BAUDRATE,
                configEntity.WeighingBaudrate, oldConfiEntity.WeighingBaudrate));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.SHUT_DOWN_DELAY,
                configEntity.shutDownDelay, oldConfiEntity.shutDownDelay));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.CENTER_LOCATION,
                configEntity.centerLocation, oldConfiEntity.centerLocation));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.CENTER_ROUTE,
                configEntity.centerRoute, oldConfiEntity.centerRoute));


        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.IS_RATE_CALCULATED_FROM_DEVICE,
                configEntity.isRateCalculatedFromDevice,
                oldConfiEntity.isRateCalculatedFromDevice));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.SMS_FOOTER,
                configEntity.smsFooter, oldConfiEntity.smsFooter));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.SMART_CC_FEATURE,
                configEntity.smartCCFeature,
                oldConfiEntity.smartCCFeature));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.ENABLE_SIMLOCK_TO_DEVICE,
                configEntity.enableSimlockToDevice,
                oldConfiEntity.enableSimlockToDevice));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.ENABLE_REJECT_TO_DEVICE,
                configEntity.enableRejectToDevice,
                oldConfiEntity.enableRejectToDevice));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.SIMLOCK_PASSWORD,
                configEntity.simlockPassword,
                oldConfiEntity.simlockPassword));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.AMOUNT_ROUND_OFF_CHECK,
                configEntity.amountRoundOffCheck,
                oldConfiEntity.amountRoundOffCheck));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.INCENTIVE_PERCENTAGE,
                configEntity.incentivePercentage,
                oldConfiEntity.incentivePercentage));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.ENABLE_SEQUENCE_NUMBER_REPORT,
                configEntity.enableSequenceNumberReport,
                oldConfiEntity.enableSequenceNumberReport));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.SNF_CONSTANT_FOR_CLR,
                configEntity.snfConstantForClr,
                oldConfiEntity.snfConstantForClr));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.ENABLE_FILLED_OR_EMPTY_CANS,
                configEntity.enableFilledOrEmptyCans,
                oldConfiEntity.enableFilledOrEmptyCans));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.ENABLE_OR_DISABLE_IP_TABLE,
                configEntity.enableOrDisableIpTable,
                oldConfiEntity.enableOrDisableIpTable));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.RDU_QUANTITY_ROUND_OFF,
                configEntity.rduQuantityRoundOff, oldConfiEntity.rduQuantityRoundOff));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.SNF_OR_CLR_FOR_TANKER,
                configEntity.snfOrClrForTanker, oldConfiEntity.snfOrClrForTanker));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.WEIGHT_RECORD_LENGTH,
                configEntity.weightRecordLength, oldConfiEntity.weightRecordLength));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.SAMPLE_AS_COLLECTION,
                configEntity.sampleAsCollection, oldConfiEntity.sampleAsCollection));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.MERGED_HMB_ENABLE,
                configEntity.mergedHMBEnable, oldConfiEntity.mergedHMBEnable));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.LACTOSCAN_PRAMS,
                configEntity.lactoscanPrams, oldConfiEntity.lactoscanPrams));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.IS_CLOUD_SUPPORT,
                configEntity.isCloudSupport, oldConfiEntity.isCloudSupport));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.UNSENT_ALERT_LIMIT,
                configEntity.unsentAlertLimit, oldConfiEntity.unsentAlertLimit));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.DEVICE_PWD,
                configEntity.devicePwd, oldConfiEntity.devicePwd));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.WHITE_LIST_URL,
                configEntity.whiteListURL, oldConfiEntity.whiteListURL));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.DEVICE_ID,
                configEntity.deviceId, oldConfiEntity.deviceId));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.CALIBRATION_DATE,
                configEntity.calibrationDate, oldConfiEntity.calibrationDate));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.CREATE_EDIT,
                configEntity.createEdit, oldConfiEntity.createEdit));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.FARM_ID_DIGIT,
                configEntity.farmIdDigit, oldConfiEntity.farmIdDigit));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.MA_PARITY,
                configEntity.maParity, oldConfiEntity.maParity));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.MA_DATABITS,
                configEntity.maDatabits, oldConfiEntity.maDatabits));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.MA_STOPBITS,
                configEntity.mastopbits, oldConfiEntity.mastopbits));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.PERIODIC_DATA_SENT,
                configEntity.periodicDataSent, oldConfiEntity.periodicDataSent));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.START_TIME_DATA_SENT,
                configEntity.startTimeDataSent, oldConfiEntity.startTimeDataSent));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.PERIODIC_DEVICE_DATA_SEND,
                configEntity.periodicDeviceDataSend, oldConfiEntity.periodicDeviceDataSend));


        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.ALLOW_EDIT_REPORT,
                configEntity.allowOperatorToReportEditing, oldConfiEntity.allowOperatorToReportEditing));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.NUMBER_OF_EDITABLE_SHIFT,
                configEntity.numberOfEditableShift, oldConfiEntity.numberOfEditableShift));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.UPDATE_RATE_MIDDLE_SESSION,
                configEntity.updateRateMiddleSession, oldConfiEntity.updateRateMiddleSession));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.MCC_OR_ABC,
                configEntity.mCCorABC, oldConfiEntity.mCCorABC));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.TRIP_CODE_START_INDEX,
                configEntity.tripCodeStartIndex, oldConfiEntity.tripCodeStartIndex));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.MAX_TRIP,
                configEntity.maxTrip, oldConfiEntity.maxTrip));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.KEY_START_TANKER_ID,
                configEntity.startTankerIndex, oldConfiEntity.startTankerIndex));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.KEY_PER_TANKER_LIMIT,
                configEntity.perDayTankerLimit, oldConfiEntity.perDayTankerLimit));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.KEY_TARE_ON_START_COLLECTION,
                configEntity.isTareOnStart, oldConfiEntity.isTareOnStart));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.LICENSE_JSON, configEntity.licenceJson,
                oldConfiEntity.licenceJson));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.MILK_TYPE, configEntity.milkType,
                oldConfiEntity.milkType));

        allConfigPushList.add(getConfigurationPushList(AmcuConfig.KEY_SET_SERVER, configEntity.setAmcuServer,
                oldConfiEntity.setAmcuServer));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.CALIBRATION_DATE, configEntity.calibrationDate,
                oldConfiEntity.calibrationDate));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.MA_PARITY, configEntity.maParity,
                oldConfiEntity.maParity));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.MA_STOPBITS, configEntity.mastopbits,
                oldConfiEntity.mastopbits));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.MA_DATABITS, configEntity.maDatabits,
                oldConfiEntity.maDatabits));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.KEY_MA1_PARITY, configEntity.ma1Parity,
                oldConfiEntity.ma1Parity));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.KEY_MA1_STOP_BITS, configEntity.ma1stopbits,
                oldConfiEntity.ma1stopbits));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.KEY_MA1_DATA_BITS, configEntity.ma1Databits,
                oldConfiEntity.ma1Databits));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.KEY_MA2_PARITY, configEntity.ma2Parity,
                oldConfiEntity.ma2Parity));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.KEY_MA2_STOP_BITS, configEntity.ma2stopbits,
                oldConfiEntity.ma2stopbits));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.KEY_MA2_DATA_BITS, configEntity.ma2Databits,
                oldConfiEntity.ma2Databits));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.KEY_DEFAULT_MILK_TYPE_BOTH, configEntity.defalutMilkTypeBoth,
                oldConfiEntity.defalutMilkTypeBoth));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.MCC_OR_ABC, configEntity.mCCorABC,
                oldConfiEntity.mCCorABC));
//        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.FARM_ID_DIGIT, configEntity.farmIdDigit,
//                oldConfiEntity.farmIdDigit));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.PERIODIC_DATA_SENT, configEntity.periodicDataSent,
                oldConfiEntity.periodicDataSent));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.START_TIME_DATA_SENT, configEntity.startTimeDataSent,
                oldConfiEntity.startTimeDataSent));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.NUMBER_OF_EDITABLE_SHIFT, configEntity.numberOfEditableShift,
                oldConfiEntity.numberOfEditableShift));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.TRIP_CODE_START_INDEX, configEntity.tripCodeStartIndex,
                oldConfiEntity.tripCodeStartIndex));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.KEY_START_TANKER_ID, configEntity.startTankerIndex,
                oldConfiEntity.startTankerIndex));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.MAX_TRIP, configEntity.maxTrip,
                oldConfiEntity.maxTrip));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.KEY_PER_TANKER_LIMIT, configEntity.perDayTankerLimit,
                oldConfiEntity.perDayTankerLimit));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.PERIODIC_DEVICE_DATA_SEND, configEntity.periodicDeviceDataSend,
                oldConfiEntity.periodicDeviceDataSend));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.CREATE_EDIT, configEntity.createEdit,
                oldConfiEntity.createEdit));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.ALLOW_EDIT_REPORT, configEntity.allowOperatorToReportEditing,
                oldConfiEntity.allowOperatorToReportEditing));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.UPDATE_RATE_MIDDLE_SESSION, configEntity.updateRateMiddleSession,
                oldConfiEntity.updateRateMiddleSession));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.KEY_TARE_ON_START_COLLECTION, configEntity.isTareOnStart,
                oldConfiEntity.isTareOnStart));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.KEY_SHOW_UNSENT_ALERT, configEntity.showUnsentAlert,
                oldConfiEntity.showUnsentAlert));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.KEY_ESCAPE_ENABLE_COLLECTION, configEntity.escapeEnableForCollection,
                oldConfiEntity.escapeEnableForCollection));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.IGNORE_ZERO_FAT_SNF, configEntity.ignoreZeroFatSNF,
                oldConfiEntity.ignoreZeroFatSNF));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.KEY_MANDATORY_RATE_CHART, configEntity.isMandatoryRatechart
                ,
                oldConfiEntity.isMandatoryRatechart
        ));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.KEY_MIN_FAT_REJECT_COW, configEntity.keyMinFatRejectCow
                ,
                oldConfiEntity.keyMinFatRejectCow
        ));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.KEY_MIN_SNF_REJECT_COW, configEntity.keyMinSnfRejectCow
                ,
                oldConfiEntity.keyMinSnfRejectCow
        ));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.KEY_MAX_FAT_REJECT_COW, configEntity.keyMaxFatRejectCow
                ,
                oldConfiEntity.keyMaxFatRejectCow
        ));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.KEY_MAX_SNF_REJECT_COW, configEntity.keyMaxSnfRejectCow
                ,
                oldConfiEntity.keyMaxSnfRejectCow
        ));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.KEY_MAX_LACTOSE_REJECT_COW, configEntity.keyMaxLactoseRejectCow
                ,
                oldConfiEntity.keyMaxLactoseRejectCow
        ));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.KEY_PRINT_ENABLE_A4, configEntity.enablehpPrinter
                ,
                oldConfiEntity.enablehpPrinter
        ));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.KEY_SEND_EMAIL_CONFIGUREIDS, configEntity.sendEmaililConf
                ,
                oldConfiEntity.sendEmaililConf
        ));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.KEY_RATE_CHART_EXCEL_IMPORT, configEntity.rateExcelImport
                ,
                oldConfiEntity.rateExcelImport
        ));


        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.KEY_MIN_FAT_REJECT_BUFF, configEntity.keyMinFatRejectBuff
                ,
                oldConfiEntity.keyMinFatRejectBuff
        ));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.KEY_MIN_SNF_REJECT_BUFF, configEntity.keyMinSnfRejectBuff
                ,
                oldConfiEntity.keyMinSnfRejectBuff
        ));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.KEY_MAX_FAT_REJECT_BUFF, configEntity.keyMaxFatRejectBuff
                ,
                oldConfiEntity.keyMaxFatRejectBuff
        ));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.KEY_MAX_SNF_REJECT_BUFF, configEntity.keyMaxSnfRejectBuff
                ,
                oldConfiEntity.keyMaxSnfRejectBuff
        ));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.KEY_MAX_LACTOSE_REJECT_BUFF, configEntity.keyMaxLactoseRejectBuff
                ,
                oldConfiEntity.keyMaxLactoseRejectBuff
        ));


        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.KEY_MIN_FAT_REJECT_MIX, configEntity.keyMinFatRejectMix
                ,
                oldConfiEntity.keyMinFatRejectMix
        ));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.KEY_MIN_SNF_REJECT_MIX, configEntity.keyMinSnfRejectMix
                ,
                oldConfiEntity.keyMinSnfRejectMix
        ));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.KEY_MAX_FAT_REJECT_MIX, configEntity.keyMaxFatRejectMix
                ,
                oldConfiEntity.keyMaxFatRejectMix
        ));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.KEY_MAX_SNF_REJECT_MIX, configEntity.keyMaxSnfRejectMix
                ,
                oldConfiEntity.keyMaxSnfRejectMix
        ));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.KEY_MAX_LACTOSE_REJECT_MIX, configEntity.keyMaxLactoseRejectMix
                ,
                oldConfiEntity.keyMaxLactoseRejectMix
        ));


        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.KEY_MA1_MILKTYPE, configEntity.maMilkType1
                ,
                oldConfiEntity.maMilkType1
        ));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.KEY_MA2_MILKTYPE, configEntity.maMilkType2
                ,
                oldConfiEntity.maMilkType2
        ));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.KEY_MA1_BAUDRATE, configEntity.ma1Baudrate
                ,
                oldConfiEntity.ma1Baudrate
        ));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.KEY_MA2_BAUDRATE, configEntity.ma2Baudrate
                ,
                oldConfiEntity.ma2Baudrate
        ));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.KEY_MA1_NAME, configEntity.ma1Name
                ,
                oldConfiEntity.ma1Name
        ));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.KEY_MA2_NAME, configEntity.ma2Name
                ,
                oldConfiEntity.ma2Name
        ));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.KEY_ALLOW_FAMER_INCREMENT, configEntity.keyAllowFormerIncrement
                ,
                oldConfiEntity.keyAllowFormerIncrement
        ));


        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.KEY_MORNING_START,
                configEntity.morningSessionStartTime
                ,
                oldConfiEntity.morningSessionStartTime
        ));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.KEY_EVENING_START,
                configEntity.eveningSessionStartTime
                ,
                oldConfiEntity.eveningSessionStartTime
        ));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.KEY_MORNING_END_TIME,
                configEntity.eveningSessionStartTime
                ,
                oldConfiEntity.eveningSessionStartTime
        ));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.KEY_EVENING_END_TIME,
                configEntity.eveningSessionEndTime
                ,
                oldConfiEntity.eveningSessionEndTime
        ));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.KEY_ALLOW_AGENT_SPLIT,
                configEntity.allowAgentSplit
                ,
                oldConfiEntity.allowAgentSplit
        ));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.ALLOW_EDIT_COLLECTION,
                configEntity.allowEditCollection
                ,
                oldConfiEntity.allowEditCollection
        ));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.KEY_ALLOW_PROTEIN_VALUE,
                configEntity.allowProteinValue
                ,
                oldConfiEntity.allowProteinValue
        ));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.KEY_ALLOW_INCENTIVE_RATE_CHART_NAME,
                configEntity.incentiveRateChartName
                ,
                oldConfiEntity.incentiveRateChartName
        ));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.KEY_ALLOW_TWO_DECIMAL,
                configEntity.allowTwoDecimal
                ,
                oldConfiEntity.allowTwoDecimal
        ));


        //Configuration for quality parameters
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.KEY_DISPLAY_FAT_CONFIGURATION,
                configEntity.displayFATConfiguration
                ,
                oldConfiEntity.displayFATConfiguration
        ));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.KEY_DISPLAY_SNF_CONFIGURATION,
                configEntity.displaySNFConfiguration
                ,
                oldConfiEntity.displaySNFConfiguration
        ));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.KEY_DISPLAY_CLR_CONFIGURATION,
                configEntity.displayCLRConfiguration
                ,
                oldConfiEntity.displayCLRConfiguration
        ));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.KEY_DISPLAY_PROTEIN_CONFIGURATION,
                configEntity.displayProteinConfiguration
                ,
                oldConfiEntity.displayProteinConfiguration
        ));


        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.KEY_RATE_FAT_CONFIGURATION,
                configEntity.rateFATConfiguration
                ,
                oldConfiEntity.rateFATConfiguration
        ));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.KEY_RATE_SNF_CONFIGURATION,
                configEntity.rateSNFConfiguration
                ,
                oldConfiEntity.rateSNFConfiguration
        ));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.KEY_RATE_CLR_CONFIGURATION,
                configEntity.rateCLRConfiguration
                ,
                oldConfiEntity.rateCLRConfiguration
        ));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.KEY_RATE_PROTEIN_CONFIGURATION,
                configEntity.rateProteinConfiguration
                ,
                oldConfiEntity.rateProteinConfiguration
        ));


        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.KEY_ROUND_FAT_CONFIGURATION,
                configEntity.roundFATConfiguration
                ,
                oldConfiEntity.roundFATConfiguration
        ));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.KEY_ROUND_SNF_CONFIGURATION,
                configEntity.roundSNFConfiguration
                ,
                oldConfiEntity.roundSNFConfiguration
        ));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.KEY_ROUND_CLR_CONFIGURATION,
                configEntity.roundCLRConfiguration
                ,
                oldConfiEntity.roundCLRConfiguration
        ));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.KEY_ROUND_PROTEIN_CONFIGURATION,
                configEntity.roundProteinConfiguration
                ,
                oldConfiEntity.roundProteinConfiguration
        ));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.KEY_ALLOW_DISPLAY_RATE,
                configEntity.allowDisplayRate
                ,
                oldConfiEntity.allowDisplayRate
        ));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.KEY_RDU_PASSWORD,
                configEntity.rduPassword,
                oldConfiEntity.rduPassword));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.KEY_HOTSPOT_SSID,
                configEntity.hotspotSsid,
                oldConfiEntity.hotspotSsid));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.KEY_ENABLE_HOTSPOT,
                configEntity.enableHotspot,
                oldConfiEntity.enableHotspot));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.ENABLE_LACTOSE_BASED_REJECTION,
                configEntity.enableLactoseBasedRejection
                , oldConfiEntity.enableLactoseBasedRejection
        ));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.ENABLE_DYNAMIC_RATE_CHART,
                configEntity.enableDynamicRateChart
                , oldConfiEntity.enableDynamicRateChart
        ));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.KEY_ALLOW_FARMER_CREATE,
                configEntity.allowFarmerCreate,
                oldConfiEntity.allowFarmerCreate
        ));

        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.KEY_ALLOW_FARMER_EDIT,
                configEntity.allowFarmerEdit,
                oldConfiEntity.allowFarmerEdit
        ));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.KEY_WS_IGNORE_THRESHOLD,
                configEntity.wsIgnoreThreshold,
                oldConfiEntity.wsIgnoreThreshold));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.KEY_WS_KG_COMMAND,
                configEntity.wsKgCommand,
                oldConfiEntity.wsKgCommand));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.KEY_WS_LITRE_COMMAND,
                configEntity.wsLitreCommand,
                oldConfiEntity.wsLitreCommand));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.KEY_ALLOW_DISPLAY_NAME_IN_REPORT,
                configEntity.allowNameInReport,
                oldConfiEntity.allowNameInReport
        ));
        allConfigPushList.add(getConfigurationPushList(ConfigurationConstants.KEY_DISPLAY_DAIRY_NAME,
                configEntity.displayDairyname,
                oldConfiEntity.displayDairyname
        ));
        return allConfigPushList;

    }

    public void setCustomFieldEntitiesFromJson(String jsonString) {

        ArrayList<CustomFieldEntity> cFEL = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            for (int i = 0; i < jsonArray.length(); i++) {
                CustomFieldEntity cFE = new CustomFieldEntity();
                JSONObject jo = jsonArray.getJSONObject(i);
                cFE.name = jo.optString("name", "");
                cFE.displayName = jo.optString("displayName", "");
                cFE.type = jo.optString("type");
                cFE.validation = jo.optString("validation");
                cFE.hint = jo.optString("hint");
                cFE.editable = jo.optBoolean("editable");
                ArrayList<String> afL = new ArrayList<>();
                JSONArray afJA = jo.optJSONArray("applicableFor");
                if (afJA.length() > 0) {
                    for (int j = 0; j < afJA.length(); j++) {
                        JSONObject joAF = afJA.getJSONObject(j);
                        if (joAF.optBoolean("value", false)) {
                            afL.add(joAF.optString("name", ""));
                        }
                    }
                }
                cFE.applicableFor = afL;
                cFEL.add(cFE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AddtionalDatabase addtionalDatabase = DatabaseHandler.getDatabaseInstance().getAdditionallDatabase();
        addtionalDatabase.deleteAllCustomFields();
        addtionalDatabase.insertOrUpdateCustomFields(cFEL);
        Util.displayErrorToast("Addition Parameters Updated Successfully", mContext);
    }

}
