package cc.springwind.mobileguard.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

import cc.springwind.mobileguard.utils.Constants;
import cc.springwind.mobileguard.utils.SpTool;

/**
 * Created by HeFan on 2016/6/30.
 */
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        TelephonyManager manager= (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String localSimSerialNumber = manager.getSimSerialNumber();
        String prefSimSerialNumber = SpTool.getString(context, Constants.SIM_SERIAL_NUMBER, "");
        if (!localSimSerialNumber.equals(prefSimSerialNumber)){
            /*SmsManager mSmsManager=SmsManager.getDefault();
            mSmsManager.sendTextMessage("15705990805",null,"sim change!!!", null, null);*/
        }
    }
}
