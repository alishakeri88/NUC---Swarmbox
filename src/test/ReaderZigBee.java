/*
 * This thread enables the socket server program and starts listening on the user predefined port number.
 * It receives the ZigBee packets and puts the value of each packet in the queue, calls the Plot1Line and 
 * Plot3Line classes for real-time plotting.
 */

package test;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ReaderZigBee extends Thread {

	public boolean isStart = false;
	Plot3Lines IMU_Panel;
	Plot1Line RSSI_Panel;
	int port;
		
	private int x;
	private int y;
	private int z;
	private int RSSI;
	
	ConcurrentLinkedQueue<Integer> qx;
	ConcurrentLinkedQueue<Integer> qy;
	ConcurrentLinkedQueue<Integer> qz;
	
	ConcurrentLinkedQueue<Integer> qRSSI;
	
	public double[] localdata1 = new double[800];
	public double[] localdata2 = new double[800];
	public double[] localdata3 = new double[800];
	public double[] localdata4 = new double[4000];
	
	public ReaderZigBee(Plot3Lines IMU_Panel, Plot1Line RSSI_Panel, int port){
		super();
		this.port = port;
		this.IMU_Panel = IMU_Panel;
		this.RSSI_Panel = RSSI_Panel;
		
		qRSSI = new ConcurrentLinkedQueue<>();
		
		qx = new ConcurrentLinkedQueue<>();
		qy = new ConcurrentLinkedQueue<>();
		qz = new ConcurrentLinkedQueue<>();
		
		// Setting up socket connection with the port number entered
		try {
			System.out.println(port);
			welcomeSocket = new ServerSocket(port);
			Socket socket = welcomeSocket.accept();
			is = socket.getInputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// Listening on the socket port
	ServerSocket welcomeSocket;
	InputStream is ;
	
	// Start plotting - the window size of plotting is 800 samples for accelerometer and 4000 samples for RSSI as 
	// we defined it above
	public void run() {
		while(true){
			
			// This calls the function blockingReadOnePacket(x,y,z) to get the Accelerometer value from Motion Sensor
			if(blockingReadOnePacket(qx, qy, qz)){
				
				// Accelerometer x
				Iterator<Integer> itx = qx.iterator();
				int count = 0;
				while(itx.hasNext())
				{
					int a = (int) itx.next();
					localdata1[count++] =  a/16384.0; // Adjusting the amplitude of signal in the panel
				}
				IMU_Panel.datax = localdata1;
				
				// Accelerometer y
				Iterator<Integer> ity = qy.iterator();
				count = 0;
				while(ity.hasNext())
				{
					int a = (int) ity.next();
					localdata2[count++] =  a/16384.0;
				}
				IMU_Panel.datay = localdata2;
				
				// Accelerometer z
				Iterator<Integer> itz = qz.iterator();
				count = 0;
				while(itz.hasNext())
				{
					int a = (int) itz.next();
					localdata3[count++] =  a/16384.0;
				}	
				IMU_Panel.dataz = localdata3;
				
				IMU_Panel.redraw();
			}
			
			// This calls the function blockingReadOnePacket(x) to get the RSSI value from Motion Sensor
			if(blockingReadOnePacket(qRSSI)){
				// RSSI
				Iterator<Integer> itx = qRSSI.iterator();
				int count = 0;
				while(itx.hasNext())
				{
					int a = (int) itx.next();
					localdata4[count++] =  (a-1024)/512.0; // Adjusting the amplitude of signal in the panel
				}
				RSSI_Panel.datax = localdata4;
				
				RSSI_Panel.redraw();
			}
		}
	}
	
	// Reading sensors data (packet) and extracting the accelerometer value. It puts this data in the queue and lets us 
	// use them for plotting
	int dataCount = 0;
	public boolean blockingReadOnePacket(ConcurrentLinkedQueue<Integer> qx, 
										 ConcurrentLinkedQueue<Integer> qy, 
										 ConcurrentLinkedQueue<Integer> qz){
		
		if(isStart){
			byte[] packet = new byte[256];
			int length;
			while(true){
				try {
					length = is.read(packet);
					if(length>0){
						StringBuffer sb = new StringBuffer();
						x = UByteToInt(packet[52],packet[53]);
						y = UByteToInt(packet[54],packet[55]);
						z = UByteToInt(packet[56],packet[57]);
						
						qx.add(x);
						qy.add(y);
						qz.add(z);

						while(qx.size()>800){
							qx.remove();
						}
						
						while(qy.size()>800){
							qy.remove();
						}
						
						while(qz.size()>800){
							qz.remove();
						}
						return true;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
			
	}

	// Reading sensors data (packet) and extracting the RSSI value. It puts this data in the queue and lets us 
	// use them for plotting
	public boolean blockingReadOnePacket(ConcurrentLinkedQueue<Integer> qRSSI){
		if(isStart){
			byte[] packet = new byte[256];
			int length;
			while(true){
				try {
					length = is.read(packet);
					if(length>0){
						StringBuffer sb = new StringBuffer();
						RSSI = -20*(UByteToInt1(packet[79])-65);
						//System.out.println(RSSI);
						qRSSI.add(RSSI);

						while(qRSSI.size()>4000){
							qRSSI.remove();
						}
						return true;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}
	
	// This function puts two bytes together and gives us the real value of accelerometer 
	// Based on UTDallas sensors protocol the data are sent as unsigned integer so this function translates them into
	// signed integer
	private int UByteToInt(byte a, byte b) {
		int myInt = ((a<<8) + b);
		if( myInt >= 32768 ){
			myInt = myInt - 65536;
		}
		return myInt;
	}
	
	// Based on UTDallas sensors protocol the data are sent as unsigned integer so this function translates them into
	// signed integer
	private int UByteToInt1(byte a) {
		int myInt = a;
		if( myInt >= 32768 ){
			myInt = myInt - 65536;
		}
		return myInt;
	}
	
	// If you need to save data to the file uncomment this part
	/*
	int fileNum = 0;
	String folder = null;
	String fileName;
	private BufferedWriter createFile() {
		try {
			Date now = new Date(); 
			Calendar cal = Calendar.getInstance(); 	
			DateFormat df = DateFormat.getDateInstance();
			if(folder == null){
				folder = df.format(now)+"-"+ cal.get(Calendar.HOUR_OF_DAY) +"-"+cal.get(Calendar.MINUTE)+"-MOTION"+MotionIndex;
			}
			fileName = fileNum++ +".txt"; 
			File dirFile  =   new  File(folder);
			dirFile.mkdirs();
			BufferedWriter out = new BufferedWriter(new FileWriter(folder+"\\"+ fileName));
			return out;
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println(e);
		}
		return null;
	}

	private boolean closeFile(BufferedWriter out) {
		try {
			out.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	*/
}