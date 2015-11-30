package gui

import javafx.beans.InvalidationListener
import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.geometry.Insets
import javafx.scene.control.CheckBox
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.GridPane
import javafx.scene.layout.Priority
import net.NetUtils

class ServerPane:GridPane()
{
    private val COL_INDEX_LOCAL_PORT:Int = 0
    private val COL_INDEX_LISTEN_CHECKBOX:Int = 1

    private val LOCALPORT_TEXTFIELD_PROMPT = "Local Port"
    private val LOCALPORT_TEXTFIELD_DEFAULT = 7005
    private val LISTEN_CHECKBOX_PROMPT = "Listening"

    private val localPortTextField = IntTextField()
    private val listenCheckbox = CheckBox()

    val port = SimpleIntegerProperty()
    val listening:BooleanProperty = listenCheckbox.selectedProperty()

    init
    {
        // configure aesthetic properties
        padding = Insets(Dimens.KEYLINE_SMALL.toDouble())
        hgap = Dimens.KEYLINE_SMALL.toDouble()
        vgap = Dimens.KEYLINE_SMALL.toDouble()

        // configure grid constraints
        columnConstraints.add(ColumnConstraints())
        columnConstraints.add(ColumnConstraints())

        val lastColumn = ColumnConstraints()
        lastColumn.isFillWidth = true
        lastColumn.hgrow = Priority.ALWAYS
        columnConstraints.add(1,lastColumn)

        // configure gui components
        localPortTextField.promptText = LOCALPORT_TEXTFIELD_PROMPT
        localPortTextField.min = NetUtils.MIN_PORT
        localPortTextField.max = NetUtils.MAX_PORT
        localPortTextField.text = LOCALPORT_TEXTFIELD_DEFAULT.toString()

        listenCheckbox.text = LISTEN_CHECKBOX_PROMPT

        // bind class properties with gui node properties
        localPortTextField.textProperty().addListener(
            InvalidationListener{port.value = localPortTextField.text.toInt()})

        // add gui controls
        add(localPortTextField,COL_INDEX_LOCAL_PORT,1)
        add(listenCheckbox,COL_INDEX_LISTEN_CHECKBOX,1)
    }
}
