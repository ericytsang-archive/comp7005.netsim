package gui.components;

import javafx.beans.property.DoubleProperty;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;

import java.util.Optional;

public class SliderSetting
{
    private final boolean allowDecimalNumbers;
    private Optional<Label> label;
    private Optional<NumberTextField> numberTextField;
    private Optional<Slider> slider;

    public SliderSetting(boolean allowDecimalNumbers)
    {
        this.allowDecimalNumbers = allowDecimalNumbers;
        label = Optional.empty();
        numberTextField = Optional.empty();
        slider = Optional.empty();
    }

    public DoubleProperty valueProperty()
    {
        return getSlider().valueProperty();
    }

    public Label getLabel()
    {
        return label.orElseGet(
            () -> {
                label = Optional.of(new Label());
                return label.get();
            });
    }

    public TextField getNumberTextField()
    {
        return numberTextField.orElseGet(() ->
            {
                numberTextField = Optional.of(new NumberTextField());
                numberTextField.get().textProperty().addListener((value,oldValue,newValue) ->
                    {
                        getSlider().setValue(Double.parseDouble(newValue));
                        getNumberTextField().setText(allowDecimalNumbers
                            ? Double.toString(getSlider().getValue())
                            : Integer.toString((int) Math.round(getSlider().getValue())));
                    });
                return numberTextField.get();
            });
    }

    public Slider getSlider()
    {
        return slider.orElseGet(() ->
            {
                slider = Optional.of(new Slider());
                slider.get().setMaxWidth(Double.MAX_VALUE);
                slider.get().valueProperty().addListener((value,oldValue,newValue) ->
                    {
                        getNumberTextField().setText(newValue.toString());
                    });
                return slider.get();
            });
    }
}
