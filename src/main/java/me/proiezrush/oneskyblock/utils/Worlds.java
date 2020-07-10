package me.proiezrush.oneskyblock.utils;

import org.bukkit.*;
import org.bukkit.generator.ChunkGenerator;

import java.io.File;
import java.util.Random;

@SuppressWarnings("deprecation")
public class Worlds {

    public World createVoidWorld(String name) {
        WorldCreator creator = new WorldCreator(name);
        creator.generator(new ChunkGenerator() {
            public byte[] generate(World world, Random random, int x, int z) {
                return new byte[32768];
            }
        });
        World world = creator.createWorld();
        world.setDifficulty(Difficulty.PEACEFUL);
        world.setSpawnFlags(true, true);
        world.setPVP(false);
        world.setStorm(false);
        world.setThundering(false);
        world.setWeatherDuration(2147483647);
        world.setAutoSave(false);
        world.setTicksPerAnimalSpawns(1);
        world.setTicksPerMonsterSpawns(1);
        world.setGameRuleValue("doMobSpawning", "false");
        world.setGameRuleValue("mobGriefing", "false");
        world.setGameRuleValue("doFireTick", "false");
        world.setGameRuleValue("showDeathMessages", "false");
        world.setSpawnLocation(0, 0, 0);
        return world;
    }

    public void deleteWorld(File file) {
        Bukkit.unloadWorld(file.getName(), false);
        if (file.isDirectory()) {
            if(file.list().length==0) {
                file.delete();
            }
            else {
                String files[] = file.list();
                for (String temp : files) {
                    File fileDelete = new File(file, temp);
                    deleteWorld(fileDelete);
                }
                if(file.list().length==0) {
                    file.delete();
                }
            }
        }
        else {
            file.delete();
        }
    }
}
