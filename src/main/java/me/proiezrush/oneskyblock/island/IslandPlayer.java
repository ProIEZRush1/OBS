package me.proiezrush.oneskyblock.island;

import me.proiezrush.oneskyblock.Main;
import me.proiezrush.oneskyblock.island.menus.*;
import me.proiezrush.oneskyblock.island.missions.Mission;
import me.proiezrush.oneskyblock.island.skills.Skill;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class IslandPlayer implements Comparable<IslandPlayer> {

    private final Main m;
    private final Player player;
    private Island currentIsland;
    private IsMenu isMenu;
    private TopMenu topMenu;
    private KickPlayerMenu kickPlayerMenu;
    private AddPlayerMenu addPlayerMenu;
    private TradingsMenu tradingsMenu;
    private SkillsMenu skillsMenu;
    private MissionsMenu missionsMenu;
    private ResetConfirmation resetConfirmation;
    private int resetsLeft;
    private int level;
    private int blocksMined;
    private boolean islandChat;
    private Skill skill;
    private final List<Mission> completedMissions;
    private final List<Mission> missions;
    public IslandPlayer(Main m, Player player) {
        this.m = m;
        this.player = player;
        this.currentIsland = null;
        this.completedMissions = new ArrayList<>();
        this.isMenu = new IsMenu(m, this);
        this.topMenu = new TopMenu(m, this);
        this.kickPlayerMenu = new KickPlayerMenu(m, this);
        this.addPlayerMenu = new AddPlayerMenu(m, this);
        this.tradingsMenu = new TradingsMenu(m, this);
        this.skillsMenu = new SkillsMenu(m, this);
        this.missionsMenu = new MissionsMenu(m, this);
        this.resetConfirmation = new ResetConfirmation(m, this);
        this.resetsLeft = m.getC().getInt("MaxResets");
        this.level = 0;
        this.blocksMined = 0;
        this.islandChat = false;
        this.skill = null;
        this.missions = new ArrayList<>(m.getMissions().values());
    }

    public void setCurrentIsland(Island currentIsland) {
        this.currentIsland = currentIsland;
    }

    public void addReset() {
        resetsLeft++;
    }

    public void removeReset() {
        resetsLeft--;
    }

    public void setResetsLeft(int resetsLeft) {
        this.resetsLeft = resetsLeft;
    }

    public void addLevel() {
        level++;
    }

    public void removeLevel() {
        level--;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void addBlock() {
        blocksMined++;
    }

    public void removeBlock() {
        blocksMined--;
    }

    public void setBlocksMined(int blocksMined) {
        this.blocksMined = blocksMined;
    }

    public void setIslandChat(boolean islandChat) {
        this.islandChat = islandChat;
    }

    public void setSkill(Skill skill) {
        this.skill = skill;
    }

    public void addCompletedMission(Mission mission) {
        completedMissions.add(mission);
    }

    public void removeCompletedMission(Mission mission) {
        completedMissions.remove(mission);
    }

    public Player getPlayer() {
        return player;
    }

    public Island getCurrentIsland() {
        return currentIsland;
    }

    public IsMenu getIsMenu() {
        isMenu = new IsMenu(m, this);
        return isMenu;
    }

    public TopMenu getTopMenu() {
        topMenu = new TopMenu(m, this);
        return topMenu;
    }

    public KickPlayerMenu getKickPlayerMenu() {
        kickPlayerMenu = new KickPlayerMenu(m, this);
        return kickPlayerMenu;
    }

    public AddPlayerMenu getAddPlayerMenu() {
        addPlayerMenu = new AddPlayerMenu(m, this);
        return addPlayerMenu;
    }

    public TradingsMenu getTradingsMenu() {
        tradingsMenu = new TradingsMenu(m, this);
        return tradingsMenu;
    }

    public SkillsMenu getSkillsMenu() {
        skillsMenu = new SkillsMenu(m, this);
        return skillsMenu;
    }

    public MissionsMenu getMissionsMenu() {
        missionsMenu = new MissionsMenu(m, this);
        return missionsMenu;
    }

    public ResetConfirmation getResetConfirmation() {
        return resetConfirmation;
    }

    public int getResetsLeft() {
        return resetsLeft;
    }

    public int getLevel() {
        return level;
    }

    public int getBlocksMined() {
        return blocksMined;
    }

    public boolean isIslandChat() {
        return islandChat;
    }

    public Skill getSkill() {
        return skill;
    }

    public List<Mission> getCompletedMissions() {
        return completedMissions;
    }

    public List<Mission> getMissions() {
        return missions;
    }

    public boolean hasResetsLeft() {
        return resetsLeft != 0;
    }

    public boolean isInIsland() {
        return player.getWorld().getName().equals(player.getName() + "_OB");
    }

    public boolean hasCompletedMission(Mission mission) {
        return completedMissions.contains(mission);
    }

    public Mission getMission(String name) {
        for (Mission mission : missions) {
            if (mission.getName().equalsIgnoreCase(name)){
                return mission;
            }
        }
        return null;
    }

    @Override
    public int compareTo(IslandPlayer o) {
        int level = o.getLevel();
        return this.level - level;
    }
}
