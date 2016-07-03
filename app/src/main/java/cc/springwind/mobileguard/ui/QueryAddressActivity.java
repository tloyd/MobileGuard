package cc.springwind.mobileguard.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cc.springwind.mobileguard.R;
import cc.springwind.mobileguard.base.BaseActivity;
import cc.springwind.mobileguard.db.AddressDao;

/**
 * Created by HeFan on 2016/6/30.
 */
public class QueryAddressActivity extends BaseActivity {

    @InjectView(R.id.et_phone)
    EditText etPhone;
    @InjectView(R.id.bt_query)
    Button btQuery;
    @InjectView(R.id.tv_query_result)
    TextView tvQueryResult;

    private String mAddress;

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            //4,控件使用查询结果
            tvQueryResult.setText(mAddress);
        }

        ;
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_address);
        ButterKnife.inject(this);

        etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String phone = etPhone.getText().toString();
                query(phone);
            }
        });
    }

    private void query(final String phone) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                mAddress = AddressDao.getAddress(phone);
                //3,消息机制,告知主线程查询结束,可以去使用查询结果
                mHandler.sendEmptyMessage(0);
            }
        }.start();
    }


    @OnClick(R.id.bt_query)
    public void onClick() {
        String phone = etPhone.getText().toString();
        if (!TextUtils.isEmpty(phone)) {
            //2,查询是耗时操作,开启子线程
            query(phone);
        } else {
            //抖动
            Animation shake = AnimationUtils.loadAnimation(
                    getApplicationContext(), R.anim.shake);
            etPhone.startAnimation(shake);
            Vibrator mVibrator= (Vibrator) getSystemService(VIBRATOR_SERVICE);
            mVibrator.vibrate(500);
        }
    }
}
