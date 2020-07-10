package me.proiezrush.oneskyblock.mysql;

import me.proiezrush.oneskyblock.Main;
import me.proiezrush.oneskyblock.island.Island;
import me.proiezrush.oneskyblock.island.IslandPlayer;
import me.proiezrush.oneskyblock.island.missions.Mission;
import me.proiezrush.oneskyblock.island.skills.Skill;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MySQL {

    private final Main m;
    private Connection connection;
    private final String host, db, user, password;
    private final int port;

    public MySQL(Main m, String host, String db, String user, String password, int port) throws SQLException, ClassNotFoundException {
        this.m = m;
        this.host = host;
        this.db = db;
        this.user = user;
        this.password = password;
        this.port = port;
        openConnection();
        setup();
    }

    private void openConnection() throws SQLException, ClassNotFoundException {
        if (connection != null && !connection.isClosed()) {
            return;
        }

        synchronized (this) {
            if (connection != null && !connection.isClosed()) {
                return;
            }
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.db, this.user, this.password);
        }
    }

    private void setup() throws SQLException {
        PreparedStatement ps = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `player_data` (UUID VARCHAR(36)," +
                " RESETS_LEFT INT, LEVEL INT, BLOCKS_MINED INT, ISLAND_CHAT BOOLEAN, SKILL TEXT, SKILL_LEVEL INT, SKILL_NUMBER INT, COMPLETED_MISSIONS TEXT)");
        ps.executeUpdate();
        PreparedStatement ps1 = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `island_data` (OWNER VARCHAR(36), SPAWN TEXT, PLAYERS LONGTEXT, LOCKED BOOLEAN, OB TEXT" +
                ", ADDED_PLAYERS LONGTEXT, NPC TEXT)");
        ps1.executeUpdate();
    }

    public void savePlayerData(IslandPlayer player) throws SQLException {
        delete(player.getPlayer());
        PreparedStatement ps = connection.prepareStatement("INSERT INTO `player_data` (UUID,RESETS_LEFT,LEVEL,BLOCKS_MINED,ISLAND_CHAT,SKILL,SKILL_LEVEL," +
                "SKILL_NUMBER,COMPLETED_MISSIONS) VALUES (?,?,?,?,?,?,?,?,?)");
        ps.setString(1, player.getPlayer().getUniqueId().toString());
        ps.setInt(2, player.getResetsLeft());
        ps.setInt(3, player.getLevel());
        ps.setInt(4, player.getBlocksMined());
        ps.setBoolean(5, player.isIslandChat());
        ps.setString(6, player.getSkill() != null ? player.getSkill().getName() : "");
        ps.setInt(7, player.getSkill() != null ? player.getSkill().getLevel() : 0);
        ps.setInt(8, player.getSkill() != null ? player.getSkill().getNumber() : 0);
        StringBuilder a = new StringBuilder();
        if (!player.getCompletedMissions().isEmpty() && player.getCompletedMissions() != null) {
            for (Mission mission : player.getCompletedMissions()) {
                if (mission != null) {
                    a.append(" ").append(mission.getName());
                }
            }
        }
        ps.setString(9, a.toString());
        ps.executeUpdate();
    }

    public void saveIslandData(Island island) throws SQLException {
        delete(island);
        PreparedStatement ps = connection.prepareStatement("INSERT INTO `island_data` (OWNER,SPAWN,PLAYERS,LOCKED,OB,ADDED_PLAYERS,NPC) VALUES (?,?,?,?,?,?,?)");
        ps.setString(1, island.getOwner().getPlayer().getUniqueId().toString());
        String a = island.getSpawn().getWorld().getName() + " " +
                island.getSpawn().getX() + " " +
                island.getSpawn().getY() + " " +
                island.getSpawn().getZ() + " " +
                island.getSpawn().getYaw() + " " +
                island.getSpawn().getPitch() + " ";
        ps.setString(2, a);
        StringBuilder b = new StringBuilder();
        if (!island.getPlayers().isEmpty() && island.getPlayers() != null) {
            for (IslandPlayer player : island.getPlayers()) {
                if (player != null) {
                    b.append(" ").append(player.getPlayer().getUniqueId().toString());
                }
            }
        }
        ps.setString(3, b.toString());
        ps.setBoolean(4, island.isLocked());
        String c = island.getOb().getWorld().getName() + " " +
                island.getOb().getX() + " " +
                island.getOb().getY() + " " +
                island.getOb().getZ() + " ";
        ps.setString(5, c);
        StringBuilder d = new StringBuilder();
        for (IslandPlayer player : island.getAddedPlayers()) {
            if (player != null) {
                d.append(" ").append(player.getPlayer().getUniqueId().toString());
            }
        }
        ps.setString(6, d.toString());
        String e;
        if (island.getNPC() != null) {
            e = island.getNPC().getLocation().getWorld().getName() + " " +
                    island.getNPC().getLocation().getX() + " " +
                    island.getNPC().getLocation().getY() + " " +
                    island.getNPC().getLocation().getZ() + " ";
        }
        else e = "";
        ps.setString(7, e);
        ps.executeUpdate();
    }

    public void setupPlayer(IslandPlayer player) throws SQLException {
        player.setResetsLeft(getResetsLeft(player));
        player.setLevel(getLevel(player));
        player.setBlocksMined(getBlocksMined(player));
        player.setIslandChat(isIslandChat(player));
        player.setSkill(getSkill(player));
        if (player.getSkill() != null) {
            player.getSkill().setLevel(getSkillLevel(player));
            player.getSkill().setNumber(getSkillNumber(player));
        }
        if (m.getIsland(player.getPlayer()) != null) {
            m.getIsland(player.getPlayer()).setOwner(player);
        }
        for (Mission mission : getCompletedMissions(player)) {
            player.addCompletedMission(mission);
        }
        setupIsland(player);
        m.addIslandPlayer(player);
    }

    private int getResetsLeft(IslandPlayer player) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT RESETS_LEFT FROM `player_data` WHERE UUID=?");
        ps.setString(1, player.getPlayer().getUniqueId().toString());
        ResultSet res = ps.executeQuery();
        String a = null;
        if (res.next()) {
            a = res.getString("RESETS_LEFT");
        }
        if (a != null) {
            return Integer.parseInt(a);
        }
        return m.getC().getInt("MaxResets");
    }

    private int getLevel(IslandPlayer player) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT LEVEL FROM `player_data` WHERE UUID=?");
        ps.setString(1, player.getPlayer().getUniqueId().toString());
        ResultSet res = ps.executeQuery();
        String a = null;
        if (res.next()) {
            a = res.getString("LEVEL");
        }
        if (a != null) {
            return Integer.parseInt(a);
        }
        return 0;
    }

    private int getBlocksMined(IslandPlayer player) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT BLOCKS_MINED FROM `player_data` WHERE UUID=?");
        ps.setString(1, player.getPlayer().getUniqueId().toString());
        ResultSet res = ps.executeQuery();
        String a = null;
        if (res.next()) {
            a = res.getString("BLOCKS_MINED");
        }
        if (a != null) {
            return Integer.parseInt(a);
        }
        return 0;
    }

    private boolean isIslandChat(IslandPlayer player) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT ISLAND_CHAT FROM `player_data` WHERE UUID=?");
        ps.setString(1, player.getPlayer().getUniqueId().toString());
        ResultSet res = ps.executeQuery();
        String a = null;
        if (res.next()) {
            a = res.getString("ISLAND_CHAT");
        }
        if (a != null) {
            return Boolean.parseBoolean(a);
        }
        return false;
    }

    private Skill getSkill(IslandPlayer player) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT SKILL FROM `player_data` WHERE UUID=?");
        ps.setString(1, player.getPlayer().getUniqueId().toString());
        ResultSet res = ps.executeQuery();
        String a = null;
        if (res.next()) {
            a = res.getString("SKILL");
        }
        if (a != null) {
            return m.getSkill(a);
        }
        return null;
    }

    private int getSkillLevel(IslandPlayer player) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT SKILL_LEVEL FROM `player_data` WHERE UUID=?");
        ps.setString(1, player.getPlayer().getUniqueId().toString());
        ResultSet res = ps.executeQuery();
        String a = null;
        if (res.next()) {
            a = res.getString("SKILL_LEVEL");
        }
        if (a != null) {
            return Integer.parseInt(a);
        }
        return 0;
    }

    private int getSkillNumber(IslandPlayer player) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT SKILL_NUMBER FROM `player_data` WHERE UUID=?");
        ps.setString(1, player.getPlayer().getUniqueId().toString());
        ResultSet res = ps.executeQuery();
        String a = null;
        if (res.next()) {
            a = res.getString("SKILL_NUMBER");
        }
        if (a != null) {
            return Integer.parseInt(a);
        }
        return 0;
    }

    private List<Mission> getCompletedMissions(IslandPlayer player) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT COMPLETED_MISSIONS FROM `player_data` WHERE UUID=?");
        ps.setString(1, player.getPlayer().getUniqueId().toString());
        ResultSet res = ps.executeQuery();
        String a = null;
        if (res.next()) {
            a = res.getString("COMPLETED_MISSIONS");
        }
        if (a != null) {
            List<Mission> missions = new ArrayList<>();
            String[] values = a.split(" ");
            for (String s : values) {
                missions.add(m.getMission(s));
            }
            return missions;
        }
        return new ArrayList<>();
    }

    private void setupIsland(IslandPlayer player) throws SQLException {
        if (m.getIsland(player.getPlayer()) != null) {
            m.getIsland(player.getPlayer()).setOwner(player);
            for (IslandPlayer players : getIslandPlayer(player)) {
                m.getIsland(player.getPlayer()).addPlayer(players);
            }
            for (IslandPlayer players : getIslandAddedPlayer(player)) {
                m.getIsland(player.getPlayer()).addAddedPlayer(players);
            }
        }
    }

    private List<IslandPlayer> getIslandPlayer(IslandPlayer player) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT PLAYERS FROM `island_data` WHERE OWNER=?");
        ps.setString(1, player.getPlayer().getUniqueId().toString());
        ResultSet res = ps.executeQuery();
        String a = null;
        if (res.next()) {
            a = res.getString("PLAYERS");
        }
        if (a != null) {
            List<IslandPlayer> players = new ArrayList<>();
            String [] values = a.split(" ");
            for (String s : values) {
                players.add(m.getPlayer(Bukkit.getPlayer(s)));
            }
            return players;
        }
        return new ArrayList<>();
    }

    private List<IslandPlayer> getIslandAddedPlayer(IslandPlayer player) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT ADDED_PLAYERS FROM `island_data` WHERE OWNER=?");
        ps.setString(1, player.getPlayer().getUniqueId().toString());
        ResultSet res = ps.executeQuery();
        String a = null;
        if (res.next()) {
            a = res.getString("ADDED_PLAYERS");
        }
        if (a != null) {
            List<IslandPlayer> players = new ArrayList<>();
            String [] values = a.split(" ");
            for (String s : values) {
                players.add(m.getPlayer(Bukkit.getPlayer(s)));
            }
            return players;
        }
        return new ArrayList<>();
    }

    public void setupIslands() throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM `island_data`");
        ResultSet res = ps.executeQuery();
        while (res.next()) {
            m.getWorlds().createVoidWorld(res.getString("SPAWN").split(" ")[0]);
            Island island = new Island(m, new Location(Bukkit.getWorld(res.getString("SPAWN").split(" ")[0]), Double.parseDouble(res.getString("SPAWN").split(" ")[1]),
                    Double.parseDouble(res.getString("SPAWN").split(" ")[2]), Double.parseDouble(res.getString("SPAWN").split(" ")[3]),
                    Float.parseFloat(res.getString("SPAWN").split(" ")[4]), Float.parseFloat(res.getString("SPAWN").split(" ")[5])), null);
            island.setLocked(res.getBoolean("LOCKED"));
            island.setOb(new Location(Bukkit.getWorld(res.getString("OB").split(" ")[0]), Double.parseDouble(res.getString("OB").split(" ")[1]),
                    Double.parseDouble(res.getString("OB").split(" ")[2]), Double.parseDouble(res.getString("OB").split(" ")[3])));
            m.addIsland(island);
        }
    }

    private void delete(Player p) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("DELETE FROM `player_data` WHERE UUID=?");
        ps.setString(1, p.getUniqueId().toString());
        ps.executeUpdate();
    }

    private void delete(Island island) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("DELETE FROM `island_data` WHERE OWNER=?");
        ps.setString(1, island.getOwner().getPlayer().getUniqueId().toString());
        ps.executeUpdate();
    }

}
