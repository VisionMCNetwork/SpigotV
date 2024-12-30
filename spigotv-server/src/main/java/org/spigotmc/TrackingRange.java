/*
 * Decompiled with CFR 0.152.
 */
package org.spigotmc;

import net.minecraft.server.Entity;
import net.minecraft.server.EntityExperienceOrb;
import net.minecraft.server.EntityGhast;
import net.minecraft.server.EntityItem;
import net.minecraft.server.EntityItemFrame;
import net.minecraft.server.EntityPainting;
import net.minecraft.server.EntityPlayer;
import org.spigotmc.SpigotWorldConfig;

public class TrackingRange {
    public static int getEntityTrackingRange(Entity entity, int defaultRange) {
        SpigotWorldConfig config = entity.world.spigotConfig;
        if (entity instanceof EntityPlayer) {
            return config.playerTrackingRange;
        }
        if (entity.activationType == 1) {
            return config.monsterTrackingRange;
        }
        if (entity instanceof EntityGhast) {
            if (config.monsterTrackingRange > config.monsterActivationRange) {
                return config.monsterTrackingRange;
            }
            return config.monsterActivationRange;
        }
        if (entity.activationType == 2) {
            return config.animalTrackingRange;
        }
        if (entity instanceof EntityItemFrame || entity instanceof EntityPainting || entity instanceof EntityItem || entity instanceof EntityExperienceOrb) {
            return config.miscTrackingRange;
        }
        return config.otherTrackingRange;
    }
}

