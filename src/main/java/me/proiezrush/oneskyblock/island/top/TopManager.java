package me.proiezrush.oneskyblock.island.top;

import me.proiezrush.oneskyblock.Main;
import me.proiezrush.oneskyblock.island.IslandPlayer;

import java.util.*;

public class TopManager {

    public static List<IslandPlayer> getTopsPlayer(Main m) {
        HashMap<Integer, IslandPlayer> hashMap = new HashMap<>();
        for (IslandPlayer player : m.getIslandPlayers().values()) {
            hashMap.putIfAbsent(player.getLevel(), player);
        }
        TreeMap<Integer, IslandPlayer> sort = new TreeMap<>(hashMap);

        return new ArrayList<>(sort.values());
    }

}
