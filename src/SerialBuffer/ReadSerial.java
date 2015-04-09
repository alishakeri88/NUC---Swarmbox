 /*
  *
  * This class reads message from the specific serial port and save
  * the message to the serial buffer.
  *
  */
	package SerialBuffer;
    import java.io.*;
   
    public class ReadSerial extends Thread
    {
        private SerialBuffer ComBuffer;
        private InputStream ComPort;
            /*
             *
             * Constructor
             *
             * @param SB The buffer to save the incoming messages.
             * @param Port The InputStream from the specific serial port.
             *
             */
        public ReadSerial(SerialBuffer SB, InputStream Port)
        {
            ComBuffer = SB;
            ComPort = Port;
        }
        public void run()
        {
            int c;
            try
            {
                while (true)
                {
                    c = ComPort.read();
                    ComBuffer.PutChar(c);
                }
            } catch (IOException e) {}
        }
    }