package wtf.melonthedev.survivalprojektplugin.commands;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static wtf.melonthedev.survivalprojektplugin.Main.*;

public class VoteKickCommand implements CommandExecutor, TabCompleter {

    FileConfiguration config = Main.getPlugin().getConfig();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (CommandUtils.isDisabled(sender, "votekick")) return true;
        if (!PlayerUtils.isPlayer(sender)) return true;
        Player p = (Player) sender;
        if (args.length == 0) {
            p.sendMessage(colorerror + serverprefix + "Syntaxerror: /votekick <Player: string/Yes/No>");
            return true;
        }
        p.sendMessage(colorerror + serverprefix + "Tut mir leid, dieser Command ist noch in der Beta und funktioniert nicht korrekt. Bitte warte bis das gefixt ist.");
        return true;
        /*
        if (config.get("voting.target") != null) {
            if (p.getName() == config.get("voting.target")) {
                p.sendMessage(colorerror + serverprefix + "Du kannst bei der Abstimmung nicht mitmachen, weil du der Angeklagte bist.");
                return true;
            }
        }

        for (Player target : Bukkit.getOnlinePlayers()) {
            if (!target.getName().equalsIgnoreCase(args[0])) {
                continue;
            }
            if (config.get("voting.oncooldown") != null){
                if (config.getBoolean("voting.oncooldown"))
                    p.sendMessage(colorerror + serverprefix + "Votekick hat einen Cooldown.");
                return true;
            }
            StringBuilder reason = new StringBuilder();
            for (String arg : args) {
                reason.append(arg).append(" ");
            }
            config.set("voting.reason", String.valueOf(reason));
            if(target.getName().equals(p.getName())) {
                p.sendTitle(colorerror + "WARUM?", "hahahahahhaha", 1, 30, 1);
                Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> p.kickPlayer(colorerror + "WARUM?!"), 40L);
                Bukkit.broadcastMessage(colorinfo + serverprefix + "Der Spieler " + p.getName() + " hat sich aufgrund seiner Dummheit selber gekickt.");
                resetVoteKick();
                return true;
            }
            if (config.contains("voting")) {
                p.sendMessage(colorerror + serverprefix + "Es läuft gerade eine Abstimmung.");
                return true;
            }
            config.set("voting.target", target.getName());
            config.set("voting.admin", p.getName());
            config.set("voting.yes", 0);
            config.set("voting.no", 0);

            if (Bukkit.getOnlinePlayers().size() <= 3) {
                config.set("voting.online", Bukkit.getOnlinePlayers().size() -1);
            } else if (Bukkit.getOnlinePlayers().size() == 4) {
                config.set("voting.online", Bukkit.getOnlinePlayers().size() -1);
            } else if (Bukkit.getOnlinePlayers().size() >= 5) {
                config.set("voting.online", Bukkit.getOnlinePlayers().size() -2);
            } else if (Bukkit.getOnlinePlayers().size() >= 10) {
                config.set("voting.online", Bukkit.getOnlinePlayers().size() -6);
            } else if (Bukkit.getOnlinePlayers().size() == 2) {
                p.sendMessage(colorerror + serverprefix + "Du kannst keinen Votekick zu 2. machen.");
                return true;
            }
            Main.getPlugin().saveConfig();
            Bukkit.broadcastMessage(colorinfo + serverprefix + "Der Spieler " + p.getName() + " möchte den Spieler " + target.getName() + " für 5 Minuten kicken.");
            if (config.getString("voting.reason") != null)
                Bukkit.broadcastMessage(colorinfo + serverprefix + "Reason: " + config.getString("voting.reason"));
            Bukkit.broadcastMessage(colorsecondinfo + serverprefix + "Du kannst mit '/votekick yes bzw. no' abstimmen");
            Bukkit.broadcastMessage(colorsecondinfo + serverprefix + "Es müssen mindestens 60% der online Spieler abgestimmt haben, um den Kick auszuführen.");
            Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> {
                if (config.get("voting") != null) {
                    Bukkit.broadcastMessage(colorinfo + serverprefix + "Die Abstimmung läuft schon 2 Minuten und wird abgebrochen.");
                    resetVoteKick();
                }
            }, 2400L);
            vote("yes", p);
            return true;
        }
        if (args[0].equalsIgnoreCase("yes")) {
            vote("yes", p);
            return true;
        } else if (args[0].equalsIgnoreCase("no")) {
            vote("no", p);
            return true;
        } else if (args[0].equalsIgnoreCase("cancel")) {
            if (config.get("voting") == null) {
                p.sendMessage(colorerror + serverprefix + "Es läuft derzeit keine Abstimmung! Erstelle eine mit /votekick <Name>");
                return true;
            }
            if ((config.getString("voting.admin")).equals(p.getName()) || p.isOp()) {
                p.sendMessage(colorinfo + serverprefix + "Du hast die Abstimmung abgebrochen.");
                resetVoteKick();
            } else {
                p.sendMessage(colorerror + serverprefix + "Nur der Spieler, der den Vote gestartet hat, kann ihn beenden.");
                return true;
            }
            return true;
        } else if (args[0].equalsIgnoreCase("resetcooldown")) {
            if (!p.isOp()) return true;
            p.sendMessage(colorinfo + serverprefix + "Du hast den Cooldown zurückgesetzt.");
            config.set("voting.oncooldown", null);
            return true;
        } else {
            p.sendMessage(colorerror + serverprefix + "Syntaxerror: /votekick <Player: online/yes/no>");
        }
        return false;*/
    }

    public void vote(String vote, Player p) {
        if (config.contains("voting.voted." + p.getName())) {
            if (config.getBoolean("voting.voted." + p.getName())) {
                p.sendMessage(colorerror + serverprefix + "Du hast schon abgestimmt.");
                return;
            }
        }
        if (!(config.contains("voting"))) {
            p.sendMessage(colorerror + serverprefix + "Es läuft derzeit keine Abstimmung! Erstelle eine mit /votekick <Name>");
            return;
        }
        if (config.get("voting." + vote) == null) {
            config.set("voting." + vote, 0);
        }
        config.set("voting." + vote, config.getInt("voting." + vote) + 1);
        config.set("voting.votes", config.getInt("voting.yes") + config.getInt("voting.no"));
        config.set("voting.voted." + p.getName(), true);
        Main.getPlugin().saveConfig();
        p.sendMessage(colorsecondinfo + serverprefix + "Du hast für '" + vote + "' gestimmt.");
        Bukkit.broadcastMessage(colorinfo + serverprefix + "Es haben schon " + colorerror + config.getInt("voting.votes") + "/" + config.getInt("voting.online") + colorinfo + " Spielern abgestimmt.");
        if (config.getInt("voting.votes") >= config.getInt("voting.online")) {
            if (config.getInt("voting.no") >= config.getInt("voting.yes")) {
                Bukkit.broadcastMessage(colorinfo + serverprefix + "Es sind mehr oder gleich viele Spieler gegen den Kick.");
                resetVoteKick();
                return;
            }
            Player target = Bukkit.getPlayer((String) Objects.requireNonNull(config.get("voting.target")));
            Date date = new Date();
            date.setMinutes(date.getMinutes() + 5);
            assert target != null;
            Bukkit.broadcastMessage(colorinfo + serverprefix + "Der Spieler " + target.getName() + " wurde für 5 Minuten gekickt!");
            if (target.isOnline()) {
                target.kickPlayer(colorerror + "Du wurdest wegen dem Votekick für 5 Minuten gekickt. Re-joine für weitere Infos!");
            }
            if (config.contains("voting.reason"))
                Bukkit.getBanList(BanList.Type.NAME).addBan(target.getName(), colorerror + "Du wurdest wegen dem Votekick für 5 Minuten gekickt." + colorsecondinfo + "Reason: " + config.get("voting.reason") + colorinfo, date, "Survivalprojekt");
            Bukkit.getBanList(BanList.Type.NAME).addBan(target.getName(), colorerror + "Du wurdest wegen dem Votekick für 5 Minuten gekickt." + colorinfo, date, "Survivalprojekt");
            resetVoteKick();
        }
    }

    public void resetVoteKick() {
        config.set("voting", null);
        config.set("voting.oncooldown", true);
        Main.getPlugin().saveConfig();
        Bukkit.broadcastMessage(colorinfo + serverprefix + "Die Abstimmung wurde beendet.");
        Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> {
            Bukkit.broadcastMessage(colorinfo + serverprefix + "Es kann wieder ein Votekick gestartet werden.");
            config.set("voting.oncooldown", null);
        }, 2400L);
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> tab = new ArrayList<>();
        tab.add("yes");
        tab.add("no");
        tab.add("cancel");
        Bukkit.getOnlinePlayers().forEach(p -> tab.add(p.getName()));
        return tab;
    }
}
