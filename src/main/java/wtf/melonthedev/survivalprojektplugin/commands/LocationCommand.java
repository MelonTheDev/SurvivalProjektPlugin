package wtf.melonthedev.survivalprojektplugin.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
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
import java.util.List;
import static wtf.melonthedev.survivalprojektplugin.Main.*;

public class LocationCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (CommandUtils.isDisabled(sender, "location")) return true;
        if (!PlayerUtils.isPlayer(sender)) return true;
        Player p = (Player) sender;
        FileConfiguration config = Main.getPlugin().getConfig();

        if (args.length != 1) {
            PlayerUtils.sendSyntaxError(p, "/location <Player: string/enable/disable>");
            return true;
        }
        if (args[0].equalsIgnoreCase("disable")) {
            config.set(p.getName() + ".location.enabled", false);
            Main.getPlugin().saveConfig();
            p.sendMessage(colorinfo + serverprefix + "Niemand kann mehr deinen Standort abrufen.");
            return true;
        } else if (args[0].equalsIgnoreCase("enable")) {
            config.set(p.getName() + ".location.enabled", true);
            Main.getPlugin().saveConfig();
            p.sendMessage(colorinfo + serverprefix + "Jeder kann jetzt deinen Standort abrufen.");
            return true;
        }

        for (Player target : Bukkit.getOnlinePlayers()) {
            //HIDDEN LOCATION
            if (!target.getName().equalsIgnoreCase(args[0]))
                continue;
            if (config.contains(target.getName() + ".location.enabled") && !config.getBoolean(target.getName() + ".location.enabled")) {
                p.sendMessage(colorerror + serverprefix + "Dieser Spieler hat seinen Standort versteckt.");
                target.sendMessage(colorinfo + serverprefix + "Der spieler " + p.getName() + " wollte deinen Standort abgerufen. OHNE ERFOLG (standort is disabled)");
                return true;
            }
            if (config.contains(target.getName() + ".isAfk") && !config.getBoolean(target.getName() + ".isAfk")) {
                p.sendMessage(colorerror + serverprefix + "Dieser Spieler ist AFK.");
                target.sendMessage(colorinfo + serverprefix + "Der spieler " + p.getName() + " wollte deinen Standort abgerufen. OHNE ERFOLG (u are AFK)");
                return true;
            }
            //LOCATION
            Location loc = target.getLocation();
            target.sendMessage(colorinfo + serverprefix + "Der spieler " + p.getName() + " hat deinen Standort abgerufen.");
            p.sendMessage(colorinfo + "Location von " + target.getName() + ": " + ChatColor.RED + "X: " + loc.getBlockX() + "," + ChatColor.GREEN + " Y: " + loc.getBlockY() + "," + ChatColor.BLUE + " Z: " + loc.getBlockZ());
            p.sendMessage(colorinfo + serverprefix + "Wir haben " + target.getName() + " darüber Informiert.");
            return true;
        }
        PlayerUtils.sendSyntaxError(p, "/location <Player: string/enable/disable>");
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> tab = new ArrayList<>();
        if (args.length == 1) {
            tab.add("disable");
            tab.add("enable");
            Bukkit.getOnlinePlayers().forEach(p -> tab.add(p.getName()));
        }
        return tab;
    }
}