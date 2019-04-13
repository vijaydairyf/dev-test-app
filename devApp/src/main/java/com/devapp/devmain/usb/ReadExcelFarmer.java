package com.devapp.devmain.usb;

import android.content.Context;

import com.devapp.devmain.agentfarmersplit.AppConstants;
import com.devapp.devmain.dao.DaoFactory;
import com.devapp.devmain.dao.FarmerDao;
import com.devapp.devmain.entity.FarmerEntity;
import com.devapp.devmain.postentities.CollectionConstants;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.server.SessionManager;
import com.devapp.devmain.user.Util;
import com.devapp.devmain.util.ValidationHelper;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import jxl.Cell;
import jxl.CellType;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class ReadExcelFarmer {

    public static ArrayList<FarmerEntity> allFarmEnt;
    public StringBuilder duplicateMessages;
    public StringBuilder newFarmerMessages;
    ArrayList<String> allFarmId = new ArrayList<String>();
    ArrayList<String> allfarmName = new ArrayList<String>();
    ArrayList<String> allfarmBarcode = new ArrayList<String>();
    ArrayList<String> allFarmSocId = new ArrayList<String>();
    ArrayList<String> allFarmCow = new ArrayList<String>();
    ArrayList<String> allMobNum = new ArrayList<String>();
    ArrayList<String> allFarmCan = new ArrayList<String>();
    int formarIncrement = 0;
    private String inputFile;
    private Context mcontext;

    public void setInputFile(String inputFile, int check) {
        this.inputFile = inputFile;

    }

    public String read(Context context) throws IOException {
        allFarmEnt = new ArrayList<FarmerEntity>();
        File inputWorkbook = new File(inputFile);
        ValidationHelper validationHelper = new ValidationHelper();
        this.mcontext = context;
        AmcuConfig amcuConfig = AmcuConfig.getInstance();
        FarmerDao farmerDao = (FarmerDao) DaoFactory.getDao(CollectionConstants.FARMER);
        formarIncrement = farmerDao.getlastFamerId();
        newFarmerMessages = new StringBuilder();
        Workbook w;
        try {
            w = Workbook.getWorkbook(inputWorkbook);
            // Get the first sheet
            Sheet sheet = w.getSheet(0);
            // Loop over first 10 column and lines

            for (int j = 0; j < sheet.getRows(); j++) {

                FarmerEntity farmEntity = new FarmerEntity();
                for (int i = 0; i < sheet.getColumns(); i++) {
                    Cell cell = sheet.getCell(i, j);
                    CellType type = cell.getType();
                    if (type == CellType.LABEL) {
                        System.out.println("I got a label "
                                + cell.getContents());

                        if (j > 0) {

                            if (i == 0) {
                                if (cell.getContents() != null
                                        && cell.getContents().length() > 0) {
                                    String farmId = Util.paddingFarmerId(cell
                                            .getContents(), AmcuConfig.getInstance().getFarmerIdDigit());


                                    if (amcuConfig.getKeyAllowFarmerIncrement()) {

                                        allFarmId.add(getFarmerId());

                                    } else {

                                        allFarmId.add(farmId);


                                    }

                                } else {

                                    if (amcuConfig.getKeyAllowFarmerIncrement()) {

                                        allFarmId.add(getFarmerId());

                                    } else {
                                        allFarmId.add(cell.getContents());


                                    }
                                }
                            } else if (i == 3) {
                                allMobNum.add(cell.getContents());
                            } else if (i == 1) {
                                allfarmName.add(cell.getContents());
                            } else if (i == 2) {
                                allfarmBarcode.add(cell.getContents());
                            } else if (i == 4) {
                                if (cell.getContents() != null
                                        && cell.getContents().length() > 0) {
                                    allFarmCow.add(cell.getContents());
                                } else {
                                    allFarmCow.add("COW");
                                }
                            } else if (i == 5) {
                                if (cell.getContents() != null
                                        && cell.getContents().length() > 0) {

                                    int cans = 0;

                                    try {
                                        cans = Integer.parseInt(cell.getContents());
                                    } catch (NumberFormatException e) {
                                        e.printStackTrace();
                                    }

                                    if (cans > 0)

                                    {
                                        allFarmCan.add(cell.getContents());
                                    } else {
                                        allFarmCan.add("0");
                                    }


                                } else {
                                    allFarmCan.add("1");
                                }
                            }
                        }
                    } else if (type == CellType.NUMBER) {
                        if (i == 0) {
                            try {
                                String farmId = Util.paddingFarmerId(String.valueOf(cell
                                        .getContents()), AmcuConfig.getInstance().getFarmerIdDigit());
                                if (amcuConfig.getKeyAllowFarmerIncrement()) {

                                    allFarmId.add(getFarmerId());

                                } else {
                                    allFarmId.add(farmId);

                                }

                            } catch (Exception e) {
                                if (amcuConfig.getKeyAllowFarmerIncrement()) {
                                    allFarmId.add(getFarmerId());

                                } else {
                                    allFarmId.add(cell
                                            .getContents());


                                }

                                e.printStackTrace();
                            }
                        } else if (i == 3) {
                            allMobNum.add(cell.getContents());
                        } else if (i == 5) {
                            if (cell.getContents() != null
                                    && cell.getContents().length() > 0) {

                                int cans = 0;

                                try {
                                    cans = Integer.parseInt(cell.getContents());
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }

                                if (cans > 0)

                                {
                                    allFarmCan.add(cell.getContents());
                                } else {
                                    allFarmCan.add("0");
                                }
                            } else {
                                allFarmCan.add("1");
                            }
                        }
                    } else if (type == CellType.EMPTY) {


                        if (amcuConfig.getKeyAllowFarmerIncrement()) {

                            allFarmId.add((getFarmerId()));

                        }


                    }
                }

                if (j > 0 && allFarmId.size() == j) {
                    allFarmSocId.add(String
                            .valueOf(new SessionManager(mcontext)
                                    .getSocietyColumnId()));

                    if (allFarmCow.size() > (j - 1)
                            && allFarmCow.get(j - 1) != null) {
                        String ma = Util.checkForValidMilkType(allFarmCow.get(j - 1));
                        if (ma != null) {
                            allFarmEnt = null;
                            return "Invalid MilkType " + ma + " please try again!";
                        }
                    } else {
                        allFarmCow.add("Cow");
                    }
// Import without barcode
                    if (allfarmBarcode.size() > (j - 1)
                            && allfarmBarcode.get(j - 1) != null) {
                    } else {
                        allfarmBarcode.add("");
                    }
//To import without mobile number
                    if (allMobNum.size() > (j - 1)
                            && allMobNum.get(j - 1) != null) {
                    } else {
                        allMobNum.add("");
                    }

                    //To import with multiple cans
                    if (allFarmCan.size() > (j - 1)
                            && allFarmCan.get(j - 1) != null && Integer.parseInt(allFarmCan.get(j - 1)) < 1) {
                        allFarmEnt = null;
                        return "Invalid Number of cans for member " + allFarmId.get(j - 1) + " please try again!";
                    } else if (allFarmCan.size() <= (j - 1)
                            || allFarmCan.get(j - 1) == null) {
                        allFarmCan.add("1");
                    }

                    try {

                        farmEntity.farmer_id = Util.validateFarmerId(allFarmId.get(j - 1)
                                , mcontext, amcuConfig.getFarmerIdDigit(), false);
                        // }
                        farmEntity.farmer_cans = allFarmCan.get(j - 1);

                        if (allfarmBarcode.get(j - 1) == null
                                || allfarmBarcode.get(j - 1).replace(" ", "").length() < 2) {
                            farmEntity.farmer_barcode = null;
                        } else {
                            farmEntity.farmer_barcode = allfarmBarcode.get(j - 1);
                        }
                        farmEntity.soc_code = String.valueOf(new SessionManager(mcontext).getSocietyColumnId());
                        farmEntity.farmer_name = allfarmName.get(j - 1);
                        farmEntity.farmer_cattle = allFarmCow.get(j - 1);
                        farmEntity.farm_mob = allMobNum.get(j - 1);

                        int id = 0;
                        id = Integer.parseInt(farmEntity.farmer_id);

                        //Adding default type as Farmer and Agent as NA

                        farmEntity.farmerType = AppConstants.FARMER_TYPE_FARMER;
                        farmEntity.agentId = AppConstants.NA;

                        if (id < 1) {
                            allFarmEnt = null;
                            return "Farmer Id should be greator than 0.";
                        } else if (AmcuConfig.getInstance().getFarmerIdDigit() > 5 && id > 999999) {
                            allFarmEnt = null;
                            return "Farmer Id should be less than 999999.";
                        } else if ((AmcuConfig.getInstance().getFarmerIdDigit() > 4 &&
                                AmcuConfig.getInstance().getFarmerIdDigit() < 6) && id > 99999) {
                            allFarmEnt = null;
                            return "Farmer Id should be less than 99999.";
                        } else if (AmcuConfig.getInstance().getFarmerIdDigit() <= 4 && id > 9999) {
                            allFarmEnt = null;
                            return "Farmer Id should be less than 9999.";
                        } else if (checkIfSample(farmEntity.farmer_id)) {
                            allFarmEnt = null;
                            return farmEntity.farmer_id + " present as a sample Id";
                        } else if (farmEntity.farmer_name == null || farmEntity.farmer_name.length() < 1) {
                            allFarmEnt = null;
                            return "Invalid name for farmer Id " + farmEntity.farmer_id;
                        }
                        //Commented for HINDI fonts
                       /* else if (!validationHelper.isValidName(farmEntity.farmer_name)) {
                            allFarmEnt = null;
                            return "Invalid name " + farmEntity.farmer_name + " for farmer Id " + farmEntity.farmer_id;
                        }
                        */
                        else if (!ValidationHelper.isValidFarmerId(farmEntity.farmer_id,
                                amcuConfig.getFarmerIdDigit())) {
                            allFarmEnt = null;
                            return "Invalid farmer Id " + farmEntity.farmer_id;

                        } else {

                            farmEntity.farmer_id = ValidationHelper.paddingOnlyFarmerId(
                                    Integer.parseInt(farmEntity.farmer_id), amcuConfig.getFarmerIdDigit());
                            String getErrMsg = Util.checkForRegisteredCode(farmEntity.farmer_id,
                                    AmcuConfig.getInstance().getFarmerIdDigit(), false);
                            if (getErrMsg != null) {
                                allFarmEnt = null;
                                return getErrMsg;
                            }
                        }
                    } catch (NullPointerException e) {
                        allFarmEnt = null;
                        return "Invalid excel data please try again!";
                    } catch (NumberFormatException e) {
                        allFarmEnt = null;
                        return "Invalid excel data please try again!";
                    } catch (Exception e) {
                        e.printStackTrace();
                        allFarmEnt = null;
                        return "Invalid excel data please try again!";

                    }

                    if (farmEntity.farmer_id != null) {
                        String dupMsg = Util.getDuplicateCenterIdOrBarCode(farmEntity.farmer_id,
                                farmEntity.farmer_barcode, mcontext);
                        if (dupMsg != null) {
                            allFarmEnt = null;
                            return dupMsg;
                        }
                    }

                    if (Util.isOperator(mcontext)) {
                        if (amcuConfig.getKeyAllowFarmerEdit() && !amcuConfig.getKeyAllowFarmerCreate()) {
                            if (farmerDao.findById(farmEntity.farmer_id) != null) {
                                allFarmEnt.add(farmEntity);
                            }
                        } else if (!amcuConfig.getKeyAllowFarmerEdit() && amcuConfig.getKeyAllowFarmerCreate()) {
                            if (farmerDao.findById(farmEntity.farmer_id) == null) {
                                allFarmEnt.add(farmEntity);
                                newFarmerMessages.append(farmEntity.farmer_id + ",");
                            }
                        } else {

                            allFarmEnt.add(farmEntity);

                        }
                    } else {

                        allFarmEnt.add(farmEntity);

                    }

                }
            }

        } catch (BiffException e) {
            e.printStackTrace();
            allFarmEnt = null;
            return "Invalid excel format please try again!";
        }


        CheckForDuplicate();

        if (duplicateMessages != null) {
            return duplicateMessages.toString();
        } else {
            return null;
        }
    }

    public boolean checkDuplicateFarmerIdOrBarCode(
            ArrayList<String> allFarmerId, ArrayList<String> allBarcodes) {
        Map<String, Integer> map = new HashMap<String, Integer>();
        Map<String, Integer> treeMapFarmerId = null;
        Map<String, Integer> treeMapBarCode = null;
        duplicateMessages = new StringBuilder();

        duplicateMessages = duplicateMessages.append("\n"
                + "Duplicate farmers from sheet" + "\n");
        boolean isDuplicate = false;

        for (String temp : allFarmerId) {
            Integer count = map.get(temp);

            map.put(temp, (count == null) ? 1 : count + 1);
            if (count != null) {
                isDuplicate = true;
            }

        }
        treeMapFarmerId = new TreeMap<String, Integer>(map);

        if (treeMapFarmerId.size() > 0) {
            duplicateValues(treeMapFarmerId);
        }

        map = new HashMap<String, Integer>();
        for (String temp : allBarcodes) {
            if (temp != null && temp.equalsIgnoreCase(""))
                continue;

            Integer count = map.get(temp);

            map.put(temp, (count == null) ? 1 : count + 1);
            if (count != null) {
                isDuplicate = true;
            }

        }
        treeMapBarCode = new TreeMap<String, Integer>(map);

        if (treeMapBarCode.size() > 0) {
            duplicateValues(treeMapBarCode);
        }

        if (!isDuplicate) {
            duplicateMessages = null;
        }

        return isDuplicate;

    }

    public void duplicateValues(Map<String, Integer> map) {

        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            if (entry.getValue() > 1) {
                duplicateMessages.append(entry.getKey() + " Value : "
                        + entry.getValue() + "\n");
            }

        }

    }

    public boolean getFarmerIdAndBarCodesFromDB() {
        ArrayList<String> allFarmId = new ArrayList<>(), allBarCodes = new ArrayList<>();
        DatabaseHandler databaseHandler = DatabaseHandler.getDatabaseInstance();
        FarmerDao farmerDao = (FarmerDao) DaoFactory.getDao(CollectionConstants.FARMER);
        ArrayList<FarmerEntity> farmerEntities = (ArrayList<FarmerEntity>) farmerDao.findAll();
        for (int i = 0, len = farmerEntities.size(); i < len; i++) {
            allFarmId.add(farmerEntities.get(i).farmer_id);
            allBarCodes.add(farmerEntities.get(i).farmer_barcode);
        }
        boolean isDuplicate = false, isDup = false;

        if (allFarmId != null) {
            isDuplicate = getDuplicateFromDB(allFarmId, 0);
            isDup = isDuplicate;
        }
        if (allBarCodes != null) {
            isDuplicate = getDuplicateFromDB(allBarCodes, 1);
            if (!isDup) {
                isDup = isDuplicate;
            }

        }
        try {
            allFarmId = databaseHandler.getAllSampleIdorBarcodes(
                    String.valueOf(new SessionManager(mcontext)
                            .getSocietyColumnId()), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Removed database close statement
        databaseHandler = DatabaseHandler.getDatabaseInstance();
        try {
            allBarCodes = databaseHandler.getAllSampleIdorBarcodes(
                    String.valueOf(new SessionManager(mcontext)
                            .getSocietyColumnId()), 1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (allFarmId != null) {
            isDuplicate = getDuplicateFromDB(allFarmId, 0);
            if (!isDup) {
                isDup = isDuplicate;
            }
        }
        if (allBarCodes != null) {
            isDuplicate = getDuplicateFromDB(allBarCodes, 1);
            if (!isDup) {
                isDup = isDuplicate;
            }
        }
        if (AmcuConfig.getInstance().getKeyAllowFarmerCreate()) {
            if (newFarmerMessages != null && newFarmerMessages.length() > 0) {
                duplicateMessages.append("\n" + "New Farmers" + "\n" + newFarmerMessages);
            }
        }

        return isDup;

    }

    public boolean getDuplicateFromDB(ArrayList<String> list, int farmOrBarcode) {

        ArrayList<String> duplicateList = new ArrayList<String>();
        for (String item : list) {
            if (farmOrBarcode == 0) {
                if (allFarmId.contains(item)) {
                    duplicateList.add(item);
                    if (AmcuConfig.getInstance().getKeyAllowFarmerEdit())
                        duplicateMessages.append(item + ",");

                }
            } else {
                if ((item != null) && item.equals("")) continue;
                if (allfarmBarcode.contains(item)) {
                    duplicateList.add(item);
                    if (AmcuConfig.getInstance().getKeyAllowFarmerEdit())
                        duplicateMessages.append(item + "\n");
                }
            }


        }

        if (duplicateList.size() > 0) {
            return true;
        } else {

            return false;
        }
    }

    public void CheckForDuplicate() {

        //To check duplicate from excel sheet
        checkDuplicateFarmerIdOrBarCode(allFarmId, allfarmBarcode);
        boolean chkForPreviousMsg = false;

        if (duplicateMessages == null) {
            duplicateMessages = new StringBuilder();
            if (AmcuConfig.getInstance().getKeyAllowFarmerEdit())
                duplicateMessages = duplicateMessages.append("\n" + "Already existing farmers" + "\n");
        } else {
            chkForPreviousMsg = true;
            if (AmcuConfig.getInstance().getKeyAllowFarmerEdit())
                duplicateMessages = duplicateMessages.append("\n" + "Already existing farmers" + "\n");
        }

        boolean isDuplicateDB = getFarmerIdAndBarCodesFromDB();

        if (!isDuplicateDB && !chkForPreviousMsg) {
            duplicateMessages = null;
        }
    }

    public boolean checkIfSample(String id) {
        boolean isDuplicate = false;
        DatabaseHandler dbh = DatabaseHandler.getDatabaseInstance();

        try {
            isDuplicate = dbh.checkForSampleId(id);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            {
                //Removed database close;
            }
            return isDuplicate;
        }

    }


    private String getFarmerId() {
        updatFarmerId();

        ValidationHelper validationHelper = new ValidationHelper();

        DecimalFormat decimalFormat = validationHelper.getFormatForFarmerId(
                AmcuConfig.getInstance().getFarmerIdDigit());

        String farmerId = validationHelper.getAppendedFarmerId(formarIncrement, decimalFormat);

        return farmerId;


    }


    private void updatFarmerId() {

        formarIncrement++;
        ValidationHelper validationHelper = new ValidationHelper();

        DecimalFormat decimalFormat = validationHelper.getFormatForFarmerId(
                AmcuConfig.getInstance().getFarmerIdDigit());

        String farmerId = validationHelper.getAppendedFarmerId(formarIncrement, decimalFormat);

        if (checkIfSample(farmerId)) {
            updatFarmerId();
        }

    }


}
