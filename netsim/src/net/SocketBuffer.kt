package net;

import java.net.DatagramPacket
import java.net.DatagramSocket

class SocketBuffer(val socket:DatagramSocket):Buffer<DatagramPacket>
{
    override fun put(element:DatagramPacket)
    {
        socket.send(element)
    }

    override fun get():DatagramPacket
    {
        val datagram = DatagramPacket(ByteArray(Protocol.MAX_UDP_PACKET_LEN),Protocol.MAX_UDP_PACKET_LEN)
        socket.receive(datagram)
        return datagram
    }
}
