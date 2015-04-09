#test
This package can read data from Bluetooth and ZigBee sensors, translate the raw data and plot them.

##Plot1Line
This class is responsible to open a panel and configure it in a way that we can plot one line in that panel.
We used this class to plot RSSI and audio signals.

##Plot3Lines
This class is responsible to open a panel and configure it in a way that we can plot three lines in that panel.
We used this class to plot the accelerometer x,y and z values.

##ReaderBluetoothAud
This class uses the SerialBuffer package to connect to the serial port and read the audio signal. Then, it translates those raw data to the real values and calls the Plot1Line class to plot audio amplitude.

##ReaderBluetoothAcc
This class is doing the same thing as ReaderBluetoothAcc, but it reads, translates and plots the accelerometer x,y and z values.

##ReaderZigBee
This class is responible to receive ZigBee packets from Beaglebone Black. Since our interface is ZigBee and the receiving device is BBB, we use a socket server-client program to transfer the data to the NUC.

Then it extracts the accelerometer x,y and z values and plots them.
