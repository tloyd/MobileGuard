package cc.springwind.mobileguard.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cc.springwind.mobileguard.R;
import cc.springwind.mobileguard.base.BaseActivity;

/**
 * Created by HeFan on 2016/6/30.
 */
public class ToolsActivity extends BaseActivity {
    @InjectView(R.id.tv_query_phone_address)
    TextView tvQueryPhoneAddress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tools);
        ButterKnife.inject(this);
    }

    @OnClick(R.id.tv_query_phone_address)
    public void onClick() {
        intent2Activity(QueryAddressActivity.class);
    }
}
