package wtf.melonthedev.survivalprojektplugin.commands;

import org.bukkit.command.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import wtf.melonthedev.survivalprojektplugin.Main;
import wtf.melonthedev.survivalprojektplugin.utils.*;
import java.util.ArrayList;
import java.util.List;
import static wtf.melonthedev.survivalprojektplugin.Main.*;

public class KillBanCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (CommandUtils.isDisabled(sender, "killban")) return true;
        if (!PlayerUtils.isPlayer(sender)) return true;
        Player p = (Player) sender;
        FileConfiguration config = Main.getPlugin().getConfig();
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("on")) {
                p.sendMessage(colorinfo + serverprefix + "Der Killban ist jetzt eingeschaltet.");
                config.set(p.getName() + ".killban.on", true);
                Main.getPlugin().saveConfig();
                return true;
            } else if (args[0].equalsIgnoreCase("off")) {
                p.sendMessage(colorinfo + serverprefix + "Der killban ist jetzt ausgeschaltet.");
                config.set(p.getName() + ".killban.on", false);
                Main.getPlugin().saveConfig();
                return true;
            }
            PlayerUtils.sendSyntaxError(p, "/killban <on/off/banntime: int>");
            return true;
        } else if (args.length == 2) {
            if (!(args[0].equalsIgnoreCase("banntime"))) {
                PlayerUtils.sendSyntaxError(p, "/killban <on/off/banntime: int>");
                return true;
            }
            try {
                int banntime = Integer.parseInt(args[1]);
                if (banntime > 6) {
                    PlayerUtils.sendCustomError(p, "Die Banntime darf nicht höher als 6 Minuten sein.");
                    return true;
                }
                if (banntime == 0) {
                    p.sendMessage(colorinfo + serverprefix + "Der Killban ist jetzt ausgeschaltet.");
                    config.set(p.getName() + ".killban." + ".on", false);
                    return true;
                } else if (banntime >= 1) {
                    config.set(p.getName() + ".killban.banntime", banntime);
                    config.set(p.getName() + ".killban.on", true);
                }
                Main.getPlugin().saveConfig();
                p.sendMessage(colorinfo + serverprefix + "Der killban wurde auf " + banntime + " Minuten gesetzt.");
            } catch (NumberFormatException e) {
                PlayerUtils.sendSyntaxError(p, "/killban <on/off/banntime: int>");
            }
            return true;
        } else {
            PlayerUtils.sendSyntaxError(p, "/killban <on/off/banntime: int>");
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> tab = new ArrayList<>();
        if (args.length == 1) {
            tab.add("bannTime");
            tab.add("on");
            tab.add("off");
        }
        return tab;
    }
}