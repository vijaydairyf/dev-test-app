package com.devapp.devmain.multipleequipments;

import com.devapp.devmain.agentfarmersplit.AppConstants;
import com.devapp.devmain.server.AmcuConfig;

import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * Created by Upendra on 6/6/2016.
 */
public class ChooseDecimalFormat {

    DecimalFormat decimalFormat;
    AmcuConfig amcuConfig;

    public ChooseDecimalFormat() {
        decimalFormat = new DecimalFormat("#0.00");
        amcuConfig = AmcuConfig.getInstance();
    }


    public DecimalFormat getRateDecimalFormat() {

        if (amcuConfig.getDecimalRoundOffRate() == 0) {
            decimalFormat = new DecimalFormat("#0");

        } else if (amcuConfig.getDecimalRoundOffRate() == 1) {
            decimalFormat = new DecimalFormat("#0.0");

        } else if (amcuConfig.getDecimalRoundOffRate() == 2) {
            decimalFormat = new DecimalFormat("#0.00");

        } else if (amcuConfig.getDecimalRoundOffRate() == 3) {
            decimalFormat = new DecimalFormat("#0.000");
        }


        if (amcuConfig.getDecimalRoundOffRateCheck()) {
            decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
        } else {
            decimalFormat.setRoundingMode(RoundingMode.FLOOR);
        }

        return decimalFormat;
    }

    public DecimalFormat getAmountDecimalFormat() {


        if (amcuConfig.getDecimalRoundOffAmount() == 0) {
            decimalFormat = new DecimalFormat("#0");

        } else if (amcuConfig.getDecimalRoundOffAmount() == 1) {
            decimalFormat = new DecimalFormat("#0.0");

        } else if (amcuConfig.getDecimalRoundOffAmount() == 2) {
            decimalFormat = new DecimalFormat("#0.00");

        } else if (amcuConfig.getDecimalRoundOffAmount() == 3) {
            decimalFormat = new DecimalFormat("#0.000");
        }


        if (amcuConfig.getDecimalRoundOffAmountCheck()) {
            decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
        } else {
            decimalFormat.setRoundingMode(RoundingMode.FLOOR);
        }


        return decimalFormat;
    }

    public DecimalFormat getWeightDecimalFormat() {

        if (amcuConfig.getDecimalRoundOffWeigh() == 0) {
            decimalFormat = new DecimalFormat("#0");

        } else if (amcuConfig.getDecimalRoundOffWeigh() == 1) {
            decimalFormat = new DecimalFormat("#0.0");

        } else if (amcuConfig.getDecimalRoundOffWeigh() == 2) {
            decimalFormat = new DecimalFormat("#0.00");

        } else if (amcuConfig.getDecimalRoundOffWeigh() == 3) {
            decimalFormat = new DecimalFormat("#0.000");
        }


        if (amcuConfig.getDecimalRoundOffWeightCheck()) {
            decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
        } else {
            decimalFormat.setRoundingMode(RoundingMode.FLOOR);
        }

        return decimalFormat;
    }

    public DecimalFormat get2DigitFormatWeight() {
        decimalFormat = new DecimalFormat("#0.00");
        if (amcuConfig.getDecimalRoundOffWeightCheck()) {
            decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
        } else {
            decimalFormat.setRoundingMode(RoundingMode.FLOOR);
        }


        return decimalFormat;
    }


    public DecimalFormat getFatAndSnfFormat() {
        if (amcuConfig.getKeyAllowTwoDeciaml()) {
            decimalFormat = new DecimalFormat("#0.00");

        } else {
            decimalFormat = new DecimalFormat("#0.0");

        }
        if (amcuConfig.getRoundOffCheckFatAndSnf()) {
            decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
        } else {
            decimalFormat.setRoundingMode(RoundingMode.FLOOR);
        }
        return decimalFormat;
    }


    public DecimalFormat getCLRFormat() {
        if (amcuConfig.getClrRoundOffUpto() == 0) {
            decimalFormat = new DecimalFormat("#0");
        } else {
            decimalFormat = new DecimalFormat("#0.0");
        }

        return decimalFormat;
    }

    /**
     * @param qualityType
     * @return
     */


    public DecimalFormat getDecimalFormatTypeForDisplay(String qualityType) {
        DecimalFormat decimalFormat = null;

        if (qualityType.equalsIgnoreCase(AppConstants.FAT)) {

            decimalFormat = getDecimalFormatFromType(decimalFormat, amcuConfig.getDisplayFATConfiguration());
            decimalFormat = getDecimalFormatFromMode(decimalFormat, amcuConfig.getRoundFATConfiguration());
        } else if (qualityType.equalsIgnoreCase(AppConstants.SNF)) {

            decimalFormat = getDecimalFormatFromType(decimalFormat, amcuConfig.getDisplaySNFConfiguration());
            decimalFormat = getDecimalFormatFromMode(decimalFormat, amcuConfig.getRoundSNFConfiguration());
        } else if (qualityType.equalsIgnoreCase(AppConstants.CLR)) {

            decimalFormat = getDecimalFormatFromType(decimalFormat, amcuConfig.getDisplayCLRConfiguration());
            decimalFormat = getDecimalFormatFromMode(decimalFormat, amcuConfig.getRoundCLRConfiguration());
        } else if (qualityType.equalsIgnoreCase(AppConstants.PROTEIN)) {

            decimalFormat = getDecimalFormatFromType(decimalFormat, amcuConfig.getDisplayProteinConfiguration());
            decimalFormat = getDecimalFormatFromMode(decimalFormat, amcuConfig.getRoundProteinConfiguration());
        }
        return decimalFormat;
    }


    /**
     * Return the decimal format based on provided quality type and Configuration
     *
     * @param qualityType
     * @return
     */

    public DecimalFormat getDecimalFormatTypeForRateChart(String qualityType) {
        DecimalFormat decimalFormat = null;

        if (qualityType.equalsIgnoreCase(AppConstants.FAT)) {

            decimalFormat = getDecimalFormatFromType(decimalFormat, amcuConfig.getRateFATConfiguration());
            decimalFormat = getDecimalFormatFromMode(decimalFormat, amcuConfig.getRoundFATConfiguration());
        } else if (qualityType.equalsIgnoreCase(AppConstants.SNF)) {

            decimalFormat = getDecimalFormatFromType(decimalFormat, amcuConfig.getRateSNFConfiguration());
            decimalFormat = getDecimalFormatFromMode(decimalFormat, amcuConfig.getRoundSNFConfiguration());
        } else if (qualityType.equalsIgnoreCase(AppConstants.CLR)) {

            decimalFormat = getDecimalFormatFromType(decimalFormat, amcuConfig.getRateCLRConfiguration());
            decimalFormat = getDecimalFormatFromMode(decimalFormat, amcuConfig.getRoundCLRConfiguration());
        } else if (qualityType.equalsIgnoreCase(AppConstants.PROTEIN)) {

            decimalFormat = getDecimalFormatFromType(decimalFormat, amcuConfig.getRateProteinConfiguration());
            decimalFormat = getDecimalFormatFromMode(decimalFormat, amcuConfig.getRoundProteinConfiguration());
        }
        return decimalFormat;

    }


    private DecimalFormat getDecimalFormatFromMode(DecimalFormat decimalFormat, String roundingMode) {
        if (roundingMode.equalsIgnoreCase(AppConstants.ROUND_UP)) {
            decimalFormat.setRoundingMode(RoundingMode.UP);
        } else if (roundingMode.equalsIgnoreCase(AppConstants.ROUND_DOWN)) {
            decimalFormat.setRoundingMode(RoundingMode.DOWN);
        } else if (roundingMode.equalsIgnoreCase(AppConstants.ROUND_HALF_UP)) {
            decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
        } else if (roundingMode.equalsIgnoreCase(AppConstants.ROUND_HALF_DOWN)) {
            decimalFormat.setRoundingMode(RoundingMode.HALF_DOWN);
        } else if (roundingMode.equalsIgnoreCase(AppConstants.ROUND_HALF_EVEN)) {
            decimalFormat.setRoundingMode(RoundingMode.HALF_EVEN);
        } else if (roundingMode.equalsIgnoreCase(AppConstants.ROUND_CEILING)) {
            decimalFormat.setRoundingMode(RoundingMode.CEILING);
        } else if (roundingMode.equalsIgnoreCase(AppConstants.ROUND_FLOOR)) {
            decimalFormat.setRoundingMode(RoundingMode.FLOOR);
        } else {
            decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
        }

        return decimalFormat;

    }

    private DecimalFormat getDecimalFormatFromType(DecimalFormat decimalFormat, int num) {
        if (num == 0) {
            decimalFormat = new DecimalFormat("#0");
        } else if (num == 1) {
            decimalFormat = new DecimalFormat("#0.0");
        } else if (num == 2) {
            decimalFormat = new DecimalFormat("#0.00");
        } else if (num == 3) {
            decimalFormat = new DecimalFormat("#0.000");
        } else {
            decimalFormat = new DecimalFormat("#0.0000");
        }

        return decimalFormat;
    }

    public DecimalFormat getWeightReadDecimalFormat() {
        String suffixFormat = "", prefix = "#0";
        for (int i = 1; i <= amcuConfig.getWeightReadRoundOff(); i++) {
            if (i == 1) {
                suffixFormat = suffixFormat + ".0";
            } else {
                suffixFormat = suffixFormat + "0";
            }
        }
        decimalFormat = new DecimalFormat(prefix + suffixFormat);
        decimalFormat = getDecimalFormatFromMode(decimalFormat, amcuConfig.getWeightReadRoundingMode());
        return decimalFormat;
    }


}
