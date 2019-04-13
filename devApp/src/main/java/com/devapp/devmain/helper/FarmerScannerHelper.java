package com.devapp.devmain.helper;

import android.content.Context;

import com.devapp.devmain.dao.DaoFactory;
import com.devapp.devmain.dao.FarmerDao;
import com.devapp.devmain.postentities.CollectionConstants;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.server.SessionManager;
import com.devapp.devmain.user.Util;

import java.util.Locale;

/**
 * Created by u_pendra on 29/12/16.
 */

public class FarmerScannerHelper {


    public static final int ID = 0;
    public static final int BARCODE = 1;

    Context mContext;
    AmcuConfig amcuConfig;
    SessionManager sessionManager;
    DatabaseHandler databaseHandler;
    private FarmerDao farmerDao;

    public FarmerScannerHelper(Context ctx) {
        this.mContext = ctx;
        amcuConfig = AmcuConfig.getInstance();
        sessionManager = new SessionManager(mContext);
        farmerDao = (FarmerDao) DaoFactory.getDao(CollectionConstants.FARMER);
        databaseHandler = DatabaseHandler.getDatabaseInstance();
    }


    /**
     * This function will return the user type(farmer,center,sample) on the basis of id
     *
     * @param id id can be barcode or type id
     * @return
     */

    public String getUserType(String id, int code) {

        String userType = UserType.INVALID;
        boolean check = false;
        id = id.toUpperCase(Locale.ENGLISH);

        if (code == ID) {
            String farmId = Util.paddingFarmerId(id, amcuConfig.getFarmerIdDigit());
            check = farmerDao.findById(farmId) != null;
            if (check) {
                return UserType.FARMER;
            }

            check = databaseHandler.isSampleExistWithId(farmId);

            if (check) {
                return UserType.SAMPLE;
            }

            check = databaseHandler.isCenterExistWithId(id);

            if (check) {
                return UserType.CENTER;
            } else {
                return userType;
            }
        } else if (code == BARCODE) {
            check = farmerDao.findByBarcode(id) != null;
            if (check) {
                return UserType.FARMER;
            }

            check = databaseHandler.isSampleExistWithBarcode(id);

            if (check) {
                return UserType.SAMPLE;
            }

            check = databaseHandler.isCenterExistWithBarcode(id);

            if (check) {
                return UserType.CENTER;
            } else {
                return userType;
            }
        } else {
            return userType;
        }


    }

    /**
     * @param id
     * @return
     */
    public int isBarocodeOrId(String id) {
        BarcodePrefix barcodePrefix;
        int code = ID;

        if (id.startsWith("STPL")) {
            if (id.length() > 17) {
                code = BARCODE;
                barcodePrefix = BarcodePrefix.STPL;
            }
        } else {
            if (id.startsWith("SIN") && id.length() > 12) {
                barcodePrefix = BarcodePrefix.SIN;
                code = BARCODE;
            }
        }

        return code;

    }

    /**
     * id the enterId in farmer scanner screen
     * usertype can be farmer sample or center
     *
     * @param id
     * @param userType
     * @return
     */

    public Object getUserDetailsFromUserType(String id, String userType) {
        int code = isBarocodeOrId(id);
        Object obj = null;
        id = id.toUpperCase(Locale.ENGLISH);


        if (userType.equalsIgnoreCase(UserType.FARMER)) {
            if (code == ID) {
                String farmId = Util.paddingFarmerId(id, amcuConfig.getFarmerIdDigit());
                obj = farmerDao.findById(farmId);
            } else if (code == BARCODE) {
                obj = farmerDao.findByBarcode(id);
            }

        } else if (userType.equalsIgnoreCase(UserType.SAMPLE)) {
            if (code == ID) {
                String sampleId = Util.paddingFarmerId(id, amcuConfig.getFarmerIdDigit());
                obj = databaseHandler.getSampleDataEntity(sampleId, 0);
            } else if (code == BARCODE) {
                obj = databaseHandler.getSampleDataEntity(id, 1);
            }
        } else if (userType.equalsIgnoreCase(UserType.CENTER)) {
            if (code == ID) {
                obj = databaseHandler.getCenterEntity(id, Util.CHECK_DUPLICATE_CENTERCODE);
            } else if (code == BARCODE) {
                obj = databaseHandler.getCenterEntity(id, Util.CHECK_DUPLICATE_CENTERBARCODE);
            }
        } else if (userType.equalsIgnoreCase(UserType.INVALID)) {
            if (code == ID) {

            } else if (code == BARCODE) {

            }
        }

        return obj;

    }


}
