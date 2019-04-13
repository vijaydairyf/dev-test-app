package com.devapp.devmain.user;

import android.content.Context;

import com.devApp.R;


/**
 * Created by Upendra on 12/10/2015.
 */
public class ConfigurableValidation {


    Context mContext;

    public ConfigurableValidation(String key, String value, Context ctx) {

        mContext = ctx;
    }


    public void validateKey(String key) {


        if (key.equalsIgnoreCase("eveningSessionStartTime")) {

        } else if (key.equalsIgnoreCase("milkAnalyzerName")) {

        } else if (key.equalsIgnoreCase("printerName")) {

        } else if (key.equalsIgnoreCase("weighingScale")) {

        } else if (key.equalsIgnoreCase("milkAnalyzerBaudrate")) {

        } else if (key.equalsIgnoreCase("printerBaudrate")) {

        } else if (key.equalsIgnoreCase("rduBaudrate")) {

        } else if (key.equalsIgnoreCase("WeighingBaudrate")) {

        } else if (key.equalsIgnoreCase("conversionFactor")) {

        } else if (key.equalsIgnoreCase("smsFooter")) {

        } else if (key.equalsIgnoreCase("enableRejectToDevice")) {

        } else if (key.equalsIgnoreCase("enableManualToDevice")) {

        } else if (key.equalsIgnoreCase("enableBonusToDevice")) {
//boolean
        } else if (key.equalsIgnoreCase("enableMultipleCollection")) {
//boolean
        } else if (key.equalsIgnoreCase("enableConfigurableFarmerIdSize")) {
//boolean
        } else if (key.equalsIgnoreCase("enableConversionFactor")) {
//boolean
        } else if (key.equalsIgnoreCase("displayKgToDevice")) {
            //boolean
        } else if (key.equalsIgnoreCase("acceptMilkInKgFormat")) {
            //boolean
        } else if (key.equalsIgnoreCase("weighingScalePrefix")) {

        } else if (key.equalsIgnoreCase("weighingScaleSeparator")) {

        } else if (key.equalsIgnoreCase("isRateChartInKg")) {

        } else if (key.equalsIgnoreCase("weighingDivisionFactor")) {

        } else {

        }


    }

    public boolean validateBooleanValue(String value) {
        if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
            return true;
        } else {
            return false;
        }
    }

    public boolean validateMilkAnalyser(String value) {
        String strMilkAnalyser[] = mContext.getApplicationContext().getResources().getStringArray(
                R.array.Milk_Analyser);


        for (String str : strMilkAnalyser) {

            if (value.equalsIgnoreCase(str)) {
                return true;
            } else {
                return false;
            }
        }

        return false;
    }

    public boolean validatePrinter(String value) {

        String strPrinter[] = mContext.getApplicationContext().getResources().getStringArray(
                R.array.Printer);
        for (String str : strPrinter) {

            if (value.equalsIgnoreCase(str)) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }


    public boolean validateWeighingScale(String value) {

        String strWeighing[] = mContext.getApplicationContext().getResources().getStringArray(
                R.array.WEIGHING);


        for (String str : strWeighing) {

            if (value.equalsIgnoreCase(str)) {
                return true;
            } else {
                return false;
            }
        }

        return false;
    }


    public boolean validateRDU(String value) {

        String strRdu[] = mContext.getApplicationContext().getResources().getStringArray(
                R.array.RDU);


        for (String str : strRdu) {

            if (value.equalsIgnoreCase(str)) {
                return true;
            } else {
                return false;
            }
        }

        return false;
    }

    public boolean validateBaudrate(String value) {

        String strPrinter[] = mContext.getApplicationContext().getResources().getStringArray(
                R.array.Set_baudrate);


        for (String str : strPrinter) {

            if (value.equalsIgnoreCase(str)) {
                return true;
            } else {
                return false;
            }
        }

        return false;
    }


    public boolean validateTime(String value) {

        if (value != null) {
            String[] timeArray = value.split(":");

            if (timeArray != null && timeArray.length > 0 && timeArray.length < 2) {

                int hour = -1, mins = -1;
                try {
                    hour = Integer.parseInt(timeArray[0]);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                try {
                    mins = Integer.parseInt(timeArray[1]);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                if (hour == -1 || hour < 1 || hour > 23) {
                    return false;
                } else if (mins == -1 || mins < 0 || mins > 59) {
                    return false;
                } else {
                    return true;
                }
            }


        }
        return false;

    }

    public boolean validateLiterOrKg(String value) {
        if (value.equalsIgnoreCase("lt")
                || value.equalsIgnoreCase("kg")) {
            return true;
        } else {
            return false;
        }
    }

    public boolean validateDouble(String value) {
        double dbl = -1;

        try {
            dbl = Double.parseDouble(value);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        if (dbl > -1 && dbl > 0) {
            return true;
        } else {
            return false;
        }

    }


}
