package com.devapp.devmain.additionalRecords;

import android.content.Context;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.devapp.devmain.agentfarmersplit.AppConstants;
import com.devapp.devmain.util.AdvanceUtil;
import com.devapp.smartcc.report.ReportHintConstant;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;


/**
 * Created by x on 26/7/17.
 */

public class FormViewHolder {
    public static final String STRING = "string";
    public static final String INTEGER = "int";
    public static final String BOOLEAN = "boolean";
    public static final String SELECTION = "selection";
    public static final String DOUBLE = "double";
    public Context mContext;
    public CustomFieldEntity mCFE;
    public String name;
    public String id;
    public String type;
    public boolean editable;
    public String hint;
    public String validationString;
    public View view;
    public JSONObject validation;
    AdvanceUtil advanceUtil;

    public FormViewHolder() {

    }


    public FormViewHolder(String name, String id, View view) {
        this.name = name;
        this.id = id;
        this.view = view;
    }

    public FormViewHolder(String name, String id, String type, String validationString, View view) {
        this.name = name;
        this.id = id;
        this.type = type;
        this.validationString = validationString;
        this.view = view;
        if (validationString != null && !validationString.equals("")) {
            try {
                validation = new JSONObject(validationString);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            validation = null;
        }
    }

    public FormViewHolder(Context context, String name, String id, String type, boolean editable, String hint, String validationString) {
        mContext = context;
        this.name = name;
        this.id = id;
        this.type = type;
        this.editable = editable;
        this.hint = hint;
        this.validationString = validationString;
        if (validationString != null && !validationString.equals("")) {
            try {
                validation = new JSONObject(validationString);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            validation = null;
        }
        this.view = initialiseView();
    }

    public FormViewHolder(Context context, CustomFieldEntity cFE) {
        mContext = context;
        this.name = cFE.displayName;
        this.id = cFE.name;
        this.type = cFE.type;
        this.editable = cFE.editable;
        this.hint = cFE.hint;
        this.validationString = cFE.validation;
        if (validationString != null && !validationString.equals("")) {
            try {
                validation = new JSONObject(validationString);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            validation = null;
        }
        advanceUtil = new AdvanceUtil(mContext);
        this.view = initialiseView();
    }

    private View initialiseView() {
        View view;
        switch (type) {
            case STRING:
            case INTEGER:
            case DOUBLE:
            case BOOLEAN:
            case SELECTION:
                if (validation != null && validation.optJSONArray(ValidationKeys.ENUM) != null
                        && validation.optJSONArray(ValidationKeys.ENUM).length() > 0) {
                    view = new AutoCompleteTextView(mContext);
                } else {
                    view = new EditText(mContext);
                }
                break;

            default:
                view = new EditText(mContext);
        }
        setUpView(view);
        return view;
    }

    private void setUpView(View v) {
        if (v instanceof AutoCompleteTextView) {
            setUpAutoCompleteTextView((AutoCompleteTextView) v);
        }
        if (v instanceof EditText) {
            if (hint != null) {
                ((EditText) v).setHint(hint);
            }
            if (validation != null) {
                ArrayList<InputFilter> ifL = new ArrayList<>();
                if (validation.optInt(ValidationKeys.MAX_LENGTH, 0) >= 0) {
                    try {
                        ifL.add(new InputFilter.LengthFilter(
                                validation.getInt(ValidationKeys.MAX_LENGTH)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (validation.optBoolean(ValidationKeys.ALL_CAPS, false)) {
                    ifL.add(new InputFilter.AllCaps());
                }
                InputFilter[] ifA = new InputFilter[ifL.size()];
                ifA = ifL.toArray(ifA);
                ((EditText) v).setFilters(ifA);
                if (type.equals(STRING)) {
                    if (validation.has(ValidationKeys.INPUT_TYPE)) {
                        if (validation.optString(ValidationKeys.INPUT_TYPE).equals("numeric")) {
                            ((EditText) v).setInputType(InputType.TYPE_CLASS_NUMBER);
                        }
                        if (validation.optString(ValidationKeys.INPUT_TYPE).equals("text")) {
                            ((EditText) v).setInputType(InputType.TYPE_CLASS_TEXT);
                        }
                    }
                }
            }

            if (type.equals(INTEGER)) {
                ((EditText) v).setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED);
            } else if (type.equals(DOUBLE)) {
                ((EditText) v).setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
            }

            advanceUtil.toSetDrawableOnText((EditText) v, name + " : ",
                    AppConstants.COLLECTION_HINT
                    , ReportHintConstant.ADDITIONAL_PADDING);

        }
    }

    private void setUpAutoCompleteTextView(AutoCompleteTextView aCTV) {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < validation.optJSONArray(ValidationKeys.ENUM).length(); i++) {
            list.add(validation.optJSONArray(ValidationKeys.ENUM).optString(i, null));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (mContext, android.R.layout.select_dialog_item, list);
        aCTV.setThreshold(1);
        aCTV.setAdapter(adapter);
    }

    public boolean validate() throws FieldValidationException {
        boolean valid = true;
        if (validation != null) {
            String value = ((EditText) view).getText().toString().trim();

            if (view instanceof EditText) {

                if (value.equalsIgnoreCase("")) {
                    return true;
                }

                if (type.equals(STRING)) {
                    if (validation.has(ValidationKeys.MIN_LENGTH)) {
                        if (value.length() < validation.optInt(ValidationKeys.MIN_LENGTH)) {
                            throw new FieldValidationException(name + " should be atleast "
                                    + validation.optInt(ValidationKeys.MIN_LENGTH) + " characters long");
                        }
                    }
                    if (validation.has(ValidationKeys.MAX_LENGTH)) {
                        if (validation.optInt(ValidationKeys.MAX_LENGTH) > 0) {
                            if (value.length() > validation.optInt(ValidationKeys.MAX_LENGTH)) {
                                throw new FieldValidationException(name + " should not be more than "
                                        + validation.optInt(ValidationKeys.MIN_LENGTH) + " characters long");
                            }
                        }
                    }
                }
                if (type.equals(INTEGER) || type.equals(DOUBLE) && value.matches("[0-9]+")) {
                    if (validation.has(ValidationKeys.LOWER_LIMIT)) {
                        if (Double.valueOf(value) < validation.optInt(ValidationKeys.LOWER_LIMIT)) {
                            throw new FieldValidationException(name + " should be greater than " +
                                    validation.optInt(ValidationKeys.LOWER_LIMIT));
                        }
                    }
                    if (validation.has(ValidationKeys.UPPER_LIMIT)) {
                        if (validation.optInt(ValidationKeys.UPPER_LIMIT) > 0) {
                            if (Double.valueOf(value) > validation.optInt(ValidationKeys.UPPER_LIMIT)) {
                                throw new FieldValidationException(name + " should be lesser than " +
                                        validation.optInt(ValidationKeys.UPPER_LIMIT));
                            }
                        }
                    }
                }
                if (validation.has(ValidationKeys.ENUM)) {
                    if (validation.optJSONArray(ValidationKeys.ENUM).length() > 0) {
                        ArrayList<String> list = new ArrayList<>();
                        for (int i = 0; i < validation.optJSONArray(ValidationKeys.ENUM).length(); i++) {
                            list.add(validation.optJSONArray(ValidationKeys.ENUM).optString(i, null));
                        }
                        if (!list.contains(value)) {
                            throw new FieldValidationException("Invalid value for " + name);
                        }
                    }
                }
                if (validation.has(ValidationKeys.REGEX)) {
                    if (!validation.optString(ValidationKeys.REGEX, "").equals("")
                            && !validation.optString(ValidationKeys.REGEX, "").equalsIgnoreCase("null")) {
                        if (!value.matches(validation.optString(ValidationKeys.REGEX))) {
                            throw new FieldValidationException("Invalid value for " + name);
                        }
                    }
                }
            }
        }
        return valid;
    }

    public String getValue() {
        String value = null;
        if (view instanceof EditText) {
            if (!((EditText) view).getText().toString().trim().equalsIgnoreCase("")) {
                value = ((EditText) view).getText().toString();
                if (type.equals(DOUBLE)) {
                    if (validation.has(ValidationKeys.FORMAT)) {
                        if (!validation.optString(ValidationKeys.FORMAT).equals("") && !validation.optString(ValidationKeys.FORMAT).equalsIgnoreCase("null")) {
                            try {
                                value = new DecimalFormat(validation.optString(ValidationKeys.FORMAT)).format(Double.valueOf(value));
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
        return value;
    }

    public void setValue(String value) {
        if (view instanceof EditText) {
            ((EditText) view).setText(value);
            if (value != null && !value.equals("")) {
                if (!editable) {
                    ((EditText) view).setEnabled(false);
                }
            }
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public String getValidationString() {
        return validationString;
    }

    public void setValidationString(String validationString) {
        this.validationString = validationString;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    interface ValidationKeys {
        String INPUT_TYPE = "inputType";
        String MAX_LENGTH = "maxLength";
        String MIN_LENGTH = "minLength";
        String ENUM = "enum";
        String FORMAT = "format";
        String REGEX = "regex";
        String ALL_CAPS = "allCaps";
        String LOWER_LIMIT = "lowerLimit";
        String UPPER_LIMIT = "upperLimit";
    }

    public class FieldValidationException extends Exception {
        public FieldValidationException(String message) {
            super(message);
        }
    }
}
