package net

import gui.CSS

/**
 * enumerations of various status that a socket may be in.
 */
enum class SocketStatus(val friendlyString:String,val css:String)
{
    /**
     * the socket is open, and properly bound to a port.
     */
    OPEN("Running",CSS.CONFIRM_TEXT),

    /**
     * the socket is not open and has failed to bing to a port for some reason.
     */
    BIND_ERR("Binding Error",CSS.WARNING_TEXT)
}
