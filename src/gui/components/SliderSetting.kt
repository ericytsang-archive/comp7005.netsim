package gui.components

import javafx.beans.property.DoubleProperty
import javafx.scene.control.Label
import javafx.scene.control.Slider
import parseDouble

class SliderSetting(private val allowDecimalNumbers:Boolean)
{
    val label:Label = Label()
    var numberTextField:NumberTextField = NumberTextField()
    var slider:Slider = Slider()

    init
    {
        // configure numberTextField so when its value changes, it updates slider
        numberTextField.textProperty().addListener(
            {
                value,oldValue,newValue ->
                slider.value = parseDouble(newValue)
                numberTextField.text = if (allowDecimalNumbers)
                    slider.value.toString()
                    else Math.round(slider.value).toString()
            })

        // configure slider so when its value changes, it updates numberTextField
        slider.maxWidth = java.lang.Double.MAX_VALUE
        slider.valueProperty().addListener(
            {
                value,oldValue,newValue ->
                numberTextField.text = newValue.toString()
            })
    }

    fun valueProperty():DoubleProperty
    {
        return slider.valueProperty()
    }
}
