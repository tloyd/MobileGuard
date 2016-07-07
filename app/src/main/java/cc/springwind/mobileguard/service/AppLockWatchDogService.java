package cc.springwind.mobileguard.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.List;

import cc.springwind.mobileguard.db.dao.AppLockedDao;
import cc.springwind.mobileguard.ui.EnterPwdActivity;

/**
 * Created by HeFan on 2016/7/6.
 */
public class AppLockWatchDogService extends Service {

    private AppLockedDao dao;
    private List<String> lockedApps;
    private boolean isWatch;
    private String mSkipPackageName = "";
    private InnerReceiver innerReceiver;
    private IntentFilter intentFilter;
    private InnerContentObserver innerContentObserver;

    @Override
    public void onCreate() {
        dao = AppLockedDao.getInstance(getApplicationContext());
        lockedApps = dao.findAll();
        isWatch = true;

        innerReceiver = new InnerReceiver();
        intentFilter = new IntentFilter("android.intent.action.SKIP");
        registerReceiver(innerReceiver, intentFilter);

        innerContentObserver = new InnerContentObserver(new Handler());
        getContentResolver().registerContentObserver(Uri.parse("content://applock/change"), true, innerContentObserver);

        watch();
    }

    class InnerContentObserver extends ContentObserver {

        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public InnerContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    lockedApps = dao.findAll();
                }
            }.start();
        }
    }

    class InnerReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            mSkipPackageName = intent.getStringExtra("packageName");
        }
    }

    private void watch() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                while (isWatch) {
                    ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                    List<ActivityManager.RunningTaskInfo> runningTasks = manager.getRunningTasks(1);
                    ActivityManager.RunningTaskInfo runningTaskInfo = runningTasks.get(0);
                    String packageName = runningTaskInfo.topActivity.getPackageName();
                    if (lockedApps.contains(packageName) && !packageName.equals(mSkipPackageName)) {
                        Intent intent = new Intent(getApplicationContext(), EnterPwdActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("packageName", packageName);
                        startActivity(intent);
                    }
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        isWatch = false;
        if (innerReceiver != null) {
            unregisterReceiver(innerReceiver);
        }
    }
}
