package gui

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.layout.Border
import javafx.scene.layout.BorderPane
import javafx.scene.layout.BorderStroke
import javafx.scene.layout.BorderStrokeStyle
import javafx.scene.layout.BorderWidths
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.CornerRadii
import javafx.scene.layout.GridPane
import javafx.scene.layout.Priority
import javafx.scene.paint.Paint
import javafx.stage.Stage

class Window:Application()
{
    private val WINDOW_TITLE = "Window Title"
    private val WINDOW_WIDTH = 700
    private val WINDOW_HEIGHT = 480

    override fun start(primaryStage:Stage)
    {
        // configure the stage stage (the window)
        primaryStage.title = WINDOW_TITLE
        // set the scene (inside the window)
        primaryStage.scene = Scene(ContentPane(),WINDOW_WIDTH.toDouble(),WINDOW_HEIGHT.toDouble())
        primaryStage.show()
    }

    private inner class ContentPane:BorderPane()
    {
        init
        {
            // add a red, dashed 3 pixel, rounded thick border about the layout
            border = Border(BorderStroke(Paint.valueOf("f00"),BorderStrokeStyle.DASHED,CornerRadii(10.0),BorderWidths(3.0)))

            // ForwardingPane
            center = ForwardingPane()

            // SettingsPane, StatisticsPane
            val bottom = GridPane()
            bottom.isGridLinesVisible = true

            val constraints = ColumnConstraints()
            constraints.isFillWidth = true
            constraints.hgrow = Priority.ALWAYS
            bottom.columnConstraints.add(constraints)
            bottom.columnConstraints.add(constraints)

            bottom.add(SettingsPane(),0,0)
            bottom.add(StatisticsPane(),1,0)
            setBottom(bottom)
        }
    }
}
