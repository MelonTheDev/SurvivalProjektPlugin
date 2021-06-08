package wtf.melonthedev.survivalprojektplugin.commands;

import org.bukkit.*;
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

public class HomeCommand implements CommandExecutor, TabCompleter {

    FileConfiguration config = Main.getPlugin().getConfig();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (CommandUtils.isDisabled(sender, "home")) return true;
        if (!PlayerUtils.isPlayer(sender)) return true;
        Player p = (Player) sender;

        if (args.length == 1) {
            //SET OR TELEPORT TO OTHER HOME
            if (args[0].equalsIgnoreCase("set")) {
                setHome(p);
            } else {
                OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
                if (!config.contains(target.getName() + ".home")) {
                    PlayerUtils.sendCustomError(p, "Der Spieler " + target.getName() + " hat kein Home.");
                    return true;
                }
                if (config.getBoolean(p.getName() + ".marry.isMarried") && Objects.requireNonNull(config.getString(p.getName() + ".marry.partner")).equalsIgnoreCase(target.getName())) {
                    teleport(p, target);
                    return true;
                }
                if (!PlayerUtils.checkOP(p)) return true;
                teleport(p, target);
            }
            return true;
        }else if (args.length == 0) {
            //TELEPORT TO HOME
            if (!config.contains(p.getName() + ".home")) {
                PlayerUtils.sendCustomError(p, "Du hast kein Home. Definiere es mit '/home set <Home: String>'");
                return true;
            }
            teleport(p, p);
            return true;
        }
        PlayerUtils.sendSyntaxError(p, "/home <set> or <>");
        return false;
    }

    public void teleport(Player p, OfflinePlayer target) {
        //VARIABLEN
        String name = target.getName();
        FileConfiguration config = Main.getPlugin().getConfig();

        World world = Bukkit.getWorld(Objects.requireNonNull(config.getString(name + ".home.w")));
        double x = config.getDouble(name + ".home.x");
        double y = config.getDouble(name + ".home.y");
        double z = config.getDouble(name + ".home.z");
        float yaw = (float) config.getDouble(name + ".home.yaw");
        float pitch = (float) config.getDouble(name + ".home.pitch");
        Location loc = new Location(world, x, y, z, yaw, pitch);

        //TELEPORT
        p.sendMessage(colorinfo + serverprefix + "Du wirst in ca. 3 Sekunden zu deinem Home teleportiert...");
        Objects.requireNonNull(loc.getWorld()).loadChunk(loc.getWorld().getChunkAt(loc));
        Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> {
            if (p.isDead()) {
                PlayerUtils.sendCustomError(p, "Du bist tot. Du kannst nicht mehr teleportiert werden :^(");
                return;
            }
            p.teleport(loc);
            p.sendMessage(colorinfo + serverprefix + "Du wurdest erfolgreich teleportiert");
        }, 63); //DELAY 3 SEC
    }

    public void setHome(Player player) {
        String name = player.getName();
        config.set(name + ".home.x", player.getLocation().getX());
        config.set(name + ".home.y", player.getLocation().getY());
        config.set(name + ".home.z", player.getLocation().getZ());
        config.set(name + ".home.w", player.getWorld().getName());
        config.set(name + ".home.yaw", player.getLocation().getYaw());
        config.set(name + ".home.pitch", player.getLocation().getPitch());
        Main.getPlugin().saveConfig();
        player.sendMessage(colorinfo + serverprefix + "Dein Home wurde erfolgreich erstellt! Du kannst dich mit '/home' dort hin teleportieren.");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> tab = new ArrayList<>();
        tab.add("set");
        if (config.getBoolean(sender.getName() + ".marry.isMarried")) {
            String partner = config.getString(sender.getName() + ".marry.partner");
            if (partner != null) tab.add(partner);
        }
        if (sender.isOp()) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getName().equals(sender.getName())) continue;
                tab.add(p.getName());
            }
        }
        return tab;
    }
}
