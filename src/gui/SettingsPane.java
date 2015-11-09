package gui;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.layout.*;

/**
 * Created by Eric Tsang on 11/8/2015.
 */
public class SettingsPane extends GridPane
{
    private static final int COL_INDEX_LABEL = 0;
    private static final int COL_INDEX_TEXTFIELD = 1;
    private static final int COL_INDEX_SLIDER = 2;
    private static final String LATENCY_LABEL = "Latency:";
    private static final double LATENCY_MAX = 5000;
    private static final double LATENCY_MIN = 0;
    private static final double LATENCY_DEFAULT = LATENCY_MIN;
    private static final String JITTER_LABEL = "Jitter:";
    private static final double JITTER_MAX = 5000;
    private static final double JITTER_MIN = 0;
    private static final double JITTER_DEFAULT = JITTER_MIN;

    private int nextRow;

    public SettingsPane()
    {
        super();

        // initialize instance data
        nextRow = 0;

        // configure aesthetic properties
        setPadding(new Insets(Dimens.KEYLINE_SMALL));
        setHgap(Dimens.KEYLINE_SMALL);
        setVgap(Dimens.KEYLINE_SMALL);

        // configure grid constraints
        ColumnConstraints column1 = new ColumnConstraints();
        column1.setHalignment(HPos.RIGHT);
        getColumnConstraints().add(0,column1);

        ColumnConstraints column2 = new ColumnConstraints();
        getColumnConstraints().add(1,column2);

        ColumnConstraints column3 = new ColumnConstraints();
        column3.setFillWidth(true);
        column3.setHgrow(Priority.ALWAYS);
        getColumnConstraints().add(2,column3);

        // latencyControl
        SliderSetting latencyControl = new SliderSetting();
        latencyControl.getLabel().setText(LATENCY_LABEL);
        latencyControl.getSlider().setMax(LATENCY_MAX);
        latencyControl.getSlider().setMin(LATENCY_MIN);
        latencyControl.getSlider().setValue(LATENCY_DEFAULT);
        addSliderSetting(latencyControl);

        // jitterControl
        SliderSetting jitterControl = new SliderSetting();
        jitterControl.getLabel().setText(JITTER_LABEL);
        jitterControl.getSlider().setMax(JITTER_MAX);
        jitterControl.getSlider().setMin(JITTER_MIN);
        jitterControl.getSlider().setValue(JITTER_DEFAULT);
        addSliderSetting(jitterControl);
    }

    private void addSliderSetting(SliderSetting newSliderSetting)
    {
        add(newSliderSetting.getLabel(),COL_INDEX_LABEL,nextRow);
        add(newSliderSetting.getNumberTextField(),COL_INDEX_TEXTFIELD,nextRow);
        add(newSliderSetting.getSlider(),COL_INDEX_SLIDER,nextRow);
        nextRow++;
    }
}
