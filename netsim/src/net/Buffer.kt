package net;

/**
 * Created by Eric Tsang on 11/7/2015.
 */
public interface Buffer<Element>
{
    fun put(element:Element);
    fun get():Element;
}
