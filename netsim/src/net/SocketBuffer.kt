package net;

import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.SocketException

/**
 * wraps a [DatagramSocket] with the [Buffer] interface
 */
class SocketBuffer(val socket:DatagramSocket):Buffer<DatagramPacket>
{
    /**
     * sends the [element] out the [socket].
     */
    override fun put(element:DatagramPacket)
    {
        socket.send(element)
    }

    /**
     * reads a [DatagramPacket] from the [socket]
     */
    override fun get():DatagramPacket
    {
        while(true)
        {
            val datagram = DatagramPacket(ByteArray(NetUtils.MAX_UDP_PACKET_LEN),NetUtils.MAX_UDP_PACKET_LEN)
            socket.receive(datagram)
            return datagram
        }
    }
}
