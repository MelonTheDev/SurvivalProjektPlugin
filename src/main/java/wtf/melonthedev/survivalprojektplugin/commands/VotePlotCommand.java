package wtf.melonthedev.survivalprojektplugin.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import wtf.melonthedev.survivalprojektplugin.Main;
import wtf.melonthedev.survivalprojektplugin.utils.CommandUtils;
import wtf.melonthedev.survivalprojektplugin.gui.ItemStacks;
import wtf.melonthedev.survivalprojektplugin.utils.PlayerUtils;

import java.util.*;

import static wtf.melonthedev.survivalprojektplugin.Main.colorinfo;
import static wtf.melonthedev.survivalprojektplugin.Main.serverprefix;

public class VotePlotCommand implements CommandExecutor, Listener, TabCompleter {
    FileConfiguration config = Main.getPlugin().getConfig();


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (CommandUtils.isDisabled(sender, "voteplot")) return true;
        if (!PlayerUtils.isPlayer(sender)) return true;
        Player p = (Player) sender;

        //The /voteplot submit and setPlot Functions
        if (args.length != 0) {
            if (args.length == 1 && args[0].equalsIgnoreCase("submit")) {
                if (!PlayerUtils.checkOP(p)) return true;
                if (!config.contains("plotvote")) { PlayerUtils.sendCustomError(p, "Es gibt derzeit keine Abstimmung."); return true; }
                getResult();
            } else if (args.length == 2 && args[0].equalsIgnoreCase("setPlot")) {
                if (!PlayerUtils.checkOP(p)) return true;
                Player plotOwner = Bukkit.getPlayer(args[1]);
                if (plotOwner == null) { PlayerUtils.sendCustomError(p, "Dieser Spieler ist nicht Online."); return true; }
                config.set("plotvote.plotowner", plotOwner.getName());
                Main.getPlugin().saveConfig();
                p.sendMessage(colorinfo + serverprefix + "Du hast den Plotowner auf '" + plotOwner.getName() + "' gesetzt.");
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getOpenInventory().getTitle().startsWith(colorinfo + "Vote the Plot:")) player.closeInventory();
                }
            } else {
                PlayerUtils.sendSyntaxError(p, "/voteplot");
            }
            return true;
        }

        //Checks if there is an Voting on going
        if (!config.contains("plotvote")) {
            PlayerUtils.sendCustomError(p, "Es gibt derzeit keine Abstimmung.");
            p.closeInventory();
            return true;
        }

        //Checks if player is the plotowner
        Player plotOwner = getPlotOwner();
        if (p == plotOwner) {
            PlayerUtils.sendCustomError(p, "Du kannst nicht bei deinem eigenen Plot abstimmen.");
            p.closeInventory();
            return true;
        }

        //Open VoteInv
        p.openInventory(createVoteInv());
        PlayerUtils.pinInventoryContents(p);
        return false;
    }


    public Inventory createVoteInv() {
        Inventory voteInv = Bukkit.createInventory(null, InventoryType.HOPPER, colorinfo + "Vote the Plot:");
        voteInv.setItem(0, ItemStacks.poop);
        voteInv.setItem(1, ItemStacks.bad);
        voteInv.setItem(2, ItemStacks.ok);
        voteInv.setItem(3, ItemStacks.good);
        voteInv.setItem(4, ItemStacks.perfect);
        return voteInv;
    }

    public Player getPlotOwner() {
        String plotOwnerName = config.getString("plotvote.plotowner");
        if (plotOwnerName == null) return null;
        return Bukkit.getPlayer(plotOwnerName);
    }

    public void setVote(Player p, Player plotOwner, String vote) {
        config.set(plotOwner.getName() + ".plotvote." + p.getName(), vote);
        Main.getPlugin().saveConfig();
        if (vote.equalsIgnoreCase("poop")) vote = "Du hättest auch gleich einen Kackhaufen bauen können";
        p.sendMessage(colorinfo + serverprefix + "Du hast für '" + vote.toUpperCase() + "' gevotet.");
        p.closeInventory();
    }

    public void removeVote(Player p, Player plotOwner) {
        config.set(plotOwner.getName() + ".plotvote." + p.getName(), null);
        Main.getPlugin().saveConfig();
        if (p.getOpenInventory().getTitle().startsWith(colorinfo + "Vote the Plot:"))
            p.closeInventory();
    }

    public void showResult(Player winner, Player secondWinner) {

    }


    @EventHandler
    public void onInvClick(InventoryClickEvent e) {
        //If-Statements to get the correct INV
        if (e.getClickedInventory() == null) return;
        if (e.getClickedInventory().getType() == InventoryType.PLAYER) return;
        if (e.getCurrentItem() == null) return;
        if (!e.getView().getTitle().startsWith(colorinfo + "Vote the Plot:")) return;
        Player p = (Player) e.getWhoClicked();

        //Checks if there is an Voting on going
        if (!config.contains("plotvote.plotowner")) {
            PlayerUtils.sendCustomError(p, "Es gibt derzeit keine Abstimmung.");
            p.closeInventory();
            return;
        }

        Player plotOwner = getPlotOwner();
        int slot = e.getSlot();

        if (slot == 0) {
            setVote(p, plotOwner, "poop");
        } else if (slot == 1) {
            setVote(p, plotOwner,"bad");
        } else if (slot == 2) {
            setVote(p, plotOwner,"ok");
        } else if (slot == 3) {
            setVote(p, plotOwner,"good");
        } else if (slot == 4) {
            setVote(p, plotOwner,"perfect");
        }
    }

    public void getResult() {
        /////////////////////////
        Bukkit.broadcastMessage(ChatColor.GOLD.toString() + ChatColor.BOLD + serverprefix + "Das Plotvoting ist vorbei.");
        Bukkit.broadcastMessage(ChatColor.GOLD  + "Die Ergebnisse werden jetzt bekannt gegeben:");
        Bukkit.broadcastMessage("");
        /////////////////////////

        //HashMap with the scores of each player
        HashMap<Player, Integer> scores = new HashMap<>();

        for (Player player : Bukkit.getOnlinePlayers()) {
            final int[] score = new int[] {0};
            ConfigurationSection section = config.getConfigurationSection(player.getName() + ".plotvote");
            //If nobody voted:
            if (section == null)
                continue;
            /////////////////////////
            //Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> {
                //Send PlotOwnerName
                Bukkit.broadcastMessage(ChatColor.GOLD + player.getName() + ":");
            //}, 42);
            /////////////////////////
            section.getValues(false).forEach((votedPlayer, playerVote) -> {
                Player p = Bukkit.getPlayer(votedPlayer);
                if (p != null)
                    removeVote(p, player);
                /////////////////////////
                //Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> {
                    Bukkit.broadcastMessage(ChatColor.GOLD + "      " + votedPlayer + ": " + playerVote.toString().toUpperCase());
                //}, 52);
                /////////////////////////
                //Sets the Score
                switch (playerVote.toString()) {
                    case "poop":
                        score[0] = score[0] - 2;
                        break;
                    case "bad":
                        score[0] = score[0] - 1;
                        break;
                    case "good":
                        score[0] = score[0] + 1;
                        break;
                    case "perfect":
                        score[0] = score[0] + 2;
                        break;
                }
            });
            scores.put(player, score[0]);
            config.set(player.getName() + ".plotvote", null);
        }

        config.set("plotvote", null);
        Main.getPlugin().saveConfig();


        //Winnweselection
        String winner = "Niemand";
        String secondWinner = null;
        int highestScore = -10;

        for (Map.Entry<Player, Integer> entry : scores.entrySet()) {
            //If gleichstand, define secondWinner
            if (entry.getValue() == highestScore) {
                if (secondWinner != null) {
                    Bukkit.broadcastMessage(ChatColor.RED + "Es gibt mehr als 2 Gewinner mit dem gleichen Punktestand! Diese sind: " + winner + ", " + secondWinner + ", " + entry.getKey().getName() + "! Das Voting ist hiermit zu ende.");
                    scores.clear();
                    return;
                }
                secondWinner = entry.getKey().getName();
            }
            if (entry.getValue() > highestScore) {
                highestScore = entry.getValue();
                winner = entry.getKey().getName();
                if (secondWinner != null) secondWinner = null;
            }
        }
        if (secondWinner != null) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendTitle(ChatColor.GREEN + "UNENTSCHIEDEN zwischen", ChatColor.GOLD + winner + " und " + secondWinner, 10, 300, 10);
            }
        } else {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendTitle(ChatColor.GREEN + winner, ChatColor.GOLD + "hat GEWONNEN!!!", 10, 300, 10);
            }
        }
        scores.clear();
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> tab = new ArrayList<>();
        if (!sender.isOp()) return null;
        if (args.length == 2 && args[0].equalsIgnoreCase("setPlot")) {
            Bukkit.getOnlinePlayers().forEach(p -> tab.add(p.getName()));
            return tab;
        }
        tab.add("setPlot");
        tab.add("submit");
        return tab;
    }
}
