package nust.seecs.cs.walkietalkie;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class AudioReceiver {

    private int AUDIO_PORT = 50005;
    private int SAMPLE_RATE = 8000;
    private int SAMPLE_SIZE = 2;
    private int BUF_SIZE = 10000;
    private InetAddress deviceIP;

    public AudioReceiver(InetAddress ip)
    {
        deviceIP=ip;
    }

    /**
     * Runs the thread which receives and plays the audio
     */
    public void receiveAudio()
    {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run()
            {
                AudioTrack track = new AudioTrack(AudioManager.STREAM_MUSIC,
                        SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO,
                        AudioFormat.ENCODING_PCM_16BIT, BUF_SIZE,
                        AudioTrack.MODE_STREAM);
                track.play();
                try
                {
                    DatagramSocket sock = new DatagramSocket(AUDIO_PORT);
                    byte[] buf = new byte[BUF_SIZE];
                    while(true)
                    {
                        DatagramPacket pack = new DatagramPacket(buf, BUF_SIZE);
                        sock.receive(pack);
                        if(!pack.getAddress().equals(deviceIP))
                        {
                            track.write(pack.getData(),0,pack.getLength());
                        }
                    }
                }
                catch (SocketException se)
                {
                }
                catch (IOException ie)
                {
                }
            }
        });
        t.start();
    }
}
