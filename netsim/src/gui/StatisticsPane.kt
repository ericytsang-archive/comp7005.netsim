package gui

import javafx.application.Platform
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

    private val SOCKET_STATUS_LABEL:String = "Socket Status:"
    private val SOCKET_STATUS_DEFAULT:SocketStatus = SocketStatus.BIND_ERR

    private val PACKETS_DELIVERED_LABEL:String = "Packets Delivered:"
    private val PACKETS_DELIVERED_DEFAULT:Int = 0

    private val PACKETS_DROPPED_LABEL:String = "Packets Dropped:"
    private val PACKETS_DROPPED_DEFAULT:Int = 0

    private val BYTES_IN_FLIGHT_LABEL:String = "Bytes In Flight:"
    private val BYTES_IN_FLIGHT_DEFAULT:Int = 0

    private val NETWORK_USAGE_LABEL:String = "Network Usage:"
    private val NETWORK_USAGE_DEFAULT:Double = 0.0

    private val PACKET_DROP_RATE_LABEL:String = "Packet Drop Rate:"
    private val PACKET_DROP_RATE_DEFAULT:Double = 0.0

    private var nextRow:Int = 0

    private val socketStatusDisplay = TextDisplay()
    private val packetsDeliveredDisplay = TextDisplay()
    private val packetsDroppedDisplay = TextDisplay()
    private val bytesInFlightDisplay = TextDisplay()
    private val networkUsageDisplay = ProgressDisplay()
    private val packetDropRateDisplay = ProgressDisplay()

    var socketStatus:SocketStatus = SOCKET_STATUS_DEFAULT

        set(value)
        {
            socketStatusDisplay.value.text = value.friendlyString
            socketStatusDisplay.value.styleClass.remove(field.css)
            socketStatusDisplay.value.styleClass.add(value.css)
            field = value
        }

    var packetsDelivered:Int = PACKETS_DELIVERED_DEFAULT

        set(value)
        {
            Platform.runLater {
                packetsDeliveredDisplay.value.text = value.toString()
            }
            field = value
        }

    var packetsDropped:Int = PACKETS_DROPPED_DEFAULT

        set(value)
        {
            Platform.runLater {
                packetsDroppedDisplay.value.text = value.toString()
            }
            field = value
        }

    var bytesInFlight:Int = BYTES_IN_FLIGHT_DEFAULT

        set(value)
        {
            Platform.runLater {
                bytesInFlightDisplay.value.text = value.toString()
            }
            field = value
        }

    var networkUsage:Double = NETWORK_USAGE_DEFAULT

        set(value)
        {
            Platform.runLater {
                networkUsageDisplay.progressBar.progressProperty().value = value
            }
            field = value
        }

    var packetDropRate:Double = PACKET_DROP_RATE_DEFAULT

        set(value)
        {
            Platform.runLater {
                packetDropRateDisplay.progressBar.progressProperty().value = value
            }
            field = value
        }

    init
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

        // configure & add child nodes
        socketStatusDisplay.label.text = SOCKET_STATUS_LABEL
        packetsDeliveredDisplay.label.text = PACKETS_DELIVERED_LABEL
        packetsDroppedDisplay.label.text = PACKETS_DROPPED_LABEL
        bytesInFlightDisplay.label.text = BYTES_IN_FLIGHT_LABEL
        networkUsageDisplay.label.text = NETWORK_USAGE_LABEL
        packetDropRateDisplay.label.text = PACKET_DROP_RATE_LABEL

        add(socketStatusDisplay)
        add(packetsDeliveredDisplay)
        add(packetsDroppedDisplay)
        add(bytesInFlightDisplay)
        add(networkUsageDisplay)
        add(packetDropRateDisplay)
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
