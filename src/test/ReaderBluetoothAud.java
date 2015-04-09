/*
 * This thread starts reading from serial ports calling the SerialBuffer package.
 * It reads the serial port, extracts the audio amplitude and puts them in the queue.Then, it calls the Plot1Line 
 * for real-time plotting. (Audio Signal)
 */

package test;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

import SerialBuffer.SerialBean;

public class ReaderBluetoothAud extends Thread{
	public boolean isStart = false;
	SerialBean sb;
	int com ;
	int baudRate;
	Plot1Line AudioPanel;
	
	private int x;
	
	ConcurrentLinkedQueue<Integer> qx;
	
	public double[] AudioData = new double[4000];
	
	public ReaderBluetoothAud(Plot1Line AudioPanel,int baudRate, int com){
		super();
		this.baudRate = baudRate;
		this.com = com;
		this.AudioPanel = AudioPanel;
		qx = new ConcurrentLinkedQueue<>();
		sb = new SerialBean(com);
		sb.baudRate = this.baudRate;
		sb.Initialize();
	}

	// Start plotting - the window size of plotting is 4000 samples as we defined it above.
	public void run() {
		while(true){
			// This calls the function blockingReadOnePacket(x) to get the audio signal amplitude from the Sensor
			if(blockingReadOnePacket(qx)){
				// Audio Amplitude
				Iterator<Integer> itx = qx.iterator();
				int count = 0;
				while(itx.hasNext())
				{
					int a = (int) itx.next();
					AudioData[count++] =  (a-1024)/1024.0; // Adjusting the amplitude of signal in the panel
				}
				AudioPanel.datax = AudioData;
				
				AudioPanel.redraw();
			}
		}
	}
	
	// Reading sensors data and extracting the audio amplitude. It puts this data in the queue and lets us 
	// use them for plotting
	public boolean blockingReadOnePacket(ConcurrentLinkedQueue<Integer> qx){
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
			if(dataCount == 10)
			{
				for(int i = 0; i<5; i++){
					x = UCharToInt(packet[2*i+1],packet[2*i]);
					//System.out.println(x);
					qx.add(x);					
				}

				while(qx.size()>4000){
					qx.remove();
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
