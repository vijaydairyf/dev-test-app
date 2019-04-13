package com.devapp.devmain.dbbackup;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.devapp.devmain.entity.ConfigurationElement;
import com.devapp.devmain.helper.DatabaseEntity;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.tableEntities.FarmerCollectionTable;
import com.devapp.devmain.user.Util;
import com.devapp.devmain.util.AdvanceUtil;
import com.devapp.devmain.util.logger.Log;

import java.io.File;

import static com.devapp.devmain.server.DatabaseHandler.KEY_COLL_REC_SEQNUM;
import static com.devapp.devmain.server.DatabaseHandler.TABLE_COLL_REC_SEQ_NUM;
import static com.devapp.devmain.server.DatabaseHandler.increment;


/**
 * Created by Upendra on 8/2/2016.
 */
public class DatabaseSync extends SQLiteOpenHelper {

    public static SQLiteDatabase sqliteDatabase;
    private static DatabaseSync databaseSync;
    private static String BACK_UP_FOLDER = ".devappdatabase";
    private static String BACK_UP = "databases";
    private Context mContext;


    private DatabaseSync(Context context) {
        super(context,
                Util.getSDCardPath() + File.separator + BACK_UP_FOLDER
                        + File.separator + BACK_UP + File.separator + DatabaseHandler.DATABASE_NAME,
                null, DatabaseHandler.DATABASE_VERSION, new DatabaseCorruptionHandler());
        this.mContext = context;

    }

    public static DatabaseSync getInstance(Context context) {
        if (databaseSync == null) {
            synchronized (DatabaseSync.class) {
                if (databaseSync == null)
                    databaseSync = new DatabaseSync(context);
            }
        }

        SecondaryDBObject sdo = new SecondaryDBObject();
        if (sdo.isFileExist() && sqliteDatabase == null ||
                (sqliteDatabase != null && !sqliteDatabase.isOpen())) {
            try {
                sqliteDatabase = databaseSync.getWritableDatabase();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return databaseSync;
    }

    private static boolean isOpen() {
        if (sqliteDatabase != null && sqliteDatabase.isOpen()) {
            return true;
        } else {
            return false;
        }
    }

    public static void closeDatabase() {
        if (isOpen()) {
            sqliteDatabase.close();
        }
    }

    public SQLiteDatabase getSqliteDatabase() {
        SecondaryDBObject sdo = new SecondaryDBObject();
        if (sdo.isFileExist() && (sqliteDatabase == null) || (sqliteDatabase != null && !sqliteDatabase.isOpen()))
            sqliteDatabase = databaseSync.getWritableDatabase();

        return sqliteDatabase;

    }

    private long insertWithSeqNum(SQLiteDatabase db,
                                  String table, ContentValues values) throws Exception {
        int seqNum = 0;
        try {
            db.beginTransaction();

            seqNum = 0;
            String selectSeqNum = "SELECT " + DatabaseHandler.KEY_COLL_REC_SEQNUM + " FROM "
                    + DatabaseHandler.TABLE_COLL_REC_SEQ_NUM;
            Cursor cursor = db.rawQuery(selectSeqNum, null);
            if (cursor.moveToFirst()) {
                seqNum = cursor.getInt(0);
            }
            seqNum = increment(seqNum);
            //Sequence number should be same across the the collection entity
            values.put(FarmerCollectionTable.SEQUENCE_NUMBER, seqNum);
            syncInsert(db, table, values);

            ContentValues seqVal = new ContentValues();
            seqVal.put(KEY_COLL_REC_SEQNUM, seqNum);
            syncUpdate(db, TABLE_COLL_REC_SEQ_NUM, seqVal, null, null);

            db.setTransactionSuccessful();


        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            db.endTransaction();
        }

        return seqNum;

    }

    public long syncInsertWithSequenceNumber(SQLiteDatabase db,
                                             String table, ContentValues values) throws Exception {

        long id = insertWithSeqNum(db, table, values);
        insertWithSeqNum(getSqliteDatabase(), table, values);

        return id;
    }

    public long syncInsertBoth(SQLiteDatabase db,
                               String table, ContentValues values) throws Exception {
        long rowId = -1;
        rowId = db.insert(table, null, values);

        if (rowId != -1) {
            rowId = getSqliteDatabase().insert(table, null, values);
        } else {

        }
        return rowId;
    }

    //Collection needs to do in transaction

    public long syncInsert(SQLiteDatabase db, String table, ContentValues values) throws Exception {
        long rowId = -1;
        rowId = db.insert(table, null, values);

        return rowId;
    }


    public long syncInsertTransaction(SQLiteDatabase db, String table, ContentValues values) throws Exception {
        long rowId = -1;
        rowId = db.insert(table, null, values);
        return rowId;
    }


    public long syncUpdateTransaction(SQLiteDatabase db, String table,
                                      ContentValues values, String strFilter, String[] strArgs)
            throws Exception {
        long rowId = -1;

        rowId = db.update(table, values, strFilter, strArgs);
        return rowId;
    }


    public long syncInsertCollection(SQLiteDatabase db, String table, ContentValues values)
            throws Exception {
        long rowId = -1;

        rowId = db.insert(table, null, values);
        return rowId;
    }


    public long syncUpdateCollection(SQLiteDatabase db, String table,
                                     ContentValues values, String strFilter, String[] strArgs)
            throws Exception {
        long rowId = -1;
        rowId = db.update(table, values, strFilter, strArgs);
        return rowId;
    }

    public long syncUpdateDB(SQLiteDatabase db, String table,
                             ContentValues values, String strFilter, String[] strArgs)
            throws Exception {
        long rowId = -1;
        rowId = db.update(table, values, strFilter, strArgs);
        return rowId;
    }

    public long syncUpdateBoth(SQLiteDatabase db, String table,
                               ContentValues values, String strFilter, String[] strArgs) throws Exception {
        long rowId = -1;
        rowId = db.update(table, values, strFilter, strArgs);

        if (rowId != -1) {
            rowId = getSqliteDatabase().update(table, values, strFilter, strArgs);
        } else {

        }
        return rowId;
    }


    public long syncUpdate(SQLiteDatabase db, String table,
                           ContentValues values, String strFilter, String[] strArgs) throws Exception {
        long rowId = -1;
        rowId = db.update(table, values, strFilter, strArgs);

        return rowId;
    }


    public int syncDeleteByDB(SQLiteDatabase db, String tableName,
                              String var1, String[] var2) throws Exception {
        int id = -1;

        id = db.delete(tableName, var1, var2);
        return id;
    }


    public int syncDeleteBoth(SQLiteDatabase db,
                              String tableName, String var1, String[] var2) throws Exception {
        int id = -1;

        try {
            id = db.delete(tableName, var1, var2);
            if (id != -1) {
                id = getSqliteDatabase().delete(tableName, var1, var2);
            } else {

            }

        } catch (Exception e) {
            e.printStackTrace();

        }

        if (id == -1) {

        }
        return id;
    }

    public void deleteAllFarmers(SQLiteDatabase db, String tableName) {
        String query = "DELETE FROM " + tableName + " WHERE " + DatabaseHandler.KEY_FARMER_COLUMNID + " NOT IN (SELECT " +
                DatabaseHandler.KEY_FARMER_COLUMNID + " FROM " + tableName +
                " WHERE " + DatabaseHandler.KEY_FARMER_ID + " = '" + DatabaseHandler.DEFAULT_FARMER_ID + "')";
        db.execSQL(query);
    }


    public int syncDelete(SQLiteDatabase db, String tableName, String var1, String[] var2) throws Exception {
        int id = -1;
        id = db.delete(tableName, var1, var2);
        return id;
    }

    public long syncInsertExcelDatabase(SQLiteDatabase db,
                                        String table, ContentValues values) throws Exception {
        long rowId = 0;

        rowId = db.insert(table, null, values);

        return rowId;
    }


    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("DatabaseSync", "onCreate inside databasesync..do nothing");
    }

    /**
     * Called when the database needs to be upgraded. The implementation
     * should use this method to drop tables, add tables, or do anything else it
     * needs to upgrade to the new schema version.
     * <p/>
     * <p>
     * The SQLite ALTER TABLE documentation can be found
     * <a href="http://sqlite.org/lang_altertable.html">here</a>. If you add new columns
     * you can use ALTER TABLE to insert them into a live table. If you rename or remove columns
     * you can use ALTER TABLE to rename the old table, then create the new table and then
     * populate the new table with the contents of the old table.
     * </p><p>
     * This method executes within a transaction.  If an exception is thrown, all changes
     * will automatically be rolled back.
     * </p>
     *
     * @param db         The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("DatabaseSync", "onUpgrade inside databasesync..do nothing");
    }

    public void insertOrIgnoreConfiguration(SQLiteDatabase db,
                                            ConfigurationElement cpl) {
        String query = "INSERT OR IGNORE into CONFIGURATION_TABLE (" + DatabaseEntity.CONFIGURATION_KEY
                + ", " + DatabaseEntity.CONFIGURATION_VALUE + ", " + DatabaseEntity.CONFIGURATION_LAST_VALUE +
                " ," +
                DatabaseEntity.CONFIGURATION_STATUS + ", "
                + DatabaseEntity.CONFIGURATION_MODIFIED_TIME + ") " + " values ('" +
                cpl.key + "','" + cpl.value + "','" + cpl.lastValue + "', "
                + cpl.status + ", " + cpl.modifiedDate + ")";

        db.execSQL(query);
    }


    public void insertOrReplaceUserHistory(SQLiteDatabase db,
                                           String userId, String role, long time) {
        String query = "INSERT OR REPLACE into USER_HISTORY (" + DatabaseEntity.USER_ID
                + ", " + DatabaseEntity.USER_ROLE + ", " + DatabaseEntity.USER_LOG_IN_TIME + ") " +
                "" + " values ('" +
                userId + "','" + role + "'," + time + ")";

        db.execSQL(query);
    }


    public void endTransaction(SQLiteDatabase db) {
        try {
            db.endTransaction();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getDatabaseIntegrity() throws Exception {
        String returnValue = null;
        Cursor cursor = sqliteDatabase.rawQuery("pragma integrity_check", null);

        if (cursor != null && cursor.moveToFirst()) {
            returnValue = cursor.getString(0);
        }
        if (cursor != null)
            cursor.close();
        return returnValue;
    }


    private void restartApp(SQLiteDatabase db) {
        if (db != sqliteDatabase) {

            AdvanceUtil.todoDbFailure(mContext, DatabaseHandler.isPrimary);
        }

        //  Util.restartApp(mContext);
    }


}
