package cc.springwind.mobileguard.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cc.springwind.mobileguard.R;
import cc.springwind.mobileguard.base.BaseActivity;
import cc.springwind.mobileguard.db.entity.ProcessInfoBean;
import cc.springwind.mobileguard.engine.ProcessInfoProvider;
import cc.springwind.mobileguard.utils.Constants;
import cc.springwind.mobileguard.utils.SpTool;

/**
 * Created by HeFan on 2016/7/5.
 */
public class ProcessManagerActivity extends BaseActivity {
    @InjectView(R.id.tv_process_count)
    TextView tvProcessCount;
    @InjectView(R.id.tv_memory_info)
    TextView tvMemoryInfo;
    @InjectView(R.id.lv_process_list)
    ListView lvProcessList;
    @InjectView(R.id.tv_des)
    TextView tvDes;
    @InjectView(R.id.bt_select_all)
    Button btSelectAll;
    @InjectView(R.id.bt_select_reverse)
    Button btSelectReverse;
    @InjectView(R.id.bt_clear)
    Button btClear;
    @InjectView(R.id.bt_setting)
    Button btSetting;

    private int mProcessCount;
    private List<ProcessInfoBean> mProcessInfoList;
    private ArrayList<ProcessInfoBean> mSystemList;
    private ArrayList<ProcessInfoBean> mCustomerList;
    private IAdapter adapter;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            adapter = new IAdapter();
            lvProcessList.setAdapter(adapter);
        }
    };
    private ProcessInfoBean mProcessInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_manager);
        ButterKnife.inject(this);

        initListerner();
        initHeadInfo();
        // TODO: 2016/7/5 安卓5.0以上系统,普通应用无法获取进程列表,4.4测试可获得进程列表
        initListData();
    }

    private void initListData() {
        new Thread() {
            public void run() {
                mProcessInfoList = ProcessInfoProvider.getProcessInfo(getApplicationContext());
                mSystemList = new ArrayList<ProcessInfoBean>();
                mCustomerList = new ArrayList<ProcessInfoBean>();

                for (ProcessInfoBean info : mProcessInfoList) {
                    if (info.isSystem) {
                        //系统进程
                        mSystemList.add(info);
                    } else {
                        //用户进程
                        mCustomerList.add(info);
                    }
                }
                mHandler.sendEmptyMessage(0);
            }
        }.start();
    }

    private long mAvailSpace;
    private String mStrTotalSpace;

    private void initHeadInfo() {
        mProcessCount = ProcessInfoProvider.getProcessCount(this);
        tvProcessCount.setText("进程总数:" + mProcessCount);

        mAvailSpace = ProcessInfoProvider.getAvailSpace(this);
        String strAvailSpace = Formatter.formatFileSize(this, mAvailSpace);

        long totalSpace = ProcessInfoProvider.getTotalSpace(this);
        mStrTotalSpace = Formatter.formatFileSize(this, totalSpace);

        tvMemoryInfo.setText("剩余/总共:" + strAvailSpace + "/" + mStrTotalSpace);
    }

    private void initListerner() {
        lvProcessList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (mCustomerList != null && mSystemList != null) {
                    if (firstVisibleItem >= mCustomerList.size() + 1) {
                        //滚动到了系统条目
                        tvDes.setText("系统进程(" + mSystemList.size() + ")");
                    } else {
                        //滚动到了用户应用条目
                        tvDes.setText("用户进程(" + mCustomerList.size() + ")");
                    }
                }
            }
        });

        lvProcessList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0 || position == mCustomerList.size() + 1) {
                    return;
                } else {
                    if (position < mCustomerList.size() + 1) {
                        mProcessInfo = mCustomerList.get(position - 1);
                    } else {
                        mProcessInfo = mSystemList.get(position - mCustomerList.size() - 2);
                    }
                    if (mProcessInfo != null) {
                        if (!mProcessInfo.packageName.equals(getPackageName())) {
                            mProcessInfo.isCheck = !mProcessInfo.isCheck;
                            CheckBox cb_box = (CheckBox) view.findViewById(R.id.cb_box);
                            cb_box.setChecked(mProcessInfo.isCheck);
                        }
                    }
                }
            }
        });
    }


    @OnClick({R.id.bt_select_all, R.id.bt_select_reverse, R.id.bt_clear, R.id.bt_setting})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_select_all:
                selectAll();
                break;
            case R.id.bt_select_reverse:
                selectReverse();
                break;
            case R.id.bt_clear:
                clear();
                break;
            case R.id.bt_setting:
                setting();
                break;
        }
    }

    private void setting() {
        Intent intent = new Intent(this, ProcessSettingActivity.class);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //通知数据适配器刷新
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void clear() {
        List<ProcessInfoBean> mKillList = new ArrayList<>();
        for (ProcessInfoBean bean : mCustomerList) {
            if (bean.packageName.equals(getPackageName())) {
                continue;
            }
            if (bean.isCheck) {
                mKillList.add(bean);
            }
        }
        for (ProcessInfoBean bean :
                mSystemList) {
            if (bean.isCheck) {
                mKillList.add(bean);
            }
        }
        int releaseSpace = 0;
        for (ProcessInfoBean bean :
                mKillList) {
            if (mCustomerList.contains(bean)) {
                mCustomerList.remove(bean);
            }
            if (mSystemList.contains(bean)) {
                mSystemList.remove(bean);
            }
            ProcessInfoProvider.killProcess(this, bean);
            releaseSpace += bean.memSize;
        }

        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }

        mProcessCount -= mKillList.size();
        mAvailSpace += releaseSpace;
        tvProcessCount.setText("进程总数:" + mProcessCount);
        tvMemoryInfo.setText("剩余/总共" + Formatter.formatFileSize(this, mAvailSpace) + "/" + mStrTotalSpace);
        String totalRelease = Formatter.formatFileSize(this, releaseSpace);
//		jni  java--c   c---java
        showToast(getApplicationContext(),
                String.format("杀死了%d进程,释放了%s空间", mKillList.size(), totalRelease));
    }

    private void selectReverse() {
        for (ProcessInfoBean bean : mCustomerList) {
            if (bean.packageName.equals(getPackageName())) {
                continue;
            }
            bean.isCheck = !bean.isCheck;
        }
        for (ProcessInfoBean bean : mSystemList) {
            bean.isCheck = !bean.isCheck;
        }
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private void selectAll() {
        for (ProcessInfoBean bean : mCustomerList) {
            if (bean.packageName.equals(getPackageName())) {
                continue;
            }
            bean.isCheck = true;
        }
        for (ProcessInfoBean bean : mSystemList) {
            bean.isCheck = true;
        }
        if (adapter != null) {
            adapter.notifyDataSetChanged();
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
            if (SpTool.getBoolean(getApplicationContext(), Constants.SHOW_SYS_PROCESS, false)) {
                return mCustomerList.size() + mSystemList.size() + 2;
            } else {
                return mCustomerList.size() + 1;
            }
        }

        @Override
        public ProcessInfoBean getItem(int position) {
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
                    mTitleViewHolder.tvTitle.setText("用户进程(" + mCustomerList.size() + ")");
                } else {
                    mTitleViewHolder.tvTitle.setText("系统进程(" + mSystemList.size() + ")");
                }
                return convertView;
            } else {
                AppViewHolder mAppViewHolder;
                if (convertView == null) {
                    convertView = View.inflate(getApplicationContext(), R.layout.listview_process_item, null);
                    mAppViewHolder = new AppViewHolder(convertView);
                    convertView.setTag(mAppViewHolder);
                } else {
                    mAppViewHolder = (AppViewHolder) convertView.getTag();
                }
                mAppViewHolder.ivIcon.setBackgroundDrawable(getItem(position).icon);
                mAppViewHolder.tvName.setText(getItem(position).name);
                String strSize = Formatter.formatFileSize(getApplicationContext(), getItem(position).memSize);
                mAppViewHolder.tvMemoryInfo.setText(strSize);

                //本进程不能被选中,所以先将checkbox隐藏掉
                if (getItem(position).packageName.equals(getPackageName())) {
                    mAppViewHolder.cbBox.setVisibility(View.GONE);
                } else {
                    mAppViewHolder.cbBox.setVisibility(View.VISIBLE);
                }

                mAppViewHolder.cbBox.setChecked(getItem(position).isCheck);

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
            @InjectView(R.id.tv_memory_info)
            TextView tvMemoryInfo;
            @InjectView(R.id.cb_box)
            CheckBox cbBox;

            AppViewHolder(View view) {
                ButterKnife.inject(this, view);
            }
        }
    }
}
