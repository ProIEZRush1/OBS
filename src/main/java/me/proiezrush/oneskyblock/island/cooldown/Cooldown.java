package me.proiezrush.oneskyblock.island.cooldown;

import me.proiezrush.oneskyblock.Main;
import me.proiezrush.oneskyblock.island.IslandPlayer;
import org.bukkit.Bukkit;

public class Cooldown {

    private final Main m;
    private final IslandPlayer player;
    private final int time;
    private int currentTime;
    private int task;
    public Cooldown(Main m, IslandPlayer player, int time) {
        this.m = m;
        this.player = player;
        this.time = time;
        this.currentTime = time;
    }

    public void start() {
        task = Bukkit.getScheduler().scheduleSyncRepeatingTask(m, () -> {
            if (currentTime != 0 && currentTime > 0) {
                currentTime--;
            }
            else {
                stop();
            }
        }, 0L, 20L);
    }

    public void stop() {
        m.removeCooldownPlayer(player);
        Bukkit.getScheduler().cancelTask(task);
    }

    public IslandPlayer getPlayer() {
        return player;
    }

    public int getTime() {
        return time;
    }
}
