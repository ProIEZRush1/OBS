package me.proiezrush.oneskyblock.commands;

import me.proiezrush.oneskyblock.Main;
import me.proiezrush.oneskyblock.island.IslandManager;
import me.proiezrush.oneskyblock.island.IslandPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ObCommand implements CommandExecutor {

    private final Main m;
    public ObCommand(Main m) {
        this.m = m;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (command.getName().equalsIgnoreCase("ob")) {
            if (commandSender instanceof Player) {
                Player p = (Player) commandSender;
                if (p.hasPermission("ob.command")) {
                    IslandPlayer player = m.getPlayer(p);
                    if (args.length == 0) {
                        if (!m.hasIsland(player)) {
                            IslandManager.create(m, m.getPlayer(p));
                            return true;
                        }
                        p.openInventory(player.getIsMenu().getInventory().getInventory());
                    }
                }
                else p.sendMessage(m.getMessages().get(p, "no-permission"));
            }
            else commandSender.sendMessage(m.getMessages().get(null, "console-error"));
        }
        return false;
    }
}
