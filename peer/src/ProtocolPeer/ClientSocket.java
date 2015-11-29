package ProtocolPeer;

import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Manuel on 2015-11-11.
 */
public class ClientSocket {

    private DatagramSocket mainSocket;
    private DatagramPacket udpPacket;
    protected LinkedBlockingQueue<DatagramPacket> sendingQueue;
    private Map<SocketAddress, Connection> connectionList;

    private final Observer observer;
    private boolean running;
    private boolean listening;

    public interface Observer
    {
        void onAccept(Connection newConnection);
    }

    public ClientSocket(int port, Observer observerv)
    {

        observer = observerv;
        connectionList = new HashMap<>();

        udpPacket = new DatagramPacket(new byte[ConstantDefinitions.MAX_PACKETSIZE], ConstantDefinitions.MAX_PACKETSIZE);
        sendingQueue = new LinkedBlockingQueue<>();

        try {
            mainSocket = new DatagramSocket(port);
        } catch (SocketException e) {
            e.printStackTrace();
            return;
        }

        running = true;
        listening = false;

        new Thread(this::startSending).start();
        new Thread(this::startReceiving).start();
    }

    public Connection connect(InetSocketAddress address) throws ConnectException
    {
        Connection newConnection = new Connection(address, this);
        connectionList.put(address, newConnection);

        if(newConnection.connect())
        {
             System.out.println("CONNECTION SUCCESSFUL !!");
             return newConnection;
        }
        else
        {
             throw new ConnectException("Unreachable host");
        }
    }

    private void startSending()
    {
        while(running)
        {
            try {

                mainSocket.send(sendingQueue.take());
                System.out.println("SENT DATAGRAM");

            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void startReceiving()
    {
        while(running)
        {
            try {
                mainSocket.receive(udpPacket);
                System.out.println("RECEIVED DATAGRAM");

            } catch (IOException e) {
                e.printStackTrace();
            }

            if(connectionList.containsKey(udpPacket.getSocketAddress()))
            {
                Connection existingConnection = connectionList.get(udpPacket.getSocketAddress());
                existingConnection.enqueue(udpPacket);
            }
            else
            {
                if(listening) {
                    System.out.println("NEW CONNECTION");
                    final Connection newConnection = new Connection(udpPacket.getSocketAddress(), this);
                    connectionList.put(udpPacket.getSocketAddress(), newConnection);
                    newConnection.enqueue(udpPacket);
                    new Thread(() -> observer.onAccept(newConnection)).start();
                }
            }
        }
    }

    protected void disconnect(Connection connection)
    {
        connectionList.remove(connection.getSocketAddress(), connection);
    }

    public void setListening(boolean listen)
    {
        listening = listen;
    }

    public void closeClient()
    {
        running = false;
        sendingQueue.clear();
        mainSocket.close();
    }

}
