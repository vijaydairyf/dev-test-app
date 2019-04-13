package com.devapp.devmain.entitymanager;

import android.content.Context;

import com.devapp.devmain.agentfarmersplit.AppConstants;
import com.devapp.devmain.dao.Dao;
import com.devapp.devmain.dao.DaoFactory;
import com.devapp.devmain.dao.FarmerDao;
import com.devapp.devmain.entity.Entity;
import com.devapp.devmain.entity.FarmerEntity;
import com.devapp.devmain.entity.FarmerPostEntity;
import com.devapp.devmain.network.EntityManager;
import com.devapp.devmain.network.InvalidUpdateException;
import com.devapp.devmain.postentities.CollectionConstants;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.user.Util;
import com.devapp.devmain.util.ValidationHelper;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by x on 5/2/18.
 */

public class FarmerManager implements EntityManager {

    private static final String TYPE = AppConstants.ConfigurationTypes.FARMER;
    public FarmerDao dao;
    AmcuConfig amcuConfig;
    private Context mContext;

    public FarmerManager(Context context) {
        mContext = context;
        amcuConfig = AmcuConfig.getInstance();
        dao = (FarmerDao) DaoFactory.getDao(CollectionConstants.FARMER);

    }

    @Override
    public List<Entity> getEntityFromJson(String jsonString, boolean isCloudPush) throws IOException, InvalidUpdateException {
        ObjectMapper mapper = new ObjectMapper();
        List<FarmerPostEntity> farmerList = mapper.readValue(jsonString, new TypeReference<List<FarmerPostEntity>>() {
        });
        List<Entity> farmerEntityList = new ArrayList<>();
        for (int i = 0, len = farmerList.size(); i < len; i++) {
            FarmerEntity farmerEntity = new FarmerEntity(farmerList.get(i));
            if (farmerEntity.farmerType == null) {
                farmerEntity.farmerType = AppConstants.FARMER_TYPE_FARMER;
            } else if (farmerEntity.farmerType.equalsIgnoreCase("AGGREGATE_FARMER")) {
                farmerEntity.farmerType = AppConstants.FARMER_TYPE_AGENT;
            }
            farmerEntity.soc_code = "0";

            int farmerCan = 0;

            try {
                farmerCan = Integer.valueOf(farmerEntity.farmer_cans);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            if (farmerCan == 0) {
                farmerEntity.farmer_cans = String.valueOf(1);
            }

            farmerEntityList.add(farmerEntity);
        }
        return farmerEntityList;
    }

    @Override
    public String getDpnUrl() {
        if (Util.FARMER_URI != null && Util.FARMER_URI.contains("amcu")) {
            amcuConfig.setFarmerURI(Util.FARMER_URI);
            Util.FARMER_URI = null;
        }
        if (amcuConfig.getFarmerURI() != null) {
            return amcuConfig.getFarmerURI();
        }
        return null;
    }

    @Override
    public void resetDpnUrl() {
        amcuConfig.setFarmerFailCount(0);
        amcuConfig.setFarmerURI(null);
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public Dao getDao() {
        return dao;
    }

    @Override
    public void saveAll(List<? extends Entity> entityList, boolean isCloudPush) {
        ArrayList<String> barcodes = new ArrayList<>();
        int farmerIdLength = amcuConfig.getFarmerIdDigit();
//        TODO THIS should be done in Transaction
        for (int i = 0, len = entityList.size(); i < len; i++) {
            FarmerEntity farmerEntity = (FarmerEntity) entityList.get(i);
            if (farmerEntity.farmer_barcode != null && !farmerEntity.farmer_barcode.equals("")) {
                dao.deleteByBarcode(farmerEntity.farmer_barcode);
            }
        }
        for (int i = 0, len = entityList.size(); i < len; i++) {

            FarmerEntity farmerEntity = (FarmerEntity) entityList.get(i);
            String msg = Util.getDuplicateIdOrBarCode(farmerEntity.farmer_id, farmerEntity.farmer_barcode, mContext);
            if (msg != null && !msg.contains("Member")) {
                Util.displayErrorToast(msg, mContext);
                continue;
            }
            farmerEntity.farmer_id = Util.validateFarmerId(farmerEntity.farmer_id,
                    mContext, amcuConfig.getFarmerIdDigit(), false);
            try {
                if (!ValidationHelper.isValidFarmerId(farmerEntity.farmer_id, amcuConfig.getFarmerIdDigit())) {
                    Util.displayErrorToast("Invalid farmer Id " + farmerEntity.farmer_id, mContext);
                    continue;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (farmerEntity.farmer_barcode != null) {
                farmerEntity.farmer_barcode = farmerEntity.farmer_barcode.toUpperCase();
            }
            farmerEntity.sentStatus = CollectionConstants.UNSENT;
            dao.saveOrUpdate(entityList.get(i));
        }
        Util.displayErrorToast("Farmer(s) Updated Successfully", mContext);
    }
}
