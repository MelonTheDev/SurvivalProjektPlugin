package wtf.melonthedev.survivalprojektplugin.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import wtf.melonthedev.survivalprojektplugin.utils.CommandUtils;
import wtf.melonthedev.survivalprojektplugin.utils.PlayerUtils;

import static wtf.melonthedev.survivalprojektplugin.Main.*;

public class DankeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (CommandUtils.isDisabled(sender, "danke")) return true;
        if (args.length == 0) {
            PlayerUtils.sendSyntaxError((Player) sender, "/danke <Reason>");
            return true;
        }
        StringBuilder reason = new StringBuilder();
        for (String arg : args) {
            reason.append(" ").append(arg);
        }
        Bukkit.broadcastMessage(colorerror + serverprefix + "DANKEEEE" + reason + "!!!");
        Bukkit.broadcastMessage(colorinfo + serverprefix + "VIELEN VIELEN DANK");
        Bukkit.broadcastMessage(ChatColor.GOLD + serverprefix + "Grüße von " + sender.getName());
        return false;
    }
}
