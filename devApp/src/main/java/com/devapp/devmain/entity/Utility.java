package com.devapp.devmain.entity;

/**
 * Created by u_pendra on 7/8/18.
 */

public abstract class Utility {

    final String REGULAR_EXPRESSION = "[^0-9]";
    final int VALID_LENGTH = 10;


    boolean isValidNumber(String number) {
        String currentNumber = number.replaceAll(
                REGULAR_EXPRESSION, "");
        if (currentNumber.length() == VALID_LENGTH)
            return true;
        else
            return false;

    }


    String getOperationalMobileNumber(String number) {
        if (number == null || number.isEmpty()) {
            return number;
        }
        String currentNumber = number.replaceAll(
                REGULAR_EXPRESSION, "");

        return currentNumber;
    }


}
