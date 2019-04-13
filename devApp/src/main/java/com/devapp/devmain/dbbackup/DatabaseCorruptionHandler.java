package com.devapp.devmain.dbbackup;

import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import android.util.Pair;

import com.devapp.devmain.server.DatabaseHandler;

import java.io.File;
import java.util.List;

/**
 * Created by x on 27/2/18.
 */

public class DatabaseCorruptionHandler implements DatabaseErrorHandler {
    private final String TAG = "DatabaseCorruptionHandler";

    @Override
    public void onCorruption(SQLiteDatabase dbObj) {
        boolean isPrimary = dbObj.getPath().startsWith(Backup.DATBASE_PATH);
        Log.e(TAG, "Corruption reported by sqlite on database: " + dbObj.getPath());
        // is the corruption detected even before database could be 'opened'?
        if (!dbObj.isOpen()) {
            // database files are not even openable. delete this database file.
            // NOTE if the database has attached databases, then any of them could be corrupt.
            // and not deleting all of them could cause corrupted database file to remain and
            // make the application crash on database read operation. To avoid this problem,
            // the application should provide its own {@link DatabaseErrorHandler} impl class
            // to delete ALL files of the database (including the attached databases).
            deleteDatabaseFile(dbObj.getPath());
            if (isPrimary) {
                DatabaseSync.closeDatabase();
                new Backup().backUpDatabase(Backup.SECONDARY); //Source is Secondary
            } else {
                DatabaseHandler.closeDatabase();
                new Backup().backUpDatabase(Backup.PRIMARY);
            }
            return;
        }


        List<Pair<String, String>> attachedDbs = null;
        try {
            // Close the database, which will cause subsequent operations to fail.
            // before that, get the attached database list first.
            try {
                attachedDbs = dbObj.getAttachedDbs();
            } catch (SQLiteException e) {
                /* ignore */
            }
            try {
                dbObj.close();
            } catch (SQLiteException e) {
                /* ignore */
            }
        } finally {
            // Delete all files of this corrupt database and/or attached databases
            if (attachedDbs != null) {
                for (Pair<String, String> p : attachedDbs) {
                    deleteDatabaseFile(p.second);
                }
            } else {
                // attachedDbs = null is possible when the database is so corrupt that even
                // "PRAGMA database_list;" also fails. delete the main database file
                deleteDatabaseFile(dbObj.getPath());
            }
        }

        if (isPrimary) {
            DatabaseSync.closeDatabase();
            new Backup().backUpDatabase(Backup.SECONDARY); //Source is Secondary
        } else {
            DatabaseHandler.closeDatabase();
            new Backup().backUpDatabase(Backup.PRIMARY);
        }

    }

    private void deleteDatabaseFile(String fileName) {
        if (fileName.equalsIgnoreCase(":memory:") || fileName.trim().length() == 0) {
            return;
        }
        Log.e(TAG, "deleting the database file: " + fileName);
        try {
            SQLiteDatabase.deleteDatabase(new File(fileName));
        } catch (Exception e) {
            /* print warning and ignore exception */
            Log.w(TAG, "delete failed: " + e.getMessage());
        }
    }
}
