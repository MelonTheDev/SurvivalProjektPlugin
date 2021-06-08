package wtf.melonthedev.survivalprojektplugin.listeners.playerlisteners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import wtf.melonthedev.survivalprojektplugin.utils.BlockUtils;

import java.util.Objects;

public class ItemMoveListener implements Listener {

    @EventHandler
    public void onItemMove(InventoryMoveItemEvent e) {
        if (BlockUtils.isLocked(Objects.requireNonNull(e.getSource().getLocation()).getBlock())) e.setCancelled(true);
    }

}
