package me.proiezrush.oneskyblock.commands;

import me.proiezrush.oneskyblock.Main;
import me.proiezrush.oneskyblock.island.IslandPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TradeCommand implements CommandExecutor {

    private final Main m;
    public TradeCommand(Main m) {
        this.m = m;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (command.getName().equals("trade")) {
            if (commandSender instanceof Player) {
                Player p = (Player) commandSender;
                if (p.hasPermission("ob.trade")) {
                    IslandPlayer player = m.getPlayer(p);
                    p.openInventory(player.getTradingsMenu().getInventory().getInventory());
                    p.sendMessage(m.getMessages().get(p, "trading-menu-open"));
                }
                else p.sendMessage(m.getMessages().get(p, "no-permission"));
            }
            else commandSender.sendMessage(m.getMessages().get(null, "console-error"));
        }
        return false;
    }
}
