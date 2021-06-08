package wtf.melonthedev.survivalprojektplugin.listeners.entitylisteners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import wtf.melonthedev.survivalprojektplugin.utils.PetUtils;

import java.util.Objects;
import java.util.UUID;

public class EntityTargetListener implements Listener {

    @EventHandler
    public void onTarget(EntityTargetEvent e) {

        //PETS ANTI-TARGET
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (PetUtils.getPet(player) == null) continue;
            UUID UUID = Objects.requireNonNull(PetUtils.getPet(player)).getUniqueId();
            if (e.getEntity().getUniqueId().toString().equals(UUID.toString())) e.setCancelled(true);
            if (PetUtils.getPassengerPet(player) == null) continue;
            UUID passengerUUID = Objects.requireNonNull(PetUtils.getPassengerPet(player)).getUniqueId();
            if (e.getEntity().getUniqueId().toString().equals(passengerUUID.toString())) e.setCancelled(true);
        }

        //MAGMACUBES/SLIMES
        if (e.getEntity().getType() == EntityType.MAGMA_CUBE || e.getEntity().getType() == EntityType.SLIME) {
            if (!(e.getTarget() instanceof Player)) return;
            Player target = (Player) e.getTarget();
            ItemStack head = target.getInventory().getHelmet();
            if (head == null) return;
            if (head.getType() != Material.PLAYER_HEAD) return;
            if (!head.hasItemMeta()) return;
            SkullMeta sMeta = (SkullMeta) head.getItemMeta();
            if (sMeta == null) return;
            if (!sMeta.hasOwner()) return;
            if (Objects.requireNonNull(sMeta.getOwner()).equalsIgnoreCase("MHF_LavaSlime") && e.getEntity().getType() == EntityType.MAGMA_CUBE) {
                e.setCancelled(true);
            } else if (Objects.requireNonNull(sMeta.getOwner()).equalsIgnoreCase("MHF_Slime") && e.getEntity().getType() == EntityType.SLIME) {
                e.setCancelled(true);
            }
        }
    }
}
