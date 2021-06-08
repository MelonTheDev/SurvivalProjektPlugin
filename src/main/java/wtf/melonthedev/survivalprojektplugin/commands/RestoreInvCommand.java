package wtf.melonthedev.survivalprojektplugin.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import wtf.melonthedev.survivalprojektplugin.utils.CommandUtils;
import wtf.melonthedev.survivalprojektplugin.utils.PlayerUtils;

public class RestoreInvCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (CommandUtils.isDisabled(sender, "lockchest")) return true;
        if (!PlayerUtils.checkOP(sender)) return true;
        if (args.length != 1) return true;
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) return true;
        if (PlayerUtils.getInvBackup(target) != null) target.getInventory().setContents(PlayerUtils.getInvBackup(target));
        return false;
    }
}
