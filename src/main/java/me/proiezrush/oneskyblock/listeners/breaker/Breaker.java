package me.proiezrush.oneskyblock.listeners.breaker;

import me.proiezrush.oneskyblock.Main;
import me.proiezrush.oneskyblock.island.IslandPlayer;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.List;

public class Breaker {

    public static void breakBlock(Player p, Block b, Collection<ItemStack> drops, Main m) {
        if (p.getLocation().getBlock().equals(b)) {
            p.teleport(p.getLocation().add(new Vector(0D, 1D, 0D)));
        }
        checkAutoPickup(m, p, b, drops);
        setType(m, b, m.getPlayer(p));
    }

    private static void checkAutoPickup(Main m, Player p, Block b, Collection<ItemStack> drops) {

        boolean a = m.getBlocks().getBoolean("autopickup");
        if (a && p.hasPermission("ob.autopick")) {
            if (drops != null) {
                for (ItemStack i : drops) {
                    if (!invFull(p) || hasItem(p, i)) {
                        p.getInventory().addItem(i);
                    }
                    else {
                        p.getWorld().dropItem(b.getLocation().add(0D, 1D, 0D), i);
                    }
                }
            }
        }
        else {
            for (ItemStack item : drops) {
                b.getWorld().dropItem(b.getLocation().add(new Vector(0D,1.5,0D)), item);
            }
        }

        if (p.getLocation().subtract(new Vector(0D, 1D, 0D)).equals(b.getLocation())) {
            p.teleport(b.getLocation().add(new Vector(0D, 1D, 0D)));
            p.teleport(b.getLocation().add(new Vector(0D, 1D, 0D)));
        }
    }

    private static void setType(Main m, Block b, IslandPlayer player) {
        List<String> list = m.getBlocks().getList("blocks." + player.getLevel());
        try {
            int random = m.getRandomNumberInRange(0, list.size() - 1);
            b.setType(Material.valueOf(list.get(random)));
        }
        catch (IllegalArgumentException e) {
            list = m.getBlocks().getList("blocks.default");
            int random = m.getRandomNumberInRange(0, list.size() - 1);
            b.setType(Material.valueOf(list.get(random)));
        }
    }
    
    private static boolean invFull(Player p){
        Inventory inv = p.getInventory();
        for (ItemStack item : inv.getContents()) {
            if (item == null) {
                return false;
            }
        }
        return true;
    }

    private static boolean hasItem(Player p, ItemStack i) {
        Inventory inv = p.getInventory();
        for (ItemStack item : inv.getContents()) {
            if (item.getType() == i.getType()) {
                return true;
            }
        }
        return false;
    }
}
