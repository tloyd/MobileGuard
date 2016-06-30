package cc.springwind.mobileguard.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
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
    @InjectView(R.id.cb_bind_sim)
    CheckBox cbBindSim;

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
            TelephonyManager manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            SpTool.putString(getApplicationContext(), Constants.SIM_SERIAL_NUMBER, manager.getSimSerialNumber());
        } else {
            SpTool.putBoolean(getApplicationContext(), Constants.SETUP_OVER, false);
            SpTool.remote(getApplicationContext(), Constants.SIM_SERIAL_NUMBER);
        }
    }
}
