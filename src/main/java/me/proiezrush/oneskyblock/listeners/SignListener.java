package me.proiezrush.oneskyblock.listeners;

import me.proiezrush.oneskyblock.Main;
import me.proiezrush.oneskyblock.island.Island;
import me.proiezrush.oneskyblock.island.IslandPlayer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;

public class SignListener implements Listener {

    private final Main m;
    public SignListener(Main m) {
        this.m = m;
    }

    @EventHandler
    public void onSignPlace(SignChangeEvent e) {
        Player p = e.getPlayer();
        IslandPlayer player = m.getPlayer(p);
        if (player.isInIsland()) {
            if (e.getLine(0).contains("[welcome]") || e.getLine(0).contains("[bienvenida]")) {
                Island island = m.getIsland(player);
                island.setWelcome(e.getBlock().getLocation());
                p.sendMessage(m.getMessages().get(p, "welcome-placed"));
            }
        }
    }

    @EventHandler
    public void onSignBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        IslandPlayer player = m.getPlayer(p);
        Block b = e.getBlock();
        if (b.getType().toString().contains("SIGN")) {
            if (player.isInIsland()) {
                BlockState state = b.getState();
                Sign sign = (Sign) state;
                Island island = m.getIsland(player);
                if (sign.getLine(0).contains("[welcome]")) {
                    if (island.getWelcome() != null) {
                        if (island.getWelcome().equals(b.getLocation())) {
                            island.setWelcome(null);
                            p.sendMessage(m.getMessages().get(p, "welcome-removed"));
                        }
                    }
                }
            }
        }
    }

}
