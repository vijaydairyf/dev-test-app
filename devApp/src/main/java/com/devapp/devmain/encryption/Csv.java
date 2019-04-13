package com.devapp.devmain.encryption;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.devapp.devmain.devicemanager.DatabaseManager;
import com.devapp.devmain.entity.BonusChartEntity;
import com.devapp.devmain.entity.Entity;
import com.devapp.devmain.entity.IncentiveRateChartPostEntity;
import com.devapp.devmain.entity.RateChartEntity;
import com.devapp.devmain.entity.RateChartPostEntity;
import com.devapp.devmain.entity.RatechartDetailsEnt;
import com.devapp.devmain.entity.ReportEntity;
import com.devapp.devmain.entitymanager.IncentiveRateChartManager;
import com.devapp.devmain.entitymanager.RateChartManager;
import com.devapp.devmain.httptasks.PostCollectionRecordsService;
import com.devapp.devmain.postentities.ConsolidatedPostData;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.server.SessionManager;
import com.devapp.devmain.user.Util;
import com.devapp.devmain.util.ValidationHelper;
import com.devapp.smartcc.entityandconstants.SmartCCConstants;
import com.devapp.smartcc.entityandconstants.SmartCCUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static com.devapp.smartcc.entityandconstants.SmartCCConstants.ESSAE;

public class Csv {

    private static String seedValue = "26eg1bca8d34795f";
    private static String saltValue = "123456789abcdefg";

    private final int checkNumber;
    //    public String strStartDate, strEndDate, todayDate;
//    public Long lnStartDate, lnEndDate, lnToday;
    AmcuConfig amcuConfig;
    SessionManager session;
    private Context mContext;

    public Csv(Context ctx, int checkNum) {
        this.mContext = ctx;
        amcuConfig = AmcuConfig.getInstance();
        session = new SessionManager(ctx);
        this.checkNumber = checkNum;
//        todayDate = Util.getTodayDateAndTime(7, mContext, false);
//
//        lnToday = Util.getLong(todayDate);
    }

    public static String DecryptS(String result) {
        //   byte[] data = this.readFromFile(fileName + ".txt");
        byte[] data = result.getBytes();
        String decryptedData = null;
        try {
            decryptedData = com.devapp.devmain.encryption.AESHelper.getInstance(saltValue.getBytes())
                    .decrypt(seedValue, new String(data));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return decryptedData;
    }

    // Sample csvString

    // Sample for reportCSVEntity
    private ArrayList<com.devapp.devmain.encryption.ReportsCSVEntity> createCsvEntityReportCSV(
            ArrayList<ReportEntity> allRepEntity, int check) {

        ArrayList<com.devapp.devmain.encryption.ReportsCSVEntity> list;

        list = new ArrayList<com.devapp.devmain.encryption.ReportsCSVEntity>();

        for (int i = 0; i < allRepEntity.size(); i++) {
            com.devapp.devmain.encryption.ReportsCSVEntity rce = new com.devapp.devmain.encryption.ReportsCSVEntity();

            rce.setAmount(String.valueOf(allRepEntity.get(i).amount));
            rce.setAWM(String.valueOf(allRepEntity.get(i).awm));
            rce.setTime(allRepEntity.get(i).time);
            rce.setFarmerID(allRepEntity.get(i).farmerId);
            rce.setName(allRepEntity.get(i).farmerName);
            rce.setFat(String.valueOf(allRepEntity.get(i).fat));
            rce.setMilkType(allRepEntity.get(i).milkType);
            rce.setMode(allRepEntity.get(i).manual);
            rce.setQuantity(String.valueOf(allRepEntity.get(i).quantity));
            rce.setRate(String.valueOf(allRepEntity.get(i).rate));
            rce.setSnf(String.valueOf(allRepEntity.get(i).snf));
            rce.setStatus(allRepEntity.get(i).status);
            rce.setTemp(String.valueOf(allRepEntity.get(i).temp));

            list.add(rce);
        }

        return list;
    }

    private String createCSVStringReportsCSV(ArrayList<com.devapp.devmain.encryption.ReportsCSVEntity> list) {

        com.devapp.devmain.encryption.CSVHelper<?> helper = new CSVHelper<com.devapp.devmain.encryption.ReportsCSVEntity>(
                com.devapp.devmain.encryption.ReportsCSVEntity.class);
        String result = helper.stringify(list);

        return result;

    }

    // csvString Report Header
    private String createCSVStringReportsHeader(
            ArrayList<com.devapp.devmain.encryption.CollectionEntityHeader> list) {

        com.devapp.devmain.encryption.CSVHelper<?> helper = new CSVHelper<com.devapp.devmain.encryption.CollectionEntityHeader>(
                com.devapp.devmain.encryption.CollectionEntityHeader.class);
        String result = helper.stringify(list);
        return result;

    }

    //Create CSV String

    // csvString report Body
    private String createCSVStringReportsBody(
            ArrayList<com.devapp.devmain.encryption.CollectionRecordBody> list) {

        com.devapp.devmain.encryption.CSVHelper<?> helper = new CSVHelper<com.devapp.devmain.encryption.CollectionRecordBody>(
                com.devapp.devmain.encryption.CollectionRecordBody.class);
        String result = helper.stringify(list);
        return result;

    }

    private String createCSVString(String input) {
        ArrayList<com.devapp.devmain.encryption.GenerateKEY> arrayListGenKey = new ArrayList<com.devapp.devmain.encryption.GenerateKEY>();
        com.devapp.devmain.encryption.GenerateKEY genKey = new com.devapp.devmain.encryption.GenerateKEY();
        genKey.setKey(input);
        arrayListGenKey.add(genKey);
        com.devapp.devmain.encryption.CSVHelper<?> helper = new CSVHelper<com.devapp.devmain.encryption.GenerateKEY>(com.devapp.devmain.encryption.GenerateKEY.class);
        String result = helper.stringify(arrayListGenKey);

        return result;
    }

    // csvString Rate header
    private String createCSVStringRateHeader(
            ArrayList<com.devapp.devmain.encryption.RateChartEntityHeader> list) {

        com.devapp.devmain.encryption.CSVHelper<?> helper = new CSVHelper<com.devapp.devmain.encryption.RateChartEntityHeader>(
                com.devapp.devmain.encryption.RateChartEntityHeader.class);
        String result = helper.stringify(list);
        return result;

    }

    // csvString Rate Body
    private String createCSVStringRateBody(ArrayList<com.devapp.devmain.encryption.RateChartEntityBody> list) {

        com.devapp.devmain.encryption.CSVHelper<?> helper = new CSVHelper<com.devapp.devmain.encryption.RateChartEntityBody>(
                com.devapp.devmain.encryption.RateChartEntityBody.class);
        String result = helper.stringify(list);
        return result;

    }

    // csvString to report header
    private ArrayList<com.devapp.devmain.encryption.CollectionEntityHeader> str2ReportHeader(String str) {

        CSVHelper<com.devapp.devmain.encryption.CollectionEntityHeader> helper = new CSVHelper<com.devapp.devmain.encryption.CollectionEntityHeader>(
                com.devapp.devmain.encryption.CollectionEntityHeader.class);

        ArrayList<com.devapp.devmain.encryption.CollectionEntityHeader> list = (ArrayList<com.devapp.devmain.encryption.CollectionEntityHeader>) helper
                .parse(str, new com.devapp.devmain.encryption.CollectionEntityHeader().getColumnMap());

        return list;
    }

    // csvString to report body
    private ArrayList<com.devapp.devmain.encryption.CollectionRecordBody> str2ReportBody(String str) {

        CSVHelper<com.devapp.devmain.encryption.CollectionRecordBody> helper = new CSVHelper<com.devapp.devmain.encryption.CollectionRecordBody>(
                com.devapp.devmain.encryption.CollectionRecordBody.class);
        ArrayList<com.devapp.devmain.encryption.CollectionRecordBody> list = (ArrayList<com.devapp.devmain.encryption.CollectionRecordBody>) helper
                .parse(str, new com.devapp.devmain.encryption.CollectionRecordBody().getColumnMap());

        return list;
    }

    public void getEncryptedDataOfRateChart(String fileName) {
        byte[] data = this.readFromFile(fileName + ".txt");

        String encryptedData = null;
        try {
            encryptedData = com.devapp.devmain.encryption.AESHelper.getInstance(saltValue.getBytes())
                    .encrypt(seedValue, new String(data));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // csvString to report header
    private ArrayList<com.devapp.devmain.encryption.RateChartEntityHeader> str2RateHeader(String str) {

        CSVHelper<com.devapp.devmain.encryption.RateChartEntityHeader> helper = new CSVHelper<com.devapp.devmain.encryption.RateChartEntityHeader>(
                com.devapp.devmain.encryption.RateChartEntityHeader.class);
        ArrayList<com.devapp.devmain.encryption.RateChartEntityHeader> list = (ArrayList<com.devapp.devmain.encryption.RateChartEntityHeader>) helper
                .parse(str, new com.devapp.devmain.encryption.RateChartEntityHeader().getColumnMap());

        return list;
    }

    // csvString to rate body
    private ArrayList<com.devapp.devmain.encryption.RateChartEntityBody> str2RateBody(String str) {

        CSVHelper<com.devapp.devmain.encryption.RateChartEntityBody> helper = new CSVHelper<com.devapp.devmain.encryption.RateChartEntityBody>(
                com.devapp.devmain.encryption.RateChartEntityBody.class);
        ArrayList<com.devapp.devmain.encryption.RateChartEntityBody> list = (ArrayList<com.devapp.devmain.encryption.RateChartEntityBody>) helper
                .parse(str, new com.devapp.devmain.encryption.RateChartEntityBody().getColumnMap());

        return list;
    }

 /*   public void generateCsvEncryptedFormat(
            ArrayList<ReportEntity> allReportsEnt, String fileName)
            throws UnsupportedEncodingException {

        if (allReportsEnt == null || allReportsEnt.size() < 1) {

            allReportsEnt = new ArrayList<ReportEntity>();
            ReportEntity repEnt = new ReportEntity();

            repEnt.amount = "0.0";
            repEnt.awm = "100.0";
            repEnt.clr = "25.0";
            repEnt.date = "01-01-2015";
            repEnt.farmerId = "0000";
            repEnt.farmerName = "Default";
            repEnt.fat = "0.0";
            repEnt.manual = "Manual";
            repEnt.milkType = "COW";
            repEnt.quantity = "0.0";
            repEnt.rate = "0.0";
            repEnt.shift = "E";
            repEnt.snf = "0.0";
            repEnt.socId = "1234";
            repEnt.status = "Success";
            repEnt.temp = "0.0";
            repEnt.time = "12.00";
            allReportsEnt.add(repEnt);

        }

        ArrayList<com.stellapps.smartamcu.encryption.ReportsCSVEntity> allReportsCsv = createCsvEntityReportCSV(
                allReportsEnt, 0);
        String result = null;
        try {
            result = this.createCSVStringReportsCSV(allReportsCsv);
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        try {
            WriteToFile(fileName + ".txt", result.getBytes("UTF8"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] data = this.readFromFile(fileName + ".txt");
        String encryptedData = null;
        try {
            encryptedData = com.stellapps.smartamcu.encryption.AESHelper.getInstance(saltValue.getBytes())
                    .encrypt(seedValue, new String(data));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        fileName = fileName + "enc";
        amcuConfig.setEncryptedReportFile(fileName);

        try {
            WriteToFile(fileName + ".txt", encryptedData.getBytes("UTF8"));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }*/

    private ArrayList<com.devapp.devmain.encryption.ReportsCSVEntity> str2ReportObject(String str) {
        CSVHelper<com.devapp.devmain.encryption.ReportsCSVEntity> helper = new CSVHelper<com.devapp.devmain.encryption.ReportsCSVEntity>(
                com.devapp.devmain.encryption.ReportsCSVEntity.class);

        // passing columnMap this way is not correct..
        ArrayList<com.devapp.devmain.encryption.ReportsCSVEntity> list = (ArrayList<com.devapp.devmain.encryption.ReportsCSVEntity>) helper
                .parse(str, new com.devapp.devmain.encryption.ReportsCSVEntity().getColumnMap());

        return list;
    }

    // Generate csv rate body

    // Generate csv collection header
    public void generateCsvCollectionHeader(ArrayList<ReportEntity> allReportsEnt,
                                            String fileName, int collectionType, String socId) throws UnsupportedEncodingException {


        ArrayList<com.devapp.devmain.encryption.CollectionRecordBody> allCollectionBody = new ArrayList<com.devapp.devmain.encryption.CollectionRecordBody>();
        if (allReportsEnt != null && allReportsEnt.size() > 0) {
            if (collectionType == 10) {
                //For periodic reports
                for (int i = 1; i < allReportsEnt.size(); i++) {
                    com.devapp.devmain.encryption.CollectionRecordBody crb = getCollectionBodyRecord(allReportsEnt.get(i), socId);
                    allCollectionBody.add(crb);
                }
            } else {
                for (int i = 0; i < allReportsEnt.size(); i++) {
                    com.devapp.devmain.encryption.CollectionRecordBody crb = getCollectionBodyRecord(allReportsEnt.get(i), socId);
                    allCollectionBody.add(crb);
                }
            }
        } else {
            Util.displayErrorToast("o collection data to import!", mContext);
            return;
        }

        if (allReportsEnt != null && allReportsEnt.size() > 0) {
            String resultHeader = null;
            String resultBody = null, result = null;
            try {
                //     resultHeader = this.createCSVStringReportsHeader(allCollectionBody);
                resultBody = this.createCSVStringReportsBody(allCollectionBody);
                //   result = resultHeader + resultBody;
                result = resultBody;
            } catch (Exception e1) {
                e1.printStackTrace();
            }

            try {
                //To generate normal file
                WriteToFile(fileName, result.getBytes("UTF8"));
                generateEncryptedFile(fileName, result);
            }
//            catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            catch (NullPointerException ne) {
                ne.printStackTrace();
            }

        }

    }

    public void generateCsvRateBody(
            ArrayList<com.devapp.devmain.encryption.RateChartEntityHeader> allRateHeader,
            ArrayList<com.devapp.devmain.encryption.RateChartEntityBody> allRateEntBody, String fileName)
            throws UnsupportedEncodingException {

        String rateHeader = null, rateBody = null, result = null;
        try {
            rateBody = this.createCSVStringRateBody(allRateEntBody);
            rateHeader = this.createCSVStringRateHeader(allRateHeader);
            result = rateHeader + rateBody;
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        try {
            WriteToFile(fileName + ".txt", result.getBytes("UTF8"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getEncryptedKEY(String result) {

        String encryptedData = null;
        System.out.println("Normal: " + result);
        try {
            byte[] data = result.getBytes();
            encryptedData = com.devapp.devmain.encryption.AESHelper.getInstance(saltValue.getBytes())
                    .encrypt(seedValue, new String(data));
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Encrypted: " + encryptedData);
        return encryptedData;

    }

    public void generateEncryptedFile(String fileName, String result) {
        //   byte[] data = this.readFromFile(fileName + ".txt");
        byte[] data = result.getBytes();
        String encryptedData = null;
        try {
            encryptedData = com.devapp.devmain.encryption.AESHelper.getInstance(saltValue.getBytes())
                    .encrypt(seedValue, new String(data));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        fileName = fileName.replaceAll(".txt", "");
        //  fileName = fileName + "enc";
        amcuConfig.setEncryptedReportFile(fileName);
        try {
            WriteToFile(fileName, encryptedData.getBytes("UTF8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getDcryptedKEY(String value) {
        String decryptedData = null;
        System.out.println("Normal value: " + value);
        try {

            com.devapp.devmain.encryption.AESHelper ase = com.devapp.devmain.encryption.AESHelper.getInstance(saltValue.getBytes());
            decryptedData = ase.decrypt(seedValue, value);
        } catch (Exception e) {

        }

        System.out.println("Decrypted value: " + value);
        return decryptedData;
    }

    public void generateCsvDcryptFormat(String fileName) {
        String decryptedData = null;

        try {

            com.devapp.devmain.encryption.AESHelper ase = com.devapp.devmain.encryption.AESHelper.getInstance(saltValue.getBytes());
            String str = readFromFileString(fileName);
            decryptedData = ase.decrypt(seedValue, str);

            if (checkNumber == Util.GETRATECHART) {
                getGeneratedRateEntityBody(decryptedData);

            } else if (checkNumber == Util.GETREPORT) {
                getGenerateReport(decryptedData);
            } else if (checkNumber == Util.INCENTIVE_RATE_CHART) {
                getIncentiveRateChart(decryptedData);
            } else {
                Toast.makeText(mContext, "Invalid data!", Toast.LENGTH_SHORT)
                        .show();
            }

        } catch (NullPointerException e2) {
            e2.printStackTrace();
            Toast.makeText(mContext, "No value found", Toast.LENGTH_SHORT)
                    .show();
        } catch (Exception e1) {
            Toast.makeText(mContext, "Invalid format!", Toast.LENGTH_SHORT)
                    .show();
            e1.printStackTrace();
        }
        // convert the decrypted data into employee objects

    }

    public void WriteToFile(String fname, byte[] bytes) {

        FileOutputStream fOut = null;
        ObjectOutputStream osw = null;

        String read = new String(bytes);

        // String filename = mContext.getFilesDir().toString() + fName +
        // ".ratemap";

        try {
            fOut = new FileOutputStream(new File(fname));
            fOut.write(bytes);
            fOut.getFD().sync();
//            osw = new ObjectOutputStream(fOut);
//
//            osw.writeObject(read);
//            osw.flush();
            fOut.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fOut != null) {
                    fOut.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public byte[] readFromFile(String fName) {
        FileInputStream fIn = null;
        ObjectInputStream isr = null;
        byte[] bytes = null;

        String _list = null;

        // String filename = ctx.getFilesDir().toString() + "" + userID
        // + ".products";
        File f = new File(fName);
        if (!f.exists()) {
            return null;
        } else {
            try {
                fIn = new FileInputStream(fName);
                isr = new ObjectInputStream(fIn);

                _list = isr.readObject().toString();

                bytes = _list.getBytes();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    fIn.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return bytes;
        }

    }

    // Create collection report object

    public ArrayList<com.devapp.devmain.encryption.ReportsCSVEntity> getReportObject(String str) {
        String[] strArray = str.split("/n");

        ArrayList<com.devapp.devmain.encryption.ReportsCSVEntity> allReportsCsvEnt = new ArrayList<com.devapp.devmain.encryption.ReportsCSVEntity>();
        try {
            for (int i = 1; i < strArray.length; i++) {
                String[] strArray2 = strArray[i].split(",");

                com.devapp.devmain.encryption.ReportsCSVEntity rce = new com.devapp.devmain.encryption.ReportsCSVEntity();
                rce.farmerId = strArray2[0];
                rce.farmerName = strArray2[1];
                rce.time = strArray2[2];
                rce.milkType = strArray2[3];
                rce.fat = strArray2[4];
                rce.snf = strArray2[5];
                rce.awm = strArray2[6];
                rce.Temp = strArray2[7];
                rce.Quantity = strArray2[8];
                rce.rate = strArray2[9];
                rce.amount = strArray2[10];
                rce.manual = strArray2[11];
                rce.status = strArray2[12];
                allReportsCsvEnt.add(rce);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return allReportsCsvEnt;

    }

    public ArrayList<com.devapp.devmain.encryption.CollectionEntityHeader> getGenerateReport(String str) {
        String[] strArray = str.split("\n");

        ArrayList<com.devapp.devmain.encryption.CollectionEntityHeader> allReportsHeader = new ArrayList<com.devapp.devmain.encryption.CollectionEntityHeader>();
        ArrayList<com.devapp.devmain.encryption.CollectionRecordBody> allCollectionReportsBody = new ArrayList<com.devapp.devmain.encryption.CollectionRecordBody>();
        try {

            String[] strArray2 = strArray[1].split(",");
            com.devapp.devmain.encryption.CollectionEntityHeader rce = new com.devapp.devmain.encryption.CollectionEntityHeader();
            rce.societyId = strArray2[0];
            rce.shift = strArray2[1];
            rce.collectionDate = strArray2[2];
            rce.endShift = strArray2[5];
            rce.startTime = Long.parseLong(strArray2[3]);
            rce.endTime = Long.parseLong(strArray[4]);

            allReportsHeader.add(rce);

            for (int i = 3; i < strArray.length; i++) {
                String[] strArray3 = strArray[i].split(",");

                com.devapp.devmain.encryption.CollectionRecordBody crb = new com.devapp.devmain.encryption.CollectionRecordBody();
                crb.farmerId = strArray3[0];
                crb.milkType = strArray3[2];
                crb.milkQuantity = Double.parseDouble(strArray3[3]);
                crb.fat = Double.parseDouble(strArray3[4]);
                crb.snf = Double.parseDouble(strArray3[5]);
                crb.amount = Double.parseDouble(strArray3[6]);
                crb.rate = Double.parseDouble(strArray3[7]);
                crb.mode = strArray3[8];
                crb.awm = Double.parseDouble(strArray3[9]);
                crb.status = strArray3[10];
                crb.collectionTime = Long.parseLong(strArray3[1]);
                allCollectionReportsBody.add(crb);
            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return allReportsHeader;

    }

    public ArrayList<com.devapp.devmain.encryption.RateChartEntityBody> getGeneratedRateEntityBody(String str) {
        String[] rateChartrray = str.split("\n");

        String[] rateDetailsArray = rateChartrray[1].split(",");

        ArrayList<com.devapp.devmain.encryption.RateChartEntityBody> allRatechartEnt = new ArrayList<com.devapp.devmain.encryption.RateChartEntityBody>();
        ArrayList<RateChartEntity> allRateEntity = new ArrayList<RateChartEntity>();

        DecimalFormat decimalFormatFS = new DecimalFormat("#0.0");
        DecimalFormat decimalFormatAWM = new DecimalFormat("#0.00");
        SmartCCUtil smartCCUtil = new SmartCCUtil(mContext);

        RatechartDetailsEnt rde = new RatechartDetailsEnt();

        try {
            com.devapp.devmain.encryption.RateChartEntityHeader reh = new com.devapp.devmain.encryption.RateChartEntityHeader();

            reh.name = rateDetailsArray[0].toUpperCase();
            reh.milkType = rateDetailsArray[1];
            reh.isActive = rateDetailsArray[2];
            reh.validityStartDate = rateDetailsArray[3];
            reh.validityEndDate = rateDetailsArray[4];
            reh.ratechartType = rateDetailsArray[5];
            if (rateDetailsArray.length > 6)
                reh.ratechartShift = rateDetailsArray[6];

            if (reh.ratechartType == null) {
                reh.ratechartType = Util.RATECHART_TYPE_COLLECTION;
            }

            // Rate chart details
            rde.rateChartName = reh.name.toUpperCase(Locale.ENGLISH);
            rde.rateValidityFrom = smartCCUtil.getDateFormatFromMillis("dd-MM-yyyy"
                    , Long.parseLong(reh.validityStartDate));
            rde.rateValidityTo = smartCCUtil.getDateFormatFromMillis("dd-MM-yyyy"
                    , Long.parseLong(reh.validityEndDate));
            rde.rateSocId = String.valueOf(session.getSocietyColumnId());
            rde.rateLvalidityFrom = Long.parseLong(reh.validityStartDate);
            rde.ratechartType = reh.ratechartType;
            rde.ratechartShift = reh.ratechartShift;
//
//
//            if (lnEndDate > 0) {
//                rde.rateLvalidityTo = Util.getLongTime(lnEndDate);
//            } else {
//                rde.rateLvalidityTo = lnEndDate;
//            }
            rde.rateLvalidityTo = Long.parseLong(reh.validityEndDate);
            rde.isActive = reh.isActive;
            rde.rateMilkType = reh.milkType.toUpperCase(Locale.ENGLISH);
            rde.rateOther1 = String.valueOf(Util.getLongDateAndTime());

            for (int i = 3; i < rateChartrray.length; i++) {

                String[] rateBodyArray = rateChartrray[i].split(",");
                com.devapp.devmain.encryption.RateChartEntityBody reb = new com.devapp.devmain.encryption.RateChartEntityBody();
                reb.fat = Double.parseDouble(decimalFormatFS.format(Double
                        .parseDouble(rateBodyArray[0].replace(" ", ""))));
                reb.snf = Double.parseDouble(decimalFormatFS.format(Double
                        .parseDouble(rateBodyArray[1].replace(" ", ""))));
                reb.rate = Double.parseDouble(decimalFormatAWM.format(Double
                        .parseDouble(rateBodyArray[2].replace(" ", ""))));
                allRatechartEnt.add(reb);

                RateChartEntity rateEnt = new RateChartEntity();

                rateEnt.endDate = rde.rateValidityTo;
                rateEnt.startDate = rde.rateValidityFrom;
                rateEnt.shift = rde.ratechartShift;

                rateEnt.milkType = rde.rateMilkType.toUpperCase(Locale.ENGLISH);
                rateEnt.rateChartName = rde.rateChartName;
                rateEnt.societyId = String
                        .valueOf(session.getSocietyColumnId());

                rateEnt.fat = Double.parseDouble(decimalFormatFS.format(Double
                        .parseDouble(rateBodyArray[0].replace(" ", ""))));
                rateEnt.snf = Double.parseDouble(decimalFormatFS.format(Double
                        .parseDouble(rateBodyArray[1].replace(" ", ""))));
                rateEnt.rate = Double.parseDouble(decimalFormatAWM
                        .format(Double.parseDouble(rateBodyArray[2]
                                .replace(" ", ""))));

                allRateEntity.add(rateEnt);

            }
        } catch (NumberFormatException e) {
            Toast.makeText(mContext, "Please check the format!",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (Exception E) {
            Toast.makeText(mContext, "Invalid file!", Toast.LENGTH_LONG).show();
            E.printStackTrace();
        }

        if (rde != null && allRateEntity.size() > 0) {
            updateRateChart(rde, allRateEntity);
        }

        return allRatechartEnt;
    }

    public void updateRateChart(RatechartDetailsEnt rcd,
                                ArrayList<RateChartEntity> allRce) {
        Util.displayErrorToast("Rate chart update in progress.", mContext);

        DatabaseManager rcm = new DatabaseManager(mContext);
        ArrayList<RateChartPostEntity> allPostEnt = new ArrayList<>();
        RateChartPostEntity rateChartPostEntity = new RateChartPostEntity();
        rateChartPostEntity.setRateChartEntity(rcd, allRce);
        allPostEnt.add(rateChartPostEntity);
        RateChartManager rateChartManager = new RateChartManager(mContext);
        try {
            rateChartManager.saveAll((List<? extends Entity>) allPostEnt, false);
            rcm.manageRateChart();
            Util.displayErrorToast("Rate chart updated successfully.", mContext);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String readFromFileString(String fname) {

        String ret = "";

        try {
            InputStream inputStream = new FileInputStream(fname);

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(
                        inputStream);
                BufferedReader bufferedReader = new BufferedReader(
                        inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();
                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString + "\n");
                }
                inputStream.close();
                ret = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            Log.e("CSV", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("CSV", "Can not read file: " + e.toString());
        }
        return ret;
    }

    public com.devapp.devmain.encryption.CollectionRecordBody getCollectionBodyRecord(ReportEntity reportEntity, String socId) {

        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
        DecimalFormat decimalFormatFS = new DecimalFormat("#0.0");
        ValidationHelper validationHelper = new ValidationHelper();

        com.devapp.devmain.encryption.CollectionRecordBody crb = new com.devapp.devmain.encryption.CollectionRecordBody();

        try {
            crb.setAmount(Double.parseDouble(decimalFormat.format(reportEntity.amount)));
            crb.setRate(Double.parseDouble(decimalFormat.format(reportEntity.rate)));
            crb.setMilkQuantity(Double.parseDouble(decimalFormat.format(reportEntity.quantity)));
            crb.setAwm(Double.parseDouble(decimalFormatFS.format(reportEntity.awm)));
            crb.setFat(Double.parseDouble(decimalFormatFS.format(reportEntity.fat)));
            crb.setSnf(Double.parseDouble(decimalFormatFS.format(reportEntity.snf)));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        crb.setCollectionTime(reportEntity.miliTime);
        crb.setFarmerId(reportEntity.farmerId);


        crb.setMilkType(reportEntity.milkType);
        crb.setMode(reportEntity.manual);

        crb.setStatus(reportEntity.status);
        crb.setShift(SmartCCUtil.getAlternateShift(reportEntity.postShift));

        crb.setSocietyId(socId);
        crb.setDate(reportEntity.postDate);
        String temp = String.valueOf(reportEntity.temp);

        crb.setTemp(temp);
        crb.setFarmerName(reportEntity.farmerName);

        try {
            crb.setBonus(Double.parseDouble(decimalFormat.format(reportEntity.bonus)));
        } catch (Exception e) {
            e.printStackTrace();
            crb.setBonus(0.00);
        }
        crb.setQualityAndQuantityMode(reportEntity.qualityMode, reportEntity.quantityMode);
        crb.setCollectioinType(reportEntity.collectionType);

        //Adding rateMode and milkQuality as per Milma requirement

        crb.setRateMode(reportEntity.rateMode);
        crb.setMilkQuality(reportEntity.milkQuality);

        //Added kgQty and ltr Qty also to the encrypted file

        crb.setKgQuantity(String.valueOf(reportEntity.kgWeight));
        crb.setLtrQuantity(String.valueOf(reportEntity.ltrsWeight));

        int numOfCans = 0;

        try {
            numOfCans = reportEntity.numberOfCans;
        } catch (Exception e) {
            e.printStackTrace();
        }

        crb.setRouteMilkStatusAndCans(reportEntity.centerRoute,
                reportEntity.milkStatusCode,
                numOfCans);

        crb.setTippingTime(reportEntity.tippingStartTime,
                reportEntity.tippingEndTime);

        crb.setUserAndAgent(reportEntity.user, reportEntity.agentId);

        crb.setSampleNumber(reportEntity.sampleNumber);

        if (reportEntity.rateCalculation == 1) {
            crb.setRateCalculatedFromDevice(true);
        } else {
            crb.setRateCalculatedFromDevice(false);
        }

        crb.setMaSerialNumber(reportEntity.serialMa);
        crb.setMilkAnalyserName(reportEntity.maName);

        if (reportEntity.oldOrNewFlag != null) {
            crb.setOldorNew(reportEntity.oldOrNewFlag);
            crb.setSequenceNumber((int) reportEntity.columnId);
        } else {
            crb.setOldorNew(SmartCCConstants.KEY_REAL_VALUE);
            try {
                int sequenceNumber = reportEntity.sampleNumber;
                crb.setSequenceNumber(sequenceNumber);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        crb.setEditedTime(reportEntity.editedTime);

        crb.setClr(reportEntity.clr);

        return crb;
    }

    public void generateCSVForConsolidatedReport(ArrayList<ConsolidatedPostData> allPostEndShift,
                                                 String fileName) {
        // String strConsolidated = PostCollectionRecordsService.toJson(allPostEndShift);
        //  System.out.println(strConsolidated);


        ConsolidatedData consolidatedData = new ConsolidatedData();
        consolidatedData.csvVersion = "3.0";
        consolidatedData.records = allPostEndShift;


        String strConsolidated = PostCollectionRecordsService.toJson(consolidatedData);
        // System.out.println(strConsolidated);
        // strConsolidated = strConsolidated.replace(String.valueOf("\"\""),String.valueOf("\""));


        String resultBody = null, result = null;
        try {
            // resultHeader = this.createCSVStringReportsHeader(allReportsCsv);
//            ConsolidatedReport consolidatedReport = getConsolidatedReport(consolidatedData);
//            ArrayList<ConsolidatedReport> allConsolidateReport = new ArrayList<>();
//            allConsolidateReport.add(consolidatedReport);
//            resultBody = this.createCSVStringForPostEndShift(allConsolidateReport);


            com.devapp.devmain.encryption.CSVHelper<?> helper = new CSVHelper<com.devapp.devmain.encryption.ConsolidatedReport>(
                    com.devapp.devmain.encryption.ConsolidatedReport.class);
            resultBody = helper.stringifyString(strConsolidated);
            resultBody = resultBody.replace("\"\"", "\"");


            //  resultBody = this.createCSVString(strConsolidated);
            //  result = resultHeader + resultBody;
            result = resultBody;
            //To write normal file
            WriteToFile(fileName, result.getBytes("UTF8"));
            //To write encrypted file
            generateEncryptedFile(fileName, result);
        } catch (Exception e1) {
            e1.printStackTrace();
        }


    }

    public String generateCSVForConsolidatedReportS(ArrayList<ConsolidatedPostData> allPostEndShift) {
        // String strConsolidated = PostCollectionRecordsService.toJson(allPostEndShift);
        //  System.out.println(strConsolidated);


        ConsolidatedData consolidatedData = new ConsolidatedData();
        consolidatedData.csvVersion = "3.0";
        consolidatedData.records = allPostEndShift;


        String strConsolidated = PostCollectionRecordsService.toJson(consolidatedData);
        // System.out.println(strConsolidated);
        // strConsolidated = strConsolidated.replace(String.valueOf("\"\""),String.valueOf("\""));


        String resultBody = null, result = null;
        try {
            // resultHeader = this.createCSVStringReportsHeader(allReportsCsv);
//            ConsolidatedReport consolidatedReport = getConsolidatedReport(consolidatedData);
//            ArrayList<ConsolidatedReport> allConsolidateReport = new ArrayList<>();
//            allConsolidateReport.add(consolidatedReport);
//            resultBody = this.createCSVStringForPostEndShift(allConsolidateReport);


            com.devapp.devmain.encryption.CSVHelper<?> helper = new CSVHelper<com.devapp.devmain.encryption.ConsolidatedReport>(
                    com.devapp.devmain.encryption.ConsolidatedReport.class);
            resultBody = helper.stringifyString(strConsolidated);
            resultBody = resultBody.replace("\"\"", "\"");


            //  resultBody = this.createCSVString(strConsolidated);
            //  result = resultHeader + resultBody;
            result = resultBody;
            //To write encrypted file
            return generateEncryptedFileS(result);
        } catch (Exception e1) {
            e1.printStackTrace();
            return "";
        }


    }

    public String generateEncryptedFileS(String result) {
        //   byte[] data = this.readFromFile(fileName + ".txt");
        byte[] data = result.getBytes();
        String encryptedData = null;
        try {
            encryptedData = com.devapp.devmain.encryption.AESHelper.getInstance(saltValue.getBytes())
                    .encrypt(seedValue, new String(data));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //  fileName = fileName + "enc";
        return encryptedData;
    }

    public void generateCSVForCleaningReport(String cleaningData, String fileName) {

        Log.v(ESSAE, "encrypted file:" + fileName);
        Log.v(ESSAE, "data to encrypt :" + cleaningData);
        String resultBody = null, result = null;
        try {
            com.devapp.devmain.encryption.CSVHelper<?> helper = new CSVHelper<com.devapp.devmain.encryption.ConsolidatedReport>(
                    com.devapp.devmain.encryption.ConsolidatedReport.class);
            resultBody = helper.stringifyString(cleaningData);
            resultBody = resultBody.replace("\"\"", "\"");

            result = resultBody;
            //To write normal file
            WriteToFile(fileName, result.getBytes("UTF8"));
            //To write encrypted file
            generateEncryptedFile(fileName, result);
        } catch (Exception e1) {
            e1.printStackTrace();
        }


    }

    public void getIncentiveRateChart(String decryptedData) {
        String rateChartType = SmartCCConstants.RATECHART_TYPE_PROTEIN;
        String centerId = null;
        boolean isRateChartActive;
        IncentiveRateChartPostEntity ratechartDetailsEnt = new IncentiveRateChartPostEntity();
        ArrayList<BonusChartEntity> allRateChartEnt = new ArrayList<BonusChartEntity>();
        DatabaseHandler dbhandler = DatabaseHandler.getDatabaseInstance();
        // Getting Rate data from JSON
        JSONArray jArray = null;
        boolean isValidRateChart = false;

        SmartCCUtil smartCCUtil = new SmartCCUtil(mContext);
        try {
            JSONObject jsonData = new JSONObject(decryptedData);

            if (jsonData.has("rateChartType")) {                         //Added ratechart type
                if (jsonData.getString("rateChartType") != null
                        && !jsonData.getString("rateChartType").equalsIgnoreCase(
                        "null")) {
                    rateChartType = jsonData.getString("rateChartType");
                } else {
                    rateChartType = Util.RATECHART_TYPE_PROTEIN;
                }

            }

            if (jsonData.has("societyList")) {
                JSONArray jsonArray = jsonData.getJSONArray("societyList");
                if (jsonArray != null && jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {

//                        strEndDate = Util.getDateAndTimeFromMilli(
//                                Long.parseLong(jsonArray.getJSONObject(i).getString("endDate")), 0);

                        if (dbhandler.getSocietyID().equalsIgnoreCase(jsonArray.getJSONObject(i).getString("societyId"))
                                && checkDateValidation(Long.parseLong(jsonArray.getJSONObject(i).getString("endDate")))) {

                            isValidRateChart = true;
//                            strStartDate = Util.getDateAndTimeFromMilli(
//                                    Long.parseLong(jsonArray.getJSONObject(i).getString("startDate")), 0);
                            if (jsonArray.getJSONObject(i).getString("isActive") != null
                                    && jsonArray.getJSONObject(i).getString("isActive").equalsIgnoreCase(
                                    "null")) {
                                isRateChartActive = false;
                            } else {
                                isRateChartActive = jsonArray.getJSONObject(i).getBoolean("isActive");
                            }

                            ratechartDetailsEnt.societyId = String.valueOf(session.getSocietyColumnId());
                            ratechartDetailsEnt.rateChartType = rateChartType;
                            ratechartDetailsEnt.milkType = jsonData.getString("milkType").toUpperCase(Locale.ENGLISH);
                            ratechartDetailsEnt.name = jsonData.getString("name").toUpperCase(Locale.ENGLISH);
                            ratechartDetailsEnt.startDate = Long.parseLong(jsonArray.getJSONObject(i).getString("startDate"));
                            ratechartDetailsEnt.endDate = Long.parseLong(jsonArray.getJSONObject(i).getString("endDate"));
                            String startDate = smartCCUtil.getDateFormatFromMillis("dd-MM-yyyy"
                                    , ratechartDetailsEnt.startDate);
                            String endDate = smartCCUtil.getDateFormatFromMillis("dd-MM-yyyy"
                                    , ratechartDetailsEnt.endDate);
                            ratechartDetailsEnt.isActive = isRateChartActive;

                            jArray = jsonData.getJSONArray("valueList");
                            if (jArray != null && jArray.length() > 0) {

                                for (int j = 0; j < jArray.length(); j++) {
                                    BonusChartEntity rateEnt = new BonusChartEntity();
                                    rateEnt.endDate = endDate;
                                    rateEnt.startDate = startDate;
                                    rateEnt.milkType = ratechartDetailsEnt.milkType;
                                    rateEnt.name = ratechartDetailsEnt.name;
                                    rateEnt.societyId = jsonArray.getJSONObject(i).getString("societyId");
                                    rateEnt.point = jArray.getJSONObject(j).getDouble("point");
                                    rateEnt.bonusRate = jArray.getJSONObject(j).getDouble("rate");
                                    allRateChartEnt.add(rateEnt);
                                }
                            }

                            ratechartDetailsEnt.valueList = allRateChartEnt;


                        }

                    }

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        if (allRateChartEnt != null && allRateChartEnt.size() > 0 && isValidRateChart) {
            Util.displayErrorToast("Rate chart update in progress.", mContext);


            ArrayList<IncentiveRateChartPostEntity> rateChartEntities = new ArrayList<>();
            rateChartEntities.add(ratechartDetailsEnt);

            DatabaseManager rateChartManager = new DatabaseManager(mContext);
            IncentiveRateChartManager incentiveRateChartManager
                    = new IncentiveRateChartManager(mContext);
            incentiveRateChartManager.saveAll(rateChartEntities, false);
//            incentiveRateDao.saveAll(allRateChartEnt);
//            DatabaseManager rateChartManager = new DatabaseManager(mContext);
//            rateChartManager.addCompleteInCentiveRateChart(ratechartDetailsEnt, allRateChartEnt, Util.USB);
            rateChartManager.manageRateChart();
            Util.displayErrorToast("Rate chart updated successfully.", mContext);


        } else {
            Util.displayErrorToast("Invalid Rate Chart", mContext);

        }
    }


    private boolean checkDateValidation(long endDate) {
        boolean isValidDate = true;

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        if (calendar.getTimeInMillis() > endDate) {
            isValidDate = false;
        }

        return isValidDate;

    }
}
