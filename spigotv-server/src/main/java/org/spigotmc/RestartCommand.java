/*
 * Decompiled with CFR 0.152.
 */
package org.spigotmc;

import java.io.File;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.spigotmc.AsyncCatcher;
import org.spigotmc.SpigotConfig;
import org.spigotmc.WatchdogThread;

public class RestartCommand
extends Command {
    public RestartCommand(String name) {
        super(name);
        this.description = "Restarts the server";
        this.usageMessage = "/restart";
        this.setPermission("bukkit.command.restart");
    }

    @Override
    public boolean execute(CommandSender sender, String currentAlias, String[] args) {
        if (this.testPermission(sender)) {
            MinecraftServer.getServer().processQueue.add(new Runnable(){

                @Override
                public void run() {
                    RestartCommand.restart();
                }
            });
        }
        return true;
    }

    public static void restart() {
        RestartCommand.restart(new File(SpigotConfig.restartScript));
    }

    public static void restart(final File script) {
        AsyncCatcher.enabled = false;
        try {
            if (script.isFile()) {
                System.out.println("Attempting to restart with " + SpigotConfig.restartScript);
                WatchdogThread.doStop();
                for (EntityPlayer p : MinecraftServer.getServer().getPlayerList().players) {
                    p.playerConnection.disconnect(SpigotConfig.restartMessage);
                }
                try {
                    Thread.sleep(100L);
                }
                catch (InterruptedException interruptedException) {}
                MinecraftServer.getServer().getServerConnection().b();
                try {
                    Thread.sleep(100L);
                }
                catch (InterruptedException interruptedException) {}
                try {
                    MinecraftServer.getServer().stop();
                }
                catch (Throwable throwable) {}
                Thread shutdownHook = new Thread(){

                    @Override
                    public void run() {
                        try {
                            String os = System.getProperty("os.name").toLowerCase();
                            if (os.contains("win")) {
                                Runtime.getRuntime().exec("cmd /c start " + script.getPath());
                            } else {
                                Runtime.getRuntime().exec(new String[]{"sh", script.getPath()});
                            }
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                shutdownHook.setDaemon(true);
                Runtime.getRuntime().addShutdownHook(shutdownHook);
            } else {
                System.out.println("Startup script '" + SpigotConfig.restartScript + "' does not exist! Stopping server.");
            }
            System.exit(0);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

