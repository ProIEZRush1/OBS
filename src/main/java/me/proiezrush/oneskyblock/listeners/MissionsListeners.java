package me.proiezrush.oneskyblock.listeners;

import me.proiezrush.oneskyblock.Main;
import me.proiezrush.oneskyblock.island.IslandPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("deprecation")
public class MissionsListeners implements Listener {

    private final Main m;
    public MissionsListeners(Main m) {
        this.m = m;
    }

    @EventHandler
    public void onCraft(CraftItemEvent e) {
        ItemStack item = e.getCurrentItem();
        HumanEntity hE = e.getWhoClicked();
        if (hE instanceof Player) {
            Player p = (Player) hE;
            IslandPlayer player = m.getPlayer(p);
            if (item != null) {
                String PATH = "Missions";

                ConfigurationSection a = m.getC().getConfig().getConfigurationSection(PATH);
                if (a != null) {
                    for (String k : a.getKeys(false)) {
                        String type = a.getString(k + ".Type");
                        if (type.equalsIgnoreCase("CRAFT")) {
                            String material = a.getString(k + ".Material");
                            int data = a.getInt(k + ".Data");
                            String command = a.getString(k + ".Command");

                            if (item.getType().equals(Material.valueOf(material)) && item.getDurability() == data) {
                                if (!player.hasCompletedMission(m.getMission(k))) {
                                    if (command.contains("[message] ")) {
                                        p.sendMessage(m.getAdm().parsePlaceholders(p, command.replace("[message] ", "").replace("%player%", p.getName())));
                                    } else if (command.contains("[op] ")) {
                                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("[op] ", "").replace("%player%", p.getName()));
                                    } else if (command.contains("[player] ")) {
                                        p.chat("/" + command.replace("[player] ", "").replace("%player%", p.getName()));
                                    }
                                    player.addCompletedMission(m.getMission(k));
                                    p.sendMessage(m.getMessages().get(p, "mission-completed").replace("%mission%", k));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        Block b = e.getBlock();
        IslandPlayer player = m.getPlayer(p);

        String PATH = "Missions";

        ConfigurationSection a = m.getC().getConfig().getConfigurationSection(PATH);
        if (a != null) {
            for (String k : a.getKeys(false)) {
                String type = a.getString(k + ".Type");
                if (type.equalsIgnoreCase("BREAK")) {
                    String material = a.getString(k + ".Material");
                    int data = a.getInt(k + ".Data");
                    int number = a.getInt(k + ".Number");
                    String command = a.getString(k + ".Command");

                    if (b.getType().equals(Material.valueOf(material)) && b.getData() == data) {
                        if (!player.hasCompletedMission(m.getMission(k))) {
                            if (player.getMission(k).getNumber() == number) {
                                if (command.contains("[message] ")) {
                                    p.sendMessage(m.getAdm().parsePlaceholders(p, command.replace("[message] ", "").replace("%player%", p.getName())));
                                }
                                else if (command.contains("[op] ")) {
                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("[op] ", "").replace("%player%", p.getName()));
                                }
                                else if (command.contains("[player] ")) {
                                    p.chat("/" + command.replace("[player] ", "").replace("%player%", p.getName()));
                                }
                                player.getMission(k).setNumber(0);
                                player.addCompletedMission(m.getMission(k));
                                p.sendMessage(m.getMessages().get(p, "mission-completed").replace("%mission%", k));
                            }
                            else player.getMission(k).addNumber();
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        Player p = e.getPlayer();
        Block b = e.getBlock();
        IslandPlayer player = m.getPlayer(p);

        String PATH = "Missions";

        ConfigurationSection a = m.getC().getConfig().getConfigurationSection(PATH);
        if (a != null) {
            for (String k : a.getKeys(false)) {
                String type = a.getString(k + ".Type");
                if (type.equalsIgnoreCase("PLACE")) {
                    String material = a.getString(k + ".Material");
                    int data = a.getInt(k + ".Data");
                    int number = a.getInt(k + ".Number");
                    String command = a.getString(k + ".Command");

                    if (b.getType().equals(Material.valueOf(material)) && b.getData() == data) {
                        if (!player.hasCompletedMission(m.getMission(k))) {
                            if (player.getMission(k).getNumber() == number) {
                                if (command.contains("[message] ")) {
                                    p.sendMessage(m.getAdm().parsePlaceholders(p, command.replace("[message] ", "").replace("%player%", p.getName())));
                                } else if (command.contains("[op] ")) {
                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("[op] ", "").replace("%player%", p.getName()));
                                } else if (command.contains("[player] ")) {
                                    p.chat("/" + command.replace("[player] ", "").replace("%player%", p.getName()));
                                }
                                player.getMission(k).setNumber(0);
                                player.addCompletedMission(m.getMission(k));
                                p.sendMessage(m.getMessages().get(p, "mission-completed").replace("%mission%", k));
                            }
                            else player.getMission(k).addNumber();
                        }
                    }
                }
            }
        }
    }

}
