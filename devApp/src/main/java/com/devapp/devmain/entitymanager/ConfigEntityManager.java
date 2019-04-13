package com.devapp.devmain.entitymanager;

import android.content.Context;
import android.content.Intent;

import com.devapp.devmain.agentfarmersplit.AppConstants;
import com.devapp.devmain.dao.ConfigurationDao;
import com.devapp.devmain.dao.Dao;
import com.devapp.devmain.dao.DaoFactory;
import com.devapp.devmain.devicemanager.ConfigurationManager;
import com.devapp.devmain.devicemanager.UserManager;
import com.devapp.devmain.entity.ConfigEntity;
import com.devapp.devmain.entity.ConfigurationConstants;
import com.devapp.devmain.entity.ConfigurationElement;
import com.devapp.devmain.entity.Entity;
import com.devapp.devmain.entity.ShiftEntity;
import com.devapp.devmain.helper.DatabaseEntity;
import com.devapp.devmain.network.EntityManager;
import com.devapp.devmain.network.InvalidUpdateException;
import com.devapp.devmain.postentities.CollectionConstants;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.SessionManager;
import com.devapp.devmain.services.UpdateFarmerService;
import com.devapp.devmain.user.UserDetails;
import com.devapp.devmain.user.Util;
import com.devapp.devmain.util.config.AppConfig;
import com.devapp.devmain.util.config.DefaultConfigurationHandler;
import com.devapp.smartcc.entityandconstants.SmartCCUtil;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

/**
 * Created by x on 5/2/18.
 */

public class ConfigEntityManager implements EntityManager {
    private final String TYPE = AppConstants.ConfigurationTypes.CONFIGURATION;
    private ConfigurationDao dao;
    private AmcuConfig amcuConfig;
    private SessionManager sessionManager;
    private Context mContext;
    private List<String> userKeys = new ArrayList<>();
    private ConfigurationManager configurationManager;

    public ConfigEntityManager(Context context) {
        mContext = context;
        amcuConfig = AmcuConfig.getInstance();
        sessionManager = new SessionManager(mContext);
        dao = (ConfigurationDao) DaoFactory.getDao(CollectionConstants.CONFIGURATION);
        String[] keys = new String[]{"managerPassword", "managerEmailID", "managerMobileNumber", "operatorPassword", "operatorMobileNumber"};
        userKeys.addAll(Arrays.asList(keys));
        configurationManager = new ConfigurationManager(mContext);
    }


    @Override
    public List<Entity> getEntityFromJson(String jsonString, boolean isCloudPush) throws IOException, InvalidUpdateException {
        ObjectMapper mapper = new ObjectMapper();
        List<Entity> configEntityList = mapper.readValue(jsonString, new TypeReference<List<ConfigEntity>>() {
        });
        List<Entity> entityList = new ArrayList<>();
        for (int i = 0, len = configEntityList.size(); i < len; i++) {
            entityList.add(getEntities((ConfigEntity) configEntityList.get(i)));
        }
        return entityList;
    }

    @Override
    public String getDpnUrl() {
        if (Util.CONFIGURATION_URI != null && Util.CONFIGURATION_URI.contains("amcu")) {
            amcuConfig.setConfigurationURI(Util.CONFIGURATION_URI);
            Util.CONFIGURATION_URI = null;
        }
        if (amcuConfig.getConfigurationURI() != null) {
            return amcuConfig.getConfigurationURI();
        }
        return null;
    }

    @Override
    public void resetDpnUrl() {
        amcuConfig.setConfigurationURI(null);
    }

    @Override
    public String getType() {
        return null;
    }

    @Override
    public Dao getDao() {
        return null;
    }

    @Override
    public void saveAll(List<? extends Entity> entityList, boolean isCloudPush) {
        ConfigurationManager configurationManager = new ConfigurationManager(mContext);
        Properties config = DefaultConfigurationHandler.getInstance().getConfig();
        int oldFarmerIdLength = 4;
        for (int i = 0, len = entityList.size(); i < len; i++) {
            ConfigurationElement cel = (ConfigurationElement) entityList.get(i);
            String key = cel.key;
            String value = cel.value;

            if (cel.key.equalsIgnoreCase(AppConfig.Key.simlockPassword.getName())) {
                configurationManager.setSimPassword();
                continue;
            } else if (key.equalsIgnoreCase(AppConfig.Key.COLLECTION_CENTER_MORNING_SHIFT_START_TIME.getName()) ||
                    key.equalsIgnoreCase(AppConfig.Key.COLLECTION_CENTER_MORNING_SHIFT_END_TIME.getName()) ||
                    key.equalsIgnoreCase(AppConfig.Key.COLLECTION_CENTER_EVENING_SHIFT_START_TIME.getName()) ||
                    key.equalsIgnoreCase(AppConfig.Key.COLLECTION_CENTER_EVENING_SHIFT_END_TIME.getName())) {
                SmartCCUtil smartCCUtil = new SmartCCUtil(mContext);
                ShiftEntity shiftEntity = configurationManager.initializeShiftEntity();
                if (key.equalsIgnoreCase(AppConfig.Key.COLLECTION_CENTER_MORNING_SHIFT_START_TIME.getName())) {
                    if (value != null && value.contains(":")) {
                        shiftEntity.morningCollectionStartTime = smartCCUtil.getTimeinLong(value);
                    }
                } else if (key.equalsIgnoreCase(AppConfig.Key.COLLECTION_CENTER_MORNING_SHIFT_END_TIME.getName())) {
                    if (value != null && value.contains(":")) {
                        shiftEntity.morningCollectionEndTime = smartCCUtil.getTimeinLong(value);
                    }
                } else if (key.equalsIgnoreCase(AppConfig.Key.COLLECTION_CENTER_EVENING_SHIFT_START_TIME.getName())) {
                    if (value != null && value.contains(":")) {
                        shiftEntity.eveningCollectionStartTime = smartCCUtil.getTimeinLong(value);
                    }
                } else if (key.equalsIgnoreCase(AppConfig.Key.COLLECTION_CENTER_EVENING_SHIFT_END_TIME.getName())) {
                    if (value != null && value.contains(":")) {
                        shiftEntity.eveningCollectionEndtime = smartCCUtil.getTimeinLong(value);
                    }
                }
                if (shiftEntity.morningCollectionStartTime >= shiftEntity.morningCollectionEndTime
                        || shiftEntity.morningCollectionStartTime <= shiftEntity.morningStartTime) {

                    Util.displayErrorToast("Invalid collection time!", mContext);
                    continue;
                } else if (shiftEntity.eveningCollectionStartTime <= shiftEntity.morningCollectionEndTime
                        || shiftEntity.eveningCollectionStartTime >= shiftEntity.eveningCollectionEndtime) {
                    Util.displayErrorToast("Invalid collection time!", mContext);
                    continue;
                } else if (shiftEntity.morningCollectionEndTime >= shiftEntity.eveningStartTime
                        || shiftEntity.eveningCollectionEndtime >= smartCCUtil.getTimeinLong("23:59")) {
                    Util.displayErrorToast("Invalid collection time!", mContext);
                    continue;
                }
            } else if (key.equalsIgnoreCase(AppConfig.Key.farmIdDigit.getName())) {
                oldFarmerIdLength = amcuConfig.getFarmerIdDigit();
            } else if (key.equalsIgnoreCase(AppConfig.Key.shutDownDelay.getName())) {
                ((ConfigurationElement) entityList.get(i)).value = String.valueOf(Util.getLongFromTime(value));
                cel.value = String.valueOf(Util.getLongFromTime(value));
            }


            dao.saveOrUpdate(entityList.get(i));
            config.put(cel.key, cel.value);


            if (userKeys.contains(cel.getKey())) {
                if (cel.key.startsWith("manager")) {
                    configurationManager.setUserEntity(UserDetails.MANAGER, cel.getKey(), cel.getValue());
                } else if (cel.key.startsWith("operator")) {
                    configurationManager.setUserEntity(UserDetails.OPERATOR, cel.getKey(), cel.getValue());
                }
            } else if (cel.key.startsWith("center")) {
                configurationManager.setDataForSociety(cel.getKey(), cel.getValue());
            } else if (cel.key.equalsIgnoreCase(ConfigurationConstants.ADDITIONAL_PARAMS)) {
                configurationManager.setCustomFieldEntitiesFromJson(cel.getValue());
            } else if (cel.key.equalsIgnoreCase("userConfig")) {
                UserManager userManager = new UserManager(mContext);
                userManager.afterGettingJSONArray(cel.getValue());
            } else if (key.equalsIgnoreCase(AppConfig.Key.farmIdDigit.getName())) {
                Intent intent = new Intent(mContext, UpdateFarmerService.class);
                intent.putExtra(UpdateFarmerService.FROM_CONFIGURATION, true);
                intent.putExtra("DigitLength", oldFarmerIdLength);
                mContext.startService(intent);
                Util.displayErrorToast("Farmers length configuration getting updated!.", mContext);
            }
        }
        Util.displayErrorToast("Configurations Updated Successfully", mContext);
    }

    private ConfigurationElement getEntities(ConfigEntity entity) {
        ConfigurationElement cel = (ConfigurationElement) dao.findById(entity.name);
        ConfigurationElement configEntity = new ConfigurationElement();
        configEntity.key = entity.name;
        configEntity.value = entity.value;
        configEntity.status = DatabaseEntity.FARMER_UNSENT_CODE;
        configEntity.userName = sessionManager.getUserId();
        configEntity.modifiedDate = Calendar.getInstance().getTimeInMillis();
        if (cel != null && cel.value != null) {
            configEntity.lastValue = cel.value;
        } else {
            configEntity.lastValue = entity.value;
        }
        return configEntity;
    }

}
