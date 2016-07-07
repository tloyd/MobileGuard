package cc.springwind.mobileguard.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;

import java.lang.reflect.Method;

import cc.springwind.mobileguard.db.dao.BlackListDao;
import cc.springwind.mobileguard.utils.LogTool;

/**
 * Created by HeFan on 2016/7/3.
 */
public class BlackListService extends Service {

    private BlackListDao mDao;
    private InnerSMSReceiver mInnerSMSReceiver;
    private IPhoneStateListener mIPhoneStateListener;
    private TelephonyManager mTelephonyManager;
    private IContentObserver observer;

    @Override
    public void onCreate() {
        mDao = BlackListDao.getInstance(getApplicationContext());

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        intentFilter.setPriority(1000);

        mInnerSMSReceiver = new InnerSMSReceiver();
        registerReceiver(mInnerSMSReceiver, intentFilter);

        mTelephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        mIPhoneStateListener = new IPhoneStateListener();

        mTelephonyManager.listen(mIPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        if (mInnerSMSReceiver != null) {
            unregisterReceiver(mInnerSMSReceiver);
        }
        if (mIPhoneStateListener != null) {
            mTelephonyManager.listen(mIPhoneStateListener, PhoneStateListener.LISTEN_NONE);
        }
        if (observer != null) {
            getContentResolver().unregisterContentObserver(observer);
        }

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    class InnerSMSReceiver extends BroadcastReceiver {
        // TODO: 2016/7/3 api23无法拦截到短信
        @Override
        public void onReceive(Context context, Intent intent) {
            LogTool.debug("onReceive");
            Object[] objects = (Object[]) intent.getExtras().get("pdus");
            for (Object object : objects) {
                SmsMessage sms = SmsMessage.createFromPdu((byte[]) object);
                String originatingAddress = sms.getOriginatingAddress();
                int mode = mDao.getMode(originatingAddress);
                if (mode == 1 || mode == 3) {
                    abortBroadcast();
                }
            }
        }
    }

    class IPhoneStateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            LogTool.debug("onCallStateChanged");
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    endCall(incomingNumber);
                    break;
            }
        }
    }

    private void endCall(String incomingNumber) {
        int mode = mDao.getMode(incomingNumber);
        if (mode == 2 || mode == 3) {
            // TODO: 2016/7/3 api23无法获取来电号码,
            // TODO: 2016/7/4 黑名单挂断未完成,aidl的导入包找不到
            LogTool.debug("incomingNumber:" + incomingNumber);

            // 拒接黑名单来电后删除记录
            observer = new IContentObserver(new Handler(), incomingNumber);
            getContentResolver().registerContentObserver(Uri.parse("content://call_log/calls"), true, observer);
            try {
                Class<?> clazz = Class.forName("android.os.ServiceManager");
                Method method = clazz.getMethod("getService", String.class);
                IBinder iBinder= (IBinder) method.invoke(null, Context.TELEPHONY_SERVICE);
                ITelephony iTelephony = ITelephony.Stub.asInterface(iBinder);
                iTelephony.endCall();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private class IContentObserver extends ContentObserver {
        private final String phone;

        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public IContentObserver(Handler handler, String phone) {
            super(handler);
            this.phone = phone;
        }

        @Override
        public void onChange(boolean selfChange) {
            getContentResolver().delete(Uri.parse("content://call_log/calls"), "number=?", new String[]{phone});
        }
    }
}
