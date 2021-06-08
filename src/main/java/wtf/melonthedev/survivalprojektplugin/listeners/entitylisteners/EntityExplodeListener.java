package wtf.melonthedev.survivalprojektplugin.listeners.entitylisteners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import wtf.melonthedev.survivalprojektplugin.others.Config;

public class EntityExplodeListener implements Listener {

    FileConfiguration settings = Config.getCustomConfig("settings.yml");

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent e) {
        if (settings.getBoolean("gameplay.disableTnt")) {
            if (e.getEntity().getType() == EntityType.PRIMED_TNT) {
                e.setCancelled(true);
            }
        }

        ConfigurationSection spawns = settings.getConfigurationSection("settings.generic.spawn");
        if (spawns == null)
            return;

        spawns.getKeys(false).forEach(world -> {
            int spawnX = settings.getInt("settings.generic.spawn." + world + ".x");
            int spawnY = settings.getInt("settings.generic.spawn. " + world + ".y");
            int spawnZ = settings.getInt("settings.generic.spawn." + world + ".z");
            String spawnW = settings.getString("settings.generic.spawn." + world + ".w");
            boolean explodeOnSpawn = settings.getBoolean("settings.spawning.creeper.explodeonspawn");
            if (spawnW == null) {
                settings.set("settings.generic.spawn." + world + ".w", "world");
                Config.saveCustomConfig(settings);
                spawnW = settings.getString("settings.generic.spawn." + world + ".w");
            }
            assert spawnW != null;
            Location spawn = new Location(Bukkit.getWorld(spawnW), spawnX, spawnY, spawnZ);
            if (e.getEntity().getType() != EntityType.CREEPER) return;
            if (!explodeOnSpawn) {
                if (e.getEntity().getLocation().distance(spawn) > 80) {
                    System.out.println("[DEBUG] Creeper exploded at " + e.getEntity().getLocation().getX() + " " + e.getEntity().getLocation().getY() + " " + e.getEntity().getLocation().getZ());
                    return;
                }
                e.blockList().clear();
            }
            if (!settings.getBoolean("settings.spawning.creeper.explode")) {
                if (e.getEntity().getType() == EntityType.CREEPER) {
                    e.blockList().clear();
                }
            }
        });
    }

}
