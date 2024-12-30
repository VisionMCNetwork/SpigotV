package rip.visionmc.spigotv;

import com.google.common.base.Throwables;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.logging.Level;

import lombok.Getter;
import lombok.Setter;

import rip.visionmc.spigotv.knockback.CraftKnockbackProfile;
import rip.visionmc.spigotv.knockback.KnockbackProfile;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

@Getter
@Setter
public class SpigotVConfig {

    private static final String HEADER = "This is the main configuration file for SpigotV.\n"
                                         + "Modify with caution, and make sure you know what you are doing.\n";

    private File configFile;
    private YamlConfiguration config;

    private KnockbackProfile currentKb;
    private Set<KnockbackProfile> kbProfiles = new HashSet<>();

    private float potionVerticalOffset;
    private float potionMultiplier;
    private float potionGravity;
	
    private boolean hidePlayersFromTab;
    private boolean firePlayerMoveEvent;
    private boolean fireLeftClickAir;
    private boolean fireLeftClickBlock;
    private boolean LeftClickBlock;
    private boolean entityActivation;
    private boolean invalidArmAnimationKick;
    private boolean mobAIEnabled;
    private boolean baseVersionEnabled;
    private boolean doChunkUnload;
    private boolean blockOperations;
    private boolean disableJoinMessage;
    private boolean disableLeaveMessage;

    public SpigotVConfig() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream is = classLoader.getResourceAsStream("version.properties");
        Properties prop = new Properties();

        try {
            prop.load(is);

            SpigotVBridge.version = (String) prop.getOrDefault("version", "Unknown");
        }
        catch (IOException io) {
            io.printStackTrace();
        }
        finally {
            if (is != null) {
                try {
                    is.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        this.configFile = new File("settings.yml");
        this.config = new YamlConfiguration();

        try {
            config.load(this.configFile);
        } catch (IOException ex) {
            System.out.println("Generating a new settings.yml file.");
        } catch (InvalidConfigurationException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not load settings.yml, please correct your syntax errors", ex);
            throw Throwables.propagate(ex);
        }

        this.config.options().header(SpigotVConfig.HEADER);
        this.config.options().copyDefaults(true);

        this.loadConfig();
    }

    private void loadConfig() {
        final KnockbackProfile defaultProfile = new CraftKnockbackProfile("Default");

        this.kbProfiles = new HashSet<>();
        this.kbProfiles.add(defaultProfile);

        for (String key : this.getKeys("knockback.profiles")) {
            final String path = "knockback.profiles." + key;
            CraftKnockbackProfile profile = (CraftKnockbackProfile) getKbProfileByName(key);

            if (profile == null) {
                profile = new CraftKnockbackProfile(key);
                this.kbProfiles.add(profile);
            }

	    profile.setHorizontal(this.getDouble(path + ".horizontal", 0.9055D));
	    profile.setVertical(this.getDouble(path + ".vertical", 0.25635D));
	    profile.setMinRange(this.getDouble(path + ".minRange", 0.12D));
	    profile.setMaxRange(this.getDouble(path + ".maxRange", 1.2D));
	    profile.setStartRange(this.getDouble(path + ".startRange", 3.0D));
	    profile.setRangeFactor(this.getDouble(path + ".rangeFactor", 0.025D));
	    profile.setHorizontalFriction(this.getDouble(path + ".horizontalFriction", 1.0D));
	    profile.setVerticalFriction(this.getDouble(path + ".verticalFriction", 60.0D));
	}

        this.currentKb = this.getKbProfileByName(this.getString("knockback.current", "default"));

        if (this.currentKb == null) {
            this.currentKb = defaultProfile;
        }

        this.hidePlayersFromTab = this.getBoolean("hide-players-from-tab", true);
        this.firePlayerMoveEvent = this.getBoolean("fire-player-move-event", false);
        this.fireLeftClickAir = this.getBoolean("fire-left-click-air", false);
        this.fireLeftClickBlock = this.getBoolean("fire-left-click-block", false);
        this.entityActivation = this.getBoolean("entity-activation", false);
        this.invalidArmAnimationKick = this.getBoolean("invalid-arm-animation-kick", false);
        this.mobAIEnabled = this.getBoolean("mob-ai", false);
        this.baseVersionEnabled = this.getBoolean("1-8-enabled", false);
        this.doChunkUnload = this.getBoolean("do-chunk-unload", true);
        this.blockOperations = this.getBoolean("block-operations", false);
        this.disableJoinMessage = this.getBoolean("disable-join-message", true);
        this.disableLeaveMessage = this.getBoolean("disable-leave-message", true);
        SpigotVBridge.disableOpPermissions = this.getBoolean("disable-op", false);
        this.potionVerticalOffset = this.getFloat("potion-vertical-offset", (float) -15.0);
        this.potionMultiplier = this.getFloat("potion-multiplier", (float) 0.5);
        this.potionGravity = this.getFloat("potion-gravity", (float) 0.03);
        try {
            this.config.save(this.configFile);
        } catch (IOException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not save " + this.configFile, ex);
        }
    }

    public KnockbackProfile getCurrentKb() {
        return this.currentKb;
    }

    public void setCurrentKb(KnockbackProfile kb) {
        this.currentKb = kb;
    }

    public KnockbackProfile getKbProfileByName(String name) {
        for (KnockbackProfile profile : this.kbProfiles) {
            if (profile.getName().equalsIgnoreCase(name)) {
                return profile;
            }
        }

        return null;
    }

    public Set<KnockbackProfile> getKbProfiles() {
        return this.kbProfiles;
    }

    public void save() {
        try {
            this.config.save(this.configFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void set(String path, Object val) {
        this.config.set(path, val);

        try {
            this.config.save(this.configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Set<String> getKeys(String path) {
        if (!this.config.isConfigurationSection(path)) {
            this.config.createSection(path);
            return new HashSet<>();
        }

        return this.config.getConfigurationSection(path).getKeys(false);
    }

    public boolean getBoolean(String path, boolean def) {
        this.config.addDefault(path, def);
        return this.config.getBoolean(path, this.config.getBoolean(path));
    }

    public double getDouble(String path, double def) {
        this.config.addDefault(path, def);
        return this.config.getDouble(path, this.config.getDouble(path));
    }

    public float getFloat(String path, float def) {
        return (float) this.getDouble(path, (double) def);
    }

    public int getInt(String path, int def) {
        this.config.addDefault(path, def);
        return config.getInt(path, this.config.getInt(path));
    }

    public <T> List getList(String path, T def) {
        this.config.addDefault(path, def);
        return this.config.getList(path, this.config.getList(path));
    }

    public String getString(String path, String def) {
        this.config.addDefault(path, def);
        return this.config.getString(path, this.config.getString(path));
    }

}
