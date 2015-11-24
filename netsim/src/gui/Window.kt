package gui

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control.ScrollPane
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
        primaryStage.scene.stylesheets.add("./gui/style.css")
        primaryStage.show()
    }

    private inner class ContentPane:BorderPane()
    {
        init
        {
            // add a red, dashed 3 pixel, rounded thick border about the layout
            border = Border(BorderStroke(Paint.valueOf("f00"),BorderStrokeStyle.DASHED,CornerRadii(10.0),BorderWidths(3.0)))

            // ForwardingPane
            val scrollPane = ScrollPane()
            scrollPane.content = ForwardingPane()

            // SettingsPane, StatisticsPane
            val bottomGrid = GridPane()
            bottomGrid.isGridLinesVisible = true

            val constraints = ColumnConstraints()
            constraints.isFillWidth = true
            constraints.hgrow = Priority.ALWAYS
            bottomGrid.columnConstraints.add(constraints)
            bottomGrid.columnConstraints.add(constraints)

            bottomGrid.add(SettingsPane(),0,0)
            bottomGrid.add(StatisticsPane(),1,0)

            center = scrollPane
            bottom = bottomGrid
        }
    }
}
