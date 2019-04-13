package com.devapp.devmain.util;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class RegexTextWatcher implements TextWatcher {
    private EditText mView;
    private String mRegex;
    private String original;
    private int start;
    private boolean isCorrecting = false;

    public RegexTextWatcher(EditText editText, String regex) {
        mView = editText;
        mRegex = regex;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (!isCorrecting) {
            start = i;
            isCorrecting = false;
        }
        original = charSequence.toString();
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (!editable.toString().matches(mRegex)) {
            try {
                isCorrecting = true;
                mView.setText(original);
                mView.setSelection(start);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
