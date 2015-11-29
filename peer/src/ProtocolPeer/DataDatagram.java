package ProtocolPeer;

import java.nio.ByteBuffer;

/**
 * Created by Manuel on 2015-11-25.
 */
public class DataDatagram extends CoolDatagram {

    ByteBuffer dataPayload;
    ByteBuffer actualPayload;

    DataDatagram(CoolDatagram coolDatagram)
    {
        super(coolDatagram.getUdpPacket());
        NUM_SEQ = coolDatagram.getPayload().getInt();
        dataPayload = coolDatagram.getPayload();
        dataPayload.clear();
        actualPayload = ByteBuffer.allocate(length - ConstantDefinitions.DATA_OVERHEAD);
        actualPayload.put(dataPayload.array(), ConstantDefinitions.DATA_OVERHEAD, length - ConstantDefinitions.DATA_OVERHEAD);
    }

    public int getSeq()
    {
        return NUM_SEQ;
    }

    public ByteBuffer getData()
    {
        return actualPayload;
    }
}
