package com.devapp.devmain.util.config;

import java.util.Properties;

public class AppConfig implements Configuration {

    private final Properties config;

    AppConfig() {
        config = DefaultConfigurationHandler.getInstance().getConfig();
    }

    @Override
    public String get(Configuration.Key key) {
        return config.getProperty(key.toString());
    }

    @Override
    public String get(Configuration.Key key, String defaultValue) {
        return config.getProperty(key.toString(), defaultValue);
    }

    public enum Key implements Configuration.Key {

        //other props
        ALLOW_MAX_LIMIT_FROM_RC("allowMaxLimitFromRC", "false"),
        createMultipleUsers("createMultipleUsers", "false"),
        customMyRateChart("customMyRateChart", "false"),
        enableConfigurableFarmerIdSize("enableConfigurableFarmerIdSize", "false"),
        enableIpTableRule("enableIpTableRule", "true"),
        enableManualToDevice("enableManualToDevice", "true"),
        enableMultipleCollection("enableMultipleCollection", "false"),
        materialCode("materialCode", "2600004"),
        ignoreZeroFatSNF("ignoreZeroFatSNF", "false"),
        shutDownDelay("shutDownDelay", "0"),
        enableRejectToDevice("enableRejectToDevice", "true"),
        enableOrDisableIpTable("enableOrDisableIpTable", "true"),
        //FIXME remove this property if it isnt relevant
        mergedHMBEnable("mergedHMBEnable", "false"),
        createEdit("createEdit", "false"),
        farmIdDigit("farmIdDigit", "4"),
        periodicDataSent("periodicDataSent", "3"),
        startTimeDataSent("startTimeDataSent", "5"),
        periodicDeviceDataSend("periodicDeviceDataSend", "3"),
        tripCodeStartIndex("tripCodeStartIndex", "501"),
        maxTrip("maxTrip", "20"),
        startTankerIndex("startTankerIndex", "900"),
        perDayTankerLimit("perDayTankerLimit", "1"),
        isTareOnStart("isTareOnStart", "false"),
        keyAllowFormerIncrement("keyAllowFormerIncrement", "false"),
        allowFamerCreate("allowFamerCreate", "true"),
        allowFamerEdit("allowFamerEdit", "true"),
        deleteFilesAfterDay("deleteFilesAfterDay", "30"),
        hotspotSsid("hotspotSsid", "smartiot"),
        previousHotspotSsid("previousHotspotSsid", "smartudp"),

        //milk collection related props.
        ACCEPT_MILK_IN_KG_FORMAT("acceptMilkInKgFormat", "false"),
        ALLOW_MILK_CAN_TOGGLING("allowCanToggling", "false"),
        changeMilktypeBuffalo("changeMilktypeBuffalo", "0"),
        changeSnfMilkTypeBuffalo("changeSnfMilkTypeBuffalo", "0"),
        deviationAlertForWeight("deviationAlertForWeight", "0.1"),
        displayKgToDevice("displayKgToDevice", "false"),
        enableMilkQuality("enableMilkQuality", "true"),
        isAddedWater("isAddedWater", "false"),
        maxLimitEmptyCan("maxLimitEmptyCan", "25"),
        minLimitFilledCan("minLimitFilledCan", "10"),
        multipleMA("multipleMA", "false"),
        numberOfEditableShift("numberOfEditableShift", "0"),
        enableFilledOrEmptyCans("enableFilledOrEmptyCans", "false"),
        snfOrClrForTanker("snfOrClrForTanker", "CLR"),
        weightRecordLength("weightRecordLength", "0"),
        sampleAsCollection("sampleAsCollection", "true"),
        milkType("milkType", ""),
        defalutMilkTypeBoth("defalutMilkTypeBoth", "COW"),
        escapeEnableForCollection("escapeEnableForCollection", "false"),
        keyMinFatRejectCow("keyMinFatRejectCow", "0"),
        keyMinSnfRejectCow("keyMinSnfRejectCow", "0"),
        keyMaxFatRejectCow("keyMaxFatRejectCow", "14"),
        keyMaxSnfRejectCow("keyMaxSnfRejectCow", "14"),
        keyMinFatRejectBuff("keyMinFatRejectBuff", "0"),
        keyMinSnfRejectBuff("keyMinSnfRejectBuff", "0"),
        keyMaxFatRejectBuff("keyMaxFatRejectBuff", "14"),
        keyMaxSnfRejectBuff("keyMaxSnfRejectBuff", "14"),
        keyMinFatRejectMix("keyMinFatRejectMix", "0"),
        keyMinSnfRejectMix("keyMinSnfRejectMix", "0"),
        keyMaxFatRejectMix("keyMaxFatRejectMix", "6"),
        keyMaxSnfRejectMix("keyMaxSnfRejectMix", "6"),
        allowAgentSplit("allowAgentSplit", "false"),
        allowEditCollection("allowEditCollection", "false"),
        KeyAllowProtein("KeyAllowProtein", "false"),
        minValidWeightForCollection("minValidWeightForCollection", "0.100f"),


        //props related to milk analyzer.
        ekoMilkAddedWaterPosition("ekoMilkAddedWaterPosition", "4"),
        ekoMilkClrPosition("ekoMilkClrPosition", "3"),
        isMaManual("isMaManual", "false"),
        milkAnalyzerBaudrate("milkAnalyzerBaudrate", "9600"),
        milkAnalyzerName("milkAnalyzerName", "LACTOSCAN"),
        secondMAType("secondMAType", "LACTOSCAN"),
        secondMABaudrate("secondMABaudrate", "9600"),
        lactoscanPrams("lactoscanPrams", ""),
        maParity("maParity", "PARITY_NONE"),
        maDatabits("maDatabits", "DATA_BITS_8"),
        mastopbits("mastopbits", "STOP_BITS_1"),
        ma1Parity("ma1Parity", "PARITY_NONE"),
        ma1stopbits("ma1stopbits", "STOP_BITS_1"),
        ma1Databits("ma1Databits", "DATA_BITS_8"),
        ma2Parity("ma2Parity", "PARITY_NONE"),
        ma2stopbits("ma2stopbits", "STOP_BITS_1"),
        ma2Databits("ma2Databits", "DATA_BITS_8"),
        maMilkType1("maMilkType1", "NONE"),
        maMilkType2("maMilkType2", "NONE"),
        ma1Baudrate("ma1Baudrate", "9600"),
        ma2Baudrate("ma2Baudrate", "9600"),
        ma1Name("ma1Name", "LACTOSCAN"),
        ma2Name("ma2Name", "LACTOSCAN"),

        //props related to rdu.
        enableBonusDisplayRDU("enableBonusDisplayRDU", "false"),
        enableIncentiveOnRdu("enableIncentiveOnRdu", "false"),
        isDisplayAmount("isDisplayAmount", "true"),
        isDisplayQuantity("isDisplayQuantity", "true"),
        isDisplayRate("isDisplayRate", "true"),
        rduBaudrate("rduBaudrate", "9600"),
        rduName("rduName", "ESSAE"),
        displayFATConfiguration("displayFATConfiguration", "1"),
        displaySNFConfiguration("displaySNFConfiguration", "1"),
        displayCLRConfiguration("displayCLRConfiguration", "1"),
        displayProteinConfiguration("displayProteinConfiguration", "2"),
        allowDisplayRate("allowDisplayRate", "true"),
        rduPassword("rduPassword", "1111"),

        //props related to WS
        ALLOW_WS_TARE("allowWSTare", "true"),
        tareCommand("tareCommand", "T"),
        tareDeviationWeight("tareDeviationWeight", "0.1"),
        weighingScale("weighingScale", "ESSAE"),
        weighingDatabits("weighingDatbits", "DATA_BITS_8"),
        weighingScalePrefix("weighingScalePrefix", "N"),
        weighingParity("weighingParity", "PARITY_NONE"),
        weighingScaleSeparator("weighingScaleSeparator", "CRLF"),
        weighingScaleSuffix("weighingScaleSuffix", "=lt"),
        weighingStopbits("weighingStopbits", "STOP_BITS_1"),
        maxWeighingScaleLimit("maxWeighingScaleLimit", "200"),
        WeighingBaudrate("WeighingBaudrate", "9600"),
        wsIgnoreThreshold("wsIgnoreThreshold", "50"),
        wsKgCommand("wsKgCommand", ""),
        wsLitreCommand("wsLitreCommand", ""),
        IS_WS_MANUAL("isWsManual", "true"),

        //props related to rate charts
        enableEditableRate("enableEditableRate", "false"),
        isRateImportAccessOperator("isRateImportAccessOperator", "false"),
        isRateChartInKg("isRateChartInKg", "false"),
        isRateEditCheck("isRateEditCheck", "false"),
        rateChart("rateChart", ""),
        salesBuffRate("salesBuffRate", "0"),
        salesCowRate("salesCowRate", "0"),
        salesMixedRate("salesMixedRate", "0"),
        selectRateChartType("selectRateChartType", "FATSNF"),
        isRateCalculatedFromDevice("isRateCalculatedFromDevice", "true"),
        updateRateMiddleSession("updateRateMiddleSession", "false"),
        rateChartMandatory("rateChartMandatory", "true"),
        incentiveRateChartName("incentiveRateChartName", ""),
        rateFATConfiguration("rateFATConfiguration", "1"),
        rateSNFConfiguration("rateSNFConfiguration", "1"),
        rateCLRConfiguration("rateCLRConfiguration", "1"),
        rateProteinConfiguration("rateProteinConfiguration", "2"),

        //props related to printer
        printerBaudrate("printerBaudrate", "9600"),
        printerName("printerName", "SMARTMOO"),
        enablehpPrinter("enablehpPrinter", "false"),

        //props related to round off, Conversion factor, formulas, computes and so on.
        AMOUNT_ROUND_OFF_PRECISION("amountRoundOff", "2"),
        CLR_ROUND_OFF_PRECISION("clrRoundOffUpto", "1"),
        IS_FAT_SNF_ROUND_OFF_ENABLED("allowRoundOffFatAndSnf", "true"),
        CLR_CONSTANT("constantForClr", "1.44"),
        KG_TO_LTR_CONVERSION_FACTOR("conversionFactor", "1.027"),
        IS_CONVERSiON_FACTOR_ENABLED("enableConversionFactor", "false"),
        fatConstantForClr("fatConstantForClr", "0.84"),
        RATE_ROUND_OFF_PRECISION("rateRoundOff", "2"),
        IS_RATE_ROUND_OFF_ENABLED("rateRoundOffCheck", "true"),
        weightRoundOff("weightRoundOff", "2"),
        weighingDivisionFactor("weighingDivisionFactor", "1"),
        weightRoundOffCheck("weightRoundOffCheck", "true"),
        amountRoundOffCheck("amountRoundOffCheck", "true"),
        snfConstantForClr("snfConstantForClr", "4"),
        rduQuantityRoundOff("rduQuantityRoundOff", "true"),
        calibrationDate("calibrationDate", "12"),
        allowTwoDecimal("allowTwoDecimal", "false"),
        roundFATConfiguration("roundFATConfiguration", "HALF_UP"),
        roundSNFConfiguration("roundSNFConfiguration", "HALF_UP"),
        roundCLRConfiguration("roundCLRConfiguration", "HALF_UP"),
        roundProteinConfiguration("roundProteinConfiguration", "HALF_UP"),
        kgFat("kgFat", "0.0"),
        kgSnf("kgSnf", "0.0"),
        kgClr("kgClr", "0.0"),

        //props related to reports, sms, email, report print and so on.
        INCLUDE_FSC_IN_PRINT("allowFSCinPrint", "false"),
        INCLUDE_FSC_IN_SMS("allowFSCinSMS", "false"),
        ALLOW_COLLECTION_ROUTE_IN_REPORT("allowCollectionRouteInReport", "false"),
        INCLUDE_CAN_COUNT_IN_REPORT("allowNumberOfCansInReport", "false"),
        PERMIT_OPERATOR_TO_EDIT_REPORT("allowOperatorToReportEditing", "false"),
        SMS_FOOTER_TEXT("collectionSMSFooter", "Powered by smartAmcu"),
        enableAllFatSnfClrView("enableAllFatSnfClrView", "false"),
        INCLUDE_CLR_IN_REPORT("enableClrInReport", "false"),
        IS_DAIRY_REPORT_ENABLED("enableDairyReport", "false"),
        enableFarmerExportMail("enableFarmerExportMail", "false"),
        enableIncentiveInReport("enableIncentiveInReport", "false"),
        enableSMS("enableSMS", "false"),
        isImportExcelSheetAccess("isImportExcelSheetAccess", "false"),
        perDaySMSLimit("perDaySMSLimit", "0"),
        sendShiftMails("sendShiftMails", "false"),
        visiblityReportEditing("visiblityReportEditing", "false"),
        visibilityCanToggling("visibilityCanToggling", "false"),
        smsFooter("smsFooter", "http://www.stellapps.com/"),
        enableSequenceNumberReport("enableSequenceNumberReport", "true"),
        unsentAlertLimit("unsentAlertLimit", "100"),
        showUnsentAlert("showUnsentAlert", "false"),
        sendEmailConf("sendEmaililConf", "false"),
        rateExcelImport("rateExcelImport", "true"),

        // sim lock feature related props
        IS_SIM_LOCK_ENABLED("allowSimlock", "false"),
        enableSimlockToDevice("enableSimlockToDevice", "false"),
        simlockPassword("simlockPassword", ""),

        //bonus feature related props
        BONUS_AMOUNT("bonus", "0"),
        bonusEndDate("bonusEndDate", ""),
        bonusStartDate("bonusStartDate", ""),
        IS_PRINT_BONUS_ENABLED("enableBonusForPrint", "false"),
        enableBonusToDevice("enableBonusToDevice", "false"),
        IS_BONUS_COMPUTE_ENABLED("isBonusEnable", "false"),
        incentivePercentage("incentivePercentage", "40"),

        //Collection center, shift, operator, manager, site related props
        COLLECTION_CENTER_EVENING_SHIFT_END_TIME("collectionEndEveningShift", "22:00"),
        COLLECTION_CENTER_MORNING_SHIFT_END_TIME("collectionEndMorningShift", "11:00"),
        COLLECTION_CENTER_EVENING_SHIFT_START_TIME("collectionStartEveningShift", "16:00"),
        COLLECTION_CENTER_MORNING_SHIFT_START_TIME("collectionStartMorningshift", "05:00"),
        enableCollectionContraints("enableCollectionContraints", "false"),
        enableCenterCollection("enableCenterCollection", "false"),
        enableEquipmentBasedCollection("enableEquipmentBasedCollection", "false"),
        enableFatSnfViewForCollection("enableFatSnfViewForCollection", "FS"),
        enableSales("enableSales", "false"),
        enableSalesFarmerId("enableSalesFarmerId", "false"),
        enableSalesFS("enableSalesFS", "false"),
        enableTruckEntry("enableTruckEntry", "false"),
        managerMobileNumber("managerMobileNumber", ""),
        managerPassword("managerPassword", "MANAGER"),
        managerEmailID("managerEmailID", ""),
        operatorPassword("operatorPassword", "operator.smartamcu"),
        operatorMobileNumber("operatorMobileNumber", ""),
        centerAddress("centerAddress", ""),
        centerBMCID("centerBMCID", ""),
        centerCode("centerCode", "SOC123"),
        centerName("centerName", "Devapp Testing"),
        centerContactEmail1("centerContactEmail1", ""),
        centerContactEmail2("centerContactEmail2", ""),
        centerContactMobile1("centerContactMobile1", ""),
        centerContactMobile2("centerContactMobile2", ""),
        centerContactPerson1("centerContactPerson1", ""),
        centerContactPerson2("centerContactPerson2", ""),
        centerLocation("centerLocation", ""),
        centerRoute("centerRoute", ""),
        morningSessionStartTime("morningSessionStartTime", "00:00"),
        eveningSessionStartTime("eveningSessionStartTime", "15:00"),
        morningSessionEndTime("morningSessionEndTime", "15:00"),
        eveningSessionEndTime("eveningSessionEndTime", "02:00"),
        deleteCollRecordShift("deleteCollRecordShift", "120"),

        //props related to chilling center, shift, operator, manager and so on.
        enableFatClrViewForChilling("enableFatClrViewForChilling", "FS"),
        smartCCFeature("smartCCFeature", "false"),
        mCCorABC("mCCorABC", "AMCU"),

        //props related smartamcu cloud service
        setAmcuServer("setAmcuServer", "amcugw2.smartmoo.com"),
        smartAmcuEmail("smartAmcuEmail", "smartamcu.smartmoo@stellapps.com"),
        urlHeader("urlHeader", "https://"),
        userConfig("userConfig", ""),
        isCloudSupport("isCloudSupport", "true"),
        whiteListURL("whiteListURL", "NA"),
        deviceId("deviceId", ""),
        devicePwd("devicePwd", ""),
        licenceJson("licenceJson", ""),
        smartAmcuPassword("smartAmcuPassword", ""),

        allowFarmerEdit("allowFarmerEdit", "true"),
        allowFarmerCreate("allowFarmerCreate", "true"),

        enableHotspot("enableHotspot", "false"),
        enableWifi("enableWifi", "false"),
        enableDispatchReport("enableDispatchReport", "false"),
        disableManualForDispatch("disableManualForDispatch", "false"),
        enableLactoseBasedRejection("enableLactoseBasedRejection", "false"),
        keyMaxLactoseRejectCow("keyMaxLactoseRejectCow", "6.0"),
        keyMaxLactoseRejectBuff("keyMaxLactoseRejectBuff", "6.0"),
        keyMaxLactoseRejectMix("keyMaxLactoseRejectMix", "6.0"),
        allowApkUpgradeFromFileSystem("allowApkUpgradeFromFileSystem", "false"),
        weightReadRoundOff("weightReadRoundOff", "2"),
        weightReadRoundingMode("weightReadRoundingMode", "HALF_UP");


            /*
            // FIXME Missing configs - defined in config entity class but not present in default config table.
            public String smartAmcuPassword;
            public String isMandatoryRatechart;
            public String allowProteinValue;

            public String minValidWeightForCollection;
            public String deleteCollRecordShift;
            public String deleteFilesAfterDay;
            public String kgFat;
            public String kgSnf;
            public String kgClr;
            public String allowFarmerEdit;
            public String allowFarmerCreate;

           */


        private final String name;
        private final String defaultValue;

        Key(String name, String defaultValue) {
            this.name = name;
            this.defaultValue = defaultValue;
        }

        public String getName() {
            return this.name;
        }

        public String getDefaultValue() {
            return this.defaultValue;
        }

        @Override
        public String toString() {
            return "Key{" +
                    "name='" + name + '\'' +
                    ", defaultValue='" + defaultValue + '\'' +
                    '}';
        }
    }
}

