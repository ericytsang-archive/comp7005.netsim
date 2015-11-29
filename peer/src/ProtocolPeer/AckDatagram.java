package ProtocolPeer;

import java.nio.ByteBuffer;

/**
 * Created by Manuel on 2015-11-25.
 */
public class AckDatagram extends CoolDatagram {


    private int NUM_ACK;

    AckDatagram(CoolDatagram coolDatagram)
    {
        super(coolDatagram.getUdpPacket());
        NUM_ACK = coolDatagram.getPayload().getInt();
    }

    public int getAck()
    {
        return NUM_ACK;
    }
}