/*
 * Decompiled with CFR 0.152.
 */
package org.github.paperspigot;

import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.github.paperspigot.PaperSpigotConfig;

public class PaperSpigotWorldConfig {
    private final String worldName;
    private final YamlConfiguration config;
    private boolean verbose;
    public boolean allowUndeadHorseLeashing;
    public double squidMinSpawnHeight;
    public double squidMaxSpawnHeight;
    public float playerBlockingDamageMultiplier;
    public int cactusMaxHeight;
    public int reedMaxHeight;
    public int fishingMinTicks;
    public int fishingMaxTicks;
    public float blockBreakExhaustion;
    public float playerSwimmingExhaustion;
    public int softDespawnDistance;
    public int hardDespawnDistance;
    public boolean keepSpawnInMemory;
    public int fallingBlockHeightNerf;
    public int tntEntityHeightNerf;
    public int waterOverLavaFlowSpeed;
    public boolean removeInvalidMobSpawnerTEs;
    public boolean removeUnloadedEnderPearls;
    public boolean removeUnloadedTNTEntities;
    public boolean removeUnloadedFallingBlocks;
    public boolean boatsDropBoats;
    public boolean disablePlayerCrits;
    public boolean disableChestCatDetection;
    public boolean netherVoidTopDamage;
    public int tickNextTickCap;
    public boolean tickNextTickListCapIgnoresRedstone;
    public boolean useAsyncLighting;
    public boolean disableEndCredits;
    public boolean loadUnloadedEnderPearls;
    public boolean loadUnloadedTNTEntities;
    public boolean loadUnloadedFallingBlocks;
    public boolean generateCanyon;
    public boolean generateCaves;
    public boolean generateDungeon;
    public boolean generateFortress;
    public boolean generateMineshaft;
    public boolean generateMonument;
    public boolean generateStronghold;
    public boolean generateTemple;
    public boolean generateVillage;
    public boolean generateFlatBedrock;
    public boolean fixCannons;
    public boolean fallingBlocksCollideWithSigns;
    public boolean optimizeExplosions;
    public boolean fastDrainLava;
    public boolean fastDrainWater;
    public int lavaFlowSpeedNormal;
    public int lavaFlowSpeedNether;
    public boolean disableExplosionKnockback;
    public boolean disableThunder;
    public boolean disableIceAndSnow;
    public boolean disableMoodSounds;
    public int mobSpawnerTickRate;
    public boolean cacheChunkMaps;
    public int containerUpdateTickRate;
    public float tntExplosionVolume;
    public boolean useHopperCheck;
    public boolean allChunksAreSlimeChunks;
    public boolean allowBlockLocationTabCompletion;
    public int portalSearchRadius;
    public boolean disableTeleportationSuffocationCheck;

    public PaperSpigotWorldConfig(String worldName) {
        this.worldName = worldName;
        this.config = PaperSpigotConfig.config;
        this.init();
    }

    public void init() {
        this.verbose = this.getBoolean("verbose", true);
        this.log("-------- World Settings For [" + this.worldName + "] --------");
        PaperSpigotConfig.readConfig(PaperSpigotWorldConfig.class, this);
    }

    private void log(String s) {
        if (this.verbose) {
            Bukkit.getLogger().info(s);
        }
    }

    private void set(String path, Object val) {
        this.config.set("world-settings.default." + path, val);
    }

    private boolean getBoolean(String path, boolean def) {
        this.config.addDefault("world-settings.default." + path, def);
        return this.config.getBoolean("world-settings." + this.worldName + "." + path, this.config.getBoolean("world-settings.default." + path));
    }

    private double getDouble(String path, double def) {
        this.config.addDefault("world-settings.default." + path, def);
        return this.config.getDouble("world-settings." + this.worldName + "." + path, this.config.getDouble("world-settings.default." + path));
    }

    private int getInt(String path, int def) {
        this.config.addDefault("world-settings.default." + path, def);
        return this.config.getInt("world-settings." + this.worldName + "." + path, this.config.getInt("world-settings.default." + path));
    }

    private float getFloat(String path, float def) {
        return (float)this.getDouble(path, def);
    }

    private <T> List getList(String path, T def) {
        this.config.addDefault("world-settings.default." + path, def);
        return this.config.getList("world-settings." + this.worldName + "." + path, this.config.getList("world-settings.default." + path));
    }

    private String getString(String path, String def) {
        this.config.addDefault("world-settings.default." + path, def);
        return this.config.getString("world-settings." + this.worldName + "." + path, this.config.getString("world-settings.default." + path));
    }

    private void allowUndeadHorseLeashing() {
        this.allowUndeadHorseLeashing = this.getBoolean("allow-undead-horse-leashing", false);
        this.log("Allow undead horse types to be leashed: " + this.allowUndeadHorseLeashing);
    }

    private void squidSpawnHeight() {
        this.squidMinSpawnHeight = this.getDouble("squid-spawn-height.minimum", 45.0);
        this.squidMaxSpawnHeight = this.getDouble("squid-spawn-height.maximum", 63.0);
        this.log("Squids will spawn between Y: " + this.squidMinSpawnHeight + " and Y: " + this.squidMaxSpawnHeight);
    }

    private void playerBlockingDamageMultiplier() {
        this.playerBlockingDamageMultiplier = this.getFloat("player-blocking-damage-multiplier", 0.5f);
        this.log("Player blocking damage multiplier set to " + this.playerBlockingDamageMultiplier);
    }

    private void blockGrowthHeight() {
        this.cactusMaxHeight = this.getInt("max-growth-height.cactus", 3);
        this.reedMaxHeight = this.getInt("max-growth-height.reeds", 3);
        this.log("Max height for cactus growth " + this.cactusMaxHeight + ". Max height for reed growth " + this.reedMaxHeight);
    }

    private void fishingTickRange() {
        this.fishingMinTicks = this.getInt("fishing-time-range.MinimumTicks", 100);
        this.fishingMaxTicks = this.getInt("fishing-time-range.MaximumTicks", 900);
    }

    private void exhaustionValues() {
        this.blockBreakExhaustion = this.getFloat("player-exhaustion.block-break", 0.025f);
        this.playerSwimmingExhaustion = this.getFloat("player-exhaustion.swimming", 0.015f);
    }

    private void despawnDistances() {
        this.softDespawnDistance = this.getInt("despawn-ranges.soft", 32);
        this.hardDespawnDistance = this.getInt("despawn-ranges.hard", 128);
        if (this.softDespawnDistance > this.hardDespawnDistance) {
            this.softDespawnDistance = this.hardDespawnDistance;
        }
        this.log("Living Entity Despawn Ranges:  Soft: " + this.softDespawnDistance + " Hard: " + this.hardDespawnDistance);
        this.softDespawnDistance *= this.softDespawnDistance;
        this.hardDespawnDistance *= this.hardDespawnDistance;
    }

    private void keepSpawnInMemory() {
        this.keepSpawnInMemory = this.getBoolean("keep-spawn-loaded", true);
        this.log("Keep spawn chunk loaded: " + this.keepSpawnInMemory);
    }

    private void fallingBlockheightNerf() {
        this.fallingBlockHeightNerf = this.getInt("falling-block-height-nerf", 0);
        if (this.fallingBlockHeightNerf != 0) {
            this.log("Falling Block Height Limit set to Y: " + this.fallingBlockHeightNerf);
        }
    }

    private void tntEntityHeightNerf() {
        this.tntEntityHeightNerf = this.getInt("tnt-entity-height-nerf", 0);
        if (this.tntEntityHeightNerf != 0) {
            this.log("TNT Entity Height Limit set to Y: " + this.tntEntityHeightNerf);
        }
    }

    private void waterOverLavaFlowSpeed() {
        this.waterOverLavaFlowSpeed = this.getInt("water-over-lava-flow-speed", 5);
        this.log("Water over lava flow speed: " + this.waterOverLavaFlowSpeed);
    }

    private void removeInvalidMobSpawnerTEs() {
        this.removeInvalidMobSpawnerTEs = this.getBoolean("remove-invalid-mob-spawner-tile-entities", true);
        this.log("Remove invalid mob spawner tile entities: " + this.removeInvalidMobSpawnerTEs);
    }

    private void removeUnloaded() {
        this.removeUnloadedEnderPearls = this.getBoolean("remove-unloaded.enderpearls", true);
        this.removeUnloadedTNTEntities = this.getBoolean("remove-unloaded.tnt-entities", true);
        this.removeUnloadedFallingBlocks = this.getBoolean("remove-unloaded.falling-blocks", true);
    }

    private void mechanicsChanges() {
        this.boatsDropBoats = this.getBoolean("game-mechanics.boats-drop-boats", false);
        this.disablePlayerCrits = this.getBoolean("game-mechanics.disable-player-crits", false);
        this.disableChestCatDetection = this.getBoolean("game-mechanics.disable-chest-cat-detection", false);
    }

    private void nethervoidTopDamage() {
        this.netherVoidTopDamage = this.getBoolean("nether-ceiling-void-damage", false);
    }

    private void tickNextTickCap() {
        this.tickNextTickCap = this.getInt("tick-next-tick-list-cap", 10000);
        this.tickNextTickListCapIgnoresRedstone = this.getBoolean("tick-next-tick-list-cap-ignores-redstone", false);
        this.log("WorldServer TickNextTick cap set at " + this.tickNextTickCap);
        this.log("WorldServer TickNextTickList cap always processes redstone: " + this.tickNextTickListCapIgnoresRedstone);
    }

    private void useAsyncLighting() {
        this.useAsyncLighting = this.getBoolean("use-async-lighting", false);
        this.log("World async lighting: " + this.useAsyncLighting);
    }

    private void disableEndCredits() {
        this.disableEndCredits = this.getBoolean("game-mechanics.disable-end-credits", false);
    }

    private void loadUnloaded() {
        this.loadUnloadedEnderPearls = this.getBoolean("load-chunks.enderpearls", false);
        this.loadUnloadedTNTEntities = this.getBoolean("load-chunks.tnt-entities", false);
        this.loadUnloadedFallingBlocks = this.getBoolean("load-chunks.falling-blocks", false);
    }

    private void generatorSettings() {
        this.generateCanyon = this.getBoolean("generator-settings.canyon", true);
        this.generateCaves = this.getBoolean("generator-settings.caves", true);
        this.generateDungeon = this.getBoolean("generator-settings.dungeon", true);
        this.generateFortress = this.getBoolean("generator-settings.fortress", true);
        this.generateMineshaft = this.getBoolean("generator-settings.mineshaft", true);
        this.generateMonument = this.getBoolean("generator-settings.monument", true);
        this.generateStronghold = this.getBoolean("generator-settings.stronghold", true);
        this.generateTemple = this.getBoolean("generator-settings.temple", true);
        this.generateVillage = this.getBoolean("generator-settings.village", true);
        this.generateFlatBedrock = this.getBoolean("generator-settings.flat-bedrock", false);
    }

    private void fixCannons() {
        if (PaperSpigotConfig.version < 9) {
            boolean value = this.config.getBoolean("world-settings.default.fix-cannons", false);
            if (!value) {
                value = this.config.getBoolean("world-settings.default.tnt-gameplay.fix-directional-bias", false);
            }
            if (!value) {
                boolean bl = value = !this.config.getBoolean("world-settings.default.tnt-gameplay.moves-in-water", true);
            }
            if (!value) {
                value = this.config.getBoolean("world-settings.default.tnt-gameplay.legacy-explosion-height", false);
            }
            if (value) {
                this.config.set("world-settings.default.fix-cannons", true);
            }
            if (this.config.contains("world-settings.default.tnt-gameplay")) {
                this.config.getDefaults().set("world-settings.default.tnt-gameplay", null);
                this.config.set("world-settings.default.tnt-gameplay", null);
            }
            if (!(value = this.config.getBoolean("world-settings." + this.worldName + ".fix-cannons", false))) {
                value = this.config.getBoolean("world-settings." + this.worldName + ".tnt-gameplay.fix-directional-bias", false);
            }
            if (!value) {
                boolean bl = value = !this.config.getBoolean("world-settings." + this.worldName + ".tnt-gameplay.moves-in-water", true);
            }
            if (!value) {
                value = this.config.getBoolean("world-settings." + this.worldName + ".tnt-gameplay.legacy-explosion-height", false);
            }
            if (value) {
                this.config.set("world-settings." + this.worldName + ".fix-cannons", true);
            }
            if (this.config.contains("world-settings." + this.worldName + ".tnt-gameplay")) {
                this.config.getDefaults().set("world-settings." + this.worldName + ".tnt-gameplay", null);
                this.config.set("world-settings." + this.worldName + ".tnt-gameplay", null);
            }
        }
        this.fixCannons = this.getBoolean("fix-cannons", false);
        this.log("Fix TNT cannons: " + this.fixCannons);
    }

    private void fallingBlocksCollideWithSigns() {
        this.fallingBlocksCollideWithSigns = this.getBoolean("falling-blocks-collide-with-signs", false);
    }

    private void optimizeExplosions() {
        this.optimizeExplosions = this.getBoolean("optimize-explosions", false);
    }

    private void fastDraining() {
        this.fastDrainLava = this.getBoolean("fast-drain.lava", false);
        this.fastDrainWater = this.getBoolean("fast-drain.water", false);
    }

    private void lavaFlowSpeed() {
        this.lavaFlowSpeedNormal = this.getInt("lava-flow-speed.normal", 30);
        this.lavaFlowSpeedNether = this.getInt("lava-flow-speed.nether", 10);
    }

    private void disableExplosionKnockback() {
        this.disableExplosionKnockback = this.getBoolean("disable-explosion-knockback", false);
    }

    private void disableThunder() {
        this.disableThunder = this.getBoolean("disable-thunder", false);
    }

    private void disableIceAndSnow() {
        this.disableIceAndSnow = this.getBoolean("disable-ice-and-snow", false);
    }

    private void disableMoodSounds() {
        this.disableMoodSounds = this.getBoolean("disable-mood-sounds", false);
    }

    private void mobSpawnerTickRate() {
        this.mobSpawnerTickRate = this.getInt("mob-spawner-tick-rate", 1);
    }

    private void cacheChunkMaps() {
        this.cacheChunkMaps = this.getBoolean("cache-chunk-maps", false);
    }

    private void containerUpdateTickRate() {
        this.containerUpdateTickRate = this.getInt("container-update-tick-rate", 1);
    }

    private void tntExplosionVolume() {
        this.tntExplosionVolume = this.getFloat("tnt-explosion-volume", 4.0f);
    }

    private void useHopperCheck() {
        this.useHopperCheck = this.getBoolean("use-hopper-check", false);
    }

    private void allChunksAreSlimeChunks() {
        this.allChunksAreSlimeChunks = this.getBoolean("all-chunks-are-slime-chunks", false);
    }

    private void allowBlockLocationTabCompletion() {
        this.allowBlockLocationTabCompletion = this.getBoolean("allow-block-location-tab-completion", true);
    }

    private void portalSearchRadius() {
        this.portalSearchRadius = this.getInt("portal-search-radius", 128);
    }

    private void disableTeleportationSuffocationCheck() {
        this.disableTeleportationSuffocationCheck = this.getBoolean("disable-teleportation-suffocation-check", false);
    }
}

