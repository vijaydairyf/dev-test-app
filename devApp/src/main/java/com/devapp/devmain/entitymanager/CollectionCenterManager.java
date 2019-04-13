package com.devapp.devmain.entitymanager;

import android.content.Context;

import com.devapp.devmain.agentfarmersplit.AppConstants;
import com.devapp.devmain.dao.CollectionCenterDao;
import com.devapp.devmain.dao.Dao;
import com.devapp.devmain.dao.DaoFactory;
import com.devapp.devmain.entity.CenterEntity;
import com.devapp.devmain.entity.CenterPostEntity;
import com.devapp.devmain.entity.Entity;
import com.devapp.devmain.helper.CattleType;
import com.devapp.devmain.network.EntityManager;
import com.devapp.devmain.network.InvalidUpdateException;
import com.devapp.devmain.postentities.CollectionConstants;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.SessionManager;
import com.devapp.devmain.user.Util;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by x on 31/1/18.
 */

public class CollectionCenterManager implements EntityManager {

    private final String TYPE = AppConstants.ConfigurationTypes.COLLECTION_CENTER_LIST;
    private CollectionCenterDao dao;
    private AmcuConfig amcuConfig;
    private SessionManager sessionManager;
    private Context mContext;

    public CollectionCenterManager(Context context) {
        mContext = context;
        amcuConfig = AmcuConfig.getInstance();
        sessionManager = new SessionManager(mContext);
        dao = (CollectionCenterDao) DaoFactory.getDao(CollectionConstants.COLLECTION_CENTER);
    }

    @Override
    public List<Entity> getEntityFromJson(String jsonString, boolean isCloudPush) throws IOException, InvalidUpdateException {
        ObjectMapper mapper = new ObjectMapper();
        List<CenterPostEntity> centerList = mapper.readValue(jsonString, new TypeReference<List<CenterPostEntity>>() {
        });
        List<Entity> centerEntityList = new ArrayList<>();
        for (int i = 0, len = centerList.size(); i < len; i++) {
            CenterPostEntity cPE = centerList.get(i);
            if (cPE.producerProfile.milkType == null || cPE.producerProfile.milkType.equalsIgnoreCase("")) {
                cPE.producerProfile.milkType = CattleType.COW;
            }
            CenterEntity centerEntity = new CenterEntity(centerList.get(i));
            centerEntity.chillingCenterId = String.valueOf(sessionManager.getSocietyColumnId());

            centerEntityList.add(centerEntity);
        }
        return centerEntityList;
    }

    @Override
    public String getDpnUrl() {
        if (Util.CHILLING_CENTER_URI != null && Util.CHILLING_CENTER_URI.contains("amcu")) {
            amcuConfig.setChillingCenterUri(Util.CHILLING_CENTER_URI);
            Util.CHILLING_CENTER_URI = null;
        }
        if (amcuConfig.getChillingUri() != null) {
            return amcuConfig.getChillingUri();
        }
        return null;
    }

    @Override
    public void resetDpnUrl() {
        amcuConfig.setCenterFailCount(0);
        amcuConfig.setChillingCenterUri(null);
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
            CenterEntity centerEntity = (CenterEntity) entityList.get(i);
            String msg = Util.getDuplicateIdOrBarCode(centerEntity.centerId, centerEntity.centerBarcode, mContext);
            if (msg != null && !msg.contains("Center")) {
                Util.displayErrorToast(msg, mContext);
                continue;
            }
            if (centerEntity.cattleType == null || centerEntity.cattleType.equalsIgnoreCase("")) {
                centerEntity.cattleType = CattleType.COW;
            }
            dao.saveOrUpdate(centerEntity);
        }
        Util.displayErrorToast("Collection Centers Updated Successfully", mContext);
    }
}
