/*
 * Decompiled with CFR 0.152.
 */
package org.github.paperspigot;

import com.google.common.base.Throwables;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import net.minecraft.server.Items;
import net.minecraft.server.MinecraftServer;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

public class PaperSpigotConfig {
    private static File CONFIG_FILE;
    private static final String HEADER = "This is the main configuration file for PaperSpigot.\nAs you can see, there's tons to configure. Some options may impact gameplay, so use\nwith caution, and make sure you know what each option does before configuring.\n\nIf you need help with the configuration or have any questions related to PaperSpigot,\njoin us at the IRC.\n\nIRC: #paperspigot @ irc.spi.gt ( http://irc.spi.gt/iris/?channels=PaperSpigot )\n";
    public static YamlConfiguration config;
    static int version;
    static Map<String, Command> commands;
    public static double babyZombieMovementSpeed;
    public static boolean interactLimitEnabled;
    public static double strengthEffectModifier;
    public static double weaknessEffectModifier;
    public static Set<Integer> dataValueAllowedItems;
    public static boolean stackableLavaBuckets;
    public static boolean stackableWaterBuckets;
    public static boolean stackableMilkBuckets;
    public static boolean warnForExcessiveVelocity;

    public static void init(File configFile) {
        CONFIG_FILE = configFile;
        config = new YamlConfiguration();
        try {
            config.load(CONFIG_FILE);
        }
        catch (IOException iOException) {
        }
        catch (InvalidConfigurationException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not load spigot.yml, please correct your syntax errors", ex);
            throw Throwables.propagate(ex);
        }
        config.options().header(HEADER);
        config.options().copyDefaults(true);
        commands = new HashMap<String, Command>();
        version = PaperSpigotConfig.getInt("config-version", 9);
        PaperSpigotConfig.set("config-version", 9);
        PaperSpigotConfig.readConfig(PaperSpigotConfig.class, null);
    }

    public static void registerCommands() {
        for (Map.Entry<String, Command> entry : commands.entrySet()) {
            MinecraftServer.getServer().server.getCommandMap().register(entry.getKey(), "Paper", entry.getValue());
        }
    }

    static void readConfig(Class<?> clazz, Object instance) {
        Method[] methodArray = clazz.getDeclaredMethods();
        int n = methodArray.length;
        int n2 = 0;
        while (n2 < n) {
            Method method = methodArray[n2];
            if (Modifier.isPrivate(method.getModifiers()) && method.getParameterTypes().length == 0 && method.getReturnType() == Void.TYPE) {
                try {
                    method.setAccessible(true);
                    method.invoke(instance, new Object[0]);
                }
                catch (InvocationTargetException ex) {
                    throw Throwables.propagate(ex.getCause());
                }
                catch (Exception ex) {
                    Bukkit.getLogger().log(Level.SEVERE, "Error invoking " + method, ex);
                }
            }
            ++n2;
        }
        try {
            config.save(CONFIG_FILE);
        }
        catch (IOException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not save " + CONFIG_FILE, ex);
        }
    }

    private static void set(String path, Object val) {
        config.set(path, val);
    }

    private static boolean getBoolean(String path, boolean def) {
        config.addDefault(path, def);
        return config.getBoolean(path, config.getBoolean(path));
    }

    private static double getDouble(String path, double def) {
        config.addDefault(path, def);
        return config.getDouble(path, config.getDouble(path));
    }

    private static float getFloat(String path, float def) {
        return (float)PaperSpigotConfig.getDouble(path, def);
    }

    private static int getInt(String path, int def) {
        config.addDefault(path, def);
        return config.getInt(path, config.getInt(path));
    }

    private static <T> List getList(String path, T def) {
        config.addDefault(path, def);
        return config.getList(path, config.getList(path));
    }

    private static String getString(String path, String def) {
        config.addDefault(path, def);
        return config.getString(path, config.getString(path));
    }

    public static boolean adaptativeChunkGC;
    private static void adaptativeChunkGC()
    {
        adaptativeChunkGC = getBoolean("adaptative-chunk-gc", true);
    }
    
    private static void babyZombieMovementSpeed() {
        babyZombieMovementSpeed = PaperSpigotConfig.getDouble("settings.baby-zombie-movement-speed", 0.5);
    }

    private static void interactLimitEnabled() {
        interactLimitEnabled = PaperSpigotConfig.getBoolean("settings.limit-player-interactions", true);
        if (!interactLimitEnabled) {
            Bukkit.getLogger().log(Level.INFO, "Disabling player interaction limiter, your server may be more vulnerable to malicious users");
        }
    }

    private static void effectModifiers() {
        strengthEffectModifier = PaperSpigotConfig.getDouble("effect-modifiers.strength", 1.3);
        weaknessEffectModifier = PaperSpigotConfig.getDouble("effect-modifiers.weakness", -0.5);
    }

    private static void dataValueAllowedItems() {
        dataValueAllowedItems = new HashSet<Integer>(PaperSpigotConfig.getList("data-value-allowed-items", Collections.emptyList()));
        Bukkit.getLogger().info("Data value allowed items: " + StringUtils.join(dataValueAllowedItems, ", "));
    }

    private static void excessiveVelocityWarning() {
        warnForExcessiveVelocity = PaperSpigotConfig.getBoolean("warnWhenSettingExcessiveVelocity", true);
    }
}

