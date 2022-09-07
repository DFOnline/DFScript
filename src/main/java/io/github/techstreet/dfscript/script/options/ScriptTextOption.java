package io.github.techstreet.dfscript.script.options;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.github.techstreet.dfscript.screen.widget.CScrollPanel;
import io.github.techstreet.dfscript.screen.widget.CTextField;
import io.github.techstreet.dfscript.script.argument.ScriptArgument;
import io.github.techstreet.dfscript.script.argument.ScriptTextArgument;
import io.github.techstreet.dfscript.script.values.ScriptTextValue;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

public class ScriptTextOption implements ScriptOption {

    String value = "";

    public ScriptTextOption(String value) {
        this.value = value;
    }

    public ScriptTextOption() {}

    @Override
    public ScriptArgument getValue() {
        return new ScriptTextArgument(value);
    }

    @Override
    public String getName() { return "Text"; }

    @Override
    public int create(CScrollPanel panel, int x, int y, int width) {
        CTextField field = new CTextField(value, x, y, width, 10, true);
        field.setChangedListener(() -> value = field.getText());
        panel.add(field);

        return y + 12;
    }

    @Override
    public Item getIcon() {
        return Items.BOOK;
    }

    @Override
    public String getType() {
        return "TEXT";
    }

    @Override
    public JsonPrimitive getJsonPrimitive() {
        return new JsonPrimitive(value);
    }
}
