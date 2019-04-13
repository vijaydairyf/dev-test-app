package com.devapp.devmain.devicemanager;

import android.content.Context;

import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.SessionManager;
import com.devapp.devmain.user.Util;
import com.devapp.devmain.util.ValidationHelper;

/**
 * Created by Upendra on 4/4/2016.
 */
public class WeighingScaleManager {

    AmcuConfig amcuConfig;
    SessionManager session;
    String wsType;
    int wsBaudrate;
    String wsPrefix;

    //    Every weighing record contain prefix and suffix, Separator is how the weighing scale record occuring.
    String wsSuffix;
    String wsSeparator;
    ValidationHelper validationHelper;
    private Context mContext;

    public WeighingScaleManager(Context context) {
        this.mContext = context;
        amcuConfig = AmcuConfig.getInstance();
        session = new SessionManager(mContext);

        wsType = amcuConfig.getWeighingScale();
        wsBaudrate = amcuConfig.getWeighingbaudrate();
        wsPrefix = amcuConfig.getWeighingPrefix();
        wsSuffix = amcuConfig.getWeighingSuffix();
        wsSeparator = amcuConfig.getWeighingSeperator();
        validationHelper = new ValidationHelper();

    }

    //return String array for weighing scale data

    public String[] splitWSDataViaSeparator(String record) {

        String[] records;
        if (wsSeparator.equalsIgnoreCase("CRLF")
                || wsSeparator.equalsIgnoreCase("\r\n")) {
            records = record.split("\r\n");
        } else if (wsSeparator.equalsIgnoreCase("CR")
                || wsSeparator.equalsIgnoreCase("\r")) {
            records = record.split("\r");
        } else if (wsSeparator.equalsIgnoreCase("LF")
                || wsSeparator.equalsIgnoreCase("\n")) {
            records = record.split("\n");
        } else if (wsSeparator.equalsIgnoreCase("TAB")
                || wsSeparator.equalsIgnoreCase("\t")) {
            records = record.split("\t");
        } else if (wsSeparator.equalsIgnoreCase("KEY_SPACE")
                || wsSeparator.equalsIgnoreCase(" ")) {
            records = record.split(" ");
        } else if (wsSeparator.equalsIgnoreCase("KEY_FF")
                || wsSeparator.equalsIgnoreCase("\f")) {
            records = record.split("\f");
        } else if (wsSeparator.equalsIgnoreCase("KEY_BS")
                || wsSeparator.equalsIgnoreCase("\b")) {
            records = record.split("\b");
        } else if (wsSeparator.equalsIgnoreCase("KEY_NUL")) {
            records = record.split("\0");
        } else {
            records = record.split(wsSeparator);
        }
        return records;
    }

    public boolean checkForPrefixAndSuffix(String report, String prefix, String suffix) {

        if (prefix != null && prefix.equalsIgnoreCase("Null")) {
            prefix = "";
        }

        if (suffix != null && suffix.equalsIgnoreCase("Null")) {
            suffix = "";
        }

        if (report.startsWith(prefix) && report.endsWith(suffix)) {
            return true;
        } else {
            return false;
        }
    }

    public String removePrefixAndSuffix(String report, String prifix, String suffix) {

        if (prifix != null && !prifix.equalsIgnoreCase("NULL")) {
            int pL = prifix.length();
            report = report.replace(report.substring(0, pL), "");
        }

        if (suffix != null && !suffix.equalsIgnoreCase("NULL")) {
            int sL = suffix.length();

            report = report.replace(report.substring((report.length() - sL), report.length()), "");
        }
        return report;
    }

    public boolean checkingForWeighingParameter(String record, boolean isKgEnable) {
        if ((record.contains("LT") || record.contains("Lt") ||
                record.contains("lt") || record.contains("=") || record.contains("L") ||
                record.contains("l") || record.contains("\r\n")) &&
                !(record.contains("KG") || record.contains("kg") || record.contains("Kg"))) {
            return true;
        } else if (isKgEnable && (record.contains("\r\n") || record.contains("="))) {
            return true;
        } else
            return false;

    }

    public String getCorrectWeight(String record) {

        String resultRecord = "";
        record = removePrefixAndSuffix(record, amcuConfig.getWeighingPrefix(),
                amcuConfig.getWeighingSuffix());

        int length = record.trim().length();

        if (amcuConfig.getWeightRecordLenght() == 0) {
            resultRecord = Util.getOnlyDecimalFromString(record);
        } else if (length == amcuConfig.getWeightRecordLenght()) {
            if (record.trim().startsWith("+")) {
                record = record.replace("+", "");
            }
            resultRecord = record.trim();
        } else {
            Util.displayErrorToast("Invalid data: " + record + " Length: " + record.length(), mContext);
            resultRecord = "";
        }
        return resultRecord;
    }


}
