package gui

import javafx.application.Platform
import javafx.beans.InvalidationListener
import javafx.geometry.Insets
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.GridPane
import javafx.scene.layout.Priority
import net.NetUtils
import java.net.InetSocketAddress
import java.util.*
import kotlin.internal.getProgressionFinalElement

class ConnectionsPane:GridPane()
{
    private val COL_INDEX_ADDR:Int = 0
    private val COL_INDEX_COLON:Int = 1
    private val COL_INDEX_PORT:Int = 2
    private val COL_INDEX_CONNECT_TOGGLE:Int = 3
    private val COL_INDEX_BI_DIR_ARROW:Int = 4
    private val COL_INDEX_SEND_MESSAGE:Int = 5
    private val COL_INDEX_RECEIVE_MESSAGE:Int = 6
    private val COL_INDEX_SEND_TOGGLE:Int = 7

    private val COLON_LABEL_TEXT:String = ":";
    private val BI_DIR_ARROW_TEXT:String = "<->"

    private var nextRow:Int = 0

    private val observer:ConnectionEntryObserver = ConnectionEntryObserver()

    private val connectionEntries:MutableSet<ConnectionEntry> = LinkedHashSet()

    init
    {
        // configure aesthetic properties
        padding = Insets(Dimens.KEYLINE_SMALL.toDouble())
        hgap = Dimens.KEYLINE_SMALL.toDouble()
        vgap = Dimens.KEYLINE_SMALL.toDouble()

        // configure grid constraints
        columnConstraints.add(COL_INDEX_ADDR,ColumnConstraints())
        columnConstraints.add(COL_INDEX_COLON,ColumnConstraints())
        columnConstraints.add(COL_INDEX_PORT,ColumnConstraints())
        columnConstraints.add(COL_INDEX_CONNECT_TOGGLE,ColumnConstraints())
        columnConstraints.add(COL_INDEX_BI_DIR_ARROW,ColumnConstraints())
        columnConstraints.add(COL_INDEX_SEND_MESSAGE,ColumnConstraints())
        columnConstraints.add(COL_INDEX_RECEIVE_MESSAGE,ColumnConstraints())
        columnConstraints.add(COL_INDEX_SEND_TOGGLE,ColumnConstraints())

        // add gui controls
        add(ConnectionEntry())
    }

    private fun add(connectionEntry:ConnectionEntry)
    {
        add(connectionEntry.remoteAddr,COL_INDEX_ADDR,nextRow)
        add(Label(COLON_LABEL_TEXT),COL_INDEX_COLON,nextRow)
        add(connectionEntry.remotePort,COL_INDEX_PORT,nextRow)
        add(connectionEntry.connectToggle,COL_INDEX_CONNECT_TOGGLE,nextRow)
        add(Label(BI_DIR_ARROW_TEXT),COL_INDEX_BI_DIR_ARROW,nextRow)
        add(connectionEntry.receiveMessage,COL_INDEX_RECEIVE_MESSAGE,nextRow)
        add(connectionEntry.sendMessage,COL_INDEX_SEND_MESSAGE,nextRow)
        add(connectionEntry.sendToggle,COL_INDEX_SEND_TOGGLE,nextRow)
        connectionEntries.add(connectionEntry)
        connectionEntry.observer = observer
        nextRow++
    }

    private fun removeAll()
    {
        children.clear()
        nextRow = 0
    }

    private inner class ConnectionEntryObserver:ConnectionEntry.Observer
    {
        override fun onDisconnect(observee:ConnectionEntry,sockAddr:InetSocketAddress)
        {
            // configure observee
            observee.connected = false
        }

        override fun onConnect(observee:ConnectionEntry,sockAddr:InetSocketAddress)
        {
            // configure observee
            observee.connected = true
        }

        override fun onDataChanged(observee:ConnectionEntry,sockAddr:InetSocketAddress?)
        {
            // configure observee
            observee.error = sockAddr == null

            // if there are no text in any of the text fields, remove it
            removeAll()
            connectionEntries.filter({it.remoteAddr.text.isEmpty()
                && it.remotePort.text.isEmpty()
                && it.sendMessage.text.isEmpty()})
                .forEach{connectionEntries.remove(it)}
            connectionEntries.forEach {add(it)}
            add(ConnectionEntry())
        }
    }

    private class ConnectionEntry
    {
        private val ADDR_PROMPT:String = "IP Address"
        private val PORT_PROMPT:String = "Port Number"
        private val CONNECT_TOGGLE_CONNECT:String = "Connect"
        private val CONNECT_TOGGLE_DISCONNECT:String = "Disconnect"
        private val SEND_TOGGLE_TEXT:String = "Send"
        private val SEND_MESSAGE_TEXT:String = "Message"

        private var validationThread:ValidationThread = ValidationThread()

        var error:Boolean = false

            set(value)
            {
                if (field == value) return
                field = value
                if (field)
                {
                    remoteAddr.styleClass.add(CSS.WARNING_CONTROL)
                    remotePort.styleClass.add(CSS.WARNING_CONTROL)
                }
                else
                {
                    remoteAddr.styleClass.remove(CSS.WARNING_CONTROL)
                    remotePort.styleClass.remove(CSS.WARNING_CONTROL)
                }
            }

        var connected:Boolean = true

            set(value)
            {
                if (field == value) return
                field = value
                if (field)
                {
                    remoteAddr.styleClass.add(CSS.NON_EDITABLE_CONTROL)
                    remoteAddr.isEditable = false
                    remotePort.styleClass.add(CSS.NON_EDITABLE_CONTROL)
                    remotePort.isEditable = false
                    sendMessage.styleClass.remove(CSS.NON_EDITABLE_CONTROL)
                    sendMessage.isEditable = true
                }
                else
                {
                    remoteAddr.styleClass.remove(CSS.NON_EDITABLE_CONTROL)
                    remoteAddr.isEditable = true
                    remotePort.styleClass.remove(CSS.NON_EDITABLE_CONTROL)
                    remotePort.isEditable = true
                    sendMessage.styleClass.add(CSS.NON_EDITABLE_CONTROL)
                    sendMessage.isEditable = false
                }
            }

        var observer:Observer? = null
        var sockAddr:InetSocketAddress? = null

        val remoteAddr = TextField()
        val remotePort = IntTextField(true)
        val connectToggle = Button()
        val sendMessage = TextField()
        val receiveMessage = Label()
        val sendToggle = CheckBox()

        init
        {
            // reassign instance variables to run their setters
            error = true
            connected = false

            // set on action code
            remoteAddr.textProperty().addListener(InvalidationListener{validateAndNotify()})
            remotePort.textProperty().addListener(InvalidationListener{validateAndNotify()})
            connectToggle.setOnAction(
                {
                    val sockAddr = sockAddr

                    if (sockAddr != null && connectToggle.text.equals(CONNECT_TOGGLE_CONNECT))
                    {
                        observer?.onConnect(this,sockAddr)
                        connectToggle.text = CONNECT_TOGGLE_DISCONNECT
                    }
                    else if (sockAddr != null && connectToggle.text.equals(CONNECT_TOGGLE_DISCONNECT))
                    {
                        observer?.onDisconnect(this,sockAddr)
                        connectToggle.text = CONNECT_TOGGLE_CONNECT
                    }
                })

            // add prompt text to text fields
            remoteAddr.promptText = ADDR_PROMPT
            remotePort.promptText = PORT_PROMPT
            connectToggle.text = CONNECT_TOGGLE_CONNECT
            sendToggle.text = SEND_TOGGLE_TEXT
            sendMessage.promptText = SEND_MESSAGE_TEXT

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
            fun onDataChanged(observee:ConnectionEntry, sockAddr:InetSocketAddress?);
            fun onConnect(observee:ConnectionEntry, sockAddr:InetSocketAddress);
            fun onDisconnect(observee:ConnectionEntry, sockAddr:InetSocketAddress);
        }

        private inner class ValidationThread:Thread()
        {
            var interrupt:Boolean = false

            override fun run()
            {
                try
                {

                    // if inputs are blank, input is invalid; throw
                    if(remoteAddr.text.isBlank())
                        throw IllegalArgumentException()

                    // try to resolve addresses
                    val sockAddr = InetSocketAddress(remoteAddr.text, remotePort.text.toInt())

                    // if addresses were not resolved, input is invalid; throw
                    if (sockAddr.isUnresolved)
                        throw IllegalArgumentException()

                    // set instance variable sock addresses
                    this@ConnectionEntry.sockAddr = sockAddr
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
                            sockAddr = null
                        }

                    // propagate unhandled exceptions
                        else -> throw ex
                    }
                }
                finally
                {
                    Platform.runLater({
                        if(!interrupt)
                        {
                            observer?.onDataChanged(this@ConnectionEntry,sockAddr)
                        }
                    })
                }
            }
        }
    }
}
