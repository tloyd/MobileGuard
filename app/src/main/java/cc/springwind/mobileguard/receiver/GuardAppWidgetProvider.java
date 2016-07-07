package cc.springwind.mobileguard.receiver;


import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import cc.springwind.mobileguard.service.UpdateWidgetService;
import cc.springwind.mobileguard.utils.LogTool;

/**
 * Created by HeFan on 2016/7/5.
 */
public class GuardAppWidgetProvider extends AppWidgetProvider {
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        LogTool.debug("onReceive");
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        context.startService(new Intent(context,UpdateWidgetService.class));
        LogTool.debug("onUpdate");
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle
            newOptions) {
        context.startService(new Intent(context,UpdateWidgetService.class));
        LogTool.debug("onAppWidgetOptionsChanged");
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        LogTool.debug("onDeleted");
    }

    @Override
    public void onEnabled(Context context) {
        context.startService(new Intent(context,UpdateWidgetService.class));
        LogTool.debug("onEnabled");
    }

    @Override
    public void onDisabled(Context context) {
        context.stopService(new Intent(context,UpdateWidgetService.class));
        LogTool.debug("onDisabled");
    }

    @Override
    public void onRestored(Context context, int[] oldWidgetIds, int[] newWidgetIds) {
        LogTool.debug("onRestored");
    }
}
