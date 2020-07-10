package me.proiezrush.oneskyblock.listeners;

import me.proiezrush.oneskyblock.Main;
import me.proiezrush.oneskyblock.island.Island;
import me.proiezrush.oneskyblock.island.IslandManager;
import me.proiezrush.oneskyblock.island.IslandPlayer;
import me.proiezrush.oneskyblock.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;

@SuppressWarnings("deprecation")
public class ClickListeners implements Listener {

    private final Main m;

    public ClickListeners(Main m) {
        this.m = m;
    }

    @EventHandler
    public void onMissionsInvClick(InventoryClickEvent e) {
        Inventory inv = e.getClickedInventory();
        HumanEntity hE = e.getWhoClicked();
        if (hE instanceof Player) {
            Player p = (Player) hE;
            if (inv != null) {
                if (inv.getName().equalsIgnoreCase(m.getC().get(p, "MissionsInventory.Name"))) {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onResetConfirmationInvClick(InventoryClickEvent e) {
        Inventory inv = e.getClickedInventory();
        ItemStack item = e.getCurrentItem();
        HumanEntity hE = e.getWhoClicked();
        if (hE instanceof Player) {
            Player p = (Player) hE;
            if (inv != null) {
                if (inv.getName().equalsIgnoreCase(m.getC().get(p, "Inventories.ResetConfirmation.Name"))) {
                    if (item != null && item.hasItemMeta()) {
                        String PATH = "Inventories.ResetConfirmation.Items";
                        String name = item.getItemMeta().getDisplayName();
                        Material material = item.getType();
                        IslandPlayer player = m.getPlayer(p);
                        if (name.equalsIgnoreCase(m.getAdm().parsePlaceholders(p, m.getC().get(p, PATH + ".Confirm.Name")))
                            && material.equals(Material.valueOf(m.getC().get(p, PATH + ".Confirm.Material")))) {
                            IslandManager.resetIsland(m, player, m.getIsland(player));
                        }
                        else if (name.equalsIgnoreCase(m.getAdm().parsePlaceholders(p, m.getC().get(p, PATH + ".Cancel.Name")))
                            && material.equals(Material.valueOf(m.getC().get(p, PATH + ".Cancel.Material")))) {
                            p.closeInventory();
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onSkillsInvClick(InventoryClickEvent e) {
        Inventory inv = e.getClickedInventory();
        ItemStack item = e.getCurrentItem();
        HumanEntity hE = e.getWhoClicked();
        if (hE instanceof Player) {
            Player p = (Player) hE;
            IslandPlayer player = m.getPlayer(p);
            if (inv != null) {
                if (inv.getName().equals(m.getC().get(p, "SkillsInventory.Name"))) {
                    if (item != null) {
                        ConfigurationSection a = m.getC().getConfig().getConfigurationSection("SkillsInventory.Items");
                        for (String k : a.getKeys(false)) {
                            String material = a.getString(k + ".Material");
                            int data = a.getInt(k + ".Data");
                            int slot = a.getInt(k + ".Slot");

                            if (inv.getItem(slot).equals(item) && item.getType().equals(Material.valueOf(material)) && item.getDurability() == data) {
                                String skill = a.getString(k + ".Skill");
                                if (player.getSkill() != null) {
                                    if (!player.getSkill().getName().equalsIgnoreCase(skill)) {
                                        player.setSkill(m.getSkill(skill));
                                        p.sendMessage(m.getMessages().get(p, "skill-changed").replace("%skill%", skill));
                                        p.closeInventory();
                                        e.setCancelled(true);
                                    }
                                    else {
                                        p.sendMessage(m.getMessages().get(p, "already-skill"));
                                        p.closeInventory();
                                        e.setCancelled(true);
                                    }
                                }
                                else {
                                    player.setSkill(m.getSkill(skill));
                                    p.sendMessage(m.getMessages().get(p, "skill-changed").replace("%skill%", skill));
                                    p.closeInventory();
                                    e.setCancelled(true);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onTradingsClick(InventoryClickEvent e) {
        Inventory inv = e.getClickedInventory();
        ItemStack item = e.getCurrentItem();
        HumanEntity hE = e.getWhoClicked();
        if (hE instanceof Player) {
            Player p = (Player) hE;
            if (inv != null) {

                ConfigurationSection a = m.getC().getConfig().getConfigurationSection("TradingInventories");
                for (String k : a.getKeys(false)) {
                    String name = a.getString(k + ".Name");
                    if (inv.getName().equals(m.getAdm().parsePlaceholders(p, name))) {
                        if (item != null) {

                            ConfigurationSection b = a.getConfigurationSection(k + ".Items");
                            for (String j : b.getKeys(false)) {
                                String material = b.getString(j + ".Material");
                                int data = b.getInt(j + ".Data");
                                int slot = b.getInt(j + ".Slot");

                                if (inv.getItem(slot).equals(item) && item.getType().equals(Material.valueOf(material)) && item.getDurability() == data) {
                                    ConfigurationSection c = b.getConfigurationSection(j + ".TradeItem");
                                    String tName = c.getString("Name");
                                    List<String> tLore = c.getStringList("Lore");
                                    String tMaterial = c.getString("Material");
                                    int tData = c.getInt("Data");

                                    ItemBuilder i = new ItemBuilder(m, tName, tMaterial, tData, true, tLore);

                                    if (containsItem(p, i.build())) {
                                        removeItem(p, getItem(p, i.build()));
                                        p.getInventory().addItem(item);
                                        p.closeInventory();
                                        p.sendMessage(m.getMessages().get(p, "item-changed"));
                                    }
                                    else {
                                        p.closeInventory();
                                        p.sendMessage(m.getMessages().get(p, "not-item").replace("%item%", tMaterial).replace("%data%", String.valueOf(tData)));
                                    }
                                    e.setCancelled(true);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onTradingsInvClick(InventoryClickEvent e) {
        Inventory inv = e.getClickedInventory();
        ItemStack item = e.getCurrentItem();
        HumanEntity hE = e.getWhoClicked();
        if (hE instanceof Player) {
            Player p = (Player) hE;
            if (inv != null) {
                if (inv.getName().equals(m.getC().get(p, "Tradings.Name"))) {
                    if (item != null) {

                        String PATH = "Tradings.Items";
                        ConfigurationSection a = m.getC().getConfig().getConfigurationSection(PATH);
                        for (String k : a.getKeys(false)) {
                            String material = a.getString(k + ".Material");
                            int data = a.getInt(k + ".Data");
                            int inventory = a.getInt(k + ".Inventory");
                            int slot = a.getInt(k + ".Slot");

                            if (inv.getItem(slot).equals(item) && item.getType().equals(Material.valueOf(material)) && item.getDurability() == data
                            || item.getType().equals(Material.valueOf(material)) && item.getDurability() == data) {
                                if (m.getTradingInv(inventory) != null) {
                                    p.closeInventory();
                                    p.openInventory(m.getTradingInv(inventory).getInventory().getInventory());
                                }
                            }
                            e.setCancelled(true);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onSettingsInvClick(InventoryClickEvent e) {
        Inventory inv = e.getClickedInventory();
        ItemStack item = e.getCurrentItem();
        HumanEntity hE = e.getWhoClicked();
        if (hE instanceof Player) {
            Player p = (Player) hE;
            if (inv != null) {
                String ISMENUPATH = "Inventories.IsMenu";
                if (inv.getName().equalsIgnoreCase(m.getC().get(p, ISMENUPATH + ".Name"))) {
                    if (item != null && item.hasItemMeta()) {
                        String name = item.getItemMeta().getDisplayName();
                        Material material = item.getType();
                        IslandPlayer player = m.getPlayer(p);
                        if (name != null) {
                            String ISMENUITEMSPATH = "Inventories.IsMenu.Items";
                            if (name.equalsIgnoreCase(m.getC().get(p, ISMENUITEMSPATH + ".Spawn.Name")) &&
                                    material.equals(Material.valueOf(m.getC().get(p, ISMENUITEMSPATH + ".Spawn.Material")))) {
                                IslandManager.spawn(m, player, m.getIsland(player));
                            }
                            else if (name.equalsIgnoreCase(m.getC().get(p, ISMENUITEMSPATH + ".TopIslands.Name")) &&
                                    material.equals(Material.valueOf(m.getC().get(p, ISMENUITEMSPATH + ".TopIslands.Material")))) {
                                p.openInventory(player.getTopMenu().getInventory().getInventory());
                            } else if (name.equalsIgnoreCase(m.getC().get(p, ISMENUITEMSPATH + ".ResetIsland.Name")) &&
                                    material.equals(Material.valueOf(m.getC().get(p, ISMENUITEMSPATH + ".ResetIsland.Material")))) {
                                p.openInventory(player.getResetConfirmation().getInventory().getInventory());
                            } else if (name.equalsIgnoreCase(m.getC().get(p, ISMENUITEMSPATH + ".KickPlayer.Name")) &&
                                    material.equals(Material.valueOf(m.getC().get(p, ISMENUITEMSPATH + ".KickPlayer.Material")))) {
                                p.openInventory(player.getKickPlayerMenu().getInventory().getInventory());
                            } else if (name.equalsIgnoreCase(m.getC().get(p, ISMENUITEMSPATH + ".LockIsland.Name")) &&
                                    material.equals(Material.valueOf(m.getC().get(p, ISMENUITEMSPATH + ".LockIsland.Material")))) {
                                IslandManager.lockIsland(m, player, m.getIsland(player));
                            } else if (name.equalsIgnoreCase(m.getC().get(p, ISMENUITEMSPATH + ".AddPlayer.Name")) &&
                                    material.equals(Material.valueOf(m.getC().get(p, ISMENUITEMSPATH + ".AddPlayer.Material")))) {
                                p.openInventory(player.getAddPlayerMenu().getInventory().getInventory());
                            } else if (name.equalsIgnoreCase(m.getC().get(p, ISMENUITEMSPATH + ".IslandLevel.Name")) &&
                                    material.equals(Material.valueOf(m.getC().get(p, ISMENUITEMSPATH + ".IslandLevel.Material")))) {
                                IslandManager.islandLevel(m, player);
                            }
                            else if (name.equalsIgnoreCase(m.getC().get(p, ISMENUITEMSPATH + ".IslandChat.Name")) &&
                                    material.equals(Material.valueOf(m.getC().get(p, ISMENUITEMSPATH + ".IslandChat.Material")))) {
                                IslandManager.islandChat(m, player, m.getIsland(player));
                            }
                            else if (name.equalsIgnoreCase(m.getC().get(p, ISMENUITEMSPATH + ".NPC.Name")) &&
                                    material.equals(Material.valueOf(m.getC().get(p, ISMENUITEMSPATH + ".NPC.Material")))) {
                                if (getItem(p, m.getNPC(p, ISMENUITEMSPATH).build()) == null) {
                                    if (m.getIsland(p).getNPC() != null) {
                                        m.getIsland(player).getNPC().remove(p);
                                        m.getIsland(player).setNPC(null);
                                    }
                                    p.closeInventory();
                                    p.getInventory().addItem(m.getNPC(p, ISMENUITEMSPATH).build());
                                    p.sendMessage(m.getMessages().get(p, "npc-gived"));
                                }
                                else {
                                    p.closeInventory();
                                    p.sendMessage(m.getMessages().get(p, "npc-already-gived"));
                                }
                            }
                            e.setCancelled(true);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onKickInvClick(InventoryClickEvent e) {
        Inventory inv = e.getClickedInventory();
        ItemStack item = e.getCurrentItem();
        HumanEntity hE = e.getWhoClicked();
        if (hE instanceof Player) {
            Player p = (Player) hE;
            if (inv != null) {
                String KICKPLAYERPATH = "Inventories.KickPlayer";
                if (inv.getName().equalsIgnoreCase(m.getC().get(p, KICKPLAYERPATH + ".Name"))) {
                    if (item != null && item.hasItemMeta()) {
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            if (item.getItemMeta().getDisplayName().equals(m.getC().get(p, KICKPLAYERPATH + ".Items.Name").replace("%player%", player.getName()))) {
                                if (!player.equals(p)) {
                                    IslandPlayer islandPlayer = m.getPlayer(p);
                                    Island island = m.getIsland(islandPlayer);
                                    IslandManager.kickPlayer(m, islandPlayer, island, m.getPlayer(player));
                                }
                                else {
                                    p.sendMessage(m.getMessages().get(p, "cannot-kick-yourself"));
                                    p.closeInventory();
                                }
                                e.setCancelled(true);
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onAddInvClick(InventoryClickEvent e) {
        Inventory inv = e.getClickedInventory();
        ItemStack item = e.getCurrentItem();
        HumanEntity hE = e.getWhoClicked();
        if (hE instanceof Player) {
            Player p = (Player) hE;
            if (inv != null) {
                String ADDPLAYERPATH = "Inventories.AddPlayer";
                if (inv.getName().equalsIgnoreCase(m.getC().get(p, ADDPLAYERPATH + ".Name"))) {
                    if (item != null && item.hasItemMeta()) {
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            if (item.getItemMeta().getDisplayName().equals(m.getC().get(p, ADDPLAYERPATH + ".Items.Name").replace("%player%", player.getName()))) {
                                if (!player.equals(p)) {
                                    IslandPlayer islandPlayer = m.getPlayer(p);
                                    Island island = m.getIsland(islandPlayer);
                                    IslandManager.addPlayer(m, islandPlayer, island, m.getPlayer(player));
                                }
                                else {
                                    p.sendMessage(m.getMessages().get(p, "cannot-add-yourself"));
                                    p.closeInventory();
                                }
                                e.setCancelled(true);
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onTopInvClick(InventoryClickEvent e) {
        Inventory inv = e.getClickedInventory();
        ItemStack item = e.getCurrentItem();
        HumanEntity hE = e.getWhoClicked();
        if (hE instanceof Player) {
            Player p = (Player) hE;
            IslandPlayer player = m.getPlayer(p);
            if (inv != null) {
                String TOPMENUPATH = "Inventories.TopMenu";
                if (inv.getName().equalsIgnoreCase(m.getC().get(p, TOPMENUPATH + ".Name"))) {
                    if (item.getType().equals(Material.getMaterial(397)) && item.getDurability() == 3) {
                        SkullMeta meta = (SkullMeta) item.getItemMeta();
                        IslandManager.teleport(m, player, m.getIsland(m.getPlayer(Bukkit.getPlayer(meta.getOwner()))));
                    }
                }
            }
        }
    }

    private boolean containsItem(Player p, ItemStack i) {
        Inventory inv = p.getInventory();
        for (ItemStack item : inv.getContents()) {
            if (item != null) {
                if (item.getType() == i.getType()) {
                    return true;
                }
            }
        }
        return false;
    }

    private ItemStack getItem(Player p, ItemStack i) {
        Inventory inv = p.getInventory();
        for (ItemStack item : inv.getContents()) {
            if (item != null) {
                if (item.getType().equals(i.getType())) {
                    return item;
                }
            }
        }
        return null;
    }

    private void removeItem(Player p, ItemStack i) {
        Inventory inv = p.getInventory();
        for (ItemStack item : inv.getContents()) {
            if (item != null) {
                if (item.getType().equals(i.getType())) {
                    i.setAmount(i.getAmount()-1);
                }
            }
        }
    }
}
