package me.proiezrush.oneskyblock.listeners;

import me.proiezrush.oneskyblock.Main;
import me.proiezrush.oneskyblock.island.IslandPlayer;
import me.proiezrush.oneskyblock.island.cooldown.Cooldown;
import me.proiezrush.oneskyblock.listeners.breaker.Breaker;
import me.proiezrush.oneskyblock.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.List;

@SuppressWarnings("deprecation")
public class BlockListeners implements Listener {

    private final Main m;
    public BlockListeners(Main m) {
        this.m = m;
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        Player p = e.getPlayer();
        Block b = e.getBlock();
        IslandPlayer player = m.getPlayer(p);
        if (player.isInIsland()) {
            ConfigurationSection a = m.getC().getConfig().getConfigurationSection("Upgrades." + player.getLevel());
            if (a == null) {
                a = m.getC().getConfig().getConfigurationSection("Upgrades.default");
            }
            if (a != null) {
                int size = a.getInt("Size");
                if (b.getLocation().getX() == size+1 || b.getLocation().getZ() == size+1 || b.getLocation().getX() == -(size+1) || b.getLocation().getZ() == -(size+1)) {
                    e.setCancelled(true);
                }
            }
        }
        else e.setCancelled(true);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        Block b = e.getBlock();
        ItemStack item = p.getItemInHand();
        IslandPlayer player = m.getPlayer(p);
        if (player.isInIsland()) {
            if (b.equals(m.getIsland(player).getOb().clone().subtract(new Vector(0D, 1D, 0D)).getBlock())) {
                if (m.hasCooldown(player)) {
                    p.sendMessage(m.getMessages().get(p, "cooldown-active").replace("%time%", String.valueOf(m.getCooldown(player).getTime())));
                    e.setCancelled(true);
                    return;
                }

                //Cooldown
                checkCooldown(player);

                Collection<ItemStack> x = b.getDrops();
                b.setType(Material.AIR);
                if (b.getType().equals(Material.CHEST) && p.hasPermission("oneskyblock.luckychest")) {
                    luckyChest(p, m);
                }
                Bukkit.getScheduler().scheduleSyncDelayedTask(m, () -> Breaker.breakBlock(p, b, x, m), 1L);
                if (p.getGameMode().equals(GameMode.SURVIVAL)) {
                    item.setDurability((short) (item.getDurability() + 1));
                }

                //Leveling system
                checkLevels(player);

                e.setCancelled(true);
            }
        }
        else e.setCancelled(true);
    }

    @EventHandler
    public void onNPCPlace(BlockPlaceEvent e) {
        Player p = e.getPlayer();
        Block b = e.getBlock();
        String PATH = "Inventories.IsMenu.Items";
        if (b.getType().equals(Material.valueOf(m.getC().get(p, PATH + ".NPC.Material"))) && b.getData() == m.getC().getInt(PATH + ".NPC.Data")) {
            removeItem(p, p.getItemInHand());
            NPC npc = new NPC(m, m.getC().get(p, "NPCName"), b.getLocation().add(new Vector(0.5D, 0D, 0.5D)));
            npc.create();
            npc.setSkin(m.getC().get(p, "NPCPlayerName"));
            npc.show(p.getPlayer());
            m.getIsland(p).setNPC(npc);

            p.sendMessage(m.getMessages().get(p, "npc-placed"));
            e.setCancelled(true);
        }
    }

    private void luckyChest(Player p, Main m) {
        ConfigurationSection a = m.getBlocks().getConfig().getConfigurationSection("luckychest-contents");
        int random = m.getRandomNumberInRange(0, a.getKeys(false).size());
        List<String> luckyChest = m.getBlocks().getList("luckychest-contents." + random);
        for (String s : luckyChest) {
            String[] value = s.split(":");
            String material = value[0];
            int data = Integer.parseInt(value[1]);
            int amount = Integer.parseInt(value[2]);
            ItemStack item = new ItemStack(Material.valueOf(material), amount, (short) data);
            p.getInventory().addItem(item);
        }
    }

    private void checkLevels(IslandPlayer player) {
        player.addBlock();
        List<String> levels = m.getC().getList("Levels");
        for (String level : levels) {
            if (player.getBlocksMined() == Integer.parseInt(level)) {
                player.addLevel();
                player.getPlayer().sendMessage(m.getMessages().get(player.getPlayer(), "level-updated").replace("%level%", String.valueOf(player.getLevel())));
                player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.valueOf(m.getC().get(player.getPlayer(), "level-updated-sound")), 10, 10);
            }
        }
    }

    private void checkCooldown(IslandPlayer player) {
        List<String> cooldown = m.getC().getList("Cooldown");
        for (int i=0;i<cooldown.size();i++) {
            if (player.getBlocksMined() == Integer.parseInt(cooldown.get(i))) {
                m.addCooldownPlayer(player);
                String c = m.getC().get(null, "Upgrades." + player.getLevel() + ".Cooldown");
                if (c == null) {
                    c = m.getC().get(null, "Upgrades.default.Cooldown");
                }
                int a;
                try {
                    a = Integer.parseInt(c);
                }
                catch (Exception e) {
                    a = 0;
                }
                Cooldown co = new Cooldown(m, player, a);
                co.start();
                m.addCooldown(co);
            }
        }
    }

    private void removeItem(Player p, ItemStack i) {
        Inventory inv = p.getInventory();
        for (ItemStack item : inv.getContents()) {
            if (item != null) {
                if (item.getType().equals(i.getType())) {
                    if (item.getAmount() == 1) {
                        p.getInventory().removeItem(i);
                    }
                    else i.setAmount(i.getAmount()-1);
                }
            }
        }
    }
}
