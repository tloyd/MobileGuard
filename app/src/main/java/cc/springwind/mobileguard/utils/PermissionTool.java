package cc.springwind.mobileguard.utils;

import android.app.Activity;
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
    public static void checkPermissionIdle(Activity context, String permission){
        if (ContextCompat.checkSelfPermission(context, permission)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context,
                    new String[]{permission},
                    1);
        }
    }

}
