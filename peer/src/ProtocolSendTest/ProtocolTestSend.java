package ProtocolSendTest;

import ProtocolPeer.*;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;

/**
 * Created by Manuel on 2015-11-28.
 *
 * Test Send
 */
public class ProtocolTestSend
{
    public static void main(String args[]) throws IOException {
        ClientSocket clientSocket = new ClientSocket(7035, new ClientObserver());
        System.out.println("CLIENT SEND CREATED");

        InetSocketAddress address = new InetSocketAddress("192.168.0.12", 9600);
        Connection connection;
        try {
            connection = clientSocket.connect(address);
        } catch (ConnectException e) {
            throw e;
        }

        DataOutputStream oustream = new DataOutputStream(connection.getOutputStream());
        byte[] dataToSend = new byte[] {0,1,2,3,4,5,6,7,8,9};


        for(int testing_fin = 0; testing_fin < 150000; testing_fin++)
        {
            oustream.write(dataToSend);
            System.out.println("TESITNG NUMBER: " + testing_fin);

            try {
                Thread.sleep(0,1);
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