package net;

/**
 * represents a data structure where elements may be [put] into it for
 * processing (if any), and later removed with [get] once processed.
 */
internal interface Buffer<Element>
{
    /**
     * places an [Element] into this [Buffer] for processing (if any).
     *
     * @param element the [Element] to place into this [Buffer].
     */
    fun put(element:Element);

    /**
     * blocks until the next [Element] is ready for removal, then removes and
     * returns the next [Element] from the buffer.
     *
     * @return an [Element] extracted from the buffer.
     */
    fun get():Element;
}
