
# EasySerialPort – Developer Guide

**Version:** 1.0.0 (as on GitHub)  
**Repository:** https://github.com/enjio/easyserialport  
**License / Disclaimer:** Check the repository's LICENSE file to confirm usage rights  
**Purpose:** A simplified serial-port communication library for Android (based on Android-Serialport)

---

## Table of Contents

1. [Introduction](#1-introduction)
2. [Getting Started](#2-getting-started)
   - [Repository setup](#21-repository-setup)
   - [Dependency inclusion](#22-dependency-inclusion)
3. [Core Concepts](#3-core-concepts)
   - [Device enumeration](#31-device-enumeration)
   - [Serial port configuration](#32-serial-port-configuration)
   - [Opening / closing port](#33-opening--closing-port)
   - [Sending data](#34-sending-data)
   - [Receiving data & listeners](#35-receiving-data--listeners)
   - [States monitoring](#36-states-monitoring)
4. [Code Examples](#4-code-examples)
   - [Simple workflow](#41-simple-workflow)
   - [Hex vs TXT sending](#42-hex-vs-txt-sending)
   - [Error handling / states](#43-error-handling--states)
5. [Best Practices & Notes](#5-best-practices--notes)
6. [Troubleshooting / FAQ](#6-troubleshooting--faq)
7. [Contribution & Roadmap](#7-contribution--roadmap)
8. [References](#8-references)

---

## 1. Introduction

EasySerialPort is designed to simplify serial port access on Android devices. The library allows you to:

- List available serial-port device paths
- Configure baud rate, data bits, stop bits, parity, flow control
- Open and close the port
- Send raw bytes, hex-formatted strings, ASCII text
- Receive data via listener callbacks
- Monitor port open/close state events

It is based on the project Android-Serialport but offers a cleaner API.

---

## 2. Getting Started

### 2.1 Repository setup

1. Clone the repository:

```bash
git clone https://github.com/enjio/easyserialport.git
cd easyserialport
```

2. In your Android project root (e.g., in `settings.gradle` or equivalent), ensure you include the dependency repository. The README indicates using jitpack:

```gradle
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        // ...
        maven { url 'https://jitpack.io' }
    }
}
```

### 2.2 Dependency inclusion

In your `build.gradle` (app module) add:

```gradle
dependencies {
    implementation 'com.github.enjio:easyserialport:1.0.0'
}
```

Ensure your project uses appropriate Android permissions (e.g., for serial device access) and relevant SDKs.

---

## 3. Core Concepts

Here are the major API capabilities:

### 3.1 Device enumeration

Use the `SerialPortFinder` (or equivalent component) to list all available device paths:

```java
List<String> paths = serialPortFinder.getAllDevicesPath();
```

This returns a list of strings (device paths) for the serial ports present.

### 3.2 Serial port configuration

Before opening the port, set the parameters. Example:

```java
serialPort.setPort(String sPort);           // e.g. "/dev/ttyS0"
serialPort.setBaudRate(int iBaud);          // e.g. 9600
serialPort.setStopBits(int stopBits);       // e.g. 1 or 2
serialPort.setDataBits(int dataBits);       // e.g. 8
serialPort.setParity(int parity);           // e.g. 0 (none), 1 (odd), 2 (even)
serialPort.setFlowCon(int flowcon);         // flow-control: e.g. 0 (none), 1 (RTS/CTS), etc.
```

**Important:** Configuration must happen before calling `open()`.

### 3.3 Opening / closing the port

```java
boolean success = serialPort.open();
// ...
serialPort.close();
```

You should monitor the result (success or failure). The library also offers a listener for state changes.

### 3.4 Sending data

There are multiple send APIs:

- `serialPort.send(byte[] bOutArray);` – send raw bytes
- `serialPort.sendHex(String sHex);` – send hex-formatted string
- `serialPort.sendTxt(String sTxt);` – send ASCII text

### 3.5 Receiving data & listeners

Set a listener to receive incoming data:

```java
serialPort.setListener(new EasySerialPort.OnSerialPortReceivedListener() {
    @Override
    public void onSerialPortDataReceived(ComPortData comPortData) {
        byte[] recBytes = comPortData.getRecData();
        String hexStr = HexStringUtils.byteArray2HexString(recBytes);
        Log.i("EasySerialPort", hexStr);
    }
});
```

The callback provides a `ComPortData` object, from which you can get the received byte array.

### 3.6 State monitoring (open/close)

You can also listen for state changes (open success/failure, closed events):

```java
serialPort.setStatesListener(new EasySerialPort.OnStatesChangeListener() {
    @Override
    public void onOpen(boolean isSuccess, String reason) {
        Log.i("EasySerialPort", "Open success: " + isSuccess + ", reason: " + reason);
    }
    
    @Override
    public void onClose() {
        Log.i("EasySerialPort", "Closed");
    }
});
```

This is helpful to react appropriately if the port fails to open.

---

## 4. Code Examples

### 4.1 Simple workflow

```java
// 1. List devices
List<String> paths = serialPortFinder.getAllDevicesPath();

// 2. Choose device, configure
serialPort.setPort(paths.get(0));
serialPort.setBaudRate(115200);
serialPort.setDataBits(8);
serialPort.setStopBits(1);
serialPort.setParity(0);
serialPort.setFlowCon(0);

// 3. Setup listeners
serialPort.setStatesListener(new EasySerialPort.OnStatesChangeListener() {
    @Override
    public void onOpen(boolean success, String reason) {
        // Handle open state
    }
    
    @Override
    public void onClose() {
        // Handle close state
    }
});

serialPort.setListener(new EasySerialPort.OnSerialPortReceivedListener() {
    @Override
    public void onSerialPortDataReceived(ComPortData data) {
        // Handle incoming bytes
        byte[] receivedData = data.getRecData();
        // Process the data...
    }
});

// 4. Open port
if (serialPort.open()) {
    // Send data
    serialPort.sendTxt("Hello device");
    // or send hex
    serialPort.sendHex("A1B2C3");
}

// 5. Later, close port
serialPort.close();
```

### 4.2 Hex vs Text sending

- If you need to send binary or protocol-specific data (e.g., control codes), use `send(byte[])` or `sendHex(String)`
- If sending plain human readable text (ASCII/UTF-8) to the device, use `sendTxt(String)`

### 4.3 Error handling / state monitoring

- Use the `OnStatesChangeListener` callback's `onOpen(...)` to detect failures early. If `isSuccess == false`, you cannot safely send or receive
- In `onClose()`, you should stop further send/receive operations and release resources
- Always check the returned boolean from `open()` before proceeding
- Be cautious about permission (Android) issues: you may need to request appropriate access to the serial device node (e.g., `/dev/ttyS0`) or configure the device in Linux/Android correctly

---

## 5. Best Practices & Notes

- **Set configuration before opening:** Changing baud rate, parity etc after `open()` may not apply
- **Threading:** Sending and receiving should ideally be done off the UI thread to avoid blocking the UI
- **Buffering / Framing:** For protocols you may need to implement your own buffering and frame-parsing logic (e.g., delimiters, checksums) on top of the raw byte callbacks
- **Flow control / hardware signals:** If your hardware uses RTS/CTS or DTR/DSR lines for flow control, set `flowCon` accordingly and ensure hardware lines are wired correctly
- **Resource cleanup:** Always call `serialPort.close()` when your activity/fragment is destroyed or when you finish communication, to release file descriptors and avoid leaks
- **Permissions (Android):** On some devices you may need to request runtime permissions for USB/serial device, or have system-level permissions set (e.g., root or privileged device)
- **Logging / debugging:** For binary data, convert to hex helps debug what bytes are exchanged
- **Device compatibility:** This library is Android-centric and may require kernel driver support for the serial device nodes

---

## 6. Troubleshooting / FAQ

| Issue | Likely cause | Solution |
|-------|-------------|----------|
| `open()` fails or `onOpen(false, reason)` reports failure | Device path incorrect, permission denied, incompatible baud/parameters, device locked by other process | Verify device path (e.g., `/dev/ttyS0`), ensure app has permission, ensure no other process holds the port, try a different baud rate |
| No data received in listener though hardware sends | Wrong wiring, flow control mismatch, wrong port settings (stop/parity/data bits) | Check wiring, check `flowCon`, ensure both ends have matching parity/baud/data bits, test with known working tool |
| Data is garbled or incorrect | Wrong encoding (text vs binary), mismatched data bits/parity/stop bits, device uses different protocol | Confirm both sides agree on frame format, test with simple known ASCII text, inspect raw bytes via hex |
| App crashes or file descriptor leak | Port not closed properly, operations done on UI thread blocking | Ensure `serialPort.close()` is called in `onDestroy()` or similar, perform send/receive on background threads |
| Inconsistent behavior across devices | Kernel driver differences, permission/security restrictions, different device tree configurations | Test on target hardware, check vendor/board supports serial nodes, adjust SELinux policies if required |

---

## 7. Contribution & Roadmap

- The repository indicates that it is modified from Android-Serialport
- If you want to contribute: fork the repo, open pull requests, follow the contributor guidelines (if any)
- Possible roadmap items:
  - Support more flow control options (XON/XOFF)
  - Support non-blocking I/O or event-driven reads
  - Provide Kotlin DSL / coroutines support
  - Extend for USB-serial adapters (FTDI/CH340)
  - Add more example apps or test suite
  - Improve documentation, error codes, logging levels

---

## 8. References

- EasySerialPort GitHub: https://github.com/enjio/easyserialport
- Android-Serialport project (base)
- Serial port communication protocol fundamentals (baud rates, parity, stop bits, flow control)
- Android documentation for permissions and hardware access
```
