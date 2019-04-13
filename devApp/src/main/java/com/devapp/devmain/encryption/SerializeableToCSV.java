package com.devapp.devmain.encryption;

import java.util.Map;

public interface SerializeableToCSV {

    public String[] getColumnHeaders();

    public String[] getValueList();

    public Map<String, String> getColumnMap();
}
