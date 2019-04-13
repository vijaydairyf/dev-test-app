package com.devapp.devmain.additionalRecords;

import java.util.ArrayList;

/**
 * Created by x on 26/7/17.
 */

public class CustomFieldEntity {
    public String name;
    public String displayName;
    public String type;
    public String validation;
    public ArrayList<String> applicableFor = new ArrayList<>();
    public String hint;
    public boolean editable;
}
