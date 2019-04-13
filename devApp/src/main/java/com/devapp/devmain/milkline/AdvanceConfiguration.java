package com.devapp.devmain.milkline;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.devapp.devmain.devicemanager.ConfigurationManager;
import com.devapp.devmain.entity.ConfigurationEntity;
import com.devapp.devmain.httptasks.ConfigurationPush;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.SessionManager;
import com.devapp.devmain.user.Util;
import com.devapp.devmain.util.ValidationHelper;
import com.devApp.R;

import java.util.Calendar;

/**
 * Created by u_pendra on 15/12/16.
 */

public class AdvanceConfiguration extends AppCompatActivity implements View.OnClickListener {

    SessionManager sessionManager;
    AmcuConfig amcuConfig;
    int realMrnStartTime;
    int realEvnStartTime;
    ConfigurationManager configurationManager;
    ConfigurationEntity configurationEntity;
    int mHour, mMins;
    EditText etTime;
    private com.eevoskos.robotoviews.widget.RobotoTextView tvheader;
    private android.support.v7.widget.CardView cardTime, cardZeroFS;
    private TextView tvMrnShiftHint, tvHintZeroSetting;
    private RelativeLayout header;
    private LinearLayout lnButton, lnRejectData;
    private EditText etMrnStartTime, etEndMrnTime, etStartEvnShift, etEndEvnShift, etMinFat, etMinSnf, etMaxFat, etMaxSnf;
    private CheckedTextView tvZeroFatAndSnf, tvEnableShiftConstraints, chTextRejectMilk,
            chCowRejectMilk, chBuffaloRejectMilk, chMixRejectMilk;
    private Button btnSave, btnCancel;
    private String selectedMilk = "COW";
    private TimePickerDialog timePickerDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.additional_configuration);
        sessionManager = new SessionManager(this);
        amcuConfig = AmcuConfig.getInstance();
        initializeView();

        initializeEntities();
        onClickEvents();
        setTimeField();
        setRealStartEndTime();

        if (Util.isOperator(AdvanceConfiguration.this))
            setForOperator();

    }

    @Override
    protected void onStart() {
        super.onStart();

        configurationManager = new ConfigurationManager(AdvanceConfiguration.this);
        configurationEntity = configurationManager.addConfiguration();

    }

    public void initializeView() {
        tvheader = (com.eevoskos.robotoviews.widget.RobotoTextView) findViewById(R.id.tvheader);
        cardTime = (android.support.v7.widget.CardView) findViewById(R.id.cardTime);
        cardZeroFS = (android.support.v7.widget.CardView) findViewById(R.id.cardZeroFS);
        tvMrnShiftHint = (TextView) findViewById(R.id.tvMrnShiftHint);
        tvHintZeroSetting = (TextView) findViewById(R.id.tvHintZeroSetting);
        header = (RelativeLayout) findViewById(R.id.header);
        lnButton = (LinearLayout) findViewById(R.id.lnButton);
        etMrnStartTime = (EditText) findViewById(R.id.etMrnStartTime);
        etEndMrnTime = (EditText) findViewById(R.id.etEndMrnTime);
        etStartEvnShift = (EditText) findViewById(R.id.etStartEvnShift);
        etEndEvnShift = (EditText) findViewById(R.id.etEndEvnShift);
        tvZeroFatAndSnf = (CheckedTextView) findViewById(R.id.tvZeroFatAndSnf);
        tvEnableShiftConstraints = (CheckedTextView) findViewById(R.id.tvEnableShiftConstraints);
        chTextRejectMilk = (CheckedTextView) findViewById(R.id.chTextRejectMilk);
        chCowRejectMilk = (CheckedTextView) findViewById(R.id.chRejectCowMilk);
        chBuffaloRejectMilk = (CheckedTextView) findViewById(R.id.chRejectBuffaloMilk);
        chMixRejectMilk = (CheckedTextView) findViewById(R.id.chRejectMixMilk);


        lnRejectData = (LinearLayout) findViewById(R.id.lnTextReject);

        etMaxFat = (EditText) findViewById(R.id.etMaxFat);
        etMaxSnf = (EditText) findViewById(R.id.etMaxSnf);
        etMinFat = (EditText) findViewById(R.id.etMinFat);
        etMinSnf = (EditText) findViewById(R.id.etMinSnf);


        btnSave = (Button) findViewById(R.id.btnSave);
        btnCancel = (Button) findViewById(R.id.btnCancel);

    }

    public void onClickEvents() {
        etMrnStartTime.setOnClickListener(this);
        etEndMrnTime.setOnClickListener(this);
        etStartEvnShift.setOnClickListener(this);
        etEndEvnShift.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        tvZeroFatAndSnf.setOnClickListener(this);
        tvEnableShiftConstraints.setOnClickListener(this);
        chTextRejectMilk.setOnClickListener(this);

        chMixRejectMilk.setOnClickListener(this);
        chCowRejectMilk.setOnClickListener(this);
        chBuffaloRejectMilk.setOnClickListener(this);

        if (tvEnableShiftConstraints.isChecked()) {
            tvEnableShiftConstraints.setCheckMarkDrawable(R.drawable.check_box);
        } else {
            tvEnableShiftConstraints.setCheckMarkDrawable(R.drawable.uncheck_box);
        }

        if (chTextRejectMilk.isChecked()) {
            chTextRejectMilk.setCheckMarkDrawable(R.drawable.check_box);
            lnRejectData.setVisibility(View.GONE);
        } else {
            chTextRejectMilk.setCheckMarkDrawable(R.drawable.uncheck_box);
            lnRejectData.setVisibility(View.VISIBLE);
        }

        if (tvZeroFatAndSnf.isChecked()) {
            tvZeroFatAndSnf.setCheckMarkDrawable(R.drawable.check_box);
        } else {
            tvZeroFatAndSnf.setCheckMarkDrawable(R.drawable.uncheck_box);
        }

    }

    @Override
    public void onClick(View v) {

        if (v == etMrnStartTime) {
            etTime = etMrnStartTime;
            timePickerDialog.show();
        } else if (v == etEndMrnTime) {
            etTime = etEndMrnTime;
            timePickerDialog.show();
        } else if (v == etStartEvnShift) {
            etTime = etStartEvnShift;
            timePickerDialog.show();
        } else if (v == etEndEvnShift) {
            etTime = etEndEvnShift;
            timePickerDialog.show();
        } else if (v == tvZeroFatAndSnf) {
            if (tvZeroFatAndSnf.isChecked()) {
                tvZeroFatAndSnf.setChecked(false);
                tvZeroFatAndSnf.setCheckMarkDrawable(R.drawable.uncheck_box);
            } else {
                tvZeroFatAndSnf.setChecked(true);
                tvZeroFatAndSnf.setCheckMarkDrawable(R.drawable.check_box);
            }

        } else if (v == tvEnableShiftConstraints) {
            if (tvEnableShiftConstraints.isChecked()) {
                tvEnableShiftConstraints.setChecked(false);
                tvEnableShiftConstraints.setCheckMarkDrawable(R.drawable.uncheck_box);
            } else {
                tvEnableShiftConstraints.setChecked(true);
                tvEnableShiftConstraints.setCheckMarkDrawable(R.drawable.check_box);
            }

        } else if (v == chTextRejectMilk) {
            if (chTextRejectMilk.isChecked()) {
                lnRejectData.setVisibility(View.VISIBLE);
                chTextRejectMilk.setChecked(false);
                chTextRejectMilk.setCheckMarkDrawable(R.drawable.uncheck_box);
            } else {
                lnRejectData.setVisibility(View.GONE);
                chTextRejectMilk.setChecked(true);
                chTextRejectMilk.setCheckMarkDrawable(R.drawable.check_box);
            }

        } else if (v == chCowRejectMilk) {
            if (chCowRejectMilk.isChecked()) {

                chCowRejectMilk.setChecked(false);
                chCowRejectMilk.setCheckMarkDrawable(R.drawable.uncheck_box);
            } else {

                chCowRejectMilk.setChecked(true);
                chCowRejectMilk.setCheckMarkDrawable(R.drawable.check_box);

                chBuffaloRejectMilk.setChecked(false);
                chBuffaloRejectMilk.setCheckMarkDrawable(R.drawable.uncheck_box);
                chMixRejectMilk.setChecked(false);
                chMixRejectMilk.setCheckMarkDrawable(R.drawable.uncheck_box);
            }
            setRejectFatAndSnf();

        } else if (v == chBuffaloRejectMilk) {
            if (chBuffaloRejectMilk.isChecked()) {

                chBuffaloRejectMilk.setChecked(false);
                chBuffaloRejectMilk.setCheckMarkDrawable(R.drawable.uncheck_box);
            } else {

                chBuffaloRejectMilk.setChecked(true);
                chBuffaloRejectMilk.setCheckMarkDrawable(R.drawable.check_box);

                chCowRejectMilk.setChecked(false);
                chCowRejectMilk.setCheckMarkDrawable(R.drawable.uncheck_box);
                chMixRejectMilk.setChecked(false);
                chMixRejectMilk.setCheckMarkDrawable(R.drawable.uncheck_box);
            }
            setRejectFatAndSnf();

        } else if (v == chMixRejectMilk) {
            if (chMixRejectMilk.isChecked()) {

                chMixRejectMilk.setChecked(false);
                chMixRejectMilk.setCheckMarkDrawable(R.drawable.uncheck_box);
            } else {

                chMixRejectMilk.setChecked(true);
                chMixRejectMilk.setCheckMarkDrawable(R.drawable.check_box);

                chCowRejectMilk.setChecked(false);
                chCowRejectMilk.setCheckMarkDrawable(R.drawable.uncheck_box);
                chBuffaloRejectMilk.setChecked(false);
                chBuffaloRejectMilk.setCheckMarkDrawable(R.drawable.uncheck_box);
            }
            setRejectFatAndSnf();

        } else if (v == btnSave) {
            onSubmit();
        } else if (v == btnCancel) {
            finish();
        }

    }

    public void initializeEntities() {
        etEndEvnShift.setText(amcuConfig.getKeyCollEndEvnShift());
        etStartEvnShift.setText(amcuConfig.getKeyCollStartEvnShift());
        etMrnStartTime.setText(amcuConfig.getKeyCollStartMrnShift());
        etEndMrnTime.setText(amcuConfig.getKeyCollEndMrnShift());
        tvZeroFatAndSnf.setChecked(amcuConfig.getKeyIgnoreZeroFatSnf());
        tvEnableShiftConstraints.setChecked(amcuConfig.getKeyEnableCollectionConstraints());
        chTextRejectMilk.setChecked(amcuConfig.getIsRateChartMandatory());

        setRejectFatAndSnf();

    }

    private void setTimeField() {

        Calendar mcurrentTime = Calendar.getInstance();
        mHour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        mMins = mcurrentTime.get(Calendar.MINUTE);

        timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar mcurrentTime = Calendar.getInstance();
                mHour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                mMins = mcurrentTime.get(Calendar.MINUTE);
                if (etTime != null)
                    etTime.setText(Util.padding(hourOfDay) + ":" + Util.padding(minute));
            }
        }, mHour, mMins, true);
    }

    public void setRealStartEndTime() {
        realMrnStartTime = Integer.parseInt(amcuConfig.getMorningStart().replace(":", ""));
        realEvnStartTime = Integer.parseInt(amcuConfig.getEveningStart().replace(":", ""));
    }


    public boolean timeValidation()

    {
        if (etEndEvnShift.getText().toString().length() > 0 && etStartEvnShift.getText().toString().length() > 0
                && etEndMrnTime.getText().toString().length() > 0
                && etMrnStartTime.getText().toString().length() > 0) {

        } else {
            displayMessage("Please enter valid time entries");
            return false;
        }


        if (Integer.parseInt(etMrnStartTime.getText().toString().replace(":", "")) <= realMrnStartTime) {
            displayMessage("Morning start time should be greator than " + amcuConfig.getMorningStart());
            return false;
        } else if (Integer.parseInt(etMrnStartTime.getText().toString().replace(":", "")) >=
                Integer.parseInt(etEndMrnTime.getText().toString().replace(":", ""))) {
            displayMessage("Morning start time should be less than " + etEndMrnTime.getText().toString());
            return false;
        } else if (Integer.parseInt(etEndMrnTime.getText().toString().replace(":", "")) >=
                Integer.parseInt(etStartEvnShift.getText().toString().replace(":", ""))) {
            displayMessage("Morning end time should be less than " + etStartEvnShift.getText().toString());
            return false;
        } else if (Integer.parseInt(etEndMrnTime.getText().toString().replace(":", "")) >= realEvnStartTime) {
            displayMessage("Morning end time should be less than" + amcuConfig.getEveningStart());
            return false;
        } else if (Integer.parseInt(etStartEvnShift.getText().toString().replace(":", "")) <= realEvnStartTime) {
            displayMessage("Evening start time should be greator than " + amcuConfig.getEveningStart());
            return false;
        } else if (Integer.parseInt(etStartEvnShift.getText().toString().replace(":", "")) >=
                Integer.parseInt(etEndEvnShift.getText().toString().replace(":", "")) &&
                Integer.parseInt(etEndEvnShift.getText().toString().replace(":", "")) > 1200) {
            displayMessage("Evening start time should be less than " + etEndEvnShift.getText().toString());
            return false;
        } else if (Integer.parseInt(etEndEvnShift.getText().toString().replace(":", "")) > 1200 &&
                Integer.parseInt(etEndEvnShift.getText().toString().replace(":", "")) >= (2400 + realMrnStartTime)) {
            displayMessage("Evening end time should be less than " + amcuConfig.getMorningStart());
            return false;
        } else if (Integer.parseInt(etEndEvnShift.getText().toString().replace(":", "")) < 1200
                && Integer.parseInt(etEndEvnShift.getText().toString().replace(":", "")) >= realMrnStartTime) {
            displayMessage("Evening end time should be less than " + amcuConfig.getMorningStart());
            return false;
        }

        return true;

    }

    public void setForOperator() {

        etMrnStartTime.setEnabled(false);
        etEndMrnTime.setEnabled(false);
        etEndEvnShift.setEnabled(false);
        etStartEvnShift.setEnabled(false);
        tvZeroFatAndSnf.setEnabled(false);
        tvEnableShiftConstraints.setEnabled(false);
        chTextRejectMilk.setEnabled(false);
        chMixRejectMilk.setEnabled(false);
        chBuffaloRejectMilk.setEnabled(false);
        chCowRejectMilk.setEnabled(false);

        etMaxSnf.setEnabled(false);
        etMinSnf.setEnabled(false);
        etMaxFat.setEnabled(false);
        etMinFat.setEnabled(false);

        btnSave.setEnabled(false);

    }


    public void onSubmit() {
        if (Util.isOperator(AdvanceConfiguration.this)) {
            displayMessage("Please contact manager to change this configuration!");
            return;
        }


        if (timeValidation() && (chTextRejectMilk.isChecked()
                || (!chTextRejectMilk.isChecked()
                && validateFatAndSnf()))) {
            amcuConfig.setKeyCollStartMrnShift(etMrnStartTime.getText().toString().trim());
            amcuConfig.setKeyCollEndMrnShift(etEndMrnTime.getText().toString().trim());
            amcuConfig.setKeyCollStartEvnShift(etStartEvnShift.getText().toString().trim());
            amcuConfig.setKeyCollEndEvnShift(etEndEvnShift.getText().toString().trim());

            amcuConfig.setKeyIgnoreZeroFatSnf(tvZeroFatAndSnf.isChecked());
            amcuConfig.setKeyEnableCollectionConstraints(tvEnableShiftConstraints.isChecked());

            amcuConfig.setIsRateChartMandatory(chTextRejectMilk.isChecked());

            if (!chTextRejectMilk.isChecked()) {
                saveFatAndSnf();
            }

            configurationManager.getSavedConfigurationList(configurationEntity);
            startService(new Intent(AdvanceConfiguration.this, ConfigurationPush.class));

            displayMessage("Configuration successfully saved!");
            finish();
        }


    }


    public void displayMessage(String message) {
        Util.displayErrorToast(message, AdvanceConfiguration.this);
    }

    public boolean validateFatAndSnf() {
        ValidationHelper validationHelper = new ValidationHelper();
        java.text.DecimalFormat decimalFormat = new java.text.DecimalFormat("#0.0");

        double minFat = Double.parseDouble(validationHelper.getDoubleFromString(decimalFormat,
                etMinFat.getText().toString().trim(), -1));
        double minSnf = Double.parseDouble(validationHelper.getDoubleFromString(decimalFormat,
                etMinSnf.getText().toString().trim(), -1));
        double maxFat = Double.parseDouble(validationHelper.getDoubleFromString(decimalFormat,
                etMaxFat.getText().toString().trim(), -1));
        double maxSnf = Double.parseDouble(validationHelper.getDoubleFromString(decimalFormat,
                etMaxSnf.getText().toString().trim(), -1));

        if (minFat < 0 || minFat >= maxFat) {
            Util.displayErrorToast("Invalid minimum fat", AdvanceConfiguration.this);
            return false;
        } else if (maxFat > 14) {
            Util.displayErrorToast("Invalid maximum fat", AdvanceConfiguration.this);
            return false;
        } else if (minSnf < 0 || minSnf >= maxSnf) {
            Util.displayErrorToast("Invalid minimum SNF", AdvanceConfiguration.this);
            return false;
        } else if (maxSnf > 14) {
            Util.displayErrorToast("Invalid maximum SNF", AdvanceConfiguration.this);
            return false;
        } else

        {
            return true;
        }
    }

    public void setRejectFatAndSnf()

    {

        if (chCowRejectMilk.isChecked()) {
            etMaxSnf.setText(String.valueOf(amcuConfig.getKeyMaxSnfRejectCow()));
            etMaxFat.setText(String.valueOf(amcuConfig.getKeyMaxFatRejectCow()));
            etMinFat.setText(String.valueOf(amcuConfig.getKeyMinFatRejectCow()));
            etMinSnf.setText(String.valueOf(amcuConfig.getKeyMinSnfRejectCow()));
        } else if (chBuffaloRejectMilk.isChecked()) {
            etMaxSnf.setText(String.valueOf(amcuConfig.getKeyMaxSnfRejectBuff()));
            etMaxFat.setText(String.valueOf(amcuConfig.getKeyMaxFatRejectBuff()));
            etMinFat.setText(String.valueOf(amcuConfig.getKeyMinFatRejectBuff()));
            etMinSnf.setText(String.valueOf(amcuConfig.getKeyMinSnfRejectBuff()));
        } else if (chMixRejectMilk.isChecked()) {
            etMaxSnf.setText(String.valueOf(amcuConfig.getKeyMaxSnfRejectMix()));
            etMaxFat.setText(String.valueOf(amcuConfig.getKeyMaxFatRejectMix()));
            etMinFat.setText(String.valueOf(amcuConfig.getKeyMinFatRejectMix()));
            etMinSnf.setText(String.valueOf(amcuConfig.getKeyMinSnfRejectMix()));
        }
    }

    public void saveFatAndSnf() {
        ValidationHelper validationHelper = new ValidationHelper();
        java.text.DecimalFormat decimalFormat = new java.text.DecimalFormat("#0.0");
        if (chCowRejectMilk.isChecked()) {

            amcuConfig.setKeyMinFatRejectCow(Float.valueOf(validationHelper.getDoubleFromString(
                    decimalFormat, etMinFat.getText().toString().trim(), 0)));
            amcuConfig.setKeyMinSnfRejectCow(Float.valueOf(validationHelper.getDoubleFromString(
                    decimalFormat, etMinSnf.getText().toString().trim(), 0)));
            amcuConfig.setKeyMaxFatRejectCow(Float.valueOf(validationHelper.getDoubleFromString(
                    decimalFormat, etMaxFat.getText().toString().trim(), 14)));
            amcuConfig.setKeyMaxSnfRejectCow(Float.valueOf(validationHelper.getDoubleFromString(
                    decimalFormat, etMaxSnf.getText().toString().trim(), 14)));
        } else if (chBuffaloRejectMilk.isChecked()) {
            amcuConfig.setKeyMinFatRejectBuff(Float.valueOf(validationHelper.getDoubleFromString(
                    decimalFormat, etMinFat.getText().toString().trim(), 0)));
            amcuConfig.setKeyMinSnfRejectBuff(Float.valueOf(validationHelper.getDoubleFromString(
                    decimalFormat, etMinSnf.getText().toString().trim(), 0)));
            amcuConfig.setKeyMaxFatRejectBuff(Float.valueOf(validationHelper.getDoubleFromString(
                    decimalFormat, etMaxFat.getText().toString().trim(), 14)));
            amcuConfig.setKeyMaxSnfRejectBuff(Float.valueOf(validationHelper.getDoubleFromString(
                    decimalFormat, etMaxSnf.getText().toString().trim(), 14)));
        } else if (chMixRejectMilk.isChecked()) {
            amcuConfig.setKeyMinFatRejectMix(Float.valueOf(validationHelper.getDoubleFromString(
                    decimalFormat, etMinFat.getText().toString().trim(), 0)));
            amcuConfig.setKeyMinSnfRejectMix(Float.valueOf(validationHelper.getDoubleFromString(
                    decimalFormat, etMinSnf.getText().toString().trim(), 0)));
            amcuConfig.setKeyMaxFatRejectMix(Float.valueOf(validationHelper.getDoubleFromString(
                    decimalFormat, etMaxFat.getText().toString().trim(), 14)));
            amcuConfig.setKeyMaxSnfRejectMix(Float.valueOf(validationHelper.getDoubleFromString(
                    decimalFormat, etMaxSnf.getText().toString().trim(), 14)));
        }

    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_PAGE_DOWN: {
                btnCancel.requestFocus();
            }
        }
        return super.onKeyUp(keyCode, event);
    }
}
