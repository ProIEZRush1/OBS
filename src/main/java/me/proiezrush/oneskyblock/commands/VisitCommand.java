package me.proiezrush.oneskyblock.commands;

import me.proiezrush.oneskyblock.Main;
import me.proiezrush.oneskyblock.island.Island;
import me.proiezrush.oneskyblock.island.IslandPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;

public class VisitCommand implements CommandExecutor {

    private final Main m;
    public VisitCommand(Main m) {
        this.m = m;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (command.getName().equalsIgnoreCase("visit")) {
            if (commandSender instanceof Player) {
                Player p = (Player) commandSender;
                if (p.hasPermission("ob.visit")) {
                    if (args.length == 1) {
                        String a = args[0];
                        IslandPlayer player = m.getPlayer(Bukkit.getPlayer(a));
                        Island island = m.getIsland(player);
                        if (island != null) {
                            IslandPlayer islandPlayer = m.getPlayer(p);
                            if (!island.getOwner().equals(islandPlayer)) {
                                if (island.isLocked() && !island.getAddedPlayers().contains(islandPlayer)) {
                                    p.sendMessage(m.getMessages().get(p, "island-is-locked"));
                                }
                                else {
                                    if (island.getWelcome() != null) {
                                        p.teleport(island.getWelcome());
                                    }
                                    else p.teleport(island.getSpawn().add(new Vector(0D, 1D, 0D)));
                                    p.sendMessage(m.getMessages().get(p, "visiting-island"));
                                }
                            }
                            else p.sendMessage(m.getMessages().get(p, "cannot-visit-you"));
                        }
                        else p.sendMessage(m.getMessages().get(p, "player-without-island"));
                    }
                    else a(p);
                }
                else p.sendMessage(m.getMessages().get(p, "no-permission"));
            }
            else commandSender.sendMessage(m.getMessages().get(null, "console-error"));
        }
        return false;
    }

    private void a(Player p) {
        List<String> list = m.getMessages().getList("visit-command-help");
        for (String s : list) {
            p.sendMessage(m.getAdm().parsePlaceholders(p, s));
        }
    }
}
