package me.proiezrush.oneskyblock.island.menus;

import me.proiezrush.oneskyblock.Main;
import me.proiezrush.oneskyblock.island.IslandPlayer;
import me.proiezrush.oneskyblock.utils.ItemBuilder;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

public class SkillsMenu extends Menu {

    private final Main m;
    private final IslandPlayer player;

    public SkillsMenu(Main m, IslandPlayer player) {
        super(m, m.getC().get(player.getPlayer(),"SkillsInventory.Name"), m.getC().getInt("SkillsInventory.Size"));
        this.m = m;
        this.player = player;
        setup();
    }

    private void setup() {
        final String PATH = "SkillsInventory.Items";

        ConfigurationSection a = m.getC().getConfig().getConfigurationSection(PATH);
        if (a != null) {
            for (String k : a.getKeys(false)) {
                String name = a.getString(k + ".Name");
                List<String> lore = a.getStringList(k + ".Lore");
                String material = a.getString(k + ".Material");
                int data = a.getInt(k + ".Data");
                int slot = a.getInt(k + ".Slot");
                String skill = a.getString(k + ".Skill");

                List<String> x = new ArrayList<>();
                List<String> b;
                if (player.getSkill() != null) {
                    b = m.getC().getConfig().getStringList("Skills." + skill + ".Levels." + player.getSkill().getLevel() + ".Description");
                }
                else b = m.getC().getConfig().getStringList("Skills." + skill + ".Levels.1.Description");

                for (String s : lore) {
                    for (int i=0;i<b.size();i++) {
                        x.add(s.replace("%level_description_" + (i + 1) + "%", b.get(i)));
                    }
                }

                ItemBuilder item = new ItemBuilder(m, name, material, data, true, x);
                getInventory().addItem(slot, item.build());
            }
        }
    }
}
