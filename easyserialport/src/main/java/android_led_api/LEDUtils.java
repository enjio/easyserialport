package android_led_api;
import java.io.FileWriter;
import java.io.IOException;

public class LEDUtils {

    static public int RED = 0;
    static public int GREEN = 1;
    static public int BLUE = 2;

    /*
     * color ：（RED ，GREEN，BLUE ）
     * onoff ： （true: 亮， false：灭）
     */
    static public void setled(int color,boolean onoff){
        String leddev = "/sys/class/leds/red/brightness";
        String BLUE_LED_DEV = "/sys/class/leds/blue/brightness";
        String GREEN_LED_DEV = "/sys/class/leds/green/brightness";
        if(color==GREEN)
            leddev = GREEN_LED_DEV;
        else if (color==BLUE)
            leddev = BLUE_LED_DEV;
        writeFile(leddev,onoff ?"255":"0");
    }

    /*
     * color ：（RED ，GREEN，BLUE ）
     * ontime ： （单位ms，闪烁亮的时间）
     * offtime ： （单位ms，闪烁灭的时间）
     * onoff ： （true: 闪烁， false：灭）
     */
//  trigger:  [none] rfkill-any rfkill-none rfkill0 mmc0 mtk_charger_type-online ac-online usb-online battery-charging-or-full battery-charging battery-full battery-charging-blink-full-solid mtk-gauge-online mtk-master-charger-online mtk-slave-charger-online mmc1 rfkill1
    static public void setled(int color,int ontime,int offtime ,boolean onoff){
        String ledtri = "/sys/class/leds/red/trigger";
        String ledontime = "/sys/class/leds/red/delay_on";
        String ledofftime = "/sys/class/leds/red/delay_off";
        if(color==GREEN){
            ledtri = "/sys/class/leds/green/trigger";
            ledontime = "/sys/class/leds/green/delay_on";
            ledofftime = "/sys/class/leds/green/delay_off";
        }
        else if (color==BLUE){
            ledtri = "/sys/class/leds/blue/trigger";
            ledontime = "/sys/class/leds/blue/delay_on";
            ledofftime = "/sys/class/leds/blue/delay_off";
        }
        if(onoff==false){
            writeFile(ledtri,"timer");
            writeFile(ledontime,"0");
            return;
        }
        writeFile(ledtri,"timer");
        writeFile(ledontime,ontime+"");
        writeFile(ledofftime,offtime+"");

    }
    //写文件
    private static void writeFile(String path, String content) {
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(path);
            if (fileWriter != null) {
                fileWriter.write(content);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileWriter != null)
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

}
