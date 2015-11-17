package gui

import gui.components.ProgressDisplay
import gui.components.SliderSetting
import gui.components.TextDisplay
import javafx.geometry.HPos
import javafx.geometry.Insets
import javafx.scene.control.Label
import javafx.scene.layout.Border
import javafx.scene.layout.BorderStroke
import javafx.scene.layout.BorderStrokeStyle
import javafx.scene.layout.BorderWidths
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.CornerRadii
import javafx.scene.layout.GridPane
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.paint.Paint

class StatisticsPane:GridPane()
{
    private val COL_INDEX_LABEL = 0
    private val COL_INDEX_CONTENT = 1

    private val PACKETS_DELIVERED_LABEL = "Packets Delivered:"

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
        val packetsDeliveredDisplay = TextDisplay()
        packetsDeliveredDisplay.label.text = PACKETS_DELIVERED_LABEL
        packetsDeliveredDisplay.value.text = "0"

        val packetsDroppedDisplay = TextDisplay()

        val bytesInFlightDisplay = ProgressDisplay()

        val throughputDisplay = ProgressDisplay()

        addTextDisplay(packetsDeliveredDisplay)
        addTextDisplay(packetsDroppedDisplay)
        addProgressDisplay(bytesInFlightDisplay)
        addProgressDisplay(throughputDisplay)
    }

    private fun addTextDisplay(newTextDisplay:TextDisplay)
    {
        add(newTextDisplay.label,COL_INDEX_LABEL,nextRow)
        add(newTextDisplay.value,COL_INDEX_CONTENT,nextRow)
        nextRow++
    }

    private fun addProgressDisplay(newProgressDisplay:ProgressDisplay)
    {
        add(newProgressDisplay.label,COL_INDEX_LABEL,nextRow)
        add(newProgressDisplay.progressBar,COL_INDEX_CONTENT,nextRow)
        nextRow++
    }
}
