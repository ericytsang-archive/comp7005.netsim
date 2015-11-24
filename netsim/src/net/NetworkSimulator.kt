package net

import java.net.DatagramPacket
import java.net.DatagramSocket
import java.util.Optional

class NetworkSimulator
{
    private var datagramSocket:DatagramSocket? = null;
    private val delayQueueBuffer:DelayQueueBuffer<DatagramPacket> = DelayQueueBuffer()

    var port:Int = 0

        set(value)
        {
            if(datagramSocket == null)
            {
                datagramSocket = DatagramSocket(value)
            }
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

    var capacity:Long = 0
    var packetDropFunction:Float = 0F
    var noise:Float = 0F
}
