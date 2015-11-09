package gui;

import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;

/**
 * Created by Eric Tsang on 11/8/2015.
 */
public class ForwardingPane extends VBox
{
    public ForwardingPane()
    {
        super();

        // add a red, dashed 3 pixel, rounded thick border about the layout
        setBorder(new Border(new BorderStroke(Paint.valueOf("0f0"),BorderStrokeStyle.DASHED,new CornerRadii(10),new BorderWidths(3))));

        // label
        Label label = new Label();
        label.setText("ForwardingPane");
        getChildren().add(label);
    }
}
