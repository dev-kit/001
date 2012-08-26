package com.bg.check.ui;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.text.TextUtils;

import com.bg.check.R;

/**
 * …Ë÷√ΩÁ√Ê°£
 */
public class SettingActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {

    private static final String PREFERENCE_KEY_EDIT_IP = "edit_ip";
    private static final String PREFERENCE_KEY_EDIT_PORT = "edit_port";

    private EditTextPreference mEditIp;
    private EditTextPreference mEditPort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        final PreferenceScreen preference = getPreferenceScreen();
        preference.getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        initPreference(preference);
    }

    @Override
    protected void onDestroy() {
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }

    private void initPreference(PreferenceScreen preference) {
        initIp(preference);
        initPort(preference);
    }

    private void initIp(PreferenceScreen preference) {
        mEditIp = (EditTextPreference) preference.findPreference(PREFERENCE_KEY_EDIT_IP);
        final String ip = mEditIp.getText();
        if (TextUtils.isEmpty(ip)) {
            mEditIp.setSummary(R.string.setting_ip_summary);
        } else {
            mEditIp.setSummary(ip);
        }
    }

    private void initPort(PreferenceScreen preference) {
        mEditPort = (EditTextPreference) preference.findPreference(PREFERENCE_KEY_EDIT_PORT);
        final String port = mEditPort.getText();
        if (TextUtils.isEmpty(port)) {
            mEditPort.setSummary(R.string.setting_port_summary);
        } else {
            mEditPort.setSummary(port);
        }
    }

    public void onSharedPreferenceChanged(SharedPreferences preference, String key) {
        if (PREFERENCE_KEY_EDIT_IP.equals(key)) {
            final String ip = preference.getString(key, null);
            if (TextUtils.isEmpty(ip)) {
                mEditIp.setSummary(R.string.setting_ip_summary);
            } else {
                mEditIp.setSummary(ip);
            }
        } else if (PREFERENCE_KEY_EDIT_PORT.equals(key)) {
            final String port = preference.getString(key, null);
            if (TextUtils.isEmpty(port)) {
                mEditPort.setSummary(R.string.setting_port_summary);
            } else {
                mEditPort.setSummary(port);
            }
        }
    }
}
