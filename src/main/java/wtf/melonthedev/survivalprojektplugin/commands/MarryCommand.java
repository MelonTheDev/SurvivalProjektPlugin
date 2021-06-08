package wtf.melonthedev.survivalprojektplugin.commands;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import wtf.melonthedev.survivalprojektplugin.Main;
import wtf.melonthedev.survivalprojektplugin.gui.InterfaceUtils;
import wtf.melonthedev.survivalprojektplugin.gui.ItemStacks;
import wtf.melonthedev.survivalprojektplugin.utils.*;

import java.util.Objects;
import static wtf.melonthedev.survivalprojektplugin.Main.*;

public class MarryCommand implements CommandExecutor, Listener {

    FileConfiguration config = Main.getPlugin().getConfig();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (CommandUtils.isDisabled(sender, "marry")) return true;
        if (!PlayerUtils.isPlayer(sender)) return true;
        Player p = (Player) sender;
        if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                PlayerUtils.sendCustomError(p, "Dieser Spieler ist nicht online.");
                return true;
            }
            if (config.contains(p.getName() + ".marry.antrag")) {
                PlayerUtils.sendCustomError(p, "Bitte lehne deinen aktuellen Antrag erst ab.");
                return true;
            }
            if (config.contains(p.getName() + ".marry.isMarried")) {
                PlayerUtils.sendCustomError(p, "Du bist schon verheiratet LMAO.");
                return true;
            }
            if (config.contains(target.getName() + ".marry.antrag")) {
                PlayerUtils.sendCustomError(p, "Diese Person hat schon einen Antrag.");
                return true;
            }
            if (config.contains(target.getName() + ".marry.isMarried")) {
                PlayerUtils.sendCustomError(p, "Diese Person ist schon verheiratet.");
                return true;
            }
            if (target.getName().equals(p.getName())) {
                PlayerUtils.sendCustomError(p, "DU KANNST DICH NICHT SELBST HEIRATEN AAAHHH.");
                return true;
            }
            config.set(target.getName() + ".marry.antrag", p.getName());
            Main.getPlugin().saveConfig();
            p.sendMessage(colorinfo + serverprefix + "Du hast " + target.getName() + " einen Heiratsantrag geschickt.");
            target.sendMessage(colorinfo + serverprefix + p.getName() + " hat dir einen Heiratsantrag geschickt. Nehme ihn mit '/marry' an!");
            return true;
        } else  if (args.length != 0) {
            PlayerUtils.sendSyntaxError(p, "/marry <Player>");
            return true;
        }

        if (config.getBoolean(p.getName() + ".marry.isMarried")) {
            if (!config.contains(p.getName() + ".marry.partner")) {
                resetMarry(p);
                PlayerUtils.sendCustomError(p, "Da stimmt etwas nicht! Dein Partner wurde nicht gefunden.");
                return true;
            }
            //WENN MARRIED
            OfflinePlayer partner = Bukkit.getOfflinePlayer(Objects.requireNonNull(config.getString(p.getName() + ".marry.partner")));
            Inventory inv = Bukkit.createInventory(null, 27, colorinfo + "Deine Ehe: Partner: " + partner.getName());
            InterfaceUtils.fillPlaceholders(inv, ItemStacks.placeholder);
            inv.setItem(6, ItemStacks.marryBackGroundRed);
            inv.setItem(7, ItemStacks.marryBackGroundRed);
            inv.setItem(8, ItemStacks.marryBackGroundRed);
            inv.setItem(10, ItemStacks.marryHome);
            inv.setItem(12, ItemStacks.marryTpa);
            inv.setItem(14, ItemStacks.marryBackGroundRed);
            inv.setItem(15, ItemStacks.marryBackGroundRed);
            inv.setItem(16, ItemStacks.marryQuit);
            inv.setItem(17, ItemStacks.marryBackGroundRed);
            inv.setItem(18, ItemStacks.marryOverviewInfo);
            inv.setItem(24, ItemStacks.marryBackGroundRed);
            inv.setItem(25, ItemStacks.marryBackGroundRed);
            inv.setItem(26, ItemStacks.marryBackGroundRed);
            p.openInventory(inv);
            PlayerUtils.pinInventoryContents(p);
        } else if (config.contains(p.getName() + ".marry.antrag")) {
            OfflinePlayer brautigam = Bukkit.getOfflinePlayer(Objects.requireNonNull(config.getString(p.getName() + ".marry.antrag")));
            Inventory inv = Bukkit.createInventory(null, 27, colorinfo + "Heiratsantrag von " + ChatColor.UNDERLINE + brautigam.getName());
            inv.setItem(0, ItemStacks.marryBackGroundGreen);
            inv.setItem(1, ItemStacks.marryBackGroundGreen);
            inv.setItem(2, ItemStacks.marryBackGroundGreen);
            inv.setItem(3, ItemStacks.marryBackGroundGreen);
            inv.setItem(4, ItemStacks.marryBackGroundYellow);
            inv.setItem(5, ItemStacks.marryBackGroundRed);
            inv.setItem(6, ItemStacks.marryBackGroundRed);
            inv.setItem(7, ItemStacks.marryBackGroundRed);
            inv.setItem(8, ItemStacks.marryBackGroundRed);
            inv.setItem(9, ItemStacks.marryBackGroundGreen);
            inv.setItem(10, ItemStacks.marryAccept);
            inv.setItem(11, ItemStacks.marryBackGroundGreen);
            inv.setItem(12, ItemStacks.marryBackGroundYellow);
            inv.setItem(13, ItemStacks.createItem(Material.ENCHANTED_BOOK, ChatColor.GOLD + "Möchten sie " + brautigam.getName() + " heiraten?", null, 1));
            inv.setItem(14, ItemStacks.marryBackGroundYellow);
            inv.setItem(15, ItemStacks.marryBackGroundRed);
            inv.setItem(16, ItemStacks.marryDecline);
            inv.setItem(17, ItemStacks.marryBackGroundRed);
            inv.setItem(18, ItemStacks.marryBackGroundGreen);
            inv.setItem(19, ItemStacks.marryBackGroundGreen);
            inv.setItem(20, ItemStacks.marryBackGroundGreen);
            inv.setItem(21, ItemStacks.marryBackGroundGreen);
            inv.setItem(22, ItemStacks.marryBackGroundYellow);
            inv.setItem(23, ItemStacks.marryBackGroundRed);
            inv.setItem(24, ItemStacks.marryBackGroundRed);
            inv.setItem(25, ItemStacks.marryBackGroundRed);
            inv.setItem(26, ItemStacks.marryBackGroundRed);
            p.openInventory(inv);
            PlayerUtils.pinInventoryContents(p);
        } else {
            PlayerUtils.sendCustomError(p, "Du bist nicht verheiratet und hast keinen Antrag.");
            return true;
        }
        return false;
    }

    @EventHandler
    public void onInvClick(InventoryClickEvent e) {
        if (e.getClickedInventory() == null) return;
        if (Objects.requireNonNull(e.getClickedInventory()).getType() == InventoryType.PLAYER) return;
        String title = e.getView().getTitle();
        if (!title.startsWith(colorinfo + "Heiratsantrag von ") && !title.startsWith(colorinfo + "Deine Ehe: Partner:")) return;
        Player p = (Player) e.getWhoClicked();
        int slot = e.getSlot();
        //CLICK EVENTS
        if (title.startsWith(colorinfo + "Heiratsantrag von ")) {
            Player partner = Bukkit.getPlayer(Objects.requireNonNull(config.getString(p.getName() + ".marry.antrag")));
            if (slot == 10) {
                if (partner == null) {
                    PlayerUtils.sendCustomError(p, "Bitte warte, bis dein Partner online ist. Ihr wollt doch zusammen heiraten.");
                } else {
                    marry(p, partner);
                }
                p.closeInventory();
            } else if (slot == 16) {
                if (partner == null) {
                    OfflinePlayer target = Bukkit.getOfflinePlayer(Objects.requireNonNull(config.getString(p.getName() + ".marry.antrag")));
                    p.sendMessage(colorinfo + serverprefix + "Super, du hast den Antrag abgelehnt. " + target.getName() + " war eh doof.");
                } else {
                    partner.sendMessage(colorinfo + serverprefix + "Leider hat " + p.getName() + " deinen Heiratsantrag abgelehnt. Ja, ich bin auch traurig.");
                    p.sendMessage(colorinfo + serverprefix + "Super, du hast den Antrag abgelehnt. " + partner.getName() + " war eh doof.");
                }
                resetMarry(p);
                p.closeInventory();
            }
        } else if (title.startsWith(colorinfo + "Deine Ehe: Partner:")) {
            OfflinePlayer partner = Bukkit.getOfflinePlayer(Objects.requireNonNull(config.getString(p.getName() + ".marry.partner")));
            if (slot == 10) {
                p.performCommand("home " + partner.getName());
                p.closeInventory();
            } else if (slot == 12) {
                p.performCommand("tpa " + partner.getName());
                p.closeInventory();
            }
        }
    }

    public void marry(Player partner1, Player partner2) {
        //MESSAGES & EFFECTS
        Bukkit.broadcastMessage(ChatColor.GOLD + partner1.getDisplayName() + " und " + partner2.getDisplayName() + " HABEN GEHEIRATET!!!!!!!!!!!!!!!!!!!!!");
        Bukkit.broadcastMessage(ChatColor.GOLD + "Ihr dürft euch jetzt küssen.");
        partner1.getWorld().spawnEntity(partner1.getLocation(), EntityType.FIREWORK);
        partner1.getWorld().spawnEntity(partner1.getLocation(), EntityType.FIREWORK);
        partner1.getWorld().spawnEntity(partner1.getLocation(), EntityType.FIREWORK);
        partner2.getWorld().spawnEntity(partner2.getLocation(), EntityType.FIREWORK);
        partner2.getWorld().spawnEntity(partner2.getLocation(), EntityType.FIREWORK);
        partner2.getWorld().spawnEntity(partner2.getLocation(), EntityType.FIREWORK);
        partner1.getWorld().spawnParticle(Particle.HEART, partner1.getLocation(), 60, 0.5, 0, 0.5);
        partner1.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, partner1.getLocation(), 100, 0.7, 0.7, 0.5);
        partner2.getWorld().spawnParticle(Particle.HEART, partner2.getLocation(), 100, 0.5, 0.5, 0.5);
        partner2.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, partner2.getLocation(), 100, 0.7, 0.7, 0.6);

        //CONFIG ENTRYS
        config.set(partner1.getName() + ".marry.marrytrail.time", 300);
        config.set(partner2.getName() + ".marry.marrytrail.time", 300);
        config.set(partner1.getName() + ".marry.marrytrail.enabled", true);
        config.set(partner2.getName() + ".marry.marrytrail.enabled", true);
        config.set(partner1.getName() + ".marry.antrag", null);
        config.set(partner1.getName() + ".marry.isMarried", true);
        config.set(partner1.getName() + ".marry.partner", partner2.getName());
        config.set(partner2.getName() + ".marry.antrag", null);
        config.set(partner2.getName() + ".marry.isMarried", true);
        config.set(partner2.getName() + ".marry.partner", partner1.getName());
        Main.getPlugin().saveConfig();
    }

    public void resetMarry(Player player) {
        config.set(player.getName() + ".marry", null);
        Main.getPlugin().saveConfig();
    }
}
