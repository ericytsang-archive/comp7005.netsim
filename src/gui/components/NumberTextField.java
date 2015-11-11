package gui.components;

import javafx.scene.control.TextField;

import java.util.Optional;

public class NumberTextField extends TextField
{
    private Optional<Double> min;
    private Optional<Double> max;

    public NumberTextField()
    {
        super();
        min = Optional.empty();
        max = Optional.empty();
    }

    public void setMax(double newMax)
    {
        max = Optional.of(newMax);
        min.filter(min -> min > newMax).ifPresent((unused) -> min = max);
    }

    public void setMin(double newMin)
    {
        min = Optional.of(newMin);
        max.filter(max -> max < newMin).ifPresent((unused) -> max = min);
    }

    public void setValue(double newValue)
    {
        newValue = Math.max(newValue,min.orElse(Double.MIN_VALUE));
        newValue = Math.min(newValue,max.orElse(Double.MAX_VALUE));
        super.replaceText(0,getLength(),Double.toString(newValue));
    }

    @Override
    public void replaceText(int start,int end,String text)
    {
        try
        {
            String newText = getText(0,start)+text+getText(end,getLength());
            setValue(Double.parseDouble(newText));
        }
        catch(NumberFormatException e)
        {
            // don't replace text
        }
    }
}
