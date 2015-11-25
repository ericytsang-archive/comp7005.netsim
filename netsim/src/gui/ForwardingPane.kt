package gui

import javafx.beans.InvalidationListener
import javafx.geometry.Insets
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.*
import parse
import java.net.InetSocketAddress
import java.util.*

internal class ForwardingPane:GridPane()
{
    private val COL_INDEX_ADDR1:Int = 0
    private val COL_INDEX_COLON1:Int = 1
    private val COL_INDEX_PORT1:Int = 2
    private val COL_INDEX_BI_DIR_ARROW:Int = 3
    private val COL_INDEX_ADDR2:Int = 4
    private val COL_INDEX_COLON2:Int = 5
    private val COL_INDEX_PORT2:Int = 6

    private val COLON_LABEL_TEXT:String = ":";
    private val BI_DIR_ARROW_TEXT:String = "<->"

    val inetSockAddressPairs:MutableMap<InetSocketAddress,InetSocketAddress> = LinkedHashMap()

    private var nextRow:Int = 0

    private val inetSockAddresses:MutableMap<ForwardingEntry,MutableSet<InetSocketAddress>> = LinkedHashMap()
    private val forwardingEntries:MutableSet<ForwardingEntry> = LinkedHashSet()
    private val forwardingEntryObserver:ForwardingEntryObserver = ForwardingEntryObserver()

    init
    {
        // configure aesthetic properties
        padding = Insets(Dimens.KEYLINE_SMALL.toDouble())
        hgap = Dimens.KEYLINE_SMALL.toDouble()
        vgap = Dimens.KEYLINE_SMALL.toDouble()

        // configure grid constraints
        columnConstraints.add(ColumnConstraints())
        columnConstraints.add(ColumnConstraints())
        columnConstraints.add(ColumnConstraints())
        columnConstraints.add(ColumnConstraints())
        columnConstraints.add(ColumnConstraints())
        columnConstraints.add(ColumnConstraints())
        columnConstraints.add(ColumnConstraints())

        val lastColumn = ColumnConstraints()
        lastColumn.isFillWidth = true
        lastColumn.hgrow = Priority.ALWAYS
        columnConstraints.add(1,lastColumn)

        // add gui controls
        add(ForwardingEntry())
    }

    private fun add(forwardingEntry:ForwardingEntry)
    {
        add(forwardingEntry.addr1,COL_INDEX_ADDR1,nextRow)
        add(Label(COLON_LABEL_TEXT),COL_INDEX_COLON1,nextRow)
        add(forwardingEntry.port1,COL_INDEX_PORT1,nextRow)
        add(Label(BI_DIR_ARROW_TEXT),COL_INDEX_BI_DIR_ARROW,nextRow)
        add(forwardingEntry.addr2,COL_INDEX_ADDR2,nextRow)
        add(Label(COLON_LABEL_TEXT),COL_INDEX_COLON2,nextRow)
        add(forwardingEntry.port2,COL_INDEX_PORT2,nextRow)
        forwardingEntry.stateObserver = forwardingEntryObserver
        forwardingEntries.add(forwardingEntry)
        nextRow++
    }

    private fun removeAll()
    {
        children.clear()
        nextRow = 0
    }

    private inner class ForwardingEntryObserver:ForwardingEntry.Observer
    {
        override fun onDataChanged(observee:ForwardingEntry)
        {
            synchronized(this,
                {
                    // find all associated address entries previously related to
                    // this input, and remove them from the address map...later,
                    // we will add the addresses from the observee to the
                    // address map if it is ok to do so
                    inetSockAddresses.remove(observee)
                        ?.forEach{inetSockAddressPairs.remove(it)}

                    val sockAddr1 = observee.sockAddr1
                    val sockAddr2 = observee.sockAddr2

                    // when there are valid address inputs, and they don't
                    // conflict with other entries, unset [observee.error], and
                    // add the address entries to the address map
                    if(sockAddr1 != null && sockAddr2 != null
                        && !sockAddr1.equals(sockAddr2)
                        && !inetSockAddressPairs.containsKey(sockAddr1)
                        && !inetSockAddressPairs.containsKey(sockAddr2))
                    {
                        observee.error = false
                        inetSockAddressPairs.put(sockAddr1,sockAddr2)
                        inetSockAddressPairs.put(sockAddr2,sockAddr1)
                        inetSockAddresses.getOrPut(observee,{LinkedHashSet()})
                            .addAll(arrayOf(sockAddr1,sockAddr2))
                    }

                    // when inputs are invalid, set [observee.error]
                    else
                    {
                        observee.error = true
                    }

                    // if there are no text in any of the text fields, remove it
                    removeAll()
                    forwardingEntries.filter{it.addr1.length == 0
                        && it.port1.length == 0 && it.addr2.length == 0
                        && it.port2.length == 0}.forEach{forwardingEntries.remove(it)}
                    forwardingEntries.forEach{add(it)}
                    add(ForwardingEntry())
                })
        }
    }
}

private class ForwardingEntry()
{
    private val ADDR_PROMPT:String = "IP Address"
    private val PORT_PROMPT:String = "Port Number"

    val addr1:TextField = TextField()
    val port1:IntTextField = IntTextField(true)
    val addr2:TextField = TextField()
    val port2:IntTextField = IntTextField(true)

    var sockAddr1:InetSocketAddress? = null

        private set

    var sockAddr2:InetSocketAddress? = null

        private set

    var error:Boolean = false

        set(value)
        {
            if(field == value) return
            field = value
            if (field)
            {
                addr1.styleClass.add(CSS.WARNING_CONTROL)
                port1.styleClass.add(CSS.WARNING_CONTROL)
                addr2.styleClass.add(CSS.WARNING_CONTROL)
                port2.styleClass.add(CSS.WARNING_CONTROL)
            }
            else
            {
                addr1.styleClass.remove(CSS.WARNING_CONTROL)
                port1.styleClass.remove(CSS.WARNING_CONTROL)
                addr2.styleClass.remove(CSS.WARNING_CONTROL)
                port2.styleClass.remove(CSS.WARNING_CONTROL)
            }
        }

    var stateObserver:ForwardingEntry.Observer? = null

    init
    {
        // set on action code
        addr1.textProperty().addListener(InvalidationListener{validateAndNotify()})
        port1.textProperty().addListener(InvalidationListener{validateAndNotify()})
        addr2.textProperty().addListener(InvalidationListener{validateAndNotify()})
        port2.textProperty().addListener(InvalidationListener{validateAndNotify()})

        // add prompt text to text fields
        addr1.promptText = ADDR_PROMPT
        port1.promptText = PORT_PROMPT
        addr2.promptText = ADDR_PROMPT
        port2.promptText = PORT_PROMPT

        // configure mins and maxs of port text fields
        port1.min = NetUtils.MIN_PORT
        port1.max = NetUtils.MAX_PORT
        port2.min = NetUtils.MIN_PORT
        port2.max = NetUtils.MAX_PORT
    }

    private fun validateAndNotify()
    {
        try
        {
            sockAddr1 = null
            sockAddr2 = null
            if(addr1.text.isBlank() || addr2.text.isBlank()) throw IllegalArgumentException()
            sockAddr1 = InetSocketAddress.createUnresolved(addr1.text,Int.parse(port1.text))
            sockAddr2 = InetSocketAddress.createUnresolved(addr2.text,Int.parse(port2.text))
        }
        catch(ex:IllegalArgumentException)
        {
            // thrown by createUnresolved if the port parameter is outside the
            // range of valid port values, or if the hostname parameter is null.
        }
        catch(ex:NumberFormatException)
        {
            // thrown by Int.parse..this should never be thrown anyway lel
        }
        finally
        {
            stateObserver?.onDataChanged(this)
        }
    }

    interface Observer
    {
        fun onDataChanged(observee:ForwardingEntry);
    }
}
