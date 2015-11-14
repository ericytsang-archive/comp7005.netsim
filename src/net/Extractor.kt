import net.Buffer

abstract class Extractor<Element>(val extractee:Buffer<Element>)
{
    private val worker:WorkerThread = WorkerThread()

    init
    {
        worker.start()
    }

    abstract fun onExtract(extractedItem:Element):Unit

    private inner class WorkerThread:Thread()
    {
        override fun run():Unit
        {
            while (true)
            {
                onExtract(extractee.get())
            }
        }
    }
}
