package wtf.melonthedev.survivalprojektplugin.listeners.entitylisteners;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import wtf.melonthedev.survivalprojektplugin.Main;

import java.util.Objects;

public class EntityHangingListeners implements Listener {

    @EventHandler
    public void onHangingPlace(HangingPlaceEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getEntity();
        if (player == null) return;
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        if (entity.getType() != EntityType.ITEM_FRAME) return;
        if (mainHand.getType() != Material.ITEM_FRAME || !mainHand.hasItemMeta()) return;
        ItemMeta meta = mainHand.getItemMeta();
        if (meta == null) return;
        PersistentDataContainer container = meta.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(Main.getPlugin(), "invisible_item_frame");
        byte visible = container.get(key, PersistentDataType.BYTE);
        if (container.has(key, PersistentDataType.BYTE) && visible == (byte) 1) {
            ItemFrame frame = (ItemFrame) event.getEntity();
            frame.setVisible(false);
            frame.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte) 1);
        }
    }

    @EventHandler
    public void onHangingBreak(HangingBreakEvent event) {
        Entity entity = event.getEntity();
        if (entity.getType() != EntityType.ITEM_FRAME) return;
        PersistentDataContainer container = entity.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(Main.getPlugin(), "invisible_item_frame");
        if (!container.has(key, PersistentDataType.BYTE)) return;
        byte visible = container.get(key, PersistentDataType.BYTE);
        if (container.has(key, PersistentDataType.BYTE) && visible == (byte) 1) {
            Location loc = event.getEntity().getLocation();
            event.getEntity().remove();
            event.setCancelled(true);
            ItemStack stack = new ItemStack(Material.ITEM_FRAME, 1);
            ItemMeta meta = stack.getItemMeta();
            assert meta != null;
            meta.setDisplayName(ChatColor.RESET + "Invisible Item Frame");
            meta.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte) 1);
            stack.setItemMeta(meta);
            Objects.requireNonNull(loc.getWorld()).dropItem(loc, stack);
        }
    }
}