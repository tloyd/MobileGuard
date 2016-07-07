package cc.springwind.mobileguard.base;

import android.app.Application;
import android.os.Environment;

import org.xutils.x;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * Created by HeFan on 2016/6/24 0024.
 */
public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                ex.printStackTrace();
                String path = Environment.getExternalStorageDirectory().getAbsoluteFile() + File.separator + "error74" +
                        ".log";
                File file = new File(path);
                try {
                    PrintWriter printWriter = new PrintWriter(file);
                    ex.printStackTrace(printWriter);
                    printWriter.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                System.exit(0);
            }
        });
    }


}
