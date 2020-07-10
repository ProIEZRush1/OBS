package me.proiezrush.oneskyblock.utils;

import me.proiezrush.oneskyblock.Main;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InvBuilder {

    private Main m;
    private Inventory inventory;
    private String name;
    private int size;
    public InvBuilder(Main m, String name, int size) {
        this.m = m;
        this.name = name;
        this.size = size;
        this.inventory = Bukkit.createInventory(null, size, m.getAdm().parsePlaceholders(null, name));
    }

    public void addItem(ItemStack item) {
        this.inventory.addItem(item);
    }

    public void addItem(int slot, ItemStack item) {
        this.inventory.setItem(slot, item);
    }

    public Inventory getInventory() {
        return inventory;
    }
}
