package net;

/**
 * Created by Eric Tsang on 11/7/2015.
 */
interface Buffer<Element>
{
    fun put(element:Element);
    fun get():Element;
}
