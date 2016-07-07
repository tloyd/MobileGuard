package cc.springwind.mobileguard.ui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.support.annotation.Nullable;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cc.springwind.mobileguard.R;
import cc.springwind.mobileguard.base.BaseActivity;
import cc.springwind.mobileguard.db.entity.AppInfoBean;
import cc.springwind.mobileguard.engine.AppInfoProvider;

/**
 * Created by HeFan on 2016/7/4.
 */
public class AppManagerAcitivity extends BaseActivity implements View.OnClickListener {
    @InjectView(R.id.tv_memory)
    TextView tvMemory;
    @InjectView(R.id.tv_sd_memory)
    TextView tvSdMemory;
    @InjectView(R.id.lv_app_list)
    ListView lvAppList;
    @InjectView(R.id.tv_des)
    TextView tvDes;

    private List<AppInfoBean> mAppInfoList;
    private List<AppInfoBean> mSystemList;
    private List<AppInfoBean> mCustomerList;
    private IAdapter mAdapter;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mAdapter = new IAdapter();
            lvAppList.setAdapter(mAdapter);
        }
    };
    private AppInfoBean mAppInfo;
    private PopupWindow mPopupWindow;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manager);
        ButterKnife.inject(this);
        initHeadInfo();
        initApplicationList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    private void initApplicationList() {
        lvAppList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (mCustomerList != null && mSystemList != null) {
                    if (firstVisibleItem >= mCustomerList.size() + 1) {
                        tvDes.setText("系统应用(" + mSystemList.size() + ")");
                    } else {
                        tvDes.setText("用户应用(" + mCustomerList.size() + ")");
                    }
                }
            }
        });

        lvAppList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0 || position == mCustomerList.size() + 1) {
                    return;
                } else {
                    if (position < mCustomerList.size() + 1) {
                        mAppInfo = mCustomerList.get(position - 1);
                    } else {
                        mAppInfo = mSystemList.get(position - mCustomerList.size() - 2);
                    }
                    showPopupWindow(view);
                }
            }
        });
    }

    private void showPopupWindow(View view) {
        /*LayoutInflater inflater = LayoutInflater.from(this);
        View popupView = inflater.inflate(R.layout.popupwindow, null);*/
        LayoutInflater inflater = (LayoutInflater) AppManagerAcitivity.this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View conentView = inflater.inflate(R.layout.popupwindow, null);

//        View popupView = View.inflate(this, R.layout.popupwindow, null);
        TextView tv_uninstall = (TextView) conentView.findViewById(R.id.tv_uninstall);
        TextView tv_start = (TextView) conentView.findViewById(R.id.tv_start);
        TextView tv_share = (TextView) conentView.findViewById(R.id.tv_share);

        tv_uninstall.setOnClickListener(AppManagerAcitivity.this);
        tv_start.setOnClickListener(AppManagerAcitivity.this);
        tv_share.setOnClickListener(AppManagerAcitivity.this);

        mPopupWindow = new PopupWindow(conentView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout
                .LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable());
        mPopupWindow.showAsDropDown(view,50,-view.getHeight());
    }

    private void getData() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                mAppInfoList = AppInfoProvider.getAppInfoList(getApplicationContext());
                mSystemList = new ArrayList<AppInfoBean>();
                mCustomerList = new ArrayList<AppInfoBean>();
                for (AppInfoBean appInfoBean : mAppInfoList) {
                    if (appInfoBean.isSystem) {
                        mSystemList.add(appInfoBean);
                    } else {
                        mCustomerList.add(appInfoBean);
                    }
                }
                mHandler.sendEmptyMessage(0);
            }
        }.start();
    }

    private void initHeadInfo() {
        String inside = Environment.getDataDirectory().getAbsolutePath();
        String external = Environment.getExternalStorageDirectory().getAbsolutePath();

        String insideAvailSpace = Formatter.formatFileSize(getApplicationContext(), getAvailableSpace(inside));
        String externalAvailSpace = Formatter.formatFileSize(getApplicationContext(), getAvailableSpace(external));

        // TODO: 2016/7/4 国际化
        tvMemory.setText("磁盘可用:" + insideAvailSpace);
        tvSdMemory.setText("sd卡可用:" + externalAvailSpace);

    }

    private long getAvailableSpace(String path) {
        StatFs mStatFs = new StatFs(path);
        long blocks = mStatFs.getAvailableBlocks();
        long size = mStatFs.getBlockSize();
        return blocks * size;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_uninstall:
                if (mAppInfo.isSystem) {
                    showToast(this, "系统应用不能卸载");
                } else {
                    Intent intent = new Intent("android.intent.action.DELETE");
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.setData(Uri.parse("package:" + mAppInfo.packageName));
                    startActivity(intent);
                }
                break;
            case R.id.tv_start:
                PackageManager pm = getPackageManager();
                //通过Launch开启制定包名的意图,去开启应用
                Intent launchIntentForPackage = pm.getLaunchIntentForPackage(mAppInfo.packageName);
                if (launchIntentForPackage != null) {
                    startActivity(launchIntentForPackage);
                } else {
                    showToast(getApplicationContext(), "此应用不能被开启");
                }
                break;
            case R.id.tv_share:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, "分享一个应用,应用名称为" + mAppInfo.name);
                intent.setType("text/plain");
                startActivity(intent);
                break;
        }
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
        }
    }


    class IAdapter extends BaseAdapter {

        @Override
        public int getViewTypeCount() {
            return super.getViewTypeCount() + 1;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0 || position == mCustomerList.size() + 1) {
                return 0;
            } else {
                return 1;
            }
        }

        @Override
        public int getCount() {
            return mSystemList.size() + mCustomerList.size() + 2;
        }

        @Override
        public AppInfoBean getItem(int position) {
            if (position == 0 || position == mCustomerList.size() + 1) {
                return null;
            } else {
                if (position < mCustomerList.size() + 1) {
                    return mCustomerList.get(position - 1);
                } else {
                    return mSystemList.get(position - mCustomerList.size() - 2);
                }
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int type = getItemViewType(position);
            if (type == 0) {
                TitleViewHolder mTitleViewHolder;
                if (convertView == null) {
                    convertView = View.inflate(getApplicationContext(), R.layout.listview_app_item_title, null);
                    mTitleViewHolder = new TitleViewHolder(convertView);
                    convertView.setTag(mTitleViewHolder);
                } else {
                    mTitleViewHolder = (TitleViewHolder) convertView.getTag();
                }
                if (position == 0) {
                    mTitleViewHolder.tvTitle.setText("用户应用(" + mCustomerList.size() + ")");
                } else {
                    mTitleViewHolder.tvTitle.setText("系统应用(" + mSystemList.size() + ")");
                }
                return convertView;
            } else {
                AppViewHolder mAppViewHolder;
                if (convertView == null) {
                    convertView = View.inflate(getApplicationContext(), R.layout.listview_app_item, null);
                    mAppViewHolder = new AppViewHolder(convertView);
                    convertView.setTag(mAppViewHolder);
                } else {
                    mAppViewHolder = (AppViewHolder) convertView.getTag();
                }
                mAppViewHolder.ivIcon.setBackgroundDrawable(getItem(position).icon);
                mAppViewHolder.tvName.setText(getItem(position).name);
                if (getItem(position).isSdCard) {
                    mAppViewHolder.tvPath.setText("sd卡应用");
                } else {
                    mAppViewHolder.tvPath.setText("手机应用");
                }
                return convertView;
            }
        }

        class TitleViewHolder {
            @InjectView(R.id.tv_title)
            TextView tvTitle;

            TitleViewHolder(View view) {
                ButterKnife.inject(this, view);
            }
        }

        class AppViewHolder {
            @InjectView(R.id.iv_icon)
            ImageView ivIcon;
            @InjectView(R.id.tv_name)
            TextView tvName;
            @InjectView(R.id.tv_path)
            TextView tvPath;

            AppViewHolder(View view) {
                ButterKnife.inject(this, view);
            }
        }
    }
}
