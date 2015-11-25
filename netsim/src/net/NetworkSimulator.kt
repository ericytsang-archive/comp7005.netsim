package net

import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetSocketAddress

class NetworkSimulator
{
    private var datagramSocket:DatagramSocket = DatagramSocket();
    private val delayQueueBuffer:DelayQueueBuffer<DatagramPacket> = DelayQueueBuffer()
    private var receiver:Receiver? = null
    private val forwarder:Forwarder

    init
    {
        forwarder = Forwarder()
    }

    var routingTable:Map<InetSocketAddress,InetSocketAddress>? = null

    var port:Int = 0

        /**
         * sets the port that the network simulator will send and receive
         * packets from.
         *
         * when the port is set, the previous socket (if any) is closed, and a
         * new socket is open on the new port, and a new thread is created to
         * read and forward data from the port.
         */
        set(value)
        {
            // close the previous socket if it is open
            if(!datagramSocket.isClosed) datagramSocket.close()

            // open the new socket on the specified port
            receiver = Receiver(DatagramSocket(value))
            datagramSocket = receiver!!.socket

            field = value
        }

    var latency:Long = 0

        set(value)
        {
            delayQueueBuffer.baseDelay = value
            field = value
        }

    var jitter:Long = 0

        set(value)
        {
            delayQueueBuffer.delayNoise = value
            field = value
        }

    var capacity:Int = 0
    var packetDropFunction:Double = 0.0
    var noise:Double = 0.0

    /**
     * gets packets from the socket, and sticks them into the delayQueueBuffer
     * so they can be delayed by a random latency, and jitter
     */
    private inner class Receiver(val socket:DatagramSocket):Extractor<SocketBuffer,DatagramPacket>(SocketBuffer(socket))
    {
        override fun onExtract(extractedItem:DatagramPacket)
        {
            delayQueueBuffer.put(extractedItem)
        }
    }

    /**
     * gets packets from the delayQueueBuffer, and forwards them to the
     * appropriate host
     */
    private inner class Forwarder():Extractor<DelayQueueBuffer<DatagramPacket>,DatagramPacket>(delayQueueBuffer)
    {
        override fun onExtract(extractedItem:DatagramPacket)
        {
            val srcSockAddr = InetSocketAddress(extractedItem.address,extractedItem.port)
            val dstSockAddr = routingTable?.get(srcSockAddr)
            if(dstSockAddr != null)
            {
                extractedItem.address = dstSockAddr.address
                extractedItem.port = dstSockAddr.port
                datagramSocket.send(extractedItem)
            }
        }
    }
}
