package com.devapp.devmain.databaseTasks;

import android.content.ContentValues;

import com.devapp.devmain.entity.CenterEntity;
import com.devapp.devmain.entity.FarmerEntity;
import com.devapp.devmain.server.DatabaseHandler;

import java.util.ArrayList;

/**
 * Created by u_pendra on 1/2/18.
 */

public class ContentValueHelper {


    public ContentValueHelper() {
    }

    public static String getTable(int type) {
        String tableName = null;
        if (type == EntityType.FARMER)
            return DatabaseHandler.TABLE_FARMER;
        if (type == EntityType.CENTER)
            return DatabaseHandler.TABLE_CHILLING_CENTER;

        return tableName;
    }

    public ArrayList<ContentValues> getContentValue(int type, ArrayList entries) {

        ArrayList<ContentValues> contentValues = null;

        if (type == EntityType.FARMER) {
            contentValues = getFarmerContentValue(entries);
        } else if (type == EntityType.CENTER) {
            contentValues = getCenterValue(entries);
        }

        return contentValues;

    }

    private ArrayList<ContentValues> getFarmerContentValue(ArrayList<FarmerEntity> allFarmerList) {
        ArrayList<ContentValues> contentValuesList = new ArrayList<>();
        ContentValues contentValues = new ContentValues();
        contentValuesList.add(contentValues);
        return contentValuesList;
    }

    private ArrayList<ContentValues> getCenterValue(ArrayList<CenterEntity> allCenterEntity) {
        ArrayList<ContentValues> contentValuesList = new ArrayList<>();
        ContentValues contentValues = new ContentValues();
        contentValuesList.add(contentValues);
        return contentValuesList;
    }


}
