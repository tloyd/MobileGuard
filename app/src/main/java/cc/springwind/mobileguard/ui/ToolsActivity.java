package cc.springwind.mobileguard.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.view.View;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.OnClick;
import cc.springwind.mobileguard.R;
import cc.springwind.mobileguard.base.BaseActivity;
import cc.springwind.mobileguard.engine.SmsBackup;

/**
 * Created by HeFan on 2016/6/30.
 */
public class ToolsActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tools);
        ButterKnife.inject(this);
    }

    @OnClick({R.id.tv_query_phone_address, R.id.tv_sms_backup,R.id.tv_common_number,R.id.tv_app_lock})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_query_phone_address:
                intent2Activity(QueryAddressActivity.class);
                break;
            case R.id.tv_sms_backup:
                showSMSBackUpDialog();
                break;
            case R.id.tv_common_number:
                intent2Activity(CommonNumberActivity.class);
                break;
            case R.id.tv_app_lock:
                intent2Activity(AppLockActivity.class);
                break;
        }
    }

    private void showSMSBackUpDialog() {
        final ProgressDialog mDialog = new ProgressDialog(this);
        // TODO: 2016/7/4 国际化
        mDialog.setTitle(getString(R.string.sms_backup));
        mDialog.setIcon(R.mipmap.ic_launcher);
        mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mDialog.show();
        new Thread() {
            @Override
            public void run() {
                super.run();
                String path = Environment.getExternalStorageDirectory() + File.separator + "backup.xml";
                SmsBackup.backup(getApplicationContext(), path, new SmsBackup.Callback() {
                    @Override
                    public void setMax(int count) {
                        mDialog.setMax(count);
                    }

                    @Override
                    public void setProgress(int index) {
                        mDialog.setProgress(index);
                    }
                });
                mDialog.dismiss();
            }
        }.start();
    }
}
