package wtf.melonthedev.survivalprojektplugin.listeners.playerlisteners;

import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import wtf.melonthedev.survivalprojektplugin.*;
import wtf.melonthedev.survivalprojektplugin.commands.PetCommand;
import wtf.melonthedev.survivalprojektplugin.utils.PetUtils;

import java.util.Collections;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import static wtf.melonthedev.survivalprojektplugin.Main.*;

public class PlayerDeathListener implements Listener {
    Player p;
    FileConfiguration config = Main.getPlugin().getConfig();
    public String[] customDeathMessages = new String[] {
            "wurde ins Krankenhaus eingeliefert", "die Beerdigung ist morgen",
            "wurde als Hoglin wiederbelebt", "muss in die Psychatrie",
            "ist von uns gegangen", "trägt emotionale Schäden davon", "hat jetzt ein Problem",
            "muss das nächste mal mehr Kompetenz zeigen", "sein Kurzzeitgedächtnis hat darunter gelitten",
            "ist dadurch sogar schlechter als stebadon", "glaubt nun an den Villianischen Gott", "das nur, weil er sein Totem vergessen hat"
    };

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        p = e.getEntity().getPlayer();
        //DEATH MESSAGE
        int randromInt =  ThreadLocalRandom.current().nextInt(0, customDeathMessages.length);
        e.setDeathMessage(colorinfo + serverprefix + e.getDeathMessage() + " und " + customDeathMessages[randromInt] + ".");
        if (p.getLocation().getBlock().getType() == Material.STONECUTTER) {
            e.setDeathMessage(colorinfo + serverprefix + p.getName() + " was zerschnitten by stonecutter und " + customDeathMessages[randromInt] + ".");
        }


        //DEATH LOCATION
        Location dloc = p.getLocation();
        config.set(p.getName() + ".deathloc.x", dloc.getBlockX());
        config.set(p.getName() + ".deathloc.y", dloc.getBlockY());
        config.set(p.getName() + ".deathloc.z", dloc.getBlockZ());
        config.set(p.getName() + ".deathloc.w", Objects.requireNonNull(dloc.getWorld()).getName());
        Main.getPlugin().saveConfig();

        if (p.getKiller() != null) {
            if (p.getKiller().getName().equalsIgnoreCase(p.getName())) {
                Bukkit.broadcastMessage(colorinfo + serverprefix + "Der Spieler " + p.getName() + " hat sich aufgrund seiner Dummheit selber getötet.");
                return;
            }

            //DROP HEAD
            ItemStack item = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta skull = (SkullMeta) item.getItemMeta();
            assert skull != null;
            skull.setOwningPlayer(p);
            skull.setDisplayName(ChatColor.AQUA + p.getName() + "'s toter Kopf.");
            skull.setLore(Collections.singletonList(ChatColor.DARK_RED + "Bah der klebt."));
            item.setItemMeta(skull);
            World w = p.getWorld();
            w.dropItem(p.getLocation(), item);

            ItemStack arrow = new ItemStack(Material.ARROW, 1);
            ItemMeta meta = arrow.getItemMeta();
            assert meta != null;
            meta.setDisplayName(colorinfo + "TEMP ARROW");
            arrow.setItemMeta(meta);
            e.getEntity().getInventory().removeItem(arrow);


            //VANISH
            if (config.getBoolean(p.getKiller().getName() + ".vanish.enabled")) {
                e.setDeathMessage(colorinfo + serverprefix + "Der Spieler " + p.getName() + " hat sich selber getötet.");
                return;
            }


            //KILLBAN
            Player target = p.getKiller();
            if (config.getBoolean(p.getName() + ".kitpvp.inPvp")) return;
            if (config.contains(p.getName() + ".killban.on")) {
                if (!config.getBoolean(p.getName() + ".killban.on")) {
                    return;
                }
            }
            Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> target.kickPlayer(colorerror + "Man darf nicht töten!!!"), 21L);
            Date date = new Date();
            int minutes;
            int time;
            if (config.contains(p.getName() + ".killban.banntime")) {
                time = config.getInt(p.getName() + ".killban.banntime");
                minutes = date.getMinutes() + config.getInt(p.getName() + ".killban.banntime");
            } else {
                time = 5;
                minutes = date.getMinutes() + 5;
            }
            date.setMinutes(minutes);
            Bukkit.getBanList(BanList.Type.NAME).addBan(target.getName(), colorerror + "Du wurdest wegen des Tötens für " + time + " Minuten gekickt." + colorinfo, date, "Survivalprojekt");
        }
    }



    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        p = e.getPlayer();
        //PET
        ConfigurationSection petSection = Main.getPlugin().getConfig().getConfigurationSection(p.getName() + ".pet");
        if (petSection == null) return;
        if (!petSection.getBoolean("isSpawned")) return;
        Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> PetUtils.teleportPet(p), 21);
    }
}
