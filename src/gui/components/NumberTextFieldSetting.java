package gui.components;

import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;

import java.util.Optional;

public class NumberTextFieldSetting
{
    private Optional<Label> label;
    private Optional<NumberTextField> numberTextField;

    public NumberTextFieldSetting(boolean allowDecimalNumbers)
    {
        label = Optional.empty();
        numberTextField = Optional.empty();
        valueProperty().addListener((value,oldValue,newValue) ->
        {
            if(!allowDecimalNumbers)
            {
                int intValue = (int) Math.round(Double.parseDouble(getNumberTextField().getText()));
                getNumberTextField().setText(Integer.toString(intValue));
            }
        });
    }

    public StringProperty valueProperty()
    {
        return getNumberTextField().textProperty();
    }

    public Label getLabel()
    {
        return label.orElseGet(() ->
        {
            label = Optional.of(new Label());
            return label.get();
        });
    }

    public NumberTextField getNumberTextField()
    {
        return numberTextField.orElseGet(() ->
        {
            numberTextField = Optional.of(new NumberTextField());
            return numberTextField.get();
        });
    }
}
