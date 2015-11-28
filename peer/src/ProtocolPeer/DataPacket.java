package ProtocolPeer;

/**
 * Created by Manuel on 2015-11-11.
 */
public class DataPacket extends Packet
{
    DataPacket(int size)
    {
        super(size);
        bytebuffer.put(ConstantDefinitions.DATA);
        length = 1;
    }
}
