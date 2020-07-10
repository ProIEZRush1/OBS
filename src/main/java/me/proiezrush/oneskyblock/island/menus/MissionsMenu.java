package me.proiezrush.oneskyblock.island.menus;

import me.proiezrush.oneskyblock.Main;
import me.proiezrush.oneskyblock.island.IslandPlayer;
import me.proiezrush.oneskyblock.utils.ItemBuilder;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

public class MissionsMenu extends Menu {

    private final Main m;
    private final IslandPlayer player;

    public MissionsMenu(Main m, IslandPlayer player) {
        super(m, m.getC().get(player.getPlayer(),"MissionsInventory.Name"), m.getC().getInt("MissionsInventory.Size"));
        this.m = m;
        this.player = player;
        setup();
    }

    private void setup() {
        final String PATH = "MissionsInventory.Items";

        ConfigurationSection a = m.getC().getConfig().getConfigurationSection(PATH);
        if (a != null) {
            for (String k : a.getKeys(false)) {
                String name = a.getString(k + ".Name");
                List<String> lore = a.getStringList(k + ".Lore");
                String material = a.getString(k + ".Material");
                int data = a.getInt(k + ".Data");
                int slot = a.getInt(k + ".Slot");
                String mission = a.getString(k + ".Mission");

                String completed = m.getC().get(player.getPlayer(), "Status.Completed");
                String uncompleted = m.getC().get(player.getPlayer(), "Status.Uncompleted");

                List<String> x = new ArrayList<>();
                for (String s : lore) {
                    x.add(s.replace("%status%", player.hasCompletedMission(m.getMission(mission)) ? completed : uncompleted));
                }

                ItemBuilder item = new ItemBuilder(m, name, material, data, true, x);
                getInventory().addItem(slot, item.build());
            }
        }
    }
}
