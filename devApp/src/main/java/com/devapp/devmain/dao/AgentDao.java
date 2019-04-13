package com.devapp.devmain.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.devapp.devmain.entity.Entity;
import com.devapp.smartcc.database.AgentDatabase;
import com.devapp.smartcc.entities.AgentEntity;
import com.devapp.smartcc.entityandconstants.SmartCCConstants;

import java.util.ArrayList;

/**
 * Created by x on 6/2/18.
 */

public class AgentDao extends ProfileDao {

    public AgentDao(SQLiteDatabase sqLiteDatabase) {
        super(sqLiteDatabase, AgentDatabase.TABLE_AGENT);
    }

    @Override
    ContentValues getContentValues(Entity entity) {
        AgentEntity agentEntity = null;
        if (entity instanceof AgentEntity) {

            agentEntity = (AgentEntity) entity;
        } else {
            return null;
        }
        ContentValues values = new ContentValues();
        values.put(AgentDatabase.KEY_AGENT_ID, agentEntity.agentID);
        values.put(AgentDatabase.KEY_AGENT_BARCODE, agentEntity.barcode);
        values.put(AgentDatabase.KEY_AGENT_CATTLE_TYPE, agentEntity.milkType);
        values.put(AgentDatabase.KEY_AGENT_MOBILE_NUM, agentEntity.mobileNum);
        values.put(AgentDatabase.KEY_AGENT_PHONE_NUM, agentEntity.phoneNum);
        values.put(AgentDatabase.KEY_AGENT_FIRST_NAME, agentEntity.firstName);
        values.put(AgentDatabase.KEY_AGENT_LAST_NAME, agentEntity.lastName);
        values.put(AgentDatabase.KEY_AGENT_REG_DATE, agentEntity.registeredDate);
        values.put(AgentDatabase.KEY_AGENT_NUMBER_OF_CANS, agentEntity.numCans);
        values.put(AgentDatabase.KEY_DISTANCE_TO_DELIVERY, agentEntity.distanceToDelivery);
        values.put(AgentDatabase.KEY_PICKUP_POINT, agentEntity.pickupPoint);
        values.put(AgentDatabase.KEY_UNIQUE_ID1, agentEntity.uniqueID1);
        values.put(AgentDatabase.KEY_UNIQUE_ID2, agentEntity.uniqueID2);
        values.put(AgentDatabase.KEY_UNIQUE_ID3, agentEntity.uniqueID3);
        values.put(AgentDatabase.KEY_SHIFT_SUPPLYING_TO, agentEntity.shiftsSupplyingTo);

        String centerList = "";
        if (agentEntity.centerList != null && agentEntity.centerList.size() > 0) {
            for (int j = 0; j < agentEntity.centerList.size(); j++) {
                centerList = agentEntity.centerList.get(j) + SmartCCConstants.LIST_SEPARATOR;
            }
        }
        values.put(AgentDatabase.KEY_AGENT_ASSOCIATED_MCC, centerList);
        return values;
    }

    @Override
    Entity getEntityFromCursor(Cursor cursor) {

        AgentEntity agentEntity = new AgentEntity();

        agentEntity.barcode = cursor.getString(cursor.getColumnIndex(AgentDatabase.KEY_AGENT_BARCODE));
        agentEntity.firstName = cursor.getString(cursor.getColumnIndex(AgentDatabase.KEY_AGENT_FIRST_NAME));
        agentEntity.lastName = cursor.getString(cursor.getColumnIndex(AgentDatabase.KEY_AGENT_LAST_NAME));
        agentEntity.phoneNum = cursor.getString(cursor.getColumnIndex(AgentDatabase.KEY_AGENT_PHONE_NUM));
        agentEntity.mobileNum = cursor.getString(cursor.getColumnIndex(AgentDatabase.KEY_AGENT_MOBILE_NUM));
        agentEntity.milkType = cursor.getString(cursor.getColumnIndex(AgentDatabase.KEY_AGENT_CATTLE_TYPE));
        agentEntity.registeredDate = cursor.getLong(cursor.getColumnIndex(AgentDatabase.KEY_AGENT_REG_DATE));
        agentEntity.uniqueID1 = cursor.getString(cursor.getColumnIndex(AgentDatabase.KEY_UNIQUE_ID1));
        agentEntity.uniqueID2 = cursor.getString(cursor.getColumnIndex(AgentDatabase.KEY_UNIQUE_ID2));
        agentEntity.uniqueID3 = cursor.getString(cursor.getColumnIndex(AgentDatabase.KEY_UNIQUE_ID3));
        agentEntity.numCans = cursor.getInt(cursor.getColumnIndex(AgentDatabase.KEY_AGENT_NUMBER_OF_CANS));
        agentEntity.shiftsSupplyingTo = cursor.getString(cursor.getColumnIndex(AgentDatabase.KEY_SHIFT_SUPPLYING_TO));
        agentEntity.routeID = cursor.getString(cursor.getColumnIndex(AgentDatabase.KEY_AGENT_ID));
        agentEntity.pickupPoint = cursor.getString(cursor.getColumnIndex(AgentDatabase.KEY_PICKUP_POINT));
        agentEntity.distanceToDelivery = cursor.getDouble(cursor.getColumnIndex(AgentDatabase.KEY_DISTANCE_TO_DELIVERY));

        String centerListStr = cursor.getString(cursor.getColumnIndex(AgentDatabase.KEY_AGENT_ASSOCIATED_MCC));
        ArrayList<String> allCenterList = new ArrayList<>();
        String[] strArray = centerListStr.split(SmartCCConstants.LIST_SEPARATOR);

        for (int j = 0; j < strArray.length; j++) {
            allCenterList.add(strArray[j]);

        }
        agentEntity.centerList = allCenterList;
        return agentEntity;
    }

    @Override
    String getEntityIdColumnName() {
        return AgentDatabase.KEY_AGENT_ID;
    }
}
