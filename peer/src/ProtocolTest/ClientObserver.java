package ProtocolTest;

import ProtocolPeer.ClientSocket;
import ProtocolPeer.Connection;

/**
 * Created by Manuel on 2015-11-28.
 */
public class ClientObserver implements ClientSocket.Observer
{
    @Override
    public void onAccept(Connection newConnection)
    {

    }
}