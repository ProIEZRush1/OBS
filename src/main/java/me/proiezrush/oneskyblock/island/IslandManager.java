package me.proiezrush.oneskyblock.island;

import me.proiezrush.oneskyblock.Main;
import me.proiezrush.oneskyblock.utils.Titles;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.io.File;

public class IslandManager {

    public static void create(Main m, IslandPlayer player) {
        Player p = player.getPlayer();
        m.getWorlds().createVoidWorld(p.getName() + "_OB");

        Island island = new Island(m, new Location(Bukkit.getWorld(player.getPlayer().getName() + "_OB"), 0.5D, 75D, 0.5D), player);
        island.addPlayer(player);
        island.getSpawn().getBlock().setType(Material.GLASS);
        island.setOb(island.getSpawn().add(new Vector(0D, 1D, 0D)));
        island.holo();
        m.addIsland(island);

        player.setCurrentIsland(island);
        p.teleport(island.getSpawn().clone().add(new Vector(0D, 1D, 0D)));
        p.sendMessage(m.getMessages().get(p, "island-created"));

        String title = m.getC().get(p, "CreateIslandTitle");
        String subtitle = m.getC().get(p, "CreateIslandSubtitle");
        Titles titles = new Titles(title.replace("%player%", p.getName()), subtitle.replace("%player%", p.getName()));
        titles.send(p);
    }

    public static void resetIsland(Main m, IslandPlayer owner, Island island) {
        Player p = owner.getPlayer();
        if (island != null) {
            if (owner.hasResetsLeft()) {
                p.teleport(new Location(Bukkit.getWorld("world"), p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ()));
                m.getHolo(island).stopTimer();
                m.removeHolo(island);
                m.removeIsland(owner);
                Bukkit.getScheduler().scheduleSyncDelayedTask(m, () -> {
                    m.getWorlds().deleteWorld(island.getSpawn().getWorld().getWorldFolder());
                    m.getWorlds().deleteWorld(new File(island.getSpawn().getWorld().getWorldFolder() + "/region"));
                }, 5L);
                owner.setCurrentIsland(null);
                owner.removeReset();
                p.sendMessage(m.getMessages().get(p, "island-reset"));
            } else p.sendMessage(m.getMessages().get(p, "no-resets"));
        }
        else p.sendMessage(m.getMessages().get(p, "island-not-exist"));
        p.closeInventory();
    }

    public static void kickPlayer(Main m, IslandPlayer owner, Island island, IslandPlayer player) {
        Player p = owner.getPlayer();
        if (island != null) {
            if (island.containsPlayer(player)) {
                island.removePlayer(player);
                if (m.getIsland(player) != null) {
                    player.getPlayer().teleport(m.getIsland(player).getSpawn());
                } else
                    p.teleport(new Location(Bukkit.getWorld("world"), p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ()));
                island.removeAddedPlayer(player);
                p.sendMessage(m.getMessages().get(p, "player-kicked"));
            } else p.sendMessage(m.getMessages().get(p, "player-not-in-island"));
        }
        else p.sendMessage(m.getMessages().get(p, "island-not-exist"));
        p.closeInventory();
    }

    public static void lockIsland(Main m, IslandPlayer owner, Island island) {
        Player p = owner.getPlayer();
        if (island != null) {
            if (!island.isLocked()) {
                island.setLocked(true);
                p.sendMessage(m.getMessages().get(p, "island-locked"));
            } else {
                island.setLocked(false);
                p.sendMessage(m.getMessages().get(p, "island-unlocked"));
            }
        }
        else p.sendMessage(m.getMessages().get(p, "island-not-exist"));
        p.closeInventory();
    }

    public static void addPlayer(Main m, IslandPlayer owner, Island island, IslandPlayer player) {
        Player p = owner.getPlayer();
        if (island != null) {
            ConfigurationSection a = m.getC().getConfig().getConfigurationSection("Upgrades." + player.getLevel());
            if (a == null) {
                a = m.getC().getConfig().getConfigurationSection("Upgrades.default");
            }
            if (a != null) {
                int maxMembers = a.getInt("MaxMembers");
                if (island.getAddedPlayers().size() <= maxMembers) {
                    if (!island.containsPlayer(player)) {
                        island.addAddedPlayer(player);
                        player.getPlayer().sendMessage(m.getMessages().get(player.getPlayer(), "added-to-island").replace("%player%", p.getName()));
                        p.sendMessage(m.getMessages().get(p, "player-added"));
                    }
                    else p.sendMessage(m.getMessages().get(p, "player-already-in-island"));
                }
                else p.sendMessage(m.getMessages().get(p, "members-limit-reached"));
            }
        }
        else p.sendMessage(m.getMessages().get(p, "island-not-exist"));
        p.closeInventory();
    }

    public static void islandLevel(Main m, IslandPlayer owner) {
        Player p = owner.getPlayer();
        p.sendMessage(m.getMessages().get(p, "island-level").replace("%level%", String.valueOf(owner.getLevel())));
        p.closeInventory();
    }

    public static void teleport(Main m, IslandPlayer player, Island island) {
        Player p = player.getPlayer();
        if (island != null) {
            if (!island.isLocked() || island.containsAddedPlayer(p)) {
                player.setCurrentIsland(island);
                island.addPlayer(player);
                p.teleport(island.getSpawn());
                p.sendMessage(m.getMessages().get(p, "teleported-island").replace("%player%", island.getOwner().getPlayer().getName()));
            }
            else p.sendMessage(m.getMessages().get(p, "other-island-locked"));
        }
        else p.sendMessage(m.getMessages().get(p, "island-not-exist"));
        p.closeInventory();
    }

    public static void spawn(Main m, IslandPlayer owner, Island island) {
        Player p = owner.getPlayer();
        if (island != null) {
            if (island.getOwner().equals(owner)) {
                owner.setCurrentIsland(island);
                p.teleport(island.getSpawn().clone().add(new Vector(0D, 1D, 0D)));
                p.sendMessage(m.getMessages().get(p, "teleport-spawn"));
            }
            else p.sendMessage(m.getMessages().get(p, "not-owner"));
        }
        else p.sendMessage(m.getMessages().get(p, "island-not-exist"));
        p.closeInventory();
    }

    public static void islandChat(Main m, IslandPlayer owner, Island island) {
        Player p = owner.getPlayer();
        if (island != null) {
            if (owner.isIslandChat()) {
                owner.setIslandChat(false);
                p.sendMessage(m.getMessages().get(p, "island-chat-disabled"));
            }
            else {
                owner.setIslandChat(true);
                p.sendMessage(m.getMessages().get(p, "island-chat-enabled"));
            }
        }
        else p.sendMessage(m.getMessages().get(p, "island-not-exist"));
        p.closeInventory();
    }

}
