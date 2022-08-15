package io.github.techstreet.dfscript.screen.script;

import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.screen.CScreen;
import io.github.techstreet.dfscript.screen.widget.CButton;
import io.github.techstreet.dfscript.script.Script;
import io.github.techstreet.dfscript.script.ScriptManager;

public class ScriptDeletionScreen extends CScreen {

    public ScriptDeletionScreen(Script script) {
        super(100,50);

        widgets.add(new CButton(5,4,90,20,"Delete",() -> {
            ScriptManager.getInstance().deleteScript(script);
            io.github.techstreet.dfscript.DFScript.MC.setScreen(new ScriptListScreen());
        }));

        widgets.add(new CButton(5,28,90,20,"Abort",() -> {
            io.github.techstreet.dfscript.DFScript.MC.setScreen(new ScriptListScreen());
        }));
    }

    @Override
    public void close() {
        DFScript.MC.setScreen(new ScriptListScreen());
    }
}
