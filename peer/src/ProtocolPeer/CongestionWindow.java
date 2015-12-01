package ProtocolPeer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Created by Manuel on 2015-11-26.
 * Designed by : Eric Tsang and Manuel Gonzales
 * Implemented by: Manuel Gonzales
 *
 * This class resembles a connection window it will add packets to its queue and will time
 * out if an ack is not received on time it will also handle increasing rtts and will shorten
 * window sizes if packets are dropped
 *
 */
public class CongestionWindow {

        public interface Observer {
            void onPacketDropped(CoolDatagram coolDatagram);
        }


        private DelayQueue<DelayedDatagram> delayQueue;
        private long averageRTT;
        private int currentSize;

        protected Observer observer;
        private Map<Integer, DelayedDatagram> queueMap;
        Semaphore semaphoreQueue;
        boolean slowStart;
        Logger connectionLog;

        private boolean stopSending;
        private Object sendingNotify;
        private int sem_count;

        private Object cutHalf;

    /**
     * starts the queues and semaphores and more which are used to keep track of the window it also
     * starts the timeout thread as well as the congestion window resize thread
     * @param observer
     * @param connectionLog
     * @throws InterruptedException
     */
        CongestionWindow(Observer observer, Logger connectionLog) throws InterruptedException
        {
            this.observer = observer;
            this.connectionLog = connectionLog;

            semaphoreQueue = new Semaphore(ConstantDefinitions.INITIAL_WINDOW_SIZE, true);
            currentSize = ConstantDefinitions.INITIAL_WINDOW_SIZE;
            slowStart = true;
            averageRTT = ConstantDefinitions.INITIAL_TIMEOUT;
            delayQueue = new DelayQueue<>();
            queueMap = new HashMap<>();

            sendingNotify = new Object();
            cutHalf = new Object();

            stopSending = false;
            sem_count = 0;

            new Thread(this::timeoutThread).start();
            new Thread(this::cutSemaphoreThread).start();
        }

    /**
     * will get notified when the window needs to be cut due to dropped packets
     * it will not cut it off if the window is getting closer to the MAX SIZE for a
     * DATAGRAM
     */
    private void cutSemaphoreThread()
        {
            while(true)
            {
                synchronized (cutHalf) {
                    try {
                        cutHalf.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                if((currentSize / 2) > ConstantDefinitions.MAX_PACKETSIZE)
                {
                    currentSize = currentSize / 2;
                    try {
                        semaphoreQueue.acquire(currentSize);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    averageRTT += ConstantDefinitions.RTT_DROPPED;
                }

                //connectionLog.addLog("CURRENT SEMAPHORE SIZE: " + currentSize);
                stopSending = false;

                synchronized (sendingNotify)
                {
                    sendingNotify.notify();
                }
            }
        }

    /**
     * This thread will take timeout packets out of the delayed queue and will retransmit them as well
     * as notifying the resizing window thread
     */
    private void timeoutThread()
        {
            while(true)
            {
                try {
                    CoolDatagram coolDatagram = delayQueue.take().coolDatagram;
                    semaphoreQueue.release(coolDatagram.getLength());

                    observer.onPacketDropped(coolDatagram);

                    synchronized (cutHalf)
                    {
                        cutHalf.notify();
                    }

                    stopSending = true;

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }

    /**
     * whenever a packet is acked it is remoed from the delayed queue so that it is not
     * time out. it icnreases iwndow size and rtt based on how the transmission went
     * @param sequenceNumber
     * @return
     */
        protected boolean ackPacket(int sequenceNumber)
        {
            connectionLog.addLog("HAS THE ACK? : " + sequenceNumber +  queueMap.containsKey(sequenceNumber));
            DelayedDatagram ackedDatagram = queueMap.get(sequenceNumber);

            if(ackedDatagram != null) {
                if(delayQueue.contains(ackedDatagram))
                {
                    delayQueue.remove(ackedDatagram);
                }
                queueMap.remove(sequenceNumber, ackedDatagram);

                if (slowStart) {
                    semaphoreQueue.release(ackedDatagram.coolDatagram.getLength() * 2);
                    currentSize += ackedDatagram.coolDatagram.getLength();
                } else {
                    long estimatedRtt = System.currentTimeMillis() - ackedDatagram.enqueueTime + ConstantDefinitions.RTT_OVERHEAD;
                    averageRTT += (estimatedRtt - averageRTT) * 0.1;

                    double size = Math.floor(((double) ackedDatagram.coolDatagram.getLength()) / ((double) currentSize / (double) ackedDatagram.coolDatagram.getLength()));

                    semaphoreQueue.release((int) size + ackedDatagram.coolDatagram.getLength());
                    currentSize += (int) size;
                }

                return true;
            }
            else
            {
                return false;
            }

        }

    /**
     * when a datagram is gonna be sent it gets added to the delayed queue in order to
     * keep track of the timeout
     * @param coolDatagram
     * @return
     */
        protected boolean putPacket(CoolDatagram coolDatagram)
        {
            if(stopSending)
            {
                synchronized (sendingNotify)
                {
                    try {
                        sendingNotify.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            if(coolDatagram != null) {
                synchronized (semaphoreQueue) {
                    try {
                        //connectionLog.addLog("CURRENT SEMAPHORE PERMITS : " + semaphoreQueue.availablePermits());
                        semaphoreQueue.acquire(coolDatagram.getLength());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
                DelayedDatagram delayedDatagram = new DelayedDatagram(coolDatagram);

                //connectionLog.addLog("SEQ NUM: "  + coolDatagram.getSeq());
                //connectionLog.addLog("LENGTH: "  + coolDatagram.getLength());
                queueMap.put(coolDatagram.getSeq() + coolDatagram.getLength(), delayedDatagram);
                delayQueue.add(delayedDatagram);

                return true;
            }
            else
            {
                return false;
            }

        }

    /**
     * Only used for retransmitting packets it will put them back into the delayed queue.
     * @param coolDatagram
     * @return
     */
        protected boolean putTimeoutPacket(CoolDatagram coolDatagram)
        {
            if(coolDatagram != null) {

                DelayedDatagram delayedDatagram = new DelayedDatagram(coolDatagram);

                //connectionLog.addLog("SEQ NUM: "  + coolDatagram.getSeq());
                //connectionLog.addLog("LENGTH: "  + coolDatagram.getLength());
                queueMap.put(coolDatagram.getSeq() + coolDatagram.getLength(), delayedDatagram);
                delayQueue.add(delayedDatagram);

                return true;
            }
            else
            {
                return false;
            }

        }

    /**
     * wrapper class used in order to add delay(timeout) to the datagrams
     */
        protected class DelayedDatagram implements Delayed
        {

            protected long enqueueTime;
            protected CoolDatagram coolDatagram;

            DelayedDatagram(CoolDatagram coolDatagram)
            {
               this.coolDatagram = coolDatagram;
               enqueueTime = System.currentTimeMillis();
                //connectionLog.addLog("AVERAGE RTT: " + averageRTT);
            }

            @Override
            public long getDelay(TimeUnit unit) {
                return unit.convert(averageRTT - ( System.currentTimeMillis() -  enqueueTime), TimeUnit.MILLISECONDS);
            }

            @Override
            public int compareTo(Delayed o)
            {
                long difference;
                difference = getDelay(TimeUnit.MILLISECONDS) - o.getDelay(TimeUnit.MILLISECONDS);
                difference = Math.max(difference, -1);
                difference = Math.min(difference, 1);
                return  (int) difference;
            }
        }
}
