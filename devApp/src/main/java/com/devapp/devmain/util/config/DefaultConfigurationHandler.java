package com.devapp.devmain.util.config;

import android.content.ContentValues;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;

import com.devapp.devmain.DevAppApplication;
import com.devapp.devmain.helper.DatabaseEntity;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.util.logger.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Properties;
import java.util.Set;

import static com.devapp.devmain.helper.DatabaseEntity.CONFIGURATION_KEY;
import static com.devapp.devmain.helper.DatabaseEntity.CONFIGURATION_LAST_VALUE;
import static com.devapp.devmain.helper.DatabaseEntity.CONFIGURATION_MODIFIED_TIME;
import static com.devapp.devmain.helper.DatabaseEntity.CONFIGURATION_STATUS;
import static com.devapp.devmain.helper.DatabaseEntity.CONFIGURATION_VALUE;
import static com.devapp.devmain.helper.DatabaseEntity.TABLE_CONFIGURATION;

/**
 * TODO Add javadocs
 */
public class DefaultConfigurationHandler {

    public static final String TAG = "DefaultConfigurationHandler";

    public static final String DEFAULT_CONFIG_FILENAME = "tab-app-configurations.properties";

    private static DefaultConfigurationHandler defaultConfigurationHandler = null;

    private static SQLiteDatabase sqliteDatabase;
    // tab app configurations
    private static Properties properties;

    private DefaultConfigurationHandler() {
        // does nothing
    }

    public static DefaultConfigurationHandler getInstance() {
        if (defaultConfigurationHandler == null) {
            synchronized (DefaultConfigurationHandler.class) {
                defaultConfigurationHandler = new DefaultConfigurationHandler();
            }
        }
        return defaultConfigurationHandler;
    }

    public static void initConfigTable(SQLiteDatabase sqlDb) {
        sqliteDatabase = sqlDb;
        Log.i("DefaultConfigurationHandler", "initialize db...");
    }

    /**
     * This method returns the current configurations used by the app.
     *
     * @return
     */
    public static Properties getConfig() {
        if (properties == null || properties.isEmpty()) {
            properties = DatabaseHandler.getDatabaseInstance().getAllConfigurations();
        }
        return properties;
    }

    /**
     * This method will read the default configuration from property file under assets folder
     * and insert the same into config table in db.
     */
    public static void loadAllConfigurations() {

        //Load the properties
        Properties props = loadConfigProperties(DEFAULT_CONFIG_FILENAME);
        Log.i(TAG, "config properties with default values are loaded");

        //Load the config properties into configuration tables in primary and secondary data stores
        loadAllConfigurations(props);

    }

    // Load properties into config table
    private static void loadAllConfigurations(Properties configs) {

        Set<String> keys = configs.stringPropertyNames();
        for (String configKey : keys) {
            ContentValues values = new ContentValues();
            values.put(CONFIGURATION_KEY, configKey);
            values.put(CONFIGURATION_VALUE, configs.getProperty(configKey));
            values.put(CONFIGURATION_LAST_VALUE, configs.getProperty(configKey));
            values.put(CONFIGURATION_MODIFIED_TIME, Calendar.getInstance().getTimeInMillis());
            values.put(CONFIGURATION_STATUS, DatabaseEntity.FARMER_UNSENT_CODE);
            sqliteDatabase.insertWithOnConflict(TABLE_CONFIGURATION, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        }
        com.devapp.devmain.util.logger.Log.i(TAG, "loaded all configurations into primary data store");
    }

    /**
     * This method reads the property file under assets folder and returns
     * an instance of Properties - which is populated with the properties defined
     * in the property file.
     *
     * @param file - property file location
     * @return Properties - which is populated with the properties defined
     * in the property file.
     */
    public static Properties loadConfigProperties(String file) {
        Properties properties = null;
        try {
            properties = new Properties();
            AssetManager assetManager = DevAppApplication.getAmcuContext().getAssets();
            InputStream inputStream = assetManager.open(file);
            properties.load(inputStream);
        } catch (IOException e) {
            Log.e(TAG, "Failed to load config file with default values due to:" + e.getMessage());
            //FIXME Handle exceptions
            e.printStackTrace();
        }
        return properties;
    }

    /**
     * This method writes the latest config properties back int the prop file under assets folder.
     *
     * @param config - Latest configurations.
     * @return isReloaded - True indicates that the file has been updated. Otherwise,
     * file is not updated with latest default values.
     */
    public static boolean reloadConfigProperties(Properties config) {
        //TODO write properties back in prop file in assets folder.
        return false;
    }


    private void handlingNullValueParams() {


    }

}
