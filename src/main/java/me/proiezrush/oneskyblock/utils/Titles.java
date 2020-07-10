package me.proiezrush.oneskyblock.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class Titles {

    private final String title;
    private final String subtitle;
    public Titles(String title, String subtitle) {
        this.title = title;
        this.subtitle = subtitle;
    }

    public void send(Player player) {
        try {
            Object enumTitle = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TITLE").get(null);
            Object titleChat = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, getText(title));

            Object enumSubtitle = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("SUBTITLE").get(null);
            Object subtitleChat = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, getText(subtitle));

            Constructor<?> titleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"), int.class, int.class, int.class);
            Object titlePacket = titleConstructor.newInstance(enumTitle, titleChat, 30, 50, 30);
            Object subtitlePacket = titleConstructor.newInstance(enumSubtitle, subtitleChat, 30, 50, 30);

            sendPacket(player, titlePacket);
            sendPacket(player, subtitlePacket);
        }

        catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    private void sendPacket(Player player, Object packet) {
        try {
            Object handle = player.getClass().getMethod("getHandle").invoke(player);
            Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
            playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
        } catch (NoSuchMethodException | NoSuchFieldException | InvocationTargetException | IllegalAccessException ignored) {

        }
    }

    private Class<?> getNMSClass(String name) {
        try {
            return Class.forName("net.minecraft.server." + getVersion() + "." + name);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    private String getVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    }

    private String getText(String msg) {
        return "{\"text\":\"" + msg + "\"}";
    }
}
