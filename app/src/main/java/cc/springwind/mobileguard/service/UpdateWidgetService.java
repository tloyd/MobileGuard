package cc.springwind.mobileguard.service;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.format.Formatter;
import android.widget.RemoteViews;

import java.util.Timer;
import java.util.TimerTask;

import cc.springwind.mobileguard.R;
import cc.springwind.mobileguard.engine.ProcessInfoProvider;
import cc.springwind.mobileguard.receiver.GuardAppWidgetProvider;

/**
 * Created by HeFan on 2016/7/5.
 */
public class UpdateWidgetService extends Service {

    private Timer timer;
    private InnerReceiver receiver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        startTimer();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        receiver = new InnerReceiver();
        registerReceiver(receiver, filter);
    }

    @Override
    public void onDestroy() {
        if (receiver!=null){
            unregisterReceiver(receiver);
        }
        cancelTimerTask();
    }

    private void startTimer() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateAppWidget();
            }
        }, 0, 5000);
    }

    private void updateAppWidget() {
        AppWidgetManager instance = AppWidgetManager.getInstance(getApplicationContext());

        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.process_widget);
        remoteViews.setTextViewText(R.id.tv_process_count, "进程总数:" + ProcessInfoProvider.getProcessCount(this));
        String strAvailSpace = Formatter.formatFileSize(this, ProcessInfoProvider.getAvailSpace(this));
        remoteViews.setTextViewText(R.id.tv_process_memory, "可用内存:" + strAvailSpace);

        Intent intent1 = new Intent("android.intent.action.HOME");
        intent1.addCategory("android.intent.category.DEFAULT");
        PendingIntent pendingIntent1 = PendingIntent.getActivity(this, 0, intent1, PendingIntent.FLAG_CANCEL_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.ll_root, pendingIntent1);

        Intent intent = new Intent("android.intent.action.KILL_BACKGROUND_PROCESS");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.btn_clear, pendingIntent);

        instance.updateAppWidget(new ComponentName(this, GuardAppWidgetProvider.class), remoteViews);
    }

    private void cancelTimerTask() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    class InnerReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                startTimer();
            } else {
                cancelTimerTask();
            }
        }
    }
}
