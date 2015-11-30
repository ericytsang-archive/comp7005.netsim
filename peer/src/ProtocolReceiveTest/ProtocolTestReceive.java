package ProtocolReceiveTest;

import ProtocolPeer.ClientSocket;
import ProtocolPeer.Connection;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by Manuel on 2015-11-28.
 */
public class ProtocolTestReceive {
    public static void main(String args[])
    {
        ClientSocket clientSocket = new ClientSocket(7006, new ClientObserver());
        System.out.println("CLIENT RECEIVE CREATED");
        clientSocket.setListening(true);



        while(true)
        {

        }
    }

    static class ClientObserver implements ClientSocket.Observer
    {
        @Override
        public void onAccept(Connection newConnection)
        {
            DataInputStream is = new DataInputStream(newConnection.getInputStream());

            while(true)
            {
                if(!newConnection.isActive())
                {
                    break;
                }

                try {
                    System.out.println(is.read());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
