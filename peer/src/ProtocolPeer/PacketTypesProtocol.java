package ProtocolPeer;

import java.util.HashMap;
import java.util.Map;

public enum PacketTypesProtocol
{
    SYN(2),
    SYN_ACK(4),
    ACK(6),
    DATA(8),
    FIN(10),
    UNKNOWN(12);

    private int packetType;

    private static Map<Integer, PacketTypesProtocol> map = new HashMap<>();

    /**
     * adds every value to the map
     */
    static {
        for (PacketTypesProtocol TODEnum : PacketTypesProtocol.values()) {
            map.put(TODEnum.packetType, TODEnum);
        }
    }

    /**
     * add every value to the enum
     * @param TOD enum value
     */
    PacketTypesProtocol(final int TOD) { packetType = TOD; }

    /**
     *
     * @param PacketTypesProtocol integer value fo the enum
     * @return actual enum
     */
    public static PacketTypesProtocol valueOf(int PacketTypesProtocol) {
        return map.get(PacketTypesProtocol);
    }

    /**
     *
     * @return integer value of the enum
     */
    public int showByteValue()
    {
        return packetType;
    }
}