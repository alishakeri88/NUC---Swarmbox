# JAVA code on the NUC
We run two JAVA packages on the NUC to receive the data from Beaglebone Black (ZigBee Sensors) and Bluetooth USB dongle (Blurtooth Sensors). Then, we plot them in real-time.

These packages are located in src folder.

##SerialBuffer
This package lets us have access to the serial ports on the NUC. In linux the serial ports are listed in the /dev folder and they are usually called /dev/ttyS* so when we run the program it goes to read these devices.
Since we are connecting our sensors to the NUC via USB interface we can read their data on the /dev/rfcomm* ports which are not known by this package as a serial port!

Here we need to make a little change to introduce these ports to our program. Following commands can link the rfcomm ports to the serial ports (ttyS):

      cd /dev
      ln -s rfcomm0 ttyS50
      ln -s rfcomm1 ttyS51

We need to make sure that these serial ports don't really exist! You can usually use the number more than 50 like what we did.

##test
We named this package as test because we can run different algorithms on it, so it is still under development and we can use it for different purposes.

We use this package to translate the raw sensors data and plot the following:
      
      *The audio signal from Bluetooth sensor
      *The accelerometer x,y and z data from Bluetooth and ZigBee sensors
      *The RSSI value from ZigBee sensor
###ReaderZigBee
This class in the test package is responible to receive ZigBee packets from Beaglebone Black. Since our interface is ZigBee and the receiving device is BBB, we use a socket server-client program to transfer the data to the NUC.
