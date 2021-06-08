package wtf.melonthedev.survivalprojektplugin.listeners.entitylisteners;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.inventory.ItemStack;
import wtf.melonthedev.survivalprojektplugin.others.Config;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class EntitySpawnListener implements Listener {

    FileConfiguration settings = Config.getCustomConfig("settings.yml");

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent e) {
        if (settings.getBoolean("settings.spawning.diaZombies")) {
            if (e.getEntity().getType() == EntityType.ZOMBIE) {
                if (!(e.getEntity() instanceof Ageable)) return;
                Ageable entity = (Ageable) e.getEntity();
                if (!entity.isAdult()) return;
                boolean spawnWithDiamonds = ThreadLocalRandom.current().nextBoolean();
                if (!spawnWithDiamonds)
                    return;
                Zombie zombie = (Zombie) e.getEntity();
                Objects.requireNonNull(zombie.getEquipment()).setBoots(new ItemStack(Material.DIAMOND_BOOTS));
                zombie.getEquipment().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
                zombie.getEquipment().setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
                zombie.getEquipment().setLeggingsDropChance(0);
                zombie.getEquipment().setChestplateDropChance(0);
                zombie.getEquipment().setBootsDropChance(0);
                zombie.getEquipment().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
            }
        }
    }
}
