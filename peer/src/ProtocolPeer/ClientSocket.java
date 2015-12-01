package ProtocolPeer;

import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Manuel on 2015-11-11.
 * Designed by : Eric Tsang and Manuel Gonzales
 * Implemented by: Manuel Gonzales
 *
 * Starting class that will open a UDP socket and will start sending and receiving from it,
 * it will lead with connections based on their addresses.
 */
public class ClientSocket {

    private DatagramSocket mainSocket;
    private DatagramPacket udpPacket;
    protected LinkedBlockingQueue<DatagramPacket> sendingQueue;
    private Map<SocketAddress, Connection> connectionList;

    private final Observer observer;
    private boolean running;
    private boolean listening;

    /**
     * csuer will be notified when there is a new connection and needs to use this interface
     */
    public interface Observer
    {
        void onAccept(Connection newConnection);
    }

    /**
     * open sockets and starts threads
     * @param port
     * @param observerv
     */
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

        new File("./logs").mkdir();

    }

    /**
     * will start the handshake with the desired address
     * @param address
     * @return
     * @throws ConnectException
     */
    public Connection connect(InetSocketAddress address) throws ConnectException
    {
        Connection newConnection = new Connection(address, this);
        connectionList.put(address, newConnection);

        if(newConnection.connect())
        {
             System.out.println("Connection Successful");
             return newConnection;
        }
        else
        {
             throw new ConnectException("Unreachable host");
        }
    }

    /**
     * continous thread that will read from a queue that all connections use to
     * send the packets through the socket
     */
    private void startSending()
    {
        while(running)
        {
            try {

                mainSocket.send(sendingQueue.take());

            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * receives all the datagrams for every connection and will multiplex it based on the
     * address they come from
     */
    private void startReceiving()
    {
        while(running)
        {
            try {
                mainSocket.receive(udpPacket);

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
                    System.out.println("New Connection");
                    final Connection newConnection = new Connection(udpPacket.getSocketAddress(), this);
                    connectionList.put(udpPacket.getSocketAddress(), newConnection);
                    newConnection.enqueue(udpPacket);
                    new Thread(() -> observer.onAccept(newConnection)).start();
                }
            }
        }
    }

    /**
     * when a connection is closed it is removed from the list of connetions
     * @param connection
     */
    protected void disconnect(Connection connection)
    {
        connectionList.remove(connection.getSocketAddress(), connection);
    }

    /**
     * sets the prot to listneing if the user wants to recieve connections
     * @param listen
     */
    public void setListening(boolean listen)
    {
        listening = listen;
    }

    /**
     * closes the port and all threads
     */
    public void closeClient()
    {
        running = false;
        sendingQueue.clear();
        mainSocket.close();
    }

}
