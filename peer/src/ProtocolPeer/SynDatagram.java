package ProtocolPeer;

/**
 * Created by Manuel on 2015-11-25.
 * Class used for SynDatagrams will get the SEQ from coolDatagrams.
 */
public class SynDatagram extends CoolDatagram {

    SynDatagram(CoolDatagram coolDatagram)
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