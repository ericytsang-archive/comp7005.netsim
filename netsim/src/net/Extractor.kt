package net

/**
 * wraps [Buffer] objects, and continuously extracts elements from them. the
 * callback [onExtract] is invoked every time a new element is extracted from
 * [extractee].
 *
 * @param extractee Buffer object to extract data from.
 */
internal abstract class Extractor<Extractee:Buffer<Element>,Element>(val extractee:Extractee)
{
    /**
     * thread that extracts data from [extractee].
     */
    private val worker:WorkerThread = WorkerThread()

    init
    {
        worker.isDaemon = true
        worker.start()
    }

    /**
     * invoked whenever new data is extracted from [extractee].
     *
     * @param extractedItem the element that is extracted from the [extractee].
     */
    abstract fun onExtract(extractedItem:Element)

    /**
     * continuously gets data from [extractee], and passes the extracted data to
     * [onExtract].
     */
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
