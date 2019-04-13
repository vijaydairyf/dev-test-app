package com.devapp.devmain.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.devapp.devmain.entity.CenterEntity;
import com.devapp.devmain.entity.Entity;
import com.devapp.devmain.server.DatabaseHandler;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by x on 6/2/18.
 */

public class CollectionCenterDao extends ProfileDao {

    public CollectionCenterDao(SQLiteDatabase sqLiteDatabase) {
        super(sqLiteDatabase, DatabaseHandler.TABLE_CHILLING_CENTER);
    }

    @Override
    ContentValues getContentValues(Entity entity) {
        CenterEntity centerEntity = null;
        if (entity instanceof CenterEntity) {

            centerEntity = (CenterEntity) entity;
        } else {
            return null;
        }

        ContentValues values = new ContentValues();

        values.put(DatabaseHandler.KEY_CHILLING_ID,
                centerEntity.chillingCenterId);
        values.put(DatabaseHandler.KEY_CHILLING_CONTACT_PERSON, centerEntity.contactPerson);
        if (centerEntity.centerName != null) {
            values.put(DatabaseHandler.KEY_CHILLING_CENTER_NAME,
                    centerEntity.centerName.toUpperCase(Locale.ENGLISH));
        } else {
            values.put(DatabaseHandler.KEY_CHILLING_CENTER_NAME, centerEntity.centerName);
        }
        values.put(DatabaseHandler.KEY_CHILLING_CONTACT_NUMBER, centerEntity.contactNumber);
        values.put(DatabaseHandler.KEY_CHILLING_CONTACT_EMAILID, centerEntity.contactEmailId);
        values.put(DatabaseHandler.KEY_CHILLING_REG_DATE, centerEntity.registerDate);
        if (centerEntity.centerRoute != null) {
            values.put(DatabaseHandler.KEY_CHILLING_ROUTE,
                    centerEntity.centerRoute.toUpperCase(Locale.ENGLISH));
        } else {
            values.put(DatabaseHandler.KEY_CHILLING_ROUTE, "NA");
        }

        values.put(DatabaseHandler.KEY_CHILLING_SINGLE_MULTIPLE, centerEntity.singleOrMultiple);
        values.put(DatabaseHandler.KEY_CHILLING_MILK_TYPE, centerEntity.cattleType);
        values.put(DatabaseHandler.KEY_CHILLING_BARCODE, centerEntity.centerBarcode);
        values.put(DatabaseHandler.KEY_CHILLING_CENTER_ID,
                centerEntity.centerId.toUpperCase(Locale.ENGLISH));
        values.put(DatabaseHandler.KEY_CHILLING_IS_ACTIVE, centerEntity.activeStatus);

        values.put(DatabaseHandler.KEY_CHILLING_CENTER_SENT, centerEntity.sentStatus);
        return values;
    }

    @Override
    Entity getEntityFromCursor(Cursor cursor) {
        CenterEntity centerEntity = new CenterEntity();

        centerEntity.centerName = cursor.getString(cursor.getColumnIndex(
                DatabaseHandler.KEY_CHILLING_CENTER_NAME));
        centerEntity.centerRoute = cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_CHILLING_ROUTE));
        centerEntity.cattleType = cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_CHILLING_MILK_TYPE));
        centerEntity.centerBarcode = cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_CHILLING_BARCODE));
        centerEntity.centerId = cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_CHILLING_CENTER_ID));
        centerEntity.chillingCenterId = cursor.getString(
                cursor.getColumnIndex(DatabaseHandler.KEY_CHILLING_ID));
        centerEntity.contactEmailId = cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_CHILLING_CONTACT_EMAILID));
        centerEntity.contactPerson = cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_CHILLING_CONTACT_PERSON));
        centerEntity.registerDate = cursor.getLong(cursor.getColumnIndex(
                DatabaseHandler.KEY_CHILLING_REG_DATE));
        centerEntity.contactNumber = cursor.getString(cursor.getColumnIndex(
                DatabaseHandler.KEY_CHILLING_CONTACT_NUMBER));
        centerEntity.singleOrMultiple = cursor.getString(
                cursor.getColumnIndex(DatabaseHandler.KEY_CHILLING_SINGLE_MULTIPLE));
        centerEntity.sentStatus = cursor.getInt(cursor.getColumnIndex(DatabaseHandler.KEY_CHILLING_CENTER_SENT));
        centerEntity.activeStatus = cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_CHILLING_IS_ACTIVE));

        return centerEntity;
    }

    @Override
    String getEntityIdColumnName() {
        return DatabaseHandler.KEY_CHILLING_CENTER_ID;
    }

    //    TODO Check if isActive check is required even when route is not specified
    public ArrayList<String> findActiveCenterByRoute(String route) {
        ArrayList<String> centerList = new ArrayList<>();
        String query = "SELECT " + DatabaseHandler.KEY_CHILLING_CENTER_ID + " FROM " + tableName + " WHERE ";
        if (route != null || route.equalsIgnoreCase("ALL")) {
            query = query + DatabaseHandler.KEY_CHILLING_ROUTE + "='" + route + "' AND ";
            query = query + DatabaseHandler.KEY_CHILLING_IS_ACTIVE + " ='1' ";
        }
        query = query + "ORDER BY " + DatabaseHandler.KEY_CHILLING_CENTER_ID + " ASC";

        Cursor cursor = rawQuery(query, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                centerList.add(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_CHILLING_CENTER_ID)));
            } while (cursor.moveToNext());

        }
        if (cursor != null) {
            cursor.close();
        }
        return centerList;
    }

    //    TODO Add an extra String "ALL" to the returned list in the Activities where this method is being used
    public ArrayList<String> getAllRoutes() {
        ArrayList<String> allRouteList = new ArrayList<>();

        String query = "SELECT DISTINCT " + DatabaseHandler.KEY_CHILLING_ROUTE +
                " FROM " + tableName + " ORDER BY "
                + DatabaseHandler.KEY_CHILLING_ROUTE + " ASC";

        Cursor cursor = rawQuery(query, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                allRouteList.add(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_CHILLING_ROUTE)));
            } while (cursor.moveToNext());

        }
//        allRouteList.add("All");
        if (cursor != null)
            cursor.close();
        return allRouteList;
    }


    public ArrayList<String> getAllActiveRoutes() {
        ArrayList<String> allRouteList = new ArrayList<>();

        String query = "SELECT DISTINCT " + DatabaseHandler.KEY_CHILLING_ROUTE +
                " FROM " + tableName + " Where " + DatabaseHandler.KEY_CHILLING_IS_ACTIVE
                + " = " + 1 + " ORDER BY "
                + DatabaseHandler.KEY_CHILLING_ROUTE + " ASC";
        Cursor cursor = rawQuery(query, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                allRouteList.add(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_CHILLING_ROUTE)));
            } while (cursor.moveToNext());
        }
//        allRouteList.add("All");
        if (cursor != null)
            cursor.close();
        return allRouteList;
    }


    public CenterEntity findByCenterId(String id) {
        CenterEntity centerEntity = null;
        Map<Key, Object> map = new LinkedHashMap<>();
        map.put(new Key(DatabaseHandler.KEY_CHILLING_CENTER_ID, FilterType.EQUALS), id);
        List<CenterEntity> list = (List<CenterEntity>) findByFilter(map);
        if (list.size() > 0) {
            centerEntity = list.get(0);
        }
        return centerEntity;
    }

}
