package top.xl.sdk;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import top.xl.easyserialport.ComPortData;
import top.xl.easyserialport.EasySerialPort;
import top.xl.easyserialport.util.HexStringUtils;

public class UartMainActivity extends Activity  implements View.OnClickListener {
    private final int REQUEST_CODE = 100;
    private EasySerialPort serialPort;
    EditText et_port;
    EditText et_baudRate ;
    EditText et_send ;
    TextView tv_textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uart);


        initView();

    }

    private void initView() {
        tv_textView = (TextView) findViewById(R.id.textView);
        et_port = (EditText) findViewById(R.id.et_port);
        et_baudRate = (EditText) findViewById(R.id.et_baud_rate);
        et_send = (EditText) findViewById(R.id.et_send);
        Button btn_close = (Button) findViewById(R.id.btn_close);
        Button btn_open = (Button) findViewById(R.id.btn_open);
        Button btn_send = (Button) findViewById(R.id.btn_send);
        btn_close.setOnClickListener(this);
        btn_open.setOnClickListener(this);
        btn_send.setOnClickListener(this);

    }


    protected void onDestroy() {
        if(serialPort!=null){
            serialPort.close();
        }
        super.onDestroy();
    }





    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_close:
                if(serialPort!=null){
                    serialPort.close();
                }
                break;

            case R.id.btn_open:
                int baudrate = Integer.parseInt(String.valueOf(et_baudRate.getText()));
                String port = String.valueOf(et_port.getText());
                try {
                    serialPort = new EasySerialPort.Builder()
                            .setBaudRate(baudrate)
                            .setPort(port)
//                    .setPort("/dev/ttyMT1")
                            .setSatesListener(new EasySerialPort.OnStatesChangeListener() {
                                @Override
                                public void onOpen(boolean isSuccess, String reason) {
                                    Log.i("keyboad", "是否开启成功：$isSuccess,原因：$reason");
                                    tv_textView.append("是否开启成功："+isSuccess+",原因："+reason+"\n");
                                }

                                @Override
                                public void onClose() {
                                    Log.i("keyboad", "已关闭");
                                    tv_textView.append("已关闭\n");
                                }
                            })
                            .setListener(new EasySerialPort.OnSerialPortReceivedListener() {
                                @Override
                                public void onSerialPortDataReceived(ComPortData comPortData) {
                                    String str = HexStringUtils.byteArray2HexString(comPortData.getRecData());
                                    Log.i("keyboad", str);
                                    tv_textView.append(str);
                                }
                            })
                            .build();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if(serialPort.isNotOpen()){
                    serialPort.open();
                }
                break;

            case R.id.btn_send:
                String sendStr = String.valueOf(et_send.getText());
                if(serialPort.isOpen()){
                    serialPort.sendTxtString(sendStr);
                }
                break;

        }
    }
}
