package com.devapp.devmain.devicemanager;

/**
 * Created by Upendra on 2/2/2016.
 */
public class MilkCollectionManager {/*

    public static String ekomilkUltraMessage;
    public String ekoMilkUltraPrevData;
    public String ma1OrMa2 = null;
    SessionManager session;
    SaveSession saveSession;
    DecimalFormat decimalFormatFS;
    // DecimalFormat decimalFormatClr;
    String maName;
    int mabaudRate;
    DecimalFormat decimalFormatClr;
    DecimalFormat decimalFormatFat;
    DecimalFormat decimalFormatSNF;
    DecimalFormat decimalFormatProtein;

    private Context mContext;
    ChooseDecimalFormat chooseDecimalFormat;

    public MilkCollectionManager(Context ctx) {
        mContext = ctx;
        initalizeVariables();
        maName = saveSession.getMA();
        mabaudRate = saveSession.getMABaudRate();
        initializeEkoMilkUltra();


    }

    public static void writeMADebugFile(byte[] byteArray, Context context) {
        String[] dataString = new String[byteArray.length];

        String str = "byteArray: " + byteArray.length + "\n"
                + "byte format: " + "\n";

        for (int i = 0; i < byteArray.length; i++) {
            dataString[i] = String.format("%02X ", byteArray[i]).trim();
            str = str + byteArray[i] + ",";
        }

        str = str + "\n" + " String format " + "\n";

        for (int i = 0; i < dataString.length; i++) {
            str = str + dataString[i] + ",";
        }

        try {
            Util.generateNoteOnSD("MADATA",
                    str + "\n", context, "smartAmcuReports");
        } catch (Exception e) {
        }
    }

    public void initializeEkoMilkUltra() {
        if (ma1OrMa2 == null || ma1OrMa2.equalsIgnoreCase(DeviceName.MA1) ||
                ma1OrMa2.equalsIgnoreCase(DeviceName.MILK_ANALYSER)) {
            ekoMilkUltraPrevData = saveSession.getMilkAnalyserPrvData();
            ekoMilkUltraPrevData = saveSession.getMA1PrevRecord();
            //  Util.displayErrorToast("MA1 selected!",mContext);
        } else if (ma1OrMa2.equalsIgnoreCase(DeviceName.MA2)) {
            ekoMilkUltraPrevData = saveSession.getMA2PrevRecord();
            //   Util.displayErrorToast("MA2 selected!",mContext);
        } else {
            ekoMilkUltraPrevData = saveSession.getMilkAnalyserPrvData();
        }
    }

    public void initalizeVariables() {
        session = new SessionManager(mContext);
        saveSession = SaveSession.getInstance();

        if (saveSession.getClrRoundOffUpto() == 0) {
            decimalFormatClr = new DecimalFormat("#0");
        } else {
            decimalFormatClr = new DecimalFormat("#0.0");
        }
        chooseDecimalFormat = new ChooseDecimalFormat(mContext);
        decimalFormatFS = chooseDecimalFormat.getFatAndSnfFormat();
        decimalFormatFat = chooseDecimalFormat.getDecimalFormatTypeForDisplay(AppConstants.FAT);
        decimalFormatSNF = chooseDecimalFormat.getDecimalFormatTypeForDisplay(AppConstants.SNF);
        decimalFormatClr = chooseDecimalFormat.getDecimalFormatTypeForDisplay(AppConstants.CLR);
        decimalFormatProtein = chooseDecimalFormat.getDecimalFormatTypeForDisplay(AppConstants.PROTEIN);


    }
    //Parse EKOMILKULTRA data

    public MilkAnalyserEntity selectMilkAnalayzerParsingMethod(String strData, ByteArrayOutputStream baos) {
        MilkAnalyserEntity maEntity = null;
//       try{ Util.generateNoteOnSD("MADATA",
//                strData +"\n", mContext,"smartAmcuReports");}
//       catch(Exception e)
//       {
//       }

        if (maName.equalsIgnoreCase("LACTOSCAN")) {

            int hashIndex = -1;
            int newLineIndex = -1;
            if (strData.contains("#")) {
                hashIndex = strData.lastIndexOf('#');
                if (hashIndex != -1) {
                    newLineIndex = strData.indexOf('\r', hashIndex);
                }
            }
            if (hashIndex != -1 && newLineIndex != -1) {
                strData = strData.substring(hashIndex + 1, newLineIndex);
                String str2 = strData;
                str2 = str2.replace(" ", "");
                if (str2.equalsIgnoreCase("CLFL")
                        || str2.equalsIgnoreCase("CLN")
                        || str2.equalsIgnoreCase("RIN")) {
//                    maEntity = new MilkAnalyserEntity();
//                    maEntity.message  = str2.trim();

                } else {

                    maEntity = parseDataFromLactoScan(strData);
                }
            }
        } else if (maName.equalsIgnoreCase("EKOMILK ULTRA PRO")) {
            initializeEkoMilkUltra();

            int parIndex1 = -1;
            int parIndex2 = -1;

            if (strData.contains("(") && strData.contains(")")) {

                parIndex1 = strData.indexOf("(");
                parIndex2 = strData.indexOf(')', parIndex1);

            }

            if (parIndex1 != -1 && parIndex2 != -1) {
                strData = strData.substring(parIndex1 + 1, parIndex2);

                String regex = "[0-9]+";

                // Util.displayErrorToast(maName +": "+strData,mContext);

                if (!strData.matches(regex)) {
                    return null;
                }

                //  Util.displayErrorToast(ekoMilkUltraPrevData,mContext);
                if (ekoMilkUltraPrevData == null
                        || !ekoMilkUltraPrevData
                        .equalsIgnoreCase(strData)) {
//                   writeMADebugFile1("Correct: "+strData+
//                           "\n"+ekoMilkUltraPrevData,mContext);
                    maEntity = parseDataFromEcoMilkUltra(strData);
                } else if (ekoMilkUltraPrevData != null) {
//                    Util.displayErrorToast(ekoMilkUltraPrevData,mContext);
//                    writeMADebugFile1("Duplicate: "+strData+
//                            "\n"+ekoMilkUltraPrevData,mContext);
                }
            }

        } else if (maName.equalsIgnoreCase("EKOMILK")) {

            int amtIdx = -1;
            int dateIdx = strData.indexOf("Date:");
            if (dateIdx != -1)
                amtIdx = strData.indexOf("Amount:", dateIdx);

            if (amtIdx != -1 && dateIdx != -1) {
                maEntity = processEKOMILKData(strData);
            }

        } else if (maName.equalsIgnoreCase("KAMDHENU")) {


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
                try {

                    maEntity = parseKamdhenuData(strData);
                    // For Milk Analyzer

                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

            }

        } else if (maName.equalsIgnoreCase("AKASHGANGA") && (mabaudRate == 9600)) {

            int hashIndex = -1;
            int newLineIndex = -1;
            if (strData.contains("temperature")) {
                hashIndex = strData.lastIndexOf("temperature");
                if (hashIndex != -1) {
                    newLineIndex = strData.indexOf("Fp=", hashIndex);
                }
            }
            if (hashIndex != -1 && newLineIndex != -1) {
                strData = strData.substring(hashIndex, newLineIndex);
                try {
                    maEntity = parseAkashGangaData(strData);
                    // For Milk Analyzer
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        } else if (maName.equalsIgnoreCase("AKASHGANGA") && (mabaudRate == 2400)) {

            int parIndex1 = -1;
            int parIndex2 = -1;

            if (strData.contains("(") && strData.contains(")")) {

                parIndex1 = strData.indexOf("(");
                parIndex2 = strData.indexOf(')', parIndex1);

            }

            if (parIndex1 != -1 && parIndex2 != -1) {
                strData = strData.substring(parIndex1 + 1, parIndex2);
                if (amcuConfig.getMilkAnalyserPrvData() == null
                        || !amcuConfig.getMilkAnalyserPrvData()
                        .equalsIgnoreCase(strData)) {
                    maEntity = parseDataFromEcoMilkUltra(strData);
                }
            }

        } else if (maName.equalsIgnoreCase("INDIFOSS")) {


            String[] strIndifoss = getIndifossArr(strData);
            if (strIndifoss != null) {

//                System.out.println("Indifoss Array: "+strIndifoss.toString());
//                Util.displayErrorToast(strIndifoss.toString(),ChillingCollectionActivity.this);

                maEntity = parseIndifossMilkAnalyserData(strIndifoss);
            }

        } else if (maName.equalsIgnoreCase("NULINE")
                || maName.equalsIgnoreCase("EKOBOND")) {


            if (baos != null && baos.toByteArray().length == 25) {

                maEntity = parseNulineMilkAnalyserData(baos.toByteArray());
                baos.reset();
            } else if (baos != null && baos.toByteArray().length > 25) {
                Util.displayErrorToast("Invalid data, press F10 and reset the MA"
                        , mContext);
            }

        } else if (maName.equalsIgnoreCase("LACTOSCAN_V2")) {


            int hashIndex = -1;
            int newLineIndex = -1;
            if (strData.contains("#")) {
                hashIndex = strData.indexOf('#');
                if (hashIndex != -1) {
                    newLineIndex = strData.indexOf("\r\n", hashIndex);
                }
            }
            if (hashIndex != -1 && newLineIndex != -1) {
                strData = strData.substring(hashIndex + 1, newLineIndex);
                String str2 = strData;

                str2 = str2.replace(" ", "");
                if (str2.equalsIgnoreCase("CLFL")
                        || str2.equalsIgnoreCase("CLN")
                        || str2.equalsIgnoreCase("RIN")) {

                    if (!session.getIsSample()) {
                        //finish();
                    } else {
                        // enableReject(str2);
                    }

                } else {
                    maEntity = parseDataFromLactoScanV2(strData);
                }

            }

        } else if (maName.equalsIgnoreCase("KSHEERAA")) {


            int hashIndex = -1;
            int newLineIndex = -1;
            if (strData.contains("KS")) {
                hashIndex = strData.lastIndexOf("KS");
                if (hashIndex != -1) {
                    newLineIndex = strData.indexOf('\r', hashIndex);
                }
            }
            if (hashIndex != -1 && newLineIndex != -1) {
                strData = strData.substring(hashIndex + 1, newLineIndex);
                maEntity = parseKsheeraaMA(strData);
            }

        } else if (maName.equalsIgnoreCase("EKOMILK EVEN")) {
            if (baos != null && baos.toByteArray().length == 25) {
                maEntity = parseEkomilkEven2MilkAnalyserData(baos.toByteArray());
            } else if (baos != null && baos.toByteArray().length > 25) {
                Util.displayErrorToast("Invalid data, press F10 and reset the MA"
                        , mContext);
            }
        } else if (maName.equalsIgnoreCase("LM2") ||
                maName.equalsIgnoreCase("LACTOPLUS")) {

            CharSequence start = "Milk Analyzer MASTER";
            CharSequence end = "Freezing point";
            strData = new String(baos.toByteArray());
            if (strData.contains(start) && strData.contains(end)) {

                try {
                    maEntity = parseLM2Data(strData);
                    // For Milk Analyzer
                } catch (Exception e) {
                    e.printStackTrace();
                    try {
                        String str = "--------------------------------";
                        Util.generateNoteOnSD("LM2Data", str + "\n" + e.getMessage().toString() + "\n", mContext, "smartAmcuReports");
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    Util.displayErrorToast("Invalid LM2 data, please try again!", mContext);
                }
            }
        } else if (maName.equalsIgnoreCase("EKOMILK_V2")) {

            int amtIdx = -1;
            int dateIdx = strData.indexOf("Date:");
            if (dateIdx != -1)
                amtIdx = strData.indexOf("Amount:", dateIdx);

            if (amtIdx != -1) {
                maEntity = processEKOMILKV2Data(strData);
            }
        } else if (maName.equalsIgnoreCase("LAKTAN_240")) {

            if (baos != null && baos.toByteArray().length == 66) {

                maEntity = parseLaktan240MilkAnalyserData(baos.toByteArray());
                baos.reset();
            } else if (baos != null && baos.toByteArray().length > 66) {
                Util.displayErrorToast("Invalid data, press F10 and reset the MA"
                        , mContext);
            }

        } else {
            Util.displayErrorToast(maName + " is not configured.", mContext);

        }
        return maEntity;
    }

    public MilkAnalyserEntity parseLM2Data(String strData) {

        MilkAnalyserEntity maEntity = new MilkAnalyserEntity();


        int fromIndex, toIndex;
        fromIndex = strData.indexOf("Temp");
        toIndex = strData.indexOf("Fat");
        maEntity.temp = Util.getOnlyDecimalFromStringLm2(strData.substring(fromIndex, toIndex));

        fromIndex = strData.indexOf("Fat");
        toIndex = strData.indexOf("SNF");
        String fat = Util.getOnlyDecimalFromStringLm2(strData.substring(fromIndex, toIndex));

        fromIndex = strData.indexOf("SNF");
        toIndex = strData.indexOf("Density");
        String snf = Util.getOnlyDecimalFromStringLm2(strData.substring(fromIndex, toIndex));

        fromIndex = strData.indexOf("Added water");
        toIndex = strData.indexOf("Freezing point");
        String awm = Util.getOnlyDecimalFromStringLm2(strData.substring(fromIndex, toIndex));


        maEntity.fat = decimalFormatFat.format(Double.valueOf(fat));
        maEntity.snf = decimalFormatSNF.format(Double.valueOf(snf));
        maEntity.addedWater = awm;
        maEntity.clr = Util.getCLR(fat, snf, mContext);

        return maEntity;

    }


    //Parse AkashGanga data

    public MilkAnalyserEntity parseDataFromEcoMilkUltra(String strECOMILKULTRA) {

        //  Util.displayErrorToast("Parsing method called!",mContext);

        MilkAnalyserEntity maEntity = new MilkAnalyserEntity();
        strECOMILKULTRA = strECOMILKULTRA.replace(" ", "");

        String[] str = strECOMILKULTRA.split("(?<=\\G.{4})");

        String fat = strECOMILKULTRA.substring(0, 4);
        String snf = strECOMILKULTRA.substring(4, 8);
        String clr = strECOMILKULTRA.substring(getClrPositionForEkoMilkUltra(), getClrPositionForEkoMilkUltra() + 4);

        maEntity.fat = addDecimalPoint(fat, decimalFormatFat);
        maEntity.snf = addDecimalPoint(snf, decimalFormatSNF);
        maEntity.clr = addDecimalPoint(clr, decimalFormatClr);

        maEntity.addedWater = addDecimalPoint(strECOMILKULTRA.substring(12, 16), new DecimalFormat("##.00"));

        ekomilkUltraMessage = strECOMILKULTRA;

        if (ma1OrMa2 == null || ma1OrMa2.equalsIgnoreCase("MA1")
                || ma1OrMa2.equalsIgnoreCase(DeviceName.MA1)) {
            amcuConfig.setMilkAnalyserPrvData(strECOMILKULTRA);
            amcuConfig.setMA1PrevRecord(strECOMILKULTRA);
        } else if (ma1OrMa2.equalsIgnoreCase("MA2")) {
            amcuConfig.setMA2PrevRecord(strECOMILKULTRA);
        }

        //  Util.displayErrorToast("Fat: "+maEntity.fat+" SNF: "+maEntity.snf,mContext);

        return maEntity;
    }

    public String[] getIndifossArr(String strIndifoss) {

        System.out.println("INDIFOSS StartData: " + strIndifoss);

        String[] strArray;
        if (strIndifoss == null) {
            strArray = null;
        } else if (strIndifoss.contains("\r\n")) {
            strArray = strIndifoss.split("\r\n");
        } else {
            strArray = strIndifoss.split("\n");
        }


        if (strArray != null && strArray.length > 3) {
            String[] strIndifossData = strArray[strArray.length - 2].split(",");

            String[] timeStamp = strIndifossData[0].split(" ");

            if (timeStamp[0].contains("00:00:00")) {

                //This check is for cleaning
                return null;
            }


            if (strIndifossData != null && !amcuConfig.getIndifossTimeStamp().
                    equalsIgnoreCase(strIndifossData[0]) && strIndifossData.length > 3) {
                return strIndifossData;

            } else {

                return null;
            }

        } else {
            return null;
        }

    }

    //Parse lactoscanV2

    public MilkAnalyserEntity parseAkashGangaData(String strAkashGanga) {

        try {
            Util.generateNoteOnSD("MADATA",
                    strAkashGanga + "\n", mContext, "smartAmcuReports");
        } catch (Exception e) {
        }

        MilkAnalyserEntity maEntity = new MilkAnalyserEntity();
        int fromIndex, toIndex;
        fromIndex = strAkashGanga.indexOf("temperature");
        toIndex = strAkashGanga.indexOf("C ");
        String temp = Util.getOnlyDecimalFromString(strAkashGanga.substring(fromIndex, toIndex));

        if (temp != null) {
            temp = temp.replace(" ", "");
        } else {
            temp = "0.0";
        }

        fromIndex = strAkashGanga.indexOf("F=");
        toIndex = strAkashGanga.indexOf("S=");

        String fat = Util.getOnlyDecimalFromString(strAkashGanga.substring(fromIndex, toIndex));

        maEntity.fat = fat;

        fromIndex = strAkashGanga.indexOf("S=");
        toIndex = strAkashGanga.indexOf("CLR =");
        String snf = Util.getOnlyDecimalFromString(strAkashGanga.substring(fromIndex, toIndex));
        maEntity.snf = snf;

        maEntity.clr = Util.getCLR(maEntity.fat, maEntity.snf, mContext);

        fromIndex = strAkashGanga.indexOf("AW=");
        toIndex = strAkashGanga.indexOf("Fp=");

        if (toIndex != -1) {
            String addedWater = Util.getOnlyDecimalFromString(strAkashGanga.substring(fromIndex, toIndex));
            maEntity.addedWater = addedWater;
        } else {
            maEntity.addedWater = "0.00";
        }
        return maEntity;
    }

    //Ksheera milk analyzer

    public MilkAnalyserEntity parseKamdhenuData(String strKamdhenu) {

        MilkAnalyserEntity maEntity = new MilkAnalyserEntity();
        DecimalFormat decimalFormat = new DecimalFormat("#0.0");

        int i;
        try {
            i = strKamdhenu.indexOf("Temp");
            String temp = strKamdhenu.substring(i + 6, i + 11);

            if (temp != null && i != -1) {
                temp = temp.replace(" ", "");
            } else {
                temp = "0.0";
            }

            i = strKamdhenu.indexOf("Fat");
            String fat = strKamdhenu.substring(i + 15, i + 20);
            fat = fat.replace("%", "");
            fat = fat.replaceAll(" ", "");
            maEntity.fat = decimalFormat.format(Double.parseDouble(fat));

            i = strKamdhenu.indexOf("SNF");
            String snf = strKamdhenu.substring(i + 15, i + 20);
            snf = snf.replace("%", "");
            snf = snf.replaceAll(" ", "");
            maEntity.snf = decimalFormat.format(Double.parseDouble(snf));
            maEntity.clr = Util.getCLR(maEntity.fat, maEntity.snf, mContext);


            i = strKamdhenu.indexOf("Added water");
            if (i != -1) {
                String addedWater = strKamdhenu.substring(i + 15, i + 20);
                addedWater = addedWater.replace("%", "");
                addedWater = addedWater.replace(" ", "");
                maEntity.addedWater = decimalFormat.format(Double.parseDouble(addedWater));
            } else {
                maEntity.addedWater = "0.00";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return maEntity;
    }

//Parse data from lactoscan

    public MilkAnalyserEntity parseDataFromLactoScanV2(String str1) {

        MilkAnalyserEntity maEntity = new MilkAnalyserEntity();


        String[] strArr = str1.split(" ");

        if (strArr.length > 1) {

            maEntity.fat = decimalFormatFat.format(Double.parseDouble(strArr[0]));
            maEntity.snf = decimalFormatSNF.format(Double.parseDouble(strArr[1]));

            maEntity.clr = Util.getCLR(maEntity.fat, maEntity.snf, mContext);
            maEntity.addedWater = "0.00";

        }
        return maEntity;
    }

    //parse EkOMILK data

    public MilkAnalyserEntity parseKsheeraaMA(String str1) {

        MilkAnalyserEntity maEntity = new MilkAnalyserEntity();
        str1 = str1.replace("|", "=");
        String[] strArr = str1.split("=");
        if (strArr.length > 5) {
            String fat = strArr[3].replace("F", "");
            String snf = strArr[4].replace("S", "");
            maEntity.fat = decimalFormatFat.format(Double.parseDouble(fat));
            maEntity.snf = decimalFormatSNF.format(Double.parseDouble(snf));
            maEntity.clr = Util.getCLR(maEntity.fat, maEntity.snf, mContext);

            if (strArr.length > 9) {
                String addedWater = strArr[9].replace("W", "");
                addedWater = decimalFormatFS.format(Double
                        .parseDouble(addedWater));
                maEntity.addedWater = addedWater;
                String temp = strArr[10].replace("T", "");
                maEntity.temp = temp;
            } else {
                maEntity.addedWater = "0.00";
            }


        }
        return maEntity;
    }

//Parse data from nuline milk analyzer

    public MilkAnalyserEntity parseDataFromLactoScan(String strLactoscan) {

        MilkAnalyserEntity maEntity = null;

        String[] strArr = strLactoscan.split(" ");

        if (strArr.length > 9) {
            maEntity = new MilkAnalyserEntity();

            maEntity.fat = decimalFormatFat.format(Double.parseDouble(strArr[0]));
            maEntity.snf = decimalFormatSNF.format(Double.parseDouble(strArr[1]));

            if (strArr[2] != null) {
                maEntity.clr = decimalFormatClr.format(Double.parseDouble(strArr[2]));
            } else {
                maEntity.clr = Util.getCLR(maEntity.fat, maEntity.snf, mContext);
            }
            maEntity.lactose = Double.parseDouble(strArr[4]);
            maEntity.protein = Double.parseDouble(strArr[5]);

            if ((amcuConfig.getChillingFATSNFCLR().equalsIgnoreCase("FC")
                    && session.getIsChillingCenter())
                    || (amcuConfig.getCollectionFATSNFCLR().equalsIgnoreCase("FC")
                    && !session.getIsChillingCenter())) {

                maEntity.snf = Util.getSNF(maEntity.fat, maEntity.clr, mContext);
            } else {
                maEntity.clr = Util.getCLR(maEntity.fat, maEntity.snf, mContext);
            }

            if (strArr.length > 8) {
                String addedWater = decimalFormatFS.format(Double
                        .parseDouble(strArr[7]));
                maEntity.addedWater = addedWater;
            } else {
                maEntity.addedWater = "0.00";
            }
        }
        return maEntity;
    }


    //Parse data from indifoss milkanalyser

    public MilkAnalyserEntity processEKOMILKData(String strEkomilk) {

        MilkAnalyserEntity maEntity = new MilkAnalyserEntity();

        String snf, fat, AWM;

        int startIndex = strEkomilk.indexOf("FAT:");
        int endIndex = strEkomilk.indexOf("SNF:");
        fat = Util.getOnlyDecimalFromStringLm2(strEkomilk.substring(startIndex, endIndex));
        startIndex = strEkomilk.indexOf("SNF:");
        endIndex = strEkomilk.indexOf("AWM:");
        snf = Util.getOnlyDecimalFromStringLm2(strEkomilk.substring(startIndex, endIndex));

        startIndex = strEkomilk.indexOf("AWM:");
        endIndex = strEkomilk.indexOf("Rate");

        AWM = Util.getOnlyDecimalFromStringLm2(strEkomilk.substring(startIndex, endIndex));
        snf = snf.replace("%", "");
        fat = fat.replace("%", "");
        AWM = AWM.replace("%", "");

        snf = decimalFormatSNF.format(Double.parseDouble(snf));
        fat = decimalFormatFat.format(Double.parseDouble(fat));
        maEntity.fat = fat;
        maEntity.snf = snf;

        maEntity.clr = Util.getCLR(fat, snf, mContext);

        try {
            String addedWater = decimalFormatFS.format(Double.parseDouble(AWM));
            maEntity.addedWater = addedWater;
        } catch (NumberFormatException e) {
            maEntity.addedWater = "0.00";
            e.printStackTrace();
        }


        try {
            Thread.sleep(1000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }

        return maEntity;

    }


    //This is added for Eko milk even

    public MilkAnalyserEntity parseNulineMilkAnalyserData(byte[] byteNuline) {
        MilkAnalyserEntity maEntity = new MilkAnalyserEntity();
        if ((byteNuline != null && byteNuline.length > 25)) {
            Util.displayErrorToast("Invalid data, press F10 and reset the MA"
                    , mContext);
            return null;
        } else if (byteNuline != null && byteNuline.length == 25) {
            String fat = Util.binaryToDecimal(byteNuline[0], byteNuline[1]);
            maEntity.fat = decimalFormatFat.format(Double.parseDouble(fat));
            String snf = Util.binaryToDecimal(byteNuline[2], byteNuline[3]);
            ;
            maEntity.snf = decimalFormatSNF.format(Double.parseDouble(snf));
            //    afterGettingMaData(etFat.getText().toString(), etSnf.getText().toString());
            String addedWater = Util.binaryToDecimal(byteNuline[6], byteNuline[7]);
            maEntity.addedWater = addedWater;
            maEntity.clr = Util.getCLR(maEntity.fat, maEntity.snf, mContext);
            return maEntity;
        } else {
            return null;
        }

    }

    public MilkAnalyserEntity parseIndifossMilkAnalyserData(String[] strIndifoss) {

        MilkAnalyserEntity maEntity = new MilkAnalyserEntity();
        amcuConfig.setIndifossTimeStamp(strIndifoss[0]);

        String fat = strIndifoss[2];
        String snf = strIndifoss[3];


        if (Util.checkIfWaterCode(session.getFarmerID(), amcuConfig.getFarmerIdDigit())
                || (Double.parseDouble(fat) > 0 ||
                Double.parseDouble(snf) > 0)) {
            maEntity.fat = decimalFormatFat.format(Double.parseDouble(fat));
            maEntity.snf = decimalFormatSNF.format(Double.parseDouble(snf));
            maEntity.clr = Util.getCLR(maEntity.fat, maEntity.snf, mContext);
            maEntity.addedWater = "0.00";
            return maEntity;
        } else {

            Util.displayErrorToast("Invaid data, Press F10 and reset the MA", mContext);
            return null;

        }
    }

    public MilkAnalyserEntity parseEkomilkEven2MilkAnalyserData(byte[] byteNuline) {

        MilkAnalyserEntity maEntity = new MilkAnalyserEntity();
        if (byteNuline != null && byteNuline.length > 8) {
            String fat = Util.binaryToDecimal(byteNuline[0], byteNuline[1]);
            maEntity.fat = decimalFormatFat.format(Double.parseDouble(fat));
            String snf = Util.binaryToDecimal(byteNuline[2], byteNuline[3]);
            maEntity.snf = decimalFormatSNF.format(Double.parseDouble(snf));

            String clr = Util.binaryToDecimal(byteNuline[4], byteNuline[5]);
            maEntity.clr = decimalFormatClr.format(Double.parseDouble(clr));

            if ((amcuConfig.getChillingFATSNFCLR().equalsIgnoreCase("FC")
                    && session.getIsChillingCenter())
                    || (amcuConfig.getCollectionFATSNFCLR().equalsIgnoreCase("FC")
                    && !session.getIsChillingCenter())) {

                maEntity.snf = Util.getSNF(maEntity.fat, maEntity.clr, mContext);
            } else {
                maEntity.clr = Util.getCLR(maEntity.fat, maEntity.snf, mContext);
            }

            String addedWater = Util.binaryToDecimal(byteNuline[6], byteNuline[7]);
            maEntity.addedWater = addedWater;
        }

        return maEntity;

    }

    ////Correction i
    // 155415153315Cal1  0.00  0.00  0.00  0.00  0.00  0.00  0.00  0.00

    public void setFATSNFCLR(MilkAnalyserEntity maEntity, EditText etFat, EditText etSnf, EditText etClr) {

        etFat.setText(maEntity.fat);
        etSnf.setText(maEntity.snf);
        etClr.setText(maEntity.clr);

    }

    public String addDecimalPoint(String str, DecimalFormat decimalFormat) {

        String value;
        String[] str1 = str.split("(?<=\\G.{2})");
        value = str1[0] + "." + str1[1];
        value = decimalFormat.format(Double.parseDouble(value));
        return value;

    }

    public MilkAnalyserEntity parseLactoScanCalibrationAndCorrection(String strData) {

        Util.displayErrorToast(strData, mContext);
        MilkAnalyserEntity maEntity = new MilkAnalyserEntity();
        String[] strArray = strData.split("  ");
        String[] str = strArray[0].split("(?<=\\G.{6})");

        maEntity.date = getDate(str[0]);
        maEntity.time = getTime(str[1]);
        maEntity.calibration = strArray[0].substring(12, strArray[0].length());

        if (strArray.length > 6) {
            maEntity.fat = decimalFormatFat.format(Double.parseDouble(strArray[1]));
            maEntity.snf = decimalFormatSNF.format(Double.parseDouble(strArray[2]));
            maEntity.clr = decimalFormatClr.format(Double.parseDouble(strArray[3]));
            maEntity.addedWater = strArray[6];
            maEntity.temp = strArray[7];
        }

        return maEntity;

    }

    //For new Ekomilk version V2
    public MilkAnalyserEntity processEKOMILKV2Data(String strEkomilk) {

        MilkAnalyserEntity maEntity = new MilkAnalyserEntity();
        String snf, fat;
        int startIndex = strEkomilk.indexOf("FAT:");
        int endIndex = strEkomilk.indexOf("SNF:");
        fat = Util.getOnlyDecimalFromStringLm2(strEkomilk.substring(startIndex, endIndex));
        startIndex = strEkomilk.indexOf("SNF:");
        endIndex = strEkomilk.indexOf("Rate");
        snf = Util.getOnlyDecimalFromStringLm2(strEkomilk.substring(startIndex, endIndex));


        snf = snf.replace("%", "");
        fat = fat.replace("%", "");

        snf = decimalFormatSNF.format(Double.parseDouble(snf));
        fat = decimalFormatFat.format(Double.parseDouble(fat));
        maEntity.fat = fat;
        maEntity.snf = snf;

        maEntity.clr = Util.getCLR(fat, snf, mContext);

        maEntity.addedWater = "0.00";

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }

        return maEntity;

    }

    public String getDate(String date) {
        return date.substring(4, 6) + "-" + date.substring(2, 4) + "-" + 20 + date.substring(0, 2);
    }

    public String getTime(String time) {
        String[] str = time.split("(?<=\\G.{2})");
        return time.substring(4, 6) + ":" + time.substring(2, 4) + ":" + time.substring(0, 2);
    }

    public int getClrPositionForEkoMilkUltra()

    {
        int clrStartPos = 4 * (saveSession.getClrPosition() - 1);
        if (saveSession.getAddedWaterPos() < saveSession.getClrPosition()) {
            clrStartPos = clrStartPos + 1;
        }

        return clrStartPos;
    }

    public MilkAnalyserEntity parseLaktan240MilkAnalyserData(byte[] byteLaktan) {
        MilkAnalyserEntity maEntity = new MilkAnalyserEntity();
        String[] dataString = new String[byteLaktan.length];

        //  writeMADebugFile(byteLaktan,mContext);

        for (int i = 0; i < byteLaktan.length; i++) {
            dataString[i] = String.format("%02X ", byteLaktan[i]).trim();
        }

        if ((byteLaktan != null && byteLaktan.length > 66)) {
            Util.displayErrorToast("Invalid data, press F10 and reset the MA"
                    , mContext);
            return null;
        } else if (byteLaktan != null && byteLaktan.length == 66) {

            String fat = dataString[6] + dataString[5] + dataString[4] + dataString[3];
            String snf = dataString[10] + dataString[9] + dataString[8] + dataString[7];
            String density = dataString[36] + dataString[35] + dataString[34] + dataString[33];

            SmartCCUtil smartCCUtil = new SmartCCUtil(mContext);
            maEntity.fat = decimalFormatFat.format(smartCCUtil.convertHexToDecimal(fat));
            maEntity.snf = decimalFormatSNF.format(smartCCUtil.convertHexToDecimal(snf));
            int addedWater = smartCCUtil.convertHexToInt(dataString[31]);
            maEntity.addedWater = decimalFormatFS.format(Double.valueOf(addedWater));

            maEntity.clr = Util.getCLR(maEntity.fat, maEntity.snf, mContext);
            return maEntity;
        } else {
            return null;
        }

    }


*/
}
