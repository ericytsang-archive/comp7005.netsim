package net;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Optional;

/**
 * Created by Eric Tsang on 11/7/2015.
 */
public class NetworkSimulator
{
    private Optional<DatagramSocket> datagramSocketOptional;
    private DelayQueueBuffer<DatagramPacket> delayQueueBuffer;
    private long capacity;
    private float packetDropFunction;
    private float noise;

    public NetworkSimulator()
    {
        datagramSocketOptional = Optional.empty();
        delayQueueBuffer = new DelayQueueBuffer<>();
    }

    public void setPort(Optional<Integer> portOptional)
    {
        portOptional.ifPresent((port) ->
        {

        });
    }

    public void setCapacity(long capacity)
    {
        this.capacity = capacity;
    }

    public void setPacketDropFunction(float packetDropFunction)
    {
        this.packetDropFunction = packetDropFunction;
    }

    public void setNoise(float noise)
    {
        this.noise = noise;
    }

    public void setLatency(long latency)
    {
        delayQueueBuffer.setBaseDelay(latency);
    }

    public void setJitter(long jitter)
    {
        delayQueueBuffer.setDelayNoise(jitter);
    }
}
