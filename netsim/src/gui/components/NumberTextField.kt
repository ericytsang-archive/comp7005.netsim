package gui.components

import javafx.scene.control.TextField
import parseDouble

class NumberTextField:TextField()
{
    private var min:Double = Double.MIN_VALUE
    private var max:Double = Double.MAX_VALUE

    fun setMax(newMax:Double)
    {
        max = newMax
        if (min > newMax) min = max
    }

    fun setMin(newMin:Double)
    {
        min = newMin
        if (max < newMin) max = min
    }

    fun setValue(newValue:Double)
    {
        var clampedValue = Math.min(Math.max(newValue,min),max)
        super.replaceText(0,length,clampedValue.toString())
    }

    override fun replaceText(start:Int,end:Int,text:String)
    {
        try
        {
            val newText = getText(0,start)+text+getText(end,length)
            setValue(parseDouble(newText))
        }
        catch (e:NumberFormatException)
        {
            // don't replace text
        }

    }
}
