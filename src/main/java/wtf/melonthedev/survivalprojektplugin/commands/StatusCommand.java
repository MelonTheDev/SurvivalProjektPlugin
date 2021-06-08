package wtf.melonthedev.survivalprojektplugin.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import wtf.melonthedev.survivalprojektplugin.Main;
import wtf.melonthedev.survivalprojektplugin.utils.CommandUtils;
import wtf.melonthedev.survivalprojektplugin.utils.PlayerUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static wtf.melonthedev.survivalprojektplugin.Main.*;

public class StatusCommand implements CommandExecutor, TabCompleter {



    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        FileConfiguration config = Main.getPlugin().getConfig();
        if (CommandUtils.isDisabled(sender, "status")) return true;
        if (!PlayerUtils.isPlayer(sender)) return true;
        Player p = (Player) sender;
        if (args.length == 1) {
            //OFF AND RESTORE METHODES
            if(args[0].equalsIgnoreCase("off")) {
                disableStatus(p);
                return true;
            } else if(args[0].equalsIgnoreCase("restore")) {
                p.sendMessage(colorinfo + serverprefix + "Du hast deinen Status wiederhergestellt.");
                showDisplayName(p);
                return true;
            }

            //SET DISPLAY NAME
            try {
                ChatColor.valueOf(args[0]);
                p.sendMessage(colorerror + serverprefix + "Wenn du die Farbe deines Status setzen möchtest mache '/status" + ChatColor.BOLD + " color FARBE'");
                return true;
            } catch (IllegalArgumentException ignored) {
            }
            config.set(p.getName() + ".status.status", args[0]);
            Main.getPlugin().saveConfig();
            p.sendMessage(colorinfo + serverprefix + "Dein Status ist jetzt " + args[0] + ".");
            p.playSound(p.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, SoundCategory.PLAYERS, 1, 1);
            Bukkit.getScheduler().runTask(Main.getPlugin(), ()-> showDisplayName(p));
            showDisplayName(p);
            return true;
        } else if(args.length == 2) {
            //SET COLOR
            if(!(args[0].equalsIgnoreCase("color"))) {
                p.sendMessage(colorerror + serverprefix + "Syntaxerror: /status /role <Status: String/Color: color>");
                return true;
            }
            try {
                ChatColor color = ChatColor.valueOf(args[1].toUpperCase());
                config.set(p.getName() + ".status.color", color.name());
                Main.getPlugin().saveConfig();
            } catch (IllegalArgumentException e) {
                p.sendMessage(colorerror + serverprefix + "Diese Farbe ist nicht vorhanden.");
                return true;
            }
            if (config.get(p.getName() + ".status.status") == null) {
                p.sendMessage(colorinfo + serverprefix + "Dein Status hat jetzt die Farbe " + args[1] + ". Setze ihn mit /status <Status: string>");
                showDisplayName(p);
                return true;
            }
            p.sendMessage(colorinfo + serverprefix + "Dein Status hat jetzt die Farbe " + args[1] + ".");
            p.playSound(p.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, SoundCategory.PLAYERS, 1, 1);
            showDisplayName(p);
            return true;
        }
        PlayerUtils.sendSyntaxError(p, "/status /role <Status: String/Color: color>");
        return false;
    }

    public static void showDisplayName(Player p) {
        FileConfiguration config = Main.getPlugin().getConfig();
        try {
            String status = config.getString(p.getName() + ".status.status");
            if (status == null) return;
            ChatColor color = ChatColor.valueOf(config.getString(p.getName() + ".status.color"));
            p.setDisplayName(color + "[" + status + "] " + ChatColor.RESET + p.getName());
        } catch (IllegalArgumentException | NullPointerException e) {
            config.set(p.getName() + ".status.color", "RESET");
            Main.getPlugin().saveConfig();
        }
    }

    public static void disableStatus(Player player) {
        FileConfiguration config = Main.getPlugin().getConfig();
        player.setDisplayName(null);
        player.sendMessage(colorinfo + serverprefix + "Du hast deinen Status ausgeschaltet.");
        config.set(player.getName() + ".status.status", null);
        config.set(player.getName() + "status.color", null);
        Main.getPlugin().saveConfig();
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> tab = new ArrayList<>();
        if (args.length == 1) {
            tab.add("color");
            //tab.add("hex");
            tab.add("restore");
        } else if (args.length == 2) {
            for (ChatColor color : ChatColor.values()) {
                tab.add(color.name().toLowerCase());
            }
        }
        return tab;
    }
}

