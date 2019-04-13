package com.devapp.devmain.user;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

/**
 * Created by Upendra on 5/23/2016.
 */
public class ManualRateChart extends Activity {

    Spinner spMilkType;
    EditText etStartFat, etEndFat, etStartSnf, etEndSnf, etFatKgRate, etSnfKgRate, etStartDate, etEndDate;

    Button btnGenerateRateChart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
