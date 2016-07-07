package cc.springwind.mobileguard.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by HeFan on 2016/7/3.
 */
public class PermissionTool {

    /**
     * 获取无返回操作的系统权限
     *
     * @param context
     * @param permission
     */
    public static void checkPermissionIdle( Activity activity, String permission){
        if (ContextCompat.checkSelfPermission(activity, permission)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{permission},
                    1);
        }
    }

}
