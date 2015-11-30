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
            System.out.println("Received THREAD this:");
            throw e;
        }

        DataOutputStream oustream = new DataOutputStream(connection.getOutputStream());
        int testing_fin = 0;

        byte[] data = new byte[]{5,5,5,5,5,5,5,5,5,5,5,5,5};
        while(testing_fin < 100000)
        {
            oustream.write(data);
            testing_fin++;
            System.out.println("TESTING FIN COUNT: " + testing_fin);

           /* try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/

        }
        System.out.println("TESTING FIN COUNT DIED: " + testing_fin);

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