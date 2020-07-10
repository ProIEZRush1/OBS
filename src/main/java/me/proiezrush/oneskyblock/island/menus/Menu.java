package me.proiezrush.oneskyblock.island.menus;

import me.proiezrush.oneskyblock.Main;
import me.proiezrush.oneskyblock.utils.InvBuilder;

public class Menu {

    private final InvBuilder inventory;
    public Menu(Main m, String name, int size) {
        this.inventory = new InvBuilder(m, name, size);
    }

    public InvBuilder getInventory() {
        return inventory;
    }
}
