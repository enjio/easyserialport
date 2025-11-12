# Functions

## 1. List Serial Ports
```
serialPortFinder.getAllDevicesPath();
```

## 2. Serial Port Property Settings
```
serialPort.setPort(String sPort);      // Set the serial port
serialPort.setBaudRate(int iBaud);     // Set the baud rate
serialPort.setStopBits(int stopBits);  // Set the stop bit
serialPort.setDataBits(int dataBits);  // Set the data bit
serialPort.setParity(int parity);      // Set the check bit
serialPort.setFlowCon(int flowcon);    // Set the flow control
```


**Warning:** Serial port property settings must be set before the `open()` function is executed.

## 3. Open Serial Port
```
serialPort.open();
```

## 4. Close Serial Port
```
serialPort.close();
```

## 5. Send Data
```
serialPort.send(byte[] bOutArray); // Send byte array
serialPort.sendHex(String sHex);   // Send Hex data
serialPort.sendTxt(String sTxt);   // Send ASCII text
```

## 6. Receive Data
```java
 serialPort.setListener(new EasySerialPort.OnSerialPortReceivedListener() {
    @Override
    public void onSerialPortDataReceived(ComPortData comPortData) {
        String str = HexStringUtils.byteArray2HexString(comPortData.getRecData());
        Log.i("keyboad", str);
    }
})
```

## 7. Monitor serial port opening and closing

```java
serialPort.setSatesListener(new EasySerialPort.OnStatesChangeListener() {
    @Override
    public void onOpen(boolean isSuccess, String reason) {
        Log.i("EasySerialPort", "isSuccess：$isSuccess,reason：$reason");
    }

    @Override
    public void onClose() {
        Log.i("EasySerialPort", "cloased");
    }
})
```
