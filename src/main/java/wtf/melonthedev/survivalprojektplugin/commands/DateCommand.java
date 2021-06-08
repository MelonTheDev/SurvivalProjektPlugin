package wtf.melonthedev.survivalprojektplugin.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import wtf.melonthedev.survivalprojektplugin.utils.CommandUtils;

import java.util.Date;
import static wtf.melonthedev.survivalprojektplugin.Main.*;

public class DateCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (CommandUtils.isDisabled(sender, "date")) return true;
        Date date = new Date();
        sender.sendMessage(colorinfo + serverprefix + "Es ist: " + date.toString() + ".");
        return false;
    }
}
