package me.proiezrush.oneskyblock;

import me.proiezrush.oneskyblock.dependencies.PAPI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Admin {

    public String parsePlaceholders(Player p, String s) {
        return PAPI.parsePlaceholders(p, s);
    }

    public String chatColor(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

}
