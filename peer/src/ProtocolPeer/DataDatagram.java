package ProtocolPeer;

import java.nio.ByteBuffer;

/**
 * Created by Manuel on 2015-11-25.
 */
public class DataDatagram extends CoolDatagram {

    int NUM_SEQ;
    ByteBuffer dataPayload;


    DataDatagram(ByteBuffer payload)
    {
        NUM_SEQ = payload.getInt();
        dataPayload = payload;
    }

    public int getSeq()
    {
        return NUM_SEQ;
    }

    public ByteBuffer getData()
    {
        return dataPayload;
    }
}
