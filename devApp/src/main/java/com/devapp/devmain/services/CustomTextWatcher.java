package com.devapp.devmain.services;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.text.NumberFormat;

public class CustomTextWatcher implements TextWatcher {
    private static final int INTEGER_CONSTRAINT = 6;
    private static final int FRACTION_CONSTRAINT = 1;
    private static final int MAX_LENGTH = INTEGER_CONSTRAINT
            + FRACTION_CONSTRAINT + 1;
    private NumberFormat nf = NumberFormat.getNumberInstance();
    private EditText et;
    private String tmp = "";
    private int moveCaretTo;

    public CustomTextWatcher(EditText et, int intCons, int fractCons) {
        this.et = et;
        nf.setMaximumIntegerDigits(intCons);
        nf.setMaximumFractionDigits(fractCons);
        nf.setGroupingUsed(false);
    }

    public int countOccurrences(String str, char c) {
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == c) {
                count++;
            }
        }
        return count;
    }

    @Override
    public void afterTextChanged(Editable s) {
        et.removeTextChangedListener(this); // remove to prevent stackoverflow
        String ss = s.toString();
        int len = ss.length();
        int dots = countOccurrences(ss, '.');
        boolean shouldParse = dots <= 1
                && (dots == 0 ? len != (INTEGER_CONSTRAINT + 1)
                : len < (MAX_LENGTH + 1));
        if (shouldParse) {
            if (len > 1 && ss.lastIndexOf(".") != len - 1) {
                try {
                    Double d = Double.parseDouble(ss);
                    if (d != null) {
                        et.setText(nf.format(d));
                    }
                } catch (NumberFormatException e) {
                }
            }
        } else {
            et.setText(tmp);
        }
        et.addTextChangedListener(this); // reset listener

        // tried to fix caret positioning after key type:
        if (et.getText().toString().length() > 0) {
            if (dots == 0 && len >= INTEGER_CONSTRAINT
                    && moveCaretTo > INTEGER_CONSTRAINT) {
                moveCaretTo = INTEGER_CONSTRAINT;
            } else if (dots > 0 && len >= (MAX_LENGTH)
                    && moveCaretTo > (MAX_LENGTH)) {
                moveCaretTo = MAX_LENGTH;
            }
            try {
                et.setSelection(et.getText().toString().length());
                // et.setSelection(moveCaretTo); <- almost had it :))
            } catch (Exception e) {
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {
        moveCaretTo = et.getSelectionEnd();
        tmp = s.toString();
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        int length = et.getText().toString().length();
        if (length > 0) {
            moveCaretTo = start + count - before;
        }
    }
}