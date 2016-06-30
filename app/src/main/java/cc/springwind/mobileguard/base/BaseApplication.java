package cc.springwind.mobileguard.base;

import android.app.Application;

import org.xutils.x;

/**
 * Created by HeFan on 2016/6/24 0024.
 */
public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
    }
}
