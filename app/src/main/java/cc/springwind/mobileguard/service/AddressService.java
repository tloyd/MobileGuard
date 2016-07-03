package cc.springwind.mobileguard.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import cc.springwind.mobileguard.R;
import cc.springwind.mobileguard.db.AddressDao;

/**
 * Created by HeFan on 2016/7/1.
 */
public class AddressService extends Service {

    private TelephonyManager mTelephonyManager;
    private WindowManager mWindowManager;
    private View toastView;
    private final WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
    private TextView tv_toast;
    private int[] mDrawableIds;
    private String mAddress;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            tv_toast.setText(mAddress);
        }
    };
    private IPhoneStateListener listener;

    @Override
    public void onCreate() {
        mTelephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        listener = new IPhoneStateListener();
        mTelephonyManager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        if (mTelephonyManager != null && listener != null) {
            mTelephonyManager.listen(listener, PhoneStateListener.LISTEN_NONE);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    class IPhoneStateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    if (mWindowManager != null && toastView != null) {
                        mWindowManager.removeView(toastView);
                    }
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    // TODO: 2016/7/1 无法获取来电号码
                    showToast(incomingNumber);
                    break;
            }
        }
    }

    private void showToast(String incomingNumber) {
        final WindowManager.LayoutParams params = mParams;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.format = PixelFormat.TRANSLUCENT;
        //在响铃的时候显示吐司,和电话类型一致
        params.type = WindowManager.LayoutParams.TYPE_TOAST;
        params.setTitle("Toast");

        //指定吐司的所在位置(将吐司指定在左上角)
        params.gravity = Gravity.CENTER;

        //吐司显示效果(吐司布局文件),xml-->view(吐司),将吐司挂在到windowManager窗体上
        toastView = View.inflate(this, R.layout.toast_view, null);
        tv_toast = (TextView) toastView.findViewById(R.id.tv_toast);

        //从sp中获取色值文字的索引,匹配图片,用作展示
        mDrawableIds = new int[]{
                R.drawable.call_locate_white,
                R.drawable.call_locate_orange,
                R.drawable.call_locate_blue,
                R.drawable.call_locate_gray,
                R.drawable.call_locate_green};
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int toastStyleIndex = Integer.parseInt(preferences.getString("pref_toast_style", "0"));
        tv_toast.setBackgroundResource(mDrawableIds[toastStyleIndex]);

        //在窗体上挂在一个view(权限)
        mWindowManager.addView(toastView, params);

        //获取到了来电号码以后,需要做来电号码查询
        query(incomingNumber);
    }

    private void query(final String incomingNumber) {
        new Thread() {
            public void run() {
                mAddress = AddressDao.getAddress(incomingNumber);
                mHandler.sendEmptyMessage(0);
            }

            ;
        }.start();
    }

}
