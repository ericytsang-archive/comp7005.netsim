package ProtocolPeer;

import java.nio.ByteBuffer;

/**
 * Created by Manuel on 2015-11-25.
 */
public class FinDatagram extends CoolDatagram {

    FinDatagram(CoolDatagram coolDatagram)
    {
        super(coolDatagram.getUdpPacket());
        length = coolDatagram.getPayload().limit();
        NUM_SEQ = coolDatagram.getPayload().getInt();
    }

    public int getSeq()
    {
        return NUM_SEQ;
    }
}