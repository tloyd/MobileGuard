package cc.springwind.mobileguard.ui;

import android.content.Intent;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Random;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cc.springwind.mobileguard.R;
import cc.springwind.mobileguard.base.BaseActivity;

/**
 * Created by HeFan on 2016/7/7.
 */
public class CacheClearActivity extends BaseActivity {

    @InjectView(R.id.bt_clear)
    Button btClear;
    @InjectView(R.id.pb_bar)
    ProgressBar pbBar;
    @InjectView(R.id.tv_name)
    TextView tvName;
    @InjectView(R.id.ll_add_text)
    LinearLayout llAddText;

    private int index = 0;
    private static final int CLEAR_CACHE = 103;
    private static final int UPDATE_CACHE_APP = 102;
    private static final int CHECK_APP_CACHE = 100;
    private static final int CHECK_FINISH = 101;
    private PackageManager packageManager;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_CACHE_APP:
                    View view = View.inflate(getApplicationContext(), R.layout.linearlayout_cache_item, null);
                    ImageView iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
                    TextView tv_item_name = (TextView) view.findViewById(R.id.tv_name);
                    TextView tv_memory_info = (TextView) view.findViewById(R.id.tv_memory_info);
                    ImageView iv_delete = (ImageView) view.findViewById(R.id.iv_delete);

                    final CacheInfo cacheInfo = (CacheInfo) msg.obj;
                    iv_icon.setBackgroundDrawable(cacheInfo.icon);
                    tv_item_name.setText(cacheInfo.name);
                    tv_memory_info.setText(Formatter.formatFileSize(getApplicationContext(), cacheInfo.cacheSize));
                    iv_delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                            intent.setData(Uri.parse("package:" + cacheInfo.packagename));
                            startActivity(intent);
                        }
                    });
                    llAddText.addView(view, 0);
                    break;
                case CHECK_APP_CACHE:
                    tvName.setText((String) msg.obj);
                    break;
                case CHECK_FINISH:
                    tvName.setText("扫描完成");
                    break;
                case CLEAR_CACHE:
                    llAddText.removeAllViews();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cache_clear);
        ButterKnife.inject(this);

        initData();
    }

    private void initData() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                packageManager = getPackageManager();
                List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);
                pbBar.setMax(installedPackages.size());
                for (PackageInfo info :
                        installedPackages) {
                    String packageName = info.packageName;
                    getPackageCache(packageName);
                    try {
                        Thread.sleep(50 + new Random().nextInt(100));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    index++;
                    pbBar.setProgress(index);

                    Message message = Message.obtain();
                    message.what = CHECK_APP_CACHE;
                    try {
                        message.obj = packageManager.getApplicationInfo(packageName, 0).loadLabel(packageManager)
                                .toString();
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    mHandler.sendMessage(message);
                }
                Message msg = Message.obtain();
                msg.what = CHECK_FINISH;
                mHandler.sendMessage(msg);
            }
        }.start();
    }

    private void getPackageCache(final String packageName) {
        IPackageStatsObserver.Stub observer = new IPackageStatsObserver.Stub() {
            @Override
            public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
                long cacheSize = pStats.cacheSize;
                if (cacheSize > 0) {
                    Message obtain = Message.obtain();
                    obtain.what = UPDATE_CACHE_APP;
                    CacheInfo info = new CacheInfo();
                    try {
                        info.cacheSize = cacheSize;
                        info.icon = packageManager.getApplicationInfo(pStats.packageName, 0).loadIcon(packageManager);
                        info.name = packageManager.getApplicationInfo(pStats.packageName, 0).loadLabel
                                (packageManager).toString();
                        info.packagename = pStats.packageName;
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    obtain.obj = info;
                    mHandler.sendMessage(obtain);
                }
            }
        };
        //1.获取指定类的字节码文件
        try {
            Class<?> clazz = Class.forName("android.content.pm.PackageManager");
            //2.获取调用方法对象
            Method method = clazz.getMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);
            //3.获取对象调用方法
            method.invoke(packageManager, packageName, observer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.bt_clear)
    public void onClick() {
        try {
            Class<?> clazz = Class.forName("android.content.pm.PackageManager");
            Method method = clazz.getMethod("freeStorageAndNotify", long.class, IPackageDataObserver.class);
            method.invoke(packageManager, Long.MAX_VALUE, new IPackageDataObserver.Stub() {
                @Override
                public void onRemoveCompleted(String packageName, boolean succeeded)
                        throws RemoteException {
                    Message msg = Message.obtain();
                    msg.what = CLEAR_CACHE;
                    mHandler.sendMessage(msg);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class CacheInfo {
        public String name;
        public Drawable icon;
        public String packagename;
        public long cacheSize;
    }
}
