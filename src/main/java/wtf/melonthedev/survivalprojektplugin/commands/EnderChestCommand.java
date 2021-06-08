package wtf.melonthedev.survivalprojektplugin.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import wtf.melonthedev.survivalprojektplugin.utils.CommandUtils;
import wtf.melonthedev.survivalprojektplugin.utils.PlayerUtils;

import static wtf.melonthedev.survivalprojektplugin.Main.*;

public class EnderChestCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (CommandUtils.isDisabled(sender, "enderchest")) return true;
        if (!PlayerUtils.isPlayer(sender)) return true;
        Player p = (Player) sender;

        if (args.length == 0) {
            p.openInventory(p.getEnderChest());
            p.playSound(p.getLocation(), Sound.BLOCK_ENDER_CHEST_OPEN, 1, 1);
        }else if (args.length == 1) {
            if (!PlayerUtils.checkOP(p)) return true;
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                p.sendMessage(colorerror + serverprefix + "Dieser Spieler ist nicht Online.");
                return true;
            }
            p.openInventory(target.getEnderChest());
            p.playSound(p.getLocation(), Sound.BLOCK_ENDER_CHEST_OPEN, SoundCategory.BLOCKS, 1, 1);
            p.sendMessage(colorinfo + serverprefix + "Du schaust in die Enderchest von " + colorsecondinfo + target.getName() + colorinfo + ".");
        } else PlayerUtils.sendSyntaxError(p, "/ec or /enderchest");
        return false;
    }
}
