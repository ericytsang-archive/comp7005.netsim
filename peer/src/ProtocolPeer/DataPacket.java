package ProtocolPeer;

/**
 * Created by Manuel on 2015-11-11.
 *
 * Class used to separate data packet/datagrams from control packet/datagrams
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
