package net

import java.util.concurrent.DelayQueue
import java.util.concurrent.Delayed
import java.util.concurrent.TimeUnit

/**
 * wraps a [DelayQueue]. [Element]s are put into the [delayQueue] with [put].
 * [Element]s are removed from the [delayQueue] with [get].
 * [Element]s may only be removed with [get] once it has spent [baseDelay]
 * milliseconds plus a random fraction of [delayNoise] milliseconds in the
 * [delayQueue].
 */
internal class DelayQueueBuffer<Element>:Buffer<Element>
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
        val sendTimeMillis = System.currentTimeMillis()+delay

        // enqueue the element
        delayQueue.put(DelayedElement(sendTimeMillis,element))
    }

    override fun get():Element
    {
        return delayQueue.take().element
    }

    /**
     * @param dequeueTimeMillis time when element may be removed from the
     *   collection in milliseconds
     * @param element element that is being inserted into the collection
     */
    private inner class DelayedElement(private val dequeueTimeMillis:Long,val element:Element):Delayed
    {
        override fun getDelay(unit:TimeUnit):Long
        {
            val delay = dequeueTimeMillis-System.currentTimeMillis()
            return unit.convert(delay,TimeUnit.MILLISECONDS)
        }

        override fun compareTo(other:Delayed):Int
        {
            var difference:Long
            difference = getDelay(TimeUnit.MILLISECONDS)-other.getDelay(TimeUnit.MILLISECONDS)
            difference = Math.max(difference,-1)
            difference = Math.min(difference,1)
            return difference.toInt()
        }
    }
}
