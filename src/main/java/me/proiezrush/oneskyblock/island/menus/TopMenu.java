package me.proiezrush.oneskyblock.island.menus;

import me.proiezrush.oneskyblock.Main;
import me.proiezrush.oneskyblock.island.IslandPlayer;
import me.proiezrush.oneskyblock.island.top.TopManager;
import me.proiezrush.oneskyblock.utils.ItemBuilder;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TopMenu extends Menu {

    private final Main m;
    private final IslandPlayer player;

    public TopMenu(Main m, IslandPlayer player) {
        super(m, m.getC().get(player.getPlayer(), "Inventories.TopMenu.Name"), 54);
        this.m = m;
        this.player = player;
        setup();
    }

    private void setup() {
        Player p = player.getPlayer();
        String PATH = "Inventories.TopMenu.Items";

        for (int i=0;i<TopManager.getTopsPlayer(m).size();i++) {
            List<String> list = m.getC().getList(PATH + ".Lore");
            List<String> x = new ArrayList<>();
            for (String s : list) {
                x.add(s.replace("%level%", String.valueOf(TopManager.getTopsPlayer(m).get(i).getLevel()))
                        .replace("%player%", TopManager.getTopsPlayer(m).get(i).getPlayer().getName())
                        .replace("%number%", String.valueOf(TopManager.getTopsPlayer(m).size() - i)));
            }
            ItemBuilder item;
            String name = m.getC().get(p, PATH + ".Name");
            String material = m.getC().get(p, PATH + ".Material");
            int data = m.getC().getInt(PATH + ".Data");

            if (material.equalsIgnoreCase("SKULL_ITEM")) {
                item = new ItemBuilder(m, name.replace("%player%", String.valueOf(TopManager.getTopsPlayer(m).get(i).getPlayer().getName()))
                        .replace("%number%", String.valueOf(TopManager.getTopsPlayer(m).size() - i)), p.getName(), true, x);
            }
            else {
                item = new ItemBuilder(m, name.replace("%level%", String.valueOf(TopManager.getTopsPlayer(m).get(i).getLevel()))
                        .replace("%player%", TopManager.getTopsPlayer(m).get(i).getPlayer().getName())
                        .replace("%number%", String.valueOf(TopManager.getTopsPlayer(m).size() - i)), material,
                        data, true, x);
            }

            getInventory().addItem((TopManager.getTopsPlayer(m).size() - 1) - i, item.build());
        }
    }
}
