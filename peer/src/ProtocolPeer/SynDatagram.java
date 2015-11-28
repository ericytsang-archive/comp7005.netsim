package ProtocolPeer;

import java.nio.ByteBuffer;

/**
 * Created by Manuel on 2015-11-25.
 */
public class SynDatagram extends CoolDatagram {

    int NUM_SEQ;


    SynDatagram(ByteBuffer payload)
    {
        NUM_SEQ = payload.getInt();
    }

    public int getSeq()
    {
        return NUM_SEQ;
    }
}