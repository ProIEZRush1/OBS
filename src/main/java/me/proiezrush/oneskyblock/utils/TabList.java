package me.proiezrush.oneskyblock.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class TabList {

    private final String header;
    private final String footer;
    public TabList(String header, String footer) {
        this.header = header;
        this.footer = footer;
    }

    public void send(Player player) {
        try {
            Object header = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, getText(this.header));
            Object footer = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, getText(this.footer));
            Constructor<?> constructor = getNMSClass("PacketPlayOutPlayerListHeaderFooter").getConstructor(getNMSClass("IChatBaseComponent"));
            Object packet = constructor.newInstance(header);

            Field field = packet.getClass().getDeclaredField("b");
            field.setAccessible(true);
            field.set(packet, footer);

            sendPacket(player, packet);
        } catch (NoSuchMethodException | InstantiationException | NoSuchFieldException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
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
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        try {
            return Class.forName("net.minecraft.server." + version + "." + name);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    private String getText(String msg) {
        return "{\"text\":\"" + msg + "\"}";
    }
}
