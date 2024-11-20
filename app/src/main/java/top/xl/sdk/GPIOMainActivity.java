package top.xl.sdk;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.gpiotest2.GpioPort;

public class GPIOMainActivity extends Activity {

        private static final String TAG = "GpioTest";

        private TextView nResault;
        private Button nGPIO_BTN;
        private Spinner nGPIO_SPI;
        private GpioPort mGpioPort;

        Handler mHandler = new Handler();
        private RadioButton nGPIO_OUT,nGPIO_IN;
        private RadioButton nGPIO_L,nGPIO_H;
        static int gpio_int = 0;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            mGpioPort =new  GpioPort();
            mGpioPort.gpio_init();

            setContentView(R.layout.gpio_test);
            nResault = (TextView)findViewById(R.id.GPIO_Resault_Text);
            nGPIO_BTN = (Button)findViewById(R.id.GPIO_btn);
            nGPIO_SPI = (Spinner)findViewById(R.id.GPIO_SPR);
            nGPIO_IN = (RadioButton)findViewById(R.id.GPIO_Direction_In);
            nGPIO_OUT = (RadioButton)findViewById(R.id.GPIO_Direction_Out);
            nGPIO_H = (RadioButton)findViewById(R.id.GPIO_Data_High);
            nGPIO_L = (RadioButton)findViewById(R.id.GPIO_Data_Low);
            RadioGroup nGPIO_DATA_RADG = (RadioGroup) findViewById(R.id.gpio_dir);
            nGPIO_DATA_RADG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    if(i == R.id.GPIO_Direction_In){
                        findViewById(R.id.gpio_data).setVisibility(View.GONE);
                    }else if(i == R.id.GPIO_Direction_Out){
                        findViewById(R.id.gpio_data).setVisibility(View.VISIBLE);
                    }
                }
            });


            nGPIO_BTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mGpioPort.set_mode(gpio_int,0);

                    if(nGPIO_IN.isChecked()){
                        mGpioPort.set_dir(gpio_int,0);

                        nResault.setText("GPIO"+gpio_int+"输入电平"+mGpioPort.get_datain(gpio_int));
                    }else{
                        mGpioPort.set_dir(gpio_int,1);
                        if(nGPIO_H.isChecked()){
                            mGpioPort.set_data(gpio_int,1);
                        }else{
                            mGpioPort.set_data(gpio_int,0);
                        }
                        nResault.setText("GPIO"+gpio_int+"输出电平"+mGpioPort.get_data(gpio_int));
                    }


                }
            });
            String [] gpios= new String[140];
            for(int i = 0 ;i<140;i++){
                gpios[i] = "GPIO"+i;
            }
            ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, gpios);
            nGPIO_SPI .setAdapter(adapter);
            nGPIO_SPI.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view,
                                           int pos, long id) {

                    gpio_int = pos;
//
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // Another interface callback
                }
            });
        }


        @Override
        public void onResume() {
            super.onResume();

        }

        public void setGpioOutput(int gpio, int value)
        {
            if (mGpioPort != null) {
                mGpioPort.set_mode(gpio,0);
                mGpioPort.set_dir(gpio,1);
                mGpioPort.set_data(gpio,value);
            }

        }


        @Override
        public void onPause() {

            super.onPause();

        }

        @Override
        public void onDestroy() {
            if (mGpioPort != null) {
                mGpioPort.gpio_deinit();
                mGpioPort = null;
            }
            super.onDestroy();
        }
    }


