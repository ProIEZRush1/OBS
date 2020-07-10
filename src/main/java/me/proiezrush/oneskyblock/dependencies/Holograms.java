package me.proiezrush.oneskyblock.dependencies;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import me.proiezrush.oneskyblock.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.HashMap;

public class Holograms {

    private static final HashMap<Location, Hologram> holograms = new HashMap<>();

    public static void createHologram(Main m, Location location) {
        if (Bukkit.getPluginManager().getPlugin("HolographicDisplays") != null && location != null) {
            Hologram hologram = HologramsAPI.createHologram(m, location);
            hologram.setAllowPlaceholders(true);
            holograms.putIfAbsent(hologram.getLocation(), hologram);
        }
    }

    public static void addLine(Location location, String line) {
        Hologram hologram = holograms.get(location);
        if (hologram != null) {
            hologram.appendTextLine(line);
        }
    }

    public static void setFirstLine(Location location, String line) {
        Hologram hologram = holograms.get(location);
        if (hologram != null) {
            try {
                hologram.removeLine(1);
            }
            catch (Exception ignore) { }
            hologram.appendTextLine(line);
        }
    }

    public static void removeLine(Location location, int number) {
        Hologram hologram = holograms.get(location);
        if (hologram != null) {
            hologram.removeLine(number);
        }
    }

    public static void deleteHologram(Location location) {
        Hologram hologram = holograms.get(location);
        if (hologram != null) {
            hologram.delete();
        }
    }

}
