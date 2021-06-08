package wtf.melonthedev.survivalprojektplugin.listeners.blocklisteners;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Objects;

import static wtf.melonthedev.survivalprojektplugin.Main.colorerror;

public class BlockClickListener implements Listener {

    @EventHandler
    public void onBlockClick(PlayerInteractEvent e) {
        if (e.getAction() != Action.LEFT_CLICK_BLOCK && e.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        //VARS
        Player p = e.getPlayer();
        Block block = e.getClickedBlock();
        if (block == null) return;

        //SIGNS
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (block.getState() instanceof Sign) {
                if (p.isSneaking()) return;
                if (!e.hasItem()) return;
                if (Objects.requireNonNull(e.getItem()).getType() != Material.FEATHER) return;
                BlockPosition position = new BlockPosition(block.getX(), block.getY(), block.getZ());
                TileEntitySign tileEntitySign = (TileEntitySign) ((CraftWorld) block.getWorld()).getHandle().getTileEntity(position);
                if (tileEntitySign == null) return;
                tileEntitySign.isEditable = true;
                tileEntitySign.a((EntityHuman)((CraftPlayer) e.getPlayer()).getHandle());
                tileEntitySign.update();
                PlayerConnection connection = ((CraftPlayer) p).getHandle().playerConnection;
                connection.sendPacket(new PacketPlayOutOpenSignEditor(position));
            } else if (block.getType() == Material.LODESTONE) {
                if (!e.hasItem()) return;
                if (Objects.requireNonNull(e.getItem()).getType() != Material.COMPASS) return;
                if (block.hasMetadata("functionalLodestone")) return;
                e.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(colorerror + "This lodestone is broken."));
                e.setCancelled(true);
            }
        }
    }
}
