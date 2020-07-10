package me.proiezrush.oneskyblock.island.missions;

public class Mission {

    private final String name;
    private int number;
    public Mission(String name) {
        this.name = name;
        this.number = 1;
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

    public int getNumber() {
        return number;
    }
}
