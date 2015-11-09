package gui;

import javafx.beans.property.DoubleProperty;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;

import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Created by Eric Tsang on 11/8/2015.
 */
public class SliderSetting
{
    public Optional<Label> label;
    public Optional<NumberTextField> numberTextField;
    public Optional<Slider> slider;

    public DoubleProperty valueProperty()
    {
        return getSlider().valueProperty();
    }

    public SliderSetting()
    {
        label = Optional.empty();
        numberTextField = Optional.empty();
        slider = Optional.empty();
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
