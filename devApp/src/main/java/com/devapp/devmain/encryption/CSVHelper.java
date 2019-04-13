package com.devapp.devmain.encryption;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class CSVHelper<T> {

    private static char VALUE_SEPERATOR = ',';
    private Class<T> clazz;

    public CSVHelper(Class<T> clazz) {
        this.clazz = clazz;
    }

    public List<T> parse(String data,
                         Map<String, String> columnMap) throws IllegalArgumentException {

        if (data == null || columnMap == null)
            throw new IllegalArgumentException(
                    "Passed Data or Column Map is null");

        HeaderColumnNameTranslateMappingStrategy<T> translationStrategy = new HeaderColumnNameTranslateMappingStrategy<T>();
        translationStrategy.setType(this.clazz);
        translationStrategy.setColumnMapping(columnMap);

        CsvToBean<T> csvToBean = new CsvToBean<T>();
        CSVReader reader = new CSVReader(new StringReader(data));
        List<T> elements = csvToBean.parse(translationStrategy,
                reader);


        return elements;
    }

    public String stringify(List<? extends SerializeableToCSV> objects)
            throws IllegalArgumentException {

        if (objects == null || objects.isEmpty())
            throw new IllegalArgumentException(
                    "List of objectes passed is either null or empty");

        List<String[]> strList = new ArrayList<String[]>();

        // add header record
        strList.add(objects.get(0).getColumnHeaders());

        Iterator<? extends SerializeableToCSV> it = objects.iterator();
        while (it.hasNext()) {
            SerializeableToCSV element = it.next();
            strList.add(element.getValueList());
        }

        StringWriter writer = new StringWriter();
        CSVWriter csvWriter = new CSVWriter(writer, CSVHelper.VALUE_SEPERATOR,
                CSVWriter.NO_QUOTE_CHARACTER);
        csvWriter.writeAll(strList);

        try {
            csvWriter.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return writer.toString();
    }

    public String stringifyString(String strData) {
        List<String[]> strList = new ArrayList<String[]>();
        strList.add(new String[]{strData});
        StringWriter writer = new StringWriter();
        CSVWriter csvWriter = new CSVWriter(writer, CSVHelper.VALUE_SEPERATOR,
                CSVWriter.NO_QUOTE_CHARACTER);
        csvWriter.writeAll(strList);

        try {
            csvWriter.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return writer.toString();
    }

}
