package me.proiezrush.oneskyblock.island.menus;

import me.proiezrush.oneskyblock.Main;
import me.proiezrush.oneskyblock.island.IslandPlayer;
import me.proiezrush.oneskyblock.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class KickPlayerMenu extends Menu {

    private final Main m;

    public KickPlayerMenu(Main m, IslandPlayer player) {
        super(m, m.getC().get(player.getPlayer(), "Inventories.KickPlayer.Name"), 54);
        this.m = m;
        setup();
    }

    private void setup() {
        String PATH = "Inventories.KickPlayer.Items";

        int slot = 0;
        for (Player player : Bukkit.getOnlinePlayers()) {
            ItemBuilder item = new ItemBuilder(m, m.getC().get(player, PATH + ".Name").replace("%player%", player.getName()),
                    m.getC().get(player, PATH + ".Material"), m.getC().getInt(PATH + ".Data"), true, m.getC().getList(PATH + ".Lore"));

            getInventory().addItem(slot, item.build());
            slot++;
        }
    }
}
