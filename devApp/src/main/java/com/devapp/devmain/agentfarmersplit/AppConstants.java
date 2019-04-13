package com.devapp.devmain.agentfarmersplit;

/**
 * Created by u_pendra on 20/4/17.
 */

public class AppConstants {

    public static final String DB_SEPERATOR = "=";

    public static final String FAT = "Fat";
    public static final String SNF = "SNF";
    public static final String CLR = "CLR";
    public static final String DEN = "Density";
    public static final String LAC = "Lactose";
    public static final String PROTEIN = "Protein";
    public static final String AWM = "Added Water";
    public static final String TEMP = "TEMP";
    public static final String SALT = "Salt";
    public static final String PH = "PH";
    public static final String FRP = "Freezing point";
    public static final String CAL = "Calibration";
    public static final String SN = "Serial number";
    public static final String CON = "Conductivity";

    public static final int HTTP_PORT = 80;
    public static final int HTTPS_PORT = 443;

    public static final int IS_FAT = 101;
    public static final int IS_SNF = 102;


    public static final int DRAWABLE_PADDING_DASHBOARD = 75;
    public static final float DASHBOARD_HINT = 20f;

    public static final int MAX_EMPTY_CAN_WEIGHT = 20;
    public static final int MAX_CAN_CAPACITY = 200;
    public static final int MAX_CANID_LENGTH = 15;
    public static final int MAX_CAN_LOCAL_ID_LENGTH = 4;
    public static final int MAX_MCC_CODE_LENGTH = 8;

    public static final String CAN_TYPE_ADD = "ADD";
    public static final String CAN_TYPE_DELETE = "DELETE";
    public static final String CAN_UPDATE_TYPE_SUCCESS = "SUCCESS";
    public static final String CAN_TYPE_UPDATE = "UPDATE";

    public static final String TANKER_TYPE_ADD = "ADD";
    public static final String TANKER_TYPE_DELETE = "DELETE";
    public static final String TANKER_UPDATE_TYPE_SUCCESS = "SUCCESS";
    public static final String TANKER_TYPE_UPDATE = "UPDATE";

    public static final String COLLECTION_SEPERATOR = " | ";

    public static final String TOTAL = "Total: ";
    public static final String TEST = "Test: ";
    public static final String UNSENT = "Unsent: ";
    public static final String QTY = "Qty: ";
    public static final String AUTO = "Auto";
    public static final String MANUAL = "Manual";

    //For testing purpose it should be 15 days
    public static final int DELETE_TANKER_ENTITY = -15;


    public static final String CONSOLIDATED_EXTENSION = "jstpl";
    public static final String HATSUN_SERVER = "amcu-gw.smartmoo.com";

    public static final String FARMER_TYPE_FARMER = "FARMER";
    public static final String FARMER_TYPE_AGENT = "Aggregate Farmer";

    public static final String KG_UNIT = "KG";
    public static final String LTR_UNIT = "LTR";

    public static final String NA = "NA";


    public static final String RATE_RUPEES = "Rs";
    public static final String TYPE_COLLECTION = "COLLECTION";
    public static final String TYPE_PROTEIN = "PROTEIN";

    public static final String JSTPL_EXTENSION = ".JSTPL";
    public static final int DRAWABLE_PADDING_TANKER_SEC = 180;
    public static final float COLLECTION_HINT = 25f;
    public static final float COLLECTION_HINT_TANKER = 20f;
    public static final int DRAWABLE_PADDING = 200;
    public static final int DRAWABLE_PADDING_TANKER = 120;
    public static final String DIG_DNS = "google.com";
    public static final String GENRIC_SERVER = "amcugw2.smartmoo.com";
    public static final String ROUND_UP = "UP";
    public static final String ROUND_DOWN = "DOWN";
    public static final String ROUND_CEILING = "CEILING";
    public static final String ROUND_FLOOR = "FLOOR";
    public static final String ROUND_HALF_UP = "HALF_UP";
    public static final String ROUND_HALF_DOWN = "HALF_DOWN";
    public static final String ROUND_HALF_EVEN = "HALF_EVEN";
    public static final String MILK_QUALITY_GOOD = "GOOD";
    public static final String MILK_QUALITY_NA = "NA";
    public static final int CONFIG_NEW_STATUS = 0;
    public static final int CONFIG_SUCCESS_STATUS = 1;
    public static final int CONFIG_PARTIAL_STATUS = 2;
    public static final int CONFIG_FAIL_STATUS = 3;
    public static boolean IS_SELECTED_AGGERATE_FARMER = false;

    public static interface RDU {
        String SMART = "SMART";
        String ESSAE = "ESSAE";
        String HATSUN = "HATSUN";
        String EVEREST = "EVEREST";
        String SMART2 = "SMART2";
        String STELLAPPS = "STELLAPPS";
        String STELLAPPSV2 = "STELLAPPSV2";
        String AKASHGANGA_16 = "AKASHGANGA-16";
        String AKASHGANGA_32 = "AKASHGANGA-32";
        String VECTOR = "VECTOR";
    }

    public static interface MA {
        String ESSAE = "ESSAE";
        String LACTOSCAN = "LACTOSCAN";
        String EKOMILK_ULTRA_PRO = "EKOMILK ULTRA PRO";
        String EKOMILK = "EKOMILK";
        String LM2 = "LM2";
        String LACTOPLUS = "LACTOPLUS";
        String KAMDHENU = "KAMDHENU";
        String AKASHGANGA = "AKASHGANGA";
        String INDIFOSS = "INDIFOSS";
        String NULINE = "NULINE";
        String EKOBOND = "EKOBOND";
        String LACTOSCAN_V2 = "LACTOSCAN_V2";
        String KSHEERAA = "KSHEERAA";
        String EKOMILK_EVEN = "EKOMILK EVEN";
        String EKOMILK_V2 = "EKOMILK_V2";
        String LAKTAN_240 = "LAKTAN_240";
    }

    public static interface ConfigurationTypes {

        String BASIC_PROFILE = "BASIC_PROFILE";
        String FARMER = "FARMER";
        String COLLECTION_CENTER_LIST = "COLLECTION_CENTER_LIST";
        String RATE_CHART = "RATE_CHART";
        String CONFIGURATION = "CONFIGURATION";
        String TRUCK = "TRUCK";
        String AGENT = "AGENT";
        String INCENTIVE_RATE_CHART = "INCENTIVE_RATE_CHART";
    }

    public interface Shift {
        String MORNING = "MORNING";
        String EVENING = "EVENING";
    }

    public interface BasicProfileType {

        String ORGANIZATION = "organization";
        String MCC = "mcc";
        String COLLECTION_CENTER = "collectionCenter";
        String ROUTE = "route";
    }

    public interface Regex {
        String NUMBER_DECIMAL = "[0-9]*[.]?[0-9]*";
        String NUMBER = "[0-9]*";
        String DATE_DD_MM_YYYY = "[0-9]{0,2}[-]{1}[0-9]{0,2}[-]{1}[0-9]{0,4}";
        String ALPHA_NUMERIC = "[a-zA-Z]";

    }
}
