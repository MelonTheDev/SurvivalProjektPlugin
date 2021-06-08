package wtf.melonthedev.survivalprojektplugin.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
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
import java.util.Objects;

import static wtf.melonthedev.survivalprojektplugin.Main.*;

public class TpaCommand implements CommandExecutor, TabCompleter {
    FileConfiguration config = Main.getPlugin().getConfig();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (CommandUtils.isDisabled(sender, "tpa")) return true;
        if (!PlayerUtils.isPlayer(sender)) return true;
        Player p = (Player) sender;
        if (args.length != 1) {
            PlayerUtils.sendSyntaxError(p, "/tpa <Player/Accept/Decline>");
            return true;
        }
        if (args[0].equalsIgnoreCase("accept")) {
            if (!config.contains(p.getName() + ".tpaRequest")) {
                PlayerUtils.sendCustomError(p, "Du hast keine Teleport-Anfrage.");
                return true;
            }
            Player target = Bukkit.getPlayer(Objects.requireNonNull(config.getString(p.getName() + ".tpaRequest")));
            if (target == null) {
                PlayerUtils.sendCustomError(p, "Dieser Spieler ist jetzt offline.");
                return true;
            }
            target.sendMessage(colorinfo + serverprefix + p.getName() + " hat deine Teleport-Anfrage angenommen.");
            p.sendMessage(colorinfo + serverprefix + "Du hat die Teleport-Anfrage angenommen.");
            config.set(p.getName() + ".tpaRequest", null);
            Main.getPlugin().saveConfig();
            if (p.getGameMode() != GameMode.CREATIVE) {
                if (config.getBoolean(p.getName() + ".marry.isMarried") && Objects.requireNonNull(config.getString(p.getName() + ".marry.partner")).equalsIgnoreCase(target.getName())) {
                    if (!ShopCommand.currencyCounter(2, Material.EMERALD, target)) {
                        p.sendMessage(colorinfo + serverprefix + "Dein Teleport-Partner ist zu arm. Ihm fehlen Emeralds um sich teleportieren zu können.");
                        return true;
                    }
                } else {
                    if (!ShopCommand.currencyCounter(5, Material.EMERALD, target)) {
                        p.sendMessage(colorinfo + serverprefix + "Dein Teleport-Partner ist zu arm. Ihm fehlen Emeralds um sich teleportieren zu können.");
                        return true;
                    }
                }
            }
            target.teleport(p);
            return true;
        } else if (args[0].equalsIgnoreCase("decline")) {
            if (!config.contains(p.getName() + ".tpaRequest")) {
                PlayerUtils.sendCustomError(p, "Du hast keine Teleport-Anfrage.");
                return true;
            }
            Player target = Bukkit.getPlayer(Objects.requireNonNull(config.getString(p.getName() + ".tpaRequest")));
            if (target != null) {
                target.sendMessage(colorinfo + serverprefix + p.getName() + " hat deine Teleport-Anfrage abgelehnt.");
            }
            p.sendMessage(colorinfo + serverprefix + "Du hat die Teleport-Anfrage abgelehnt.");
            config.set(p.getName() + ".tpaRequest", null);
            Main.getPlugin().saveConfig();
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            PlayerUtils.sendCustomError(p, "Dieser Spieler ist nicht online.");
            return true;
        }
        if (target.getName().equals(p.getName())) {
            PlayerUtils.sendCustomError(p, "Du kannst dir nicht selbst eine Teleport-Anfrage senden.");
            return true;
        }
        config.set(target.getName() + ".tpaRequest", p.getName());
        Main.getPlugin().saveConfig();
        p.sendMessage(colorinfo + serverprefix + "Du hast eine Teleportanfrage an " + target.getName() + " gesendet.");
        target.sendMessage(colorinfo + serverprefix + p.getName() + " hat dir eine Teleportanfrage gesendet. Nehme sie an mit /tpa accept");
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> tab = new ArrayList<>();
        tab.add("accept");
        tab.add("decline");
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getName().equals(sender.getName())) continue;
            tab.add(p.getName());
        }
        return tab;
    }
}
