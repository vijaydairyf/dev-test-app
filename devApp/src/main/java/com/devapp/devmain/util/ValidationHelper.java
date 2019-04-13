package com.devapp.devmain.util;


import android.content.Context;
import android.widget.EditText;

import com.devapp.devmain.DevAppApplication;
import com.devapp.devmain.entity.MilkAnalyserEntity;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.SessionManager;
import com.devapp.devmain.user.Util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationHelper {

    public static String getValidText(String input, String defaultValue)

    {
        if (input == null) {
            return defaultValue;
        } else {
            return input.trim();
        }
    }

    public static boolean isValidFarmerId(String farmerId, int length) {
        int id = -1;
        try {
            id = Integer.parseInt(farmerId);
        } catch (NullPointerException e1) {

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        String dummyId = "0";
        for (int i = 0; i < length; i++) {
            dummyId = dummyId + 9;
        }

        if (id > 0 && id <= Integer.parseInt(dummyId)) {
            return true;
        } else {
            return false;
        }

    }

    public static String paddingOnlyFarmerId(int farmId, int length) {

        String returnFarmId = farmId + "";
        for (int i = returnFarmId.length(); i < length; i++) {
            returnFarmId = "0" + returnFarmId;
        }
        return returnFarmId;


    }

    public static boolean isReserveCode(String farmerId, Context context) {

        int id = Integer.parseInt(farmerId);
        if ((id == 9999) || (id == 9998) ||
                (id == 9997) || (id == 991) || (id == 999)) {

            Util.displayErrorToast(id + " code is reserved, Please enter other code.", context);
            return false;
        } else {
            return true;
        }

    }

    public boolean validateNonZeroPositiveInteger(String value) {
        int returnValue = -1;

        try {
            returnValue = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        if (returnValue == -1)
            return false;
        else
            return true;
    }

    public int getIntegerFromString(String value) {
        int returnValue = -1;

        try {
            returnValue = Integer.parseInt(value.trim());
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return returnValue;
    }

    public int getIntegerFromString(String value, int defaultValue) {
        int returnValue = defaultValue;
        try {
            returnValue = Integer.parseInt(value.trim());
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return returnValue;
    }

    public double getDoubleFromString(String value, double defaultValue) {
        double retValue = defaultValue;
        try {
            retValue = Double.parseDouble(value);
        } catch (NullPointerException e) {

        } catch (NumberFormatException e) {

        }
        return retValue;
    }

    public String getValidString(String returnValue, DecimalFormat decimalFormat) {


        try {
            returnValue = decimalFormat.format(Double.parseDouble(returnValue));
        } catch (NullPointerException e) {

        } catch (NumberFormatException e) {

        }
        return returnValue;
    }

    public String getDoubleFromString(DecimalFormat decimalFormat, String value, double defalutTime) {
        double retValue = defalutTime;
        try {
            retValue = Double.parseDouble(value);
        } catch (NullPointerException e) {

        } catch (NumberFormatException e) {

        }
        return decimalFormat.format(retValue);
    }

    public String getStringFromDouble(DecimalFormat decimalFormat, double value) {

        double retValue = value;

        return decimalFormat.format(retValue);
    }

    public String getDoubleFromString(DecimalFormat decimalFormat, String value) {
        double retValue = 0.00;
        try {
            retValue = Double.parseDouble(value);
        } catch (NullPointerException e) {

        } catch (NumberFormatException e) {

        }
        return decimalFormat.format(retValue);
    }

    public long getLongFromString(String value) {
        long returnValue = -1;

        try {
            returnValue = Long.valueOf(value.trim());
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return returnValue;
    }

    public boolean validateMobileNumber(String str) {

        if (str.trim().length() == 10) {
            if (getLongFromString(str.trim()) < 6000000000l) {
                return false;
            } else {
                return true;
            }

        } else {
            return false;
        }
    }

    public boolean validateEmailId(String str) {

        if (str.contains(" ")) {
            return false;

        } else if (!str.contains("@")) {
            return false;
        } else if (!str.contains(".")) {
            return false;
        } else if (str.length() < 4) {
            return false;
        } else {
            return true;
        }
    }

    public boolean validateMaxWeight(String strWeight, Context ctx) {
        DecimalFormat decimalFormatConversion = new DecimalFormat("#0.0000");
        String value = Util.convertStringtoDecimal(strWeight.trim(), decimalFormatConversion, "0");
        double data = Double.valueOf(value);

        if (data < Util.MIN_WEIGHT_LIMIT || data > Util.MAX_WEIGHT_LIMIT) {
            Util.displayErrorToast("Limit should be greater than " + Util.MIN_WEIGHT_LIMIT +
                    " and less than " + Util.MAX_WEIGHT_LIMIT, ctx);
            return false;
        } else {
            return true;
        }

    }

    public String getIpAddress(Context ctx) {
        AmcuConfig amcuConfig = AmcuConfig.getInstance();

        String server = amcuConfig.getServer();
        String ipAddress = null;
        try {
            InetAddress ia = InetAddress.getByName(server);
            ipAddress = ia.getHostAddress();

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        if (ipAddress != null) {
            amcuConfig.setIpForServer(ipAddress);
        }
        return ipAddress;
    }

    public String getIpAddressFromServer(String dns) {
        String ipAddress = null;
        try {
            InetAddress ia = InetAddress.getByName(dns);
            ipAddress = ia.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return ipAddress;

    }

    public boolean validMilkWeight(double dQuantity, Context context) {
        double maxLimit = AmcuConfig.getInstance().getMaxlimitOfWeight();
        double minLimit = AmcuConfig.getInstance().getKeyMinValidWeight();

        if (((dQuantity > AmcuConfig.getInstance().getMaxlimitOfWeight()) &&
                (Util.checkIfSampleCode(new SessionManager(context).getFarmerID(),
                        AmcuConfig.getInstance().getFarmerIdDigit())))) {
            Util.displayErrorToast("Weight should be less than " + maxLimit
                    , context);

            return false;
        }

        if (dQuantity < minLimit) {
//             Util.displayErrorToast("Weight should be greater than "+minLimit
//                     ,context);
            return false;
        }

        if (dQuantity >= minLimit &&
                dQuantity <= maxLimit) {
            return true;
        } else if (dQuantity >= maxLimit) {
            Util.displayErrorToast("Weight should be less than " + maxLimit + " " +
                            "Please check the WS!"
                    , context);
            return false;
        } else {
            return false;
        }
    }

    public boolean isValidFatAndSnf(double fat, double snf, Context context) {
        boolean returnValue = false;

        if (fat > Util.MAX_FAT_LIMIT) {
            Util.displayErrorToast("Fat value should be less than" + Util.MAX_FAT_LIMIT
                    + " , press F10 to refresh the app", context);
        } else if (fat < Util.MIN_FAT_LIMIT) {
//            Util.displayErrorToast("Fat value should be greater than "+Util.MIN_FAT_LIMIT
//                    +" , press F10 to refresh the app",context);
        } else if (snf > Util.MAX_SNF_LIMIT) {
            Util.displayErrorToast("SNF value should be less than " + Util.MAX_SNF_LIMIT
                    + " , press F10 to refresh the app", context);
        } else if (snf < Util.MIN_SNF_LIMIT) {
//            Util.displayErrorToast("SNF value should be greater than "+Util.MIN_SNF_LIMIT
//                    +" , press F10 to refresh the app",context);
        } else {
            returnValue = true;
        }

        return returnValue;
    }

    public boolean isValidQuality(MilkAnalyserEntity maEntity, Context context) {
        AmcuConfig amcuConfig = AmcuConfig.getInstance();
        SessionManager session = new SessionManager(context);
        boolean isValid = isValidFatAndSnf(maEntity.fat, maEntity.snf, context);
        if (isValid && amcuConfig.getKeyAllowProteinValue() && !session.getIsChillingCenter()) {
            isValid = isValidProtein(maEntity.protein, context);
        }
        return isValid;
    }

    public boolean isValidQualityForSample(MilkAnalyserEntity maEntity, Context context) {
        boolean isValid = isValidFatAndSnf(maEntity.fat, maEntity.snf, context);
        return isValid;
    }

    public int getEditTextLength(EditText etText) {
        if (etText == null) {
            return -1;
        } else {
            return etText.getText().toString().trim().length();
        }

    }

    public boolean isDatabaseExist(String dbName) {

        return true;
    }

    public String getValidateDouble(DecimalFormat decimalFormat, String str, String defalutValue) {
        String returnValue = defalutValue;
        if (decimalFormat == null) {
            decimalFormat = new DecimalFormat("#0.00");
        }

        try {
            double value = Double.parseDouble(str);
            returnValue = decimalFormat.format(value);
        } catch (NullPointerException e1) {

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return returnValue;
    }

    public DecimalFormat getFormatForFarmerId(int length) {

        String format = "";

        for (int i = 0; i < length; i++) {
            format = format + "0";
        }

        return new DecimalFormat(format);

    }

    public String getAppendedFarmerId(int farmerId, DecimalFormat format) {

        return format.format(farmerId);
    }

    /**
     * Name should contain only the Alphabet, and space between the first and
     * Last name
     *
     * @param input
     * @return
     */

    public String getTextForName(String input) {

        if (input == null)
            return input;

        input = input.trim();

        Pattern pattern = Pattern.compile("[a-zA-Z_ ]");
        Matcher matcher = pattern.matcher(input);
        while (matcher.find()) {
            String s = matcher.group();
            input = input.replaceAll("\\" + s, "");
        }
        System.out.println(input);
        return input;
    }

    /**
     * It support @,.,_
     *
     * @param input
     * @return
     */

    public String getTextForEmail(String input) {

        if (input == null)
            return input;

        input = input.trim();
        Pattern pattern = Pattern.compile("[a-zA-Z0-9@_.]");
        Matcher matcher = pattern.matcher(input);
        while (matcher.find()) {
            String s = matcher.group();
            input = input.replaceAll("\\" + s, "");
        }
        return input;
    }

    /**
     * Id can contains _,and alphanumeric value
     *
     * @param input
     * @return
     */
    public String getTextForID(String input) {

        if (input == null)
            return input;

        input = input.trim();
        Pattern pattern = Pattern.compile("[a-zA-Z0-9_]");
        Matcher matcher = pattern.matcher(input);
        while (matcher.find()) {
            String s = matcher.group();
            input = input.replaceAll("\\" + s, "");
        }
        return input;
    }

    public boolean isValidProtein(double protein, Context context) {
        boolean returnValue = false;

        if (protein > Util.MAX_FAT_LIMIT) {
            Util.displayErrorToast("Protein value should be less than" + Util.MAX_FAT_LIMIT
                    + " , press F10 to refresh the app", context);
        } else if (protein < Util.MIN_FAT_LIMIT) {

        } else {
            returnValue = true;
        }
        return returnValue;
    }


    public double getDoubleFromStringValue(String value, double defaultValue) {
        double retValue = 0.0;
        try {
            retValue = Double.parseDouble(value);
        } catch (NullPointerException e) {
            retValue = defaultValue;
        } catch (NumberFormatException e) {
            retValue = defaultValue;

        }
        return retValue;
    }

    public boolean isValidName(String name) {

        boolean returnValue = true;

        if (name == null || name.trim().isEmpty()) {
            Util.displayErrorToast("Please enter valid name!",
                    DevAppApplication.getAmcuContext());
            returnValue = false;
        } else if (name.trim().length() > 50) {
            Util.displayErrorToast("Name length should be less than 50 character!",
                    DevAppApplication.getAmcuContext());
            returnValue = false;
        } else {
            Pattern p = Pattern.compile("[^a-z ]", Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(name);
            if (m.find()) {
                Util.displayErrorToast("Name should not contain any special character!",
                        DevAppApplication.getAmcuContext());
                returnValue = false;
            }
        }
        return returnValue;

    }


}