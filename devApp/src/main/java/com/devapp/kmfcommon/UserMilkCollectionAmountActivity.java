package com.devapp.kmfcommon;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.devApp.R;


public class UserMilkCollectionAmountActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_milk_collection_amount);
        int totalMilk = getIntent().getIntExtra("TOTALMILK", 0);
        Toast.makeText(getApplicationContext(), "Total sum Mik::" + totalMilk, Toast.LENGTH_LONG).show();
    }


}
