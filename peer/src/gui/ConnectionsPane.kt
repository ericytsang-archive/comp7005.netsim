package gui

import javafx.application.Platform
import javafx.beans.InvalidationListener
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.GridPane
import net.NetUtils
import java.net.InetSocketAddress

class ConnectionsPane:GridPane()
{
    init
    {

    }

    private class ConnectionEntry
    {
        private val ADDR_PROMPT:String = "IP Address"
        private val PORT_PROMPT:String = "Port Number"

        val remoteAddr = TextField()
        val remotePort = IntTextField(true)
        val connect = Button()
        val sendMessage = TextField()
        val receiveMessage = Label()

        var validationThread:ValidationThread = ValidationThread()

        init
        {
            // set on action code
            remoteAddr.textProperty().addListener(InvalidationListener{validateAndNotify()})
            remotePort.textProperty().addListener(InvalidationListener{validateAndNotify()})

            // add prompt text to text fields
            remoteAddr.promptText = ADDR_PROMPT
            remotePort.promptText = PORT_PROMPT

            // configure mins and maxs of port text fields
            remotePort.min = NetUtils.MIN_PORT
            remotePort.max = NetUtils.MAX_PORT
        }

        private fun validateAndNotify()
        {
            synchronized(this,{
                // interrupt the previous thread so it will abort its callback operation
                validationThread.interrupt = true

                // begin the validation on the validation thread
                validationThread = ValidationThread()
                validationThread.start()
            })
        }

        interface Observer
        {
            fun onDataChanged(observee:ConnectionEntry, sockAddr1: InetSocketAddress?);
        }

        private inner class ValidationThread:Thread()
        {
            var interrupt:Boolean = false

            override fun run()
            {
                try
                {

                    // if inputs are blank, input is invalid; throw
                    if(remoteAddr.text.isBlank() || addr2.text.isBlank())
                        throw IllegalArgumentException()

                    // try to resolve addresses
                    val sockAddr1 = InetSocketAddress(addr1.text, port1.text.toInt())
                    val sockAddr2 = InetSocketAddress(addr2.text, port2.text.toInt())

                    // if addresses were not resolved, input is invalid; throw
                    if (sockAddr1.isUnresolved || sockAddr2.isUnresolved)
                        throw IllegalArgumentException()

                    // set instance variable sock addresses
                    Platform.runLater({
                        if(!interrupt)
                        {
                            stateObserver?.onDataChanged(this@ForwardingEntry,sockAddr1,sockAddr2)
                        }
                    })
                }
                catch(ex:Exception)
                {
                    when
                    {

                    // IllegalArgumentException: thrown by createUnresolved if the
                    // port parameter is outside the range of valid port values,
                    // or if the hostname parameter is null.
                    //
                    // NumberFormatException: thrown by Int.parse..thrown then text
                    // field is empty
                        ex is IllegalArgumentException || ex is NumberFormatException ->
                        {
                            Platform.runLater({
                                if(!interrupt)
                                {
                                    stateObserver?.onDataChanged(this@ForwardingEntry,null,null)
                                }
                            })
                        }

                    // propagate unhandled exceptions
                        else -> throw ex
                    }
                }
            }
        }
    }
}
