package me.proiezrush.oneskyblock.island.menus;

import me.proiezrush.oneskyblock.Main;
import me.proiezrush.oneskyblock.island.IslandPlayer;
import me.proiezrush.oneskyblock.utils.ItemBuilder;
import org.bukkit.entity.Player;

public class IsMenu extends Menu {

    private final Main m;
    private final IslandPlayer player;

    public IsMenu(Main m, IslandPlayer player) {
        super(m, m.getC().get(player.getPlayer(), "Inventories.IsMenu.Name"), m.getC().getInt("Inventories.IsMenu.Size"));
        this.m = m;
        this.player = player;
        setup();
    }

    private void setup() {
        Player p = player.getPlayer();
        final String PATH = "Inventories.IsMenu.Items";

        ItemBuilder spawn = new ItemBuilder(m, m.getC().get(p, PATH + ".Spawn.Name"), m.getC().get(p, PATH + ".Spawn.Material"),
                m.getC().getInt(PATH + ".Spawn.Data"), true, m.getC().getList(PATH + ".Spawn.Lore"));
        ItemBuilder topIslands = new ItemBuilder(m, m.getC().get(p, PATH + ".TopIslands.Name"), m.getC().get(p, PATH + ".TopIslands.Material"),
                m.getC().getInt(PATH + ".TopIslands.Data"), true, m.getC().getList(PATH + ".TopIslands.Lore"));
        ItemBuilder resetIsland = new ItemBuilder(m, m.getC().get(p, PATH + ".ResetIsland.Name"), m.getC().get(p, PATH + ".ResetIsland.Material"),
                m.getC().getInt(PATH + ".ResetIsland.Data"), true, m.getC().getList(PATH + ".ResetIsland.Lore"));
        ItemBuilder kickPlayer = new ItemBuilder(m, m.getC().get(p, PATH + ".KickPlayer.Name"), m.getC().get(p, PATH + ".KickPlayer.Material"),
                m.getC().getInt(PATH + ".KickPlayer.Data"), true, m.getC().getList(PATH + ".KickPlayer.Lore"));
        ItemBuilder lockIsland = new ItemBuilder(m, m.getC().get(p, PATH + ".LockIsland.Name"), m.getC().get(p, PATH + ".LockIsland.Material"),
                m.getC().getInt(PATH + ".LockIsland.Data"), true, m.getC().getList(PATH + ".LockIsland.Lore"));
        ItemBuilder addPlayer = new ItemBuilder(m, m.getC().get(p, PATH + ".AddPlayer.Name"), m.getC().get(p, PATH + ".AddPlayer.Material"),
                m.getC().getInt(PATH + ".AddPlayer.Data"), true, m.getC().getList(PATH + ".AddPlayer.Lore"));
        ItemBuilder islandLevel = new ItemBuilder(m, m.getC().get(p, PATH + ".IslandLevel.Name"), m.getC().get(p, PATH + ".IslandLevel.Material"),
                m.getC().getInt(PATH + ".IslandLevel.Data"), true, m.getC().getList(PATH + ".IslandLevel.Lore"));
        ItemBuilder islandChat = new ItemBuilder(m, m.getC().get(p, PATH + ".IslandChat.Name"), m.getC().get(p, PATH + ".IslandChat.Material"),
                m.getC().getInt(PATH + ".IslandChat.Data"), true, m.getC().getList(PATH + ".IslandChat.Lore"));
        ItemBuilder npc = new ItemBuilder(m, m.getC().get(p, PATH + ".NPC.Name"), m.getC().get(p, PATH + ".NPC.Material"),
                m.getC().getInt(PATH + ".NPC.Data"), true, m.getC().getList(PATH + ".NPC.Lore"));

        getInventory().addItem(m.getC().getInt(PATH + ".Spawn.Slot"), spawn.build());
        getInventory().addItem(m.getC().getInt(PATH + ".TopIslands.Slot"), topIslands.build());
        getInventory().addItem(m.getC().getInt(PATH + ".ResetIsland.Slot"), resetIsland.build());
        getInventory().addItem(m.getC().getInt(PATH + ".KickPlayer.Slot"), kickPlayer.build());
        getInventory().addItem(m.getC().getInt(PATH + ".LockIsland.Slot"), lockIsland.build());
        getInventory().addItem(m.getC().getInt(PATH + ".AddPlayer.Slot"), addPlayer.build());
        getInventory().addItem(m.getC().getInt(PATH + ".IslandLevel.Slot"), islandLevel.build());
        getInventory().addItem(m.getC().getInt(PATH + ".IslandChat.Slot"), islandChat.build());
        getInventory().addItem(m.getC().getInt(PATH + ".NPC.Slot"), npc.build());
    }

}
