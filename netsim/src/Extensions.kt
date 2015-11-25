// todo: add as extension to the Boolean.Companion class
fun parseBoolean(string:String) = java.lang.Boolean.valueOf(string)
fun Byte.Companion.parse(string:String) = java.lang.Byte.valueOf(string)
fun Short.Companion.parse(string:String) = java.lang.Short.valueOf(string)
fun Int.Companion.parse(string:String) = java.lang.Integer.valueOf(string)
fun Long.Companion.parse(string:String) = java.lang.Long.valueOf(string)
fun Float.Companion.parse(string:String) = java.lang.Float.valueOf(string)
fun Double.Companion.parse(string:String) = java.lang.Double.valueOf(string)

// todo: add as extension to the Math.Companion class
fun roll(probabilityTrue:Double):Boolean = Math.random() >= 1.0 - probabilityTrue
