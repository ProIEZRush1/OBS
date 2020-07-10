package me.proiezrush.oneskyblock.utils;

import me.proiezrush.oneskyblock.Main;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemBuilder {

    private final ItemStack item;

    public ItemBuilder(Main m, String name, String material, int data, boolean hasLore, List<String> lore, Player p) {
        this.item = new ItemStack(Material.valueOf(material), 1, (short) data);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(m.getAdm().parsePlaceholders(p, name));
        if (hasLore) {
            List<String> l = new ArrayList<>();
            for (String s : lore) {
                l.add(m.getAdm().parsePlaceholders(p, s));
            }
            meta.setLore(l);
        }
        this.item.setItemMeta(meta);
    }

    public ItemBuilder(Main m, String name, String material, int data, boolean hasLore, List<String> lore) {
        this.item = new ItemStack(Material.valueOf(material), 1, (short) data);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(m.getAdm().chatColor(name));
        if (hasLore) {
            List<String> l = new ArrayList<>();
            for (String s : lore) {
                l.add(m.getAdm().chatColor(s));
            }
            meta.setLore(l);
        }
        this.item.setItemMeta(meta);
    }

    public ItemBuilder(Main m, String name, String owner, boolean hasLore, List<String> lore) {
        this.item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setDisplayName(m.getAdm().chatColor(name));
        if (hasLore) {
            List<String> l = new ArrayList<>();
            for (String s : lore) {
                l.add(m.getAdm().chatColor(s));
            }
            meta.setLore(l);
        }
        meta.setOwner(owner);
        this.item.setItemMeta(meta);
    }

    public ItemStack build() {
        return item;
    }
}
