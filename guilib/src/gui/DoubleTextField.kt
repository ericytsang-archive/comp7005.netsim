package gui

import javafx.scene.control.TextField

public class DoubleTextField(var allowEmpty:Boolean = false):TextField()
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
        val newText = getText(0,start)+text+getText(end,length)
        try
        {
            setValue(newText.toDouble())
        }
        catch (e:NumberFormatException)
        {
            if(allowEmpty && newText.isEmpty())
            {
                super.replaceText(0,length,newText)
            }
        }
    }
}
