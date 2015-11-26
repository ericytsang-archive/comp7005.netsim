package net

abstract class Extractor<Extractee:Buffer<Element>,Element>(val extractee:Extractee)
{
    private val worker:WorkerThread = WorkerThread()

    init
    {
        worker.isDaemon = true
        worker.start()
    }

    abstract fun onExtract(extractedItem:Element)

    private inner class WorkerThread:Thread()
    {
        override fun run():Unit
        {
            while(true)
            {
                onExtract(extractee.get())
            }
        }
    }
}
