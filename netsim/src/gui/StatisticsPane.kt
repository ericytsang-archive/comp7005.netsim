package gui

import javafx.application.Platform
import javafx.geometry.HPos
import javafx.geometry.Insets
import javafx.scene.chart.PieChart
import javafx.scene.control.Label
import javafx.scene.control.ProgressBar
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.GridPane
import javafx.scene.layout.Priority
import net.SocketStatus

internal class StatisticsPane:GridPane()
{
    private val COL_INDEX_LABEL:Int = 0
    private val COL_INDEX_CONTENT:Int = 1
    private val COL_INDEX_NET_USAGE_CHART:Int = 2

    private val SOCKET_STATUS_LABEL:String = "Socket Status:"
    private val PACKETS_DELIVERED_LABEL:String = "Packets Delivered:"
    private val PACKETS_DROPPED_LABEL:String = "Packets Dropped:"
    private val BYTES_IN_FLIGHT_LABEL:String = "Bytes In Flight:"
    private val NETWORK_USAGE_LABEL:String = "Network Usage:"
    private val PACKET_DROP_RATE_LABEL:String = "Packet Drop Rate:"

    private var nextRow:Int = 0

    private val socketStatusDisplay = TextDisplay()
    private val packetsDeliveredDisplay = TextDisplay()
    private val packetsDroppedDisplay = TextDisplay()
    private val bytesInFlightDisplay = TextDisplay()
    private val networkUsageDisplay = ProgressDisplay()
    private val packetDropRateDisplay = ProgressDisplay()
    private val networkUsageChartDispay = PieChart()

    var socketStatus:SocketStatus = SocketStatus.OPEN

        set(value)
        {
            socketStatusDisplay.value.text = value.friendlyString
            socketStatusDisplay.value.styleClass.remove(field.css)
            socketStatusDisplay.value.styleClass.add(value.css)
            field = value
        }

    var packetsDelivered:Int = 0

        set(value)
        {
            Platform.runLater {
                packetsDeliveredDisplay.value.text = value.toString()
            }
            field = value
        }

    var packetsDropped:Int = 0

        set(value)
        {
            Platform.runLater {
                packetsDroppedDisplay.value.text = value.toString()
            }
            field = value
        }

    var bytesInFlight:Int = 0

        set(value)
        {
            Platform.runLater {
                bytesInFlightDisplay.value.text = value.toString()
            }
            field = value
        }

    var networkUsage:Double = 0.0

        set(value)
        {
            Platform.runLater {
                networkUsageDisplay.progressBar.progressProperty().value = value
            }
            field = value
        }

    var packetDropRate:Double = 0.0

        set(value)
        {
            Platform.runLater {
                packetDropRateDisplay.progressBar.progressProperty().value = value
            }
            field = value
        }

    var networkUsageChartData = networkUsageChartDispay.data

    init
    {
        // configure aesthetic properties
        padding = Insets(Dimens.KEYLINE_SMALL.toDouble())
        hgap = Dimens.KEYLINE_SMALL.toDouble()
        vgap = Dimens.KEYLINE_SMALL.toDouble()

        // configure grid constraints
        val column1 = ColumnConstraints()
        column1.halignment = HPos.RIGHT
        columnConstraints.add(COL_INDEX_LABEL,column1)

        val column2 = ColumnConstraints()
        column2.isFillWidth = true
        column2.hgrow = Priority.ALWAYS
        columnConstraints.add(COL_INDEX_CONTENT,column2)

        val column3 = ColumnConstraints()
        column1.halignment = HPos.CENTER
        columnConstraints.add(COL_INDEX_NET_USAGE_CHART,column3)

        // configure & add child nodes
        socketStatusDisplay.label.text = SOCKET_STATUS_LABEL
        packetsDeliveredDisplay.label.text = PACKETS_DELIVERED_LABEL
        packetsDroppedDisplay.label.text = PACKETS_DROPPED_LABEL
        bytesInFlightDisplay.label.text = BYTES_IN_FLIGHT_LABEL
        networkUsageDisplay.label.text = NETWORK_USAGE_LABEL
        packetDropRateDisplay.label.text = PACKET_DROP_RATE_LABEL
        networkUsageChartDispay.minWidth = 250.0
        networkUsageChartDispay.minHeight = 250.0
        networkUsageChartDispay.prefWidth = 250.0
        networkUsageChartDispay.prefHeight = 250.0
        networkUsageChartDispay.animated = false
        networkUsageChartDispay.labelsVisible = true

        add(socketStatusDisplay)
        add(packetsDeliveredDisplay)
        add(packetsDroppedDisplay)
        add(bytesInFlightDisplay)
        add(networkUsageDisplay)
        add(packetDropRateDisplay)
        // add the pie chart. it should be in its own column, and span all rows
        // todo: find out way to get the row count some other way??? eh..
        add(networkUsageChartDispay,COL_INDEX_NET_USAGE_CHART,0,1,nextRow+1)
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

    private class TextDisplay(
        val label:Label = Label(),
        val value:Label = Label())

    private class ProgressDisplay(
        val label:Label = Label(),
        val progressBar:ProgressBar = ProgressBar())
}
