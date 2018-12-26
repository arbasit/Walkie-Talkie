package nust.seecs.cs.walkietalkie;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class AudioBroadcast {
    private int PORT = 50005;
    private int RECORDING_RATE = 8000;
    private int CHANNEL = AudioFormat.CHANNEL_IN_MONO;
    private int FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    private int BUFFER_SIZE = AudioRecord.getMinBufferSize(
            RECORDING_RATE, CHANNEL, FORMAT);
    private AudioRecord recorder;
    private boolean recording = false;
    private DatagramSocket socket;
    private InetAddress broadcastAddress;
    private byte[] buffer;
    private DatagramPacket packet;
    public AudioBroadcast(InetAddress broadcastAddress)
    {
        this.broadcastAddress=broadcastAddress;
        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    /**
     * starts audio streaming
     */
    public void startAudioBroadcast() {
        recording = true;
        startBroadcast();
    }

    /**
     * stops audio streaming
     */
    public void stopAudioBroadcast() {
        recording = false;
        recorder.release();
    }

    /**
     * starts the audio stream thread
     */
    private void startBroadcast() {
       Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    buffer = new byte[BUFFER_SIZE];
                    recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                            RECORDING_RATE, CHANNEL, FORMAT, BUFFER_SIZE * 10);
                    recorder.startRecording();
                    while (recording == true) {
                        int read = recorder.read(buffer, 0, buffer.length);
                        packet = new DatagramPacket(buffer, read,
                                broadcastAddress, PORT);
                        socket.send(packet);
                    }
                } catch (UnknownHostException e) {

                } catch (IOException e) {

                } catch (IllegalStateException e) {
                }
            }
        });
       t.start();
    }
}
