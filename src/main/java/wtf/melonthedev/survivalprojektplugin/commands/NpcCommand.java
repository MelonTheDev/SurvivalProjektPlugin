package wtf.melonthedev.survivalprojektplugin.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import wtf.melonthedev.survivalprojektplugin.utils.CommandUtils;
import wtf.melonthedev.survivalprojektplugin.others.NPC;
import wtf.melonthedev.survivalprojektplugin.utils.PlayerUtils;

import java.util.ArrayList;
import java.util.List;

import static wtf.melonthedev.survivalprojektplugin.Main.colorinfo;
import static wtf.melonthedev.survivalprojektplugin.Main.serverprefix;

public class NpcCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (CommandUtils.isDisabled(sender, "npc")) return true;
        if (!PlayerUtils.isPlayer(sender)) return true;
        Player p = (Player) sender;
        if (!PlayerUtils.checkOP(p)) return true;
        if (args.length == 1) {
            NPC.createNpc(p, args[0], args[0]);
            p.sendMessage(colorinfo + serverprefix + "Du hast einen NPC mit dem Namen " + args[0] + " erstellt.");
            return true;
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("remove")) {
                NPC.deleteNPC(args[1]);
                p.sendMessage(colorinfo + serverprefix + "Du hast alle NPCs mit dem Namen " + args[1] + " entfernt.");
                return true;
            } else if (args[0].equalsIgnoreCase("set")) {
                NPC.teleportNPC(args[1], p);
                return true;
            }
            NPC.createNpc(p, args[0], args[1]);
            return true;
        } else if (args.length == 3)  {
            if (args[0].equalsIgnoreCase("move")) {
                Player target = Bukkit.getPlayer(args[2]);
                if (target == null) return true;
                NPC.moveNpc(args[1], target.getLocation());
                return true;
            }
        }
        PlayerUtils.sendSyntaxError(p, "/npc <Skin: player/set/remove> <Name: string>");
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> tab = new ArrayList<>();
        if (args.length == 1) {
            tab.add("remove");
            tab.add("set");
            tab.add("move");
        }
        for (Player p : Bukkit.getOnlinePlayers()) {
            tab.add(p.getName());
        }
        return tab;
    }
}
