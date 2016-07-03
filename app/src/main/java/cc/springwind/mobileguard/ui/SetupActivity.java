package cc.springwind.mobileguard.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cc.springwind.mobileguard.R;
import cc.springwind.mobileguard.base.BaseActivity;
import cc.springwind.mobileguard.utils.Constants;
import cc.springwind.mobileguard.utils.SpTool;

/**
 * Created by HeFan on 2016/6/26.
 */
public class SetupActivity extends BaseActivity {
    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 1;
    @InjectView(R.id.cb_bind_sim)
    CheckBox cbBindSim;
    private TelephonyManager manager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        ButterKnife.inject(this);

        initUI();
    }

    private void initUI() {
        String sim_number = SpTool.getString(this, Constants.SIM_SERIAL_NUMBER, "");
        if (TextUtils.isEmpty(sim_number)) {
            cbBindSim.setChecked(false);
        } else {
            cbBindSim.setChecked(true);
        }

    }

    public void prePage(View view) {

    }

    public void nextPage(View view) {
        String sim_number = SpTool.getString(this, Constants.SIM_SERIAL_NUMBER, "");
        if (!TextUtils.isEmpty(sim_number)) {
            intent2Activity(Setup2Activity.class);
            overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
            finish();
        } else {
            showToast(this, "请绑定sim卡");
        }
    }

    @OnClick(R.id.cb_bind_sim)
    public void onClick() {
        if (cbBindSim.isChecked()) {
            manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_PHONE_STATE)) {

                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_PHONE_STATE},
                            MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
                }
            }
        } else {
            SpTool.putBoolean(getApplicationContext(), Constants.SETUP_OVER, false);
            SpTool.remote(getApplicationContext(), Constants.SIM_SERIAL_NUMBER);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
            grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_PHONE_STATE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    SpTool.putString(getApplicationContext(), Constants.SIM_SERIAL_NUMBER, manager.getSimSerialNumber
                            ());
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
