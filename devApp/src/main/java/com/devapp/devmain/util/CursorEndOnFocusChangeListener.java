package com.devapp.devmain.util;

import android.view.View;
import android.widget.EditText;

public class CursorEndOnFocusChangeListener implements View.OnFocusChangeListener {
    @Override
    public void onFocusChange(View view, boolean b) {
        try {
            EditText v = (EditText) view;
            v.setSelection(v.getText().length());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
