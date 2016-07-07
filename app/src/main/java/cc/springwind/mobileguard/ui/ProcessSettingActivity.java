package cc.springwind.mobileguard.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cc.springwind.mobileguard.R;
import cc.springwind.mobileguard.base.BaseActivity;
import cc.springwind.mobileguard.service.LockScreenService;
import cc.springwind.mobileguard.utils.Constants;
import cc.springwind.mobileguard.utils.ServiceTool;
import cc.springwind.mobileguard.utils.SpTool;

/**
 * Created by HeFan on 2016/7/5.
 */
public class ProcessSettingActivity extends BaseActivity {
    @InjectView(R.id.cb_show_system_process)
    CheckBox cbShowSystemProcess;
    @InjectView(R.id.cb_lock_clear)
    CheckBox cbLockClear;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_setting);
        ButterKnife.inject(this);

        initSystemShow();
        initLockClear();
    }

    private void initLockClear() {
        boolean isRunning = ServiceTool.isRunning(this, "com.itheima.mobilesafe74.service.LockScreenService");
        if(isRunning){
            cbLockClear.setText("锁屏清理已开启");
        }else{
            cbLockClear.setText("锁屏清理已关闭");
        }
        //cb_lock_clear选中状态维护
        cbLockClear.setChecked(isRunning);

        //对选中状态进行监听
        cbLockClear.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //isChecked就作为是否选中的状态
                if(isChecked){
                    cbLockClear.setText("锁屏清理已开启");
                    startService(new Intent(getApplicationContext(), LockScreenService.class));
                }else{
                    cbLockClear.setText("锁屏清理已关闭");
                    stopService(new Intent(getApplicationContext(), LockScreenService.class));
                }
            }
        });
    }

    private void initSystemShow() {
        boolean flag = SpTool.getBoolean(getApplicationContext(), Constants.SHOW_SYS_PROCESS, false);
        cbShowSystemProcess.setChecked(flag);
        if (flag) {
            cbShowSystemProcess.setText("显示系统进程");
        } else {
            cbShowSystemProcess.setText("隐藏系统进程");
        }
        cbShowSystemProcess.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cbShowSystemProcess.setText("显示系统进程");
                } else {
                    cbShowSystemProcess.setText("隐藏系统进程");
                }
                SpTool.putBoolean(getApplicationContext(), Constants.SHOW_SYS_PROCESS, isChecked);
            }
        });
    }


    @OnClick({R.id.cb_show_system_process, R.id.cb_lock_clear})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cb_show_system_process:
                break;
            case R.id.cb_lock_clear:
                break;
        }
    }
}
