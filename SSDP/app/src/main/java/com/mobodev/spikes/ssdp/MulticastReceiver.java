package com.mobodev.spikes.ssdp;

import static com.mobodev.spikes.ssdp.Constants.IPV4_UPNP_MULTICAST_GROUP;
import static com.mobodev.spikes.ssdp.Constants.UPNP_MULTICAST_PORT;

import android.util.Log;

import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.SocketException;

public class MulticastReceiver implements Runnable {
    private static final String TAG = "MulticastReceiver";
    private final Router router;
    private final DatagramProcessor datagramProcessor;
    private int maxDatagramBytes = 640;
    protected InetSocketAddress multicastAddress;
    protected MulticastSocket socket;

    public MulticastReceiver(Router router, DatagramProcessor datagramProcessor) {
        this.router = router;
        this.datagramProcessor = datagramProcessor;

        try {

            Log.i(TAG, "Creating wildcard socket (for receiving multicast datagrams) on port: " + UPNP_MULTICAST_PORT);
            multicastAddress = new InetSocketAddress(IPV4_UPNP_MULTICAST_GROUP, UPNP_MULTICAST_PORT);

            socket = new MulticastSocket(UPNP_MULTICAST_PORT);
            socket.setReuseAddress(true);
            socket.setReceiveBufferSize(32768); // Keep a backlog of incoming datagrams if we are not fast enough

            Log.i(TAG, "Joining multicast group: " + multicastAddress);
            socket.joinGroup(multicastAddress.getAddress());

        } catch (Exception ex) {
            Log.e(TAG, "error create MulticastReceiver", ex);
        }
    }

    synchronized public void stop() {
        if (socket != null && !socket.isClosed()) {
            try {
                Log.i(TAG, "Leaving multicast group");
                socket.leaveGroup(multicastAddress.getAddress());
                // Well this doesn't work and I have no idea why I get "java.net.SocketException: Can't assign requested address"
            } catch (Exception ex) {
                Log.w(TAG, "Could not leave multicast group: " + ex);
            }
            // So... just close it and ignore the log messages
            socket.close();
        }
    }

    @Override
    public void run() {
        Log.i(TAG, "Entering blocking receiving loop, listening for UDP datagrams on: " + socket.getLocalAddress());
        while (true) {

            try {
                byte[] buf = new byte[maxDatagramBytes];
                DatagramPacket datagram = new DatagramPacket(buf, buf.length);

                socket.receive(datagram);

                Log.i(TAG,
                        "UDP datagram received from: " + datagram.getAddress().getHostAddress()
                                + ":" + datagram.getPort());

                router.received(datagramProcessor.read(datagram));

            } catch (SocketException ex) {
                Log.w(TAG, "Socket closed");
                break;
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
        try {
            if (!socket.isClosed()) {
                Log.i(TAG, "Closing multicast socket");
                socket.close();
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
