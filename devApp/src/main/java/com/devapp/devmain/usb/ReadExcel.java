package com.devapp.devmain.usb;

import android.content.Context;

import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.SessionManager;
import com.devapp.devmain.user.Util;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import jxl.Cell;
import jxl.CellType;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class ReadExcel {

    DecimalFormat decimalFormatAMT = new DecimalFormat("#0.00");
    DecimalFormat decimalFormatFS = new DecimalFormat("#0.0");
    ArrayList<RateEntityExcel> allRateEnt;
    SessionManager session;
    AmcuConfig amcuConfig;
    private String inputFile;
    private Context mcontext;
    private int check;

    public void setInputFile(String inputFile, int check) {
        this.inputFile = inputFile;
        this.check = check;

        allRateEnt = new ArrayList<RateEntityExcel>();
    }


    public Map<String, String> readConfiguration(Context context) {
        File configWorkbook = new File(inputFile);
        this.mcontext = context;
        amcuConfig = AmcuConfig.getInstance();
        session = new SessionManager(context);

        TreeMap treeMap = new TreeMap();
        Workbook workbook;

        try {
            workbook = Workbook.getWorkbook(configWorkbook);
            Sheet sheet = workbook.getSheet(0);


            for (int r = 1; r < sheet.getRows(); r++) {
                Cell cell = sheet.getCell(0, r);
                String key = null, value = null;

                key = cell.getContents();
                cell = sheet.getCell(1, r);
                value = cell.getContents();

                treeMap.put(key, value);
            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        }

        return treeMap;
    }


    @SuppressWarnings("finally")
    public ArrayList<RateEntityExcel> read(Context context) throws IOException {
        File inputWorkbook = new File(inputFile);
        this.mcontext = context;
        amcuConfig = AmcuConfig.getInstance();
        session = new SessionManager(context);

        Workbook w;
        try {
            w = Workbook.getWorkbook(inputWorkbook);
            // Get the first sheet
            Sheet sheet = w.getSheet(0);
            // Loop over first 10 column and lines

            for (int row = 0; row < sheet.getColumns(); row++) {

                for (int column = 0; column < sheet.getRows(); column++) {
                    Cell cell = sheet.getCell(row, column);
                    CellType type = cell.getType();

                    if (check == Util.CHECK_RATECHART)

                    {
                        // For Rate
                        if (row >= 2 && column >= 2) {
                            RateEntityExcel rateEntEx = new RateEntityExcel();

                            type = cell.getType();
                            if (type == CellType.NUMBER) {
                                if (cell.getContents() != null
                                        && !cell.getContents().isEmpty()) {
                                    try {
                                        rateEntEx.rate = Double
                                                .valueOf(decimalFormatAMT.format(Double
                                                        .parseDouble(cell
                                                                .getContents())));
                                    } catch (NumberFormatException e) {
                                        e.printStackTrace();
                                        return null;
                                    }
                                } else {

                                    return null;
                                }
                            } else {

                                return null;
                            }

                            // For fat and Snf

                            try {
                                cell = sheet.getCell(0, column);
                                type = cell.getType();

                                if (type == CellType.NUMBER) {
                                    if (cell.getContents() != null
                                            && !cell.getContents().isEmpty()) {
                                        rateEntEx.fat = Double
                                                .valueOf(decimalFormatFS.format(Double
                                                        .parseDouble(cell
                                                                .getContents())));
                                    } else {

                                        return null;
                                    }
                                } else {

                                    return null;
                                }

                                cell = sheet.getCell(row, 0);

                                if (cell.getType() == CellType.NUMBER) {
                                    if (cell.getContents() != null
                                            && !cell.getContents().isEmpty()) {
                                        rateEntEx.snf = Double
                                                .valueOf(decimalFormatFS.format(Double
                                                        .parseDouble(cell
                                                                .getContents())));
                                    } else {

                                        return null;
                                    }
                                } else {

                                    return null;
                                }
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                                return null;
                            } catch (Exception e) {
                                e.printStackTrace();
                                return null;
                            }
                            allRateEnt.add(rateEntEx);
                        }
                    }
                }

            }
        } catch (BiffException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return allRateEnt;
    }

    public class RateEntityExcel implements Serializable {
        /**
         *
         */
        private static final long serialVersionUID = 1L;
        public double fat;
        public double snf;
        public double rate;

    }

}