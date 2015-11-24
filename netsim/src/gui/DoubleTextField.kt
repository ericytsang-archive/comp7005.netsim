package gui

import javafx.scene.control.TextField
import parse

internal class DoubleTextField:TextField()
{
    var min:Double = Double.MIN_VALUE

        set(newMin:Double)
        {
            field = newMin
            if (max < newMin) max = min
        }

    var max:Double = Double.MAX_VALUE

        set(newMax:Double)
        {
            field = newMax
            if (min > newMax) min = max
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
            setValue(Double.parse(newText))
        }
        catch (e:NumberFormatException)
        {
            // don't replace text
        }
    }
}
