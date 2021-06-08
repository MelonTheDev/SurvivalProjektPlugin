package wtf.melonthedev.survivalprojektplugin.listeners.playerlisteners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import wtf.melonthedev.survivalprojektplugin.Main;
import wtf.melonthedev.survivalprojektplugin.commands.PetCommand;
import wtf.melonthedev.survivalprojektplugin.utils.PetUtils;
import wtf.melonthedev.survivalprojektplugin.utils.PlayerUtils;

import java.util.Objects;
import java.util.UUID;

public class PlayerMoveListener implements Listener {

    boolean isDamaging;

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        FileConfiguration config = Main.getPlugin().getConfig();
        Player p = e.getPlayer();
        //AFK
        if (config.getBoolean(e.getPlayer().getName() + ".isAfk")) {
            if (e.getFrom().getBlockY() != Objects.requireNonNull(e.getTo()).getBlockY() && e.getFrom().getBlockY() < e.getTo().getBlockY())
                PlayerUtils.setAfk(p, false);
            else if (e.getFrom().getBlockX() != e.getTo().getBlockX() || e.getFrom().getBlockY() != e.getTo().getBlockY()) {
                e.setCancelled(true);
            }
        }

        //PETS
        ConfigurationSection petSection = config.getConfigurationSection(p.getName() + ".pet");
        if (petSection == null) return;
        if (petSection.getBoolean("isSpawned")) {
            try {
                Location petLoc = Objects.requireNonNull(Bukkit.getEntity(UUID.fromString(Objects.requireNonNull(petSection.getString("uuid"))))).getLocation();
                if (p.getLocation().distance(petLoc) > 30) {
                    if (p.getLocation().add(0, -2, 0).getBlock().getType() == Material.AIR) return;
                    PetUtils.teleportPet(p);
                    System.out.println("[DEBUG] Teleportet Pet from " + p.getName());
                }
            } catch (IllegalArgumentException | NullPointerException ignored) {
            }
        }

        //MARRY TRAIL
        if (config.getBoolean(p.getName() + ".marry.marrytrail.enabled")) {
            int time = config.getInt(p.getName() + ".marry.marrytrail.time");
            if (time == 0) {
                config.set(p.getName() + ".marry.marrytrail", null);
                Main.getPlugin().saveConfig();
                return;
            }
            time--;
            p.spawnParticle(Particle.HEART, p.getLocation(), 20, 0.5, 0, 0.5);
            config.set(p.getName() + ".marry.marrytrail.time", time);
            Main.getPlugin().saveConfig();
        }

        //DAMAGE
        if (e.getPlayer().getName().equals("Jonbadon")) {
            isDamaging = false;
            if (Objects.requireNonNull(e.getTo()).getBlock().getType() == Material.STONECUTTER) {
                isDamaging = true;
                damage(p);
            }
        }
    }

    public void damage(Player p) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!isDamaging) cancel();
                if (p.isDead()) cancel();
                p.damage(0.5);
                isDamaging = true;
            }
        }.runTaskTimer(Main.getPlugin(), 0, 11);
    }

}
