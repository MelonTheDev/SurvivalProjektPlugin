package wtf.melonthedev.survivalprojektplugin.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import wtf.melonthedev.survivalprojektplugin.Main;
import wtf.melonthedev.survivalprojektplugin.utils.CommandUtils;
import wtf.melonthedev.survivalprojektplugin.utils.PlayerUtils;

public class AfkCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (CommandUtils.isDisabled(sender, "afk")) return true;
        if (!PlayerUtils.isPlayer(sender)) return true;
        Player p = (Player) sender;

        FileConfiguration config = Main.getPlugin().getConfig();
        if (args.length != 0) {
            if (args.length == 1) {
                if (!PlayerUtils.checkOP(p)) return true;
                Player target = Bukkit.getPlayer(args[0]);
                if (target == null) {
                    PlayerUtils.sendCustomError(p, "Dieser Spieler ist nicht online.");
                    return true;
                }
                if (config.getBoolean(target.getName() + ".isAfk")) {
                    PlayerUtils.setAfk(target, false);
                    return true;
                }
                PlayerUtils.setAfk(target, true);
                return true;
            }
            PlayerUtils.sendSyntaxError(p, "/afk");
            return true;
        }
        if (config.getBoolean(p.getName() + ".isAfk")) {
            PlayerUtils.setAfk(p, false);
            return true;
        }
        PlayerUtils.setAfk(p, true);
        return false;
    }
}
