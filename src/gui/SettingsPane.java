package gui;

import gui.components.NumberTextFieldSetting;
import gui.components.SliderSetting;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.layout.*;

public class SettingsPane extends GridPane
{
    private static final int COL_INDEX_LABEL = 0;
    private static final int COL_INDEX_TEXTFIELD = 1;
    private static final int COL_INDEX_SLIDER = 2;

    private static final String SERVER_PORT_LABEL = "Server Port:";
    private static final double SERVER_PORT_MAX = 65535;
    private static final double SERVER_PORT_MIN = 1;
    private static final double SERVER_PORT_DEFAULT = 7005;

    private static final String NETWORK_CAPACITY_LABEL = "Capacity:";
    private static final double NETWORK_CAPACITY_MAX = 10000;
    private static final double NETWORK_CAPACITY_MIN = 0;
    private static final double NETWORK_CAPACITY_DEFAULT = 3000;

    private static final String PACKET_DROP_FUN_LABEL = "Packet Drop Function:";
    private static final double PACKET_DROP_FUN_MAX = 6;
    private static final double PACKET_DROP_FUN_MIN = 0;
    private static final double PACKET_DROP_FUN_DEFAULT = 4;

    private static final String NOISE_LABEL = "Noise:";
    private static final double NOISE_MAX = 1;
    private static final double NOISE_MIN = 0;
    private static final double NOISE_DEFAULT = 0.01;

    private static final String LATENCY_LABEL = "Latency:";
    private static final double LATENCY_MAX = 5000;
    private static final double LATENCY_MIN = 0;
    private static final double LATENCY_DEFAULT = 250;

    private static final String JITTER_LABEL = "Jitter:";
    private static final double JITTER_MAX = 5000;
    private static final double JITTER_MIN = 0;
    private static final double JITTER_DEFAULT = 250;

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

        // serverPortControl
        NumberTextFieldSetting serverPortControl = new NumberTextFieldSetting(false);
        serverPortControl.getLabel().setText(SERVER_PORT_LABEL);
        serverPortControl.getNumberTextField().setMax(SERVER_PORT_MAX);
        serverPortControl.getNumberTextField().setMin(SERVER_PORT_MIN);
        serverPortControl.getNumberTextField().setValue(SERVER_PORT_DEFAULT);
        addNumberTextFieldSetting(serverPortControl);

        // packetDropFunctionControl
        SliderSetting networkCapacityControl = new SliderSetting(false);
        networkCapacityControl.getLabel().setText(NETWORK_CAPACITY_LABEL);
        networkCapacityControl.getSlider().setMax(NETWORK_CAPACITY_MAX);
        networkCapacityControl.getSlider().setMin(NETWORK_CAPACITY_MIN);
        networkCapacityControl.getSlider().setValue(NETWORK_CAPACITY_DEFAULT);
        addSliderSetting(networkCapacityControl);

        // packetDropFunctionControl
        SliderSetting packetDropFunctionControl = new SliderSetting(true);
        packetDropFunctionControl.getLabel().setText(PACKET_DROP_FUN_LABEL);
        packetDropFunctionControl.getSlider().setMax(PACKET_DROP_FUN_MAX);
        packetDropFunctionControl.getSlider().setMin(PACKET_DROP_FUN_MIN);
        packetDropFunctionControl.getSlider().setValue(PACKET_DROP_FUN_DEFAULT);
        addSliderSetting(packetDropFunctionControl);

        // noiseControl
        SliderSetting noiseControl = new SliderSetting(true);
        noiseControl.getLabel().setText(NOISE_LABEL);
        noiseControl.getSlider().setMax(NOISE_MAX);
        noiseControl.getSlider().setMin(NOISE_MIN);
        noiseControl.getSlider().setValue(NOISE_DEFAULT);
        addSliderSetting(noiseControl);

        // latencyControl
        SliderSetting latencyControl = new SliderSetting(false);
        latencyControl.getLabel().setText(LATENCY_LABEL);
        latencyControl.getSlider().setMax(LATENCY_MAX);
        latencyControl.getSlider().setMin(LATENCY_MIN);
        latencyControl.getSlider().setValue(LATENCY_DEFAULT);
        addSliderSetting(latencyControl);

        // jitterControl
        SliderSetting jitterControl = new SliderSetting(false);
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

    private void addNumberTextFieldSetting(NumberTextFieldSetting newNumberTextField)
    {
        add(newNumberTextField.getLabel(),COL_INDEX_LABEL,nextRow);
        add(newNumberTextField.getNumberTextField(),COL_INDEX_TEXTFIELD,nextRow);
        nextRow++;
    }
}
