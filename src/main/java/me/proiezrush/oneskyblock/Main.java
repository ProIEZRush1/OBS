package me.proiezrush.oneskyblock;

import me.proiezrush.oneskyblock.commands.*;
import me.proiezrush.oneskyblock.dependencies.ProtocolLib;
import me.proiezrush.oneskyblock.island.Holo;
import me.proiezrush.oneskyblock.island.Island;
import me.proiezrush.oneskyblock.island.IslandPlayer;
import me.proiezrush.oneskyblock.island.cooldown.Cooldown;
import me.proiezrush.oneskyblock.island.menus.Menu;
import me.proiezrush.oneskyblock.island.menus.TradingInvMenu;
import me.proiezrush.oneskyblock.island.missions.Mission;
import me.proiezrush.oneskyblock.island.skills.Skill;
import me.proiezrush.oneskyblock.listeners.*;
import me.proiezrush.oneskyblock.mysql.MySQL;
import me.proiezrush.oneskyblock.utils.ItemBuilder;
import me.proiezrush.oneskyblock.npc.NPC;
import me.proiezrush.oneskyblock.utils.Settings;
import me.proiezrush.oneskyblock.utils.Worlds;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.SQLException;
import java.util.*;

public class Main extends JavaPlugin {

    private Settings config, messages, blocks, data;
    private Admin adm;
    private Worlds worlds;
    private HashMap<Player, IslandPlayer> islandPlayers;
    private HashMap<IslandPlayer, Island> islands;
    private HashMap<Integer, Menu> tradingInventories;
    private HashMap<String, Skill> skills;
    private HashMap<String, Mission> missions;
    private List<IslandPlayer> cooldownPlayers;
    private HashMap<IslandPlayer, Cooldown> cooldowns;
    private HashMap<Island, Holo> holos;
    private MySQL mySQL;

    @Override
    public void onEnable() {
        File f = new File("plugins/OneSkyBlock");
        if (!f.exists()) {
            f.mkdir();
        }
        this.config = new Settings(this, "config", false, true);
        this.messages = new Settings(this, "messages", false, true);
        this.blocks = new Settings(this, "blocks", false, true);
        this.data = new Settings(this, "data", false, true);
        this.adm = new Admin();
        Bukkit.getConsoleSender().sendMessage(adm.chatColor("&aEnabling OneSkyBlock..."));
        this.worlds = new Worlds();
        this.islandPlayers = new HashMap<>();
        this.islands = new HashMap<>();
        this.tradingInventories = new HashMap<>();
        this.skills = new HashMap<>();
        this.missions = new HashMap<>();
        this.cooldownPlayers = new ArrayList<>();
        this.cooldowns = new HashMap<>();
        this.holos = new HashMap<>();

        loadTradingInventories();
        loadSkills();
        loadMissions();
        boolean a = config.getBoolean("MySQL.Enabled");
        if (!a) {
            Bukkit.getConsoleSender().sendMessage(adm.chatColor("&aYAML storage type..."));
            loadData();
        }
        else {
            Bukkit.getConsoleSender().sendMessage(adm.chatColor("&aMySQL storage type..."));
            Bukkit.getConsoleSender().sendMessage(adm.chatColor("&aConnecting to MySQL..."));
            try {
                mySQL = new MySQL(this, config.getConfig().getString("MySQL.Host"), config.getConfig().getString("MySQL.Database"),
                        config.getConfig().getString("MySQL.Username"), config.getConfig().getString("MySQL.Password"), config.getInt("MySQL.Port"));
            } catch (SQLException | ClassNotFoundException throwables) {
                Bukkit.getConsoleSender().sendMessage(adm.chatColor("&cConnection to MySQL failed..."));
                Bukkit.getPluginManager().disablePlugin(Bukkit.getPluginManager().getPlugin("OneSkyBlock"));
                return;
            }
            try {
                mySQL.setupIslands();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        this.getCommand("ob").setExecutor(new ObCommand(this));
        this.getCommand("trade").setExecutor(new TradeCommand(this));
        this.getCommand("missions").setExecutor(new MissionsCommand(this));
        this.getCommand("skills").setExecutor(new SkillsCommand(this));
        this.getCommand("visit").setExecutor(new VisitCommand(this));
        if (Bukkit.getPluginManager().getPlugin("ProtocolLib") != null) {
            ProtocolLib.ListenForNPCPackets(this);
            Bukkit.getConsoleSender().sendMessage(adm.chatColor("&aProtocolLib hooked..."));
        }
        else Bukkit.getConsoleSender().sendMessage(adm.chatColor("&cProtocolLib not found, disabling NPC interact..."));
        Bukkit.getPluginManager().registerEvents(new SkillsListeners(this), this);
        Bukkit.getPluginManager().registerEvents(new MissionsListeners(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerListeners(this), this);
        Bukkit.getPluginManager().registerEvents(new ClickListeners(this), this);
        Bukkit.getPluginManager().registerEvents(new BlockListeners(this), this);
        Bukkit.getPluginManager().registerEvents(new ChatListeners(this), this);
        Bukkit.getPluginManager().registerEvents(new SignListener(this), this);

        Bukkit.getConsoleSender().sendMessage(adm.chatColor("&aPlugin enabled"));
        Bukkit.getConsoleSender().sendMessage(adm.chatColor("&aMade by &e" + getDescription().getAuthors()));
        Bukkit.getConsoleSender().sendMessage(adm.chatColor("&aVersion &e" + getDescription().getVersion()));
    }

    @Override
    public void onDisable() {
        boolean a = config.getBoolean("MySQL.Enabled");
        if (!a) {
            saveData();
        }
        else {
            for (Island island : islands.values()) {
                try {
                    mySQL.saveIslandData(island);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            for (IslandPlayer player : islandPlayers.values()) {
                try {
                    mySQL.savePlayerData(player);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }
        for (World world : Bukkit.getWorlds()) {
            world.save();
        }
        for (IslandPlayer player : islandPlayers.values()) {
            this.saveNPC(player);
        }
        islandPlayers.clear();
        islands.clear();
        Bukkit.getConsoleSender().sendMessage(adm.chatColor("&cPlugin disabled"));
        Bukkit.getConsoleSender().sendMessage(adm.chatColor("&cMade by &e" + getDescription().getAuthors()));
        Bukkit.getConsoleSender().sendMessage(adm.chatColor("&cVersion &e" + getDescription().getVersion()));
    }

    public void addIslandPlayer(IslandPlayer player) {
        islandPlayers.putIfAbsent(player.getPlayer(), player);
    }

    public void removeIslandPlayer(Player player) {
        islandPlayers.remove(player);
    }

    public void addIsland(Island island) {
        islands.putIfAbsent(island.getOwner(), island);
    }

    public void removeIsland(IslandPlayer player) {
        islands.remove(player);
    }

    public void addTradingInv(int ID, Menu menu) {
        tradingInventories.putIfAbsent(ID, menu);
    }

    public void removeTradingInv(int ID) {
        tradingInventories.remove(ID);
    }

    public void addSkill(Skill skill) {
        skills.putIfAbsent(skill.getName(), skill);
    }

    public void removeSkill(String name) {
        skills.remove(name);
    }

    public void addMission(Mission mission) {
        missions.putIfAbsent(mission.getName(), mission);
    }

    public void removeMission(String name) {
        missions.remove(name);
    }

    public void addCooldownPlayer(IslandPlayer player) {
        if (!cooldownPlayers.contains(player)) {
            cooldownPlayers.add(player);
        }
    }

    public void removeCooldownPlayer(IslandPlayer player) {
        cooldownPlayers.remove(player);
    }

    public void addCooldown(Cooldown cooldown) {
        cooldowns.putIfAbsent(cooldown.getPlayer(), cooldown);
    }

    public void removeCooldown(IslandPlayer player) {
        cooldowns.remove(player);
    }

    public void addHolo(Island island, Holo holo) {
        holos.putIfAbsent(island, holo);
    }

    public void removeHolo(Island island) {
        holos.remove(island);
    }

    public Settings getC() {
        return config;
    }

    public Settings getMessages() {
        return messages;
    }

    public Settings getBlocks() {
        return blocks;
    }

    public Admin getAdm() {
        return adm;
    }

    public Worlds getWorlds() {
        return worlds;
    }

    public HashMap<Player, IslandPlayer> getIslandPlayers() {
        return islandPlayers;
    }

    public HashMap<IslandPlayer, Island> getIslands() {
        return islands;
    }

    public HashMap<Integer, Menu> getTradingInventories() {
        return tradingInventories;
    }

    public HashMap<String, Skill> getSkills() {
        return skills;
    }

    public HashMap<String, Mission> getMissions() {
        return missions;
    }

    public HashMap<IslandPlayer, Cooldown> getCooldowns() {
        return cooldowns;
    }

    public boolean hasIsland(IslandPlayer player) {
        return islands.get(player) != null;
    }

    public IslandPlayer getPlayer(Player p) {
        if (islandPlayers.get(p) != null) {
            return islandPlayers.get(p);
        }
        return null;
    }

    public Island getIsland(IslandPlayer player) {
        return islands.get(player) != null ? islands.get(player) : getIsland(player.getPlayer());
    }

    public Island getIsland(Player player) {
        for (Island island : islands.values()) {
            if (island.getSpawn().getWorld().getName().equalsIgnoreCase(player.getName() + "_OB")) {
                return island;
            }
        }
        return null;
    }

    public Menu getTradingInv(int ID) {
        if (tradingInventories.get(ID) != null) {
            return tradingInventories.get(ID);
        }
        return null;
    }

    public Skill getSkill(String name) {
        if (skills.get(name) != null) {
            return skills.get(name);
        }
        return null;
    }

    public Mission getMission(String name) {
        if (missions.get(name) != null) {
            return missions.get(name);
        }
        return null;
    }

    public boolean hasCooldown(IslandPlayer player) {
        return cooldownPlayers.contains(player);
    }

    public Cooldown getCooldown(IslandPlayer player) {
        return cooldowns.get(player);
    }

    public Holo getHolo(Island island) {
        return holos.get(island);
    }

    public MySQL getMySQL() {
        return mySQL;
    }

    public int getRandomNumberInRange(int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException("max must be greater than min");
        }
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

    public void loadTradingInventories() {
        String PATH = "TradingInventories";

        ConfigurationSection a = config.getConfig().getConfigurationSection(PATH);
        if (a != null) {
            for (String k : a.getKeys(false)) {
                String name = a.getString(k + ".Name");
                int size = a.getInt(k + ".Size");

                TradingInvMenu tradingInvMenu = new TradingInvMenu(this, name, size);

                ConfigurationSection b = a.getConfigurationSection(k + ".Items");
                for (String j : b.getKeys(false)) {
                    String iName = b.getString(j + ".Name");
                    List<String> lore = b.getStringList(j + ".Lore");
                    String material = b.getString(j + ".Material");
                    int data = b.getInt(j + ".Data");
                    int slot = b.getInt(j + ".Slot");

                    ItemBuilder item = new ItemBuilder(this, iName, material, data, true, lore);

                    tradingInvMenu.getInventory().addItem(slot, item.build());
                }
                addTradingInv(Integer.parseInt(k), tradingInvMenu);
            }
        }
    }

    public void loadSkills() {
        String PATH = "Skills";

        ConfigurationSection a = config.getConfig().getConfigurationSection(PATH);
        if (a != null) {
            for (String k : a.getKeys(false)) {
                Skill skill = new Skill(k);
                addSkill(skill);
            }
        }
    }

    public void loadMissions() {
        String PATH = "Missions";

        ConfigurationSection a = config.getConfig().getConfigurationSection(PATH);
        if (a != null) {
            for (String k : a.getKeys(false)) {
                Mission mission = new Mission(k);
                addMission(mission);
            }
        }
    }

    private void saveData() {
        for (IslandPlayer player : islandPlayers.values()) {
            if (player != null) {
                data.set("Players", null);
                String name = player.getPlayer().getName();
                data.set("Players." + name + ".ResetsLeft", player.getResetsLeft());
                data.set("Players." + name + ".Level", player.getLevel());
                data.set("Players." + name + ".BlocksMined", player.getBlocksMined());
                data.set("Players." + name + ".IslandChat", player.isIslandChat());
                data.set("Players." + name + ".Skill", player.getSkill() != null ? player.getSkill().getName() : "");
                data.set("Players." + name + ".SkillLevel", player.getSkill() != null ? player.getSkill().getLevel() : "");
                data.set("Players." + name + ".SkillNumber", player.getSkill() != null ? player.getSkill().getNumber() : "");
                List<String> x = new ArrayList<>();
                for (Mission mission : player.getCompletedMissions()) {
                    x.add(mission.getName());
                }
                data.set("Players." + name + ".CompletedMissions", x);
            }
        }
        for (Island island : islands.values()) {
            if (island != null) {
                if (island.getOwner() != null) {
                    data.set("Islands", null);
                    String name = island.getOwner().getPlayer().getName();
                    data.set("Islands." + name + ".Spawn.World", island.getSpawn().getWorld().getName());
                    data.set("Islands." + name + ".Spawn.X", island.getSpawn().getX());
                    data.set("Islands." + name + ".Spawn.Y", island.getSpawn().getY());
                    data.set("Islands." + name + ".Spawn.Z", island.getSpawn().getZ());
                    data.set("Islands." + name + ".Spawn.Yaw", island.getSpawn().getYaw());
                    data.set("Islands." + name + ".Spawn.Pitch", island.getSpawn().getPitch());
                    List<String> x = new ArrayList<>();
                    for (IslandPlayer players : island.getPlayers()) {
                        x.add(players.getPlayer().getName());
                    }
                    data.set("Islands." + name + ".Players", x);
                    data.set("Islands." + name + ".Locked", island.isLocked());
                    data.set("Islands." + name + ".OB.World", island.getOb().getWorld().getName());
                    data.set("Islands." + name + ".OB.X", island.getOb().getX());
                    data.set("Islands." + name + ".OB.Y", island.getOb().getY());
                    data.set("Islands." + name + ".OB.Z", island.getOb().getZ());
                    List<String> y = new ArrayList<>();
                    for (IslandPlayer players : island.getAddedPlayers()) {
                        y.add(players.getPlayer().getName());
                    }
                    if (island.getWelcome() != null) {
                        data.set("Islands." + name + ".Welcome.World", island.getWelcome().getWorld().getName());
                        data.set("Islands." + name + ".Welcome.X", island.getWelcome().getX());
                        data.set("Islands." + name + ".Welcome.Y", island.getWelcome().getY());
                        data.set("Islands." + name + ".Welcome.Z", island.getWelcome().getZ());
                    }
                    data.set("Islands." + name + ".AddedPlayers", y);
                }
            }
        }
        data.save();
    }

    public void saveNPC(IslandPlayer player) {
        Island island = getIsland(player);
        if (island != null) {
            String name = island.getOwner().getPlayer().getName();
            if (island.getNPC() != null) {
                data.set("Islands." + name + ".NPC.World", island.getNPC().getLocation().getWorld().getName());
                data.set("Islands." + name + ".NPC.X", island.getNPC().getLocation().getX());
                data.set("Islands." + name + ".NPC.Y", island.getNPC().getLocation().getY());
                data.set("Islands." + name + ".NPC.Z", island.getNPC().getLocation().getZ());
                data.save();
            }
        }
    }

    private void loadData() {
        ConfigurationSection a = data.getConfig().getConfigurationSection("Islands");
        if (a != null) {
            for (String k : a.getKeys(false)) {
                String sW = a.getString(k + ".Spawn.World");
                double sX = a.getDouble(k + ".Spawn.X");
                double sY = a.getDouble(k + ".Spawn.Y");
                double sZ = a.getDouble(k + ".Spawn.Z");
                float sYaw = (float) a.getDouble(k + ".Spawn.Yaw");
                float sPitch = (float) a.getDouble(k + ".Spawn.Pitch");

                boolean isLocked = a.getBoolean(k + ".Locked");

                String oW = a.getString(k + ".OB.World");
                double oX = a.getDouble(k + ".OB.X");
                double oY = a.getDouble(k + ".OB.Y");
                double oZ = a.getDouble(k + ".OB.Z");

                File f = new File(Bukkit.getWorldContainer().getAbsolutePath());
                File[] files = f.listFiles();
                if (files != null) {
                    for (File fl : files) {
                        if (fl.getName().contains("_OB")) {
                            worlds.createVoidWorld(fl.getName());
                        }
                    }
                }

                Location welcome = null;
                if (a.getConfigurationSection(k + ".Welcome") != null) {
                    String wW = a.getString(k + ".Welcome.World");
                    double wX = a.getDouble(k + ".Welcome.X");
                    double wY = a.getDouble(k + ".Welcome.Y");
                    double wZ = a.getDouble(k + ".Welcome.Z");
                    welcome = new Location(Bukkit.getWorld(wW), wX, wY, wZ);
                }

                Island island = new Island(this, new Location(Bukkit.getWorld(sW), sX, sY, sZ, sYaw, sPitch), null);
                island.setLocked(isLocked);
                island.setOb(new Location(Bukkit.getWorld(oW), oX, oY, oZ));
                island.holo();
                if (welcome != null) {
                    island.setWelcome(welcome);
                }
                addIsland(island);
            }
        }
    }

    public void loadPlayer(Player p) {

        ConfigurationSection a = data.getConfig().getConfigurationSection("Islands");
        ConfigurationSection b = data.getConfig().getConfigurationSection("Players." + p.getName());

        if (a != null && b != null) {
            int resetsLeft = b.getInt("ResetsLeft");
            int level = b.getInt("Level");
            int blocksMined = b.getInt("BlocksMined");
            boolean islandChat = b.getBoolean("IslandChat");
            String skill = b.getString("Skill");
            String skillLevel = b.getString("SkillLevel");
            String skillNumber = b.getString("SkillNumber");
            List<String> completedMissions = b.getStringList("CompletedMissions");

            IslandPlayer islandPlayer = new IslandPlayer(this, p);
            islandPlayer.setResetsLeft(resetsLeft);
            islandPlayer.setLevel(level);
            islandPlayer.setBlocksMined(blocksMined);
            islandPlayer.setIslandChat(islandChat);
            if (!skill.equals("") && !skillLevel.equals("") && !skillNumber.equals("")) {
                islandPlayer.setSkill(getSkill(skill));
                islandPlayer.getSkill().setLevel(Integer.parseInt(skillLevel));
                islandPlayer.getSkill().setNumber(Integer.parseInt(skillNumber));
            }
            for (String s : completedMissions) {
                islandPlayer.addCompletedMission(getMission(s));
            }
            addIslandPlayer(islandPlayer);

            Island island = getIsland(islandPlayer.getPlayer());
            if (island != null) {
                island.setOwner(islandPlayer);
                removeIsland(islandPlayer);

                List<String> players = a.getStringList(island.getOwner().getPlayer().getName() + ".Players");
                for (String s : players) {
                    if (getPlayer(Bukkit.getPlayer(s)) != null) {
                        island.addPlayer(getPlayer(Bukkit.getPlayer(s)));
                    }
                }

                List<String> addedPlayers = a.getStringList(island.getOwner().getPlayer().getName() + ".AddedPlayers");
                for (String s : addedPlayers) {
                    if (getPlayer(Bukkit.getPlayer(s)) != null) {
                        island.addAddedPlayer(getPlayer(Bukkit.getPlayer(s)));
                    }
                }

                String nW = a.getString(island.getOwner().getPlayer().getName() + ".NPC.World");
                double nX = a.getDouble(island.getOwner().getPlayer().getName() + ".NPC.X");
                double nY = a.getDouble(island.getOwner().getPlayer().getName() + ".NPC.Y");
                double nZ = a.getDouble(island.getOwner().getPlayer().getName() + ".NPC.Z");

                if (nW != null) {
                    NPC npc = new NPC(this, this.getC().get(p, "NPCName"), new Location(Bukkit.getWorld(nW), nX, nY, nZ));
                    npc.create();
                    npc.setSkin(this.getC().get(p, "NPCPlayerName"));
                    npc.show(p.getPlayer());
                    this.getIsland(p).setNPC(npc);
                    island.setNPC(npc);
                }

                addIsland(island);

                if (p.getWorld().getName().contains("_OB")) {
                    islandPlayer.setCurrentIsland(getIsland(p));
                }
            }
        }
    }
    
    public ItemBuilder getNPC(Player p, String ISMENUITEMSPATH) {
        return new ItemBuilder(this, this.getC().get(p, ISMENUITEMSPATH + ".NPC.Name"), this.getC().get(p, ISMENUITEMSPATH + ".NPC.Material"),
                this.getC().getInt(ISMENUITEMSPATH + ".NPC.Data"), false, null);
    }
}
