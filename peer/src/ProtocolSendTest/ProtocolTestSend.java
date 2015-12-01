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

        InetSocketAddress address = new InetSocketAddress("127.0.0.1", 7006);
        Connection connection;
        try {
            connection = clientSocket.connect(address);
        } catch (ConnectException e) {
            throw e;
        }

        DataOutputStream oustream = new DataOutputStream(connection.getOutputStream());


        for(int testing_fin = 0; testing_fin < 100000; testing_fin++)
        {
            oustream.write(5);
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