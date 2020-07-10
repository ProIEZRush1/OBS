package me.proiezrush.oneskyblock.island.skills;

public class Skill {

    private final String name;
    private int level;
    private int number;
    public Skill(String name) {
        this.name = name;
        this.level = 1;
        this.number = 1;
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

    public void addNumber() {
        number++;
    }

    public void removeNumber() {
        number--;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public int getNumber() {
        return number;
    }
}
