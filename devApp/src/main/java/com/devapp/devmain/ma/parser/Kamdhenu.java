package com.devapp.devmain.ma.parser;

import android.view.View;

import com.devapp.devmain.entity.MilkAnalyserEntity;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;


/**
 * Created by Upendra on 9/23/2016.
 */
public class Kamdhenu extends Ma {

    public Kamdhenu() {
        decimalFormatFat = new DecimalFormat("#0.0");
        decimalFormatSNF = new DecimalFormat("#0.0");
        decimalFormatAw = new DecimalFormat("#0.00");
    }

    @Override
    public void displayAlert(View.OnClickListener onClickListener, boolean mandatory) {

    }

    @Override
    public MilkAnalyserEntity parse(String strKamdhenu, ByteArrayOutputStream baos) throws NonAnalysisDataException {
        MilkAnalyserEntity maEntity = new MilkAnalyserEntity();

        int i;
        try {
            i = strKamdhenu.indexOf("Temp");
            String temp = strKamdhenu.substring(i + 6, i + 11);

            if (temp != null && i != -1) {
                temp = temp.replace(" ", "");
            } else {
                temp = "0.0";
            }
            maEntity.temp = Double.parseDouble(temp);
            paramsRead.add(Params.TEMPERATURE);
            i = strKamdhenu.indexOf("Fat");
            String fat = strKamdhenu.substring(i + 15, i + 20);
            fat = fat.replace("%", "");
            fat = fat.replaceAll(" ", "");
            maEntity.fat = Double.parseDouble(fat);
            paramsRead.add(Params.FAT);

            i = strKamdhenu.indexOf("SNF");
            String snf = strKamdhenu.substring(i + 15, i + 20);
            snf = snf.replace("%", "");
            snf = snf.replaceAll(" ", "");
            maEntity.snf = Double.parseDouble(snf);
            paramsRead.add(Params.SNF);

            i = strKamdhenu.indexOf("Protein");
            String protein = strKamdhenu.substring(i + 15, i + 20);
            protein = protein.replace("%", "");
            protein = protein.replaceAll(" ", "");
            maEntity.protein = Double.parseDouble(protein);
            paramsRead.add(Params.PROTEIN);

            i = strKamdhenu.indexOf("Added water");
            if (i != -1) {
                String addedWater = strKamdhenu.substring(i + 15, i + 20);
                addedWater = addedWater.replace("%", "");
                addedWater = addedWater.replace(" ", "");
                maEntity.addedWater = Double.parseDouble(addedWater);
                paramsRead.add(Params.ADDED_WATER);
            }
            i = strKamdhenu.indexOf("Density");
            if (i != -1) {
                String density = strKamdhenu.substring(i + 15, i + 20);
                density = density.replace("%", "");
                density = density.replace(" ", "");
                maEntity.density = Double.parseDouble(density);
                paramsRead.add(Params.DENSITY);
            }
            i = strKamdhenu.indexOf("Lactose");
            if (i != -1) {
                String lactose = strKamdhenu.substring(i + 15, i + 20);
                lactose = lactose.replace("%", "");
                lactose = lactose.replace(" ", "");
                maEntity.lactose = Double.parseDouble(lactose);
                paramsRead.add(Params.LACTOSE);
            }
            i = strKamdhenu.indexOf("Solids");
            if (i != -1) {
                String solids = strKamdhenu.substring(i + 15, i + 20);
                solids = solids.replace("%", "");
                solids = solids.replace(" ", "");
                maEntity.solids = Double.parseDouble(solids);
                paramsRead.add(Params.SOLIDS);
            }
            i = strKamdhenu.indexOf("CLR");
            if (i != -1) {
                String clr = strKamdhenu.substring(i + 15, i + 20);
                clr = clr.replace("%", "");
                clr = clr.replace(" ", "");
                maEntity.clr = Double.parseDouble(clr);
                paramsRead.add(Params.CLR);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return maEntity;
    }

    @Override
    public String validateData(String strData, ByteArrayOutputStream baos) throws NonAnalysisDataException, DataFlushRequiredException {
        int hashIndex = -1;
        int newLineIndex = -1;
        if (strData.contains("temperature")) {
            hashIndex = strData.lastIndexOf("temperature");
            if (hashIndex != -1) {
                newLineIndex = strData.indexOf("CLR", hashIndex);
            }
        }
        if (hashIndex != -1 && newLineIndex != -1) {
            strData = strData.substring(hashIndex + 1, newLineIndex);
            return strData;
        }
        return null;
    }

}
