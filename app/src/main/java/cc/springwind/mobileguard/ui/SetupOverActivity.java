package cc.springwind.mobileguard.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import cc.springwind.mobileguard.R;
import cc.springwind.mobileguard.base.BaseActivity;
import cc.springwind.mobileguard.utils.Constants;
import cc.springwind.mobileguard.utils.SpTool;

/**
 * Created by HeFan on 2016/6/30.
 */
public class SetupOverActivity extends BaseActivity {
    private TextView tvPhone;
    private TextView tvResetSetup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean setup_over = SpTool.getBoolean(this, Constants.SETUP_OVER, false);
        if (setup_over) {
            setContentView(R.layout.activity_setupover);
            initUI();
        } else {
            intent2Activity(SetupActivity.class);
            finish();
        }
    }

    private void initUI() {
        tvPhone = (TextView) findViewById(R.id.tv_urgen_phone);
        tvResetSetup = (TextView) findViewById(R.id.tv_reset_setup);
        //设置联系人号码
        String phone = SpTool.getString(this, Constants.URGENT_NUMBER, "");
        tvPhone.setText(phone);
        //重新设置条目被点击
        tvResetSetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent2Activity(SetupActivity.class);
                finish();
            }
        });
    }
}
