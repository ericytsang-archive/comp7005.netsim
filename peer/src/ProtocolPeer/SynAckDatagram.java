package ProtocolPeer;

/**
 * Created by Manuel on 2015-11-25.
 * Class used for SynAckDatagrams will get the SEQ and ACK from coolDatagrams.
 */
public class SynAckDatagram extends CoolDatagram {

    int NUM_ACK;


    SynAckDatagram(CoolDatagram coolDatagram)
    {
        super(coolDatagram.getUdpPacket());
        length = coolDatagram.getPayload().limit();
        NUM_SEQ = coolDatagram.getPayload().getInt();
        NUM_ACK = coolDatagram.getPayload().getInt();
    }

    public int getSeq()
    {
        return NUM_SEQ;
    }

    public int getAck()
    {
        return NUM_ACK;
    }
}