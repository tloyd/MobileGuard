package cc.springwind.mobileguard.ui;

import android.app.Notification;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.widget.TextView;

import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cc.springwind.mobileguard.R;
import cc.springwind.mobileguard.base.BaseActivity;
import cc.springwind.mobileguard.utils.StreamTool;

/**
 * Created by HeFan on 2016/6/24 0024.
 */
public class SplashActivity extends BaseActivity {
    @InjectView(R.id.tv_version)
    TextView tvVersion;

    private static final String PATH = "http://192.168.1.12:8080/version.json";

    private static final int DOWNLOAD_FINISHED = 6;
    private static final int DOWNLOADING = 5;
    private static final int URL_ERROR = 2;
    private static final int UPDATE_CODE = 0;
    private static final int JSON_ERROR = 4;
    private static final int IO_ERROR = 3;
    private static final int ENTER_HOME = 1;

    private Notification.Builder builder;
    private PackageInfo packageInfo;

    private int mLocalVersionCode;
    private String jsonString;
    private String versionCode;
    private String versionName;
    private String versionDesc;
    private String downloadUrl;
    private boolean isUpdate;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_CODE:
                    showUpdateDialog();
                    break;
                case ENTER_HOME:
                    intent2Activity(MainActivity.class);
                    finish();
                    break;
                case URL_ERROR:
                    showToast(getApplicationContext(), getString(R.string.url_error_msg));
                    intent2Activity(MainActivity.class);
                    finish();
                    break;
                case IO_ERROR:
                    break;
                case JSON_ERROR:
                    break;
                case DOWNLOADING:
                    builder.setContentText(getString(R.string.download_notify_text) + msg.arg1 + "%...");
                    builder.setProgress(100, msg.arg1, false);
                    mNotificationManager.notify(1001, builder.build());
                    break;
                case DOWNLOAD_FINISHED:
                    builder.setTicker(getString(R.string.download_finished_notify_ticker));
                    builder.setContentTitle(getString(R.string.download_finished_notify_title));
                    builder.setContentText(getString(R.string.download_finished_notify_text));
                    mNotificationManager.notify(1001, builder.build());
                    break;
            }
        }
    };
    private long startTime;
    private long endTime;

    /**
     * 显示是否更新对话框
     */
    private void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.mipmap.ic_launcher).
                setTitle(R.string.update_dialog_title).
                setMessage(R.string.update_dialog_message).
                setPositiveButton(R.string.update_dialog_positivebutton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        downloadApk();
                        finish();
                        intent2Activity(MainActivity.class);
                    }
                }).setNegativeButton(R.string.update_dialog_negativebutton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                intent2Activity(MainActivity.class);
            }
        }).setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                intent2Activity(MainActivity.class);
            }
        }).show();
    }

    /**
     * 下载更新应用
     */
    private void downloadApk() {
        RequestParams mRequestParams = new RequestParams("http://ddmyapp.cc.tc.qq" +
                ".com/16891/7CB442CEA75F390D9B6899AA4545E0D1.apk?mkey=576d19cc21021ace&f=8e5d&c=0&fsname=com" +
                ".jikexueyuan.geekacademy_4.2.1-971ac41_423.apk&p=.apk");
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
            mRequestParams.setSaveFilePath(filePath);
        } else {
            mRequestParams.setSaveFilePath(getFilesDir().getAbsolutePath());
        }
        mRequestParams.setAutoRename(true);
        Callback.Cancelable post = x.http().post(mRequestParams, new Callback.ProgressCallback<File>() {

            @Override
            public void onSuccess(File result) {
                installApk(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ex.printStackTrace();
            }

            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onFinished() {
                Message msg = Message.obtain();
                msg.what = DOWNLOAD_FINISHED;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onWaiting() {
            }

            @Override
            public void onStarted() {

            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
                Message msg = Message.obtain();
                int m = (int) ((current * 100 / total));
                System.out.println("-->>current:" + current + "/total:" + total + "=" + m);
                msg.what = DOWNLOADING;
                msg.arg1 = m;
                mHandler.sendMessage(msg);
            }
        });
    }

    /**
     * 安装apk文件
     *
     * @param result
     */
    private void installApk(File result) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(result), "application/vnd.android.package-archive");
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        intent2Activity(MainActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.inject(this);
        isUpdate = preferences.getBoolean(PREF_UPDATE, true);
        initData();
        initAddressDB("address.db");
    }

    /**
     * 将assets中的数据库文件读取到应用内存中去
     *
     * @param dbName
     */
    private void initAddressDB(String dbName) {
        File files = getFilesDir();
        File file = new File(files, dbName);
        if (file.exists()) {
            return;
        }
        InputStream mInputStream = null;
        FileOutputStream mFileOutputStream = null;
        try {
            mInputStream = getAssets().open(dbName);
            //3,将读取的内容写入到指定文件夹的文件中去
            mFileOutputStream = new FileOutputStream(file);
            //4,每次的读取内容大小
            byte[] bs = new byte[1024];
            int temp = -1;
            while ((temp = mInputStream.read(bs)) != -1) {
                mFileOutputStream.write(bs, 0, temp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mInputStream != null && mFileOutputStream != null) {
                try {
                    mInputStream.close();
                    mFileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 初始化版本数据
     */
    private void initData() {
        this.tvVersion.setText(getString(R.string.splash_activity_versionstr) + getVersionName());
        mLocalVersionCode = getVersionCode();
        builder = new Notification.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher).setContentTitle(getString(R.string.download_notify_title))
                .setContentText(getString(R.string.download_notify_text)).setTicker(getString(R.string
                .download_notify_ticker));
        if (isUpdate) {
            checkVersion();
        } else {
            mHandler.sendEmptyMessageDelayed(ENTER_HOME, 2000);
        }
    }

    /**
     * 校验版本号
     */
    private void checkVersion() {
        new Thread() {
            @Override
            public void run() {
                Message msg = Message.obtain();
                startTime = System.currentTimeMillis();
                try {
                    URL url = new URL(PATH);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout(2000);
                    connection.setReadTimeout(2000);
                    if (connection.getResponseCode() == 200) {
                        InputStream is = connection.getInputStream();
                        jsonString = StreamTool.stream2String(is);
                        JSONObject jsonObject = new JSONObject(jsonString);
                        versionCode = jsonObject.getString("versionCode");
                        versionName = jsonObject.getString("versionName");
                        versionDesc = jsonObject.getString("versionDesc");
                        downloadUrl = jsonObject.getString("downloadUrl");
                        System.out.println("checkVersion-->>" + versionCode + ":" + versionName + ":" + versionDesc +
                                ":" + downloadUrl);
                        if (mLocalVersionCode < Integer.parseInt(versionCode)) {
                            msg.what = UPDATE_CODE;
                        } else {
                            msg.what = ENTER_HOME;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    msg.what = URL_ERROR;
                } finally {
                    endTime = System.currentTimeMillis();
                    if (endTime - startTime < 4000) {
                        try {
                            Thread.sleep(4000 - (endTime - startTime));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    mHandler.sendMessage(msg);
                }
            }
        }.start();
    }

    /**
     * 获取本地版本号
     *
     * @return
     */
    private int getVersionCode() {
        return packageInfo.versionCode;
    }

    /**
     * 获取版本名称
     *
     * @return
     */
    private String getVersionName() {
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "0.0.0";
    }


}
