package cc.springwind.mobileguard.ui;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import cc.springwind.mobileguard.R;
import cc.springwind.mobileguard.service.AddressService;
import cc.springwind.mobileguard.service.AppLockWatchDogService;
import cc.springwind.mobileguard.service.BlackListService;
import cc.springwind.mobileguard.utils.ServiceTool;

/**
 * Created by HeFan on 2016/6/25 0025.
 */
public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

    private Preference pref_app_lock;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        findPreference("pref_comming_call_location").setOnPreferenceChangeListener(this);
        findPreference("pref_toast_style").setOnPreferenceChangeListener(this);
        findPreference("pref_black_list").setOnPreferenceChangeListener(this);
        pref_app_lock = findPreference("pref_app_lock");
        pref_app_lock.setOnPreferenceChangeListener(this);
//        initPreference();
    }

    private void initPreference() {
        boolean running = ServiceTool.isRunning(getActivity().getApplicationContext(), "cc.springwind.mobileguard" +
                ".service.AppLockWatchDogService");
        if (running) {
            pref_app_lock.setDefaultValue(true);
        } else {
            pref_app_lock.setDefaultValue(false);
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference.getKey().equals("pref_comming_call_location")) {
            if ((Boolean) newValue) {
                getActivity().startService(new Intent(getActivity(), AddressService.class));
            } else {
                getActivity().stopService(new Intent(getActivity(), AddressService.class));
            }
        }
        if (preference.getKey().equals("pref_black_list")) {
            if ((Boolean) newValue) {
                getActivity().startService(new Intent(getActivity(), BlackListService.class));
            } else {
                getActivity().stopService(new Intent(getActivity(), BlackListService.class));
            }
        }
        if (preference.getKey().equals("pref_app_lock")) {
            if ((Boolean) newValue) {
                getActivity().startService(new Intent(getActivity(), AppLockWatchDogService.class));
            } else {
                getActivity().stopService(new Intent(getActivity(), AppLockWatchDogService.class));
            }
        }
        return true;
    }
}
