package com.devapp.devmain.entitymanager;

import android.content.Context;

import com.devapp.devmain.agentfarmersplit.AppConstants;
import com.devapp.devmain.dao.Dao;
import com.devapp.devmain.dao.DaoFactory;
import com.devapp.devmain.dao.DbTransaction;
import com.devapp.devmain.dao.RateChartNameDao;
import com.devapp.devmain.dao.RateDao;
import com.devapp.devmain.entity.Entity;
import com.devapp.devmain.entity.RateChartEntity;
import com.devapp.devmain.entity.RateChartPostEntity;
import com.devapp.devmain.entity.RatechartDetailsEnt;
import com.devapp.devmain.entity.UpdateStatus;
import com.devapp.devmain.network.EntityManager;
import com.devapp.devmain.network.InvalidUpdateException;
import com.devapp.devmain.postentities.CollectionConstants;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.SessionManager;
import com.devapp.devmain.tableEntities.RateChartNameTable;
import com.devapp.devmain.tableEntities.RateTable;
import com.devapp.devmain.user.Util;
import com.devapp.devmain.util.logger.Log;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by x on 1/2/18.
 */

public class RateChartManager implements EntityManager {

    private final String TYPE = AppConstants.ConfigurationTypes.RATE_CHART;

    private RateChartNameDao rateChartNameDao;
    private RateDao rateDao;
    private AmcuConfig amcuConfig;
    private Context mContext;
    private SessionManager session;

    public RateChartManager(Context context) {
        mContext = context;
        amcuConfig = AmcuConfig.getInstance();
        session = new SessionManager(mContext);

        rateChartNameDao = (RateChartNameDao) DaoFactory.getDao(CollectionConstants.RATECHART_NAME);
        rateDao = (RateDao) DaoFactory.getDao(CollectionConstants.RATES);
    }

    @Override
    public List<Entity> getEntityFromJson(String jsonString, boolean isCloudPush) throws IOException, InvalidUpdateException {

        Log.v(Log.RATECHART, "Starting parse rate json: " + System.currentTimeMillis());
        ObjectMapper mapper = new ObjectMapper();
        List<Entity> rateChartList = mapper.readValue(jsonString,
                new TypeReference<List<RateChartPostEntity>>() {
                });
        Log.v(Log.RATECHART, "Parsed rate elements from json: " + System.currentTimeMillis());
        for (int i = 0, len = rateChartList.size(); i < len; i++) {
            RateChartPostEntity rateChartPostEntity = (RateChartPostEntity) rateChartList.get(i);
            rateChartPostEntity.milkType = rateChartPostEntity.milkType.toUpperCase();
            rateChartPostEntity.rateChartName = rateChartPostEntity.rateChartName.toUpperCase();
            List<RateChartEntity> rateList = rateChartPostEntity.valuesList;
            for (int j = 0, size = rateList.size(); j < size; j++) {
                RateChartEntity rateChart = rateList.get(j);
                rateChart.endDate = Util.getDateAndTimeFromMilli(rateChartPostEntity.endDate, 0);
                rateChart.startDate = Util.getDateAndTimeFromMilli(rateChartPostEntity.startDate, 0);
                rateChart.milkType = rateChartPostEntity.milkType.toUpperCase(Locale.ENGLISH);

                rateChart.societyId = String
                        .valueOf(session.getSocietyColumnId());
            }
            if (null != rateChartPostEntity.societyId && null != session.getCollectionID()) {
                String centerIdInUpperCase = rateChartPostEntity.societyId.toUpperCase();
                String sessionIDUpperCase = session.getCollectionID();
                if (centerIdInUpperCase.matches("0*" + sessionIDUpperCase) || sessionIDUpperCase.matches("0*" + centerIdInUpperCase)) {
                } else {
                    String message = "Invalid rate chart for center " + session.getCollectionID();
                    Util.writeRatechartLogs(rateChartPostEntity, mContext, Util.INVALID, isCloudPush ? Util.CLOUD : Util.USB);
                    throw new InvalidUpdateException(message, UpdateStatus.INVALID_SOCIETY);
                }
            }
        }
        Log.v(Log.RATECHART, "Converted rate elements from json: " + System.currentTimeMillis());
        return rateChartList;
    }

    @Override
    public String getDpnUrl() {
        if (Util.RATECHART_URI != null && Util.RATECHART_URI.contains("amcu")) {
            amcuConfig.setUpdateUri(Util.RATECHART_URI);
            Util.RATECHART_URI = null;
        }
        if (amcuConfig.getUpdateURI() != null) {
            return amcuConfig.getUpdateURI();
        }
        return null;
    }

    @Override
    public void resetDpnUrl() {
        amcuConfig.setUpdateUri(null);
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public Dao getDao() {
        return null;
    }

    //TODO need to write the log only once
    @Override
    public void saveAll(List<? extends Entity> entityList, boolean isCloudPush) {
        ArrayList<RateChartPostEntity> rCPE = (ArrayList<RateChartPostEntity>) entityList;

        DbTransaction transaction = new DbTransaction();
        transaction.add(rateChartNameDao);
        transaction.add(rateDao);
        transaction.startTransaction();


        try {
            for (int i = 0, len = rCPE.size(); i < len; i++) {
                long refId = rateChartNameDao.findRateRefIdFromName(rCPE.get(i).rateChartName);
                RatechartDetailsEnt ratechartNameEntity = getRateChartDetailsEnt(rCPE.get(i));
                if (refId != -1) {
                    rateChartNameDao.deleteByKey(RateChartNameTable.NAME, rCPE.get(i).rateChartName);
                    rateDao.deleteByKey(RateTable.RATE_REF_ID, String.valueOf(refId));
                    Util.writeRatechartLogs(ratechartNameEntity, mContext, Util.DELETE, Util.DEVICE);
                }

                rateChartNameDao.save(ratechartNameEntity);
                refId = rateChartNameDao.findRateRefIdFromName(ratechartNameEntity.rateChartName);
                rateDao.saveWithRefId(rCPE.get(i).valuesList, refId);
                Util.writeRatechartLogs(ratechartNameEntity, mContext, Util.ADD,
                        isCloudPush ? Util.CLOUD : Util.USB);
            }
            transaction.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            transaction.endTransaction();
        }

    }

    public RatechartDetailsEnt getRateChartDetailsEnt(RateChartPostEntity rateChart) {
        RatechartDetailsEnt ratechEnt = new RatechartDetailsEnt();
        ratechEnt.rateChartName = rateChart.rateChartName;
        ratechEnt.rateValidityFrom = Util.getDateAndTimeFromMilli(rateChart.startDate, 0);
        ratechEnt.rateValidityTo = Util.getDateAndTimeFromMilli(rateChart.endDate, 0);
        ratechEnt.rateSocId = String.valueOf(session.getSocietyColumnId());
        ratechEnt.isActive = String.valueOf(rateChart.isActive);
        ratechEnt.rateMilkType = rateChart.milkType;
        ratechEnt.ratechartType = rateChart.rateChartType;
        ratechEnt.rateLvalidityFrom = rateChart.startDate;
        ratechEnt.rateLvalidityTo = rateChart.endDate;
        ratechEnt.ratechartShift = rateChart.shift;
        ratechEnt.rateOther1 = String.valueOf(Util.getLongDateAndTime());
        ratechEnt.modifiedTime = System.currentTimeMillis();
        return ratechEnt;
    }
}
