package wtf.melonthedev.survivalprojektplugin.commands;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Container;
import org.bukkit.block.TileState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import wtf.melonthedev.survivalprojektplugin.Main;
import wtf.melonthedev.survivalprojektplugin.gui.InterfaceUtils;
import wtf.melonthedev.survivalprojektplugin.utils.*;
import wtf.melonthedev.survivalprojektplugin.gui.ItemStacks;

import java.util.Objects;

import static wtf.melonthedev.survivalprojektplugin.Main.*;

public class LockchestCommand implements CommandExecutor, Listener {

    FileConfiguration config = Main.getPlugin().getConfig();
    Block block;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (CommandUtils.isDisabled(sender, "lockchest")) return true;
        if (!PlayerUtils.isPlayer(sender)) return true;
        Player p = (Player) sender;
        if (args.length != 0) {
            PlayerUtils.sendSyntaxError(p, "/lockchest");
            return true;
        }
        Block block = p.getTargetBlock(null, 4);
        if (!(block.getState() instanceof Container)) {
            p.sendMessage(colorerror + serverprefix + "Du musst einen Container anschauen");
            return true;
        }
        openMainPage(p);
        return false;
    }

    @EventHandler
    public void onInvClick(InventoryClickEvent e) {
        //GENERAL
        if (e.getClickedInventory() == null) return;
        if (Objects.requireNonNull(e.getClickedInventory()).getType() == InventoryType.PLAYER) return;
        if (!e.getView().getTitle().startsWith(colorinfo + "LockChest")) return;
        Player player = (Player) e.getWhoClicked();
        String title = e.getView().getTitle();
        int slot = e.getSlot();
        Block block = player.getTargetBlock(null, 4);
        ItemStack currentItem = e.getCurrentItem();
        if (currentItem == null) return;

        //CLICK EVENTS
        if (title.equals(colorinfo + "LockChest: Select Mode")) {
            if (slot == 11) lockChest(player);
            else if (slot == 15) unlockChest(player);
            else if (slot == 22) {
                if (!BlockUtils.isLocked(block)) {
                    player.closeInventory();
                    PlayerUtils.sendCustomError(player, "Dieser Container ist nicht verschlossen.");
                    return;
                }
                if (!BlockUtils.hasAccess(block, player)) {
                    player.closeInventory();
                    PlayerUtils.sendCustomError(player, "Dieser Container ist nicht von dir verschlossen.");
                    return;
                }
                this.block = block;
                openSelectPlayerInventory(player);
            }
        } else if (title.equals(colorinfo + "LockChest: Select Trust Methode")) {
            OfflinePlayer target = SkullUtils.getSkullOwner(Objects.requireNonNull(e.getInventory().getItem(4)));
            if (slot == 11) {
                BlockUtils.addChestAccess(block, target);
                player.closeInventory();
                player.sendMessage(colorinfo + serverprefix + "Du hast " + target.getName() + " zugriff auf den Container gewährt.");
            } else if (slot == 15) {
                BlockUtils.removeChestAccess(block, target);
                player.closeInventory();
                player.sendMessage(colorinfo + serverprefix + target.getName() + " kann nun nicht mehr auf den Container zugreifen.");
            }
            else if (slot == 22) {
                this.block = block;
                openSelectPlayerInventory(player);
            }
        } else if (title.equals(colorinfo + "LockChest: Select Player")) {
            if (slot == 22) openMainPage(player);
            if (currentItem.getType() == Material.PLAYER_HEAD) {
                openTrustUpdateInventory(player, SkullUtils.getSkullOwner(currentItem));
            } else if (currentItem.getType() == Material.CREEPER_HEAD) {
                player.closeInventory();
                player.sendMessage(colorinfo + serverprefix + "Please paste the name of the player in the chat");
                config.set(player.getName() + ".waitForInput.status", true);
                config.set(player.getName() + ".waitForInput.waitForLockChestTrustName", true);
                Main.getPlugin().saveConfig();
            }
        }
    }

    private void openSelectPlayerInventory(Player player) {
        Inventory trustInv = Bukkit.createInventory(null, 27, colorinfo + "LockChest: Select Player");
        InterfaceUtils.fillPlaceholders(trustInv, ItemStacks.placeholder);
        trustInv.setItem(10, ItemStacks.blackplaceholder);
        trustInv.setItem(11, ItemStacks.blackplaceholder);
        trustInv.setItem(12, ItemStacks.blackplaceholder);
        trustInv.setItem(13, ItemStacks.blackplaceholder);
        trustInv.setItem(14, ItemStacks.blackplaceholder);
        trustInv.setItem(15, ItemStacks.blackplaceholder);
        trustInv.setItem(16, ItemStacks.blackplaceholder);
        int currentSlot = 10;
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!Objects.requireNonNull(trustInv.getItem(currentSlot)).isSimilar(ItemStacks.blackplaceholder)) return;
            if (block != null && BlockUtils.hasAccess(block, p)) continue;
            trustInv.setItem(currentSlot, ItemStacks.createSkull(p.getName(), colorinfo + p.getName(), null, 1));
            currentSlot++;
        }
        trustInv.setItem(23, ItemStacks.lockChestOfflinePlayer);
        trustInv.setItem(22, ItemStacks.arrowUp);
        player.openInventory(trustInv);
        PlayerUtils.pinInventoryContents(player);
    }

    public static void openTrustUpdateInventory(Player player, OfflinePlayer target) {
        Inventory trustInv = Bukkit.createInventory(null, 27, colorinfo + "LockChest: Select Trust Methode");
        InterfaceUtils.fillPlaceholders(trustInv, ItemStacks.placeholder);
        trustInv.setItem(11, ItemStacks.lockChestAddPlayer);
        trustInv.setItem(15, ItemStacks.lockChestRemovePlayer);
        trustInv.setItem(4, ItemStacks.createSkull(target.getName(), colorinfo + "Target: " + target.getName(), null, 1));
        trustInv.setItem(22, ItemStacks.arrowUp);
        player.openInventory(trustInv);
        PlayerUtils.pinInventoryContents(player);
    }

    public void unlockChest(Player player) {
        Block block = player.getTargetBlock(null, 4);
        setContainer(block, player, "unlock");
        if (block.getState() instanceof Chest) {
            Container container = (Container) block.getState();
            container.setLock(null);
        }
    }

    public void lockChest(Player player) {
        Block block = player.getTargetBlock(null, 4);
        setContainer(block, player, "lock");
    }

    private void openMainPage(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, colorinfo + "LockChest: Select Mode");
        InterfaceUtils.fillPlaceholders(inv, ItemStacks.placeholder);
        inv.setItem(11, ItemStacks.lockItem);
        inv.setItem(15, ItemStacks.unlockItem);
        inv.setItem(22, ItemStacks.lockChestEditPerms);
        player.openInventory(inv);
        PlayerUtils.pinInventoryContents(player);
    }

    public static void setContainer(Block block, Player player, String mode) {
        if (!(block.getState() instanceof Container)) {
            player.sendMessage(colorerror + serverprefix + "Du musst einen Container anschauen");
            player.closeInventory();
            return;
        }
        //PERSISTANT DATA CONTAINER
        if (!(block.getState() instanceof TileState)) return;
        TileState state = (TileState) block.getState();
        PersistentDataContainer container = state.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(Main.getPlugin(), "locked-chests");
        //MODE
        switch (mode) {
            case "lock":
                if (container.has(key, PersistentDataType.STRING)) {
                    PlayerUtils.sendCustomError(player, "Dieser Behälter ist schon verschlossen.");
                    player.closeInventory();
                    return;
                }
                container.set(key, PersistentDataType.STRING, player.getUniqueId().toString());
                player.sendMessage(colorinfo + serverprefix + "Du hast die Chest abgeschlossen. Unlocke sie mit '/lockchest' > Unlock Container");
                break;
            case "unlock":
                if (!BlockUtils.isLocked(block)) {
                    PlayerUtils.sendCustomError(player, "Dieser Behälter ist nicht verschlossen.");
                    player.closeInventory();
                    return;
                }
                if (!BlockUtils.hasAccess(block, player)) {
                    PlayerUtils.sendCustomError(player, "Dieser Behälter wurde nicht von dir verschlossen.");
                    player.closeInventory();
                    return;
                }
                container.remove(key);
                player.sendMessage(colorinfo + serverprefix + "Du hast den Behälter freigegeben.");
                player.closeInventory();
                break;
        }
        state.update();
        player.closeInventory();
    }
}
