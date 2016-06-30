package cc.springwind.mobileguard.service;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

/**
 * Created by HeFan on 2016/6/30.
 */
public class LocationService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();

        LocationManager mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria mCriteria = new Criteria();
        mCriteria.setCostAllowed(true);
        mCriteria.setAccuracy(Criteria.ACCURACY_HIGH);
        String bestProvider = mLocationManager.getBestProvider(mCriteria, true);
        MyLocationListener mLocationListener = new MyLocationListener();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager
                .PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission
                .ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLocationManager.requestLocationUpdates(bestProvider, 0, 0, mLocationListener);
    }

    class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            //经度
            double longitude = location.getLongitude();
            //纬度
            double latitude = location.getLatitude();
            //4,发送短信(添加权限)
            /*SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage("5556", null, "longitude = " + longitude + ",latitude = " + latitude, null, null);*/
            System.out.println("-->>longitude = " + longitude + ",latitude = " + latitude);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
