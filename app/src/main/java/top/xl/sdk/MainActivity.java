package top.xl.sdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onUartClick(View view) {
        startActivity(new android.content.Intent(this, UartMainActivity.class));
    }

    public void onGpioClick(View view) {
        startActivity(new android.content.Intent(this, GPIOMainActivity.class));
    }
    //定时开关机
    public void onAlarmClick(View view) {
        startActivity(new android.content.Intent(this, top.xl.schpwronoff.AlarmClock.class));
    }

    public void onOffClick(View view) {
        sendBroadcast(new Intent("android.intent.action.SHUTDOWN"));
    }
    boolean ledopened = false;
    public void onLedClick(View view) {
        if(ledopened){
            LEDUtils.setled(LEDUtils.GREEN,false);
            ledopened = false;
        }else{
            LEDUtils.setled(LEDUtils.GREEN,true);
            ledopened = true;
        }
    }

    public void onRebootClick(View view) {
        sendBroadcast(new Intent("android.intent.action.REBOOT"));
    }

    public void onFactoryRestOnclick(View view) {
        sendBroadcast(new Intent("android.intent.action.FACTORY_RESET"));
    }

    public void onSNClick(View view) {
        String sn = android.os.Build.SERIAL;
        Toast.makeText(this, sn, Toast.LENGTH_SHORT).show();
    }

    public void onScreenshotClick(View view) {
        sendBroadcast(new Intent("android.intent.action.SCREENSHOT"));
    }

    public void onBrightnessClick(View view) {
        toggleBrightness();
    }




    private static int brightnessleve = 0;
    private void toggleBrightness(){
        brightnessleve = (brightnessleve+1)%4;
        switch(brightnessleve){
            case 0:
                setBrightness(25);
                break;
            case 1:
                setBrightness(120);
                break;
            case 2:
                setBrightness(180);
                break;
            case 3:
                setBrightness(255);
                break;
        }
    }

    private void setBrightness(final int brightness) {
        AsyncTask.execute(new Runnable() {
            public void run() {
                Settings.System.putInt(MainActivity.this.getContentResolver(),
                        Settings.System.SCREEN_BRIGHTNESS, brightness); }
        });
    }
    //音量
    public void onAudioClick(View view) {
        AudioManager  mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int currentVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC); //获取音量
        Toast.makeText(this, "currentVolume:"+currentVolume, Toast.LENGTH_SHORT).show();
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 12, 0); //设置音量
    }
    ///FOTA
    public void onFotaClick(View view) {
        startActivity(new Intent(this, FOTAActivity.class));
    }
    //静默安装
    public void onInstallClick(View view) {
        String result = SilentInstallUtils.installSilent("top.xl.sdk","/sdcard/test.apk");
        Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
    }
}