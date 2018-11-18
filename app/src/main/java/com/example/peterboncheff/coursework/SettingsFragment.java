package com.example.peterboncheff.coursework;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.*;

import static android.support.v7.preference.Preference.*;

public class SettingsFragment extends PreferenceFragmentCompat implements OnPreferenceChangeListener{

    private final String USERNAME_PREFERENCE_KEY_ID = "display_name_pref", PASSWORD_PREFERENCE_KEY_ID = "display_password_pref";
    private final String TEMPERATURE_PREFERENCE_KEY_ID = "temperature_preference_key";
    private final static String SETTINGS_TAG = SettingsFragment.class.getName();
    public final static String SETTINGS_SHARED_PREFERENCES_FILE_NAME = SETTINGS_TAG + ".SETTINGS_SHARED_PREFERENCES_FILE_NAME";

    private EditTextPreference usernamePreference, passwordPreference;
    private SwitchPreference temperaturePreference;
    private User currentUser;

    @Override
    public void onCreatePreferences(Bundle bundle, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
        getPreferenceManager().setSharedPreferencesName(SETTINGS_SHARED_PREFERENCES_FILE_NAME);
        addPreferencesFromResource(R.xml.preferences);
        init();
    }


    private void init(){

        usernamePreference = (EditTextPreference) findPreference(USERNAME_PREFERENCE_KEY_ID);
        passwordPreference = (EditTextPreference) findPreference(PASSWORD_PREFERENCE_KEY_ID);
        temperaturePreference = (SwitchPreference) findPreference(TEMPERATURE_PREFERENCE_KEY_ID);
        usernamePreference.setOnPreferenceChangeListener(this);

    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        final String USERNAME_SUMMARY = "Current username: " , PASSWORD_SUMMARY = "Current password: ";
        final String TEMPERATURE_FAHRENHEIT = "Fahrenheit";
        if(preference == usernamePreference){
            usernamePreference.setSummary(USERNAME_SUMMARY + String.valueOf(newValue));
            //update username in database;
        }else if(preference == passwordPreference){
            passwordPreference.setSummary(PASSWORD_SUMMARY + String.valueOf(newValue));
            //update password in database
        }else if(preference == temperaturePreference){
            MainActivity.DISPLAY_TEMPERATURE_IN_CELSIUS = (temperaturePreference.getSummary() != TEMPERATURE_FAHRENHEIT);
            //if true - display temperature in Fahrenheit else in Celsius(default);
        }
        return false;
    }
}
