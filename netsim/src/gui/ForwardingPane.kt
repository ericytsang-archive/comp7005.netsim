package gui

import javafx.beans.InvalidationListener
import javafx.beans.value.ChangeListener
import javafx.geometry.HPos
import javafx.geometry.Insets
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.*
import java.net.InetSocketAddress

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

    private var nextRow:Int = 0

    init
    {
        configureLayout()
        addChildNodes()
    }

    private fun configureLayout()
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
    }

    private fun addChildNodes()
    {
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
        nextRow++
    }
}

// todo: do disss
private class ForwardingEntry()
{
    val ADDR_PROMPT:String = "IP Address"
    val PORT_PROMPT:String = "Port Number"

    val addr1:TextField = TextField()
    val port1:NumberTextField = NumberTextField()
    val addr2:TextField = TextField()
    val port2:NumberTextField = NumberTextField()

    var sockAddr1:InetSocketAddress? = null
        private set(value) {sockAddr1 = value}
    var sockAddr2:InetSocketAddress? = null
        private set(value) {sockAddr2 = value}

    var stateObserver:ForwardingEntry.Observer? = null

    init
    {
        // set on action code
        addr1.textProperty().addListener(InvalidationListener { validateAndNotifyIfStateChanged() })
        port1.textProperty().addListener(InvalidationListener { validateAndNotifyIfStateChanged() })
        addr2.textProperty().addListener(InvalidationListener { validateAndNotifyIfStateChanged() })
        port2.textProperty().addListener(InvalidationListener { validateAndNotifyIfStateChanged() })

        // add prompt text to text fields
        addr1.promptText = ADDR_PROMPT
        port1.promptText = PORT_PROMPT
        addr2.promptText = ADDR_PROMPT
        port2.promptText = PORT_PROMPT
    }

    private fun validateAndNotifyIfStateChanged()
    {
        System.out.println("${addr1.text}:${port1.text}<->${addr2.text}:${port2.text}");
    }

    private fun validate():Boolean
    {
        return true
    }

    private interface Observer
    {
        fun onDataChanged();
    }
}
