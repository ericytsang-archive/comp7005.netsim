package gui

import gui.components.NumberTextField
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.Border
import javafx.scene.layout.BorderStroke
import javafx.scene.layout.BorderStrokeStyle
import javafx.scene.layout.BorderWidths
import javafx.scene.layout.CornerRadii
import javafx.scene.layout.VBox
import javafx.scene.paint.Paint
import java.net.InetAddress

/**
 * Created by Eric Tsang on 11/8/2015.
 */
class ForwardingPane:VBox()
{
    init
    {
        // add a red, dashed 3 pixel, rounded thick border about the layout
        border = Border(BorderStroke(Paint.valueOf("0f0"),BorderStrokeStyle.DASHED,CornerRadii(10.0),BorderWidths(3.0)))

        // label
        val label = Label()
        label.text = "ForwardingPane"
        children.add(label)
    }
}

// todo: do disss
private class ForwardingEntry()
{
    val address1:TextField = TextField()
    val port1:NumberTextField = NumberTextField()
    val address2:TextField = TextField()
    val port2:NumberTextField = NumberTextField()

    var observer:ForwardingEntry.Observer? = null
    var valid:Boolean = false

    init
    {
        address1.setOnAction {  }
    }

    private fun validateAndNotifyIfStateChanged()
    {

    }

    private fun validate():Boolean
    {
        val inetAddress:InetAddress = InetAddress.getByName(address1.text)
        return true
    }

    private interface Observer
    {
        fun onDataChanged();
    }
}
