package com.devapp.devmain.dbbackup;

import com.devapp.devmain.user.Util;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.Date;

/**
 * Created by yyy on 8/8/16.
 */
public class SecondaryDBObject {
    public final String SQLite_DATABASE_NAME = "devappUsb";
    public int versionNumber;
    public String filePath;
    public long fileSize;
    public long fileLastModified;
    public String BACKUP_DATABASE_PATH =
            Util.getSDCardPath() + "/.devappdatabase/databases";
    public boolean fileExists = false;

    public void setSecondaryDB() {
        long millisec;
        try {
            // create new file
            File deviceFile = new File(BACKUP_DATABASE_PATH + File.separator + SQLite_DATABASE_NAME);

            // true if the file path is a file, else false
            try {
                fileExists = deviceFile.exists();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // if path exists
            if (fileExists) {
                // returns the time file was last modified
                millisec = deviceFile.lastModified();
                fileLastModified = millisec;
                fileSize = deviceFile.length();
                // date and time
                Date dt = new Date(millisec);
                // path
                filePath = deviceFile.getPath();
                System.out.print("FileName with path:" + filePath + " last modified at: " + dt);
                // geing version number db
                RandomAccessFile fp = new RandomAccessFile(deviceFile, "r");
                fp.seek(60);
                byte[] buff = new byte[4];
                fp.read(buff, 0, 4);
                versionNumber = ByteBuffer.wrap(buff).getInt();
                fp.close();
            }
        } catch (Exception e) {
            // if any error occurs
            e.printStackTrace();
        }
    }

    public boolean isFileExist() {
        File deviceFile = new File(BACKUP_DATABASE_PATH + File.separator + SQLite_DATABASE_NAME);

        // true if the file path is a file, else false
        try {
            fileExists = deviceFile.exists();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileExists;
    }


}
