package cc.springwind.mobileguard.ui;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import cc.springwind.mobileguard.R;
import cc.springwind.mobileguard.service.AddressService;

/**
 * Created by HeFan on 2016/6/25 0025.
 */
public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        findPreference("pref_comming_call_location").setOnPreferenceChangeListener(this);
        findPreference("pref_toast_style").setOnPreferenceChangeListener(this);
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
        if (preference.getKey().equals("pref_toast_style")) {

        }
        return true;
    }
}
