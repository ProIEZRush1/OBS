package me.proiezrush.oneskyblock.island;

import me.proiezrush.oneskyblock.Main;
import me.proiezrush.oneskyblock.dependencies.Holograms;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.List;

public class Holo {

    private Location loc;
    public Holo(Location loc) {
        this.loc = loc;
    }

    private int task;
    public void startTimer(Main m, int timer) {
        List<String> list = m.getMessages().getList("holograms");
        this.task = Bukkit.getScheduler().scheduleSyncRepeatingTask(m, () -> {
            int random = m.getRandomNumberInRange(0, list.size()-1);
            Holograms.setFirstLine(loc, m.getAdm().chatColor(list.get(random)));
        }, 0L, timer);
    }

    public void stopTimer() {
        Bukkit.getScheduler().cancelTask(this.task);
    }

}
