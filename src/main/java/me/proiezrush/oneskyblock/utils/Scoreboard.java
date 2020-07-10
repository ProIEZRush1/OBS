package me.proiezrush.oneskyblock.utils;

import me.proiezrush.oneskyblock.Main;
import me.proiezrush.oneskyblock.netherboard.BPlayerBoard;
import me.proiezrush.oneskyblock.netherboard.Netherboard;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class Scoreboard {

    private Main m;
    private String title;
    private List<String> list;
    private int taskID;
    public Scoreboard(Main m, String title, List<String> list) {
        this.m = m;
        this.title = title;
        this.list = list;
    }

    public void set() {
        taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(m, () -> {
            for (Player players : Bukkit.getOnlinePlayers()) {
                update(players);
            }
        }, 0, 20L);
    }

    private void update(Player player) {
        BPlayerBoard board = Netherboard.instance().getBoard(player);
        if (board == null) {
            board = Netherboard.instance().createBoard(player, m.getAdm().parsePlaceholders(player, title));
        }
        for (int i=0;i<list.size();i++) {
            int pos = list.size()-i;
            board.set(m.getAdm().parsePlaceholders(player, list.get(i)), pos);
        }
    }

    public void remove(Player player) {
        BPlayerBoard board = Netherboard.instance().getBoard(player);
        if (board != null) {
            Netherboard.instance().removeBoard(player);
        }
    }

}
