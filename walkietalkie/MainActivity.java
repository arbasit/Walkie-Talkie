package nust.seecs.cs.walkietalkie;

import android.Manifest;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static ListView listView;
    private static ArrayList<String> list= new ArrayList<>();
    private static ArrayAdapter<String> adapter;
    private static Listener listener;
    private static DatagramSocket socket;
    private static Scanner scanner;
    private static AudioReceiver receiver;
    private static AudioBroadcast broadcast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String[] permission= {Manifest.permission.RECORD_AUDIO};
        ActivityCompat.requestPermissions(this,permission,200); // asking for audio permissions
        Button reset=findViewById(R.id.reset);

        IP ip=new IP(getApplicationContext());
        InetAddress broadcastAddress=ip.getBroadcastAddress();
        InetAddress myIp=ip.getIP();

        adapter= new ArrayAdapter<>(this, R.layout.row, list);
        listView=findViewById(R.id.users);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        try {
            socket=new DatagramSocket(25000);
            listener=new Listener(socket,this,android.os.Build.MODEL,ip.getIP()); //listener for pings
            scanner= new Scanner(socket,broadcastAddress,25000);                //scanner for pinging devices
            scanner.start();
            listener.start();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.reset();
            }
        });

        receiver=new AudioReceiver(myIp);
        receiver.receiveAudio();

        broadcast=new AudioBroadcast(broadcastAddress);

        Button talk=findViewById(R.id.talk);
        talk.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        broadcast.startAudioBroadcast();
                        break;

                    case MotionEvent.ACTION_UP:
                        broadcast.stopAudioBroadcast();
                        break;
                }
                return false;
            }
        });
        refresh();
    }

    /**
     * Displays a hash map on the screen in the form of a list view
     * @param map hash map to display
     */
    public void printList(final HashMap<String,Boolean> map)
    {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                adapter.clear();
                Set<String> keys=map.keySet();
                String out="";
                for (String s:keys)
                {
                    list.add(s);
                }
                adapter.notifyDataSetChanged();

            }

        });
    }

    /**
     * Refreshes the contents of ListView
     */
    private void refresh()
    {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10000);
                    listener.reset();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }
}