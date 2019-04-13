package com.devapp.devmain.entitymanager;

import android.content.Context;

import com.devapp.devmain.agentfarmersplit.AppConstants;
import com.devapp.devmain.dao.AgentDao;
import com.devapp.devmain.dao.Dao;
import com.devapp.devmain.dao.DaoFactory;
import com.devapp.devmain.entity.AgentPostEntity;
import com.devapp.devmain.entity.Entity;
import com.devapp.devmain.network.EntityManager;
import com.devapp.devmain.network.InvalidUpdateException;
import com.devapp.devmain.postentities.CollectionConstants;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.user.Util;
import com.devapp.smartcc.entities.AgentEntity;
import com.devapp.smartcc.entityandconstants.SmartCCConstants;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by x on 30/1/18.
 */

public class AgentManager implements EntityManager {

    private final String TYPE = AppConstants.ConfigurationTypes.AGENT;
    private AgentDao dao;
    private AmcuConfig amcuConfig;
    private Context mContext;

    public AgentManager(Context context) {
        mContext = context;
        amcuConfig = AmcuConfig.getInstance();
        dao = (AgentDao) DaoFactory.getDao(CollectionConstants.AGENT);
    }

    @Override
    public List<Entity> getEntityFromJson(String jsonString, boolean isCloudPush) throws IOException, InvalidUpdateException {
        ObjectMapper mapper = new ObjectMapper();
        List<AgentPostEntity> agentList = mapper.readValue(jsonString, new TypeReference<List<AgentPostEntity>>() {
        });
        List<Entity> agentEntityList = new ArrayList<>();
        for (int i = 0, len = agentList.size(); i < len; i++) {
            agentEntityList.add(new AgentEntity(agentList.get(i)));
        }
        return agentEntityList;
    }

    @Override
    public String getDpnUrl() {
        if (SmartCCConstants.AGENT_UPDATE_URI != null) {
            amcuConfig.setAgentUpdateUrl(SmartCCConstants.AGENT_UPDATE_URI);
            SmartCCConstants.AGENT_UPDATE_URI = null;
        }
        if (amcuConfig.getAgentUpdateUrl() != null) {
            return amcuConfig.getAgentUpdateUrl();
        }
        return null;
    }

    @Override
    public void resetDpnUrl() {
        amcuConfig.setAgentFailCount(0);
        amcuConfig.setAgentUpdateUrl(null);
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
        Util.displayErrorToast("Agent(s) Updated Successfully", mContext);
    }
}
