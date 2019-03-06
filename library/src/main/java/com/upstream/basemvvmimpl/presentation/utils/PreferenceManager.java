package com.upstream.basemvvmimpl.presentation.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

public class PreferenceManager {

    private final String TAG = "PreferenceManager";

    public static class Builder {
        private boolean isShared = true;
        private String filename;
        private Context context;

        public Builder setContext(Context context) {
            this.context = context;
            return this;
        }

        public Builder setShared(boolean value) {
            this.isShared = value;
            return this;
        }

        public Builder setFilename(String filename) {
            this.filename = filename;
            return this;
        }

        public PreferenceManager build() throws IllegalArgumentException {

            if (context == null) throw new IllegalArgumentException("No context!");

            if (!isShared) {
                return new PreferenceManager(context);
            } else {
                if (TextUtils.isEmpty(filename))
                    throw new IllegalArgumentException("Filename cannot be empty!");
                else {
                    filename = context.getPackageName() + "." + filename;
                    return new PreferenceManager(context, filename);
                }
            }
        }
    }

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private PreferenceManager(Context context, String preference_filename) throws IllegalArgumentException {
        sharedPreferences = context.getSharedPreferences(preference_filename, Context.MODE_PRIVATE);
    }

    private PreferenceManager(Context context) {
        sharedPreferences = android.preference.PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean contains(String preference) {
        return sharedPreferences.contains(preference);
    }

    public void setOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener);
    }

    public void removeOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener);
    }

    public void savePreference(String preference, int value) {
        editor = sharedPreferences.edit();
        editor.putInt(preference, value);
        editor.apply();
    }

    public void savePreference(String preference, String value) {
        editor = sharedPreferences.edit();
        editor.putString(preference, value);
        editor.apply();
    }

    public void savePreference(String preference, boolean value) {
        editor = sharedPreferences.edit();
        editor.putBoolean(preference, value);
        editor.apply();
    }

    public void savePreference(String preference, float value) {
        editor = sharedPreferences.edit();
        editor.putFloat(preference, value);
        editor.apply();
    }

    public void savePreference(String preference, long value) {
        editor = sharedPreferences.edit();
        editor.putLong(preference, value);
        editor.apply();
    }

    public String getStringPreference(String preference, String default_value) {
        return sharedPreferences.getString(preference, default_value);
    }

    public boolean getBoolPreference(String preference, boolean default_value) {
        return sharedPreferences.getBoolean(preference, default_value);
    }

    public int getIntPreference(String preference, int default_value) {
        return sharedPreferences.getInt(preference, default_value);
    }

    public long getLongPreference(String preference, long default_value) {
        return sharedPreferences.getLong(preference, default_value);
    }

    public Float getFloatPreference(String preference, float default_value) {
        return sharedPreferences.getFloat(preference, default_value);
    }
}
