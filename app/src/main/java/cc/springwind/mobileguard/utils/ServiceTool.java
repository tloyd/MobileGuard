package cc.springwind.mobileguard.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Created by HeFan on 2016/7/5.
 */
public class ServiceTool {

    public static boolean isRunning(Context ctx,String serviceName){
        //1,获取activityMananger管理者对象,可以去获取当前手机正在运行的所有服务
        ActivityManager mAM = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        //2,获取手机中正在运行的服务集合(多少个服务)
        List<ActivityManager.RunningServiceInfo> runningServices = mAM.getRunningServices(1000);
        //3,遍历获取的所有的服务集合,拿到每一个服务的类的名称,和传递进来的类的名称作比对,如果一致,说明服务正在运行
        for (ActivityManager.RunningServiceInfo runningServiceInfo : runningServices) {
            if(serviceName.equals(runningServiceInfo.service.getClassName())){
                return true;
            }
        }
        return false;
    }
}
