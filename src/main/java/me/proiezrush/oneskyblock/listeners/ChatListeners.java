package me.proiezrush.oneskyblock.listeners;

import me.proiezrush.oneskyblock.Main;
import me.proiezrush.oneskyblock.island.Island;
import me.proiezrush.oneskyblock.island.IslandPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListeners implements Listener {

    private final Main m;
    public ChatListeners(Main m) {
        this.m = m;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        IslandPlayer player = m.getPlayer(p);
        Island island = m.getIsland(player);
        if (player.isIslandChat()) {
            for (Player players : Bukkit.getOnlinePlayers()) {
                if (!island.containsPlayer(m.getPlayer(players))) {
                    e.getRecipients().remove(players);
                }
            }
        }
    }

}
