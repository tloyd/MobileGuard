package cc.springwind.mobileguard.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cc.springwind.mobileguard.R;
import cc.springwind.mobileguard.base.BaseActivity;
import cc.springwind.mobileguard.utils.Constants;
import cc.springwind.mobileguard.utils.MD5Tool;
import cc.springwind.mobileguard.utils.PermissionTool;
import cc.springwind.mobileguard.utils.SpTool;

public class MainActivity extends BaseActivity {

    @InjectView(R.id.gv_home)
    GridView gvHome;

    private String[] mFunctions;
    private int[] mFunctionImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        initUI();
        initPermission();
    }

    /**
     * 初始化用户权限
     */
    private void initPermission() {
        PermissionTool.checkPermissionIdle(this, Manifest.permission.RECEIVE_SMS);
        PermissionTool.checkPermissionIdle(this, Manifest.permission.SYSTEM_ALERT_WINDOW);
        PermissionTool.checkPermissionIdle(this, Manifest.permission.READ_PHONE_STATE);
        PermissionTool.checkPermissionIdle(this, Manifest.permission.PROCESS_OUTGOING_CALLS);
    }

    protected void initUI() {
        // TODO: 2016/6/25 0025 国际化
        mFunctions = new String[]{"手机防盗", "通信卫士", "软件管理", "进程管理", "流量统计", "手机杀毒", "缓存清理", "高级工具", "设置中心"};
        mFunctionImages = new int[]{R.drawable.home_safe, R.drawable.home_callmsgsafe, R.drawable.home_apps,
                R.drawable.home_taskmanager, R.drawable.home_netmanager, R.drawable.home_trojan,
                R.drawable.home_sysoptimize, R.drawable.home_tools, R.drawable.home_settings};
        GridViewAdapter adapter = new GridViewAdapter();
        gvHome.setAdapter(adapter);
        gvHome.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        showDialog();
                        break;
                    case 1:
                        intent2Activity(BlackListActivity.class);
                        break;
                    case 2:
                        intent2Activity(AppManagerAcitivity.class);
                        break;
                    case 3:
                        intent2Activity(ProcessManagerActivity.class);
                        break;
                    case 5:
                        intent2Activity(AntiVirusActivity.class);
                        break;
                    case 6:
                        intent2Activity(CacheClearActivity.class);
                        break;
                    case 7:
                        intent2Activity(ToolsActivity.class);
                        break;
                    case 8:
                        intent2Activity(SettingsActivity.class);
                        break;
                }
            }
        });
    }

    private void showDialog() {
        String password = SpTool.getString(this, Constants.PASSWORD, "");
        if (password.equals(""))
            showSetPwdDialog();
        else
            showConfirmPwdDialog();
    }

    private void showConfirmPwdDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog confirmPwdDialog = builder.create();
        View view = View.inflate(this, R.layout.dialog_confirm_pwd, null);
        confirmPwdDialog.setView(view);
        confirmPwdDialog.show();

        final EditText et_comfirm_pwd = (EditText) view.findViewById(R.id.et_comfirm_pwd);
        view.findViewById(R.id.btn_sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String confirmPsd = et_comfirm_pwd.getText().toString();

                if (!TextUtils.isEmpty(confirmPsd)) {
                    String psd = SpTool.getString(getApplicationContext(), Constants.PASSWORD, "");
                    if (psd.equals(MD5Tool.getMD5EncodeWithSalt(confirmPsd))) {
                        intent2Activity(SetupOverActivity.class);
                        confirmPwdDialog.dismiss();
                    } else {
                        // TODO: 2016/6/25 0025
                        showToast(getApplicationContext(), "确认密码错误");
                    }
                } else {
                    // TODO: 2016/6/25 0025
                    showToast(getApplicationContext(), "请输入密码");
                }
            }
        });
        view.findViewById(R.id.btn_no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmPwdDialog.dismiss();
            }
        });
    }

    private void showSetPwdDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog createPwdDialog = builder.create();
        View view = View.inflate(this, R.layout.dialog_set_pwd, null);
        createPwdDialog.setView(view);
        createPwdDialog.show();

        final EditText et_first_pwd = (EditText) view.findViewById(R.id.et_first_pwd);
        final EditText et_second_pwd = (EditText) view.findViewById(R.id.et_second_pwd);
        view.findViewById(R.id.btn_create).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String psd = et_first_pwd.getText().toString();
                String confirmPsd = et_second_pwd.getText().toString();

                if (!TextUtils.isEmpty(psd) && !TextUtils.isEmpty(confirmPsd)) {
                    if (psd.equals(confirmPsd)) {
                        intent2Activity(SetupActivity.class);
                        createPwdDialog.dismiss();
                        SpTool.putString(getApplicationContext(), Constants.PASSWORD, MD5Tool.getMD5EncodeWithSalt
                                (psd));
                    } else {
                        // TODO: 2016/6/25 0025
                        showToast(getApplicationContext(), "密码错误");
                    }
                } else {
                    // TODO: 2016/6/25 0025
                    showToast(getApplicationContext(), "请输入密码");
                }
            }
        });
        view.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPwdDialog.dismiss();
            }
        });
    }

    class GridViewAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mFunctions.length;
        }

        @Override
        public Object getItem(int position) {
            return mFunctions[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(), R.layout.item_gridview, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.ivIcon.setImageResource(mFunctionImages[position]);
            holder.tvName.setText(mFunctions[position]);
            return convertView;
        }

        class ViewHolder {
            @InjectView(R.id.iv_icon)
            ImageView ivIcon;
            @InjectView(R.id.tv_name)
            TextView tvName;

            ViewHolder(View view) {
                ButterKnife.inject(this, view);
            }
        }
    }
}
