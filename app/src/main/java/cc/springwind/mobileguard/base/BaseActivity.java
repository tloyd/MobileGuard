package cc.springwind.mobileguard.base;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import cc.springwind.mobileguard.R;

/**
 * Created by Administrator on 2016/6/24 0024.
 */
public class BaseActivity extends AppCompatActivity {
    protected static final String PREF_UPDATE="pref_update";

    protected NotificationManager mNotificationManager;
    protected Toast toast;
    protected SharedPreferences preferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    /**
     * activity跳转
     *
     * @param targetActivity
     */
    protected void intent2Activity(Class<? extends Activity> targetActivity) {
        Intent intent = new Intent(this, targetActivity);
        startActivity(intent);
    }

    protected void showToast(Context context, CharSequence msg) {
        if (toast != null)
            toast.setText(msg);
        else
            toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        toast.show();
    }
}
