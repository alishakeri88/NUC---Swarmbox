package test;

import java.awt.Color;
import java.awt.Font;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.BevelBorder;
import java.awt.CardLayout;
import javax.swing.ImageIcon;

public class Window extends JFrame{
	
	public int BTPort = 0;
	public int AudioBTPort = 0;
	public int ZigBeePort = 0;
	
	public boolean readStartE = false;
	String msg = "";
	
	JPanel BTPanel;
	JPanel AudioPanel;
	JPanel ZigBeePanel;
	JPanel RSSIPanel;
	
	JButton bStart = new JButton("Start");

	ReaderBluetoothAcc RBM = null;
	ReaderBluetoothAud RBA = null;
	ReaderZigBee RZB = null;

	public final static int baudRateMotion = 115200;
	public final static int baudRateAudio = 230400;
	
	public static void main(String[] args){		
		System.out.println("Baud rate is "+baudRateMotion + " for Motion 1");
		System.out.println("Baud rate is "+baudRateAudio + " for audio");
		Window w = new Window();
    }
	
	public Window() {
        getContentPane().setLayout(null);
        
        //Defining Labels
        JLabel lblTerraswarm = new JLabel("");
        lblTerraswarm.setIcon(new ImageIcon("/home/utd/Documents/Read Time Plot/Terraswarm_Logo.png"));
        lblTerraswarm.setBounds(12, 12, 127, 135);
        getContentPane().add(lblTerraswarm);
        
        JLabel lblUTD = new JLabel("UTDallas ESSP Lab");
        lblUTD.setFont(new Font("Courier", Font.BOLD, 20));
        lblUTD.setBounds(12, 284, 250, 47);
        getContentPane().add(lblUTD);
        
        JLabel lblMicrophone = new JLabel("Microphone");
        lblMicrophone.setBounds(919, 136, 90, 15);
        getContentPane().add(lblMicrophone);
        
        JLabel lblBluetooth = new JLabel("Bluetooth Accelerometer");
        lblBluetooth.setBounds(833, 293, 176, 15);
        getContentPane().add(lblBluetooth);
        
        JLabel lblZigbeeAccelerometer = new JLabel("ZigBee Accelerometer");
        lblZigbeeAccelerometer.setBounds(854, 448, 156, 15);
        getContentPane().add(lblZigbeeAccelerometer);
        
        JLabel lblZigBeeRSSI = new JLabel("ZigBee RSSI");
        lblZigBeeRSSI.setBounds(919, 603, 90, 15);
        getContentPane().add(lblZigBeeRSSI);
        
        JLabel lblAudio1 = new JLabel("Audio Sensor - Bluetooth");
        lblAudio1.setBounds(31, 343, 213, 30);
        getContentPane().add(lblAudio1);
        
        JLabel lblAudio2 = new JLabel("Serial Port Number");
        lblAudio2.setBounds(31, 369, 146, 30);
        getContentPane().add(lblAudio2);
        
        JLabel lblMotion1 = new JLabel("MotionNet Sensor - Bluetooth");
        lblMotion1.setBounds(31, 415, 223, 30);
        getContentPane().add(lblMotion1);
        
        JLabel lblMotion2 = new JLabel("Serial Port Number");
        lblMotion2.setBounds(31, 440, 148, 30);
        getContentPane().add(lblMotion2);
        
        JLabel lblZigBee1 = new JLabel("MotionNet Sensor - ZigBee");
        lblZigBee1.setBounds(29, 487, 215, 30);
        getContentPane().add(lblZigBee1);
        
        JLabel lblZigBee2 = new JLabel("Socket Port Number");
        lblZigBee2.setBounds(29, 512, 148, 30);
        getContentPane().add(lblZigBee2);
        
        
        //Defining TextEdit Boxes
        final TextField txtAudioPort = new TextField();
        txtAudioPort.setBounds(183, 369, 61, 30);
        getContentPane().add(txtAudioPort);
        
        final TextField txtMotionPort = new TextField(); 
        txtMotionPort.setBounds(183, 440, 61, 30);
        getContentPane().add(txtMotionPort);
        
        final TextField txtZigBeePort = new TextField();
        txtZigBeePort.setBounds(183, 512, 61, 30);
        getContentPane().add(txtZigBeePort);
        
                
        //Defining Plot Panels
        final Plot1Line AudioPanel = new Plot1Line();
        AudioPanel.setBounds(298, 12, 731, 142);
        getContentPane().add(AudioPanel);
        
        final Plot3Lines BTPanel = new Plot3Lines();
        BTPanel.setBounds(298, 166, 731, 142);
        getContentPane().add(BTPanel);
        
        final Plot3Lines ZigBeePanel = new Plot3Lines();
        ZigBeePanel.setBounds(298, 320, 731, 142);
        getContentPane().add(ZigBeePanel);       
        
        final Plot1Line RSSIPanel = new Plot1Line();
        RSSIPanel.setBounds(298, 474, 731, 142);
        getContentPane().add(RSSIPanel);
        
    
        //Defining Background Panels
        JPanel AudioPanelBack = new JPanel();
        AudioPanelBack.setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));
        AudioPanelBack.setBounds(12, 341, 250, 69);
        getContentPane().add(AudioPanelBack);
        AudioPanelBack.setLayout(new CardLayout(0, 0));
        
        JPanel MotionPanelBack = new JPanel();
        MotionPanelBack.setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));
        MotionPanelBack.setBounds(12, 415, 250, 69);
        getContentPane().add(MotionPanelBack);
        MotionPanelBack.setLayout(new CardLayout(0, 0));
        
        JPanel ZiggBeePanelBack = new JPanel();
        ZiggBeePanelBack.setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));
        ZiggBeePanelBack.setBounds(12, 487, 250, 69);
        getContentPane().add(ZiggBeePanelBack);
        ZiggBeePanelBack.setLayout(new CardLayout(0, 0));
        
                
        //Plotting starts by clicking start button
        bStart.setBounds(12, 568, 250, 30);
        bStart.addActionListener(new ActionListener()
		{
		      public void actionPerformed(ActionEvent e)
		      {   		    	  
		    	  try{
			    	  //Getting the address of serial ports
		    		  BTPort = Integer.parseInt(txtMotionPort.getText());
			    	  AudioBTPort = Integer.parseInt(txtAudioPort.getText());
			    	  
			    	  //Getting the port number for socket connection
			    	  ZigBeePort = Integer.parseInt(txtZigBeePort.getText());
			    	  
			    	  //Start reading and plotting data
			    	  if(readStartE==false){
			    		  
			    		  // Audio Plot (Voice Data)
			    		  if(RBA == null){
			    			  RBA = new ReaderBluetoothAud(AudioPanel, baudRateAudio, AudioBTPort);
			    			  RBA.start();
			    		  }
			    		  
			    		  //Motion Plot (IMU Data)
			    		  if(RBM == null){
			    			  RBM = new ReaderBluetoothAcc(BTPanel, baudRateMotion, BTPort);
			    			  RBM.start();
			    		  }
			    		  
			    		  // Motion and RSSI Plot
			    		  if (RZB == null){
			    			  RZB = new ReaderZigBee(ZigBeePanel, RSSIPanel, ZigBeePort);
			    			  RZB.start();
			    		  }
			    		  
			    		  //These configuration let us use the pause option
			    		  readStartE = true;
		    			  RBM.isStart = true;
		    			  RBA.isStart = true;
		    			  RZB.isStart = true;
		    			  bStart.setText("Pause");
			    		  
			    	  }else{//pause record
			    		  readStartE = false;
		    			  RBM.isStart = false;
		    			  RBA.isStart = false;
		    			  RZB.isStart = false;
		    			  bStart.setText("Start");
			    	  }
		      	}catch(NumberFormatException e1){
		      		//System.out.print(e1);
		      	}
		      }
		});
        getContentPane().add(bStart); 
        

        this.setBackground(new Color(180,255,255));
        this.setTitle("UTDallas ESSP");
        this.setSize(1043, 655);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.setAlwaysOnTop(true);
    }
}


