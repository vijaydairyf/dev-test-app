package com.devapp.devmain.dao;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.devapp.devmain.dbbackup.Backup;
import com.devapp.devmain.entity.Entity;

import java.util.ArrayList;

/**
 * Created by x on 6/2/18.
 */

public abstract class ProfileDao extends Dao {
    ProfileDao(SQLiteDatabase sqLiteDatabase, String tableName) {
        super(sqLiteDatabase, tableName);
    }


    private void delete(String id) throws SQLException {
        sqLiteDatabase.delete(tableName,
                getEntityIdColumnName() + " = ? ",
                new String[]{String.valueOf(id)});
    }

    public synchronized void delete(Entity entity) throws SQLException {
        Entity re = findById((Long) entity.getPrimaryKeyId());
        delete((String) entity.getPrimaryKeyId());

        if (secondaryDao != null) {
            secondaryDao.delete(re);
            try {
                secondaryDao.delete(re);
            } catch (Exception e) {
                e.printStackTrace();
                new Backup().validateAndBackup();
            }
        }
    }


    public Entity findById(String id) {

        Entity e = null;
        String selectQuery = "SELECT * FROM " + tableName + " WHERE " +
                getEntityIdColumnName() + " = '" + id + "'";

        ArrayList<? extends Entity> entities = findByQuery(selectQuery);


        if (entities.size() > 0) {
            e = entities.get(0);
        }
        return e;

    }

    Entity findByEntity(Entity entity) {
        if (entity.getPrimaryKeyId() != null && !entity.getPrimaryKeyId().toString().trim().isEmpty()) {
            return findById((String) entity.getPrimaryKeyId());
        }
        return null;
    }

    public ArrayList<? extends Entity> findAll() {
        String query = "SELECT * FROM " + tableName + " ORDER BY " + getEntityIdColumnName();
        return findByQuery(query);
    }
}
