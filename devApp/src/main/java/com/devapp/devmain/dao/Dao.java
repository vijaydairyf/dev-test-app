package com.devapp.devmain.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.devapp.devmain.dbbackup.Backup;
import com.devapp.devmain.entity.Entity;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.tableEntities.ExtraParams;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.devapp.devmain.server.DatabaseHandler.KEY_COLL_REC_SEQNUM;
import static com.devapp.devmain.server.DatabaseHandler.TABLE_COLL_REC_SEQ_NUM;

/**
 * Created by u_pendra on 3/2/18.
 */

public abstract class Dao {

    final SQLiteDatabase sqLiteDatabase;
    protected Dao secondaryDao;
    protected String tableName;
    private DbTransaction mDbTransaction;


    Dao(SQLiteDatabase sqLiteDatabase, String tableName) {

        this.sqLiteDatabase = sqLiteDatabase;
        this.tableName = tableName;
    }


    public void setSecondaryDao(Dao dao) {
        this.secondaryDao = dao;
    }

    public void setTransactionTracker(DbTransaction dbTransaction) {
        mDbTransaction = dbTransaction;
    }

    public void startTransaction() {
        sqLiteDatabase.beginTransaction();
        if (secondaryDao != null) {
            try {
                secondaryDao.startTransaction();
            } catch (Exception e) {
                e.printStackTrace();
                mDbTransaction.setTransactionStatus(DbTransaction.SECONDARY_FAILED);
            }
        }

    }

    public void setTransactionSuccessful() {
        sqLiteDatabase.setTransactionSuccessful();
        if (secondaryDao != null && mDbTransaction.getTransactionStatus() != DbTransaction.SECONDARY_FAILED) {
            try {
                secondaryDao.setTransactionSuccessful();
            } catch (Exception e) {
                e.printStackTrace();
                mDbTransaction.setTransactionStatus(DbTransaction.SECONDARY_FAILED);
            }
        }
    }

    public void endTransaction() {
        sqLiteDatabase.endTransaction();
        if (secondaryDao != null) {
            if (mDbTransaction.getTransactionStatus() != DbTransaction.SECONDARY_FAILED) {
                try {
                    secondaryDao.endTransaction();
                } catch (Exception e) {
                    e.printStackTrace();
                    new Backup().validateAndBackup();
                }
            } else {
                new Backup().validateAndBackup();
            }
        }
    }


    private long saveWithoutTransaction(final Entity entity) {

        long id = -1;
        ContentValues contentValues = getContentValues(entity);

        int seqNum = getLastTransactionSequenceNumber();
        seqNum = DatabaseHandler.increment(seqNum);
//       All tables containing sequence number have the coloumn as "seqNum"
        contentValues.put(ExtraParams.SEQUENCE_NUMBER, seqNum);
        id = sqLiteDatabase.insertOrThrow(tableName, null, contentValues);

        setLastTransactionSequenceNumber(seqNum);

        return id;

    }

    public int getLastTransactionSequenceNumber() {
        String selectSeqNum = "SELECT " + DatabaseHandler.KEY_COLL_REC_SEQNUM + " FROM "
                + DatabaseHandler.TABLE_COLL_REC_SEQ_NUM;
        Cursor cursor = sqLiteDatabase.rawQuery(selectSeqNum, null);
        int seqNum = 0;
        if (cursor.moveToFirst()) {
            seqNum = cursor.getInt(0);
        }
        return seqNum;
    }

    //    TODO: Secondary DB chaining
    public long setLastTransactionSequenceNumber(int seqNum) {
        ContentValues seqVal = new ContentValues();
        seqVal.put(KEY_COLL_REC_SEQNUM, seqNum);
        return sqLiteDatabase.update(TABLE_COLL_REC_SEQ_NUM, seqVal, null, null);
    }

    private synchronized long saveWithSequenceNumber(Entity entity) {

        long id = 0;
        try {
            sqLiteDatabase.beginTransaction();
            saveWithoutTransaction(entity);
            sqLiteDatabase.setTransactionSuccessful();
        } catch (SQLException e) {
            e.printStackTrace();
            throw (e);
        } finally {
            //TODO to check if finally will be called after throwing exception from catch

            sqLiteDatabase.endTransaction();
        }
        if (secondaryDao != null) {
            try {
                secondaryDao.saveWithSequenceNumber(entity);
            } catch (Exception e) {
                e.printStackTrace();
                new Backup().validateAndBackup();
            }
        }
        return id;
    }


    public long saveOrUpdate(Entity entity) throws SQLException {

        Entity e = findByEntity(entity);

        if (e != null) {
            entity.setPrimaryKeyId(e.getPrimaryKeyId());
            return update(entity);
        } else {
            /*if (entity.getClass().getAnnotation(TransactionalEntity.class) != null) {
                return saveWithSequenceNumber(entity);
            } else {
                return save(entity);
            }*/
            return save(entity);
        }

    }


    //TODO obtaining sequence number should be part of collection record sequence table
    public synchronized long save(Entity entity) throws SQLException {

        long id = -1;
        ContentValues contentValues = getContentValues(entity);

        Log.v("ID ", entity.getPrimaryKeyId().toString());
        id = sqLiteDatabase.insertOrThrow(tableName, null, contentValues);

        //Failure to save into secondary DB is suppressed
        if (secondaryDao != null &&
                (mDbTransaction == null || mDbTransaction.getTransactionStatus() != DbTransaction.SECONDARY_FAILED)) {
            try {
                secondaryDao.save(entity);
            } catch (Exception e) {
                e.printStackTrace();
                if (mDbTransaction != null) {
                    mDbTransaction.setTransactionStatus(DbTransaction.SECONDARY_FAILED);
                } else {
                    new Backup().validateAndBackup();
                }
            }
        }
        return id;

    }

    public synchronized long update(final Entity entity) {
        ContentValues contentValues = getContentValues(entity);
        long id = sqLiteDatabase.update(tableName, contentValues,
                getEntityIdColumnName() + " = ? ",
                new String[]{String.valueOf(entity.getPrimaryKeyId())});
        if (secondaryDao != null &&
                (mDbTransaction == null || mDbTransaction.getTransactionStatus() != DbTransaction.SECONDARY_FAILED)) {
            try {
                secondaryDao.update(entity);
            } catch (Exception e) {
                e.printStackTrace();
                if (mDbTransaction != null) {
                    mDbTransaction.setTransactionStatus(DbTransaction.SECONDARY_FAILED);
                } else {
                    new Backup().validateAndBackup();
                }
            }
        }
        return id;
    }

    protected synchronized long update(ContentValues values, String filter, String[] args) {
        long rowId = -1;
        rowId = sqLiteDatabase.update(tableName, values, filter, args);
        if (secondaryDao != null &&
                (mDbTransaction == null || mDbTransaction.getTransactionStatus() != DbTransaction.SECONDARY_FAILED)) {
            try {
                secondaryDao.update(values, filter, args);
            } catch (Exception e) {
                e.printStackTrace();
                if (mDbTransaction != null) {
                    mDbTransaction.setTransactionStatus(DbTransaction.SECONDARY_FAILED);
                } else {
                    new Backup().validateAndBackup();
                }
            }
        }
        return rowId;
    }


    //TODO How to get the ID's of saved entities
//    TODO Delete this method
    public synchronized void saveAll(ArrayList<? extends Entity> entities) {

        try {
            sqLiteDatabase.beginTransaction();
            for (Entity entity : entities) {
                save(entity);
            }
            sqLiteDatabase.setTransactionSuccessful();

        } catch (SQLException e) {
            e.printStackTrace();
            throw (e);
        } finally {
            sqLiteDatabase.endTransaction();
        }
    }

    private void delete(long id) throws SQLException {

        sqLiteDatabase.delete(tableName,
                getEntityIdColumnName() + " = ? ",
                new String[]{String.valueOf(id)});

    }

    public synchronized void delete(Entity entity) throws SQLException {
        Entity re = findById((Long) entity.getPrimaryKeyId());
        delete((Long) entity.getPrimaryKeyId());

        if (secondaryDao != null &&
                (mDbTransaction == null || mDbTransaction.getTransactionStatus() != DbTransaction.SECONDARY_FAILED)) {
            try {
                secondaryDao.delete(re);
            } catch (Exception e) {
                e.printStackTrace();
                if (mDbTransaction != null) {
                    mDbTransaction.setTransactionStatus(DbTransaction.SECONDARY_FAILED);
                } else {
                    new Backup().validateAndBackup();
                }
            }
        }
    }

    public long deleteByKey(String key, String value) {
        long rowId = sqLiteDatabase.delete(tableName, key + " = ? ", new String[]{value});
        if (secondaryDao != null &&
                (mDbTransaction == null || mDbTransaction.getTransactionStatus() != DbTransaction.SECONDARY_FAILED)) {
            try {
                secondaryDao.deleteByKey(key, value);
            } catch (Exception e) {
                e.printStackTrace();
                if (mDbTransaction != null) {
                    mDbTransaction.setTransactionStatus(DbTransaction.SECONDARY_FAILED);
                } else {
                    new Backup().validateAndBackup();
                }
            }
        }
        return rowId;
    }

    public void deleteAll() {
        // deleteByKey(null, null);
        sqLiteDatabase.delete(tableName, null, null);
        if (secondaryDao != null &&
                (mDbTransaction == null || mDbTransaction.getTransactionStatus() != DbTransaction.SECONDARY_FAILED)) {
            try {
                secondaryDao.deleteAll();
            } catch (Exception e) {
                e.printStackTrace();
                if (mDbTransaction != null) {
                    mDbTransaction.setTransactionStatus(DbTransaction.SECONDARY_FAILED);
                } else {
                    new Backup().validateAndBackup();
                }
            }
        }
    }

    public List<? extends Entity> findAll() {
        String query = "SELECT * FROM " + tableName;
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        Entity entity = null;
        List<Entity> entities = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                entity = getEntityFromCursor(cursor);
                entities.add(entity);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return entities;
    }


    protected List<? extends Entity> findByFilter(Map<Key, Object> map) {
        String query = "SELECT * FROM " + tableName + " WHERE ";
        StringBuilder sb = new StringBuilder(query);
        StringBuilder order = new StringBuilder();

        ArrayList<String> val = new ArrayList<>();
        List<Entity> entities = new ArrayList<>();
        boolean isFirst = true;
        boolean hasAtleastOneNonNull = false;
        int i = 0;

        for (Map.Entry<Key, Object> mapEntry : map.entrySet()) {
            if (mapEntry.getValue() != null &&
                    !String.valueOf(mapEntry.getValue()).equalsIgnoreCase(FilterType.Order.ASC)
                    && !String.valueOf(mapEntry.getValue()).equalsIgnoreCase(FilterType.Order.DESC)) {
                hasAtleastOneNonNull = true;
                break;
            }


        }


        for (Key key : map.keySet()) {
            if (map.get(key) != null) {
                if (!isFirst) {
                    sb.append("AND ");
                } else {
                    isFirst = false;
                }
                switch (key.getFilterType()) {
                    case FilterType.EQUALS: {
                        sb.append(key.getKeyName() + " = ? ");
                        val.add((String) map.get(key));
                        break;
                    }
                    case FilterType.NOT_EQUALS: {
                        sb.append(key.getKeyName() + " != ? ");
                        val.add((String) map.get(key));
                        break;
                    }
                    case FilterType.LESS_THAN: {
                        sb.append(key.getKeyName() + " < ? ");
                        val.add((String) map.get(key));
                        break;
                    }
                    case FilterType.LESS_THAN_OR_EQUALS: {
                        sb.append(key.getKeyName() + " <= ? ");
                        val.add((String) map.get(key));
                        break;
                    }
                    case FilterType.GREATER_THAN: {
                        sb.append(key.getKeyName() + " > ? ");
                        val.add((String) map.get(key));
                        break;
                    }
                    case FilterType.GREATER_THAN_OR_EQUALS: {
                        sb.append(key.getKeyName() + " >= ? ");
                        val.add((String) map.get(key));
                        break;
                    }
                    case FilterType.BETWEEN: {
                        String[] values = (String[]) map.get(key);
                        if (values.length < 2) {
                            if (sb.substring((sb.length() - 4), sb.length()).toString().equals("AND ")) {
                                sb.delete((sb.length() - 4), sb.length());
                            }
                            break;
                        }
                        sb.append(key.getKeyName() + " BETWEEN ? AND ? ");
                        val.add(values[0]);
                        val.add(values[1]);
                        break;
                    }
                    case FilterType.IN: {
                        String[] values = (String[]) map.get(key);
                        if (values.length < 1) {
                            if (sb.substring((sb.length() - 4), sb.length()).toString().equals("AND ")) {
                                sb.delete((sb.length() - 4), sb.length());
                            }
                            break;
                        }
                        sb.append(key.getKeyName() + " IN (" + generatePlaceholders(values.length) + ") ");
                        for (String value : values) {
                            val.add(value);
                        }
                        break;
                    }
                    case FilterType.ORDER_BY: {
                        if (sb.substring((sb.length() - 4), sb.length()).toString().equals("AND ")) {
                            sb.delete((sb.length() - 4), sb.length());
                        }
                        if (order.length() > 0) {
                            order.append(", ");
                        } else {
                            order.append("ORDER BY ");
                        }
                        order.append(key.getKeyName() + " " + map.get(key) + " ");
                        break;
                    }
                    case FilterType.BOTH: {
                        sb.append("(" + key.getKeyName() + " = ? OR " + key.getKeyName() + " = 'BOTH') ");
                        val.add((String) map.get(key));
                        break;
                    }
                }
            }

        }

        Cursor cursor = null;
        if (!hasAtleastOneNonNull) {
            query = "SELECT * FROM " + tableName;
            cursor = sqLiteDatabase.rawQuery(query, null);
        } else {
            String[] values = new String[]{};
            values = val.toArray(new String[0]);
            query = sb.append(order).toString();
            cursor = sqLiteDatabase.rawQuery(query, values);
        }


        Entity entity = null;
        if (cursor != null && cursor.moveToFirst()) {
            do {
                entity = getEntityFromCursor(cursor);
                entities.add(entity);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return entities;
    }

    private String generatePlaceholders(int count) {
        String placeHolders = "";
        for (int i = 1; i <= count; i++) {
            if (i == count) {
                placeHolders = placeHolders + "?";
            } else {
                placeHolders = placeHolders + "?,";
            }
        }
        return placeHolders;
    }

    protected List<? extends Entity> findByKey(Map<String, String> map) {
        String query = "SELECT * FROM " + tableName + " WHERE ";
        StringBuilder sb = new StringBuilder(query);

        ArrayList<String> val = new ArrayList<>();
        List<Entity> entities = new ArrayList<>();
        boolean isFirst = true;
        int i = 0;
        for (String key : map.keySet()) {
            if (map.get(key) != null) {
                if (!isFirst) {
                    sb.append("AND ");
                } else {
                    isFirst = false;
                }

                if (key.endsWith("!")) {                     //For not equals conditions
                    sb.append(key.substring(0, key.length() - 1) + " != ? ");
                } else {
                    sb.append(key + " = ? ");
                }
                val.add(map.get(key));
            }

        }
        String[] values = new String[]{};
        values = val.toArray(new String[0]);
        Cursor cursor = sqLiteDatabase.rawQuery(sb.toString(), values);
        Entity entity = null;
        if (cursor != null && cursor.moveToFirst()) {
            do {
                entity = getEntityFromCursor(cursor);
                entities.add(entity);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return entities;
    }

    protected Entity findOneByKey(Map<String, String> map) {
        Entity entity = null;
        List<Entity> entities = (List<Entity>) findByKey(map);
        if (entities != null && entities.size() > 0) {
            entity = entities.get(0);
        }
        return entity;
    }

    public Entity findById(long id) {

        Entity e = null;
        String selectQuery = "SELECT * FROM " + tableName + " WHERE " +
                getEntityIdColumnName() + " = " + id;

        ArrayList<? extends Entity> entities = findByQuery(selectQuery);


        if (entities.size() > 0) {
            e = entities.get(0);
        }
        return e;

    }

    public int getCount() {
        int count = 0;
        String query = "SELECT COUNT(*) FROM " + tableName;
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        return count;
    }


    protected ArrayList<? extends Entity> findByQuery(String query) {

        ArrayList<Entity> entities = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        Entity entity = null;

        if (cursor != null && cursor.moveToFirst()) {
            do {
                entity = getEntityFromCursor(cursor);
                entities.add(entity);
            } while (cursor.moveToNext());
            cursor.close();
        }


        return entities;
    }

    protected Cursor rawQuery(String query, String[] args) {
        Log.d("AMCU", query);
        return sqLiteDatabase.rawQuery(query, null);
    }

    Entity findByEntity(Entity entity) {

        if ((Long) entity.getPrimaryKeyId() != -1) {
            return findById((Long) entity.getPrimaryKeyId());
        }
        return null;
    }

    Entity findOneByQuery(String query) {
        ArrayList<? extends Entity> entities = findByQuery(query);
        if (entities.size() > 0) {
            return entities.get(0);
        } else {
            return null;
        }
    }

    public boolean isValid(Entity entity) {
        return true;
    }


    abstract ContentValues getContentValues(Entity entity);

    abstract Entity getEntityFromCursor(Cursor cursor);

    abstract String getEntityIdColumnName();

    interface FilterType {
        int EQUALS = 1;
        int NOT_EQUALS = 2;
        int GREATER_THAN = 3;
        int GREATER_THAN_OR_EQUALS = 4;
        int LESS_THAN = 5;
        int LESS_THAN_OR_EQUALS = 6;
        int BETWEEN = 7;
        int IN = 8;
        int ORDER_BY = 9;
        int BOTH = 10;

        interface Order {
            String ASC = "ASC";
            String DESC = "DESC";
        }
    }


}
