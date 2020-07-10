package me.proiezrush.oneskyblock.island.top;

import me.proiezrush.oneskyblock.island.IslandPlayer;

import java.util.Comparator;

public class PlayerComparator implements Comparator<IslandPlayer> {

    public int compare(IslandPlayer p1, IslandPlayer p2) {
        return p1.getLevel() - p2.getLevel();
    }
}