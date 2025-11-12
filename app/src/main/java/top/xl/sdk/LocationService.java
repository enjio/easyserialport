package top.xl.sdk;

import android.app.*;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;


public class LocationService extends Service {

    private static final String CHANNEL_ID = "gps_channel";
    private LocationManager locationManager;
    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();
        startForeground(1, createNotification());
        startLocationMonitoring();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        return START_NOT_STICKY;
    }

    private Notification createNotification() {
        String channelId = "my_service_channel";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "My Service Channel",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        return new NotificationCompat.Builder(this, channelId)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.app_name))
                .setSmallIcon(R.drawable.ic_settings_schpwron)
                .build();
    }


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "GPS Service", NotificationManager.IMPORTANCE_LOW);
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) manager.createNotificationChannel(channel);
        }
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    private void startLocationMonitoring() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        // 检查位置服务是否启用
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) &&
                !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            Log.e("Location", "所有位置服务都不可用");
            return;
        }
        // 创建位置请求 criteria
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE); // 高精度
        criteria.setSpeedRequired(true); // 要求速度信息
        criteria.setPowerRequirement(Criteria.POWER_HIGH); // 高功耗模式获取更频繁更新

        try {
            String bestProvider = locationManager.getBestProvider(criteria, true);
            if (bestProvider == null) {
                Log.e("Location", "没有可用的位置提供者");
                return;
            }
            locationManager.requestLocationUpdates(
                    bestProvider,
                    500,  // 500ms 更新间隔，更快的速度响应
                    0.5f, // 0.5米距离变化触发更新
                    locationListener
            );

        } catch (SecurityException e) {
            Log.e("SpeedMonitor", "位置权限不足", e);
        }
    }

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.d("Location", "位置更新：" + location.getLatitude()+ "," + location.getLongitude()+" "+location.getSpeed());
            if (location.hasSpeed()) {
                float speedMs = location.getSpeed();
                float speedKmh = speedMs * 3.6f;

            }else{

            }
        }
    };
}