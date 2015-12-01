package gui

import ProtocolPeer.ClientSocket
import ProtocolPeer.Connection
import javafx.application.Application
import javafx.beans.InvalidationListener
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
    private val clientSocketObserver = ClientSocketObserver()

    private var clientSocket:ClientSocket? = null

    override fun start(primaryStage:Stage)
    {
        // configure the stage stage (the window)
        primaryStage.title = WINDOW_TITLE

        // set the scene (inside the window)
        primaryStage.scene = Scene(ContentPane(),WINDOW_WIDTH.toDouble(),WINDOW_HEIGHT.toDouble())
        primaryStage.scene.stylesheets.add(CSS.FILE_PATH)
        primaryStage.show()

        serverPane.port.addListener(InvalidationListener
        {
            clientSocket = ClientSocket(serverPane.port.value,clientSocketObserver)
            clientSocket!!.setListening(serverPane.listening.value)
        })
        serverPane.listening.addListener(InvalidationListener
        {
            clientSocket?.setListening(serverPane.listening.value)
        })
    }

    private inner class ClientSocketObserver:ClientSocket.Observer
    {
        override fun onAccept(newConnection:Connection?)
        {
            // todo: implement
            throw UnsupportedOperationException()
        }
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
