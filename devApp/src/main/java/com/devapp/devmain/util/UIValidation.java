package com.devapp.devmain.util;

import android.content.Context;
import android.text.InputFilter;
import android.text.Spanned;
import android.widget.EditText;
import android.widget.Toast;

import com.devapp.devmain.user.Util;

/**
 * Created by u_pendra on 19/4/18.
 */

public class UIValidation {

    private static UIValidation mUIValidation;
    InputFilter filter;

    private UIValidation() {
    }

    public static UIValidation getInstance() {
        if (mUIValidation == null) {
            mUIValidation = new UIValidation();
        }

        return mUIValidation;
    }


    public boolean nameValidation(EditText etText) {
        if (etText == null) {
            return false;
        } else if (etText.getText().toString().trim().length() == 0) {
            return false;
        } else if (etText.getText().toString().trim().length() > 20) {
            return false;
        } else {
            return true;
        }

    }

    public boolean numberValidation(EditText editText) {

        if (editText != null) {
            try {
                double num = Double.parseDouble(editText.getText().toString());
            } catch (NumberFormatException e) {
                e.printStackTrace();

                return false;
            }
            return true;
        } else {
            return false;
        }
    }


    public void alphabetValidation(final EditText edit, final int check,
                                   final Context ctx, final int lengthOfEditText) {
        filter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {

                if (lengthOfEditText != 0) {
                    edit.setFilters(new InputFilter[]{filter,
                            new InputFilter.LengthFilter(lengthOfEditText)});
                }

                if (check == Util.ONLY_ALPHABET) {
                    for (int i = start; i < end; i++) {
                        if (!Character.isLetter(source.charAt(i))
                                && !Character.toString(source.charAt(i))
                                .equals(" ")
                                && !Character.toString(source.charAt(i))
                                .equals(".")) {
                            Toast.makeText(ctx, "Only alphabets allowed!",
                                    Toast.LENGTH_SHORT).show();
                            edit.setText("");
                            return "";

                        }
                    }
                } else if (check == Util.ONLY_ALPHANUMERIC) {
                    for (int i = start; i < end; i++) {
                        if (!Character.isLetterOrDigit(source.charAt(i))
                                && !Character.toString(source.charAt(i))
                                .equals("_")) {
                            Toast.makeText(ctx, "Only alphanumerics allowed!",
                                    Toast.LENGTH_SHORT).show();
                            edit.setText("");
                            return "";
                        }
                    }
                } else if (check == Util.ALPHANUMERIC_SPACE) {
                    // for alpha numeric with space
                    for (int i = start; i < end; i++) {
                        if (!Character.isLetterOrDigit(source.charAt(i))
                                && !Character.toString(source.charAt(i))
                                .equals("_") && !Character.toString(source.charAt(i))
                                .equals(" ")) {
                            Toast.makeText(ctx, "Only alphanumerics allowed!",
                                    Toast.LENGTH_SHORT).show();
                            edit.setText("");
                            return "";
                        }
                    }
                } else if (check == Util.ONLY_NUMERIC) {
                    for (int i = start; i < end; i++) {
                        if (!Character.isDigit(source.charAt(i))) {
                            Toast.makeText(ctx, "Only numerics allowed!",
                                    Toast.LENGTH_SHORT).show();
                            edit.setText("");
                            return "";
                        }

                    }
                } else if (check == Util.ONLY_DECIMAL) {
                    for (int i = start; i < end; i++) {
                        if (!Character.isDigit(source.charAt(i))
                                && !Character.toString(source.charAt(i))
                                .equals(".")) {
                            Toast.makeText(ctx,
                                    "Only decimal numbers allowed!",
                                    Toast.LENGTH_SHORT).show();
                            edit.setText("");
                            return "";
                        }

                    }
                }


                return null;
            }
        };
        edit.setFilters(new InputFilter[]{filter});
    }


}
