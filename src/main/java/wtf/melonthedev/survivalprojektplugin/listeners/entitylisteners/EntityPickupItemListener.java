package wtf.melonthedev.survivalprojektplugin.listeners.entitylisteners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.meta.ItemMeta;

public class EntityPickupItemListener implements Listener {

    @EventHandler
    public void onItemPickUp(EntityPickupItemEvent e) {
        if (e.getItem().getItemStack().getType() == Material.ARROW && e.getItem().getItemStack().hasItemMeta()) {
            ItemMeta meta = e.getItem().getItemStack().getItemMeta();
            assert meta != null;
            String name = ChatColor.RESET + meta.getDisplayName();
            if (meta.hasDisplayName()) {
                if (name.equals("TEMP ARROW"))
                    e.setCancelled(true);
            }
        }
    }
}
