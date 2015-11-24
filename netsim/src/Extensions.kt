object Boolean
{
    fun parse(string:String) = java.lang.Boolean.valueOf(string)
}

fun Byte.Companion.parse(string:String) = java.lang.Byte.valueOf(string)
fun Short.Companion.parse(string:String) = java.lang.Short.valueOf(string)
fun Int.Companion.parse(string:String) = java.lang.Integer.valueOf(string)
fun Long.Companion.parse(string:String) = java.lang.Long.valueOf(string)
fun Float.Companion.parse(string:String) = java.lang.Float.valueOf(string)
fun Double.Companion.parse(string:String) = java.lang.Double.valueOf(string)
