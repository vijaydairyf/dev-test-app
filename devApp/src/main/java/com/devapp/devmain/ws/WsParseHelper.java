package com.devapp.devmain.ws;

import com.devapp.devmain.user.Util;

/**
 * Created by x on 13/2/18.
 */

public class WsParseHelper {
    public boolean isValidFormat(String data, String prefix, String suffix) {
        if (prefix != null && prefix.equalsIgnoreCase("Null")) {
            prefix = "";
        }

        if (suffix != null && suffix.equalsIgnoreCase("Null")) {
            suffix = "";
        }

        if (data.startsWith(prefix) && data.endsWith(suffix)) {
            return true;
        } else {
            return false;
        }
    }

    public String[] splitUsingSeperator(String data, String separator) {
        String[] records;
        if (separator.equalsIgnoreCase("CRLF")
                || separator.equalsIgnoreCase("\r\n")) {
            records = data.split("\r\n");
        } else if (separator.equalsIgnoreCase("CR")
                || separator.equalsIgnoreCase("\r")) {
            records = data.split("\r");
        } else if (separator.equalsIgnoreCase("LF")
                || separator.equalsIgnoreCase("\n")) {
            records = data.split("\n");
        } else if (separator.equalsIgnoreCase("TAB")
                || separator.equalsIgnoreCase("\t")) {
            records = data.split("\t");
        } else if (separator.equalsIgnoreCase("KEY_SPACE")
                || separator.equalsIgnoreCase(" ")) {
            records = data.split(" ");
        } else if (separator.equalsIgnoreCase("KEY_FF")
                || separator.equalsIgnoreCase("\f")) {
            records = data.split("\f");
        } else if (separator.equalsIgnoreCase("KEY_BS")
                || separator.equalsIgnoreCase("\b")) {
            records = data.split("\b");
        } else if (separator.equalsIgnoreCase("KEY_NUL")) {
            records = data.split("\0");
        } else {
            records = data.split(separator);
        }
        return records;

    }

    private String removePrefixAndSuffix(String report, String prifix, String suffix) {

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

    public double getWeight(String data, String prefix, String suffix) {
        data = removePrefixAndSuffix(data, prefix,
                suffix);

        int length = data.trim().length();

        if (data.trim().startsWith("+")) {
            data = data.replace("+", "");
        }
        return Double.parseDouble(Util.getOnlyDecimalFromString(data));
    }

}
