package cc.springwind.mobileguard.ui;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cc.springwind.mobileguard.R;
import cc.springwind.mobileguard.base.BaseActivity;

/**
 * Created by HeFan on 2016/7/6.
 */
public class EnterPwdActivity extends BaseActivity {
    @InjectView(R.id.tv_app_name)
    TextView tvAppName;
    @InjectView(R.id.iv_app_icon)
    ImageView ivAppIcon;
    @InjectView(R.id.et_psd)
    EditText etPsd;
    @InjectView(R.id.bt_submit)
    Button btSubmit;
    private String packageName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_pwd);
        ButterKnife.inject(this);
        packageName = getIntent().getStringExtra("packageName");
        initData();
    }

    private void initData() {
        PackageManager packageManager = getPackageManager();
        try {
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, 0);
            ivAppIcon.setBackground(applicationInfo.loadIcon(packageManager));
            tvAppName.setText(applicationInfo.loadLabel(packageManager));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.bt_submit)
    public void onClick() {
        String psd = etPsd.getText().toString();
        if (!TextUtils.isEmpty(psd)) {
            if (psd.equals("123")) {
                //解锁,进入应用,告知看门口不要再去监听以及解锁的应用,发送广播
                Intent intent = new Intent("android.intent.action.SKIP");
                intent.putExtra("packageName", packageName);
                sendBroadcast(intent);

                finish();
            } else {
                showToast(getApplicationContext(), "密码错误");
            }
        } else {
            showToast(getApplicationContext(), "请输入密码");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }
}
