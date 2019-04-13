package com.devapp.devmain.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.devapp.devmain.entity.Entity;
import com.devapp.devmain.entity.RatechartDetailsEnt;
import com.devapp.devmain.tableEntities.RateChartNameTable;
import com.devapp.devmain.user.Util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by x on 30/7/18.
 */

public class RateChartNameDao extends ProfileDao {
    RateChartNameDao(SQLiteDatabase sqLiteDatabase) {
        super(sqLiteDatabase, RateChartNameTable.TABLE_NAME);
    }

    @Override
    ContentValues getContentValues(Entity entity) {
        RatechartDetailsEnt rateChartEntity = null;
        if (entity instanceof RatechartDetailsEnt) {
            rateChartEntity = (RatechartDetailsEnt) entity;
        } else {
            return null;
        }
        ContentValues values = new ContentValues();
        values.put(RateChartNameTable.MILKTYPE, rateChartEntity.rateMilkType);
        values.put(RateChartNameTable.OTHER1, rateChartEntity.rateOther1);
        values.put(RateChartNameTable.OTHER2, rateChartEntity.rateOther2);
        values.put(RateChartNameTable.VALIDFROM, rateChartEntity.rateValidityFrom);
        values.put(RateChartNameTable.VALIDTO, rateChartEntity.rateValidityTo);
        values.put(RateChartNameTable.SOCID, rateChartEntity.rateSocId);
        values.put(RateChartNameTable.LVALIDITYFROM, rateChartEntity.rateLvalidityFrom);
        values.put(RateChartNameTable.LVALIDITYTO, rateChartEntity.rateLvalidityTo);
        values.put(RateChartNameTable.ISACTIVE, rateChartEntity.isActive);
        values.put(RateChartNameTable.TYPE, rateChartEntity.ratechartType);
        values.put(RateChartNameTable.NAME, rateChartEntity.rateChartName);
        values.put(RateChartNameTable.RATE_SHIFT, rateChartEntity.ratechartShift);
        values.put(RateChartNameTable.MODIFIED_TIME, rateChartEntity.modifiedTime);
        return values;
    }

    @Override
    Entity getEntityFromCursor(Cursor cursor) {
        RatechartDetailsEnt rateChartEntity = new RatechartDetailsEnt();

        rateChartEntity.columnId = cursor.getLong(cursor.getColumnIndex(RateChartNameTable.COLUMNID));
        rateChartEntity.rateChartName = cursor.getString(cursor.getColumnIndex(RateChartNameTable.NAME));
        rateChartEntity.rateValidityFrom = cursor.getString(cursor.getColumnIndex(RateChartNameTable.VALIDFROM));
        rateChartEntity.rateValidityTo = cursor.getString(cursor.getColumnIndex(RateChartNameTable.VALIDTO));
        rateChartEntity.rateMilkType = cursor.getString(cursor.getColumnIndex(RateChartNameTable.MILKTYPE));
        rateChartEntity.rateOther1 = cursor.getString(cursor.getColumnIndex(RateChartNameTable.OTHER1));
        rateChartEntity.rateOther2 = cursor.getString(cursor.getColumnIndex(RateChartNameTable.OTHER2));
        rateChartEntity.rateSocId = cursor.getString(cursor.getColumnIndex(RateChartNameTable.SOCID));
        rateChartEntity.isActive = cursor.getString(cursor.getColumnIndex(RateChartNameTable.ISACTIVE));
        rateChartEntity.rateLvalidityFrom = cursor.getLong(cursor.getColumnIndex(RateChartNameTable.LVALIDITYFROM));
        rateChartEntity.rateLvalidityTo = cursor.getLong(cursor.getColumnIndex(RateChartNameTable.LVALIDITYTO));
        rateChartEntity.ratechartType = cursor.getString(cursor.getColumnIndex(RateChartNameTable.TYPE));
        rateChartEntity.ratechartShift = cursor.getString(cursor.getColumnIndex(RateChartNameTable.RATE_SHIFT));
        rateChartEntity.modifiedTime = cursor.getLong(cursor.getColumnIndex(RateChartNameTable.MODIFIED_TIME));
        return rateChartEntity;
    }

    @Override
    String getEntityIdColumnName() {
        return RateChartNameTable.NAME;
    }

    public RatechartDetailsEnt findByName(String name) {
        Map<Key, Object> map = new LinkedHashMap<>();
        map.put(new Key(RateChartNameTable.NAME, FilterType.EQUALS), name);
        List<RatechartDetailsEnt> ratechartDetailsEntList = (List<RatechartDetailsEnt>) findByFilter(map);
        RatechartDetailsEnt rde = null;

        if (ratechartDetailsEntList != null && ratechartDetailsEntList.size() > 0) {
            rde = ratechartDetailsEntList.get(0);
        }

        return rde;
    }

    public long findRateRefIdFromName(String name) {
        long id = -1;

        RatechartDetailsEnt ratechartDetailsEnt = findByName(name);

        if (ratechartDetailsEnt != null) {
            id = ratechartDetailsEnt.columnId;
        }

        return id;

    }

    public ArrayList<RatechartDetailsEnt> findRateChartFromInputs(String name, String milkType,
                                                                  String rateChartType) {
        Map<Key, Object> map = new LinkedHashMap<>();
        map.put(new Key(RateChartNameTable.NAME, FilterType.EQUALS), name);
        map.put(new Key(RateChartNameTable.MILKTYPE, FilterType.EQUALS), milkType);
        map.put(new Key(RateChartNameTable.TYPE, FilterType.EQUALS), rateChartType);
        map.put(new Key(RateChartNameTable.LVALIDITYFROM, FilterType.ORDER_BY), FilterType.Order.ASC);

        List<RatechartDetailsEnt> ratechartDetailsEntList = (List<RatechartDetailsEnt>) findByFilter(map);

        return (ArrayList<RatechartDetailsEnt>) ratechartDetailsEntList;


    }

    public ArrayList<RatechartDetailsEnt> findLatestRateChartForGivenTime(
            String milkType, String rateChartType, long time, String shift) {

        Map<Key, Object> map = new LinkedHashMap<>();

        map.put(new Key(RateChartNameTable.MILKTYPE, FilterType.EQUALS), milkType);
        map.put(new Key(RateChartNameTable.RATE_SHIFT, FilterType.BOTH), (rateChartType.equalsIgnoreCase(Util.RATECHART_TYPE_DYNAMIC) ?
                shift : null));
        map.put(new Key(RateChartNameTable.TYPE, FilterType.EQUALS), rateChartType);
        map.put(new Key(RateChartNameTable.LVALIDITYFROM, FilterType.LESS_THAN_OR_EQUALS), String.valueOf(time));
        map.put(new Key(RateChartNameTable.LVALIDITYFROM, FilterType.ORDER_BY), FilterType.Order.DESC);
        map.put(new Key(RateChartNameTable.MODIFIED_TIME, FilterType.ORDER_BY), FilterType.Order.DESC);

        List<RatechartDetailsEnt> ratechartDetailsEntList = (List<RatechartDetailsEnt>) findByFilter(map);
        return (ArrayList<RatechartDetailsEnt>) ratechartDetailsEntList;
    }




 /*   public void saveAll(List<? extends Entity> entityList, boolean isCloudPush) {
        Util.WRITE_RATE_ADD_LOG = true;
        ArrayList<RateChartPostEntity> rCPE = (ArrayList<RateChartPostEntity>) entityList;
        for (int i = 0, len = rCPE.size(); i < len; i++) {
            dbHandler.addCompleteRateChart(getRateChartDetailsEnt(rCPE.get(i)),
                    (ArrayList<RateChartEntity>) rCPE.get(i).valuesList, DatabaseHandler.isPrimary,
                    isCloudPush ? Util.CLOUD : Util.USB);
        }
    }*/

}
