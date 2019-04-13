package com.devapp.devmain.main;

import android.content.Context;

import com.devapp.devmain.devicemanager.DatabaseManager;
import com.devapp.devmain.entity.MilkAnalyserEntity;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.server.SessionManager;

/**
 * Created by Upendra on 2/10/2016.
 */
public class CalculateBonus {

    private static CalculateBonus calculateBonus = null;
    DatabaseHandler dbh;
    AmcuConfig amcuConfig;
    SessionManager session;
    private Context mContext;

    private CalculateBonus(Context ctx) {
        this.mContext = ctx;
        amcuConfig = AmcuConfig.getInstance();
        session = new SessionManager(ctx);
        dbh = DatabaseHandler.getDatabaseInstance();

    }

    public static CalculateBonus getInstance(Context ctx) {
        if (calculateBonus == null) {
            calculateBonus = new CalculateBonus(ctx);
        }

        return calculateBonus;
    }


    public String getBonusRateChart(String milkType) {
        String rateChart = null;

        if (milkType.equalsIgnoreCase("COW")) {
            rateChart = amcuConfig.getBonusRateChartCow();
        } else if (milkType.equalsIgnoreCase("BUFFALO")) {
            rateChart = amcuConfig.getBonusRateChartBuffalo();
        } else if (milkType.equalsIgnoreCase("MIXED")) {
            rateChart = amcuConfig.getBonusRateChartMixed();
        }
        return rateChart;
    }


    public double getBonus(double fat, double snf, double clr, String milkType) {
        double bonus = 0;
        String rateChartName = getBonusRateChart(milkType);
        if (rateChartName != null) {
            DatabaseManager dbManager = new DatabaseManager(mContext);
            String strBonus = dbManager.getRateForGivenParams(fat, snf, clr, rateChartName);
            bonus = Double.parseDouble(strBonus);
        }


        return bonus;
    }

    public String getDynamicRateChart(String milkType) {
        String rateChart = null;

        if (milkType.equalsIgnoreCase("COW")) {
            rateChart = amcuConfig.getDynamicRateChartCow();
        } else if (milkType.equalsIgnoreCase("BUFFALO")) {
            rateChart = amcuConfig.getDynamicRateChartBuffalo();
        } else if (milkType.equalsIgnoreCase("MIXED")) {
            rateChart = amcuConfig.getDynamicRateChartMixed();
        }
        return rateChart;
    }

    public double getDynamicAmount(MilkAnalyserEntity maEntity, String milkType) {
        double dynamicRate = 0;
        String rateChartName = getDynamicRateChart(milkType);
        if (rateChartName != null) {
            DatabaseManager dbManager = new DatabaseManager(mContext);
            String strRate = dbManager.getDynamicRate(maEntity, rateChartName);
            dynamicRate = Double.parseDouble(strRate);
        }

        return dynamicRate;
    }
}
