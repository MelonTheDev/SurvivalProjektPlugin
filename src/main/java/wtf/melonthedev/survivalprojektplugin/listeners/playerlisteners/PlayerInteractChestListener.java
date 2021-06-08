package wtf.melonthedev.survivalprojektplugin.listeners.playerlisteners;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.block.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import wtf.melonthedev.survivalprojektplugin.Main;
import wtf.melonthedev.survivalprojektplugin.utils.BlockUtils;

import static wtf.melonthedev.survivalprojektplugin.Main.*;

public class PlayerInteractChestListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        FileConfiguration config = Main.getPlugin().getConfig();
        Player p = e.getPlayer();

        //LOCKCHEST
        if (!e.hasBlock()) return;
        if (e.getClickedBlock() == null) return;
        if (!(e.getClickedBlock().getState() instanceof Container)) return;
        System.out.println("[DEBUG] " + BlockUtils.getContainerStateAsStirng(e.getClickedBlock().getState()) + " interact by " + e.getPlayer().getName() + " at " + e.getClickedBlock().getX() + " " + e.getClickedBlock().getY() + " " + e.getClickedBlock().getZ());
        //vanish function
        if (config.getBoolean(p.getName() + ".vanish.enabled")) {
            e.setCancelled(true);
            if (!(e.getClickedBlock().getState() instanceof Chest)) return;
            Container container = (Container) e.getClickedBlock().getState();
            Inventory inv = Bukkit.createInventory(null, container.getInventory().getSize(), "Chest (read only)");
            inv.setContents(container.getInventory().getContents());
            p.openInventory(inv);
            container.getInventory().setContents(inv.getContents());
            return;
        }
        if (BlockUtils.isLocked(e.getClickedBlock()) && !BlockUtils.hasAccess(e.getClickedBlock(), p)) {
            if (e.getAction() == Action.LEFT_CLICK_BLOCK) p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(colorerror + "Du kannst diesen Behälter nicht zerstören, da er verschlossen ist."));
            else p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(colorerror + "Du kannst diesen Behälter nicht öffnen, da er verschlossen ist."));
            e.setCancelled(true);
        }
    }
}
