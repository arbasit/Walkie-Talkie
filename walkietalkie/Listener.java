package nust.seecs.cs.walkietalkie;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;

public class Listener extends Thread {
    private DatagramSocket socket;
    private MainActivity activity;
    private String deviceName;
    private InetAddress deviceIp;
    private HashMap<String,Boolean> hash;
    private Thread t;
    private byte[] response;
    private byte[] receive;
    public Listener(DatagramSocket socket,MainActivity activity,String deviceName,InetAddress address)
    {
        this.socket=socket;
        this.activity=activity;
        this.deviceIp=address;
        this.deviceName=deviceName;
    }
    @Override
    public void start()
    {

        hash= new HashMap<>();
        if(t==null)
        {
            t=new Thread(this);
            t.start();
        }
    }
    @Override
    public void run()
    {
        while(true)
        {
            try {
                receive=new byte[1024];
                DatagramPacket packet = new DatagramPacket(receive, receive.length);
                socket.receive(packet);
                if(packet.getAddress().equals(deviceIp))
                {
                   // activity.showMyToast("My Own");
                }
                else{
                    //activity.showMyToast(new String(packet.getData(),"utf-8"));
                    String msg=new String(packet.getData(),"utf-8");
                    int method=parseMessage(msg);
                    if(method==1)
                    {
                      //  activity.showMyToast("Responding");
                        respond(packet.getAddress(),packet.getPort());

                    }
                    else
                    {
                       // activity.showMyToast(packet.getAddress().toString());
                        String s[]=msg.split("\r\n");
                        map(s[1]+"\n"+s[2]);
                    }

                }

               // InetAddress ip=packet.getAddress();
                //show(ip);

            } catch (IOException e) {

            }
        }
    }
    public void map(String s)
    {
       // s.replaceAll("\r\n","\n");
        if(!hash.containsKey(s))
        {
            hash.put(s,true);
            activity.printList(hash);
        }
    }

    public int parseMessage(String packet)
    {
        String[] s=packet.split("\r\n");
        if(s[0].equals("ECHO"))
        {
            return 1;
        }
        else if(s[0].equals("ACK")){
            return 2;
        }
        return 0;
    }

    public void respond(InetAddress ip,int port) throws IOException {
        String s="ACK\r\nName:"+deviceName+"\r\nIP"+":"+(deviceIp.toString())+"\r\n\r\n";
        byte[] response=s.getBytes();
        DatagramPacket packet=new DatagramPacket(response,response.length,ip,port);
        socket.send(packet);
    }

    public void reset()
    {
        hash=new HashMap<>();
        activity.printList(hash);
    }

}
