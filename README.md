# Android Reverse Shell

## Introduction
A simple Reverse Shell for Android Operating System by using Sockets and android service. The code can be compiled from Android Studio to APK and the APK must be install on the target device. The APK can get you the Device Info, dump SMS Content and can get you a Shell of the device.APK will be running in background so no layout is active 

## Running
* Set the IP address and the Port number in `Config_var.java` file

## Installation
* Get the APK package of the code from Android Studio -> Build -> Build APK
* Install it on the target device
* And after apk installtion, run `nc -lvnp portnumber` from your terminal to accept the communication, the port number should be same from the one in the `Config_car.java` file
## Available Commands 
* **dumpMessages** : This will dump all the SMS from the device to your terminal
* **deviceInfo** : This will get the device build information 
* **shell** : This will get the Shell of the target Device
* **bye** : To exit the initial shell 
* More are comming 

## Images
<img src="https://github.com/karma9874/AndroidReverseShell/Images/shell.JPG" alt="alt text" width="850" height="500">




