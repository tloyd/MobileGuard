package cc.springwind.mobileguard.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
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
import cc.springwind.mobileguard.db.dao.AppLockedDao;
import cc.springwind.mobileguard.db.entity.AppInfoBean;
import cc.springwind.mobileguard.engine.AppInfoProvider;

/**
 * Created by HeFan on 2016/7/6.
 */
public class AppLockActivity extends BaseActivity {
    @InjectView(R.id.btn_app_locked)
    Button btnAppLocked;
    @InjectView(R.id.btn_app_unlocked)
    Button btnAppUnlocked;
    @InjectView(R.id.tv_app_islock_number)
    TextView tvAppIslockNumber;
    @InjectView(R.id.lv_app_locked)
    ListView lvAppLocked;
    @InjectView(R.id.lv_app_unlocked)
    ListView lvAppUnlocked;

    private AppLockedDao dao;
    private List<AppInfoBean> lockedApps;
    private List<AppInfoBean> unlockedApps;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_lock);
        ButterKnife.inject(this);

        initData();
        initAnimation();
    }

    @OnClick({R.id.btn_app_locked, R.id.btn_app_unlocked})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_app_locked:
                btnAppLocked.setBackgroundResource(R.drawable.tab_left_pressed);
                btnAppUnlocked.setBackgroundResource(R.drawable.tab_right_default);

                lvAppLocked.setVisibility(View.VISIBLE);
                lvAppUnlocked.setVisibility(View.GONE);
                break;
            case R.id.btn_app_unlocked:
                btnAppLocked.setBackgroundResource(R.drawable.tab_left_default);
                btnAppUnlocked.setBackgroundResource(R.drawable.tab_right_pressed);

                lvAppLocked.setVisibility(View.GONE);
                lvAppUnlocked.setVisibility(View.VISIBLE);
                break;
        }
    }

    private IAdapter mLockedAppAdapter;
    private IAdapter mUnLockedAppAdapter;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mLockedAppAdapter = new IAdapter(true);
            mUnLockedAppAdapter = new IAdapter(false);

            lvAppLocked.setAdapter(mLockedAppAdapter);
            lvAppUnlocked.setAdapter(mUnLockedAppAdapter);
        }
    };

    private void initData() {
        dao = AppLockedDao.getInstance(getApplicationContext());
        new Thread() {
            @Override
            public void run() {
                super.run();
                List<String> mList = dao.findAll();
                List<AppInfoBean> allAppList = AppInfoProvider.getAppInfoList(getApplicationContext());

                lockedApps = new ArrayList<>();
                unlockedApps = new ArrayList<>();

                for (AppInfoBean bean : allAppList) {
                    if (mList.contains(bean.packageName)) {
                        lockedApps.add(bean);
                    } else {
                        unlockedApps.add(bean);
                    }
                }
                mHandler.sendEmptyMessage(1);
            }
        }.start();
    }

    private TranslateAnimation mAnimation;

    private void initAnimation() {
        mAnimation = new TranslateAnimation(
                TranslateAnimation.RELATIVE_TO_SELF, 0,
                TranslateAnimation.RELATIVE_TO_SELF, 1,
                TranslateAnimation.RELATIVE_TO_SELF, 0,
                TranslateAnimation.RELATIVE_TO_SELF, 0);
        mAnimation.setDuration(500);
    }

    class IAdapter extends BaseAdapter {

        private final boolean flag;

        public IAdapter(boolean flag) {
            this.flag = flag;
        }

        @Override
        public int getCount() {
            if (flag) {
                tvAppIslockNumber.setText("已加锁应用:" + lockedApps.size());
                return lockedApps.size();
            } else {
                tvAppIslockNumber.setText("未加锁应用:" + unlockedApps.size());
                return unlockedApps.size();
            }
        }

        @Override
        public AppInfoBean getItem(int position) {
            if (flag) {
                return lockedApps.get(position);
            } else {
                return unlockedApps.get(position);
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(), R.layout.listview_islock_item, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            final AppInfoBean bean = getItem(position);

            viewHolder.ivIcon.setBackgroundDrawable(bean.icon);
            viewHolder.tvName.setText(bean.name);
            if (flag) {
                viewHolder.ivLock.setImageResource(R.drawable.lock);
            } else {
                viewHolder.ivLock.setImageResource(R.drawable.unlock);
            }

            final View view=convertView;
            viewHolder.ivLock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    view.startAnimation(mAnimation);
                    mAnimation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            if (flag){
                                lockedApps.remove(bean);
                                unlockedApps.add(bean);

                                dao.delete(bean.packageName);
                                mLockedAppAdapter.notifyDataSetChanged();
                            } else {
                                unlockedApps.remove(bean);
                                lockedApps.add(bean);

                                dao.insert(bean.packageName);
                                mUnLockedAppAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                }
            });

            return convertView;
        }

        class ViewHolder {
            @InjectView(R.id.iv_icon)
            ImageView ivIcon;
            @InjectView(R.id.tv_name)
            TextView tvName;
            @InjectView(R.id.iv_lock)
            ImageView ivLock;

            ViewHolder(View view) {
                ButterKnife.inject(this, view);
            }
        }
    }
}
