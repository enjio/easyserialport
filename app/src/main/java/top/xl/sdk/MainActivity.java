package top.xl.sdk;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android_led_api.LEDUtils;

public class MainActivity extends AppCompatActivity {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private static final int BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE = 1002;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 检查并请求权限
        checkAndRequestLocationPermissions();

        Intent intent = new Intent(this, LocationService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
    }

    public void onUartClick(View view) {
        startActivity(new android.content.Intent(this, UartMainActivity.class));
    }

    int ledopened = 0;
    public void onLedClick(View view) {
        if(ledopened == 0) {
            LEDUtils.setled(LEDUtils.RED, true);
            ledopened = 1;
        }else if(ledopened == 1){
            LEDUtils.setled(LEDUtils.RED,false);
              ledopened = 2;
        }else if(ledopened == 2){
            LEDUtils.setled(LEDUtils.GREEN,true);
            ledopened = 3;
        }else if(ledopened == 3){
            LEDUtils.setled(LEDUtils.GREEN,false);
            ledopened = 0;
        }
    }
    // ==================== 权限与定位相关 ====================
    // 检查并请求位置权限
    private void checkAndRequestLocationPermissions() {
        if (!PermissionUtils.hasLocationPermission(this)) {
            // 没有位置权限，需要请求
            requestLocationPermissions();
        } else {
            // 已有位置权限，检查后台权限
            checkBackgroundLocationPermission();
        }
    }

    // 请求位置权限
    private void requestLocationPermissions() {
        // 如果需要向用户解释为什么需要权限
        if (PermissionUtils.shouldShowRequestPermissionRationale(this)) {
            showPermissionExplanationDialog();
        } else {
            // 直接请求权限
            String[] permissions;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                permissions = new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                };
            } else {
                permissions = new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION
                };
            }

            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE
            );
        }
    }

    // 显示权限说明对话框
    private void showPermissionExplanationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("需要位置权限")
                .setMessage("应用需要位置权限来提供定位服务，是否授权？")
                .setPositiveButton("确定", (dialog, which) -> {
                    // 用户点击确定，请求权限
                    String[] permissions = {
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    };
                    ActivityCompat.requestPermissions(this,
                            permissions,
                            LOCATION_PERMISSION_REQUEST_CODE
                    );
                })
                .setNegativeButton("取消", null)
                .show();
    }

    // 检查后台位置权限（Android 10+）
    private void checkBackgroundLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
                !PermissionUtils.hasBackgroundLocationPermission(this)) {

            // 请求后台位置权限
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                            Manifest.permission.INTERNET,
                            Manifest.permission.ACCESS_NETWORK_STATE,
                            Manifest.permission.WAKE_LOCK,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    },
                    BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE
            );
        } else {
            // 所有权限都已获取，开始位置监控
            startLocationMonitoring();
        }
    }

    // 处理权限请求结果
    @Override
    public void onRequestPermissionsResult(int requestCode,  String[] permissions,
                                            int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 位置权限已授予，检查后台权限
                    checkBackgroundLocationPermission();
                } else {
                    // 权限被拒绝
                    handlePermissionDenied();
                }
                break;

            case BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 后台位置权限已授予
                    startLocationMonitoring();
                } else {
                    // 后台权限被拒绝，但仍可以获取一次性位置
                    Log.w("Permission", "后台位置权限被拒绝");
                    startLocationMonitoring();
                }
                break;
        }
    }

    private void startLocationMonitoring() {
        // 启动位置服务
        Intent intent = new Intent(this, LocationService.class);
        startService(intent);
    }
    // 处理权限被拒绝的情况
    private void handlePermissionDenied() {
        new AlertDialog.Builder(this)
                .setTitle("权限被拒绝")
                .setMessage("位置权限被拒绝，应用将无法提供定位服务。你可以在设置中手动授权。")
                .setPositiveButton("去设置", (dialog, which) -> {
                    // 跳转到应用设置页面
                    openAppSettings();
                })
                .setNegativeButton("取消", null)
                .show();
    }

    // 打开应用设置页面
    private void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }
}