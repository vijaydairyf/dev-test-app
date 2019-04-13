package com.devapp.devmain.entity;

import android.content.Context;
import android.widget.EditText;

import com.devapp.devmain.DevAppApplication;
import com.devapp.devmain.agentfarmersplit.AppConstants;
import com.devapp.devmain.multipleequipments.ChooseDecimalFormat;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.SessionManager;
import com.devapp.devmain.user.Util;
import com.devapp.devmain.util.ValidationHelper;

import java.io.Serializable;
import java.text.DecimalFormat;

public class MilkAnalyserEntity implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public double fat;
    public double snf;
    public double clr;
    public double temp;
    public double protein;
    public double addedWater;
    public double lactose;
    public double salt;
    public double freezingPoint;
    public double pH;
    public double conductivity;
    public double density;
    public double solids;

    public String milkAnalyserId;
    public String timeStamp;
    public String errorCode;
    public String name;
    public int baudrate;

    //Added for calibration

    public String date;
    public String time;
    public String calibration;
    public String serialNum;

    public String message;

    public MilkAnalyserEntity() {

    }

    public MilkAnalyserEntity(ReportEntity reportEntity) {
        this.fat = reportEntity.fat;
        this.snf = reportEntity.snf;
        this.clr = reportEntity.clr;
        this.temp = reportEntity.temp;
        this.protein = reportEntity.protein;

        this.addedWater = reportEntity.awm;
        this.lactose = reportEntity.lactose;
        this.salt = reportEntity.salt;
        this.freezingPoint = reportEntity.freezingPoint;
        this.pH = reportEntity.pH;
        this.conductivity = reportEntity.conductivity;
        this.density = reportEntity.density;

        this.name = reportEntity.maName;
    }


    public MilkAnalyserEntity(EditText etFat, EditText etSnf, EditText etClr,
                              EditText etProtein, EditText etLactose, EditText etAWM) {

        ValidationHelper validationHelper = new ValidationHelper();
        ChooseDecimalFormat chooseDecimalFormat = new ChooseDecimalFormat();
        DecimalFormat decimalFormat = chooseDecimalFormat.getFatAndSnfFormat();
        if (etFat != null) {
            decimalFormat = chooseDecimalFormat.getDecimalFormatTypeForDisplay(AppConstants.FAT);
            this.fat = Double.valueOf(decimalFormat.format(
                    validationHelper.getDoubleFromString(etFat.getText().toString().trim(), 0)));
            etFat.setText(String.valueOf(this.fat));
        }

        if (etSnf != null) {
            decimalFormat = chooseDecimalFormat.getDecimalFormatTypeForDisplay(
                    AppConstants.SNF);

            this.snf = Double.valueOf(decimalFormat.format(
                    validationHelper.getDoubleFromString(etSnf.getText().toString().trim(), 0)));
            etSnf.setText(String.valueOf(this.snf));
        }


        if (etClr != null) {
            decimalFormat = chooseDecimalFormat.getDecimalFormatTypeForDisplay(
                    AppConstants.CLR);
            this.clr = Double.valueOf(decimalFormat.format(
                    validationHelper.getDoubleFromString(etClr.getText().toString().trim(), 0)));
            etClr.setText(String.valueOf(this.clr));
        }
        if (etProtein != null && etProtein.getText().toString().trim().length() > 0) {
            decimalFormat = chooseDecimalFormat.getDecimalFormatTypeForDisplay(
                    AppConstants.PROTEIN);
            this.protein = Double.valueOf(decimalFormat.format(
                    validationHelper.getDoubleFromString(etProtein.getText().toString().trim(), 0)));
            etProtein.setText(String.valueOf(this.protein));
        }
        if (etLactose != null) {
            this.lactose = validationHelper.getDoubleFromString(etLactose.getText().toString().trim(), 0);
        }
        if (etAWM != null) {
            this.addedWater = validationHelper.getDoubleFromString(etAWM.getText().toString().trim(), 0);
        }

        if ((etFat.isEnabled() && !etClr.isEnabled()) && this.snf > 0) {
            this.clr = Util.getCLR(this.fat, this.snf);
            etClr.setText(String.valueOf(this.clr));
        }

        if ((etFat.isEnabled() && !etSnf.isEnabled()) && this.clr > 0) {
            this.snf = Util.getSNF(this.fat, this.clr);
            etSnf.setText(String.valueOf(this.snf));
        }


    }

    public boolean isValid() {
        AmcuConfig amcuConfig = AmcuConfig.getInstance();
        SessionManager session = new SessionManager(DevAppApplication.getAmcuContext());
        boolean isValid = isValidFatAndSnf(this.fat, this.snf, DevAppApplication.getAmcuContext());
        if (isValid && amcuConfig.getKeyAllowProteinValue() && !session.getIsChillingCenter()) {
            isValid = isValidProtein(this.protein, DevAppApplication.getAmcuContext());
        }

        return isValid;
    }

    public boolean isValidProtein(double protein, Context context) {
        boolean returnValue = false;

        if (protein > Util.MAX_FAT_LIMIT) {
            Util.displayErrorToast("Protein value should be less than" + Util.MAX_FAT_LIMIT
                    + " , press F10 to refresh the app", context);
        } else if (protein < Util.MIN_FAT_LIMIT) {
            Util.displayErrorToast("Please enter valid protein value", context);
        } else {
            returnValue = true;
        }
        return returnValue;
    }

    public boolean isValidFatAndSnf(double fat, double snf, Context context) {
        boolean returnValue = false;

        if (fat > Util.MAX_FAT_LIMIT) {
            Util.displayErrorToast("Fat value should be less than" + Util.MAX_FAT_LIMIT
                    + " , press F10 to refresh the app", context);
        } else if (fat < Util.MIN_FAT_LIMIT) {
            Util.displayErrorToast("Please enter valid Fat value", context);
        } else if (snf > Util.MAX_SNF_LIMIT) {
            Util.displayErrorToast("SNF value should be less than " + Util.MAX_SNF_LIMIT
                    + " , press F10 to refresh the app", context);
        } else if (snf < Util.MIN_SNF_LIMIT) {
            Util.displayErrorToast("Please enter valid SNF value", context);
        } else if (this.clr <= 0 || this.clr > 80) {
            Util.displayErrorToast("Please enter valid CLR value", context);

        } else {
            returnValue = true;
        }

        return returnValue;
    }

}
