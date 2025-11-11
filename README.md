# EasySerialPort

[English](https://github.com/enjio/easyserialport/blob/main/README_EN.md)

## 添加仓库到根目录的 settings.gradle

在 `repositories` 块末尾添加：

```gradle
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
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
serialHelper.setPort(String sPort);      // 设置串口
serialHelper.setBaudRate(int iBaud);     // 设置波特率
serialHelper.setStopBits(int stopBits);  // 设置停止位
serialHelper.setDataBits(int dataBits);  // 设置数据位
serialHelper.setParity(int parity);      // 设置校验位
serialHelper.setFlowCon(int flowcon);    // 设置流控制
```

**注意：** 串口属性设置必须在 `open()` 函数执行之前设置。

## 3. 打开串口

```java
serialHelper.open();
```

## 4. 关闭串口

```java
serialHelper.close();
```

## 5. 发送数据

```java
serialHelper.send(byte[] bOutArray); // 发送字节数组
serialHelper.sendHex(String sHex);   // 发送十六进制数据
serialHelper.sendTxt(String sTxt);   // 发送ASCII文本
```

## 6. 接收数据

```java
@Override
protected void onDataReceived(final ComBean comBean) {
    Toast.makeText(getBaseContext(), new String(comBean.bRec, "UTF-8"), Toast.LENGTH_SHORT).show();
}
```

## 基于[Android-Serialport](https://github.com/xmaihh/Android-Serialport)修改

