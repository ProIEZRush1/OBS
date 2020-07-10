package me.proiezrush.oneskyblock.island.menus;

import me.proiezrush.oneskyblock.Main;
import me.proiezrush.oneskyblock.island.IslandPlayer;
import me.proiezrush.oneskyblock.utils.ItemBuilder;
import org.bukkit.entity.Player;

public class ResetConfirmation extends Menu {

    private final Main m;
    private final IslandPlayer player;

    public ResetConfirmation(Main m, IslandPlayer player) {
        super(m, m.getC().get(player.getPlayer(), "Inventories.ResetConfirmation.Name"), m.getC().getInt("Inventories.ResetConfirmation.Size"));
        this.m = m;
        this.player = player;
        setup();
    }
    
    private void setup() {
        Player p = player.getPlayer();
        final String PATH = "Inventories.ResetConfirmation.Items";

        ItemBuilder confirm = new ItemBuilder(m, m.getC().get(p, PATH + ".Confirm.Name"), m.getC().get(p, PATH + ".Confirm.Material"),
                m.getC().getInt(PATH + ".Confirm.Data"), true, m.getC().getList(PATH + ".Confirm.Lore"));
        ItemBuilder cancel = new ItemBuilder(m, m.getC().get(p, PATH + ".Cancel.Name"), m.getC().get(p, PATH + ".Cancel.Material"),
                m.getC().getInt(PATH + ".Cancel.Data"), true, m.getC().getList(PATH + ".Cancel.Lore"));
        
        getInventory().addItem(m.getC().getInt(PATH + ".Confirm.Slot"), confirm.build());
        getInventory().addItem(m.getC().getInt(PATH + ".Cancel.Slot"), cancel.build());
    }
}
