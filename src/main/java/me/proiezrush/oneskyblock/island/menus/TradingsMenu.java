package me.proiezrush.oneskyblock.island.menus;

import me.proiezrush.oneskyblock.Main;
import me.proiezrush.oneskyblock.island.IslandPlayer;
import me.proiezrush.oneskyblock.utils.ItemBuilder;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

public class TradingsMenu extends Menu {

    private final Main m;

    public TradingsMenu(Main m, IslandPlayer player) {
        super(m, m.getC().get(player.getPlayer(), "Tradings.Name"), m.getC().getInt("Tradings.Size"));
        this.m = m;
        setup();
    }

    private void setup() {
        final String PATH = "Tradings.Items";

        ConfigurationSection a = m.getC().getConfig().getConfigurationSection(PATH);
        if (a != null) {
            for (String k : a.getKeys(false)) {
                String name = a.getString(k + ".Name");
                List<String> lore = a.getStringList(k + ".Lore");
                String material = a.getString(k + ".Material");
                int data = a.getInt(k + ".Data");
                int slot = a.getInt(k + ".Slot");

                ItemBuilder item = new ItemBuilder(m, name, material, data, true, lore);
                getInventory().addItem(slot, item.build());
            }
        }
    }
}
