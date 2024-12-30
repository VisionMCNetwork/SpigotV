package org.spigotmc;

import rip.visionmc.spigotv.util.DateUtil;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.DedicatedServer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class TicksPerSecondCommand extends Command {

	public TicksPerSecondCommand(String name) {
		super(name);
		this.description = "Gets the current ticks per second for the server";
		this.usageMessage = "/tps";
		this.setPermission("bukkit.command.tps");
	}

	private static String format(double tps) {
		return (((tps > 18.0) ? ChatColor.GREEN : (tps > 16.0) ? ChatColor.YELLOW : ChatColor.RED))
		+ (((tps > 20.0) ? "*" : "") + Math.min(Math.round(tps * 100.0) / 100.0, 20.0));
	}
	
	@Override
	public boolean execute(CommandSender sender, String currentAlias, String[] args) {
		if (!testPermission(sender)) {
			return true;
		}

		double[] tps = org.bukkit.Bukkit.spigot().getTPS();
		String[] tpsAvg = new String[tps.length];

		for (int i = 0; i < tps.length; i++) {
			tpsAvg[i] = format(tps[i]);
		}

		final long usedMemory = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576L;
		final long allocatedMemory = Runtime.getRuntime().totalMemory() / 1048576L;

		sender.sendMessage("" + ChatColor.GRAY + ChatColor.STRIKETHROUGH + "----------------------------------------------");
		sender.sendMessage(ChatColor.GOLD + "TPS (1m, 5m, 15m): " + org.apache.commons.lang.StringUtils.join(tpsAvg, ", "));
		sender.sendMessage(ChatColor.GOLD + "Memory: " + ChatColor.GREEN + usedMemory + "/" + allocatedMemory + " MB");
		sender.sendMessage(ChatColor.GOLD + "Online: " + ChatColor.GREEN + org.bukkit.Bukkit.getOnlinePlayers().size() + "/" + org.bukkit.Bukkit.getMaxPlayers());
		sender.sendMessage(ChatColor.GOLD + "Last Tick Time: " + ChatColor.GREEN + (System.currentTimeMillis() - MinecraftServer.LAST_TICK_TIME) + "ms");
		sender.sendMessage("" + ChatColor.GRAY + ChatColor.STRIKETHROUGH + "----------------------------------------------");

		return true;
	}
}
