package wtf.melonthedev.survivalprojektplugin.others;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import wtf.melonthedev.survivalprojektplugin.Main;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

public class NPC {
    private static final List<EntityPlayer> NPC = new ArrayList<>();
    public static void loadNPCs() {
        FileConfiguration config = Config.getCustomConfig("npcs.yml");
        if (!(NPC.isEmpty()))
            return;
        if (config.getConfigurationSection("npcs") == null) {
            return;
        }
        Objects.requireNonNull(config.getConfigurationSection("npcs")).getKeys(false).forEach(npc -> {
            MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
            HashMap<String, String> npcInfos = new HashMap<>();
            npcInfos.put("name", config.getString("npcs." + npc + ".name"));
            npcInfos.put("id", config.getString("npcs." + npc + ".id"));
            npcInfos.put("uuid", config.getString("npcs." + npc + ".uuid"));
            npcInfos.put("world", config.getString("npcs." + npc + ".location.w"));
            npcInfos.put("x", config.getString("npcs." + npc + ".location.x"));
            npcInfos.put("y", config.getString("npcs." + npc + ".location.y"));
            npcInfos.put("z", config.getString("npcs." + npc + ".location.z"));
            npcInfos.put("yaw", config.getString("npcs." + npc + ".location.yaw"));
            npcInfos.put("pitch", config.getString("npcs." + npc + ".location.pitch"));
            npcInfos.put("text", config.getString("npcs." + npc + ".text"));
            npcInfos.put("signature", config.getString("npcs." + npc + ".signature"));
            if (npcInfos.get("id") != null) {
                if (Bukkit.getWorld(npcInfos.get("world")) == null) return;
                WorldServer world = ((CraftWorld) Objects.requireNonNull(Bukkit.getWorld(npcInfos.get("world")))).getHandle();
                GameProfile gameProfile = new GameProfile(UUID.fromString(npcInfos.get("uuid")), npcInfos.get("name"));
                EntityPlayer loadedNPC = new EntityPlayer(server, world, gameProfile, new PlayerInteractManager(world));
                Location loc = new Location(Bukkit.getWorld(npcInfos.get("world")), Double.parseDouble(npcInfos.get("x")), Double.parseDouble(npcInfos.get("y")), Double.parseDouble(npcInfos.get("z")), Float.parseFloat(npcInfos.get("yaw")), Float.parseFloat(npcInfos.get("pitch")));
                loadedNPC.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
                gameProfile.getProperties().put("textures", new Property("textures", npcInfos.get("text"), npcInfos.get("signature")));
                loadedNPC.getDataWatcher().set(new DataWatcherObject<>(16, DataWatcherRegistry.a), (byte)127);
                NPC.add(loadedNPC);
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getWorld().getName().equalsIgnoreCase(npcInfos.get("world")))
                        sendRequiredPackets(loadedNPC, player);
                }
                return;
            }
            config.set("npcs." + npc, null);
            Main.getPlugin().saveConfig();
        });
    }

    public static EntityPlayer createNpc(Player p, String playerSkin, String npcName) {
        FileConfiguration config = Config.getCustomConfig("npcs.yml");
        MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer world = ((CraftWorld) p.getWorld()).getHandle();
        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), npcName);
        EntityPlayer npc = new EntityPlayer(server, world, gameProfile, new PlayerInteractManager(world));
        npc.setLocation(p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ(), p.getLocation().getYaw(), p.getLocation().getPitch());
        String[] skin = getSkin(p, playerSkin);
        gameProfile.getProperties().put("textures", new Property("textures", skin[0], skin[1]));
        npc.getDataWatcher().set(new DataWatcherObject<>(16, DataWatcherRegistry.a), (byte)127);

        npc.setOnFire(100);
        NPC.add(npc);
        String npcUUID = npc.getUniqueID().toString();
        config.set("npcs." + npcUUID + ".name", npc.getName());
        config.set("npcs." + npcUUID + ".id", npc.getId());
        config.set("npcs." + npcUUID + ".uuid", npc.getUniqueID().toString());
        config.set("npcs." + npcUUID + ".text", skin[0]);
        config.set("npcs." + npcUUID + ".signature", skin[1]);
        config.set("npcs." + npcUUID + ".location.x", npc.getBukkitEntity().getLocation().getX());
        config.set("npcs." + npcUUID + ".location.y", npc.getBukkitEntity().getLocation().getY());
        config.set("npcs." + npcUUID + ".location.z", npc.getBukkitEntity().getLocation().getZ());
        config.set("npcs." + npcUUID + ".location.yaw", npc.getBukkitEntity().getLocation().getYaw());
        config.set("npcs." + npcUUID + ".location.pitch", npc.getBukkitEntity().getLocation().getPitch());
        config.set("npcs." + npcUUID + ".location.w", p.getWorld().getName());
        Config.saveCustomConfig(config);
        for (Player player : Bukkit.getOnlinePlayers()) {
            sendRequiredPackets(npc, player);
        }
        return npc;
    }

    private static String[] getSkin(Player p, String name) {
        try {
            URL apiUrl = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
            InputStreamReader reader = new InputStreamReader(apiUrl.openStream());
            String uuid = new JsonParser().parse(reader).getAsJsonObject().get("id").getAsString();
            URL sessionUrl = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
            reader = new InputStreamReader(sessionUrl.openStream());
            JsonObject property = new JsonParser().parse(reader).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();
            String texture = property.get("value").getAsString();
            String signature = property.get("signature").getAsString();
            return new String[] {texture, signature};
        } catch (Exception ignored) {
            EntityPlayer npc = ((CraftPlayer) p).getHandle();
            GameProfile profile = npc.getProfile();
            Property property = profile.getProperties().get("textures").iterator().next();
            String texture = property.getValue();
            String signature = property.getSignature();
            return new String[] {texture, signature};
        }
    }

    public static void addJoinPacket(Player player) {
        FileConfiguration config = Config.getCustomConfig("npcs.yml");
        for (EntityPlayer npc : NPC) {
            if (Objects.equals(config.getString("npcs." + npc.getUniqueID() + ".location.w"), player.getWorld().getName()))
                sendRequiredPackets(npc, player);
        }
    }


    public static void removeNPCs() {
        for (EntityPlayer npc : getNPCs()) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                PlayerConnection connection = ((CraftPlayer)p).getHandle().playerConnection;
                connection.sendPacket(new PacketPlayOutEntityDestroy(npc.getId()));
            }
        }
        NPC.clear();
    }

    public static void deleteNPC(String npcName) {
        FileConfiguration config = Config.getCustomConfig("npcs.yml");
        List<EntityPlayer> npcs = new ArrayList<>();
        for (EntityPlayer npc : NPC) {
            if (npcName.equals(npc.getName())) {
                npcs.add(npc);
                for (Player p : Bukkit.getOnlinePlayers()) {
                    PlayerConnection connection = ((CraftPlayer)p).getHandle().playerConnection;
                    connection.sendPacket(new PacketPlayOutEntityDestroy(npc.getId()));
                }
            }
        }
        if (!npcs.isEmpty()) {
            npcs.forEach(npc -> {
                config.set("npcs." + npc.getUniqueID(), null);
                Config.saveCustomConfig(config);
                NPC.remove(npc);
            });
        }
    }


    public static void moveNpc(String npcName, Location loc) {
        for (EntityPlayer npc : NPC) {
            if (!npcName.equals(npc.getName())) continue;
            double x = loc.getX();
            double y = loc.getY();
            double z = loc.getZ();
            for (Player player : Bukkit.getOnlinePlayers()) {
                PlayerConnection playerConnection = ((CraftPlayer)player).getHandle().playerConnection;
                playerConnection.sendPacket(new PacketPlayOutEntity.PacketPlayOutRelEntityMove(npc.getId(), (short)(x * 4096), (short)(y * 4096), (short)(z * 4096), true));
            }
            return;
        }
    }

    public static void teleportNPC(String npcName, Player p) {
        for (EntityPlayer npc : NPC) {
            if (npcName.equals(npc.getName())) {
                Location loc = p.getLocation();
                npc.getBukkitEntity().teleport(loc);
                npc.setLocation(p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ(), p.getLocation().getYaw(), p.getLocation().getPitch());
                p.sendMessage("LUL");
                WorldServer server = ((CraftWorld) p.getWorld()).getHandle();
                npc.teleportTo(server, new BlockPosition(p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ()));
                for (Player player : Bukkit.getOnlinePlayers()) {
                    PlayerConnection playerConnection = ((CraftPlayer)player).getHandle().playerConnection;
                    playerConnection.sendPacket(new PacketPlayOutEntityMetadata(npc.getId(), npc.getDataWatcher(), false));
                }
                return;
            }
        }
    }

    public static void sendRequiredPackets(EntityPlayer npc, Player player) {
        PlayerConnection connection = ((CraftPlayer)player).getHandle().playerConnection;
        connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, npc));
        connection.sendPacket(new PacketPlayOutNamedEntitySpawn(npc));
        connection.sendPacket(new PacketPlayOutEntityHeadRotation(npc, (byte) npc.yaw));
        connection.sendPacket(new PacketPlayOutEntityMetadata(npc.getId(), npc.getDataWatcher(), true));
        Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, npc)), 20);
    }

    public static void updateNpc(EntityPlayer npc) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            addJoinPacket(player);
            PlayerConnection playerConnection = ((CraftPlayer)player).getHandle().playerConnection;
            playerConnection.sendPacket(new PacketPlayOutEntityMetadata(npc.getId(), npc.getDataWatcher(), false));
        }
    }

    public static void disableNpcs() {
        removeNPCs();
        getNPCs().clear();
    }

    public static List<EntityPlayer> getNPCs() {
        return NPC;
    }
}
