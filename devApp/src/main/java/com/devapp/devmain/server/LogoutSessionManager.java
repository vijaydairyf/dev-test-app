package com.devapp.devmain.server;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class LogoutSessionManager {
    // Sharedpref file name
    private static final String PREF_NAME = "logoutSessionPref";
    private static final String KEY_EMAIL = "userEmail";
    private static final String KEY_PASSWORD = "userPassword";
    private static final String KEY_IS_PASSWORD_REMEMBERED = "rememberPassword";
    // Shared Preferences
    SharedPreferences pref;
    // Editor for Shared preferences
    Editor editor;
    // Context
    Context _context;
    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Constructor
    public LogoutSessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
        editor.commit();
    }

    public String getUserId() {
        return pref.getString(KEY_EMAIL, null);
    }

    public String getUserPassword() {
        return pref.getString(KEY_PASSWORD, null);
    }

    /**
     * Clear session details
     */
    public void logoutUser() {
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();
        // SynchronizedArrayList.getInstance().clear();
    }

    public void createLoginSession(String email, String password) {

        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PASSWORD, password);

        // commit changes
        editor.commit();
    }

    // ///////////////Remember password/////////////////////
    public void setIsRememberPassword() {
        editor.putBoolean(KEY_IS_PASSWORD_REMEMBERED, true);
        // commit changes
        editor.commit();
    }

    public boolean isPasswordRemembered() {
        return pref.getBoolean(KEY_IS_PASSWORD_REMEMBERED, false);
    }

}
