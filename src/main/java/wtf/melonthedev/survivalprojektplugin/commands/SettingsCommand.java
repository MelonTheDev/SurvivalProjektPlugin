package wtf.melonthedev.survivalprojektplugin.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import wtf.melonthedev.survivalprojektplugin.Main;
import wtf.melonthedev.survivalprojektplugin.utils.CommandUtils;
import wtf.melonthedev.survivalprojektplugin.others.Config;
import wtf.melonthedev.survivalprojektplugin.utils.PlayerUtils;

import static wtf.melonthedev.survivalprojektplugin.Main.*;

public class SettingsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (CommandUtils.isDisabled(sender, "settings")) return true;
        if (!PlayerUtils.isPlayer(sender)) return true;
        Player p = (Player) sender;
        FileConfiguration config = Main.getPlugin().getConfig();
        FileConfiguration settings = Config.getCustomConfig("settings.yml");
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("dropmobheads")) {
                if (args[1].equalsIgnoreCase("false")) {
                    config.set(p.getName() + ".dropMobHeads", false);
                    Main.getPlugin().saveConfig();
                    p.sendMessage(colorinfo + serverprefix + "Du hast das droppen aller Mobköpfe abgeschaltet.");
                    return true;
                } else if (args[1].equalsIgnoreCase("true")) {
                    config.set(p.getName() + ".dropMobHeads", true);
                    Main.getPlugin().saveConfig();
                    p.sendMessage(colorinfo + serverprefix + "Du hast das droppen von Mobköpfe eingeschaltet.");
                    return true;
                }
            }
            p.sendMessage(colorerror + serverprefix + "Syntaxerror: /settings");
            return true;
        }
        if (!p.isOp()) {
            p.sendMessage(colorerror + serverprefix + "Du hast dazu keine Berechtigung. Frage einen Admin.");
            return true;
        }
        Inventory inv = Bukkit.createInventory(null, 27, colorinfo + "Plugin Settings");
        p.openInventory(inv);
        config.set(p.getName() + ".Inv.isopen", true);
        Main.getPlugin().saveConfig();
        return false;
    }
}
