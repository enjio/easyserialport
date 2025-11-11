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

import android_led_api.LEDUtils;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onUartClick(View view) {
        startActivity(new android.content.Intent(this, UartMainActivity.class));
    }


    public void onOffClick(View view) {
        sendBroadcast(new Intent("android.intent.action.SHUTDOWN"));
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

}