package cc.springwind.mobileguard.ui;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import cc.springwind.mobileguard.R;

/**
 * Created by HeFan on 2016/6/25 0025.
 */
public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
