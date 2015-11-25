package gui

import javafx.application.Application
import javafx.beans.InvalidationListener
import javafx.scene.Scene
import javafx.scene.control.ScrollPane
import javafx.scene.layout.BorderPane
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.GridPane
import javafx.scene.layout.Priority
import javafx.stage.Stage
import net.NetworkSimulator

class Window:Application()
{
    private val WINDOW_TITLE = "Window Title"
    private val WINDOW_WIDTH = 725
    private val WINDOW_HEIGHT = 480

    private val forwardingPane:ForwardingPane = ForwardingPane()
    private val settingsPane:SettingsPane = SettingsPane()
    private val statisticsPane:StatisticsPane = StatisticsPane()

    private val netsim:NetworkSimulator = NetworkSimulator()

    override fun start(primaryStage:Stage)
    {
        // configure the stage stage (the window)
        primaryStage.title = WINDOW_TITLE

        // set the scene (inside the window)
        primaryStage.scene = Scene(ContentPane(),WINDOW_WIDTH.toDouble(),WINDOW_HEIGHT.toDouble())
        primaryStage.scene.stylesheets.add("./gui/style.css")
        primaryStage.show()

        // bind setting pane properties to modify network simulator values
        settingsPane.jitter.addListener(InvalidationListener
            {
                netsim.jitter = settingsPane.jitter.value
            })
        settingsPane.latency.addListener(InvalidationListener
            {
                netsim.latency = settingsPane.latency.value
            })
        settingsPane.networkCapacity.addListener(InvalidationListener
            {
                netsim.capacity = settingsPane.networkCapacity.value
            })
        settingsPane.noise.addListener(InvalidationListener
            {
                netsim.noise = settingsPane.noise.value
            })
        settingsPane.packetDropFunction.addListener(InvalidationListener
            {
                netsim.packetDropFunction = settingsPane.packetDropFunction.value
            })
        settingsPane.serverPort.addListener(InvalidationListener
            {
                netsim.port = settingsPane.serverPort.value
            })

        // initialize every one of the netsim's fields from the setting's defaults
        netsim.jitter = settingsPane.jitter.value
        netsim.latency = settingsPane.latency.value
        netsim.capacity = settingsPane.networkCapacity.value
        netsim.noise = settingsPane.noise.value
        netsim.packetDropFunction = settingsPane.packetDropFunction.value
        netsim.port = settingsPane.serverPort.value

        // set network simulator's routing table from the routing table in the
        // forwarding pane
        netsim.routingTable = forwardingPane.inetSockAddressPairs

        // hook up the network simulator to the statistics pane
        netsim.bytesInFlight.addListener(InvalidationListener
            {
                statisticsPane.bytesInFlight = netsim.bytesInFlight.value.toDouble()/netsim.capacity.toDouble()
            })
        netsim.packetDropRate.addListener(InvalidationListener
            {
                statisticsPane.packetDropRate = netsim.packetDropRate.value
            })
        netsim.packetsDelivered.addListener(InvalidationListener
            {
                statisticsPane.packetsDelivered = netsim.packetsDelivered.value
            })
        netsim.packetsDropped.addListener(InvalidationListener
            {
                statisticsPane.packetsDropped = netsim.packetsDropped.value
            })
        netsim.socketStatus.addListener(InvalidationListener
            {
                statisticsPane.socketStatus = netsim.socketStatus.value
            })
        netsim.throughput.addListener(InvalidationListener
            {
                val nominator:Double = netsim.bytesInFlight.value.toDouble()/(netsim.latency+netsim.jitter.toDouble()/0.5).toDouble()
                val denominator:Double = netsim.capacity.toDouble()/(netsim.latency+netsim.jitter.toDouble()/0.5).toDouble()
                statisticsPane.throughput = nominator/denominator
            })
    }

    private inner class ContentPane:BorderPane()
    {
        init
        {
            // ForwardingPane
            val scrollPane = ScrollPane()
            scrollPane.content = forwardingPane

            // SettingsPane, StatisticsPane
            val bottomGrid = GridPane()
            bottomGrid.isGridLinesVisible = true

            val constraints = ColumnConstraints()
            constraints.isFillWidth = true
            constraints.hgrow = Priority.ALWAYS
            bottomGrid.columnConstraints.add(constraints)
            bottomGrid.columnConstraints.add(constraints)

            bottomGrid.add(settingsPane,0,0)
            bottomGrid.add(statisticsPane,1,0)

            center = scrollPane
            bottom = bottomGrid
        }
    }
}
