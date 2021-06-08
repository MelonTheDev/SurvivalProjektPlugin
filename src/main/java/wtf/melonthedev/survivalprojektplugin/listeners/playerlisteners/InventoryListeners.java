package wtf.melonthedev.survivalprojektplugin.listeners.playerlisteners;

import org.bukkit.*;
import org.bukkit.block.ShulkerBox;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import wtf.melonthedev.survivalprojektplugin.Main;
import wtf.melonthedev.survivalprojektplugin.utils.BlockUtils;
import wtf.melonthedev.survivalprojektplugin.utils.ItemUtils;

import java.util.Objects;

import static wtf.melonthedev.survivalprojektplugin.Main.colorinfo;

public class InventoryListeners implements Listener {

    FileConfiguration config = Main.getPlugin().getConfig();

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        //VARs
        Player p = (Player) e.getWhoClicked();
        Inventory currentInventory = e.getClickedInventory();
        if (currentInventory == null) return;
        ItemStack currentItem = e.getCurrentItem();
        if (currentItem == null) return;
        ItemMeta currentItemMeta = e.getCurrentItem().getItemMeta();
        if (currentItemMeta == null) return;

        //FIXED INVENTORY
        if (config.contains(p.getName() + ".Inv.isopen") && config.getBoolean(p.getName() + ".Inv.isopen"))
            e.setCancelled(true);

        //ETC CANCELLABLES
        if (e.getView().getTitle().startsWith(ChatColor.AQUA + "Shukler Preview: ")) e.setCancelled(true);
        if (currentItem.hasItemMeta() && currentItemMeta.hasDisplayName()) if (currentItemMeta.getDisplayName().equals(colorinfo + "TEMP ARROW")) e.setCancelled(true);

        //SHULKER PREVIEW
        if (e.getClick() == ClickType.SHIFT_RIGHT) {
            if (currentItem.getType().toString().contains("SHULKER_BOX")) {
                BlockUtils.showShulkerPreview(currentItem, p);
                e.setCancelled(true);
            }
        }

        //ARROW HOLDER
        //ItemUtils.removeTempArrow(p);
    }

    @EventHandler
    public void onInvClose(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();
        Inventory inv = e.getInventory();
        InventoryType invType = inv.getType();
        if (config.get(p.getName() + ".Inv.isopen") != null && config.getBoolean(p.getName() + ".Inv.isopen")) {
            config.set(p.getName() + ".Inv.isopen", null);
            Main.getPlugin().saveConfig();
            if (invType == InventoryType.ANVIL) p.playSound(p.getLocation(), Sound.BLOCK_CHEST_CLOSE, SoundCategory.PLAYERS, 1, 1);
            else if (invType == InventoryType.BARREL) p.playSound(p.getLocation(), Sound.BLOCK_BARREL_CLOSE, SoundCategory.PLAYERS, 1, 1);
            else if (invType == InventoryType.BEACON) p.playSound(p.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, SoundCategory.PLAYERS, 1, 1);
            else if (invType == InventoryType.BLAST_FURNACE) p.playSound(p.getLocation(), Sound.BLOCK_CHEST_CLOSE, SoundCategory.PLAYERS, 1, 1);
            else if (invType == InventoryType.BREWING) p.playSound(p.getLocation(), Sound.BLOCK_BREWING_STAND_BREW, SoundCategory.PLAYERS, 1, 1);
            else if (invType == InventoryType.CARTOGRAPHY) p.playSound(p.getLocation(), Sound.BLOCK_CHEST_CLOSE, SoundCategory.PLAYERS, 1, 1);
            else if (invType == InventoryType.CHEST) p.playSound(p.getLocation(), Sound.BLOCK_CHEST_CLOSE, SoundCategory.PLAYERS, 1, 1);
            else if (invType == InventoryType.CRAFTING) p.playSound(p.getLocation(), Sound.BLOCK_CHEST_CLOSE, SoundCategory.PLAYERS, 1, 1);
            else if (invType == InventoryType.DISPENSER) p.playSound(p.getLocation(), Sound.BLOCK_DISPENSER_DISPENSE, SoundCategory.PLAYERS, 1, 1);
            else if (invType == InventoryType.DROPPER) p.playSound(p.getLocation(), Sound.BLOCK_DISPENSER_DISPENSE, SoundCategory.PLAYERS, 1, 1);
            else if (invType == InventoryType.ENCHANTING) p.playSound(p.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.PLAYERS, 1, 1);
            else if (invType == InventoryType.ENDER_CHEST) p.playSound(p.getLocation(), Sound.BLOCK_ENDER_CHEST_CLOSE, SoundCategory.PLAYERS, 1, 1);
            else if (invType == InventoryType.FURNACE) p.playSound(p.getLocation(), Sound.BLOCK_CHEST_CLOSE, SoundCategory.PLAYERS, 1, 1);
            else if (invType == InventoryType.GRINDSTONE) p.playSound(p.getLocation(), Sound.BLOCK_GRINDSTONE_USE, SoundCategory.PLAYERS, 1, 1);
            else if (invType == InventoryType.HOPPER) p.playSound(p.getLocation(), Sound.BLOCK_CHEST_CLOSE, SoundCategory.PLAYERS, 1, 1);
            else if (invType == InventoryType.LECTERN) p.playSound(p.getLocation(), Sound.BLOCK_CHEST_CLOSE, SoundCategory.PLAYERS, 1, 1);
            else if (invType == InventoryType.LOOM) p.playSound(p.getLocation(), Sound.BLOCK_CHEST_CLOSE, SoundCategory.PLAYERS, 1, 1);
            else if (invType == InventoryType.MERCHANT) p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_YES, SoundCategory.PLAYERS, 1, 1);
            else if (invType == InventoryType.SHULKER_BOX) p.playSound(p.getLocation(), Sound.BLOCK_SHULKER_BOX_CLOSE, SoundCategory.PLAYERS, 1, 1);
            else if (invType == InventoryType.SMITHING) p.playSound(p.getLocation(), Sound.BLOCK_CHEST_CLOSE, SoundCategory.PLAYERS, 1, 1);
            else if (invType == InventoryType.WORKBENCH) p.playSound(p.getLocation(), Sound.BLOCK_CHEST_CLOSE, SoundCategory.PLAYERS, 1, 1);
            else p.playSound(p.getLocation(), Sound.BLOCK_CHEST_CLOSE, SoundCategory.PLAYERS, 1, 1);
        }
    }
}
