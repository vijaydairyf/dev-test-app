package com.devapp.devmain.server;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.devapp.smartcc.report.ReportHintConstant;

import java.util.Locale;
import java.util.Set;

public class SessionManager {
    // Email address
    public static final String KEY_EMAIL = "UserEmail";
    public static final String KEY_PASSWORD = "UserPassword";
    public static final String KEY_FIRST_LOGIN = "FirstLogin";
    public static final String KEY_FARMER_NAME = "FarmerName";
    public static final String KEY_FARMER_ID = "FarmerID";
    public static final String KEY_FARMER_MOBILE = "farmNumber";
    public static final String KEY_FARMER_BARCODE = "farmerBarcode";
    public static final String KEY_SAMPLE_MA = "isSampleMA";
    public static final String KEY_SAMPLE_WEIGH = "isSampleWeigh";
    public static final String KEY_IS_SAMPLE = "isSample";
    public static final String KEY_USER_ROLE = "userRole";
    public static final String KEY_USER_MOBILE = "userMobile";
    public static final String KEY_SOCIETY_NAME = "socName";
    public static final String KEY_SOCIETY_ID = "socId";
    public static final String KEY_COLLECTION_ID = "collectionId";
    // Sharedpref file name
    public static final String PREF_NAME = "LoginPref";
    // All Shared Preferences Keys
    private static final String KEY_IS_LOGIN = "IsLoggedIn";
    private static final String KEY_MANAGER_SOC_EMAIL = "socManagerEmail";
    private static final String KEY_IS_PASSWORD_REMEMBERED = "IsPasswordRemembered";
    private static final String KEY_IS_FARMER_UNRESTERED = "isUnregistered";
    private static final String KEY_TXN_NUMBER = "txnNumber";
    private static final String KEY_ALL_FILES = "allFiles";
    private static final String KEY_FILE_NAME = "file";
    private static final String KEY_BATTERY_STATUS = "batStatus";
    private static final String KEY_MESSAGE_COUNT = "msgCount";
    private static final String KEY_SET_TODAY_DATE = "setTodayDate";
    private static final String KEY_SET_REJECT = "reject";
    private static final String KEY_SET_ASSOC_SOCIETY = "assSociety";
    private static final String KEY_TEMP_FARMID = "tempFarmId";
    private static final String KEY_MANAGER_EMAIL = "managerEmail";
    private static final String KEY_MANAGER_NAME = "managerName";
    private static final String KEY_MANAGER_MOBILE = "managerMobile";
    private static final String KEY_OPERATOR_EMAIL = "operatorEmail";
    private static final String KEY_IS_SUNDAY = "isSunday";
    private static final String KEY_IS_FIRST = "isFirst";
    private static final String KEY_START_TIME = "startTime";
    private static final String KEY_END_TIME = "endTime";
    private static final String KEY_REPORT_SENT = "reportSent";
    private static final String KEY_RECENT_RATECHART = "recentRatechart";
    private static final String KEY_SESSION_FARMERS = "allsessionFarmer";
    private static final String KEY_EMAIL_SENT = "emailSent";
    private static final String KEY_REPORT_TYPE = "reportType";
    private static final String KEY_MORNING_START = "mrnStart";
    private static final String KEY_EVENING_START = "evnStart";
    private static final String KEY_SELECTED_LANGUAGE = "selLanguage";
    private static final String KEY_WEEKLY_SENT = "weekSent";
    private static final String KEY_MONTHLY_SENT = "monthSent";
    private static final String KEY_MAIL_WEEKLY_SENT = "MweekSent";
    private static final String KEY_MAIL_MONTHLY_SENT = "MmonthSent";
    private static final String KEY_RATE_CHART_VERSION = "rateChartVersion";
    private static final String KEY_MESSAGE_LIMIT = "messageLimit";
    private static final String KEY_SETTING_MSG = "settingMsg";
    private static final String KEY_SETMIN_FAT = "setMinFat";
    private static final String KEY_SETMIN_SNF = "setMinSnf";
    private static final String KEY_SETMIN_CLR = "setMinClr";
    private static final String KEY_SETMAX_FAT = "setMaxFat";
    private static final String KEY_SETMAX_SNF = "setMaxSnf";
    private static final String KEY_SETMAX_CLR = "setMaxClr";
    private static final String KEY_SETEKOMILK = "setEKOMILK";
    private static final String KEY_SUPPORT_UNREG = "supportUnreg";
    private static final String KEY_SUPPORT_KEYBOARD = "supportKEY";
    private static final String KEY_MILK_TYPE = "millkType";
    private static final String KEY_SOCIETY_COLUMN_ID = "columId";
    private static final String KEY_UPDATED_RATECHART_URI = "updatedRateChartURI";
    private static final String KEY_SET_MA_TYPE = "setMATYPE";
    private static final String KEY_LAST_COLLECTION_RECORD = "lastCollectionRecord";
    private static final String KEY_LAST_TEMPERORY_RECORD = "lastTempRecord";
    private static final String KEY_CHECK_FARMER_OR_BARCODE = "farmOrBarcode";
    private static final String KEY_GET_SMS_COUNT = "getSMSCount";
    private static final String KEY_IS_CHILLING_CENTER = "isCenterChilling";
    private static final String KEY_COMING_FROM = "startCollection";
    private static final String KEY_CENTER_ROUTE = "centerRoute";
    private static final String KEY_IS_LAST_MA_COLLECTION = "lastMaCollection";
    private static final String KEY_IS_COMPLETE = "isComplete";
    private static final String KEY_IS_MCC = "isMCC";


    private static final String KEY_SET_LAST_MEMBER_SORTING = "lastMemberSorting";
    private static final String KEY_SET_LAST_MCC_SORTING = "lastMCCSorting";

    private static final String KEY_LAST_MEMBER_SORTING_ORDER = "lastMemberSortingOrder";
    private static final String KEY_LAST_MCC_SORTING_ORDER = "lastMccSortingOrder";

    private static final String KEY_BLUETOOTH_ENABLE = "keyBluetoothEnable";


    private static final String KEY_IS_MENU_ENABLE = "isMenuEnable";

    // Shared Preferences
    SharedPreferences pref;
    // Editor for Shared preferences
    Editor editor;
    // Context
    Context _context;
    // Shared pref mode
    int PRIVATE_MODE = 0;


    // Constructor
    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     */
    public void createLoginSession(String email, String password) {
        editor.putBoolean(KEY_IS_LOGIN, true);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PASSWORD, password);


        // commit changes
        editor.commit();
    }

    public String getUserId() {

        if (pref.getString(KEY_EMAIL, null) != null) {
            return pref.getString(KEY_EMAIL, null).toUpperCase(Locale.ENGLISH);
        } else {
            return pref.getString(KEY_EMAIL, null);
        }
    }

    public String getUserPassword() {
        return pref.getString(KEY_PASSWORD, null);
    }

    /**
     * Clear session details
     */
    public void logoutUser() {
        // Clearing all data from Shared Preferences
        editor.putBoolean(KEY_IS_LOGIN, false);
        editor.putString(KEY_EMAIL, null);
        editor.putString(KEY_PASSWORD, "");
        editor.commit();
        // SynchronizedArrayList.getInstance().clear();
    }

    /**
     * Quick check for login
     **/
    // Get Login State
    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGIN, false);
    }

    // ///////////////Remember password/////////////////////
    public void setIsRememberPassword() {
        editor.putBoolean(KEY_IS_PASSWORD_REMEMBERED, true);
        // commit changes
        editor.commit();
    }

    public boolean isPasswordRemembered() {
        return pref.getBoolean(KEY_IS_PASSWORD_REMEMBERED, false);
    }

    public String getFarmerName() {
        return pref.getString(KEY_FARMER_NAME, "Farmer");

    }

    public void setFarmerName(String farmerName) {
        editor.putString(KEY_FARMER_NAME, farmerName);
        editor.commit();
    }

    public String getFarmerID() {
        return pref.getString(KEY_FARMER_ID, "FarmerID");

    }

    public void setFarmerID(String farmerId) {
        editor.putString(KEY_FARMER_ID, farmerId);
        editor.commit();
    }

    public String getFarmerBarcode() {
        return pref.getString(KEY_FARMER_BARCODE, "");
    }

    public void setFarmerBarcode(String farmerBarcode) {
        editor.putString(KEY_FARMER_BARCODE, farmerBarcode);
        editor.commit();
    }

    // Get Login State
    public boolean isFirstLogin() {
        return pref.getBoolean(KEY_FIRST_LOGIN, true);
    }

    public void setFirstLogin(boolean b) {
        editor.putBoolean(KEY_FIRST_LOGIN, b);
        // commit changes
        editor.commit();
    }

    public String getUserRole() {
        return pref.getString(KEY_USER_ROLE, "operator");

    }

    public void setUserRole(String userRole) {
        editor.putString(KEY_USER_ROLE, userRole);
        editor.commit();
    }

    public String getUserMobile() {
        return pref.getString(KEY_USER_MOBILE, "1234567890");

    }

    public void setUserMobile(String mobile) {
        editor.putString(KEY_USER_MOBILE, mobile);
        editor.commit();
    }

    public String getSocietyName() {
        return pref.getString(KEY_SOCIETY_NAME, "society");

    }

    public void setSocietyName(String socName) {
        editor.putString(KEY_SOCIETY_NAME, socName);
        editor.commit();
    }

    public String getCollectionID() {
        // return pref.getString(KEY_SOCIETY_ID, "societyID");
        DatabaseHandler db = DatabaseHandler.getDatabaseInstance();
        return db.getSocietyID();

    }

    public void setCollectionID(String socId) {
        editor.putString(KEY_SOCIETY_ID, socId);
        editor.commit();
    }

    public long getSocietyColumnId() {
        return pref.getLong(KEY_SOCIETY_COLUMN_ID, -1);

    }

    public void setSocietyColumnId(long socId) {
        editor.putLong(KEY_SOCIETY_COLUMN_ID, socId);
        editor.commit();
    }

    public boolean getUnregistered() {
        return pref.getBoolean(KEY_IS_FARMER_UNRESTERED, false);

    }

    public void setUnregistered(boolean isUnreg) {
        editor.putBoolean(KEY_IS_FARMER_UNRESTERED, isUnreg);
        editor.commit();
    }

    public String getFarmNumber() {
        return pref.getString(KEY_FARMER_MOBILE, "9535740256");

    }

    public void setFarmNumber(String mobNumber) {
        editor.putString(KEY_FARMER_MOBILE, mobNumber);
        editor.commit();
    }

    public int getTXNumber() {
        return pref.getInt(KEY_TXN_NUMBER, 0);
    }

    public void setTXNumber(int txNumber) {
        editor.putInt(KEY_TXN_NUMBER, txNumber);
        editor.commit();
    }

    public Set<String> getAllFiles() {
        return pref.getStringSet(KEY_ALL_FILES, null);
    }

    public void setAllFiles(Set<String> fileName) {
        editor.putStringSet(KEY_ALL_FILES, fileName);
        editor.commit();
    }

    public String getFileName() {

        return pref.getString(KEY_FILE_NAME, "");
    }

    public void setFileName(String file) {
        editor.putString(KEY_FILE_NAME, file);
        editor.commit();

    }

    public void setBatStatus(int status) {
        editor.putInt(KEY_BATTERY_STATUS, status);
        editor.commit();
    }

    public int getBatstatus() {
        return pref.getInt(KEY_BATTERY_STATUS, 0);
    }

    public int getMessageCount() {
        return pref.getInt(KEY_MESSAGE_COUNT, 0);
    }

    public void setMessageCount(int count) {
        editor.putInt(KEY_MESSAGE_COUNT, count);
        editor.commit();
    }

    public String getTodayDate() {
        return pref.getString(KEY_SET_TODAY_DATE, "Date");
    }

    public void setTodayDate(String today) {
        editor.putString(KEY_SET_TODAY_DATE, today);
        editor.commit();
    }

    public String getMilkReject() {
        return pref.getString(KEY_SET_REJECT, "NO");
    }

    public void setMilkReject(String Reject) {
        editor.putString(KEY_SET_REJECT, Reject);
        editor.commit();
    }

    public String getAssociateSociety() {
        return pref.getString(KEY_SET_ASSOC_SOCIETY, null);
    }

    public void setAssociateSociety(String assoc) {
        editor.putString(KEY_SET_ASSOC_SOCIETY, assoc);
        editor.commit();
    }

    public void setTempFarmid(String farmId) {
        editor.putString(KEY_TEMP_FARMID, farmId);
        editor.commit();
    }

    public String setTempFarmid() {
        return pref.getString(KEY_TEMP_FARMID, null);
    }

    public String getManagerEmailID() {
        return pref.getString(KEY_MANAGER_EMAIL,
                "");
    }

    public void setManagerEmailID(String managerEmail) {
        editor.putString(KEY_MANAGER_EMAIL, managerEmail);
        editor.commit();
    }

    public String getOperatorEmail() {
        return pref.getString(KEY_OPERATOR_EMAIL, null);
    }

    public void setOperatorEmail(String operatorEmail) {
        editor.putString(KEY_OPERATOR_EMAIL, operatorEmail);
        editor.commit();
    }

    public boolean getTodaySunday() {
        return pref.getBoolean(KEY_IS_SUNDAY, false);
    }

    public void setTodaySunday(boolean sunday) {
        editor.putBoolean(KEY_IS_SUNDAY, sunday);
        editor.commit();
    }

    public boolean getTodayFirstDay() {
        return pref.getBoolean(KEY_IS_FIRST, false);
    }

    public void setTodayFirstDay(boolean firstDay) {
        editor.putBoolean(KEY_IS_FIRST, firstDay);
        editor.commit();
    }


    public void setSendReport(boolean sendReport) {
        editor.putBoolean(KEY_REPORT_SENT, sendReport);
        editor.commit();
    }

    public boolean getSentReport() {
        return pref.getBoolean(KEY_REPORT_SENT, true);
    }

    public String getRecentRateChart() {
        return pref.getString(KEY_RECENT_RATECHART, "rateChart");

    }

    public void setRecentRateChart(String rateChart) {
        editor.putString(KEY_RECENT_RATECHART, rateChart);
        editor.commit();

    }

    public Set<String> getAllSessionFarmer() {
        return pref.getStringSet(KEY_SESSION_FARMERS, null);
    }

    public void setAllSessionFarmer(Set<String> allFarmer) {
        editor.putStringSet(KEY_SESSION_FARMERS, allFarmer);
        editor.commit();
    }

    public boolean getDailyEmailSent() {
        return pref.getBoolean(KEY_EMAIL_SENT, false);
    }

    public void setDailyEmailSent(boolean sent) {
        editor.putBoolean(KEY_EMAIL_SENT, sent);
        editor.commit();
    }

    public int getReportType() {
        return pref.getInt(KEY_REPORT_TYPE, 0);
    }

    public void setReportType(int type) {
        editor.putInt(KEY_REPORT_TYPE, type);
        editor.commit();
    }


    public String getSelectedLanguage() {
        return pref.getString(KEY_SELECTED_LANGUAGE, "English");
    }

    public void setSelectedLanguage(String language) {
        editor.putString(KEY_SELECTED_LANGUAGE, language);
        editor.commit();
    }

    public int getRateChartVersion() {
        return pref.getInt(KEY_RATE_CHART_VERSION, 0);
    }

    public void setRateChartVersion(int version) {
        editor.putInt(KEY_RATE_CHART_VERSION, version);

        editor.commit();
    }

    public boolean getWeeklySent() {
        return pref.getBoolean(KEY_WEEKLY_SENT, false);
    }

    public void setWeeklySent(boolean sent) {
        editor.putBoolean(KEY_WEEKLY_SENT, sent);
        editor.commit();
    }

    public boolean getMonthlySent() {
        return pref.getBoolean(KEY_MONTHLY_SENT, false);
    }

    public void setMonthlySent(boolean sent) {
        editor.putBoolean(KEY_MONTHLY_SENT, sent);
        editor.commit();
    }

    public boolean getMailWeeklySent() {
        return pref.getBoolean(KEY_MAIL_WEEKLY_SENT, true);
    }

    public void setMailWeeklySent(boolean sent) {
        editor.putBoolean(KEY_MAIL_WEEKLY_SENT, sent);
        editor.commit();
    }

    public boolean getMailMonthlySent() {
        return pref.getBoolean(KEY_MAIL_MONTHLY_SENT, true);
    }

    public void setMailMonthlySent(boolean sent) {
        editor.putBoolean(KEY_MAIL_MONTHLY_SENT, sent);
        editor.commit();
    }

    public String getManagerName() {
        return pref.getString(KEY_MANAGER_NAME, "Manager");
    }

    public void setManagerName(String name) {
        editor.putString(KEY_MANAGER_NAME, name);
        editor.commit();
    }

    public String getManagerMobile() {
        return pref.getString(KEY_MANAGER_MOBILE, "9535740256");
    }

    public void setManagerMobile(String name) {
        editor.putString(KEY_MANAGER_MOBILE, name);
        editor.commit();
    }

    public int getPerDayMessageLimit() {
        return pref.getInt(KEY_MESSAGE_LIMIT, 0);

    }

    public void setPerDayMessageLimit(int limit) {
        editor.putInt(KEY_MESSAGE_LIMIT, limit);
        editor.commit();
    }

    public String getSettingMessage() {
        return pref.getString(KEY_SETTING_MSG, "");

    }

    public void setSettingMessage(String msg) {
        editor.putString(KEY_SETTING_MSG, msg);
        editor.commit();
    }


    public boolean getEKOMILK() {
        return pref.getBoolean(KEY_SETEKOMILK, true);
    }

    public void setEKOMILK(boolean EKOMILK) {
        editor.putBoolean(KEY_SETEKOMILK, EKOMILK);
        editor.commit();
    }

    public boolean getSupportUnregisterUser() {
        return pref.getBoolean(KEY_SUPPORT_UNREG, false);
    }

    public void setSupportUnregisterUser(boolean unregUser) {
        editor.putBoolean(KEY_SUPPORT_UNREG, unregUser);
        editor.commit();
    }

    public void setSupportKeyboard(boolean keyboard) {
        editor.putBoolean(KEY_SUPPORT_KEYBOARD, keyboard);
        editor.commit();
    }

    public boolean getSupportKeyBoard() {
        return pref.getBoolean(KEY_SUPPORT_KEYBOARD, false);
    }

    public String getMilkType() {
        return pref.getString(KEY_MILK_TYPE, "COW");
    }

    public void setMilkType(String milkType) {
        editor.putString(KEY_MILK_TYPE, milkType);
        editor.commit();
    }

    public boolean getSampleMA() {
        return pref.getBoolean(KEY_SAMPLE_MA, true);
    }

    public void setSampleMA(boolean isMA) {
        editor.putBoolean(KEY_SAMPLE_MA, isMA);
        editor.commit();
    }

    public boolean getSampleWeigh() {
        return pref.getBoolean(KEY_SAMPLE_WEIGH, true);
    }

    public void setSampleWeigh(boolean isWeigh) {
        editor.putBoolean(KEY_SAMPLE_WEIGH, isWeigh);
        editor.commit();
    }

    public boolean getIsSample() {
        return pref.getBoolean(KEY_IS_SAMPLE, false);
    }

    public void setIsSample(boolean isSample) {
        editor.putBoolean(KEY_IS_SAMPLE, isSample);
        editor.commit();
    }

    public String getIsUpdatedRateChartURI() {
        return pref.getString(KEY_UPDATED_RATECHART_URI, null);
    }

    public void setIsUpdatedRateChartURI(String isSample) {
        editor.putString(KEY_UPDATED_RATECHART_URI, isSample);
        editor.commit();
    }

    public String getSocManagerEmail() {
        return pref.getString(KEY_MANAGER_SOC_EMAIL,
                "");
    }

    public void setSocManagerEmail(String managerEmail) {
        editor.putString(KEY_MANAGER_SOC_EMAIL, managerEmail);
        editor.commit();
    }

    public String getMAType() {
        return pref.getString(KEY_SET_MA_TYPE, "MA1");
    }

    public void setMAType(String MAType) {
        editor.putString(KEY_SET_MA_TYPE, MAType);
        editor.commit();
    }

    public String getReportData() {
        return pref.getString(KEY_LAST_COLLECTION_RECORD, null);
    }

    public void setReportData(String report) {
        editor.putString(KEY_LAST_COLLECTION_RECORD, report);
        editor.commit();
    }

    public boolean getFarmOrBarcode() {
        return pref.getBoolean(KEY_CHECK_FARMER_OR_BARCODE, false);
    }

    public void setFarmOrBarcode(boolean farmOrBarcode) {
        //if is false means button should be in auto mode
        editor.putBoolean(KEY_CHECK_FARMER_OR_BARCODE, farmOrBarcode);
        editor.commit();
    }

    public int getSMSCount() {
        return pref.getInt(KEY_GET_SMS_COUNT, 0);
    }

    public void setSMSCount(int smsCount) {
        editor.putInt(KEY_GET_SMS_COUNT, smsCount);
        editor.commit();
    }

    public boolean getIsChillingCenter() {
        return pref.getBoolean(KEY_IS_CHILLING_CENTER, false);
    }

    public void setIsChillingCenter(boolean isChilling) {
        editor.putBoolean(KEY_IS_CHILLING_CENTER, isChilling);
        editor.commit();
    }

    public String getComingFrom() {
        return pref.getString(KEY_COMING_FROM, null);
    }

    public void setComingFrom(String comFrom) {
        editor.putString(KEY_COMING_FROM, comFrom);
        editor.commit();

    }

    public String getCollectionCenterRoute()

    {
        return pref.getString(KEY_CENTER_ROUTE, "NA");
    }

    public void setCollectionCenterRoute(String route) {
        editor.putString(KEY_CENTER_ROUTE, route);
        editor.commit();
    }

    // For last ma collection
//    public void setIsLastMACollection(boolean isMaCollection) {
//
//        editor.putBoolean(KEY_IS_LAST_MA_COLLECTION,isMaCollection);
//        editor.commit();
//    }
//
//    public boolean getIsLastMACollection()
//    {
//
//        return pref.getBoolean(KEY_IS_LAST_MA_COLLECTION,false);
//    }


    //This Configuration commented in 11.6.1
    public String getMorningEndTime() {
        return pref.getString(KEY_START_TIME, "14:00");
    }


    public String getEveningEndTime() {
        return pref.getString(KEY_END_TIME, "02:00");
    }


    public String getMorningStart() {
        return pref.getString(KEY_MORNING_START, "00:00");
    }


    public String getEveningStart() {
        return pref.getString(KEY_EVENING_START, "15:00");
    }


    public boolean getRecordStatusComplete()

    {
        return pref.getBoolean(KEY_IS_COMPLETE, true);
    }

    public void setRecordStatusComplete(boolean isComplete) {
        editor.putBoolean(KEY_IS_COMPLETE, isComplete);
        editor.commit();
    }


    public boolean getMCCStatus()

    {
        return pref.getBoolean(KEY_IS_MCC, true);
    }

    public void setMCCStatus(boolean isMCC) {
        editor.putBoolean(KEY_IS_MCC, isMCC);
        editor.commit();
    }

    public String getKeySetLastMemberSorting() {
        return pref.getString(KEY_SET_LAST_MEMBER_SORTING, ReportHintConstant.COLL_TIME);
    }

    public void setKeySetLastMemberSorting(String sort)

    {
        editor.putString(KEY_SET_LAST_MEMBER_SORTING, sort);
        editor.commit();
    }

    public String getKeySetLastMccSorting() {

        return pref.getString(KEY_SET_LAST_MCC_SORTING, ReportHintConstant.COLL_TIME);
    }

    public void setKeySetLastMccSorting(String sort) {
        editor.putString(KEY_SET_LAST_MCC_SORTING, sort);
        editor.commit();
    }


    public String getKeySetLastMemberSortingOrder() {
        return pref.getString(KEY_LAST_MEMBER_SORTING_ORDER, "ASC");
    }

    public void setKeySetLastMemberSortingOrder(String order)

    {
        editor.putString(KEY_LAST_MEMBER_SORTING_ORDER, order);
        editor.commit();
    }

    public String getKeySetLastMccSortingOrder() {

        return pref.getString(KEY_LAST_MCC_SORTING_ORDER, "ASC");
    }

    public void setKeySetLastMccSortingOrder(String order) {
        editor.putString(KEY_LAST_MCC_SORTING_ORDER, order);
        editor.commit();
    }

    public boolean getKeyBluetoothEnable() {

        return pref.getBoolean(KEY_BLUETOOTH_ENABLE, false);
    }

    public void setKeyBluetoothEnable(boolean isEnable) {
        editor.putBoolean(KEY_BLUETOOTH_ENABLE, isEnable);
        editor.commit();
    }

    public boolean getKeyIsMenuEnable() {

        return pref.getBoolean(KEY_IS_MENU_ENABLE, false);
    }

    public void setKeyIsMenuEnable(boolean isEnable) {
        editor.putBoolean(KEY_IS_MENU_ENABLE, isEnable);
        editor.commit();
    }


}
