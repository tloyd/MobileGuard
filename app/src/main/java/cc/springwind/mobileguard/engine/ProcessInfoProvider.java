package cc.springwind.mobileguard.engine;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Debug;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import cc.springwind.mobileguard.R;
import cc.springwind.mobileguard.db.entity.ProcessInfoBean;

/**
 * Created by HeFan on 2016/7/5.
 */
public class ProcessInfoProvider {

    public static int getProcessCount(Context context) {
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = mActivityManager.getRunningAppProcesses();
        return runningAppProcesses.size();
    }

    public static long getAvailSpace(Context context) {
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo outInfo = new ActivityManager.MemoryInfo();
        mActivityManager.getMemoryInfo(outInfo);
        return outInfo.availMem;
    }

    public static long getTotalSpace(Context context) {
        FileReader fileReader = null;
        BufferedReader reader = null;
        try {
            fileReader = new FileReader("proc/meminfo");
            reader = new BufferedReader(fileReader);
            String line = reader.readLine();
            char[] chars = line.toCharArray();
            StringBuffer buffer = new StringBuffer();
            for (char c : chars) {
                if (c >= '0' && c <= '9') {
                    buffer.append(c);
                }
            }
            return Long.parseLong(buffer.toString()) * 1024;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileReader != null && reader != null) {
                    fileReader.close();
                    reader.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    public static List<ProcessInfoBean> getProcessInfo(Context context) {
        List<ProcessInfoBean> processInfoList = new ArrayList<>();
        ActivityManager mAcitivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        PackageManager mPackageManager = context.getPackageManager();
        List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfoList = mAcitivityManager
                .getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : runningAppProcessInfoList) {
            ProcessInfoBean bean = new ProcessInfoBean();
            bean.packageName = info.processName;
            Debug.MemoryInfo[] memoryInfo = mAcitivityManager.getProcessMemoryInfo(new int[]{info.pid});
            Debug.MemoryInfo memoryInfoCurrent = memoryInfo[0];
            int dirty = memoryInfoCurrent.getTotalPrivateDirty();
            bean.memSize = dirty * 1024;

            try {
                ApplicationInfo applicationInfo = mPackageManager.getApplicationInfo(bean.packageName, 0);
                bean.name = applicationInfo.loadLabel(mPackageManager).toString();
                bean.icon = applicationInfo.loadIcon(mPackageManager);

                if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
                    bean.isSystem = true;
                } else {
                    bean.isSystem = false;
                }
            } catch (Exception e) {
                bean.name = info.processName;
                bean.icon = context.getResources().getDrawable(R.drawable.icon);
                bean.isSystem = true;
                e.printStackTrace();
            }
            processInfoList.add(bean);
        }
        return processInfoList;
    }

    public static void killProcess(Context context, ProcessInfoBean bean) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        manager.killBackgroundProcesses(bean.packageName);
    }

    public static void killAll(Context context) {
        List<ProcessInfoBean> beanList = getProcessInfo(context);
        for (ProcessInfoBean bean :
                beanList) {
            killProcess(context, bean);
        }
    }
}
