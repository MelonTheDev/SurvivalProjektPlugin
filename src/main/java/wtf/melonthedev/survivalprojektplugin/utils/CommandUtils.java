package wtf.melonthedev.survivalprojektplugin.utils;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import wtf.melonthedev.survivalprojektplugin.others.Config;

import static wtf.melonthedev.survivalprojektplugin.Main.colorerror;
import static wtf.melonthedev.survivalprojektplugin.Main.serverprefix;

public class CommandUtils {

    static FileConfiguration settings = Config.getCustomConfig("settings.yml");

    public static boolean isDisabled(CommandSender sender, String command) {
        if (settings.getBoolean("settings.commands." + command + ".enabled")) {
            return false;
        }
        sender.sendMessage(colorerror + serverprefix + "Dieser Command wurde für diesen Server deaktiviert. Wenn du denkst, das ist ein Fehler, kontaktiere bitte unser Team.");
        return true;
    }
}
