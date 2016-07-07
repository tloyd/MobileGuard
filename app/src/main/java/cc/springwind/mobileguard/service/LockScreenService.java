package cc.springwind.mobileguard.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;

import cc.springwind.mobileguard.engine.ProcessInfoProvider;

/**
 * Created by HeFan on 2016/7/5.
 */
public class LockScreenService extends Service {
    private IntentFilter filter;
    private InnerReceiver receiver;

    @Override
    public void onCreate() {
        filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        receiver = new InnerReceiver();
        registerReceiver(receiver, filter);
    }

    class InnerReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ProcessInfoProvider.killAll(context);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
    }
}
