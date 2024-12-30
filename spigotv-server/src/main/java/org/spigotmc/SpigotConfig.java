/*
 * Decompiled with CFR 0.152.
 */
package org.spigotmc;

import co.aikar.timings.Timings;
import co.aikar.timings.TimingsManager;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import gnu.trove.map.hash.TObjectIntHashMap;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import net.minecraft.server.AttributeRanged;
import net.minecraft.server.GenericAttributes;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.spigotmc.RestartCommand;
import org.spigotmc.TicksPerSecondCommand;
import org.spigotmc.WatchdogThread;

public class SpigotConfig {
    private static File CONFIG_FILE;
    private static final String HEADER = "This is the main configuration file for Spigot.\nAs you can see, there's tons to configure. Some options may impact gameplay, so use\nwith caution, and make sure you know what each option does before configuring.\nFor a reference for any variable inside this file, check out the Spigot wiki at\nhttp://www.spigotmc.org/wiki/spigot-configuration/\n\nIf you need help with the configuration or have any questions related to Spigot,\njoin us at the IRC or drop by our forums and leave a post.\n\nIRC: #spigot @ irc.spi.gt ( http://www.spigotmc.org/pages/irc/ )\nForums: http://www.spigotmc.org/\n";
    public static YamlConfiguration config;
    static int version;
    static Map<String, Command> commands;
    public static boolean logCommands;
    public static int tabComplete;
    public static String whitelistMessage;
    public static String unknownCommandMessage;
    public static String serverFullMessage;
    public static String outdatedClientMessage;
    public static String outdatedServerMessage;
    public static int timeoutTime;
    public static boolean restartOnCrash;
    public static String restartScript;
    public static String restartMessage;
    public static boolean bungee;
    public static boolean lateBind;
    public static boolean disableStatSaving;
    public static TObjectIntHashMap<String> forcedStats;
    public static int playerSample;
    public static int playerShuffle;
    public static List<String> spamExclusions;
    public static boolean silentCommandBlocks;
    public static boolean filterCreativeItems;
    public static Set<String> replaceCommands;
    public static int userCacheCap;
    public static boolean saveUserCacheOnStopOnly;
    public static int intCacheLimit;
    public static double movedWronglyThreshold;
    public static double movedTooQuicklyThreshold;
    public static double maxHealth;
    public static double movementSpeed;
    public static double attackDamage;
    public static boolean debug;

    static {
        outdatedClientMessage = "Outdated client! Please use {0}";
        outdatedServerMessage = "Outdated server! I'm still on {0}";
        timeoutTime = 60;
        restartOnCrash = true;
        restartScript = "./start.sh";
        forcedStats = new TObjectIntHashMap();
        maxHealth = 2048.0;
        movementSpeed = 2048.0;
        attackDamage = 2048.0;
    }

    public static void init(File configFile) {
        CONFIG_FILE = configFile;
        config = new YamlConfiguration();
        try {
            config.load(CONFIG_FILE);
        }
        catch (IOException iOException) {
        }
        catch (InvalidConfigurationException ex) {
            Bukkit.getLogger().log(java.util.logging.Level.SEVERE, "Could not load spigot.yml, please correct your syntax errors", ex);
            throw Throwables.propagate(ex);
        }
        config.options().header(HEADER);
        config.options().copyDefaults(true);
        commands = new HashMap<String, Command>();
        version = SpigotConfig.getInt("config-version", 8);
        SpigotConfig.set("config-version", 8);
        SpigotConfig.readConfig(SpigotConfig.class, null);
    }

    public static void registerCommands() {
        for (Map.Entry<String, Command> entry : commands.entrySet()) {
            MinecraftServer.getServer().server.getCommandMap().register(entry.getKey(), "Spigot", entry.getValue());
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
                    Bukkit.getLogger().log(java.util.logging.Level.SEVERE, "Error invoking " + method, ex);
                }
            }
            ++n2;
        }
        try {
            config.save(CONFIG_FILE);
        }
        catch (IOException ex) {
            Bukkit.getLogger().log(java.util.logging.Level.SEVERE, "Could not save " + CONFIG_FILE, ex);
        }
    }

    private static void set(String path, Object val) {
        config.set(path, val);
    }

    private static boolean getBoolean(String path, boolean def) {
        config.addDefault(path, def);
        return config.getBoolean(path, config.getBoolean(path));
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

    private static double getDouble(String path, double def) {
        config.addDefault(path, def);
        return config.getDouble(path, config.getDouble(path));
    }

    private static void logCommands() {
        logCommands = SpigotConfig.getBoolean("commands.log", true);
    }

    private static void tabComplete() {
        if (version < 6) {
            boolean oldValue = SpigotConfig.getBoolean("commands.tab-complete", true);
            if (oldValue) {
                SpigotConfig.set("commands.tab-complete", 0);
            } else {
                SpigotConfig.set("commands.tab-complete", -1);
            }
        }
        tabComplete = SpigotConfig.getInt("commands.tab-complete", 0);
    }

    private static String transform(String s) {
        return ChatColor.translateAlternateColorCodes('&', s).replaceAll("\\n", "\n");
    }

    private static void messages() {
        if (version < 8) {
            SpigotConfig.set("messages.outdated-client", outdatedClientMessage);
            SpigotConfig.set("messages.outdated-server", outdatedServerMessage);
        }
        whitelistMessage = SpigotConfig.transform(SpigotConfig.getString("messages.whitelist", "You are not whitelisted on this server!"));
        unknownCommandMessage = SpigotConfig.transform(SpigotConfig.getString("messages.unknown-command", "Unknown command. Type \"/help\" for help."));
        serverFullMessage = SpigotConfig.transform(SpigotConfig.getString("messages.server-full", "The server is full!"));
        outdatedClientMessage = SpigotConfig.transform(SpigotConfig.getString("messages.outdated-client", outdatedClientMessage));
        outdatedServerMessage = SpigotConfig.transform(SpigotConfig.getString("messages.outdated-server", outdatedServerMessage));
    }

    private static void watchdog() {
        timeoutTime = SpigotConfig.getInt("settings.timeout-time", timeoutTime);
        restartOnCrash = SpigotConfig.getBoolean("settings.restart-on-crash", restartOnCrash);
        restartScript = SpigotConfig.getString("settings.restart-script", restartScript);
        restartMessage = SpigotConfig.transform(SpigotConfig.getString("messages.restart", "Server is restarting"));
        commands.put("restart", new RestartCommand("restart"));
        WatchdogThread.doStart(timeoutTime, restartOnCrash);
    }

    private static void bungee() {
        if (version < 4) {
            SpigotConfig.set("settings.bungeecord", false);
            System.out.println("Oudated config, disabling BungeeCord support!");
        }
        bungee = SpigotConfig.getBoolean("settings.bungeecord", false);
    }

    private static void timings() {
        boolean timings = SpigotConfig.getBoolean("timings.enabled", true);
        boolean verboseTimings = SpigotConfig.getBoolean("timings.verbose", true);
        TimingsManager.privacy = SpigotConfig.getBoolean("timings.server-name-privacy", false);
        TimingsManager.hiddenConfigs = SpigotConfig.getList("timings.hidden-config-entries", Lists.newArrayList("database", "settings.bungeecord-addresses"));
        int timingHistoryInterval = SpigotConfig.getInt("timings.history-interval", 300);
        int timingHistoryLength = SpigotConfig.getInt("timings.history-length", 3600);
        Timings.setVerboseTimingsEnabled(verboseTimings);
        Timings.setTimingsEnabled(timings);
        Timings.setHistoryInterval(timingHistoryInterval * 20);
        Timings.setHistoryLength(timingHistoryLength * 20);
        Bukkit.getLogger().log(java.util.logging.Level.INFO, "Spigot Timings: " + timings + " - Verbose: " + verboseTimings + " - Interval: " + SpigotConfig.timeSummary(Timings.getHistoryInterval() / 20) + " - Length: " + SpigotConfig.timeSummary(Timings.getHistoryLength() / 20));
    }

    protected static String timeSummary(int seconds) {
        String time = "";
        if (seconds > 3600) {
            time = String.valueOf(time) + TimeUnit.SECONDS.toHours(seconds) + "h";
            seconds /= 60;
        }
        if (seconds > 0) {
            time = String.valueOf(time) + TimeUnit.SECONDS.toMinutes(seconds) + "m";
        }
        return time;
    }

    private static void nettyThreads() {
        int count = SpigotConfig.getInt("settings.netty-threads", 4);
        System.setProperty("io.netty.eventLoopThreads", Integer.toString(count));
        Bukkit.getLogger().log(java.util.logging.Level.INFO, "Using {0} threads for Netty based IO", count);
    }

    private static void lateBind() {
        lateBind = SpigotConfig.getBoolean("settings.late-bind", false);
    }

    private static void stats() {
        disableStatSaving = SpigotConfig.getBoolean("stats.disable-saving", false);
        if (!config.contains("stats.forced-stats")) {
            config.createSection("stats.forced-stats");
        }
        ConfigurationSection section = config.getConfigurationSection("stats.forced-stats");
        for (String name : section.getKeys(true)) {
            if (!section.isInt(name)) continue;
            forcedStats.put(name, section.getInt(name));
        }
        if (disableStatSaving && section.getInt("achievement.openInventory", 0) < 1) {
            Bukkit.getLogger().warning("*** WARNING *** stats.disable-saving is true but stats.forced-stats.achievement.openInventory isn't set to 1. Disabling stat saving without forcing the achievement may cause it to get stuck on the player's screen.");
        }
    }

    private static void tpsCommand() {
        commands.put("tps", new TicksPerSecondCommand("tps"));
    }

    private static void playerSample() {
        playerSample = SpigotConfig.getInt("settings.sample-count", 12);
        System.out.println("Server Ping Player Sample Count: " + playerSample);
    }

    private static void playerShuffle() {
        playerShuffle = SpigotConfig.getInt("settings.player-shuffle", 0);
    }

    private static void spamExclusions() {
        spamExclusions = SpigotConfig.getList("commands.spam-exclusions", Arrays.asList("/skill"));
    }

    private static void silentCommandBlocks() {
        silentCommandBlocks = SpigotConfig.getBoolean("commands.silent-commandblock-console", false);
    }

    private static void filterCreativeItems() {
        filterCreativeItems = SpigotConfig.getBoolean("settings.filter-creative-items", true);
    }

    private static void replaceCommands() {
        if (config.contains("replace-commands")) {
            SpigotConfig.set("commands.replace-commands", config.getStringList("replace-commands"));
            config.set("replace-commands", null);
        }
        replaceCommands = new HashSet<String>(SpigotConfig.getList("commands.replace-commands", Arrays.asList("setblock", "summon", "testforblock", "tellraw")));
    }

    private static void userCacheCap() {
        userCacheCap = SpigotConfig.getInt("settings.user-cache-size", 1000);
    }

    private static void saveUserCacheOnStopOnly() {
        saveUserCacheOnStopOnly = SpigotConfig.getBoolean("settings.save-user-cache-on-stop-only", false);
    }

    private static void intCacheLimit() {
        intCacheLimit = SpigotConfig.getInt("settings.int-cache-limit", 1024);
    }

    private static void movedWronglyThreshold() {
        movedWronglyThreshold = SpigotConfig.getDouble("settings.moved-wrongly-threshold", 0.0625);
    }

    private static void movedTooQuicklyThreshold() {
        movedTooQuicklyThreshold = SpigotConfig.getDouble("settings.moved-too-quickly-threshold", 100.0);
    }

    private static void attributeMaxes() {
        ((AttributeRanged)GenericAttributes.maxHealth).b = maxHealth = SpigotConfig.getDouble("settings.attribute.maxHealth.max", maxHealth);
        ((AttributeRanged)GenericAttributes.MOVEMENT_SPEED).b = movementSpeed = SpigotConfig.getDouble("settings.attribute.movementSpeed.max", movementSpeed);
        ((AttributeRanged)GenericAttributes.ATTACK_DAMAGE).b = attackDamage = SpigotConfig.getDouble("settings.attribute.attackDamage.max", attackDamage);
    }

    private static void debug() {
        debug = SpigotConfig.getBoolean("settings.debug", false);
        if (debug && !LogManager.getRootLogger().isTraceEnabled()) {
            LoggerContext ctx = (LoggerContext)LogManager.getContext(false);
            Configuration conf = ctx.getConfiguration();
            conf.getLoggerConfig("").setLevel(Level.ALL);
            ctx.updateLoggers(conf);
        }
        if (LogManager.getRootLogger().isTraceEnabled()) {
            Bukkit.getLogger().info("Debug logging is enabled");
        } else {
            Bukkit.getLogger().info("Debug logging is disabled");
        }
    }
}

