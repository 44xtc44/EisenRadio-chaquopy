# EisenRadio-chaquopy



## Overview

This repository shows the source code of an Android application with Python package 
[EisenRadio](https://github.com/44xtc44/EisenRadio)
as backend.

Java / Python calls with Chaquopy plugin for Gradle. https://github.com/chaquo/chaquopy



<table style="padding:10px">
<tr>
  <td>
      <img src="./android_studio_class_eisen_chakko.png"  alt="classes"  >
  </td>
    
  <td>
      <img src="./android_studio_avm_eisen_chakko.png" alt="AVM open">
  </td>

  <td>
      
* Java TextView 

* Python calculates

* Python backend

* logcat ForeGround

  </td>
</tr>
</table>



## Running the project

Install latest Android Studio.

Start a virtual device (AVD) and enable the preinstalled Browser, not Viewer. 

Enable Notifications in the "Settings" app or menu.

## How it works

* [DisplayMessageActivity.java](https://github.com/44xtc44/EisenRadio-chaquopy/blob/dev/app/src/main/java/com/hornr/BasicModules/DisplayMessageActivity.java) -
Java TextView that sends a message to another activity. 

* [PythonModuleActivity.java](https://github.com/44xtc44/EisenRadio-chaquopy/blob/dev/app/src/main/java/com/hornr/PythonModule/PythonModuleActivity.java) -
A Python return to the Java interpreter via JSON.

* [PythonApp](https://github.com/44xtc44/EisenRadio-chaquopy/tree/dev/app/src/main/java/com/hornr/PythonApp) -
A Python backend runs in a foreground promoted service on Android. Frontend is a Browser.

* [runEisenRadio.java](https://github.com/44xtc44/EisenRadio-chaquopy/blob/dev/app/src/main/java/com/hornr/PythonApp/runEisenRadio.java) -
ForeGroundService has to implement a notification routine. Choice is a Java Callable.

* [RadioRecorderInfo.java](https://github.com/44xtc44/EisenRadio-chaquopy/blob/dev/app/src/main/java/com/hornr/PythonApp/RadioRecorderInfo.java) -
Access the localhost network to request app information. Network config.

* [PythonAppActivity.java](https://github.com/44xtc44/EisenRadio-chaquopy/blob/dev/app/src/main/java/com/hornr/PythonApp/PythonAppActivity.java) -
Deal with the new multi-access Permissions handling.

* [ForeGroundService.java](https://github.com/44xtc44/EisenRadio-chaquopy/blob/dev/app/src/main/java/com/hornr/BasicModules/ForeGroundService.java) -
An idle ForeGroundService to show its implementation.


## Thank you

[joaoventura](https://github.com/joaoventura/pybridge)


## License

MIT
