package net;

/**
 * Created by Eric Tsang on 11/7/2015.
 */
public abstract class AsynchronousExtractor<Element>
{
    private final Buffer<Element> extractee;

    public AsynchronousExtractor(Buffer<Element> extractee)
    {
        this.extractee = extractee;
        WorkerThread worker = new WorkerThread();
        worker.start();
    }

    public abstract void onExtract(Element extractedItem);

    private class WorkerThread extends Thread
    {
        @Override
        public void run()
        {
            while(true)
            {
                try
                {
                    onExtract(extractee.get());
                }
                catch(InterruptedException e)
                {
                    break;
                }
            }
            throw new RuntimeException("extractor thread interrupted!");
        }
    }
}
