package com.devapp.kmfcommon;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.devapp.devmain.helper.ShiftSummaryActivity;
import com.devapp.devmain.macollection.MultipleMilkAnalyser;
import com.devapp.devmain.macollection.ParallelActivity;
import com.devapp.devmain.main.FarmerScannerActivity;
import com.devapp.devmain.main.LoginActivity;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.SessionManager;
import com.devapp.devmain.user.Util;
import com.devapp.smartcc.entityandconstants.SmartCCUtil;
import com.devApp.R;

public class UserSelectionActivity extends Activity {

    CardView row1, row2, row3;
    TextView weight_collection, milk_collection, both_colection, tvheader;
    String CID;
    AmcuConfig amcuConfig;
    private String choice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_selection);
        CID = new SessionManager(UserSelectionActivity.this).getSocietyName();
        tvheader = (TextView) findViewById(R.id.tvheader);
        row1 = (CardView) findViewById(R.id.row1);
        row2 = (CardView) findViewById(R.id.row2);
        row3 = (CardView) findViewById(R.id.row3);
        tvheader.setText(CID);
        weight_collection = (TextView) findViewById(R.id.weight_collection);
        milk_collection = (TextView) findViewById(R.id.milk_collection);
        both_colection = (TextView) findViewById(R.id.both_colection);

        amcuConfig = AmcuConfig.getInstance();

//        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade);
//        weight_collection.startAnimation(animation);
//        milk_collection.startAnimation(animation);
//        both_colection.startAnimation(animation);

        row1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    weight_collection.setTextColor(getResources().getColor(R.color.btnCattlePressed));
                    milk_collection.setTextColor(getResources().getColor(R.color.white));
                    both_colection.setTextColor(getResources().getColor(R.color.white));
                }
            }
        });
        row2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    weight_collection.setTextColor(getResources().getColor(R.color.white));
                    milk_collection.setTextColor(getResources().getColor(R.color.btnCattlePressed));
                    both_colection.setTextColor(getResources().getColor(R.color.white));
                }
            }
        });
        row3.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    weight_collection.setTextColor(getResources().getColor(R.color.white));
                    milk_collection.setTextColor(getResources().getColor(R.color.white));
                    both_colection.setTextColor(getResources().getColor(R.color.btnCattlePressed));
                }
            }
        });
    }

    public void gotoBothActivity(View view) {

        if (!amcuConfig.getSmartCCFeature()) {
            Intent intent = new Intent(getApplicationContext(), FarmerScannerActivity.class);
            intent.putExtra("USER_SELECTION", "BOTH");
            startActivity(intent);
        } else if (!checkValidSession()) {
            return;
        } else if (amcuConfig.getSmartCCFeature()) {
            Intent intent = new Intent(getApplicationContext(), ParallelActivity.class);
            intent.putExtra("USER_SELECTION", "BOTH");
            startActivity(intent);
        } else {

        }

        finish();
    }

    public void gotoWeightActivity(View view) {
        Intent intent = new Intent(getApplicationContext(), FarmerScannerActivity.class);
        intent.putExtra("USER_SELECTION", "WS");
        startActivity(intent);
        finish();

    }

    public void gotoMilkActivity(View view) {

        if (amcuConfig.getCheckboxMultipleMA() && amcuConfig.getMultipleMA()) {
            if (!checkValidSession()) {
                return;
            } else {
                startActivity(new Intent(UserSelectionActivity.this, MultipleMilkAnalyser.class));
                finish();
            }
        } else {
            openChoiceAlertForSIDorCID();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(UserSelectionActivity.this, LoginActivity.class));
        finish();
    }

    public void openChoiceAlertForSIDorCID() {
        AlertDialog.Builder builder = new AlertDialog.Builder(UserSelectionActivity.this);
        LayoutInflater inflater = UserSelectionActivity.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.milkanalysis_dialogbox, null);
        RadioGroup radioSIDCID = (RadioGroup) dialogView.findViewById(R.id.radio_choice);
        choice = "SID";
        radioSIDCID.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio_sid:
                        choice = "SID";
                        break;
                    case R.id.radio_cid:
                        choice = "CID";
                        break;
                }
            }
        });


        builder.setView(dialogView);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (choice.equalsIgnoreCase("SID")) {
                    //  Toast.makeText(UserSelectionActivity.this, "SID", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {
                    //  Toast.makeText(UserSelectionActivity.this, "CID", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
                Intent intent = new Intent(getApplicationContext(), FarmerScannerActivity.class);
                intent.putExtra("USER_SELECTION", "MA");
                intent.putExtra("USER_ID", choice);
                startActivity(intent);
                finish();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_F1:
                startActivity(new Intent(UserSelectionActivity.this, ShiftSummaryActivity.class));
                return true;
            default:
                break;
        }
        return false;
    }

    public boolean checkValidSession() {

        boolean returnValue = true;
        if (amcuConfig.getKeyEnableCollectionConstraints() && !new SmartCCUtil(UserSelectionActivity.this)
                .checkForValidCollection(Util.getCurrentShift())) {
            Util.displayErrorToast("Please check the collection time", UserSelectionActivity.this);
            returnValue = false;
        }
        return returnValue;
    }

}
