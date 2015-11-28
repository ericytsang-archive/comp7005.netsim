package ProtocolPeer;

import java.net.DatagramPacket;
import java.nio.ByteBuffer;

/**
 * Created by Manuel on 2015-11-25.
 */
public class CoolDatagram{

    private ByteBuffer packetPayload;
    private PacketTypesProtocol packetType;
    private int length;
    private DatagramPacket udpPacket;

    CoolDatagram(DatagramPacket packet)
    {
        udpPacket = packet;
        length = packet.getData().length;
        packetPayload = ByteBuffer.allocate(length);
        packetPayload.put(packet.getData());
    }

    CoolDatagram()
    {

    }

    protected ByteBuffer getPayload()
    {
        return packetPayload;
    }

    public PacketTypesProtocol getPacketType()
    {
        switch(packetPayload.get())
        {
            case ConstantDefinitions.SYN:
                packetType = PacketTypesProtocol.SYN;
                break;
            case ConstantDefinitions.ACK:
                packetType = PacketTypesProtocol.ACK;
                break;
            case ConstantDefinitions.DATA:
                packetType = PacketTypesProtocol.DATA;
                break;
            case ConstantDefinitions.SYNACK:
                packetType = PacketTypesProtocol.SYN_ACK;
                break;
            case ConstantDefinitions.FIN:
                packetType = PacketTypesProtocol.FIN;
                break;
            default:
                System.err.println("unknown Packet Type");
                packetType = packetType.UNKNOWN;
                break;
        }

        return packetType;
    }


    protected int getSeq()
    {
        return 0;
    }

    protected int getLength()
    {
        return length;
    }

    protected DatagramPacket getUdpPacket()
    {
        return udpPacket;
    }

}
