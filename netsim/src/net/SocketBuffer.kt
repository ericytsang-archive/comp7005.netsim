package net;

import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.SocketException

class SocketBuffer(val socket:DatagramSocket):Buffer<DatagramPacket>
{
    override fun put(element:DatagramPacket)
    {
        socket.send(element)
    }

    override fun get():DatagramPacket
    {
        while(true)
        {
            try
            {
                val datagram = DatagramPacket(ByteArray(Protocol.MAX_UDP_PACKET_LEN),Protocol.MAX_UDP_PACKET_LEN)
                socket.receive(datagram)
                return datagram
            }
            catch(ex:SocketException)
            {
                // thrown when socket is closed. this is normal
                /* continue */
            }
        }
    }
}
