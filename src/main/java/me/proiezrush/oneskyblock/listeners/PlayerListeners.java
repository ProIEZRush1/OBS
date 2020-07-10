package me.proiezrush.oneskyblock.listeners;

import me.proiezrush.oneskyblock.Main;
import me.proiezrush.oneskyblock.island.IslandPlayer;
import me.proiezrush.oneskyblock.npc.NPC;
import me.proiezrush.oneskyblock.utils.Scoreboard;
import me.proiezrush.oneskyblock.utils.TabList;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.sql.SQLException;
import java.util.List;

public class PlayerListeners implements Listener {

    private final Main m;
    public PlayerListeners(Main m) {
        this.m = m;
    }

    public static int I = 0;

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        TabList tabList = new TabList(getHeader(p), getFooter(p));
        tabList.send(p);
        Scoreboard scoreboard = new Scoreboard(m, m.getC().get(p, "Scoreboard.Title"), m.getC().getList("Scoreboard.List"));
        scoreboard.set();
        boolean a = m.getC().getBoolean("MySQL.Enabled");
        if (!a) {
            m.loadPlayer(p);
        }
        else {
            try {
                m.getMySQL().setupPlayer(new IslandPlayer(m, p));
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        IslandPlayer islandPlayer = m.getPlayer(p);
        if (islandPlayer == null) {
            islandPlayer = new IslandPlayer(m, p);
            m.addIslandPlayer(islandPlayer);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        boolean a = m.getC().getBoolean("MySQL.Enabled");
        if (!a) {
            m.saveNPC(m.getPlayer(p));
        }
        else {
            try {
                m.getMySQL().saveIslandData(m.getIsland(m.getPlayer(p)));
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        m.removeIslandPlayer(p);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        Entity en = e.getEntity();
        if (en instanceof Player) {
            Player p = (Player) en;
            IslandPlayer player = m.getPlayer(p);
            if (e.getCause().equals(EntityDamageEvent.DamageCause.VOID)) {
                e.setCancelled(true);
                p.teleport(m.getIsland(player).getSpawn());
            }
            else if (e.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
                e.setCancelled(true);
            }
            else {
                if (!player.isInIsland()) {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent e) {
        Entity en = e.getEntity();
        if (en instanceof Player) {
            Player p = (Player) en;
            IslandPlayer player = m.getPlayer(p);
            if (!player.isInIsland()) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        IslandPlayer player = m.getPlayer(p);
        if (!player.isInIsland()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        Player p = e.getPlayer();
        ItemStack item = e.getItemDrop().getItemStack();
        if (item != null) {
            String ISMENUPATH = "Inventories.IsMenu.Items";
            if (item.equals(m.getNPC(p, ISMENUPATH).build())) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();
        IslandPlayer player = m.getPlayer(p);
        if (m.getIsland(player).getNPC() == null) {
            NPC npc = new NPC(m, m.getC().get(p, "NPCName"), m.getIsland(p).getSpawn().clone().add(new Vector(1D, 0D, 1D)));
            npc.create();
            npc.setSkin(m.getC().get(p, "NPCPlayerName"));
            npc.show(p.getPlayer());
            m.getIsland(p).setNPC(npc);
        }
    }

    @EventHandler
    public void onNPCInteract(PlayerInteractEntityEvent e) {
        I = 1;
    }

    private String getHeader(Player p) {
        List<String> list = m.getC().getList("TabList.Header");
        StringBuilder a = new StringBuilder();
        for (String s : list) {
            a.append("\n").append(s);
        }
        return m.getAdm().parsePlaceholders(p, a.toString());
    }

    private String getFooter(Player p) {
        List<String> list = m.getC().getList("TabList.Footer");
        StringBuilder a = new StringBuilder();
        for (String s : list) {
            a.append("\n").append(s);
        }
        return m.getAdm().parsePlaceholders(p, a.toString());
    }

}
