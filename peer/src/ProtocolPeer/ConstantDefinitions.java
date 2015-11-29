package ProtocolPeer;

/**
 * Created by Manuel on 2015-11-11.
 */
public class ConstantDefinitions {

    public static final byte SYN = (byte) 0x02;
    public static final byte SYNACK = (byte) 0x04;
    public static final byte ACK = (byte) 0x06;
    public static final byte FIN = (byte) 0x08;
    public static final byte DATA = (byte) 0x0A;

    public static final int SYN_SIZE = 5;
    public static final int SYNACK_SIZE = 9;
    public static final int ACK_SIZE = 5;
    public static final int FIN_SIZE = 5;
    public static final int DATA_OVERHEAD = 5;

    public static final int SEQ_BYTESIZE = 4;
    public static final int ACK_BYTESIZE = 4;
    public static final int MAX_PACKETSIZE = 8192;
    public static final int INITIAL_WINDOW_SIZE = 50000;
    public static final int RANDOM_FACTOR = 2;

    public static final int INITIAL_TIMEOUT = 10000;
    public static final long RTT_OVERHEAD = 50;
    public static final int RTT_DROPPED = 2;
}
