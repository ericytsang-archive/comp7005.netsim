import java.nio.ByteBuffer;

/**
 * Created by Manuel on 2015-11-25.
 */
public class AckDatagram extends CoolDatagram {

    int NUM_ACK;

    AckDatagram(ByteBuffer payload)
    {
        NUM_ACK = payload.getInt();
    }

    public int getAck()
    {
        return NUM_ACK;
    }
}