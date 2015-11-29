package ProtocolPeer;

import java.nio.ByteBuffer;

/**
 * Created by Manuel on 2015-11-11.
 */
public class Packet {

    int length;
    int max_size;
    boolean initiated;
    ByteBuffer bytebuffer;
    byte[] packetReady;

    Packet(int size)
    {
        if(size < 0)
        {
            System.err.println("invalid Packet Size");
        }

        max_size = size;
        bytebuffer = ByteBuffer.allocate(max_size);

        length = 0;
        initiated = true;
    }

    public Packet()
    {
        initiated = false;
    }

    /**
     * Will add the data into the packet
     * @param data byte array of data
     * @param size sie of data
     * @return true if data was added, false if packet is full
     */
    public boolean addData(byte[] data, int size)
    {
        if(initiated)
        {
            if (length + size > max_size) {
                return false;
            }

            bytebuffer.put(data, 0, size);
            length += size;

            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean addSequence(int seq_num)
    {
        if(initiated)
        {
            if (length + ConstantDefinitions.SEQ_BYTESIZE > max_size) {
                return false;
            }

            ByteBuffer seq_num_ba = ByteBuffer.allocate(ConstantDefinitions.SEQ_BYTESIZE);
            bytebuffer.put(seq_num_ba.putInt(seq_num).array(), 0, ConstantDefinitions.SEQ_BYTESIZE);
            length += ConstantDefinitions.SEQ_BYTESIZE;
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean addAcknowledgment(int ack_num)
    {
        if(initiated)
        {
            if (length + ConstantDefinitions.ACK_BYTESIZE > max_size)
            {
                return false;
            }

            ByteBuffer ack_num_ba = ByteBuffer.allocate(ConstantDefinitions.ACK_BYTESIZE);
            bytebuffer.put(ack_num_ba.putInt(ack_num).array(), 0, ConstantDefinitions.ACK_BYTESIZE);
            length += ConstantDefinitions.ACK_BYTESIZE;

            return true;
        }
        else
        {
            return false;
        }
    }

    public byte[] getPacket()
    {
        if(initiated)
        {
            packetReady = new byte[length];
            bytebuffer.clear();
            bytebuffer.get(packetReady, 0, length);

            return packetReady;
        }
        else
        {
            return null;
        }
    }


}
