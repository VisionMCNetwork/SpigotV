# VisionMC SpigotX Fork - SpigotV
 
- Practice Server Spigot with the following:
  - Customizable potions
  - Range Reduction Knockback
  - Full Java 17 Support 

# Fixes

- Patches

```
[SpigotV-0001] Add-range-reduction.patch
[SpigotV-0002] Change-knockback-math.patch
[SpigotV-0003] Fix-maven-repositories.patch
[SpigotV-0004] Update-libraries.patch

[GoatSpigot-????] Custom-tps-command.patch

[KewlSpigot-????] Add-potion-and-inventory-events.patch
[KewlSpigot-????] Add-separate-friction-values.patch
[KewlSpigot-????] Add-configurable-potions.patch
[KewlSpigot-????] Add-chunk-snapshot-api.patch
[KewlSpigot-????] Optimize-imports.patch

[FoxSpigot-????] Improve-hit-registration.patch

[PandaSpigot-0010] Avoid blocking on network manager creation
[PandaSpigot-0021] Set-cap-on-JDK-per-thread-native-byte-buffer-cache.patch
[PandaSpigot-0024] Player-Chunk-Load-Unload-Events.patch
[PandaSpigot-0027] Use-a-Shared-Random-for-Entities.patch
[PandaSpigot-0028] Reduce-IO-ops-opening-a-new-region-file.patch
[PandaSpigot-0031] Optimize-BlockPosition-helper-methods.patch
[PandaSpigot-0034] Cache-user-authenticator-threads.patch
[PandaSpigot-0035] Optimize-Network-Queue.patch
[PandaSpigot-0038] Optimize-VarInt-reading-and-writing.patch


[KigPaper-0128] Fix-Entity-and-Command-Block-memory-leaks.patch
[KigPaper-0129] Fix-more-EnchantmentManager-leaks.patch
[KigPaper-0138] Fix-some-more-memory-leaks.patch
[KigPaper-0148] Fix-chunk-leak-on-world-unload.patch
[KigPaper-0162] Debloat-join-leave-messages.patch

[Akarin-0001] Avoid-double-I/O-operation-on-load-player-file.patch

[IonSpigot-0006] Fix-Chunk-Loading.patch

[Paper-0033] Optimize-explosions.patch
[Paper-0102] update-log4j.patch
[Paper-0125] Improve Maps-in-item-frames-performance-and-bug-fixes.patch
[Paper-0164] [MC-117075]-TE-Unload-Lag-Spike.patch
[Paper-0301] Optimize-Region-File-Cache.patch
[Paper-0321]-Cleanup-allocated-favicon-ByteBuf.patch
[Paper-0451] Reduce-memory-footprint-of-NBTTagCompound.patch

[TacoSpigot-0018] Limit-the-length-of-buffered-bytes-to-read.patch
```

# How stable is it?

- Performance
  - 20.00 TPS with 150+ moving and walking bots online (3GB and 4 cores allocated, this was a bare minimum host)
  - KB and potions don't change and stay smooth under large stress
