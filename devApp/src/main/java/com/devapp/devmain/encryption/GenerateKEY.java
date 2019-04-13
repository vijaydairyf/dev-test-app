package com.devapp.devmain.encryption;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Upendra on 10/8/2015.
 */
public class GenerateKEY implements SerializeableToCSV {

    public String key;

    public GenerateKEY() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String str) {
        this.key = str;
    }

    @Override
    public String[] getColumnHeaders() {
        return new String[]{"KEY"};
    }

    @Override
    public String[] getValueList() {
        return new String[]{getKey()};
    }

    @Override
    public Map<String, String> getColumnMap() {

        Map<String, String> columnMap = new HashMap<String, String>();

        columnMap.put("KEY", "key");
        return columnMap;
    }


    public String toString() {
        return this.getKey();
    }
}
