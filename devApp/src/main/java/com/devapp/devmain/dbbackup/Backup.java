package com.devapp.devmain.dbbackup;

import android.content.Context;
import android.content.Intent;

import com.devapp.devmain.main.SplashActivity;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.user.Util;
import com.devapp.devmain.util.logger.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Upendra on 8/4/2016.
 */
public class Backup {


    public static final int SECONDARY = 1;
    public static final int PRIMARY = 2;
    public static final String DATBASE_PATH = "/data/data/com.devApp/databases";
    public static final String BACKUP_DATABASE_PATH = Util.getSDCardPath() + "/.devappdatabase/databases";
    public final String SQLite_DATABASE_NAME = "devappUsb";
    public final String primarySource = "/data/data/com.devApp/";
    public final String secondarySource = Util.getSDCardPath() + "/.devappdatabase/";
    private String userName = "";

    public Backup() {
        userName = getUser();
        Log.d("Backup", "UserName :" + userName);

    }

    private static int getDbVersionFromFile(String path) {
        int version = 0;
        File file = new File(path);
        if (!file.exists()) {
            return version;
        }
        try {
            RandomAccessFile fp = new RandomAccessFile(file, "r");
            fp.seek(60);
            byte[] buff = new byte[4];
            fp.read(buff, 0, 4);
            version = ByteBuffer.wrap(buff).getInt();
            fp.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return version;
    }

    public void validateAndBackup() {
        boolean primaryExists = checkPrimaryDbExists();
        boolean secondaryExists = checkSecondaryDbExists();
        boolean isPrimarySane = checkPrimaryDbIntegrity();
        boolean isSecondarySane = checkSecondaryDbIntegrity();
        Log.d("Backup", "PrimaryExists " + primaryExists);
        Log.d("Backup", "SecondaryExists " + secondaryExists);
        Log.d("Backup", "PrimarySane " + isPrimarySane);
        Log.d("Backup", "SecondarySane " + isSecondarySane);
        if (primaryExists && isPrimarySane) {
            Log.d("Backup", "Copying primary to secondary db");
            backUpDatabase(Backup.PRIMARY);
        } else if (secondaryExists && isSecondarySane) {
            if (DatabaseHandler.DATABASE_VERSION >= getSecondaryDbVersion()) {
                Log.d("Backup", "Copying secondary to primary db");
                backUpDatabase(Backup.SECONDARY);
            }
        }
    }

    public int getPrimaryDbVersion() {
        return getDbVersionFromFile(DATBASE_PATH + "/" + SQLite_DATABASE_NAME);
    }

    public int getSecondaryDbVersion() {
        return getDbVersionFromFile(BACKUP_DATABASE_PATH + "/" + SQLite_DATABASE_NAME);
    }

    public boolean checkDatabase(int check) {
        if (check == PRIMARY) {
            return checkForDatabase(DATBASE_PATH);
        } else {
            return checkForDatabase(BACKUP_DATABASE_PATH);
        }
    }

    public boolean checkForDatabase(String str) {
        boolean isFileExist = false;
        try {
            File fileDatabase = new File(str);
            if (fileDatabase.exists()) {
                Log.d("Backup", "Database exist");
                isFileExist = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isFileExist;
    }

    public boolean checkPrimaryDbExists() {
        return checkForDatabase(DATBASE_PATH + "/" + SQLite_DATABASE_NAME);
    }

    public boolean checkSecondaryDbExists() {
        return checkForDatabase(BACKUP_DATABASE_PATH + "/" + SQLite_DATABASE_NAME);
    }

    //To copy database from sdcard vice-versa
    public void copyDatabase(int source) {
        if (source == PRIMARY) {
            copyDatabaseAndSharedPref(primarySource, secondarySource);
        } else {
            copyDatabaseAndSharedPref(secondarySource, primarySource);
        }

    }

    public void backUpDatabase(int source) {
        long startTime = System.currentTimeMillis();
        userName = getUser();
        if (source == PRIMARY) {
            File backupDir = new File(BACKUP_DATABASE_PATH);
            if (!backupDir.exists()) {
                ArrayList<String> cmds = new ArrayList<>();
                cmds.add("mkdir " + Util.getSDCardPath() + "/.devappdatabase");
                cmds.add("mkdir " + BACKUP_DATABASE_PATH);
                cmds.add("chmod 771 " + BACKUP_DATABASE_PATH);
                cmds.add("chown " + userName + ":" + userName + " " + BACKUP_DATABASE_PATH);
                int i = executeCommands(cmds);
            }
            backupDatabase(DATBASE_PATH, BACKUP_DATABASE_PATH);
            copyOnlySharedPrefs(primarySource, secondarySource);
        } else {
            File primaryDir = new File(DATBASE_PATH);
            if (!primaryDir.exists()) {
                ArrayList<String> cmds = new ArrayList<>();
                cmds.add("mkdir " + DATBASE_PATH);
                cmds.add("chmod 771 " + DATBASE_PATH);
                cmds.add("chown " + userName + ":" + userName + " " + DATBASE_PATH);
                int i = executeCommands(cmds);
            }
            backupDatabase(BACKUP_DATABASE_PATH, DATBASE_PATH);
            copyOnlySharedPrefs(secondarySource, primarySource);
        }
        long endTime = System.currentTimeMillis();
        Log.d("prfmc", "DB & SP Backup : " + (endTime - startTime));
    }

    private void backupDatabase(String source, String destination) {
        long startTime = System.currentTimeMillis();
        ArrayList<String> commands = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmm");
        String fileName = secondarySource + "backup" + dateFormat.format(new Date()) + ".db";
        commands.add("sqlite3 " + source + "/" + SQLite_DATABASE_NAME + " \".backup '" + fileName + "'\"");
        int i = -1;
        Log.d("Backup", "Trying to create DB dump " + commands.get(0));
        i = executeCommands(commands);
        Log.d("Backup", "Backup exited with " + i);

        if (i == 0) {
            ArrayList<String> restoreCmds = new ArrayList<>();
            restoreCmds.add("sqlite3 " + destination + "/" + SQLite_DATABASE_NAME + " \".restore '" + fileName + "'\"");
            i = executeCommands(restoreCmds);
            ArrayList<String> permissionCmds = new ArrayList<>();
            permissionCmds.add("chmod 660 " + destination + "/" + SQLite_DATABASE_NAME);
            permissionCmds.add("chown " + userName + ":" + userName + " " + destination + "/" + SQLite_DATABASE_NAME);
            i = executeCommands(permissionCmds);
        }
        try {
            new File(fileName).delete();
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            Log.d("prfmc", "DB " + source + " (Delete Dump Failed) : " + (endTime - startTime));
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        Log.d("prfmc", "DB " + source + " : " + (endTime - startTime));
    }

    public int executeCommands(ArrayList<String> cmds) {
        int i = -1;
        try {
            java.lang.Process process = Runtime.getRuntime().exec("su");
            BufferedReader bis = new BufferedReader(new InputStreamReader(process.getInputStream()));
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            for (String tmpCmd : cmds) {
                Log.d("Backup", "$ " + tmpCmd);
                os.writeBytes(tmpCmd + "\n");
            }
            os.writeBytes("exit\n");
            os.flush();
            os.close();
            String line = "";
            while ((line = bis.readLine()) != null) {
                Log.d("Output :", line);
            }
            i = process.waitFor();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return i;
    }

    private String getUser() {
        int i = -1;
        String user = "";
        android.os.Process process = new android.os.Process();
        int uid = process.myUid();
        uid = uid - 10000;
        user = "u0_a" + String.valueOf(uid);
        /*try {
            Process process = Runtime.getRuntime().exec("id");
            BufferedReader bis = new BufferedReader(new InputStreamReader(process.getInputStream()));
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            os.writeBytes("id");
            os.writeBytes("exit\n");
            os.flush();
            os.close();
            String line = "";
            while ((line = bis.readLine()) != null){
                Log.d("Output :", line);
                user = line.substring((line.indexOf("(")+1), line.indexOf(")"));
            }
            i = process.waitFor();


        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        return user;
    }

    public void copyOnlySharedPrefs(String source, String destination) {
        ArrayList<String> commands = new ArrayList<>();
        /*File database = Environment.getExternalStorageDirectory();
        commands.add("mkdir " + database + "/.devappdatabase");*/
        commands.add("cp -pr " + source + "shared_prefs " + destination);
        try {
            Process process = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            for (String tmpCmd : commands) {
                os.writeBytes(tmpCmd + "\n");
            }
            os.writeBytes("exit\n");
            os.flush();
            os.close();
            int i = process.waitFor();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void copyDatabaseAndSharedPref(String source, String destination) {
        ArrayList<String> commands = new ArrayList<>();
        File database = new File(Util.getSDCardPath());

        //No need to create if already exist
        commands.add("mkdir " + database + "/.devappdatabase");

        commands.add("cp -pr " + source + "databases " + destination);
        commands.add("cp -pr " + source + "shared_prefs " + destination);

        try {
            Process process = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            for (String tmpCmd : commands) {
                os.writeBytes(tmpCmd + "\n");
            }
            os.writeBytes("exit\n");
            os.flush();
            os.close();
            int i = process.waitFor();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public int getDbVersion(boolean isPrimary) {
        if (isPrimary) {
            return getDbVersionFromFile(DATBASE_PATH + File.separator + SQLite_DATABASE_NAME);
        } else {
            return getDbVersionFromFile(BACKUP_DATABASE_PATH + File.separator + SQLite_DATABASE_NAME);
        }
    }

    public void gotoSplash(Context context) {
        Intent intent = new Intent(context, SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("SERVICE_RUNNING", true);
        context.startActivity(intent);
    }

    /////////////////delete file
    public void deleteBackupFile() {
        File file = new File(BACKUP_DATABASE_PATH);
        if (file.exists()) {
            file.delete();
        }
    }


    public boolean checkPrimaryDbIntegrity() {
        return checkDbIntegrity(DATBASE_PATH + "/" + SQLite_DATABASE_NAME);
    }

    public boolean checkSecondaryDbIntegrity() {
        return checkDbIntegrity(BACKUP_DATABASE_PATH + "/" + SQLite_DATABASE_NAME);
    }

    private boolean checkDbIntegrity(String path) {
        long startTime = System.currentTimeMillis();

        ArrayList<String> commands = new ArrayList<>();
        commands.add("sqlite3 " + path + " 'pragma integrity_check'");
        try {
            Process process = Runtime.getRuntime().exec("su");
            BufferedReader bis = new BufferedReader(new InputStreamReader(process.getInputStream()));
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            for (String tmpCmd : commands) {
                os.writeBytes(tmpCmd + "\n");
            }

            os.writeBytes("exit\n");
            os.flush();
            os.close();
            String line = "";
            while ((line = bis.readLine()) != null) {
                Log.d("Backup", line);
                if (line.equalsIgnoreCase("ok")) {
                    long endTime = System.currentTimeMillis();
                    Log.d("prfmc", "Pragma " + path + " : " + (endTime - startTime));
                    return true;
                }
            }
            int i = process.waitFor();
            Log.d("Backup", "Output " + i);


        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        Log.d("prfmc", "Pragma " + path + " : " + (endTime - startTime));
        return false;

    }

    private void copySharedPref(String source, String destination) {
        ArrayList<String> commands = new ArrayList<>();


        commands.add("cp -pr " + source + "shared_prefs " + destination);
//        commands.add("cp -pr " + source + "shared_prefs " + Environment.getExternalStorageDirectory());
        commands.add("cp -pr " + source + "shared_prefs " + Util.getSDCardPath());

        try {
            Process process = Runtime.getRuntime().exec("su");
            BufferedInputStream bis = new BufferedInputStream(process.getInputStream());
            DataInputStream is = new DataInputStream(process.getInputStream());
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            for (String tmpCmd : commands) {
                os.writeBytes(tmpCmd + "\n");
            }
            os.writeBytes("exit\n");
            os.flush();
            os.close();
            int i = process.waitFor();


        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
