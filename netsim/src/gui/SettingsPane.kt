package gui

import gui.components.SliderSetting
import javafx.geometry.HPos
import javafx.geometry.Insets
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.GridPane
import javafx.scene.layout.Priority

class SettingsPane:GridPane()
{
    private val COL_INDEX_LABEL = 0
    private val COL_INDEX_TEXTFIELD = 1
    private val COL_INDEX_SLIDER = 2

    private val SERVER_PORT_LABEL = "Server Port:"
    private val SERVER_PORT_MAX = 65535.0
    private val SERVER_PORT_MIN = 1.0
    private val SERVER_PORT_DEFAULT = 7005.0

    private val NETWORK_CAPACITY_LABEL = "Capacity:"
    private val NETWORK_CAPACITY_MAX = 10000.0
    private val NETWORK_CAPACITY_MIN = 0.0
    private val NETWORK_CAPACITY_DEFAULT = 3000.0

    private val PACKET_DROP_FUN_LABEL = "Packet Drop Function:"
    private val PACKET_DROP_FUN_MAX = 6.0
    private val PACKET_DROP_FUN_MIN = 0.0
    private val PACKET_DROP_FUN_DEFAULT = 4.0

    private val NOISE_LABEL = "Noise:"
    private val NOISE_MAX = 1.0
    private val NOISE_MIN = 0.0
    private val NOISE_DEFAULT = 0.01

    private val LATENCY_LABEL = "Latency:"
    private val LATENCY_MAX = 5000.0
    private val LATENCY_MIN = 0.0
    private val LATENCY_DEFAULT = 250.0

    private val JITTER_LABEL = "Jitter:"
    private val JITTER_MAX = 5000.0
    private val JITTER_MIN = 0.0
    private val JITTER_DEFAULT = 250.0

    private var nextRow:Int = 0

    init
    {
        configureLayout()
        addChildNodes()
    }

    private fun configureLayout()
    {
        // configure aesthetic properties
        padding = Insets(Dimens.KEYLINE_SMALL.toDouble())
        hgap = Dimens.KEYLINE_SMALL.toDouble()
        vgap = Dimens.KEYLINE_SMALL.toDouble()

        // configure grid constraints
        val column1 = ColumnConstraints()
        column1.halignment = HPos.RIGHT
        columnConstraints.add(0,column1)

        val column2 = ColumnConstraints()
        columnConstraints.add(1,column2)

        val column3 = ColumnConstraints()
        column3.isFillWidth = true
        column3.hgrow = Priority.ALWAYS
        columnConstraints.add(2,column3)
    }

    private fun addChildNodes()
    {
        // serverPortControl
        val serverPortControl = SliderSetting(false)
        serverPortControl.label.text = SERVER_PORT_LABEL
        serverPortControl.slider.max = SERVER_PORT_MAX
        serverPortControl.slider.min = SERVER_PORT_MIN
        serverPortControl.slider.value = SERVER_PORT_DEFAULT
        addNumberTextFieldSetting(serverPortControl)

        // packetDropFunctionControl
        val networkCapacityControl = SliderSetting(false)
        networkCapacityControl.label.text = NETWORK_CAPACITY_LABEL
        networkCapacityControl.slider.max = NETWORK_CAPACITY_MAX
        networkCapacityControl.slider.min = NETWORK_CAPACITY_MIN
        networkCapacityControl.slider.value = NETWORK_CAPACITY_DEFAULT
        addSliderSetting(networkCapacityControl)

        // packetDropFunctionControl
        val packetDropFunctionControl = SliderSetting(true)
        packetDropFunctionControl.label.text = PACKET_DROP_FUN_LABEL
        packetDropFunctionControl.slider.max = PACKET_DROP_FUN_MAX
        packetDropFunctionControl.slider.min = PACKET_DROP_FUN_MIN
        packetDropFunctionControl.slider.value = PACKET_DROP_FUN_DEFAULT
        addSliderSetting(packetDropFunctionControl)

        // noiseControl
        val noiseControl = SliderSetting(true)
        noiseControl.label.text = NOISE_LABEL
        noiseControl.slider.max = NOISE_MAX
        noiseControl.slider.min = NOISE_MIN
        noiseControl.slider.value = NOISE_DEFAULT
        addSliderSetting(noiseControl)

        // latencyControl
        val latencyControl = SliderSetting(false)
        latencyControl.label.text = LATENCY_LABEL
        latencyControl.slider.max = LATENCY_MAX
        latencyControl.slider.min = LATENCY_MIN
        latencyControl.slider.value = LATENCY_DEFAULT
        addSliderSetting(latencyControl)

        // jitterControl
        val jitterControl = SliderSetting(false)
        jitterControl.label.text = JITTER_LABEL
        jitterControl.slider.max = JITTER_MAX
        jitterControl.slider.min = JITTER_MIN
        jitterControl.slider.value = JITTER_DEFAULT
        addSliderSetting(jitterControl)
    }

    private fun addSliderSetting(newSliderSetting:SliderSetting)
    {
        add(newSliderSetting.label,COL_INDEX_LABEL,nextRow)
        add(newSliderSetting.numberTextField,COL_INDEX_TEXTFIELD,nextRow)
        add(newSliderSetting.slider,COL_INDEX_SLIDER,nextRow)
        nextRow++
    }

    private fun addNumberTextFieldSetting(newSliderSetting:SliderSetting)
    {
        add(newSliderSetting.label,COL_INDEX_LABEL,nextRow)
        add(newSliderSetting.numberTextField,COL_INDEX_TEXTFIELD,nextRow)
        nextRow++
    }
}
