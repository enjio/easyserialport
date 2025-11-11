# easyserialport
# Document


Add it in your root settings.gradle at the end of repositories:

	dependencyResolutionManagement {
		repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
		repositories {
			mavenCentral()
			maven { url 'https://jitpack.io' }
		}
	}
Step 2. Add the dependency

	dependencies {
	        implementation 'com.github.enjio:easyserialport:1.0.0'
	}



# Function
## 1.List the serial port
```
serialPortFinder.getAllDevicesPath();
```
## 2.Serial port property settings
```
serialHelper.setPort(String sPort);      //set the serial port
serialHelper.setBaudRate(int iBaud);     //set the baud rate
serialHelper.setStopBits(int stopBits);  //set the stop bit
serialHelper.setDataBits(int dataBits);  //set the data bit
serialHelper.setParity(int parity);      //set the check bit
serialHelper.setFlowCon(int flowcon);    //set the flow control


Serial port property settings must be set before the function 'open()' is executed.
## 3. Open the serial port
```
serialHelper.open();
```
## 4.Close the serial port
```
serialHelper.close();
```
## 5.Send
```
serialHelper.send(byte[] bOutArray); // send byte[]
serialHelper.sendHex(String sHex);  // send Hex
serialHelper.sendTxt(String sTxt);  // send ASCII
```
## 6.Receiving
```
 @Override
protected void onDataReceived(final ComBean comBean) {
       Toast.makeText(getBaseContext(), new String(comBean.bRec, "UTF-8"), Toast.LENGTH_SHORT).show();
   }
```
## 7.Sticky processing
Support sticky package processing, the reason is seen in the [Issue](https://github.com/xmaihh/Android-Serialport/issues/1) , the provided sticky package processing
- Not processed (default)
- First and last special character processing
- Fixed length processing
- Dynamic length processing

Supports custom sticky packet processing.

## Step 1
The first step is to implement the [AbsStickPackageHelper](https://github.com/xmaihh/Android-Serialport/blob/master/serialport/src/main/java/tp/xmaihh/serialport/stick/AbsStickPackageHelper.java) interface.
```
/**
 * Accept the message, the helper of the sticky packet processing, return the final data through inputstream, need to manually process the sticky packet, and the returned byte[] is the complete data we expected.
 * Note: This method will be called repeatedly until it resolves to a complete piece of data. This method is synchronous, try not to do time-consuming operations, otherwise it will block reading data.
 */
public interface AbsStickPackageHelper {
    byte[] execute(InputStream is);
}
```
## Step 2
Set sticky package processing
```
serialHelper.setStickPackageHelper(AbsStickPackageHelper mStickPackageHelper);
```

