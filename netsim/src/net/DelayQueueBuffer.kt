package net

import java.util.concurrent.DelayQueue
import java.util.concurrent.Delayed
import java.util.concurrent.TimeUnit

class DelayQueueBuffer<Element>:Buffer<Element>
{
    /**
     * the delay queue that elements are actually put into and extracted from.
     */
    private val delayQueue:DelayQueue<DelayedElement> = DelayQueue()

    /**
     * base value that elements are delayed by.
     */
    public var baseDelay:Long = 0

    /**
     * random fraction of this value is added to the delay of an element
     * enqueued into the buffer.
     */
    public var delayNoise:Long = 0

    override fun put(element:Element)
    {
        // generate a random delay associated with the element
        val delay = baseDelay+(delayNoise*Math.random()).toLong()

        // enqueue the element
        delayQueue.put(DelayedElement(delay,element))
    }

    override fun get():Element
    {
        return delayQueue.take().element
    }

    /**
     * @param delay minimum time element must remain in collection in
     *   milliseconds before it may be removed
     * @param element element that is being inserted into the collection
     */
    private inner class DelayedElement(private val delay:Long,val element:Element):Delayed
    {
        override fun getDelay(unit:TimeUnit):Long
        {
            return TimeUnit.MILLISECONDS.convert(delay,unit)
        }

        override fun compareTo(o:Delayed):Int
        {
            var difference:Long
            difference = getDelay(TimeUnit.MILLISECONDS)-o.getDelay(TimeUnit.MILLISECONDS)
            difference = Math.max(difference,Integer.MIN_VALUE.toLong())
            difference = Math.min(difference,Integer.MAX_VALUE.toLong())
            return (difference).toInt()
        }
    }
}