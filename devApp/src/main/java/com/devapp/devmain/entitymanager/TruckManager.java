package com.devapp.devmain.entitymanager;

import android.content.Context;

import com.devapp.devmain.agentfarmersplit.AppConstants;
import com.devapp.devmain.dao.Dao;
import com.devapp.devmain.dao.DaoFactory;
import com.devapp.devmain.dao.TruckDao;
import com.devapp.devmain.entity.Entity;
import com.devapp.devmain.network.EntityManager;
import com.devapp.devmain.network.InvalidUpdateException;
import com.devapp.devmain.postentities.CollectionConstants;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.user.Util;
import com.devapp.smartcc.entities.TrucKInfo;
import com.devapp.smartcc.entityandconstants.SmartCCConstants;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.util.List;

/**
 * Created by x on 1/2/18.
 */

public class TruckManager implements EntityManager {

    private final String TYPE = AppConstants.ConfigurationTypes.TRUCK;
    private TruckDao dao;
    private AmcuConfig amcuConfig;
    private Context mContext;

    public TruckManager(Context context) {
        mContext = context;
        amcuConfig = AmcuConfig.getInstance();
        dao = (TruckDao) DaoFactory.getDao(CollectionConstants.TRUCK);
    }

    @Override
    public List<Entity> getEntityFromJson(String jsonString, boolean isCloudPush) throws IOException, InvalidUpdateException {
        ObjectMapper mapper = new ObjectMapper();
        List<Entity> truckList = mapper.readValue(jsonString, new TypeReference<List<TrucKInfo>>() {
        });
        return truckList;
    }

    @Override
    public String getDpnUrl() {
        if (SmartCCConstants.TRUCK_UPDATE_URI != null) {
            amcuConfig.setTruckUpdateUrl(SmartCCConstants.TRUCK_UPDATE_URI);
            String test = amcuConfig.getTruckUpdateUrl();
            SmartCCConstants.TRUCK_UPDATE_URI = null;
        }
        if (amcuConfig.getTruckUpdateUrl() != null) {
            return amcuConfig.getTruckUpdateUrl();
        }
        return null;
    }

    @Override
    public void resetDpnUrl() {
        amcuConfig.setTruckFailCount(0);
        amcuConfig.setTruckUpdateUrl(null);
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
        for (int i = 0, len = entityList.size(); i < len; i++) {
            dao.saveOrUpdate(entityList.get(i));
        }
        Util.displayErrorToast("Truck(s) Updated Successfully", mContext);
    }
}
