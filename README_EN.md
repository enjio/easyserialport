# Functions

## 1. List Serial Ports
```
serialPortFinder.getAllDevicesPath();
```

## 2. Serial Port Property Settings
```
serialHelper.setPort(String sPort);      // Set the serial port
serialHelper.setBaudRate(int iBaud);     // Set the baud rate
serialHelper.setStopBits(int stopBits);  // Set the stop bit
serialHelper.setDataBits(int dataBits);  // Set the data bit
serialHelper.setParity(int parity);      // Set the check bit
serialHelper.setFlowCon(int flowcon);    // Set the flow control
```


**Warning:** Serial port property settings must be set before the `open()` function is executed.

## 3. Open Serial Port
```
serialHelper.open();
```

## 4. Close Serial Port
```
serialHelper.close();
```

## 5. Send Data
```
serialHelper.send(byte[] bOutArray); // Send byte array
serialHelper.sendHex(String sHex);   // Send Hex data
serialHelper.sendTxt(String sTxt);   // Send ASCII text
```

## 6. Receive Data
```
@Override
protected void onDataReceived(final ComBean comBean) {
    Toast.makeText(getBaseContext(), new String(comBean.bRec, "UTF-8"), Toast.LENGTH_SHORT).show();
}
```
