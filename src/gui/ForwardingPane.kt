package gui

import javafx.scene.control.Label
import javafx.scene.layout.Border
import javafx.scene.layout.BorderStroke
import javafx.scene.layout.BorderStrokeStyle
import javafx.scene.layout.BorderWidths
import javafx.scene.layout.CornerRadii
import javafx.scene.layout.VBox
import javafx.scene.paint.Paint

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
