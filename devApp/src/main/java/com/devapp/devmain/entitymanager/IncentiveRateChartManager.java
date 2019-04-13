package com.devapp.devmain.entitymanager;

import android.content.Context;

import com.devapp.devmain.agentfarmersplit.AppConstants;
import com.devapp.devmain.dao.Dao;
import com.devapp.devmain.dao.DaoFactory;
import com.devapp.devmain.dao.DbTransaction;
import com.devapp.devmain.dao.IncentiveRateDao;
import com.devapp.devmain.dao.RateChartNameDao;
import com.devapp.devmain.entity.BonusChartEntity;
import com.devapp.devmain.entity.Entity;
import com.devapp.devmain.entity.IncentiveRateChartPostEntity;
import com.devapp.devmain.entity.RateChartPostEntity;
import com.devapp.devmain.entity.RatechartDetailsEnt;
import com.devapp.devmain.entity.UpdateStatus;
import com.devapp.devmain.helper.DatabaseEntity;
import com.devapp.devmain.network.EntityManager;
import com.devapp.devmain.network.InvalidUpdateException;
import com.devapp.devmain.postentities.CollectionConstants;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.SessionManager;
import com.devapp.devmain.tableEntities.RateChartNameTable;
import com.devapp.devmain.user.Util;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by x on 3/2/18.
 */

public class IncentiveRateChartManager implements EntityManager {

    private final String TYPE = AppConstants.ConfigurationTypes.COLLECTION_CENTER_LIST;
    private IncentiveRateDao incentiveRateDao;
    private RateChartNameDao rateChartNameDao;
    private AmcuConfig amcuConfig;
    private Context mContext;
    private SessionManager session;

    public IncentiveRateChartManager(Context context) {
        mContext = context;
        amcuConfig = AmcuConfig.getInstance();
        session = new SessionManager(mContext);
        incentiveRateDao = (IncentiveRateDao) DaoFactory.getDao(CollectionConstants.INCENTIVE_RATES);
        rateChartNameDao = (RateChartNameDao) DaoFactory.getDao(CollectionConstants.RATECHART_NAME);
    }

    @Override
    public List<Entity> getEntityFromJson(String jsonString, boolean isCloudPush) throws IOException, InvalidUpdateException {
        ObjectMapper mapper = new ObjectMapper();
        List<Entity> entities =
                mapper.readValue(jsonString, new TypeReference<List<IncentiveRateChartPostEntity>>() {
                });
        for (int i = 0, len = entities.size(); i < len; i++) {
            IncentiveRateChartPostEntity iRCPE = (IncentiveRateChartPostEntity) entities.get(i);
            iRCPE.name = iRCPE.name.toUpperCase();
            List<BonusChartEntity> rateCharts = iRCPE.valueList;
            for (int j = 0, len2 = rateCharts.size(); j < len2; j++) {
                BonusChartEntity bCE = rateCharts.get(j);
                bCE.endDate = Util.getDateAndTimeFromMilli(iRCPE.endDate, 0);
                bCE.startDate = Util.getDateAndTimeFromMilli(iRCPE.startDate, 0);
                bCE.milkType = iRCPE.milkType.toUpperCase();
                bCE.name = iRCPE.name.toUpperCase();
                bCE.societyId = iRCPE.societyId;
            }
            if (null != iRCPE.societyId && null != session.getCollectionID()) {
                String centerIdInUpperCase = iRCPE.societyId.toUpperCase();
                String sessionIDUpperCase = session.getCollectionID();
                if (centerIdInUpperCase.matches("0*" + sessionIDUpperCase) || sessionIDUpperCase.matches("0*" + centerIdInUpperCase)) {
                } else {
                    String message = "Invalid rate chart for center " + session.getCollectionID();
                    RateChartPostEntity rateChartPostEntity = new RateChartPostEntity();
                    rateChartPostEntity.rateChartName = iRCPE.name.toUpperCase();
                    rateChartPostEntity.startDate = iRCPE.startDate;
                    rateChartPostEntity.endDate = iRCPE.endDate;
                    Util.writeRatechartLogs(rateChartPostEntity, mContext, Util.INVALID, isCloudPush ? Util.CLOUD : Util.USB);
                    throw new InvalidUpdateException(message, UpdateStatus.INVALID_SOCIETY);
                }
            }
        }
        return entities;
    }

    @Override
    public String getDpnUrl() {
        if (Util.INCENTIVE_RATECHART_URI != null && Util.INCENTIVE_RATECHART_URI.contains("amcu")) {
            amcuConfig.setIncentiveUpdateUri(Util.INCENTIVE_RATECHART_URI);
            Util.INCENTIVE_RATECHART_URI = null;
        }
        if (amcuConfig.getIncentiveUpdateURI() != null) {
            return amcuConfig.getIncentiveUpdateURI();
        }
        return null;
    }

    @Override
    public void resetDpnUrl() {
        amcuConfig.setIncentiveUpdateUri(null);
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public Dao getDao() {
        return null;
    }

   /* @Override
    public void saveAll(List<? extends Entity> entityList, boolean isCloudPush) {
        dao.saveAll(entityList, isCloudPush);
    }*/

    @Override
    public void saveAll(List<? extends Entity> entityList, boolean isCloudPush) {
        ArrayList<IncentiveRateChartPostEntity> rCPE = (ArrayList<IncentiveRateChartPostEntity>) entityList;

        DbTransaction transaction = new DbTransaction();
        transaction.add(rateChartNameDao);
        transaction.add(incentiveRateDao);
        transaction.startTransaction();


        try {
            for (int i = 0, len = rCPE.size(); i < len; i++) {
                long refId = rateChartNameDao.findRateRefIdFromName(rCPE.get(i).name);
                RatechartDetailsEnt ratechartNameEntity = getRateChartDetailsEnt(rCPE.get(i));
                if (refId != -1) {
                    rateChartNameDao.deleteByKey(RateChartNameTable.NAME, rCPE.get(i).name);
                    incentiveRateDao.deleteByKey(DatabaseEntity.InCentive.RATE_CHART_NAME, rCPE.get(i).name);
                    Util.writeRatechartLogs(ratechartNameEntity, mContext, Util.DELETE, Util.DEVICE);
                }
                rateChartNameDao.save(ratechartNameEntity);
                incentiveRateDao.saveAll(rCPE.get(i).valueList);
                Util.writeRatechartLogs(ratechartNameEntity, mContext, Util.ADD,
                        isCloudPush ? Util.CLOUD : Util.USB);


            }
            transaction.setTransactionSuccessful();
        } catch (Exception e) {

        } finally {
            transaction.endTransaction();
        }

    }

    public RatechartDetailsEnt getRateChartDetailsEnt(IncentiveRateChartPostEntity rateChart) {
        RatechartDetailsEnt ratechEnt = new RatechartDetailsEnt();
        ratechEnt.rateChartName = rateChart.name;
        ratechEnt.rateValidityFrom = Util.getDateAndTimeFromMilli(rateChart.startDate, 0);
        ratechEnt.rateValidityTo = Util.getDateAndTimeFromMilli(rateChart.endDate, 0);
        ratechEnt.rateSocId = String.valueOf(session.getSocietyColumnId());
        ratechEnt.isActive = String.valueOf(rateChart.isActive);
        ratechEnt.rateMilkType = rateChart.milkType.toUpperCase();
        ratechEnt.ratechartType = rateChart.rateChartType;
        ratechEnt.rateLvalidityFrom = rateChart.startDate;
        ratechEnt.rateLvalidityTo = rateChart.endDate;
        ratechEnt.rateOther1 = String.valueOf(Util.getLongDateAndTime());
        ratechEnt.modifiedTime = System.currentTimeMillis();
        return ratechEnt;
    }

}
