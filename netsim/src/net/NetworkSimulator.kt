package net

import gui.SocketStatus
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import roll
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetSocketAddress

/**
 * emulates a network.
 *
 * configure its settings: [routingTable], [latency], [jitter], [port], [noise],
 * [packetDropFunction] and [capacity].
 *
 * get statistical, and status information: [socketStatus], [packetsDelivered],
 * [packetsDropped], [bytesInFlight] and [packetDropRate].
 *
 * this [NetworkSimulator] will read [DatagramPacket]s from the [datagramSocket]
 * listening on port [port]. received [DatagramPacket]s will remain in the
 * [delayQueueBuffer] for [latency] milliseconds, and an additional random
 * fraction of [jitter] milliseconds before being forwarded to the mapped
 * [InetSocketAddress] determined by the [routingTable] which maps source
 * [InetSocketAddress]es to destination [InetSocketAddress]es.
 */
class NetworkSimulator
{
    /**
     * updated by this [NetworkSimulator].
     *
     * status of this [NetworkSimulator]'s [datagramSocket].
     */
    val socketStatus = SimpleObjectProperty<SocketStatus>(SocketStatus.BIND_ERR)

    /**
     * updated by this [NetworkSimulator].
     *
     * total number of [DatagramPacket]s forwarded by this [NetworkSimulator].
     */
    val packetsDelivered = SimpleIntegerProperty(0)

    /**
     * updated by this [NetworkSimulator].
     *
     * total number of [DatagramPacket]s dropped by this [NetworkSimulator].
     */
    val packetsDropped = SimpleIntegerProperty(0)

    /**
     * updated by this [NetworkSimulator].
     *
     * number of bytes of data that have been received by this
     * [NetworkSimulator], but have yet to be forwarded.
     */
    val bytesInFlight = SimpleIntegerProperty(0)

    /**
     * updated by this [NetworkSimulator].
     *
     * probability that received [DatagramPacket]s are dropped by this
     * [NetworkSimulator]; dropped [DatagramPacket]s per received
     * [DatagramPacket].
     */
    val packetDropRate = SimpleDoubleProperty(0.0)

    /**
     * maps [InetSocketAddress] with a single [InetSocketAddress]. used to
     * determine which [InetSocketAddress] to forward received [DatagramPacket]s
     * to based on its source [InetSocketAddress].
     */
    var routingTable:Map<InetSocketAddress,InetSocketAddress>? = null

    /**
     * the [port] determines which port on the machine the [NetworkSimulator] is
     * going to open the [datagramSocket] on to send and receive
     * [DatagramPacket]s.
     */
    var port:Int = 0

        /**
         * when [port] is set, the existing [datagramSocket] is closed, and a
         * new [datagramSocket] is open on the new [port].
         *
         * a new [Receiver] instance is created to extract data from the
         * [datagramSocket].
         */
        set(value)
        {
            synchronized(this,{
                // close the previous socket if it is open
                if (!datagramSocket.isClosed) datagramSocket.close()

                // open the new socket on the specified port & create a new
                // Receiver object to extract data from the port
                try
                {
                    datagramSocket = Receiver(DatagramSocket(value)).socket
                    socketStatus.value = SocketStatus.OPEN
                }
                catch(ex:Exception)
                {
                    socketStatus.value = SocketStatus.BIND_ERR
                }

                field = value
            })
        }

    /**
     * minimum amount of time in milliseconds that each packet must be in this
     * [NetworkSimulator] before getting forwarded to its destination.
     */
    var latency:Long

        set(value) { delayQueueBuffer.baseDelay = value }

        get() = delayQueueBuffer.baseDelay

    /**
     * maximum amount of time in milliseconds that each packet may be
     * additionally randomly delayed in this [NetworkSimulator] before getting
     * forwarded to its destination.
     */
    var jitter:Long

        set(value) { delayQueueBuffer.delayNoise = value }

        get() = delayQueueBuffer.delayNoise

    /**
     * maximum number of bytes that this [NetworkSimulator] can hold.
     */
    var capacity:Int = 0

    /**
     * the variable in the following formula:
     *
     * [packetDropRate] = networkUsage ^ [packetDropFunction]
     */
    var packetDropFunction:Int = 0

    /**
     * minimum value that [packetDropRate] can equal.
     */
    var noise:Double = 0.0

    /**
     * used by the [NetworkSimulator] to read and send [DatagramPacket]s from
     * and out.
     */
    private var datagramSocket:DatagramSocket = DatagramSocket();

    /**
     * [DatagramPacket] are inserted into this [delayQueueBuffer] once they have
     * been received by this [NetworkSimulator]. they will remain in the
     * [delayQueueBuffer] for some [latency] and [jitter]. they will then be
     * removed from the [delayQueueBuffer], and forwarded to its destination.
     */
    private val delayQueueBuffer:DelayQueueBuffer<DatagramPacket> = DelayQueueBuffer()

    init
    {
        Forwarder()
        DropPacketProbabilityCalculatorDaemon().start()
    }

    /**
     * has [packetDropRate] probability of returning true; false otherwise.
     */
    private fun rollToDropPacket():Boolean
    {
        // roll to generate result
        val result = roll(packetDropRate.value)

        // update statistics
        if(result)
        {
            packetsDropped.value++
        }
        else
        {
            packetsDelivered.value++
        }

        return result
    }

    /**
     * extracts [DatagramPacket]s from its [socket], and sticks them into the
     * [delayQueueBuffer] so they can be delayed by the [latency], and [jitter]
     * before getting forwarded.
     */
    private inner class Receiver(val socket:DatagramSocket):Extractor<SocketBuffer,DatagramPacket>(SocketBuffer(socket))
    {
        override fun onExtract(extractedItem:DatagramPacket)
        {
            if(!rollToDropPacket())
            {
                // update bytes in flight
                bytesInFlight.value += extractedItem.length

                // read packet from the socket, and place in delay buffer
                delayQueueBuffer.put(extractedItem)
            }
        }
    }

    /**
     * extracts [DatagramPacket]s from the [delayQueueBuffer], and forwards them
     * to the appropriate host.
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
            if(dstSockAddr != null)
            {
                extractedItem.address = dstSockAddr.address
                extractedItem.port = dstSockAddr.port
                datagramSocket.send(extractedItem)
            }
        }
    }

    /**
     * periodically calculates based on the current state of this
     * [NetworkSimulator] and updates [packetDropRate].
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
                val x:Double =
                    if(capacity != 0) bytesInFlight.value.toDouble()/capacity
                    else 1.0
                var y:Double
                y = Math.pow(x,packetDropFunction.toDouble())
                y = Math.max(noise,y)

                packetDropRate.value = y

                // sleep so we are not pinning a core
                Thread.sleep(10)
            }
        }
    }
}
