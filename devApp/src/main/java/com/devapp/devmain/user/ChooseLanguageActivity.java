package com.devapp.devmain.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.devapp.devmain.main.LoginActivity;
import com.devapp.devmain.server.SessionManager;
import com.devApp.R;


public class ChooseLanguageActivity extends Activity {

    RadioGroup radioGroup;
    RadioButton selectedRadioButton;
    Button btnSubmit;
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.choose_language);

        radioGroup = (RadioGroup) findViewById(R.id.radioLanguage);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);

        session = new SessionManager(ChooseLanguageActivity.this);

        btnSubmit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                int selectedId = radioGroup.getCheckedRadioButtonId();
                selectedRadioButton = (RadioButton) findViewById(selectedId);

                session.setSelectedLanguage(selectedRadioButton.getText()
                        .toString());

                startActivity(new Intent(ChooseLanguageActivity.this, LoginActivity.class));
                finish();

            }
        });

    }
}
