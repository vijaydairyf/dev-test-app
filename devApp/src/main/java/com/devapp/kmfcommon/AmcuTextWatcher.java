package com.devapp.kmfcommon;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;


/**
 * Created by yyy on 26/4/16.
 */
public class AmcuTextWatcher implements TextWatcher {

    public View view;

    public AmcuTextWatcher(View view) {
        this.view = view;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        String text = editable.toString();
        switch (view.getId()) {
            /*case R.id.name:
                model.setName(text);
                break;
            case R.id.email:
                model.setEmail(text);
                break;
            case R.id.phone:
                model.setPhone(text);
                break;*/
        }
    }
}
