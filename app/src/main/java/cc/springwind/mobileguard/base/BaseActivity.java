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
import cc.springwind.mobileguard.service.AddressService;

/**
 * Created by Administrator on 2016/6/24 0024.
 */
public class BaseActivity extends AppCompatActivity {
    protected static final String PREF_UPDATE = "pref_update";
    protected static final String PREF_COMMING_CALL_LOCATION = "pref_comming_call_location";

    protected NotificationManager mNotificationManager;
    protected Toast toast;
    protected SharedPreferences preferences;
    protected boolean isCommingCallLocationShow;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        isCommingCallLocationShow = preferences.getBoolean(PREF_COMMING_CALL_LOCATION, false);
        if (isCommingCallLocationShow) {
            startService(new Intent(getApplicationContext(), AddressService.class));
        } else {
            stopService(new Intent(getApplicationContext(), AddressService.class));
        }
    }

    /**
     * 显式Intent来执行activity跳转
     *
     * @param targetActivity
     */
    protected void intent2Activity(Class<? extends Activity> targetActivity) {
        Intent intent = new Intent(this, targetActivity);
        startActivity(intent);
    }

    /**
     * 不阻塞显示Toast信息
     *
     * @param context
     * @param msg
     */
    protected void showToast(Context context, CharSequence msg) {
        if (toast != null)
            toast.setText(msg);
        else
            toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

