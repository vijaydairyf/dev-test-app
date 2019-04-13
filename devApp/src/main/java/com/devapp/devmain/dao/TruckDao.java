package com.devapp.devmain.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.devapp.devmain.entity.Entity;
import com.devapp.smartcc.database.TruckCCDatabase;
import com.devapp.smartcc.entities.TrucKInfo;
import com.devapp.smartcc.entityandconstants.SmartCCConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by x on 6/2/18.
 */

public class TruckDao extends ProfileDao {

    public TruckDao(SQLiteDatabase sqLiteDatabase) {
        super(sqLiteDatabase, TruckCCDatabase.TABLE_TRUCK_INFO);
    }

    @Override
    ContentValues getContentValues(Entity entity) {
        TrucKInfo truckInfo = null;

        if (entity instanceof TrucKInfo) {

            truckInfo = (TrucKInfo) entity;
        } else {
            return null;
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(TruckCCDatabase.KEY_TRUCK_ID, truckInfo.truckId);
        contentValues.put(TruckCCDatabase.KEY_TRUCK_NUMBER, truckInfo.truckNumber);
        contentValues.put(TruckCCDatabase.KEY_TRUCK_NAME, truckInfo.truckName);
        contentValues.put(TruckCCDatabase.KEY_TRUCK_MODEL, truckInfo.model);
        contentValues.put(TruckCCDatabase.KEY_TRUCK_MAKE, truckInfo.make);
        contentValues.put(TruckCCDatabase.KEY_TRUCK_TYPE, truckInfo.type);
        contentValues.put(TruckCCDatabase.KEY_TRUCK_MAXCAPACITY, truckInfo.maxCapacity);
        contentValues.put(TruckCCDatabase.KEY_TRUCK_NUM_OF_CANS, truckInfo.numOfCans);
        contentValues.put(TruckCCDatabase.KEY_TRUCK_LAST_SERVICE_DATE, truckInfo.lastServiceDate);
        contentValues.put(TruckCCDatabase.KEY_TRUCK_CONTRACT_START_DATE, truckInfo.contractStartDate);
        contentValues.put(TruckCCDatabase.KEY_TRUCK_OWNER_NAME, truckInfo.ownerName);
        contentValues.put(TruckCCDatabase.KEY_TRUCK_OWNER_CONTACT_NUM, truckInfo.ownerContactNumber);
        contentValues.put(TruckCCDatabase.KEY_TRUCK_DRIVER_NAME, truckInfo.driverName);
        contentValues.put(TruckCCDatabase.KEY_TRUCK_DRIVER_CONTACT_NUMBER, truckInfo.driverContactNumber);

        String routes = "";

        if (truckInfo.routes != null && truckInfo.routes.size() > 0) {
            for (int i = 0; i < truckInfo.routes.size(); i++) {
                routes = routes + SmartCCConstants.LIST_SEPARATOR;
            }
        }
        contentValues.put(TruckCCDatabase.KEY_TRUCK_ROUTE, routes);
        return contentValues;
    }

    @Override
    Entity getEntityFromCursor(Cursor cursor) {
        TrucKInfo trucKInfo = new TrucKInfo();
        trucKInfo.truckId = cursor.getString(cursor.getColumnIndex(TruckCCDatabase.KEY_TRUCK_ID));
        trucKInfo.numOfCans = cursor.getShort(cursor.getColumnIndex(TruckCCDatabase.KEY_TRUCK_NUM_OF_CANS));
        trucKInfo.truckNumber = cursor.getString(cursor.getColumnIndex(TruckCCDatabase.KEY_TRUCK_NUMBER));
        trucKInfo.truckName = cursor.getString(cursor.getColumnIndex(TruckCCDatabase.KEY_TRUCK_NAME));
        trucKInfo.model = cursor.getString(cursor.getColumnIndex(TruckCCDatabase.KEY_TRUCK_MODEL));
        trucKInfo.make = cursor.getString(cursor.getColumnIndex(TruckCCDatabase.KEY_TRUCK_MAKE));
        trucKInfo.type = cursor.getString(cursor.getColumnIndex(TruckCCDatabase.KEY_TRUCK_TYPE));
        trucKInfo.maxCapacity = cursor.getDouble(cursor.getColumnIndex(TruckCCDatabase.KEY_TRUCK_MAXCAPACITY));
        trucKInfo.lastServiceDate = cursor.getLong(cursor.getColumnIndex(TruckCCDatabase.KEY_TRUCK_LAST_SERVICE_DATE));
        trucKInfo.contractStartDate = cursor.getLong(cursor.getColumnIndex(TruckCCDatabase.KEY_TRUCK_CONTRACT_START_DATE));
        trucKInfo.ownerName = cursor.getString(cursor.getColumnIndex(TruckCCDatabase.KEY_TRUCK_OWNER_NAME));
        trucKInfo.ownerContactNumber = cursor.getString(cursor.getColumnIndex(TruckCCDatabase.KEY_TRUCK_DRIVER_CONTACT_NUMBER));
        trucKInfo.driverName = cursor.getString(cursor.getColumnIndex(TruckCCDatabase.KEY_TRUCK_DRIVER_NAME));
        trucKInfo.driverContactNumber = cursor.getString(cursor.getColumnIndex(TruckCCDatabase.KEY_TRUCK_DRIVER_CONTACT_NUMBER));

        String routes = cursor.getString(cursor.getColumnIndex(TruckCCDatabase.KEY_TRUCK_ROUTE));
        if (routes != null) {
            String[] allRoutes = routes.split(SmartCCConstants.LIST_SEPARATOR);
            List<String> listRoutes = new ArrayList<>();
            for (int i = 0; i < allRoutes.length; i++) {
                listRoutes.add(allRoutes[i]);
            }
            trucKInfo.routes = listRoutes;
        }
        return trucKInfo;
    }

    @Override
    String getEntityIdColumnName() {
        return TruckCCDatabase.KEY_TRUCK_ID;
    }
}
