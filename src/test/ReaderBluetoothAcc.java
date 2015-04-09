/*
 * This thread starts reading from serial ports calling the SerialBuffer package.
 * It reads the serial port, extracts the accelerometer value and puts them in the queue.Then, it calls the Plot3Line 
 * for real-time plotting. (Accelerometer)
 */

package test;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

import SerialBuffer.SerialBean;

public class ReaderBluetoothAcc extends Thread{
	public boolean isStart = false;
	SerialBean sb;
	int com ;
	int baudRate;
	Plot3Lines MotionPanel;

	
	private int x;
	private int y;
	private int z;
	
	ConcurrentLinkedQueue<Integer> qx;
	ConcurrentLinkedQueue<Integer> qy;
	ConcurrentLinkedQueue<Integer> qz;
	
	public double[] localdata1 = new double[800];
	public double[] localdata2 = new double[800];
	public double[] localdata3 = new double[800];

	public ReaderBluetoothAcc(Plot3Lines MotionPanel, int baudRate,int com){
		super();
		this.baudRate = baudRate;
		this.com = com;
		this.MotionPanel = MotionPanel;
		qx = new ConcurrentLinkedQueue<>();
		qy = new ConcurrentLinkedQueue<>();
		qz = new ConcurrentLinkedQueue<>();
		sb = new SerialBean(com);
		sb.baudRate = this.baudRate;
		sb.Initialize();
	}


	// Start plotting - the window size of plotting is 800 samples as we defined it above.
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
					localdata1[count++] =  a/4096.0; // Adjusting the amplitude of signal in the panel
				}
				MotionPanel.datax = localdata1;
				
				// Accelerometer y
				Iterator<Integer> ity = qy.iterator();
				count = 0;
				while(ity.hasNext())
				{
					int a = (int) ity.next();
					localdata2[count++] =  a/4096.0;
				}
				MotionPanel.datay = localdata2;
				
				// Accelerometer y
				Iterator<Integer> itz = qz.iterator();
				count = 0;
				while(itz.hasNext())
				{
					int a = (int) itz.next();
					localdata3[count++] =  a/4096.0;
				}	
				MotionPanel.dataz = localdata3;
				
				MotionPanel.redraw();
			}
		
		}
	}
	// Reading sensors data and extracting the accelerometer value. It puts this data in the queue and lets us 
	// use them for plotting
	public boolean blockingReadOnePacket(ConcurrentLinkedQueue<Integer> qx, ConcurrentLinkedQueue<Integer> qy, ConcurrentLinkedQueue<Integer> qz){
		int dataCount = 0;
		final char DLE = 0x10;
		final char SOH = 0x01;
		final char  EOT = 0x04;
		if(isStart){
				char cData = 0;
				char pData = 0;
				char packet[] = new char[256];
				dataCount = 0;
				while (((pData != DLE) || (cData != SOH))){	// find the start of packet
					pData = cData;
					cData = (char) sb.ReadPort(1).toCharArray()[0];
					if ((pData == DLE) && (cData == DLE))
					{
					pData = cData;
					cData = (char) sb.ReadPort(1).toCharArray()[0];
					}
				}
				//data<<"srart detected\r\n";
				while (((pData != DLE) || (cData != EOT)) ){	
					pData = cData;
					cData = (char) sb.ReadPort(1).toCharArray()[0];
				
					if (cData != DLE)
						packet [(dataCount++)%256] = cData;
					else
					{
						pData = cData;
						cData = (char) sb.ReadPort(1).toCharArray()[0];
						if (cData == DLE)
						{
							packet [(dataCount++)%256] = cData;
						}
					}
				}
			
			if(dataCount == 22)
			{
				x = UCharToInt(packet[0],packet[1]);
				y = UCharToInt(packet[2],packet[3]);
				z = UCharToInt(packet[4],packet[5]);
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
		}else{
			sb.ReadPort(10);
		}
		return false;
	}
	// This function puts two bytes together and gives us the real value of accelerometer 
	// Based on UTDallas sensors protocol the data are sent as unsigned integer so this function translates them into
	// signed integer
	public int UCharToInt( char a, char b){
		int myInt = ((a<<8) + b);
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
