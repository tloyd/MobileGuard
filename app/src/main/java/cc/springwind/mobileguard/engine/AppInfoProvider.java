package cc.springwind.mobileguard.engine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.List;

import cc.springwind.mobileguard.db.entity.AppInfoBean;

/**
 * Created by HeFan on 2016/7/4.
 */
public class AppInfoProvider {
    public static List<AppInfoBean> getAppInfoList(Context context) {
        PackageManager mPackageManager = context.getPackageManager();
        List<PackageInfo> packages = mPackageManager.getInstalledPackages(0);
        List<AppInfoBean> mAppInfoList = new ArrayList<>();
        for (PackageInfo packageInfo : packages) {
            AppInfoBean appInfoBean = new AppInfoBean();
            appInfoBean.packageName = packageInfo.packageName;
            ApplicationInfo applicationInfo = packageInfo.applicationInfo;
            appInfoBean.name = applicationInfo.loadLabel(mPackageManager).toString();
            appInfoBean.icon = applicationInfo.loadIcon(mPackageManager);
            if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
                //系统应用
                appInfoBean.isSystem = true;
            } else {
                //非系统应用
                appInfoBean.isSystem = false;
            }
            if((applicationInfo.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE)==ApplicationInfo.FLAG_EXTERNAL_STORAGE){
                //系统应用
                appInfoBean.isSdCard = true;
            }else{
                //非系统应用
                appInfoBean.isSdCard = false;
            }
            mAppInfoList.add(appInfoBean);
        }
        return mAppInfoList;
    }
}
