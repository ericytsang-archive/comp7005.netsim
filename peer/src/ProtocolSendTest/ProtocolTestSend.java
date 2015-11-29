package ProtocolSendTest;

import ProtocolPeer.*;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;

/**
 * Created by Manuel on 2015-11-28.
 */
public class ProtocolTestSend
{
    public static void main(String args[]) throws IOException {
        ClientSocket clientSocket = new ClientSocket(7035, new ClientObserver());
        System.out.println("CLIENT SEND CREATED");

        InetSocketAddress address = new InetSocketAddress("127.0.0.1", 7026);
        Connection connection;
        try {
            connection = clientSocket.connect(address);
        } catch (ConnectException e) {
            System.out.println("Received THREAD this:");
            throw e;
        }

        DataOutputStream oustream = new DataOutputStream(connection.getOutputStream());
        int testing_fin = 0;

        while(testing_fin < 10)
        {
            oustream.write(5);
            testing_fin++;

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        connection.disconnect();
    }

    static public class ClientObserver implements ClientSocket.Observer
    {
        @Override
        public void onAccept(Connection newConnection)
        {

        }
    }
}