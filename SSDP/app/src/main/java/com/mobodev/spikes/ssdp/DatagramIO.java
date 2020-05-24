package com.mobodev.spikes.ssdp;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.logging.Level;

public class DatagramIO implements Runnable {
    private static final String TAG = "DatagramIO";

    private int timeToLive = 4;
    private int maxDatagramBytes = 640;

    private MulticastSocket socket; // For sending unicast & multicast, and reveiving unicast
    private InetAddress localAddress;

    public DatagramIO() {
        try {
            socket = new MulticastSocket(0);
            socket.setTimeToLive(timeToLive);
            socket.setReceiveBufferSize(262144); // Keep a backlog of incoming datagrams if we are not fast enough

            localAddress = socket.getLocalAddress();
            Log.i(TAG, "Creating bound socket (for datagram input/output) on: " + localAddress);
        } catch (IOException e) {
            Log.e(TAG, "error create DatagramIO", e);
        }
    }

    synchronized public void stop() {
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }

    @Override
    public void run() {
        Log.v(TAG, "Entering blocking receiving loop, listening for UDP datagrams on: " + localAddress);

        while (true) {

            try {
                byte[] buf = new byte[maxDatagramBytes];
                DatagramPacket datagram = new DatagramPacket(buf, buf.length);

                socket.receive(datagram);

                Log.v(TAG,
                        "UDP datagram received from: "
                                + datagram.getAddress().getHostAddress()
                                + ":" + datagram.getPort()
                                + " on: " + localAddress
                );


//                router.received(datagramProcessor.read(localAddress.getAddress(), datagram));

            } catch (SocketException ex) {
                Log.w(TAG, "Socket closed");
                break;
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
        try {
            if (!socket.isClosed()) {
                Log.i(TAG, "Closing unicast socket");
                socket.close();
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    synchronized public void send(DatagramPacket datagram) {
        Log.i(TAG, "Sending message from address: " + localAddress);

        try {
            socket.send(datagram);
        } catch (SocketException ex) {
            Log.w(TAG, "Socket closed, aborting datagram send to: " + datagram.getAddress());
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            Log.w(TAG, "Exception sending datagram to: " + datagram.getAddress() + ": " + ex, ex);
        }
    }
}
