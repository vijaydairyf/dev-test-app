package com.devapp.devmain.ConsolidationPost;

import android.content.Context;

import com.devapp.devmain.agentfarmersplit.QualityParams;
import com.devapp.devmain.agentfarmersplit.QuantityParams;
import com.devapp.devmain.agentfarmersplit.RateParams;
import com.devapp.devmain.postentities.MilkAnalyser;
import com.devapp.devmain.postentities.QualityMetaData;
import com.devapp.devmain.postentities.QualityParamsPost;
import com.devapp.devmain.postentities.QualityReadingData;
import com.devapp.devmain.postentities.QuantityParamspost;
import com.devapp.devmain.postentities.RateParamsPost;
import com.devapp.devmain.postentities.TemperatureData;
import com.devapp.devmain.postentities.WeighingScaleData;
import com.devapp.devmain.server.DatabaseHandler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by u_pendra on 18/1/18.
 */

public class ConsolidatedHelperMethods {


    private static ConsolidatedHelperMethods consolidatedHelperMethods;
    private static Context mContext;

    DatabaseHandler databaseHandler;

    private ConsolidatedHelperMethods(Context context) {
        this.mContext = context;
        databaseHandler = DatabaseHandler.getDatabaseInstance();
    }

    public static ConsolidatedHelperMethods getInstance(Context context) {
        if (consolidatedHelperMethods == null) {
            consolidatedHelperMethods = new ConsolidatedHelperMethods(context);
        }

        return consolidatedHelperMethods;
    }

    public static Date getCollectionDateFromLongTime(long time) {
        long milliSec = time;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US);
        GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone("Asia/Kolkata"));
        calendar.setTimeInMillis(milliSec);
        System.out.println("GregorianCalendar -" + sdf.format(calendar.getTime()));

        Date date = null;
        try {
            date = sdf.parse(sdf.format(calendar.getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }


    public QualityParamsPost fromQualityParams(QualityParams qualityParams) {
        QualityParamsPost qualityParamsPost = new QualityParamsPost();

        MilkAnalyser milkAnalyser = new MilkAnalyser();
        QualityReadingData qualityReadingData = new QualityReadingData();

        qualityReadingData.alcohol = qualityParams.alcohol;
        qualityReadingData.fat = qualityParams.fat;
        qualityReadingData.snf = qualityParams.snf;
        qualityReadingData.awm = qualityParams.awm;
        qualityReadingData.clr = qualityParams.clr;
        qualityReadingData.com = qualityParams.com;
        qualityReadingData.density = qualityParams.density;
        qualityReadingData.freezingPoint = qualityParams.freezingPoint;
        qualityReadingData.lactose = qualityParams.lactose;
        qualityReadingData.pH = qualityParams.pH;
        qualityReadingData.protein = qualityParams.protein;
        qualityReadingData.salt = qualityParams.salt;

        TemperatureData temperatureData = new TemperatureData();
        temperatureData.temperature = qualityParams.temperature;
        temperatureData.unit = "F";

        qualityReadingData.temperature = temperatureData;

        QualityMetaData qualityMetaData = new QualityMetaData();

        qualityMetaData.calibration = qualityParams.calibration;
        qualityMetaData.maData = qualityParams.maData;
        qualityMetaData.maMake = qualityParams.maName;
        qualityMetaData.maNumber = qualityParams.maNumber;
        qualityMetaData.serialNumber = qualityParams.maSerialNumber;


        milkAnalyser.qualityMetaData = qualityMetaData;
        milkAnalyser.qualityReadingData = qualityReadingData;

        qualityParamsPost.milkAnalyser = milkAnalyser;
        qualityParamsPost.measuredTime = getCollectionDateFromLongTime(qualityParams.qualityTime);
        qualityParamsPost.qualityTime = getCollectionDateFromLongTime(qualityParams.qualityTime);

        qualityParamsPost.mode = qualityParams.qualityMode;
        qualityParamsPost.milkStatusCode = qualityParams.milkStatusCode;
        qualityParamsPost.milkQuality = qualityParams.milkQuality;


        return qualityParamsPost;
    }


    public QuantityParamspost fromQuantityParams(QuantityParams quantityParams) {

        QuantityParamspost quantityParamspost = new QuantityParamspost();

        WeighingScaleData weighingScaleData = new WeighingScaleData();
        weighingScaleData.inKg = quantityParams.kgQuantity;
        weighingScaleData.inLtr = quantityParams.ltrQuantity;
        weighingScaleData.measuredValue = quantityParams.weighingQuantity;
        weighingScaleData.measurementUnit = quantityParams.measurementUnit;

        quantityParamspost.weighingScaleData = weighingScaleData;

        quantityParamspost.measurementTime = getCollectionDateFromLongTime(
                quantityParams.quantityTime);
        quantityParamspost.tippingEndTime = getCollectionDateFromLongTime(
                quantityParams.tippingEndTime);
        quantityParamspost.tippingStartTime = getCollectionDateFromLongTime(
                quantityParams.tippingStartTime);

        quantityParamspost.mode = quantityParams.quantityMode;

        return quantityParamspost;


    }

    public RateParamsPost fromRateParams(RateParams rateParams) {

        RateParamsPost rateParamsPost = new RateParamsPost();

        rateParamsPost.amountToBePaid = rateParams.amount;
        rateParamsPost.bonus = rateParams.bonus;
        rateParamsPost.incentive = rateParams.incentiveAmount;
        rateParamsPost.isRateCalculated = rateParams.isRateCalculated;
        rateParamsPost.mode = rateParams.rateMode;
        rateParamsPost.rate = rateParams.rate;
        rateParamsPost.rateChartName = rateParams.rateChartName;
        rateParamsPost.unit = rateParams.rateUnit;
        rateParamsPost.incentiveRate = rateParams.incentiveRate;

        return rateParamsPost;
    }


}
