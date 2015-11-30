package net

/**
 * contains constants associated with networking.
 */
object NetUtils
{
    /**
     * lowest possible port number.
     */
    val MIN_PORT:Int = 0x0001

    /**
     * highest port number that typically exists.
     */
    val MAX_PORT:Int = 0xFFFF

    /**
     * maximum length of a UDP datagram's payload in bytes.
     */
    val MAX_UDP_PACKET_LEN = 65507
}
