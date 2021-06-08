package wtf.melonthedev.survivalprojektplugin.commands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import wtf.melonthedev.survivalprojektplugin.Main;
import wtf.melonthedev.survivalprojektplugin.gui.ItemStacks;
import wtf.melonthedev.survivalprojektplugin.utils.CommandUtils;
import wtf.melonthedev.survivalprojektplugin.utils.PlayerUtils;

import static wtf.melonthedev.survivalprojektplugin.Main.colorinfo;

public class ArrowHolderCommand implements CommandExecutor, Listener {

    FileConfiguration config = Main.getPlugin().getConfig();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (CommandUtils.isDisabled(sender, "arrowholder")) return true;
        if (!PlayerUtils.isPlayer(sender)) return true;
        Player p = (Player) sender;
        if (args.length != 0) {
            PlayerUtils.sendSyntaxError(p, "/arrowholder");
            return true;
        }
        Inventory inv = Bukkit.createInventory(null, InventoryType.DROPPER, colorinfo + "Arrowholder");
        inv.setItem(0, ItemStacks.placeholder);
        inv.setItem(1, ItemStacks.placeholder);
        inv.setItem(2, ItemStacks.placeholder);
        inv.setItem(3, ItemStacks.placeholder);
        inv.setItem(5, ItemStacks.placeholder);
        inv.setItem(6, ItemStacks.placeholder);
        inv.setItem(7, ItemStacks.placeholder);
        inv.setItem(8, ItemStacks.placeholder);
        if (config.contains(p.getName() + ".arrowholder.hasArrow") && config.getBoolean(p.getName() + ".arrowholder.hasArrow"))
            inv.setItem(4, new ItemStack(Material.ARROW));
        p.openInventory(inv);
        config.set(p.getName() + ".arrowholder.invOpen", true);
        return false;
    }

    @EventHandler
    public void onInvClick(InventoryClickEvent event) {
        if (config.contains(event.getWhoClicked().getName() + ".arrowholder.invOpen") && config.getBoolean(event.getWhoClicked().getName() + ".arrowholder.invOpen")) {
            if (event.getClickedInventory() == null) return;
            if (event.getClickedInventory().getType() == InventoryType.PLAYER) return;
            if (event.getSlot() != 4) event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInvClose(InventoryCloseEvent event) {
        if (config.contains(event.getPlayer().getName() + ".arrowholder.invOpen") && config.getBoolean(event.getPlayer().getName() + ".arrowholder.invOpen")) {
            Inventory inv = event.getPlayer().getInventory();
            ItemStack is = event.getInventory().getItem(4);
            if (is == null) config.set(event.getPlayer().getName() + ".arrowholder.hasArrow", null);
            else if (is.getType() == Material.ARROW) {
                config.set(event.getPlayer().getName() + ".arrowholder.hasArrow", true);
                is.setAmount(is.getAmount() - 1);
            } else config.set(event.getPlayer().getName() + ".arrowholder.hasArrow", null);
            if (is != null) {
                if (inv.firstEmpty() == -1) event.getPlayer().getWorld().dropItem(event.getPlayer().getLocation(), is);
                else inv.addItem(is);
            }
            config.set(event.getPlayer().getName() + ".arrowholder.invOpen", null);
            Main.getPlugin().saveConfig();
        }
    }
}
