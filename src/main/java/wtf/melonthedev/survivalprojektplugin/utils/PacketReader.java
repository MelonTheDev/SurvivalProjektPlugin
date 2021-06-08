package wtf.melonthedev.survivalprojektplugin.utils;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.Packet;
import net.minecraft.server.v1_16_R3.PacketPlayInUseEntity;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import wtf.melonthedev.survivalprojektplugin.Main;
import wtf.melonthedev.survivalprojektplugin.others.NPC;
import wtf.melonthedev.survivalprojektplugin.others.events.NpcRightClickEvent;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PacketReader {

    Channel channel;
    public static Map<UUID, Channel> channels = new HashMap<>();

    public static void injectForAll() {
        if (!(Bukkit.getOnlinePlayers().isEmpty())) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                try {
                    PacketReader reader = new PacketReader();
                    reader.inject(p);
                } catch (Exception ignored) {}
            }
        }
    }

    public static void uninjectForAll() {
        if (Bukkit.getOnlinePlayers().isEmpty()) return;
        for (Player p : Bukkit.getOnlinePlayers()) {
            try {
                PacketReader reader = new PacketReader();
                reader.uninject(p);
            } catch (Exception ignored) {}
        }
    }

    public void inject(Player p) {
        CraftPlayer craftPlayer = (CraftPlayer) p;
        channel = craftPlayer.getHandle().playerConnection.networkManager.channel;
        channels.put(p.getUniqueId(), channel);
        if (channel.pipeline().get("PacketInjector") != null) {
            return;
        }
        if (!craftPlayer.isOnline()) return;
        channel.pipeline().addAfter("decoder", "PacketInjector", new MessageToMessageDecoder<PacketPlayInUseEntity>() {
            @Override
            protected void decode(ChannelHandlerContext channelHandlerContext, PacketPlayInUseEntity packet, List<Object> arg) {
                arg.add(packet);
                readPacket(p, packet);
            }
        });
    }

    public void uninject(Player p) {
        channel = channels.get(p.getUniqueId());
        if (channel.pipeline().get("PacketInjector") != null) {
            channel.pipeline().remove("PacketInjector");
        }
    }

    public void readPacket(Player p, Packet<?> packet) {
        if (!p.isOnline()) return;
        if (packet.getClass().getSimpleName().equalsIgnoreCase("PacketPlayInUseEntity")) {
            if (getValue(packet, "action").toString().equalsIgnoreCase("ATTACK"))
                return;
            if (getValue(packet, "d").toString().equalsIgnoreCase("OFF_HAND"))
                return;
            if (getValue(packet, "action").toString().equalsIgnoreCase("INTERACT_AT"))
                return;
            int id = (int) getValue(packet, "a");
            if (getValue(packet, "action").toString().equalsIgnoreCase("INTERACT")) {
                for (EntityPlayer npc : NPC.getNPCs()) {
                    if (npc.getId() == id) {
                        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> Bukkit.getPluginManager().callEvent(new NpcRightClickEvent(p, npc)), 0);
                    }
                }
            }
        }
    }

    private Object getValue(Object instance, String name) {
        Object result = null;
        try {
            Field field = instance.getClass().getDeclaredField(name);
            field.setAccessible(true);
            result = field.get(instance);
            field.setAccessible(false);
        } catch (Exception ignored) {
        }
        return result;
    }
}
