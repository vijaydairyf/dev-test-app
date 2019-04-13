package com.devapp.devmain.util;

import android.text.InputFilter;
import android.text.Spanned;

import com.devapp.devmain.util.logger.Log;

public class RegexInputFilter implements InputFilter {

    private String mRegex;

    public RegexInputFilter(String regex) {
        mRegex = regex;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        Log.d("Incoming Source", source.toString());
        if (source.equals("")) { // for backspace
            return source;
        }
        if (source.toString().matches(mRegex)) {
            Log.d("inputFilter", source.toString());
            return source;
        }
        return "";
    }
}
