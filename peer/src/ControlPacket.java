import com.sun.corba.se.impl.orbutil.closure.Constant;

import java.nio.ByteBuffer;
import java.util.Optional;

/**
 * Created by Manuel on 2015-11-11.
 */
public class ControlPacket extends Packet {

    ControlPacket(PacketType packet_type)
    {
        switch (packet_type) {
            case SYN:
                max_size = ConstantDefinitions.SYN_SIZE;
                bytebuffer = ByteBuffer.allocate(max_size);
                bytebuffer.put(ConstantDefinitions.SYN);
                break;
            case SYN_ACK:
                max_size = ConstantDefinitions.SYNACK_SIZE;
                bytebuffer = ByteBuffer.allocate(max_size);
                bytebuffer.put(ConstantDefinitions.SYNACK);
                break;
            case ACK:
                max_size = ConstantDefinitions.ACK_SIZE;
                bytebuffer = ByteBuffer.allocate(max_size);
                bytebuffer.put(ConstantDefinitions.ACK);
                break;
            case FIN:
                max_size = ConstantDefinitions.FIN_SIZE;
                bytebuffer = ByteBuffer.allocate(max_size);
                bytebuffer.put(ConstantDefinitions.FIN);
                break;
            default:
                System.err.println("unknown Packet Type");

                break;
        }

        length = 1;
        initiated = true;
    }
}

