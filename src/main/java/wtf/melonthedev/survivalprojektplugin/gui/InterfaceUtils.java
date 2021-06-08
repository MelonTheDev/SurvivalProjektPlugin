package wtf.melonthedev.survivalprojektplugin.gui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InterfaceUtils {

    public static void fillPlaceholders(Inventory inv, ItemStack placeholder) {
        for (int i = 0; i < inv.getSize(); i++) {
            inv.setItem(i, placeholder);
        }
    }

    public static void setMainContents(Inventory inv, ItemStack slot1, ItemStack slot2, ItemStack slot3, ItemStack slot4) {
        inv.setItem(10, slot1);
        inv.setItem(12, slot2);
        inv.setItem(14, slot3);
        inv.setItem(16, slot4);
    }
}
