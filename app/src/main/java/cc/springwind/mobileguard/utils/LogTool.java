package cc.springwind.mobileguard.utils;

import android.util.Log;

/**
 * Created by HeFan on 2016/7/3.
 */
public class LogTool {
    private static final boolean DEBUG = true;
    private static final String TAG = "-->>";

    public static void debug(String msg) {
        if (DEBUG) {
            Log.d(TAG, msg);
        }
    }

    public static void info(String msg) {
        if (DEBUG) {
            Log.i(TAG, msg);
        }
    }

    public static void error(String msg) {
        if (DEBUG) {
            Log.e(TAG, msg);
        }
    }

    public static void warn(String msg) {
        if (DEBUG) {
            Log.w(TAG, msg);
        }
    }
}
