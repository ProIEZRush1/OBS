package me.proiezrush.oneskyblock.island;

import me.proiezrush.oneskyblock.Main;
import me.proiezrush.oneskyblock.dependencies.Holograms;
import me.proiezrush.oneskyblock.npc.NPC;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class Island {

    private final Main m;
    private final Location spawn;
    private IslandPlayer owner;
    private final List<IslandPlayer> players;
    private boolean locked;
    private Location ob;
    private final List<IslandPlayer> addedPlayers;
    private NPC npc;
    private Location welcome;
    public Island(Main m, Location spawn, IslandPlayer owner) {
        this.m = m;
        this.spawn = spawn;
        this.owner = owner;
        this.players = new ArrayList<>();
        this.locked = false;
        this.ob = null;
        this.addedPlayers = new ArrayList<>();
        this.npc = null;
        this.welcome = null;
    }

    public void setOwner(IslandPlayer owner) {
        this.owner = owner;
    }

    public void addPlayer(IslandPlayer player) {
        if (!players.contains(player)) {
            players.add(player);
        }
    }

    public void removePlayer(IslandPlayer player) {
        players.remove(player);
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public void setOb(Location ob) {
        this.ob = ob;
    }

    public void addAddedPlayer(IslandPlayer player) {
        if (!addedPlayers.contains(player)) {
            addedPlayers.add(player);
        }
    }

    public void removeAddedPlayer(IslandPlayer player) {
        addedPlayers.remove(player);
    }

    public void setNPC(NPC npc) {
        this.npc = npc;
    }

    public void setWelcome(Location welcome) {
        this.welcome = welcome;
    }

    public Location getSpawn() {
        return spawn;
    }

    public IslandPlayer getOwner() {
        return owner;
    }

    public boolean containsPlayer(IslandPlayer player) {
        return players.contains(player);
    }

    public List<IslandPlayer> getPlayers() {
        return players;
    }

    public boolean isLocked() {
        return locked;
    }

    public Location getOb() {
        return ob;
    }

    public boolean containsAddedPlayer(Player player) {
        return addedPlayers.contains(m.getPlayer(player));
    }

    public List<IslandPlayer> getAddedPlayers() {
        return addedPlayers;
    }

    public NPC getNPC() {
        return npc;
    }

    public Location getWelcome() {
        return welcome;
    }

    public void holo() {
        Holograms.createHologram(m, this.getOb().clone().add(new Vector(0D, 1D, 0D)));
        Holograms.setFirstLine(this.getOb().clone().add(new Vector(0D, 1D, 0D)), "");
        Holo holo = new Holo(this.getOb().clone().add(new Vector(0D, 1D, 0D)));
        holo.startTimer(m, 15 * 20);
        m.addHolo(this, holo);
    }

}
