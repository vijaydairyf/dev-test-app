package com.devapp.devmain.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.devapp.devmain.entity.Entity;
import com.devapp.devmain.entity.FarmerEntity;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.tableEntities.FarmerTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


/**
 * Created by x on 6/2/18.
 */

public class FarmerDao extends ProfileDao {

    public FarmerDao(SQLiteDatabase sqLiteDatabase) {
        super(sqLiteDatabase, FarmerTable.TABLE_FARMER);
    }

    public int getlastFamerId() {
        int id = 0;

        String selectQuery = null;
        selectQuery = " SELECT " + FarmerTable.KEY_FARMER_ID + " FROM " + FarmerTable.TABLE_FARMER +
                " WHERE " + DatabaseHandler.KEY_FARMER_COLUMNID + " NOT IN (SELECT " + DatabaseHandler.KEY_FARMER_COLUMNID +
                " FROM " + FarmerTable.TABLE_FARMER + " WHERE " + FarmerTable.KEY_FARMER_ID + " = '" + DatabaseHandler.DEFAULT_FARMER_ID + "')"
                + " ORDER BY " + "cast (" + FarmerTable.KEY_FARMER_ID + " as int)" + " DESC LIMIT 1 ";
        Cursor cursor = sqLiteDatabase.rawQuery(selectQuery, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                id = Integer.parseInt(cursor.getString(0));

            }
        }
        return id;

    }

    public ArrayList<FarmerEntity> findAllOrderByFarmerId() {
        String query = "SELECT * FROM " + FarmerTable.TABLE_FARMER + " ORDER BY " + FarmerTable.KEY_FARMER_ID;
        ArrayList<FarmerEntity> entities = (ArrayList<FarmerEntity>) findByQuery(query);
        return entities;
    }

    public ArrayList<FarmerEntity> findAllByMemberType(String type) {
        String selectQuery = "Select * From " + FarmerTable.TABLE_FARMER + " Where " + FarmerTable.KEY_FARMER_TYPE +
                " = '" + type + "'";
        return (ArrayList<FarmerEntity>) findByQuery(selectQuery);
    }

    public ArrayList<FarmerEntity> findAllByMemberAndCattleType(String type, String cattleType) {
        String selectQuery = "Select * From " + FarmerTable.TABLE_FARMER + " Where " +
                FarmerTable.KEY_FARMER_TYPE +
                " = '" + type + "'" + " AND " + FarmerTable.KEY_FARMER_CATTLE + " ='" + cattleType + "'";
        return (ArrayList<FarmerEntity>) findByQuery(selectQuery);
    }

    public ArrayList<FarmerEntity> findAllByStatus(int status) {
        String query = "SELECT * FROM " + tableName + " WHERE " + FarmerTable.KEY_FARMER_SENT + " = " + status;
        return (ArrayList<FarmerEntity>) findByQuery(query);
    }

    public ArrayList<FarmerEntity> findAllByAgentId(String agentId) {
        ArrayList<FarmerEntity> farmerEntities = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        map.put(FarmerTable.KEY_AGENT_ID, agentId);
        farmerEntities = (ArrayList<FarmerEntity>) findByKey(map);
        return farmerEntities;

    }

    public ArrayList<String> getAllFarmerIds() {
        ArrayList<String> idList = new ArrayList<>();
        ArrayList<FarmerEntity> farmerEntities = (ArrayList<FarmerEntity>) findAll();
        for (int i = 0, len = farmerEntities.size(); i < len; i++) {
            idList.add(farmerEntities.get(i).farmer_id);
        }
        return idList;
    }

    public ArrayList<String> getAllSplitFarmerIds() {

        ArrayList<String> idList = new ArrayList<>();
        String query = "SELECT * FROM " + tableName + " WHERE "
                + FarmerTable.KEY_AGENT_ID + " != 'NA' AND " + FarmerTable.KEY_AGENT_ID
                + " IS NOT NULL";
        ArrayList<FarmerEntity> farmerEntities = (ArrayList<FarmerEntity>) findByQuery(query);
        for (int i = 0, len = farmerEntities.size(); i < len; i++) {
            idList.add(farmerEntities.get(i).farmer_id);
        }
        return idList;
    }


    public ArrayList<String> getAllBarcodes() {
        ArrayList<String> barcodeList = new ArrayList<>();
        ArrayList<FarmerEntity> farmerEntities = (ArrayList<FarmerEntity>) findAll();
        for (int i = 0, len = farmerEntities.size(); i < len; i++) {
            barcodeList.add(farmerEntities.get(i).farmer_barcode);
        }
        return barcodeList;
    }

    public ArrayList<String> getAllFarmerIdByAgentId(String agentId) {
        String query = "SELECT * FROM " + tableName + " WHERE "
                + FarmerTable.KEY_AGENT_ID + "='" + agentId + "'";
        ArrayList<FarmerEntity> farmerEntities = (ArrayList<FarmerEntity>) findByQuery(query);
        ArrayList<String> farmerIds = new ArrayList<>();
        for (int i = 0, len = farmerEntities.size(); i < len; i++) {
            farmerIds.add(farmerEntities.get(i).farmer_id);
        }
        return farmerIds;
    }

    public void deleteByBarcode(String barcode) {
        deleteByKey(FarmerTable.KEY_FARMER_BARCODE, barcode);
    }

    public void deleteByCenterId(String centerId) {
        deleteByKey(FarmerTable.KEY_FARMER_SOCCODE, centerId);
    }

    public long deleteByFarmerId(String farmerId) {
        return deleteByKey(FarmerTable.KEY_FARMER_ID, farmerId);
    }

    public FarmerEntity findByBarcode(String barcode) {
        Map<String, String> map = new HashMap<>();
        map.put(FarmerTable.KEY_FARMER_BARCODE, barcode);
        List<FarmerEntity> farmerEntities = (ArrayList<FarmerEntity>) findByKey(map);
        if (farmerEntities != null && farmerEntities.size() > 0) {
            return farmerEntities.get(0);
        } else {
            return null;
        }
    }

    @Override
    ContentValues getContentValues(Entity entity) {

        FarmerEntity farmerEntity = null;

        if (entity instanceof FarmerEntity) {

            farmerEntity = (FarmerEntity) entity;
        } else {
            return null;
        }

        ContentValues values = new ContentValues();

        values.put(FarmerTable.KEY_FARMER_NAME, farmerEntity.farmer_name);
        values.put(FarmerTable.KEY_FARMER_CANS, farmerEntity.farmer_cans);
        values.put(FarmerTable.KEY_FARMER_SOCCODE, farmerEntity.soc_code);
        values.put(FarmerTable.KEY_FARMER_MOBILE, farmerEntity.farm_mob);
        values.put(FarmerTable.KEY_FARMER_EMAIL, farmerEntity.farm_email);
        values.put(FarmerTable.KEY_FARMER_CATTLE, farmerEntity.farmer_cattle.toUpperCase(Locale.ENGLISH));

        values.put(FarmerTable.KEY_FARMER_COW, farmerEntity.numCow);
        values.put(FarmerTable.KEY_FARMER_BUFFALO, farmerEntity.numBuff);
        values.put(FarmerTable.KEY_FARMER_NO_CATTLE, farmerEntity.numCattle);
        values.put(FarmerTable.KEY_FARMER_ASSIGN_RATECHART, farmerEntity.assignRatechart);

        values.put(FarmerTable.KEY_FARMER_REGISTRATION_DATE, farmerEntity.farmer_regDate);
        values.put(FarmerTable.KEY_ENABLE_MULTIPLECANS, String.valueOf(farmerEntity.isMultipleCans));

        values.put(FarmerTable.KEY_FARMER_BARCODE, farmerEntity.farmer_barcode);
        values.put(FarmerTable.KEY_FARMER_ID, farmerEntity.farmer_id);
        values.put(FarmerTable.KEY_FARMER_SENT, farmerEntity.sentStatus);
        values.put(FarmerTable.KEY_AGENT_ID, farmerEntity.agentId);
        values.put(FarmerTable.KEY_FARMER_TYPE, farmerEntity.farmerType);

        return values;

    }

    @Override
    Entity getEntityFromCursor(Cursor cursor) {
        FarmerEntity farmerEntity = new FarmerEntity();

        farmerEntity.assignRatechart = cursor.getString(cursor.getColumnIndex(
                FarmerTable.KEY_FARMER_ASSIGN_RATECHART));
        farmerEntity.farm_email = cursor.getString(cursor.getColumnIndex(FarmerTable.KEY_FARMER_EMAIL));
        farmerEntity.farm_mob = cursor.getString(cursor.getColumnIndex(FarmerTable.KEY_FARMER_MOBILE));
        farmerEntity.farmer_barcode = cursor.getString(cursor.getColumnIndex(FarmerTable.KEY_FARMER_BARCODE));
        farmerEntity.farmer_cans = cursor.getString(cursor.getColumnIndex(FarmerTable.KEY_FARMER_CANS));
        farmerEntity.farmer_cattle = cursor.getString(cursor.getColumnIndex(FarmerTable.KEY_FARMER_CATTLE));
        farmerEntity.farmer_id = cursor.getString(cursor.getColumnIndex(FarmerTable.KEY_FARMER_ID));
        farmerEntity.farmer_name = cursor.getString(cursor.getColumnIndex(FarmerTable.KEY_FARMER_NAME));
        farmerEntity.farmer_regDate = cursor.getLong(cursor.getColumnIndex(
                FarmerTable.KEY_FARMER_REGISTRATION_DATE));
        farmerEntity.isActive = true;
        farmerEntity.isMultipleCans = Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(
                FarmerTable.KEY_ENABLE_MULTIPLECANS)));
        farmerEntity.numBuff = cursor.getString(cursor.getColumnIndex(FarmerTable.KEY_FARMER_BUFFALO));
        farmerEntity.numCattle = cursor.getString(cursor.getColumnIndex(FarmerTable.KEY_FARMER_NO_CATTLE));
        farmerEntity.numCow = cursor.getString(cursor.getColumnIndex(FarmerTable.KEY_FARMER_COW));
        farmerEntity.soc_code = cursor.getString(cursor.getColumnIndex(FarmerTable.KEY_FARMER_SOCCODE));
        farmerEntity.sentStatus = cursor.getInt(cursor.getColumnIndex(FarmerTable.KEY_FARMER_SENT));
        farmerEntity.agentId = cursor.getString(cursor.getColumnIndex(FarmerTable.KEY_AGENT_ID));
        farmerEntity.farmerType = cursor.getString(cursor.getColumnIndex(FarmerTable.KEY_FARMER_TYPE));

        return farmerEntity;
    }

    @Override
    String getEntityIdColumnName() {
        return FarmerTable.KEY_FARMER_ID;
    }
}
