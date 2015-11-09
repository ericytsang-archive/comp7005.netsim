package gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

public class Window extends Application
{
    private static final String WINDOW_TITLE = "Window Title";
    private static final int WINDOW_WIDTH = 640;
    private static final int WINDOW_HEIGHT = 480;

    public static void main(String[] args)
    {
        launch(args);
    }

    @Override
    // stage: the window
    public void start(Stage primaryStage) throws Exception
    {
        // configure the stage stage (the window)
        primaryStage.setTitle(WINDOW_TITLE);
        // set the scene (inside the window)
        primaryStage.setScene(new Scene(new ContentPane(),WINDOW_WIDTH,WINDOW_HEIGHT));
        primaryStage.show();
    }

    private class ContentPane extends BorderPane
    {
        public ContentPane()
        {
            super();

            // add a red, dashed 3 pixel, rounded thick border about the layout
            setBorder(new Border(new BorderStroke(Paint.valueOf("f00"),BorderStrokeStyle.DASHED,new CornerRadii(10),new BorderWidths(3))));

            // ForwardingPane
            setCenter(new ForwardingPane());

            // SettingsPane, StatisticsPane
            GridPane bottom = new GridPane();
            bottom.setGridLinesVisible(true);

            ColumnConstraints constraints = new ColumnConstraints();
            constraints.setFillWidth(true);
            constraints.setHgrow(Priority.ALWAYS);
            bottom.getColumnConstraints().add(constraints);
            bottom.getColumnConstraints().add(constraints);

            bottom.add(new SettingsPane(),0,0);
            bottom.add(new StatisticsPane(),1,0);
            setBottom(bottom);
        }
    }
}
