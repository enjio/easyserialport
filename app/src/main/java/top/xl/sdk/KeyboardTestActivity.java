package top.xl.sdk;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.Toast;

public class KeyboardTestActivity extends Activity {

    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tvResult = new TextView(this);
        tvResult.setTextSize(20);
        tvResult.setText("请按下任意按键...");
        setContentView(tvResult);


        // 注册按键监听
        IntentFilter filter = new IntentFilter("com.android.action.key");
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY); // 提高优先级
        registerReceiver(keyReceiver, filter);

    }

    @Override
    public void onDestroy() {
            super.onDestroy();
            unregisterReceiver(keyReceiver);
    }
    BroadcastReceiver keyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("com.android.action.key".equals(intent.getAction())) {
                int keycode = intent.getIntExtra("keycode", 0);
                int action = intent.getIntExtra("action", 0);
                String msg = "KeyCode: " + keycode + " (" + KeyEvent.keyCodeToString(keycode) + ")";
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        }
    };
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        String msg = "KeyDown: " + keyCode + " (" + KeyEvent.keyCodeToString(keyCode) + ")";
        tvResult.setText(msg);
        return true; // 拦截事件
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        String msg = "KeyUp: " + keyCode + " (" + KeyEvent.keyCodeToString(keyCode) + ")";
        tvResult.setText(msg);
        return true;
    }


}