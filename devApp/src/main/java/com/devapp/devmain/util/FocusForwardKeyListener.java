package com.devapp.devmain.util;

import android.view.KeyEvent;
import android.view.View;
import android.widget.AutoCompleteTextView;

import static android.view.KeyEvent.KEYCODE_NUMPAD_ENTER;

public class FocusForwardKeyListener implements View.OnKeyListener {
    View nextView;

    public FocusForwardKeyListener(View nextView) {
        this.nextView = nextView;
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {

        if (keyCode == KEYCODE_NUMPAD_ENTER) {
            if (event.getAction() == KeyEvent.ACTION_UP) {
                v.dispatchKeyEvent(new KeyEvent(0, 0, KeyEvent.ACTION_DOWN,
                        KeyEvent.KEYCODE_ENTER, 0));
                v.dispatchKeyEvent(new KeyEvent(0, 0, KeyEvent.ACTION_UP,
                        KeyEvent.KEYCODE_ENTER, 0));
            }
            return true;
        }

        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
            if (v instanceof AutoCompleteTextView) {
                AutoCompleteTextView view = (AutoCompleteTextView) v;
                if (view.isPopupShowing()) {
                    return false;
                }
            }
            nextView.requestFocus();
            return true;
        }
        return false;
    }
}
