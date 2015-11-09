package net;

/**
 * Created by Eric Tsang on 11/7/2015.
 */
public interface Buffer<Element>
{
    void put(Element element);
    Element get() throws InterruptedException;
}
