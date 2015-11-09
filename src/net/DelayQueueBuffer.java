package net;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * Created by Eric Tsang on 11/7/2015.
 */
public class DelayQueueBuffer<Element> implements Buffer<Element>
{
    /**
     * the delay queue that elements are actually put into and extracted from.
     */
    private DelayQueue<DelayedElement> delayQueue;

    /**
     * base value that elements are delayed by.
     */
    private long baseDelay;

    /**
     * random fraction of this value is added to the delay of an element
     * enqueued into the buffer.
     */
    private long delayNoise;

    public long getBaseDelay()
    {
        return baseDelay;
    }

    public void setBaseDelay(long baseDelay)
    {
        this.baseDelay = baseDelay;
    }

    public long getDelayNoise()
    {
        return delayNoise;
    }

    public void setDelayNoise(long delayNoise)
    {
        this.delayNoise = delayNoise;
    }

    @Override
    public void put(Element element)
    {
        // generate a random delay associated with the element
        long delay = baseDelay + (long) (delayNoise*Math.random());

        // enqueue the element
        delayQueue.put(new DelayedElement(delay,element));
    }

    @Override
    public Element get() throws InterruptedException
    {
        return delayQueue.take().getElement();
    }

    private class DelayedElement implements Delayed
    {
        private final Element element;

        /**
         * delay of the element in milliseconds
         */
        private long delay;

        public DelayedElement(long delay,Element element)
        {
            this.delay = delay;
            this.element = element;
        }

        @Override
        public long getDelay(TimeUnit unit)
        {
            return TimeUnit.MILLISECONDS.convert(delay,unit);
        }

        @Override
        public int compareTo(Delayed o)
        {
            long difference;
            difference = getDelay(TimeUnit.MILLISECONDS) - o.getDelay(TimeUnit.MILLISECONDS);
            difference = Math.max(difference,-1);
            difference = Math.min(difference,1);
            return (int) (difference);
        }

        public Element getElement()
        {
            return element;
        }
    }
}
