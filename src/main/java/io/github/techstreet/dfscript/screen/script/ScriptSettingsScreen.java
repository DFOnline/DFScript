package io.github.techstreet.dfscript.screen.script;

import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.screen.CScreen;
import io.github.techstreet.dfscript.screen.widget.*;
import io.github.techstreet.dfscript.script.Script;
import io.github.techstreet.dfscript.script.ScriptManager;
import io.github.techstreet.dfscript.script.action.ScriptActionType;
import io.github.techstreet.dfscript.script.options.ScriptNamedOption;
import io.github.techstreet.dfscript.script.options.ScriptOption;
import io.github.techstreet.dfscript.util.chat.ChatUtil;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ScriptSettingsScreen extends CScreen {
    private final Script script;

    private static int scroll = 0;

    private final CScrollPanel panel;

    private final List<CWidget> contextMenu = new ArrayList<>();

    public ScriptSettingsScreen(Script script) {
        super(125, 100);
        this.script = script;
        panel = new CScrollPanel(0, 3, 120, 94);

        widgets.add(panel);

        int y = 3;
        int index = 0;

        CText descriptionText = new CText(5, y, Text.of("Description:"));
        panel.add(descriptionText);

        y += 5;

        CTextField description = new CTextField(script.getDescription(), 5, y, 110, 20, true);
        description.setChangedListener(() -> script.setDescription(description.getText()));
        panel.add(description);

        y += 22;

        for(ScriptNamedOption option : script.getOptions())
        {
            String name = option.getFullName();

            Text text = Text.of(name);

            CWrappedText ctext = new CWrappedText(5, y, 110*2, text);

            panel.add(ctext);

            int height = DFScript.MC.textRenderer.getWrappedLinesHeight(name, 110*2) / 2;
            int finalIndex = index;
            panel.add(new CButton(5, y, 115, height, "",() -> {}) {
                @Override
                public void render(MatrixStack stack, int mouseX, int mouseY, float tickDelta) {
                    Rectangle b = getBounds();
                    if (b.contains(mouseX, mouseY)) {
                        DrawableHelper.fill(stack, b.x, b.y, b.x + b.width, b.y + b.height, 0x33000000);
                    }
                }

                @Override
                public boolean mouseClicked(double x, double y, int button) {
                    if (getBounds().contains(x, y)) {
                        DFScript.MC.getSoundManager().play(PositionedSoundInstance.ambient(SoundEvents.UI_BUTTON_CLICK, 1f,1f));

                        if (button != 0) {
                            CButton insertBefore = new CButton((int) x, (int) y, 40, 8, "Insert Before", () -> {
                                DFScript.MC.setScreen(new ScriptAddSettingScreen(script, finalIndex));
                            });
                            CButton insertAfter = new CButton((int) x, (int) y+8, 40, 8, "Insert After", () -> {
                                DFScript.MC.setScreen(new ScriptAddSettingScreen(script, finalIndex + 1));
                            });
                            CButton delete = new CButton((int) x, (int) y+16, 40, 8, "Delete", () -> {
                                script.getOptions().remove(finalIndex);
                                scroll = panel.getScroll();
                                DFScript.MC.setScreen(new ScriptSettingsScreen(script));
                            });
                            DFScript.MC.send(() -> {
                                panel.add(insertBefore);
                                panel.add(insertAfter);
                                panel.add(delete);
                                contextMenu.add(insertBefore);
                                contextMenu.add(insertAfter);
                                contextMenu.add(delete);
                            });
                        }
                        else
                        {
                            DFScript.MC.setScreen(new ScriptEditSettingScreen(script, option));
                        }
                        return true;
                    }
                    return false;
                }
            });

            y += height;
            y++;

            y = option.create(panel, 5, y);

            index++;
        }

        CButton add = new CButton(37, y, 46, 8, "Add", () -> {
            DFScript.MC.setScreen(new ScriptAddSettingScreen(script, script.getOptions().size()));
        });

        panel.add(add);

        panel.setScroll(scroll);
    }

    @Override
    public void close() {
        DFScript.MC.setScreen(new ScriptEditScreen(script));
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean b = super.mouseClicked(mouseX, mouseY, button);
        clearContextMenu();
        return b;
    }
    private void clearContextMenu() {
        for (CWidget w : contextMenu) {
            panel.remove(w);
        }
        contextMenu.clear();
    }
}