package net

import gui.SocketStatus
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import roll
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetSocketAddress

class NetworkSimulator
{
    private var datagramSocket:DatagramSocket = DatagramSocket();
    private val delayQueueBuffer:DelayQueueBuffer<DatagramPacket> = DelayQueueBuffer()
    private var receiver:Receiver? = null
    private val forwarder:Forwarder
    private val dropPacketProbabilityCalculator:Thread

    init
    {
        forwarder = Forwarder()
        dropPacketProbabilityCalculator = DropPacketProbabilityCalculatorDaemon()
        dropPacketProbabilityCalculator.start()
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
    var packetDropFunction:Int = 0
    var noise:Double = 0.0

    val socketStatus = SimpleObjectProperty<SocketStatus>(SocketStatus.BIND_ERR)
    val packetsDelivered = SimpleIntegerProperty(0)
    val packetsDropped = SimpleIntegerProperty(0)
    val bytesInFlight = SimpleIntegerProperty(0)
    val throughput = SimpleDoubleProperty(0.0)
    val dropPacketProbability = SimpleDoubleProperty(0.0)

    private fun rollToDropPacket():Boolean
    {
        return roll(dropPacketProbability.value)
    }

    /**
     * gets packets from the socket, and sticks them into the delayQueueBuffer
     * so they can be delayed by a random latency, and jitter
     */
    private inner class Receiver(val socket:DatagramSocket):Extractor<SocketBuffer,DatagramPacket>(SocketBuffer(socket))
    {
        override fun onExtract(extractedItem:DatagramPacket)
        {
            // update bytes in flight
            bytesInFlight.value += extractedItem.length

            // read packet from the socket, and place in delay buffer
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
            // update bytes in flight
            bytesInFlight.value -= extractedItem.length

            // read from the delay buffer, and forward packet to destination
            val srcSockAddr = InetSocketAddress(extractedItem.address,extractedItem.port)
            val dstSockAddr = routingTable?.get(srcSockAddr)
            if(dstSockAddr != null && !rollToDropPacket())
            {
                extractedItem.address = dstSockAddr.address
                extractedItem.port = dstSockAddr.port
                datagramSocket.send(extractedItem)
            }
        }
    }

    /**
     * periodically calculates the probability that the network should use to
     * drop packets
     */
    private inner class DropPacketProbabilityCalculatorDaemon:Thread()
    {
        init
        {
            isDaemon = true
        }
        override fun run()
        {
            while(true)
            {
                // calculate
                val x:Double = bytesInFlight.value.toDouble()/capacity.toDouble()
                var y:Double
                y = Math.pow(x,packetDropFunction.toDouble())
                y = Math.max(noise,y)

                dropPacketProbability.value = y
                println("dropPacketProbability.value: ${dropPacketProbability.value}")

                // sleep so we are not pinning a core
                Thread.sleep(100)
            }
        }
    }
}
