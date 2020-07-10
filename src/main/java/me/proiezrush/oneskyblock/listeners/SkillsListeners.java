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
public class SkillsListeners implements Listener {

    private final Main m;
    public SkillsListeners(Main m) {
        this.m = m;
    }

    @EventHandler
    public void onCraft(CraftItemEvent e) {
        ItemStack item = e.getRecipe().getResult();
        HumanEntity hE = e.getWhoClicked();
        if (hE instanceof Player) {
            Player p = (Player) hE;
            IslandPlayer player = m.getPlayer(p);
            if (item != null) {
                if (player.getSkill() != null) {
                    String PATH = "Skills." + player.getSkill().getName() + ".Levels";

                    ConfigurationSection a = m.getC().getConfig().getConfigurationSection(PATH + "." + player.getSkill().getLevel());
                    if (a != null) {
                        String type = a.getString("Type");

                        if (type.equalsIgnoreCase("CRAFT")) {
                            String material = a.getString("Material");
                            int data = a.getInt("Data");
                            String command = a.getString("Command");

                            if (item.getType().equals(Material.valueOf(material)) && item.getDurability() == data) {
                                if (command.contains("[message] ")) {
                                    p.sendMessage(m.getAdm().parsePlaceholders(p, command.replace("[message] ", "").replace("%player%", p.getName())));
                                }
                                else if (command.contains("[op] ")) {
                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("[op] ", "").replace("%player%", p.getName()));
                                }
                                else if (command.contains("[player] ")) {
                                    p.chat("/" + command.replace("[player] ", "").replace("%player%", p.getName()));
                                }
                                player.getSkill().addLevel();
                                player.getSkill().setNumber(1);
                                p.sendMessage(m.getMessages().get(p, "skill-level-changed").replace("%level%", String.valueOf(player.getSkill().getLevel())).replace("%skill%", player.getSkill().getName()));
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

        if (player.getSkill() != null) {
            String PATH = "Skills." + player.getSkill().getName() + ".Levels";

            ConfigurationSection a = m.getC().getConfig().getConfigurationSection(PATH + "." + player.getSkill().getLevel());
            if (a != null) {
                String type = a.getString("Type");

                if (type.equalsIgnoreCase("BREAK")) {
                    String material = a.getString("Material");
                    int data = a.getInt("Data");
                    int number = a.getInt("Number");
                    String command = a.getString("Command");

                    if (b.getType().equals(Material.valueOf(material)) && b.getData() == data) {
                        if (player.getSkill().getNumber() == number) {
                            if (command.contains("[message] ")) {
                                p.sendMessage(m.getAdm().parsePlaceholders(p, command.replace("[message] ", "").replace("%player%", p.getName())));
                            }
                            else if (command.contains("[op] ")) {
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("[op] ", "").replace("%player%", p.getName()));
                            }
                            else if (command.contains("[player] ")) {
                                p.chat("/" + command.replace("[player] ", "").replace("%player%", p.getName()));
                            }
                            player.getSkill().addLevel();
                            player.getSkill().setNumber(1);
                            p.sendMessage(m.getMessages().get(p, "skill-level-changed").replace("%level%", String.valueOf(player.getSkill().getLevel()))
                                    .replace("%skill%", player.getSkill().getName()));
                        }
                        else player.getSkill().addNumber();
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

        if (player.getSkill() != null) {
            String PATH = "Skills." + player.getSkill().getName() + ".Levels";

            ConfigurationSection a = m.getC().getConfig().getConfigurationSection(PATH + "." + player.getSkill().getLevel());
            if (a != null) {
                String type = a.getString("Type");

                if (type.equalsIgnoreCase("PLACE")) {
                    String material = a.getString("Material");
                    int data = a.getInt("Data");
                    int number = a.getInt("Number");
                    String command = a.getString("Command");

                    if (b.getType().equals(Material.valueOf(material)) && b.getData() == data) {
                        if (player.getSkill().getNumber() == number) {
                            if (command.contains("[message] ")) {
                                p.sendMessage(m.getAdm().parsePlaceholders(p, command.replace("[message] ", "").replace("%player%", p.getName())));
                            } else if (command.contains("[op] ")) {
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("[op] ", "").replace("%player%", p.getName()));
                            } else if (command.contains("[player] ")) {
                                p.chat("/" + command.replace("[player] ", "").replace("%player%", p.getName()));
                            }
                            player.getSkill().addLevel();
                            player.getSkill().setNumber(1);
                            p.sendMessage(m.getMessages().get(p, "skill-level-changed").replace("%level%", String.valueOf(player.getSkill().getLevel()))
                                    .replace("%skill%", player.getSkill().getName()));
                        }
                        else player.getSkill().addNumber();
                    }
                }
            }
        }
    }

}
