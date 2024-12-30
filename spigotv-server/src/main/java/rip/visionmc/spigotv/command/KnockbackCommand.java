package rip.visionmc.spigotv.command;

import org.bukkit.ChatColor;
import rip.visionmc.spigotv.SpigotV;
import rip.visionmc.spigotv.knockback.CraftKnockbackProfile;
import rip.visionmc.spigotv.knockback.KnockbackProfile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class KnockbackCommand extends Command {

    public KnockbackCommand() {
        super("knockback");
		this.description = "Gets the knockback information for the server";
		this.setPermission("bukkit.command.knockback");
        this.setAliases(Collections.singletonList("kb"));
        this.setUsage(StringUtils.join(new String[]{
                " ",
                ChatColor.YELLOW + "Knockback Commands:",
                " ",
                ChatColor.GOLD + "/kb list" + ChatColor.WHITE + " - " + ChatColor.YELLOW + "List all profiles",
                ChatColor.GOLD + "/kb create <name>" + ChatColor.WHITE + " - " + ChatColor.YELLOW + "Create new profile",
                ChatColor.GOLD + "/kb delete <name>" + ChatColor.WHITE + " - " + ChatColor.YELLOW + "Delete a profile",
                ChatColor.GOLD + "/kb load <name>" + ChatColor.WHITE + " - " + ChatColor.YELLOW + "Load existing profile",
                ChatColor.GOLD + "/kb horizontal <name> <double>" + ChatColor.WHITE + " - " + ChatColor.YELLOW + "Set horizontal",
                ChatColor.GOLD + "/kb vertical <name> <double>" + ChatColor.WHITE + " - " + ChatColor.YELLOW + "Set vertical",
                ChatColor.GOLD + "/kb minrange <name> <double>" + ChatColor.WHITE + " - " + ChatColor.YELLOW + "Set min range",
                ChatColor.GOLD + "/kb maxrange <name> <double>" + ChatColor.WHITE + " - " + ChatColor.YELLOW + "Set max range",
                ChatColor.GOLD + "/kb startrange <name> <double>" + ChatColor.WHITE + " - " + ChatColor.YELLOW + "Set start range",
                ChatColor.GOLD + "/kb rangefactor <name> <double>" + ChatColor.WHITE + " - " + ChatColor.YELLOW + "Set range factor",
		" ",
        }, "\n"));
    }

    @Override
    public boolean execute(CommandSender sender, String alias, String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "Unknown command.");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(usageMessage);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "list": {
                List<String> messages = new ArrayList<>();

                for (KnockbackProfile profile : SpigotV.INSTANCE.getConfig().getKbProfiles()) {
                    boolean current = SpigotV.INSTANCE.getConfig().getCurrentKb().getName().equals(profile.getName());

                    messages.add(ChatColor.GOLD + profile.getName() + (current ? ChatColor.WHITE + " [Active]" : ""));

                    for (String value : profile.getValues()) {
                        messages.add(ChatColor.YELLOW + " * " + value);
                    }
                }

                sender.sendMessage("");
                sender.sendMessage(ChatColor.GOLD + "Knockback Profiles:");
                sender.sendMessage("");
                sender.sendMessage(StringUtils.join(messages, "\n"));
                sender.sendMessage("");
            }
            break;
            case "create": {
                if (args.length > 1) {
                    String name = args[1];

                    for (KnockbackProfile profile : SpigotV.INSTANCE.getConfig().getKbProfiles()) {
                        if (profile.getName().equalsIgnoreCase(name)) {
                            sender.sendMessage(ChatColor.RED + "A knockback profile with that name already exists.");
                            return true;
                        }
                    }

                    CraftKnockbackProfile profile = new CraftKnockbackProfile(name);

                    profile.save();

                    SpigotV.INSTANCE.getConfig().getKbProfiles().add(profile);

                    sender.sendMessage(ChatColor.GOLD + "You created a new profile " + ChatColor.YELLOW + name + ChatColor.GOLD + ".");
                } else {
                    sender.sendMessage(ChatColor.GOLD + "Usage: /kb create <name>");
                }
            }
            break;
            case "delete": {
                if (args.length > 1) {
                    final String name = args[1];

                    if (SpigotV.INSTANCE.getConfig().getCurrentKb().getName().equalsIgnoreCase(name)) {
                        sender.sendMessage(ChatColor.RED + "You cannot delete the profile that is being used.");
                        return true;
                    } else {
                        if (SpigotV.INSTANCE.getConfig().getKbProfiles().removeIf(profile -> profile.getName().equalsIgnoreCase(name))) {
                            SpigotV.INSTANCE.getConfig().set("knockback.profiles." + name, null);
                            sender.sendMessage(ChatColor.RED + "You deleted the profile " + ChatColor.YELLOW + name + ChatColor.RED + ".");
                        } else {
                            sender.sendMessage(ChatColor.RED + "A profile with that name could not be found.");
                        }

                        return true;
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Usage: /kb delete <name>");
                }
            }
            break;
            case "load": {
                if (args.length > 1) {
                    KnockbackProfile profile = SpigotV.INSTANCE.getConfig().getKbProfileByName(args[1]);

                    if (profile == null) {
                        sender.sendMessage(ChatColor.RED + "A profile with that name could not be found.");
                        return true;
                    }

                    SpigotV.INSTANCE.getConfig().setCurrentKb(profile);
                    SpigotV.INSTANCE.getConfig().set("knockback.current", profile.getName());
                    SpigotV.INSTANCE.getConfig().save();

                    sender.sendMessage(ChatColor.GOLD + "You loaded the profile " + ChatColor.YELLOW + profile.getName() + ChatColor.GOLD + ".");
                    return true;
                }
            }
            break;
            case "horizontal": {
                if (args.length == 3 && NumberUtils.isNumber(args[2])) {
                    KnockbackProfile profile = SpigotV.INSTANCE.getConfig().getKbProfileByName(args[1]);

                    if (profile == null) {
                        sender.sendMessage(ChatColor.RED + "A profile with that name could not be found.");
                        return true;
                    }

                    profile.setHorizontal(Double.parseDouble(args[2]));
                    profile.save();

                    sender.sendMessage(ChatColor.GOLD + "You have updated " + ChatColor.YELLOW + profile.getName() + ChatColor.GOLD + "'s values to:");

                    for (String value : profile.getValues()) {
                        sender.sendMessage(ChatColor.YELLOW + "* " + value);
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Wrong syntax.");
                }
            }
            break;
            case "vertical": {
                if (args.length == 3 && NumberUtils.isNumber(args[2])) {
                    KnockbackProfile profile = SpigotV.INSTANCE.getConfig().getKbProfileByName(args[1]);

                    if (profile == null) {
                        sender.sendMessage(ChatColor.RED + "A profile with that name could not be found.");
                        return true;
                    }

                    profile.setVertical(Double.parseDouble(args[2]));
                    profile.save();

                    sender.sendMessage(ChatColor.GOLD + "You have updated " + ChatColor.YELLOW + profile.getName() + ChatColor.GOLD + "'s values to:");

                    for (String value : profile.getValues()) {
                        sender.sendMessage(ChatColor.YELLOW + "* " + value);
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Wrong syntax.");
                }
            }
            break;
            case "minrange": {
                if (args.length == 3 && NumberUtils.isNumber(args[2])) {
                    KnockbackProfile profile = SpigotV.INSTANCE.getConfig().getKbProfileByName(args[1]);

                    if (profile == null) {
                        sender.sendMessage(ChatColor.RED + "A profile with that name could not be found.");
                        return true;
                    }

                    profile.setMinRange(Double.parseDouble(args[2]));
                    profile.save();

                    sender.sendMessage(ChatColor.GOLD + "You have updated " + ChatColor.YELLOW + profile.getName() + ChatColor.GOLD + "'s values to:");

                    for (String value : profile.getValues()) {
                        sender.sendMessage(ChatColor.YELLOW + "* " + value);
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Wrong syntax.");
                }
            }
            break;
            case "maxrange": {
                if (args.length == 3 && NumberUtils.isNumber(args[2])) {
                    KnockbackProfile profile = SpigotV.INSTANCE.getConfig().getKbProfileByName(args[1]);

                    if (profile == null) {
                        sender.sendMessage(ChatColor.RED + "A profile with that name could not be found.");
                        return true;
                    }

                    profile.setMaxRange(Double.parseDouble(args[2]));
                    profile.save();

                    sender.sendMessage(ChatColor.GOLD + "You have updated " + ChatColor.YELLOW + profile.getName() + ChatColor.GOLD + "'s values to:");

                    for (String value : profile.getValues()) {
                        sender.sendMessage(ChatColor.YELLOW + "* " + value);
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Wrong syntax.");
                }
            }
            break;
            case "startrange": {
                if (args.length == 3 && NumberUtils.isNumber(args[2])) {
                    KnockbackProfile profile = SpigotV.INSTANCE.getConfig().getKbProfileByName(args[1]);

                    if (profile == null) {
                        sender.sendMessage(ChatColor.RED + "A profile with that name could not be found.");
                        return true;
                    }

                    profile.setStartRange(Double.parseDouble(args[2]));
                    profile.save();

                    sender.sendMessage(ChatColor.GOLD + "You have updated " + ChatColor.YELLOW + profile.getName() + ChatColor.GOLD + "'s values to:");

                    for (String value : profile.getValues()) {
                        sender.sendMessage(ChatColor.YELLOW + "* " + value);
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Wrong syntax.");
                }
            }
            break;
            case "rangefactor": {
                if (args.length == 3 && NumberUtils.isNumber(args[2])) {
                    KnockbackProfile profile = SpigotV.INSTANCE.getConfig().getKbProfileByName(args[1]);

                    if (profile == null) {
                        sender.sendMessage(ChatColor.RED + "A profile with that name could not be found.");
                        return true;
                    }

                    profile.setRangeFactor(Double.parseDouble(args[2]));
                    profile.save();

                    sender.sendMessage(ChatColor.GOLD + "You have updated " + ChatColor.YELLOW + profile.getName() + ChatColor.GOLD + "'s values to:");

                    for (String value : profile.getValues()) {
                        sender.sendMessage(ChatColor.YELLOW + "* " + value);
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Wrong syntax.");
                }
            }
            break;
	    default: {
                sender.sendMessage(usageMessage);
            }
        }
        return true;
    }
}
