# EasySerialPort

[English](https://github.com/enjio/easyserialport/blob/main/README_EN.md)
[LEDUtils](https://github.com/enjio/easyserialport/blob/main/LEDUTILS.md)


## 添加仓库到根目录的 settings.gradle

在 `repositories` 块末尾添加：

```gradle
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

## 第二步：添加依赖

```gradle
dependencies {
    implementation 'com.github.enjio:easyserialport:1.0.0'
}
```

# 功能说明

## 1. 列出串口设备

```java
serialPortFinder.getAllDevicesPath();
```

## 2. 串口属性设置

```java
serialPort.setPort(String sPort);      // 设置串口
serialPort.setBaudRate(int iBaud);     // 设置波特率
serialPort.setStopBits(int stopBits);  // 设置停止位
serialPort.setDataBits(int dataBits);  // 设置数据位
serialPort.setParity(int parity);      // 设置校验位
serialPort.setFlowCon(int flowcon);    // 设置流控制
```

**注意：** 串口属性设置必须在 `open()` 函数执行之前设置。

## 3. 打开串口

```java
serialPort.open();
```

## 4. 关闭串口

```java
serialPort.close();
```

## 5. 发送数据

```java
serialPort.send(byte[] bOutArray); // 发送字节数组
serialPort.sendHex(String sHex);   // 发送十六进制数据
serialPort.sendTxt(String sTxt);   // 发送ASCII文本
```

## 6. 接收数据

```java
 serialPort.setListener(new EasySerialPort.OnSerialPortReceivedListener() {
    @Override
    public void onSerialPortDataReceived(ComPortData comPortData) {
        String str = HexStringUtils.byteArray2HexString(comPortData.getRecData());
        Log.i("keyboad", str);
    }
})
```

## 7. 监听串口打开关闭

```java
serialPort.setSatesListener(new EasySerialPort.OnStatesChangeListener() {
    @Override
    public void onOpen(boolean isSuccess, String reason) {
        Log.i("EasySerialPort", "是否开启成功：$isSuccess,原因：$reason");
    }

    @Override
    public void onClose() {
        Log.i("EasySerialPort", "已关闭");
    }
})
```


## 基于[Android-Serialport](https://github.com/xmaihh/Android-Serialport)修改

