/*
 * Decompiled with CFR 0.152.
 */
package org.spigotmc;

import rip.visionmc.spigotv.SpigotV;
import co.aikar.timings.SpigotTimings;
import java.util.List;
import net.minecraft.server.AxisAlignedBB;
import net.minecraft.server.Chunk;
import net.minecraft.server.Entity;
import net.minecraft.server.EntityAmbient;
import net.minecraft.server.EntityAnimal;
import net.minecraft.server.EntityArrow;
import net.minecraft.server.EntityComplexPart;
import net.minecraft.server.EntityCreature;
import net.minecraft.server.EntityCreeper;
import net.minecraft.server.EntityEnderCrystal;
import net.minecraft.server.EntityEnderDragon;
import net.minecraft.server.EntityFallingBlock;
import net.minecraft.server.EntityFireball;
import net.minecraft.server.EntityFireworks;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityLiving;
import net.minecraft.server.EntityMonster;
import net.minecraft.server.EntityProjectile;
import net.minecraft.server.EntitySheep;
import net.minecraft.server.EntitySlime;
import net.minecraft.server.EntityTNTPrimed;
import net.minecraft.server.EntityVillager;
import net.minecraft.server.EntityWeather;
import net.minecraft.server.EntityWither;
import net.minecraft.server.MathHelper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.World;
import org.spigotmc.SpigotWorldConfig;

public class ActivationRange {
    static AxisAlignedBB maxBB = AxisAlignedBB.a(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
    static AxisAlignedBB miscBB = AxisAlignedBB.a(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
    static AxisAlignedBB animalBB = AxisAlignedBB.a(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
    static AxisAlignedBB monsterBB = AxisAlignedBB.a(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);

    public static byte initializeEntityActivationType(Entity entity) {
        if (entity instanceof EntityMonster || entity instanceof EntitySlime) {
            return 1;
        }
        if (entity instanceof EntityCreature || entity instanceof EntityAmbient) {
            return 2;
        }
        return 3;
    }

    public static boolean initializeEntityActivationState(Entity entity, SpigotWorldConfig config) {
        return entity.activationType == 3 && config.miscActivationRange == 0 || entity.activationType == 2 && config.animalActivationRange == 0 || entity.activationType == 1 && config.monsterActivationRange == 0 || entity instanceof EntityHuman || entity instanceof EntityProjectile || entity instanceof EntityEnderDragon || entity instanceof EntityComplexPart || entity instanceof EntityWither || entity instanceof EntityFireball || entity instanceof EntityWeather || entity instanceof EntityTNTPrimed || entity instanceof EntityFallingBlock || entity instanceof EntityEnderCrystal || entity instanceof EntityFireworks;
    }

    public static void activateEntities(World world) {
        if (!SpigotV.INSTANCE.getConfig().isEntityActivation()) {
            return;
        }
        SpigotTimings.entityActivationCheckTimer.startTiming();
        int miscActivationRange = world.spigotConfig.miscActivationRange;
        int animalActivationRange = world.spigotConfig.animalActivationRange;
        int monsterActivationRange = world.spigotConfig.monsterActivationRange;
        int maxRange = Math.max(monsterActivationRange, animalActivationRange);
        maxRange = Math.max(maxRange, miscActivationRange);
        maxRange = Math.min((world.spigotConfig.viewDistance << 4) - 8, maxRange);
        for (Entity entity : world.players) {
            entity.activatedTick = MinecraftServer.currentTick;
            maxBB = entity.getBoundingBox().grow(maxRange, 256.0, maxRange);
            miscBB = entity.getBoundingBox().grow(miscActivationRange, 256.0, miscActivationRange);
            animalBB = entity.getBoundingBox().grow(animalActivationRange, 256.0, animalActivationRange);
            monsterBB = entity.getBoundingBox().grow(monsterActivationRange, 256.0, monsterActivationRange);
            int i = MathHelper.floor(ActivationRange.maxBB.a / 16.0);
            int j = MathHelper.floor(ActivationRange.maxBB.d / 16.0);
            int k = MathHelper.floor(ActivationRange.maxBB.c / 16.0);
            int l = MathHelper.floor(ActivationRange.maxBB.f / 16.0);
            int i1 = i;
            while (i1 <= j) {
                int j1 = k;
                while (j1 <= l) {
                    if (world.getWorld().isChunkLoaded(i1, j1)) {
                        ActivationRange.activateChunkEntities(world.getChunkAt(i1, j1));
                    }
                    ++j1;
                }
                ++i1;
            }
        }
        SpigotTimings.entityActivationCheckTimer.stopTiming();
    }

    private static void activateChunkEntities(Chunk chunk) {
        List<Entity>[] listArray = chunk.entitySlices;
        int n = chunk.entitySlices.length;
        int n2 = 0;
        while (n2 < n) {
            List<Entity> slice = listArray[n2];
            block5: for (Entity entity : slice) {
                if ((long)MinecraftServer.currentTick <= entity.activatedTick) continue;
                if (entity.defaultActivationState) {
                    entity.activatedTick = MinecraftServer.currentTick;
                    continue;
                }
                switch (entity.activationType) {
                    case 1: {
                        if (!monsterBB.b(entity.getBoundingBox())) continue block5;
                        entity.activatedTick = MinecraftServer.currentTick;
                        break;
                    }
                    case 2: {
                        if (!animalBB.b(entity.getBoundingBox())) continue block5;
                        entity.activatedTick = MinecraftServer.currentTick;
                        break;
                    }
                    default: {
                        if (!miscBB.b(entity.getBoundingBox())) continue block5;
                        entity.activatedTick = MinecraftServer.currentTick;
                    }
                }
            }
            ++n2;
        }
    }

    public static boolean checkEntityImmunities(Entity entity) {
        if (entity.inWater || entity.fireTicks > 0) {
            return true;
        }
        if (!(entity instanceof EntityArrow) ? !entity.onGround || entity.passenger != null || entity.vehicle != null : !((EntityArrow)entity).inGround) {
            return true;
        }
        if (entity instanceof EntityLiving) {
            EntityLiving living = (EntityLiving)entity;
            if (living.hurtTicks > 0 || living.effects.size() > 0) {
                return true;
            }
            if (entity instanceof EntityCreature && ((EntityCreature)entity).getGoalTarget() != null) {
                return true;
            }
            if (entity instanceof EntityVillager && ((EntityVillager)entity).cm()) {
                return true;
            }
            if (entity instanceof EntityAnimal) {
                EntityAnimal animal = (EntityAnimal)entity;
                if (animal.isBaby() || animal.isInLove()) {
                    return true;
                }
                if (entity instanceof EntitySheep && ((EntitySheep)entity).isSheared()) {
                    return true;
                }
            }
            if (entity instanceof EntityCreeper && ((EntityCreeper)entity).cn()) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkIfActive(Entity entity) {
        boolean isActive;
        SpigotTimings.checkIfActiveTimer.startTiming();
        if (!entity.isAddedToChunk() || entity instanceof EntityFireworks || entity.loadChunks) {
            SpigotTimings.checkIfActiveTimer.stopTiming();
            return true;
        }
        boolean bl = isActive = entity.activatedTick >= (long)MinecraftServer.currentTick || entity.defaultActivationState;
        if (!isActive) {
            if (((long)MinecraftServer.currentTick - entity.activatedTick - 1L) % 20L == 0L) {
                if (ActivationRange.checkEntityImmunities(entity)) {
                    entity.activatedTick = MinecraftServer.currentTick + 20;
                }
                isActive = true;
            }
        } else if (!entity.defaultActivationState && entity.ticksLived % 4 == 0 && !ActivationRange.checkEntityImmunities(entity)) {
            isActive = false;
        }
        int x = MathHelper.floor(entity.locX);
        int z = MathHelper.floor(entity.locZ);
        Chunk chunk = entity.world.getChunkIfLoaded(x >> 4, z >> 4);
        if (isActive && (chunk == null || !chunk.areNeighborsLoaded(1))) {
            isActive = false;
        }
        SpigotTimings.checkIfActiveTimer.stopTiming();
        return isActive;
    }
}

