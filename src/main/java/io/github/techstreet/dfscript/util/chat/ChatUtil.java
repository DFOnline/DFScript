package io.github.techstreet.dfscript.util.chat;

import io.github.techstreet.dfscript.DFScript;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.*;

import java.awt.*;

public class ChatUtil {

    public static void playSound(SoundEvent sound) {
        playSound(sound, 1F);
    }

    public static void playSound(SoundEvent sound, float pitch) {
        playSound(sound, 2F, pitch);
    }

    public static void playSound(SoundEvent sound, float pitch, float volume) {
        if (sound != null) {
            io.github.techstreet.dfscript.DFScript.MC.player.playSound(sound, volume, pitch);
        }
    }

    public static void chat(String message) {
        DFScript.MC.player.sendChatMessage(message, Text.literal(message));
    }

    public static void executeCommand(String command) {
        chat("/" + command.replaceFirst("^/", ""));
    }

    public static void executeCommandSilently(String command, int messageAmount) {
        executeCommand(command);
        MessageGrabber.hide(messageAmount);
    }

    public static void executeCommandSilently(String command) {
        executeCommandSilently(command, 1);
    }

    public static void sendMessage(String text) {
        sendMessage(Text.literal(text), null);
    }

    public static void sendMessage(Text text) {
        sendMessage(text, null);
    }

    public static void sendMessage(String text, ChatType prefixType) {
        sendMessage(Text.literal(text), prefixType);
    }

    public static void sendMessage(Text text, ChatType prefixType) {
        if (io.github.techstreet.dfscript.DFScript.MC.player == null) return;
        String prefix = "";
        if (prefixType != null) {
            prefix = prefixType.getString();
        }
        io.github.techstreet.dfscript.DFScript.MC.player.sendMessage(Text.literal(prefix).append(text), false);
    }

    public static MutableText setColor(MutableText component, Color color) {
        Style colorStyle = component.getStyle().withColor(TextColor.fromRgb(color.getRGB()));
        component.setStyle(colorStyle);
        return component;
    }

    public static void sendActionBar(Text msg) {
        if (io.github.techstreet.dfscript.DFScript.MC.player == null) return;
        io.github.techstreet.dfscript.DFScript.MC.player.sendMessage(msg, true);
    }

    public static void error(String s) {
        sendMessage(s, ChatType.FAIL);
    }

    public static void info(String s) {
        sendMessage(s, ChatType.INFO_BLUE);
    }
}
