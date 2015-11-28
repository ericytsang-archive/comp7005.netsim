import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Created by Manuel on 2015-11-26.
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

        CongestionWindow(Observer observer) throws InterruptedException
        {
            this.observer = observer;

            semaphoreQueue = new Semaphore(ConstantDefinitions.INITIAL_WINDOW_SIZE, true);
            currentSize = ConstantDefinitions.INITIAL_WINDOW_SIZE;
            slowStart = true;
            averageRTT = ConstantDefinitions.INITIAL_TIMEOUT;
            delayQueue = new DelayQueue<>();
            queueMap = new HashMap<>();

            new Thread(this::timeoutThread).start();
        }

        private void timeoutThread()
        {
            while(true)
            {
                try {
                    observer.onPacketDropped(delayQueue.take().coolDatagram);

                    if(currentSize > ConstantDefinitions.MAX_PACKETSIZE)
                    {
                        semaphoreQueue.acquire(currentSize / 2);
                        averageRTT += ConstantDefinitions.RTT_DROPPED;
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }

        protected boolean ackPacket(int sequenceNumber)
        {
            DelayedDatagram ackedDatagram = queueMap.get(sequenceNumber);

            if(ackedDatagram != null) {
                delayQueue.remove(ackedDatagram);
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

        protected synchronized boolean putPacket(CoolDatagram coolDatagram)
        {
            if(coolDatagram != null) {
                try {
                    semaphoreQueue.acquire(coolDatagram.getLength());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                DelayedDatagram delayedDatagram = new DelayedDatagram(coolDatagram);
                queueMap.put(coolDatagram.getSeq() + coolDatagram.getLength(), delayedDatagram);
                delayQueue.add(delayedDatagram);

                return true;
            }
            else
            {
                return false;
            }

        }

        protected class DelayedDatagram implements Delayed
        {

            protected long enqueueTime;
            protected CoolDatagram coolDatagram;

            DelayedDatagram(CoolDatagram coolDatagram)
            {
               this.coolDatagram = coolDatagram;
               enqueueTime = System.currentTimeMillis();
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
