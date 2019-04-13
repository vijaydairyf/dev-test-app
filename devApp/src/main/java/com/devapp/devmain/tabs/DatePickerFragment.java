package com.devapp.devmain.tabs;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

import com.devapp.devmain.encryption.EncryptedReportActivity;
import com.devapp.smartcc.report.FilterMccReportActivity;
import com.devapp.smartcc.report.FilterMemberReportActivity;

import java.util.Calendar;

/**
 * Created by Upendra on 1/2/2016.
 */
public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {


    public static int mActivity = 0;
    public static int FILTER_CC_ACTIVITY = 0;
    public static int FILTER_REPORT_ACTIVITY = 1;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        if (mActivity == 0) {
            return new DatePickerDialog(getActivity(),
                    (FilterMccReportActivity) getActivity(), year, month, day);
        } else if (mActivity == 2) {
            return new DatePickerDialog(getActivity(),
                    (EncryptedReportActivity) getActivity(), year, month, day);
        } else {
            return new DatePickerDialog(getActivity(),
                    (FilterMemberReportActivity) getActivity(), year, month, day);
        }
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user
    }
}