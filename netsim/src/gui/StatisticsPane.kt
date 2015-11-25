package gui

import javafx.geometry.HPos
import javafx.geometry.Insets
import javafx.scene.control.Label
import javafx.scene.control.ProgressBar
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.GridPane
import javafx.scene.layout.Priority

internal class StatisticsPane:GridPane()
{
    private val COL_INDEX_LABEL:Int = 0
    private val COL_INDEX_CONTENT:Int = 1

    private val PACKETS_DELIVERED_LABEL:String = "Packets Delivered:"
    private val PACKETS_DELIVERED_DEFAULT:Double = 0.0

    private val SOCKET_STATUS_LABEL:String = "Socket Status:"
    private val SOCKET_STATUS_DEFAULT:SocketStatus = SocketStatus.BIND_ERR

    private val PACKETS_DROPPED_LABEL:String = "Packets Dropped:"
    private val PACKETS_DROPPED_DEFAULT:Double = 0.0

    private val THROUGHPUT_LABEL:String = "Throughput:"
    private val THROUGHPUT_DEFAULT:Double = 0.20

    private val BYTES_IN_FLIGHT_LABEL:String = "Bytes In Flight:"
    private val BYTES_IN_FLIGHT_DEFAULT:Double = 0.50

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
        val column1 = ColumnConstraints()
        column1.halignment = HPos.RIGHT
        columnConstraints.add(0,column1)

        val column2 = ColumnConstraints()
        column2.isFillWidth = true
        column2.hgrow = Priority.ALWAYS
        columnConstraints.add(1,column2)
    }

    private fun addChildNodes()
    {
        val socketStatusDisplay = TextDisplay()
        socketStatusDisplay.label.text = SOCKET_STATUS_LABEL
        socketStatusDisplay.value.text = SOCKET_STATUS_DEFAULT.friendlyString
        socketStatusDisplay.value.styleClass.add(SOCKET_STATUS_DEFAULT.css)

        val packetsDeliveredDisplay = TextDisplay()
        packetsDeliveredDisplay.label.text = PACKETS_DELIVERED_LABEL
        packetsDeliveredDisplay.value.text = PACKETS_DELIVERED_DEFAULT.toString()

        val packetsDroppedDisplay = TextDisplay()
        packetsDroppedDisplay.label.text = PACKETS_DROPPED_LABEL
        packetsDroppedDisplay.value.text = PACKETS_DROPPED_DEFAULT.toString()

        val bytesInFlightDisplay = ProgressDisplay()
        bytesInFlightDisplay.label.text = BYTES_IN_FLIGHT_LABEL
        bytesInFlightDisplay.progressBar.progressProperty().value = BYTES_IN_FLIGHT_DEFAULT

        val throughputDisplay = ProgressDisplay()
        throughputDisplay.label.text = THROUGHPUT_LABEL
        throughputDisplay.progressBar.progressProperty().value = THROUGHPUT_DEFAULT

        add(socketStatusDisplay)
        add(packetsDeliveredDisplay)
        add(packetsDroppedDisplay)
        add(bytesInFlightDisplay)
        add(throughputDisplay)
    }

    private fun add(newTextDisplay:TextDisplay)
    {
        add(newTextDisplay.label,COL_INDEX_LABEL,nextRow)
        add(newTextDisplay.value,COL_INDEX_CONTENT,nextRow)
        nextRow++
    }

    private fun add(newProgressDisplay:ProgressDisplay)
    {
        add(newProgressDisplay.label,COL_INDEX_LABEL,nextRow)
        add(newProgressDisplay.progressBar,COL_INDEX_CONTENT,nextRow)
        nextRow++
    }
}

enum class SocketStatus(val friendlyString:String,val css:String)
{
    OPEN("Running",CSS.CONFIRM_TEXT),
    BIND_ERR("Binding Error",CSS.WARNING_TEXT)
}

private class TextDisplay()
{
    val label:Label = Label()
    val value:Label = Label()
}

private class ProgressDisplay()
{
    val label:Label = Label()
    val progressBar:ProgressBar = ProgressBar()
}
