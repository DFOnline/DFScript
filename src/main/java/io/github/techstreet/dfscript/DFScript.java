package io.github.techstreet.dfscript;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import io.github.techstreet.dfscript.commands.CommandManager;
import io.github.techstreet.dfscript.config.internal.ConfigFile;
import io.github.techstreet.dfscript.config.internal.ConfigInstruction;
import io.github.techstreet.dfscript.config.internal.gson.ConfigSerializer;
import io.github.techstreet.dfscript.config.internal.gson.types.*;
import io.github.techstreet.dfscript.config.internal.gson.types.list.*;
import io.github.techstreet.dfscript.config.structure.ConfigManager;
import io.github.techstreet.dfscript.config.types.*;
import io.github.techstreet.dfscript.config.types.list.*;
import io.github.techstreet.dfscript.features.*;
import io.github.techstreet.dfscript.loader.Loader;
import io.github.techstreet.dfscript.loader.v2.CodeInitializer;
import io.github.techstreet.dfscript.script.ScriptManager;
import io.github.techstreet.dfscript.util.Scheduler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DFScript implements ModInitializer {

    public static final Logger LOGGER = LogManager.getLogger();
    public static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();
    public static final MinecraftClient MC = MinecraftClient.getInstance();

    public static final String MOD_NAME = "DFScript";
    public static final String MOD_ID = "dfscript";
    public static String MOD_VERSION;

    public static String PLAYER_UUID = null;
    public static String PLAYER_NAME = null;

    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(ConfigInstruction.class, new ConfigSerializer())
            .registerTypeAdapter(BooleanSetting.class, new BooleanSerializer())
            .registerTypeAdapter(IntegerSetting.class, new IntegerSerializer())
            .registerTypeAdapter(DoubleSetting.class, new DoubleSerializer())
            .registerTypeAdapter(FloatSetting.class, new FloatSerializer())
            .registerTypeAdapter(LongSetting.class, new LongSerializer())
            .registerTypeAdapter(StringSetting.class, new StringSerializer())
            .registerTypeAdapter(StringListSetting.class, new StringListSerializer())
            .registerTypeAdapter(EnumSetting.class, new EnumSerializer())
            .registerTypeAdapter(DynamicStringSetting.class, new DynamicStringSerializer())
            .registerTypeAdapter(SoundSetting.class, new SoundSerializer())
            .setPrettyPrinting()
            .create();
    public static final JsonParser JSON_PARSER = new JsonParser();

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing");
        Runtime.getRuntime().addShutdownHook(new Thread(this::onClose));

        // allows FileDialog class to open without a HeadlessException
        System.setProperty("java.awt.headless", "false");

        PLAYER_UUID = MC.getSession().getUuid();
        PLAYER_NAME = MC.getSession().getUsername();

        MOD_VERSION = FabricLoader.getInstance().getModContainer(MOD_ID).get().getMetadata().getVersion().getFriendlyString();

        Loader loader = Loader.getInstance();
        loader.load(new CommandManager());
        loader.load(new ScriptManager());
        loader.load(new Scheduler());
        loader.load(new UpdateAlerts());

        CodeInitializer initializer = new CodeInitializer();
        initializer.add(new ConfigFile());
        initializer.add(new ConfigManager());

        LOGGER.info("Initialized");
    }

    public void onClose() {
        LOGGER.info("Closing...");
        try {
            ConfigFile.getInstance().save();
        } catch (Exception err) {
            LOGGER.error("Error");
            err.printStackTrace();
        }
        LOGGER.info("Closed.");
    }
}
