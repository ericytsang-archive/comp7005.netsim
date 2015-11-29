package gui

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control.ScrollPane
import javafx.scene.layout.BorderPane
import javafx.stage.Stage

class Window:Application()
{
    private val WINDOW_TITLE = "Window Title"
    private val WINDOW_WIDTH = 1000
    private val WINDOW_HEIGHT = 480

    private val connectionsPane = ConnectionsPane()
    private val serverPane = ServerPane()

    override fun start(primaryStage:Stage)
    {
        // configure the stage stage (the window)
        primaryStage.title = WINDOW_TITLE

        // set the scene (inside the window)
        primaryStage.scene = Scene(ContentPane(),WINDOW_WIDTH.toDouble(),WINDOW_HEIGHT.toDouble())
        primaryStage.scene.stylesheets.add(CSS.FILE_PATH)
        primaryStage.show()
    }

    private inner class ContentPane:BorderPane()
    {
        init
        {
            // populate the content pane
            center = ScrollPane(connectionsPane)
            bottom = serverPane
        }
    }
}
