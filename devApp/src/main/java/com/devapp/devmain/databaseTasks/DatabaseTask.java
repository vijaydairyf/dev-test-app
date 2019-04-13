package com.devapp.devmain.databaseTasks;

import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by u_pendra on 22/1/18.
 */

public class DatabaseTask {

    private SQLiteDatabase sqLiteDatabase;
    private DatabaseTask chainTask;

    public DatabaseTask(SQLiteDatabase sqLiteDatabase) {
        this.sqLiteDatabase = sqLiteDatabase;
    }

    DatabaseTask getChainTask() {
        return chainTask;
    }

    public void setChainTask(DatabaseTask databaseTask) {
        this.chainTask = databaseTask;
    }

    SQLiteDatabase getDatabase() {
        return this.sqLiteDatabase;
    }

    boolean hasChainTask() {

        if (chainTask == null) {
            return false;
        } else {
            return true;
        }
    }

    public void doAction(String query) throws SQLException {
        if (this.sqLiteDatabase != null) {
            this.sqLiteDatabase.execSQL(query);
        } else {
            throw new SQLException("Invalid database!");
        }

        if (chainTask != null) {
            chainTask.doAction(query);
        }
    }


    public void doActionInTransaction(ArrayList<ActionEntity> actionList) {
        if (this.sqLiteDatabase != null) {
            sqLiteDatabase.beginTransaction();
            for (ActionEntity actionEntity : actionList) {
                if (actionEntity.query != null) {
                    sqLiteDatabase.execSQL(actionEntity.query);
                } else if (actionEntity.contentValuesList != null
                        && actionEntity.contentValuesList.size() > 0) {
                    if (actionEntity.action.equalsIgnoreCase("Insert")) {

                        for (ContentValues contentValues : actionEntity.contentValuesList) {
                            sqLiteDatabase.insert(actionEntity.tableName, null, contentValues);

                        }
                    } else if (actionEntity.action.equalsIgnoreCase("Update")) {

                        for (ContentValues contentValues : actionEntity.contentValuesList) {
                            sqLiteDatabase.update(actionEntity.tableName, contentValues, null, null);

                        }
                    }
                }
            }
            this.sqLiteDatabase.endTransaction();
            this.sqLiteDatabase.setTransactionSuccessful();
        } else {
            throw new SQLException("Invalid database!");
        }
        if (chainTask != null) {
            chainTask.doActionInTransaction(actionList);
        }
    }


}
