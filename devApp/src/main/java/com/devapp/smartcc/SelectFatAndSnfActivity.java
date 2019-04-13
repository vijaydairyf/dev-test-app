package com.devapp.smartcc;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.devApp.R;


/**
 * Created by u_pendra on 13/12/16.
 */

public class SelectFatAndSnfActivity extends AppCompatActivity implements View.OnTouchListener {


    public String textResponse;
    Spinner spFat, spSnf;
    private TextView txtResponse, tvFat, tvFatValue, tvSnf, tvSnfValue;
    private EditText etFatPosition, etFatDivisionFactor, etSnfPosition, etSnfdivisionFactor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        textResponse = getIntent().getStringExtra("TEXT_RESPONSE");
        setContentView(R.layout.activity_select_fat_snf);
        initializeView();
        onTouchText();
        onTextChange();
        setResponseText();

    }


    public void initializeView() {
        txtResponse = (TextView) findViewById(R.id.txtResponse);
        tvFat = (TextView) findViewById(R.id.tvFat);
        tvFatValue = (TextView) findViewById(R.id.tvFatValue);
        tvSnf = (TextView) findViewById(R.id.tvSnf);
        tvSnfValue = (TextView) findViewById(R.id.tvSnfValue);
        etFatPosition = (EditText) findViewById(R.id.etFatPosition);
        etFatDivisionFactor = (EditText) findViewById(R.id.etFatDivisionFactor);
        etSnfPosition = (EditText) findViewById(R.id.etSnfPosition);
        etSnfdivisionFactor = (EditText) findViewById(R.id.etSnfdivisionFactor);

    }

    public void onTouchText() {
        txtResponse.setOnTouchListener(this);
        tvFat.setOnTouchListener(this);
        tvFatValue.setOnTouchListener(this);
        tvSnf.setOnTouchListener(this);
        tvSnfValue.setOnTouchListener(this);
    }

    public void setResponseText() {
        String response = "";
        String[] tokenList = getToken();
        for (int i = 0; i < tokenList.length; i++) {
            response = response + tokenList[i] + "," + "\n";
        }


        txtResponse.setText(response);
    }

    public String getPos(int i) {
        return "Pos " + i + ": ";

    }


    public String[] getToken() {
        String delem = "[ ]+";
        String[] tokenList = textResponse.split(delem);
        for (int i = 0; i < tokenList.length; i++) {
            tokenList[i] = getPos(i) + tokenList[i].replaceAll("[^\\w\\s]", "");
        }
        return tokenList;

    }


    @Override
    public boolean onTouch(View v, MotionEvent motionEvent) {

        if (v == txtResponse) {
        } else if (v == tvFat) {
        } else if (v == tvFatValue) {
        } else if (v == tvSnf) {
        } else if (v == tvSnfValue) {
        }

        return false;
    }


    public void onTextChange() {

        etFatPosition.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                //String[] position[] = etFatPosition.getText().toString().
                try {
                    int pos = Integer.parseInt(etFatPosition.getText().toString());

                    if (pos > -1 && pos < getToken().length) {
                        tvFatValue.setText(getToken()[pos]);
                    }

                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

            }
        });
//Text Changed
        etFatDivisionFactor.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                try {
                    int divFactor = Integer.parseInt(etFatDivisionFactor.getText().toString());

                    if (divFactor > -1 && divFactor <= 1000) {
                        tvFatValue.setText(String.valueOf(Double.parseDouble(tvFatValue.getText().toString()) / divFactor));
                    }

                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

            }
        });
//Text Changed
        etSnfPosition.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                try {
                    int pos = Integer.parseInt(etSnfPosition.getText().toString());

                    if (pos > -1 && pos < getToken().length) {
                        tvSnfValue.setText(getToken()[pos]);
                    }

                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

            }
        });
//Text Changed
        etSnfdivisionFactor.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                try {
                    int divFactor = Integer.parseInt(etSnfdivisionFactor.getText().toString());

                    if (divFactor > -1 && divFactor <= 1000) {
                        tvSnfValue.setText(String.valueOf(Double.parseDouble(tvSnfValue.getText().toString()) / divFactor));
                    }

                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

            }
        });


    }


    public void onSpinnerSelect() {
        spFat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        spSnf.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                int decimalValue = 0;
                if (spSnf.getSelectedItem().toString().equalsIgnoreCase("Binary")) {
                    decimalValue = Integer.parseInt(tvSnfValue.getText().toString(), 2);

                } else if (spSnf.getSelectedItem().toString().equalsIgnoreCase("Octal")) {

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }


}
