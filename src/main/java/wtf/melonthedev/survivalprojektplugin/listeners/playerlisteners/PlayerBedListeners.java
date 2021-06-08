package wtf.melonthedev.survivalprojektplugin.listeners.playerlisteners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import wtf.melonthedev.survivalprojektplugin.Main;
import wtf.melonthedev.survivalprojektplugin.commands.PetCommand;
import wtf.melonthedev.survivalprojektplugin.utils.PetUtils;
import wtf.melonthedev.survivalprojektplugin.utils.WorldUtils;

public class PlayerBedListeners implements Listener {

    public int playersSleep;

    @EventHandler
    public void onBedEnter(PlayerBedEnterEvent e) {
        Player p = e.getPlayer();
        FileConfiguration config = Main.getPlugin().getConfig();
        if (config.getBoolean(p.getName() + ".pet.isSpawned")) {
            PetUtils.despawnPet(p);
        }

        Bukkit.getScheduler().runTask(Main.getPlugin(), () -> {
            if (!p.isSleeping()) return;
            //IF PLAYER IS ALONE
            if (Bukkit.getOnlinePlayers().size() <= 1) {
                Bukkit.getServer().broadcastMessage(ChatColor.DARK_AQUA + p.getName() + " liegt im Bett. zzZzZZ.");
                return;
            }

            //IF PLAYER IS IN ANOTHER WORLD IGNORE, ELSE ADD A PLAYER TO mustSleep
            int playersMustSleep = 0;
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getWorld().getName().equalsIgnoreCase("world_nether") || player.getWorld().getName().equalsIgnoreCase("world_the_end")) {
                    player.setSleepingIgnored(true);
                    continue;
                }
                if (config.getBoolean(player.getName() + ".isAfk")) continue;
                playersMustSleep++;
            }
            playersSleep++;
            if (Bukkit.getOnlinePlayers().size() >= 5) {
                Bukkit.getServer().broadcastMessage(ChatColor.DARK_AQUA + p.getName() + " liegt im Bett. zzZzZZ. (1/1)");
                Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> {
                    WorldUtils.beginNewDay();
                    resetSleepCount();
                }, 100);
                return;
            }
            Bukkit.getServer().broadcastMessage(ChatColor.DARK_AQUA + p.getName() + " liegt im Bett. zzZzZZ. (" + playersSleep + "/" + playersMustSleep + ")");
            int finalPlayersMustSleep = playersMustSleep;
            Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> {
                if (playersSleep < finalPlayersMustSleep) {
                    return;
                }
                WorldUtils.beginNewDay();
                resetSleepCount();
            }, 100);
        });
    }

    @EventHandler
    public void onBedLeave(PlayerBedLeaveEvent e) {
        if (playersSleep <= 0) return;
        playersSleep = playersSleep - 1;
    }

    public void resetSleepCount() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.setSleepingIgnored(false);
        }
        playersSleep = 0;
    }

}
