package cc.springwind.mobileguard.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

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
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    class InnerSMSReceiver extends BroadcastReceiver {
        // TODO: 2016/7/3 无法拦截到短信
        @Override
        public void onReceive(Context context, Intent intent) {
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
        int mode=mDao.getMode(incomingNumber);
        if (mode==2||mode==3){
            // TODO: 2016/7/3 黑名单挂断未完成
            LogTool.debug("incomingNumber:"+incomingNumber);
        }
    }
}
