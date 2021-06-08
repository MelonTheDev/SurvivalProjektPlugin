package wtf.melonthedev.survivalprojektplugin.listeners.entitylisteners;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import wtf.melonthedev.survivalprojektplugin.Main;
import wtf.melonthedev.survivalprojektplugin.utils.PetUtils;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class EntityClickListener implements Listener {

    private final String[] villagerNames = new String[] {
            "Paul", "Fred", "Hans", "Peter", "Jakob", "Marlon", "Björn", "Hans-Peter", "Tom", "Rudolf", "Ralf", "Timmy"
    };

    @EventHandler
    public void onRightClickEntity(PlayerInteractEntityEvent e) {
        Player p = e.getPlayer();
        Entity entity = e.getRightClicked();
        FileConfiguration config = Main.getPlugin().getConfig();

        //PETS
        if (!config.contains(p.getName() + ".pet.uuid")) return;
        UUID uuid = UUID.fromString(Objects.requireNonNull(config.getString(p.getName() + ".pet.uuid")));
        if (!entity.getUniqueId().equals(uuid)) return;
        if (p.isSneaking()) {
            //SIT DOWN LOGIC
        } else {
            PetUtils.setPlayerOnSaddle(p);
        }
    }

    @EventHandler
    public void onDildoClick(PlayerInteractEntityEvent e) {
        Player p = e.getPlayer();
        Entity entity = e.getRightClicked();
        if (entity.getType() != EntityType.VILLAGER && !(entity instanceof Player))
            return;
        if (!(entity instanceof Ageable))
            return;
        if (p.getInventory().getItemInMainHand().getType() != Material.END_ROD)
            return;
        ItemStack dildo = p.getInventory().getItemInMainHand();
        if (!dildo.hasItemMeta())
            return;
        ItemMeta im = dildo.getItemMeta();
        assert im != null;
        if (!im.hasDisplayName())
            return;
        if (!im.getDisplayName().equalsIgnoreCase("DILDO") && !im.getDisplayName().equalsIgnoreCase("SCHWANZ"))
            return;
        e.setCancelled(true);
        if (((Ageable) entity).isAdult()) {
            p.sendMessage("<Villager> ;)");
            p.getWorld().spawnParticle(Particle.HEART, entity.getLocation(), 20, 0.5, 0, 0.5);
            p.getWorld().spawnParticle(Particle.CLOUD, entity.getLocation(), 40, 0, 0, 0, 3);
            return;
        }
        ((Ageable) entity).setAdult();
        int randromName =  ThreadLocalRandom.current().nextInt(0, villagerNames.length);
        p.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, entity.getLocation(), 20, 0.5, 0, 0.5);
        p.sendMessage("<Villager " + villagerNames[randromName] + "> OUUHH krass ich bin groß geworden.");
    }
}
