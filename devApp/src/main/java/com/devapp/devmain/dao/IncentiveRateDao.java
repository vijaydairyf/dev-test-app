package com.devapp.devmain.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.devapp.devmain.entity.BonusChartEntity;
import com.devapp.devmain.entity.Entity;
import com.devapp.devmain.helper.DatabaseEntity;

import java.util.List;

/**
 * Created by u_pendra on 3/8/18.
 */

public class IncentiveRateDao extends Dao {

    IncentiveRateDao(SQLiteDatabase sqLiteDatabase) {
        super(sqLiteDatabase, DatabaseEntity.InCentive.TABLE_NAME);
    }


    public synchronized void saveAll(List<? extends Entity> entities) {
        for (Entity entity : entities) {
            save(entity);
        }
    }

    @Override
    ContentValues getContentValues(Entity entity) {

        BonusChartEntity bonusChartEntity = (BonusChartEntity) entity;
        ContentValues values = new ContentValues();

        values.put(DatabaseEntity.InCentive.VALUE,
                bonusChartEntity.point);
        values.put(DatabaseEntity.InCentive.BONUS, bonusChartEntity.bonusRate);
        values.put(DatabaseEntity.InCentive.START_DATE, bonusChartEntity.startDate);
        values.put(DatabaseEntity.InCentive.END_DATE, bonusChartEntity.endDate);
        values.put(DatabaseEntity.InCentive.SOCIETY_ID, bonusChartEntity.societyId);
        values.put(DatabaseEntity.InCentive.MILK_TYPE, bonusChartEntity.milkType);
        values.put(DatabaseEntity.InCentive.RATE_CHART_NAME,
                bonusChartEntity.name);
        values.put(DatabaseEntity.InCentive.RATE_CHART_TYPE,
                bonusChartEntity.rateChartType);


        return values;
    }

    @Override
    Entity getEntityFromCursor(Cursor cursor) {

        BonusChartEntity bonusChartEntity = new BonusChartEntity();

        bonusChartEntity.bonusRate = Double.parseDouble(cursor.getString(cursor.getColumnIndex(
                DatabaseEntity.InCentive.BONUS
        )));
        bonusChartEntity.point = Double.parseDouble(cursor.getString(cursor.getColumnIndex(
                DatabaseEntity.InCentive.VALUE
        )));

        bonusChartEntity.milkType = cursor.getString(cursor.getColumnIndex(
                DatabaseEntity.InCentive.MILK_TYPE
        ));
      /*  bonusChartEntity.isActive = Boolean.valueOf(cursor.getString(cursor.getColumnIndex(
                DatabaseEntity.InCentive.A
        )));*/

        bonusChartEntity.rateChartType = cursor.getString(cursor.getColumnIndex(
                DatabaseEntity.InCentive.RATE_CHART_TYPE
        ));
        bonusChartEntity.endDate = cursor.getString(cursor.getColumnIndex(
                DatabaseEntity.InCentive.END_DATE
        ));
        bonusChartEntity.startDate = cursor.getString(cursor.getColumnIndex(
                DatabaseEntity.InCentive.START_DATE
        ));

        return bonusChartEntity;
    }

    @Override
    String getEntityIdColumnName() {
        return DatabaseEntity.InCentive.RATE_CHART_NAME;
    }



      /*  public void saveAll(List<? extends Entity> entityList, boolean isCloudPush) {
            Util.WRITE_RATE_ADD_LOG = true;
            ArrayList<IncentiveRateChartPostEntity> rateChartList = (ArrayList<IncentiveRateChartPostEntity>) entityList;
            for (int i = 0, len = rateChartList.size(); i < len; i++) {
                dbHandler.addCompleteInCentiveRateChart(getRateChartDetailsEnt(rateChartList.get(i)), (ArrayList<BonusChartEntity>) rateChartList.get(i).valueList, DatabaseHandler.isPrimary, isCloudPush ? Util.CLOUD : Util.USB);
            }
        }*/


}
