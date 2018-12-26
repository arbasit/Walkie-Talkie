package nust.seecs.cs.walkietalkie;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Scanner extends Thread{
    private DatagramSocket socket;
    private InetAddress ip;
    private int port;
    private byte[] msg;
    private Thread t;
    private DatagramPacket packet;
    public Scanner(DatagramSocket socket,InetAddress ip,int port)
    {
        this.socket=socket;
        this.ip=ip;
        this.port=port;
    }


    @Override
    public void start()
    {
        msg="ECHO\r\n\r\n".getBytes();
        packet=new DatagramPacket(msg,msg.length,ip,port);
        if(t==null)
        {
            t = new Thread (this);
            t.start ();
        }
    }

    @Override
    public void run()
    {
      while(true)
      {
          try {
              socket.send(packet);
              currentThread().sleep(3000);
          } catch (IOException e) {
              e.printStackTrace();
          } catch (InterruptedException e) {
              e.printStackTrace();
          }
      }
    }

}
