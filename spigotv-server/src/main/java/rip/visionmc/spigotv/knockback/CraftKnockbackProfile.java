package rip.visionmc.spigotv.knockback;

import org.bukkit.ChatColor;
import rip.visionmc.spigotv.SpigotV;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CraftKnockbackProfile implements KnockbackProfile {

    private String name;
    private double horizontal = 0.9055D;
    private double vertical = 0.25635D;
    private double minRange = 0.12D;
    private double maxRange = 1.2D;
    private double startRange = 3.0D;
    private double rangeFactor = 0.025D;
    private double horizontalFriction = 1.0D;
    private double verticalFriction = 60.0D;

    public CraftKnockbackProfile(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String[] getValues() {
        return new String[]{
                ChatColor.GOLD + "Horizontal" + ChatColor.WHITE + ": " + this.horizontal,
                ChatColor.GOLD + "Vertical" + ChatColor.WHITE + ": " + this.vertical,
                ChatColor.GOLD + "Min Range" + ChatColor.WHITE + ": " + this.minRange,
                ChatColor.GOLD + "Max Range" + ChatColor.WHITE + ": " + this.maxRange,
                ChatColor.GOLD + "Start Range" + ChatColor.WHITE + ": " + this.startRange,
                ChatColor.GOLD + "Range Factor" + ChatColor.WHITE + ": " + this.rangeFactor,
                ChatColor.GOLD + "Horizontal Friction" + ChatColor.WHITE + ": " + this.horizontalFriction,
                ChatColor.GOLD + "Vertical Friction" + ChatColor.WHITE + ": " + this.verticalFriction
        };
    }

    public void save() {
        final String path = "knockback.profiles." + this.name;

        SpigotV.INSTANCE.getConfig().set(path + ".horizontal", this.horizontal);
        SpigotV.INSTANCE.getConfig().set(path + ".vertical", this.vertical);
        SpigotV.INSTANCE.getConfig().set(path + ".minRange", this.minRange);
        SpigotV.INSTANCE.getConfig().set(path + ".maxRange", this.maxRange);
        SpigotV.INSTANCE.getConfig().set(path + ".startRange", this.startRange);
        SpigotV.INSTANCE.getConfig().set(path + ".rangeFactor", this.rangeFactor);
        SpigotV.INSTANCE.getConfig().set(path + ".horizontalFriction", this.horizontalFriction);
        SpigotV.INSTANCE.getConfig().set(path + ".verticalFriction", this.verticalFriction);
        SpigotV.INSTANCE.getConfig().save();
    }
}
