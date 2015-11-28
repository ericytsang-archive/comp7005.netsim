package ProtocolTest;

import ProtocolPeer.*;

/**
 * Created by Manuel on 2015-11-28.
 */
public class ProtocolTest{

    public static void main(String args[])
    {
        ClientSocket clientSocket = new ClientSocket(7005, new ClientObserver());
        System.out.println("CLIENT CREATED");
    }




}
