package cc.springwind.mobileguard.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cc.springwind.mobileguard.R;
import cc.springwind.mobileguard.base.BaseActivity;
import cc.springwind.mobileguard.utils.Constants;
import cc.springwind.mobileguard.utils.SpTool;
import cc.springwind.mobileguard.utils.TextCheckTool;

/**
 * Created by HeFan on 2016/6/26.
 */
public class Setup2Activity extends BaseActivity {

    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    @InjectView(R.id.et_phone_number)
    EditText etPhoneNumber;
    @InjectView(R.id.bt_select_number)
    Button btSelectNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup2);
        ButterKnife.inject(this);
    }

    public void prePage(View view) {
        intent2Activity(SetupActivity.class);
        overridePendingTransition(R.anim.pre_in_anim, R.anim.pre_out_anim);
        finish();
    }

    public void nextPage(View view) {
        if (TextUtils.isEmpty(etPhoneNumber.getText().toString())) {
            // TODO: 2016/6/30 国际化 
            showToast(this, "请输入电话号码");
        } else if (!TextCheckTool.isNumeric(etPhoneNumber.getText().toString())) {
            showToast(this, "电话号码只能是数字");
        } else {
            SpTool.putString(getApplicationContext(), Constants.URGENT_NUMBER, etPhoneNumber.getText().toString());
            SpTool.putBoolean(getApplicationContext(), Constants.SETUP_OVER, true);
            finish();
            overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
            intent2Activity(SetupOverActivity.class);
        }
    }

    @OnClick(R.id.bt_select_number)
    public void onClick() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    PERMISSIONS_REQUEST_READ_CONTACTS);
        } else {
            Intent intent = new Intent(this, ContactsListActivity.class);
            startActivityForResult(intent, 0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
            grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_READ_CONTACTS:
                Intent intent = new Intent(this, ContactsListActivity.class);
                startActivityForResult(intent, 0);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            String phone = data.getStringExtra("phone").replace("-", "").replace(" ", "").trim();
            etPhoneNumber.setText(phone);
        }
    }
}
