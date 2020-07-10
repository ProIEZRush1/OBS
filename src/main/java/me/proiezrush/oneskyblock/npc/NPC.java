package me.proiezrush.oneskyblock.npc;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.proiezrush.oneskyblock.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.UUID;

public class NPC {

    private final Main m;
    private final String name;
    private final Location location;
    private GameProfile gameProfile;
    private Object entityPlayer;
    public NPC(Main m, String name, Location location) {
        this.m = m;
        this.name = name;
        this.location = location;
    }

    public void create() {
        try {
            Object minecraftServer = getCraftBukkitClass("CraftServer").getMethod("getServer").invoke(Bukkit.getServer());
            Object worldServer = getCraftBukkitClass("CraftWorld").getMethod("getHandle").invoke(location.getWorld());
            this.gameProfile = new GameProfile(UUID.randomUUID(), m.getAdm().chatColor(name));

            Constructor<?> entityPlayerConstructor = getNMSClass("EntityPlayer").getDeclaredConstructors()[0];
            Constructor<?> interactManagerConstructor = getNMSClass("PlayerInteractManager").getDeclaredConstructors()[0];

            this.entityPlayer = entityPlayerConstructor.newInstance(minecraftServer, worldServer, gameProfile, interactManagerConstructor.newInstance(worldServer));
            this.entityPlayer.getClass().getMethod("setLocation", double.class, double.class, double.class, float.class, float.class).invoke(entityPlayer,
                    location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        } catch (NoSuchMethodException | InstantiationException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void show(Player player) {
        try {
            Object addPlayerEnum = getNMSClass("PacketPlayOutPlayerInfo").getDeclaredClasses()[2].getField("ADD_PLAYER").get(null);
            Constructor<?> packetPlayOutPlayerInfoConstructor = getNMSClass("PacketPlayOutPlayerInfo").getConstructor(getNMSClass("PacketPlayOutPlayerInfo").getDeclaredClasses()[2],
                    Class.forName("[Lnet.minecraft.server." + getVersion() + ".EntityPlayer;"));

            Object array = Array.newInstance(getNMSClass("EntityPlayer"), 1);
            Array.set(array, 0, this.entityPlayer);

            Object packetPlayOutPlayerInfo = packetPlayOutPlayerInfoConstructor.newInstance(addPlayerEnum, array);
            sendPacket(player, packetPlayOutPlayerInfo);

            Constructor<?> packetPlayOutNamedEntitySpawnConstructor = getNMSClass("PacketPlayOutNamedEntitySpawn").getConstructor(getNMSClass("EntityHuman"));
            Object packetPlayOutNamedEntitySpawn = packetPlayOutNamedEntitySpawnConstructor.newInstance(this.entityPlayer);
            sendPacket(player, packetPlayOutNamedEntitySpawn);

            Constructor<?> packetPlayOutEntityHeadRotationConstructor = getNMSClass("PacketPlayOutEntityHeadRotation").getConstructor(getNMSClass("Entity"), byte.class);
            float yaw = (float) this.entityPlayer.getClass().getField("yaw").get(this.entityPlayer);
            Object packetPlayOutEntityHeadRotation = packetPlayOutEntityHeadRotationConstructor.newInstance(this.entityPlayer, (byte) (yaw * 256 / 360));
            sendPacket(player, packetPlayOutEntityHeadRotation);
        } catch (IllegalAccessException | NoSuchFieldException | NoSuchMethodException | InstantiationException | InvocationTargetException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String[] getSkin(String name) {
        try {
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
            InputStreamReader reader = new InputStreamReader(url.openStream());
            String uuid = new JsonParser().parse(reader).getAsJsonObject().get("id").getAsString();

            URL url2 = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
            InputStreamReader reader2 = new InputStreamReader(url2.openStream());
            JsonObject property = new JsonParser().parse(reader2).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();
            String texture = property.get("value").getAsString();
            String signature = property.get("signature").getAsString();
            return new String[] {texture, signature};
        } catch (IOException e) {
            return new String[] {m.getC().getConfig().getString("NPCTexture"), m.getC().getConfig().getString("NPCSignature")};
        }
    }

    public void remove(Player player) {
        try {
            Object addPlayerEnum = getNMSClass("PacketPlayOutPlayerInfo").getDeclaredClasses()[2].getField("REMOVE_PLAYER").get(null);
            Constructor<?> packetPlayOutPlayerInfoConstructor = getNMSClass("PacketPlayOutPlayerInfo").getConstructor(getNMSClass("PacketPlayOutPlayerInfo").getDeclaredClasses()[2],
                    Class.forName("[Lnet.minecraft.server." + getVersion() + ".EntityPlayer;"));

            Object array = Array.newInstance(getNMSClass("EntityPlayer"), 1);
            Array.set(array, 0, this.entityPlayer);

            Object packetPlayOutPlayerInfo = packetPlayOutPlayerInfoConstructor.newInstance(addPlayerEnum, array);
            sendPacket(player, packetPlayOutPlayerInfo);

            Constructor<?> packetPlayOutEntityDestroyConstructor = getNMSClass("PacketPlayOutEntityDestroy").getConstructor(int[].class);
            Object packetPlayOutEntityDestroy = packetPlayOutEntityDestroyConstructor.newInstance(new int[] {(int) this.entityPlayer.getClass().getMethod("getId").invoke(entityPlayer)});
            sendPacket(player, packetPlayOutEntityDestroy);
        } catch (IllegalAccessException | NoSuchFieldException | NoSuchMethodException | InstantiationException | InvocationTargetException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void setSkin(String skin) {
        String[] name = getSkin(skin);
        gameProfile.getProperties().put("textures", new Property("textures", name[0], name[1]));
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

    private Class<?> getCraftBukkitClass(String name) {
        try {
            return Class.forName("org.bukkit.craftbukkit." + getVersion() + "." + name);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    private String getVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    }

    public Location getLocation() {
        return location;
    }

    public Object getEntityPlayer() {
        return entityPlayer;
    }
}
