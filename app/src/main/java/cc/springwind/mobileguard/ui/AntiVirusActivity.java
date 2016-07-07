package cc.springwind.mobileguard.ui;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.xutils.common.util.MD5;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cc.springwind.mobileguard.R;
import cc.springwind.mobileguard.base.BaseActivity;
import cc.springwind.mobileguard.db.dao.VirusDao;

/**
 * Created by HeFan on 2016/7/7.
 */
public class AntiVirusActivity extends BaseActivity {
    @InjectView(R.id.iv_rotate_anim_pic)
    ImageView ivRotateAnimPic;
    @InjectView(R.id.tv_scan_app_name)
    TextView tvScanAppName;
    @InjectView(R.id.pgb_scan)
    ProgressBar pgbScan;
    @InjectView(R.id.ll_scan_list)
    LinearLayout llScanList;
    private int index = 0;
    private static final int SCANNING = 100;
    private static final int FINISH = 101;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == SCANNING) {
                ScanInfo scanInfo = (ScanInfo) msg.obj;
                textView = new TextView(getApplicationContext());
                tvScanAppName.setText(scanInfo.name);
                if (scanInfo.isVirus) {
                    textView.setText(scanInfo.name);
                    textView.setTextColor(Color.RED);
                } else {
                    textView.setText(scanInfo.name);
                    textView.setTextColor(Color.BLACK);
                }
                llScanList.addView(textView, 0);
            } else if (msg.what == FINISH) {
                tvScanAppName.setText("扫面完成");
                ivRotateAnimPic.clearAnimation();
                uninstallVirusApp();
            }
        }
    };
    private ArrayList<ScanInfo> virusAppList;

    private void uninstallVirusApp() {
        for (ScanInfo info :
                virusAppList) {
            Intent intent = new Intent(Intent.ACTION_DELETE);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setData(Uri.parse("package:" + info.packageName));
            startActivity(intent);
        }
    }

    private TextView textView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anti_virus);
        ButterKnife.inject(this);

        initAnimation();
        scanVirus();
    }

    private void scanVirus() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                List<String> virusMD5List = VirusDao.getVirusMD5List();

                PackageManager packageManager = getPackageManager();
                List<PackageInfo> installedPackages = packageManager.getInstalledPackages(PackageManager
                        .GET_SIGNATURES);

                virusAppList = new ArrayList<>();
                ArrayList<ScanInfo> allAppList = new ArrayList<>();

                pgbScan.setMax(installedPackages.size());

                for (PackageInfo info :
                        installedPackages) {
                    ScanInfo scanInfo = new ScanInfo();
                    Signature[] signatures = info.signatures;
                    Signature signature = signatures[0];
                    String s = signature.toCharsString();
                    String md5 = MD5.md5(s);
                    if (virusMD5List.contains(md5)) {
                        scanInfo.isVirus = true;
                        virusAppList.add(scanInfo);
                    } else {
                        scanInfo.isVirus = false;
                    }
                    scanInfo.packageName = info.packageName;
                    scanInfo.name = info.applicationInfo.loadLabel(packageManager).toString();
                    allAppList.add(scanInfo);
                    index++;
                    pgbScan.setProgress(index);
                    try {
                        Thread.sleep(50 + new Random().nextInt(100));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Message me = Message.obtain();
                    me.what = SCANNING;
                    me.obj = scanInfo;
                    mHandler.sendMessage(me);
                }
                Message me = Message.obtain();
                me.what = FINISH;
                mHandler.sendMessage(me);

            }
        }.start();
    }

    class ScanInfo {
        public boolean isVirus;
        public String packageName;
        public String name;
    }

    private void initAnimation() {
        RotateAnimation rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation
                .RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(1000);
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        rotateAnimation.setFillAfter(true);
        ivRotateAnimPic.setAnimation(rotateAnimation);
    }


}
