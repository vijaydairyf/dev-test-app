package com.devapp.smartcc.report;

import com.devapp.smartcc.entityandconstants.SmartCCUtil;

/**
 * Created by u_pendra on 28/3/17.
 */

public class ReportHintConstant {

    public static final String MEMBER_ID = "Code: ";
    public static final String ROUTE = "Route: ";
    public static final String MCC = "MCC: ";
    public static final String DATE_FROM = "From: ";
    public static final String DATE_TO = "To: ";
    public static final String SHIFT = "Shift: ";
    public static final String CATTLE_TYPE = "Type: ";
    public static final String SAMPLE_NUM = "SID: ";
    public static final String FAT = "Fat: ";
    public static final String SNF = "SNF: ";
    public static final String CLR = "CLR: ";
    public static final String RATE = SmartCCUtil.getRupeeSymbol() + ": ";
    public static final String AMOUNT = SmartCCUtil.getRupeeSign() + ": ";

    public static final String RATE_H = "Rate: ";
    public static final String AMOUNT_H = "Amount: ";

    public static final String AGENT = "Agent: ";

    public static final String DATE = "Date: ";

    public static final String QUANTITY_TIME = "WT: ";
    public static final String QUALITY_TIME = "MT: ";
    public static final String COLL_TIME = "CT: ";
    public static final String QTY_LTRS = "Lt: ";
    public static final String QTY_KG = "Kg: ";
    public static final String MILK_STATUS = "S: ";
    public static final String MCC_NAME = "Name: ";
    public static final String N_O_C = "Cans: ";

    public static final String QTY = "QTY: ";

    public static final String ALLOW_HEADER = "Allow Header";

    public static final String RADIO_BUTTON = "RADIO_BUTTON";
    public static final String IS_AGGERATE_FARMER = "isAggerateFarmer";
    public static final String COLLECTION_TYPE = "collectionType";
    public static final String IS_EDITED = "isEdited";

    public static final String MEMBER_BILL_REG_HEADING = "Member bill register";
    public static final String MEMBER_BILL_SUMMARY_HEADING = "Member bill summary";
    public static final String PERIODIC_HEADER = "Periodic Report";
    public static final String PERIODIC_SHIFT_RECORD_HEAER = "Periodic Shift Summary";
    public static final String DAILY_SHIFT_REPORT_HEADER = "Daily shift report";
    public static final String DAIRY_REPORT_HEADER = "Dairy Report";

    public final static int DAILY_SHIFT = 1;
    public final static int PERIODIC = 2;
    public final static int PERIODIC_SHIFT = 3;
    public final static int MEMBER_BILL_REG = 4;
    public final static int MEMBER_BILL_SUMM = 5;
    public final static int DAIRY_REPORT = 6;
    public final static int PERIODIC_TOTAL = 7;

    public final static String REPORT_ACCEPT_STATUS = "Accept";
    public final static String REPORT_REJECT_STATUS = "Reject";

    public final static String REPORT_ENTRY_AUTO = "Auto";
    public final static String REPORT_ENTRY_MANUAL = "Manual";

    public final static int ADDITIONAL_PADDING = 280;
    public static final String FAT_KG = "KG-Fat: ";
    public static final String SNF_KG = "KG-SNF: ";

    public static final String IS_COMPLETE = "isComplete";

}