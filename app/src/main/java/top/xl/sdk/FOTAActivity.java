package top.xl.sdk;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


public class FOTAActivity extends Activity {

    public static String BROADCAST_ACTION_CHECK_VERSION = "ota.update.BROADCAST_ACTION_CHECK_VERSION";
    public static String BROADCAST_ACTION_DOWNLOAD_FILE = "ota.update.BROADCAST_ACTION_DOWNLOAD_FILE";

    public static String BROADCAST_ACTION_FOTA_INFO = "ota.update.BROADCAST_ACTION_FOTA_INFO";

    //是否有新版本下载
    public static String BROADCAST_EXTRA_VERSION = "BROADCAST_EXTRA_VERSION";
    //下载状态 开始 结束
    public static String BROADCAST_EXTRA_DOWNLOAD_STATUS = "BROADCAST_EXTRA_DOWNLOAD_STATUS";
    //下载进度
    public static String BROADCAST_EXTRA_FIREWARE_DOWNLOAD_PROGRESS = "BROADCAST_EXTRA_FIREWARE_DOWNLOAD_PROGRESS";
    //fata包校验结果
    public static String BROADCAST_EXTRA_FIREWARE_CHECK_RESULT = "BROADCAST_EXTRA_FIREWARE_CHECK_RESULT";

    public static int BROADCAST_EXTRA_DOWNLOAD_STATUS_START = 0;
    public static int BROADCAST_EXTRA_DOWNLOAD_STATUS_FINISH = 1;
    public static int BROADCAST_EXTRA_DOWNLOAD_STATUS_FAIL = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fota);

        IntentFilter filter = new IntentFilter();
        filter.addAction(BROADCAST_ACTION_CHECK_VERSION);
        filter.addAction(BROADCAST_ACTION_DOWNLOAD_FILE);
        registerReceiver(mBroadcastReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }

    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BROADCAST_ACTION_FOTA_INFO.equals(action)) {  // 获取版本信息
                if(intent.hasExtra(BROADCAST_EXTRA_VERSION)){ // 版本信息
                    String version = intent.getStringExtra(BROADCAST_EXTRA_VERSION);
                    Log.d("FOTA", "version:"+version);
                    Toast.makeText(FOTAActivity.this, "version:"+version, Toast.LENGTH_SHORT).show();
                }else if(intent.hasExtra(BROADCAST_EXTRA_FIREWARE_CHECK_RESULT)){ // 校验结果
                    boolean result = intent.getBooleanExtra(BROADCAST_EXTRA_FIREWARE_CHECK_RESULT, false);
                    Log.d("FOTA", "result:"+result);
                    Toast.makeText(FOTAActivity.this, "result:"+result, Toast.LENGTH_SHORT).show();
                }else if (intent.hasExtra(BROADCAST_EXTRA_DOWNLOAD_STATUS)){ // 下载状态
                    int status = intent.getIntExtra(BROADCAST_EXTRA_DOWNLOAD_STATUS, -1);
                    if(status == BROADCAST_EXTRA_DOWNLOAD_STATUS_START){ // 下载开始
                        Log.d("FOTA", "download start");
                        Toast.makeText(FOTAActivity.this, "download start", Toast.LENGTH_SHORT).show();
                    }else if(status == BROADCAST_EXTRA_DOWNLOAD_STATUS_FINISH){ // 下载完成
                        Log.d("FOTA", "download finish");
                        Toast.makeText(FOTAActivity.this, "download finish", Toast.LENGTH_SHORT).show();
                    }else if(status == BROADCAST_EXTRA_DOWNLOAD_STATUS_FAIL){ // 下载失败
                        Log.d("FOTA", "download fail");
                        Toast.makeText(FOTAActivity.this, "download fail", Toast.LENGTH_SHORT).show();
                    }
                }else if(intent.hasExtra(BROADCAST_EXTRA_FIREWARE_DOWNLOAD_PROGRESS)){ // 下载进度
                    int progress = intent.getIntExtra(BROADCAST_EXTRA_FIREWARE_DOWNLOAD_PROGRESS, -1);
                    Log.d("FOTA", "download progress:"+progress);
                    Toast.makeText(FOTAActivity.this, "download progress:"+progress, Toast.LENGTH_SHORT).show();
                }

            }
        }
    };

    public void onCheckClick(View view) {
        sendBroadcast(new Intent(BROADCAST_ACTION_CHECK_VERSION)); // 获取版本信息
    }

    public void onInstallClick(View view) {
        sendBroadcast(new Intent(BROADCAST_ACTION_DOWNLOAD_FILE)); // 下载固件
    }

    public void onFotaClick(View view) {
        Intent intent = new Intent();
        intent.setClassName("com.xl.otaupdate", "com.xl.otaupdate.MainActivity");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}