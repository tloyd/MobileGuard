package cc.springwind.mobileguard.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cc.springwind.mobileguard.R;
import cc.springwind.mobileguard.base.BaseActivity;
import cc.springwind.mobileguard.db.dao.BlackListDao;
import cc.springwind.mobileguard.db.entity.BlackListBean;

/**
 * Created by HeFan on 2016/7/3.
 */
public class BlackListActivity extends BaseActivity {
    @InjectView(R.id.bt_add)
    Button btAdd;
    @InjectView(R.id.lv_blacknumber)
    ListView lvBlacknumber;

    private List<BlackListBean> mList;
    private ListAdapter mAdapter;
    private BlackListDao mDao;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (mAdapter == null) {
                mAdapter = new ListAdapter();
                lvBlacknumber.setAdapter(mAdapter);
            } else {
                mAdapter.notifyDataSetChanged();
            }
        }
    };
    private int mode = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_black_list);
        ButterKnife.inject(this);

        initUI();
        initData();
    }

    private void initData() {
        mDao = BlackListDao.getInstance(getApplicationContext());
        new Thread() {
            @Override
            public void run() {
                super.run();
                mList = mDao.find(0);
                mHandler.sendEmptyMessage(0);
            }
        }.start();
    }

    private void initUI() {
        lvBlacknumber.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    @OnClick(R.id.bt_add)
    public void onClick() {
        showDialog();
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final AlertDialog dialog = builder.create();
        View view = View.inflate(getApplicationContext(), R.layout.dialog_add_blacknumber, null);
        dialog.setView(view, 0, 0, 0, 0);

        final EditText et_phone = (EditText) view.findViewById(R.id.et_phone);
        RadioGroup rg_group = (RadioGroup) view.findViewById(R.id.rg_group);

        Button bt_submit = (Button) view.findViewById(R.id.bt_submit);
        Button bt_cancel = (Button) view.findViewById(R.id.bt_cancel);

        //监听其选中条目的切换过程
        rg_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_sms:
                        //拦截短信
                        mode = 1;
                        break;
                    case R.id.rb_phone:
                        //拦截电话
                        mode = 2;
                        break;
                    case R.id.rb_all:
                        //拦截所有
                        mode = 3;
                        break;
                }
            }
        });

        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //1,获取输入框中的电话号码
                String phone = et_phone.getText().toString();
                if (!TextUtils.isEmpty(phone)) {
                    //2,数据库插入当前输入的拦截电话号码
                    mDao.insert(phone, mode + "");
                    //3,让数据库和集合保持同步(1.数据库中数据重新读一遍,2.手动向集合中添加一个对象(插入数据构建的对象))
                    BlackListBean blackNumberInfo = new BlackListBean();
                    blackNumberInfo.phone = phone;
                    blackNumberInfo.mode = mode + "";
                    //4,将对象插入到集合的最顶部
                    mList.add(0, blackNumberInfo);
                    //5,通知数据适配器刷新(数据适配器中的数据有改变了)
                    if (mAdapter != null) {
                        mAdapter.notifyDataSetChanged();
                    }
                    //6,隐藏对话框
                    dialog.dismiss();
                } else {
                    showToast(getApplicationContext(), "请输入拦截号码");
                }
            }
        });

        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    class ListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public BlackListBean getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder mHolder;
            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(), R.layout.listview_blacklist_item, null);
                mHolder = new ViewHolder(convertView);
                convertView.setTag(mHolder);
            } else {
                mHolder = (ViewHolder) convertView.getTag();
            }
            mHolder.tvPhone.setText(getItem(position).phone);
            switch (Integer.parseInt(getItem(position).mode)) {
                // TODO: 2016/7/3 国际化
                case 3:
                    mHolder.tvMode.setText("拦截所有");
                    break;
                case 1:
                    mHolder.tvMode.setText("拦截短信");
                    break;
                case 2:
                    mHolder.tvMode.setText("拦截电话");
                    break;
            }
            mHolder.ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDao.delete(mList.get(position).phone);
                    mList.remove(position);
                    if (mAdapter != null) {
                        mAdapter.notifyDataSetChanged();
                    }
                }
            });
            return convertView;
        }

        class ViewHolder {
            @InjectView(R.id.tv_phone)
            TextView tvPhone;
            @InjectView(R.id.tv_mode)
            TextView tvMode;
            @InjectView(R.id.iv_delete)
            ImageView ivDelete;

            ViewHolder(View view) {
                ButterKnife.inject(this, view);
            }
        }
    }
}
