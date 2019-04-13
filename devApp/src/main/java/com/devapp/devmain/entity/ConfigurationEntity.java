package com.devapp.devmain.entity;

import java.io.Serializable;

/**
 * Created by u_pendra on 22/2/17.
 */

public class ConfigurationEntity implements Serializable {

    public String morningSessionStartTime;
    public String eveningSessionStartTime;
    public String morningSessionEndTime;
    public String eveningSessionEndTime;
    public String perDaySMSLimit;


    //Save session

    public String milkType;
    public String rateChart;
    public String milkAnalyzerName;
    public String printerName;
    public String weighingScale;
    public String secondMAType;

    //To set baudrate
    public String milkAnalyzerBaudrate;
    public String printerBaudrate;
    public String rduBaudrate;
    public String WeighingBaudrate;
    public String secondMABaudrate;

    //for clr calculation

    public String fatConstantForClr;
    public String snfConstantForClr;
    public String constantForClr;


    //for roundoff
    public String weightRoundOff;
    public String amountRoundOff;
    public String rateRoundOff;

    public String weightRoundOffCheck;
    public String amountRoundOffCheck;
    public String rateRoundOffCheck;

    //For essae rdu
    public String isAddedWater;
    public String isRateEditCheck;

    //To set buffalo ratechart
    public String changeMilktypeBuffalo;
    public String changeSnfMilkTypeBuffalo;

    //for tab shutdown delay
    public String shutDownDelay;

    public String isMaManual;
    public String isWsManual;

    //For bonus
    public String bonusStartDate;
    public String bonusEndDate;
    public String bonus;
    public String isBonusEnable;

    //Regarding mail and SMS
    public String sendShiftMails;
    public String enableSMS;

    public String isRateImportAccessOperator;
    public String isImportExcelSheetAccess;

    public String simlockPassword;
    public String allowSimlock;

    //Credentials
    public String smartAmcuEmail;
    public String smartAmcuPassword;

    public String smsFooter;
    public String collectionSMSFooter;

    //Enable manual to device
    public String enableManualToDevice;
    public String enableSimlockToDevice;
    public String enableBonusToDevice;
    public String enableMultipleCollection;
    public String enableConfigurableFarmerIdSize;

    //Enable or disable the reject
    public String enableRejectToDevice;

    public String acceptMilkInKgFormat;
    public String enableConversionFactor;
    public String conversionFactor;

    public String weighingScalePrefix;
    public String weighingScaleSuffix;
    public String weighingScaleSeparator;
    public String displayKgToDevice;
    public String isRateChartInKg;
    public String weighingDivisionFactor;

    public String enableSalesFS;
    public String salesBuffRate;
    public String salesCowRate;
    public String enableSales;

    public String enableBonusDisplayRDU;
    public String enableIncentiveOnRdu;
    public String enableIpTableRule;

    //For sales


    public String enableSalesFarmerId;
    public String urlHeader;
    public String incentivePercentage;
    public String enableIncentiveInReport;
    public String enableSequenceNumberReport;


    public String enableClrInReport;
    public String enableTruckEntry;
    public String enableCenterCollection;
    public String enableDairyReport;
    public String enableFilledOrEmptyCans;
    public String tareCommand;
    public String selectRateChartType;
    public String enableBonusForPrint;

    //Allow editable rate
    public String enableEditableRate;
    //To allow farmer export mail
    public String enableFarmerExportMail;
    //To allow the milkquality
    public String enableMilkQuality;
    // To allow equipmentbasedcollection
    public String enableEquipmentBasedCollection;
    public String setAmcuServer;
    //Allow route and number of cans in report
    public String allowNumberOfCansInReport;
    public String allowCollectionRouteInReport;
    public String enableFatSnfViewForCollection;
    public String enableFatClrViewForChilling;
    public String enableAllFatSnfClrView;
    public String visiblityReportEditing;

    public String visibilityCanToggling;
    public String allowCanToggling;
    public String maxLimitEmptyCan;
    public String minLimitFilledCan;

    // Clr round off
    public String clrRoundOffUpto;

    // overruling the config setting
    public String allowFSCinSMS;
    public String allowFSCinPrint;

    public String multipleMA;
    public String customMyRateChart;
    public String allowMaxLimitFromRC;
    public String allowRoundOffFatAndSnf;

    public String ekoMilkClrPosition;
    public String ekoMilkAddedWaterPosition;

    public String smartCCFeature;
    public String allowWSTare;

    public String createMultipleUsers;
    public String materialCode;


    public String tareDeviationWeight;
    public String deviationAlertForWeight;
    public String isRateCalculatedFromDevice;

    //Milk lane phase 2 requirement
    public String ignoreZeroFatSNF;
    public String enableCollectionContraints;

    public String collectionStartMorningshift;
    public String collectionEndMorningShift;
    public String collectionStartEveningShift;
    public String collectionEndEveningShift;

    public String isDisplayAmount;
    public String isDisplayQuantity;
    public String isDisplayRate;
    public String userConfig;
    public String weighingParity;
    public String weighingDatbits;
    public String weighingStopbits;

    public String rduName;


    public String managerPassword;
    public String managerMobileNumber;
    public String managerEmailID;
    public String operatorPassword;
    public String operatorMobileNumber;
    public String centerName;
    public String centerCode;
    public String centerLocation;
    public String centerRoute;
    public String centerBMCID;
    public String centerAddress;
    public String centerContactPerson1;
    public String centerContactPerson2;
    public String centerContactEmail1;
    public String centerContactEmail2;
    public String centerContactMobile1;
    public String centerContactMobile2;

    public String maxWeighingScaleLimit;
    public String enableOrDisableIpTable;

    public String rduQuantityRoundOff;
    public String snfOrClrForTanker;


    public String isCloudSupport;
    public String unsentAlertLimit;

    public String showUnsentAlert;
    public String defalutMilkTypeBoth;
    public String minValidWeightForCollection;

    public String deleteCollRecordShift;
    public String deleteFilesAfterDay;

    public String escapeEnableForCollection;

    public String devicePwd;
    // public String licenceJson ="licenceJson";

    public String whiteListURL;
    public String deviceId;
    public String calibrationDate;
    public String createEdit;
    public String farmIdDigit;
    public String maParity;
    public String mastopbits;
    public String maDatabits;
    public String periodicDataSent;
    public String startTimeDataSent;

    public String periodicDeviceDataSend;

    public String allowOperatorToReportEditing;
    public String numberOfEditableShift;


    public String licenceJson;
    public String mCCorABC;
    public String updateRateMiddleSession;
    public String tripCodeStartIndex;
    public String maxTrip;
    public String startTankerIndex;
    public String perDayTankerLimit;
    public String isTareOnStart;

    public String weightRecordLength;
    public String sampleAsCollection;
    public String mergedHMBEnable;

    public String lactoscanPrams;

    public String ma1Parity;
    public String ma1stopbits;
    public String ma1Databits;
    public String ma2Parity;
    public String ma2stopbits;
    public String ma2Databits;

    public String isMandatoryRatechart;

    public String keyMinFatRejectCow;
    public String keyMinSnfRejectCow;
    public String keyMaxFatRejectCow;
    public String keyMaxSnfRejectCow;

    public String enablehpPrinter;

    public String sendEmaililConf;
    public String rateExcelImport;

    public String keyMinFatRejectBuff;
    public String keyMinSnfRejectBuff;
    public String keyMaxFatRejectBuff;
    public String keyMaxSnfRejectBuff;

    public String keyMinFatRejectMix;
    public String keyMinSnfRejectMix;
    public String keyMaxFatRejectMix;
    public String keyMaxSnfRejectMix;


    public String maMilkType1;
    public String maMilkType2;

    public String ma1Baudrate;
    public String ma2Baudrate;
    public String ma1Name;
    public String ma2Name;
    public String keyAllowFormerIncrement;


    public String allowAgentSplit;
    public String allowEditCollection;

    public String allowProteinValue;
    public String incentiveRateChartName;

    public String allowTwoDecimal;


    public String displayFATConfiguration;
    public String displaySNFConfiguration;
    public String displayCLRConfiguration;
    public String displayProteinConfiguration;

    public String rateFATConfiguration;
    public String rateSNFConfiguration;
    public String rateCLRConfiguration;
    public String rateProteinConfiguration;


    public String roundFATConfiguration;
    public String roundSNFConfiguration;
    public String roundCLRConfiguration;
    public String roundProteinConfiguration;

    public String rduPassword;
    public String hotspotSsid;
    public String enableHotspot;
    public String enableLactoseBasedRejection;
    public String keyMaxLactoseRejectMix;
    public String keyMaxLactoseRejectBuff;
    public String keyMaxLactoseRejectCow;
    public String allowApkUpgradeFromFileSystem;
    public String enableDynamicRateChart;

    public String allowDisplayRate;
    public String enableWifi;
    public String enableDispatchReport;
    public String disableManualForDispatch;
    public String salesMixedRate;

    public String kgFat;
    public String kgSnf;
    public String kgClr;
    public String allowFarmerEdit;
    public String allowFarmerCreate;
    public String allowNameInReport;
    public String displayDairyname;

    public String wsIgnoreThreshold;
    public String wsKgCommand;
    public String wsLitreCommand;
    public String weightReadRoundOff;
    public String weightReadRoundingMode;


}
