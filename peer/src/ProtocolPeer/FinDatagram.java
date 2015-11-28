package ProtocolPeer;

import java.nio.ByteBuffer;

/**
 * Created by Manuel on 2015-11-25.
 */
public class FinDatagram extends CoolDatagram {

    int NUM_SEQ;
    int NUM_ACK;


    FinDatagram(ByteBuffer payload)
    {
        NUM_SEQ = payload.getInt();
        NUM_ACK = payload.getInt();
    }

    public int getSeq()
    {
        return NUM_SEQ;
    }
}