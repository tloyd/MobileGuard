package cc.springwind.mobileguard.receiver;

import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.SmsMessage;
import android.widget.Toast;

import cc.springwind.mobileguard.R;
import cc.springwind.mobileguard.service.LocationService;
import cc.springwind.mobileguard.utils.Constants;
import cc.springwind.mobileguard.utils.SpTool;

/**
 * Created by HeFan on 2016/6/30.
 */
public class SmsReceiver extends DeviceAdminReceiver {
    private ComponentName mDeviceAdminSample;
    private DevicePolicyManager mDPM;

    @Override
    public void onReceive(Context context, Intent intent) {
        //组件对象可以作为是否激活的判断标志
        mDeviceAdminSample = new ComponentName(context, SmsReceiver.class);
        mDPM = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);

        boolean setup_over = SpTool.getBoolean(context, Constants.SETUP_OVER, false);
        if (setup_over) {
            Object[] objects = (Object[]) intent.getExtras().get("pdus");
            for (Object object : objects) {
                SmsMessage sms = SmsMessage.createFromPdu((byte[]) object);
                String originatingAddress = sms.getOriginatingAddress();
                String messageBody = sms.getMessageBody();
                if (messageBody.contains("#*alarm*#")) {
                    MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.ylzs);
                    mediaPlayer.setLooping(true);
                    mediaPlayer.start();
                }
                if (messageBody.contains("#*location*#")) {
                    context.startService(new Intent(context, LocationService.class));
                }
                if (messageBody.contains("#*lockscrenn*#")) {
                    //是否开启的判断
                    if (mDPM.isAdminActive(mDeviceAdminSample)) {
                        //激活--->锁屏
                        mDPM.lockNow();
                        //锁屏同时去设置密码
                        mDPM.resetPassword("123", 0);
                    } else {
                        Toast.makeText(context, "请先激活", Toast.LENGTH_LONG).show();
                    }

                }
                if (messageBody.contains("#*wipedate*#")) {
                    if (mDPM.isAdminActive(mDeviceAdminSample)) {
                        mDPM.wipeData(0);//手机数据
                    } else {
                        Toast.makeText(context, "请先激活", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }
}
