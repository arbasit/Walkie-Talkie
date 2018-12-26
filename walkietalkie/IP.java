package nust.seecs.cs.walkietalkie;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 *
 */
public class IP {
    private Context context;
    public IP(Context c)
    {
        this.context=c;
    }

    /**
     * Returns broadcast address of the network
     *
     * @return broadcast address
     */
    public InetAddress getBroadcastAddress()  {
        InetAddress ip=null;
        try{
            WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            DhcpInfo dhcp = wifi.getDhcpInfo();
            int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
            byte[] quads = new byte[4];
            for (int k = 0; k < 4; k++)
                quads[k] = (byte) (broadcast >> (k * 8));
            ip= InetAddress.getByAddress(quads);
        }catch(IOException e)
        {

        }

        return ip;
    }

    /**
     * Returns the ip address of the device
     * @return ip address of device
     */
    public InetAddress getIP()

    {
        InetAddress ip=null;
        try{
            WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            DhcpInfo dhcp = wifi.getDhcpInfo();
            int dip=dhcp.ipAddress;
            byte[] quads = new byte[4];
            for (int k = 0; k < 4; k++)
                quads[k] = (byte) (dip >> (k * 8));
            ip= InetAddress.getByAddress(quads);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return ip;
    }

}
