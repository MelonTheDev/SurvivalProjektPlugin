package wtf.melonthedev.survivalprojektplugin.listeners.playerlisteners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import wtf.melonthedev.survivalprojektplugin.utils.PlayerUtils;

import java.util.Objects;

public class PlayerGrapplingListener implements Listener {

    @EventHandler
    public void onFishing(PlayerFishEvent e) {
        Player p = e.getPlayer();
        if (p.getInventory().getItemInMainHand().getType() != Material.FISHING_ROD && p.getInventory().getItemInOffHand().getType() != Material.FISHING_ROD) {
            return;
        }
        if (e.getState() != PlayerFishEvent.State.IN_GROUND && e.getState() != PlayerFishEvent.State.REEL_IN && e.getState() != PlayerFishEvent.State.CAUGHT_ENTITY) {
            return;
        }
        ItemMeta im = p.getInventory().getItemInMainHand().getItemMeta();
        ItemMeta offhandIm = p.getInventory().getItemInOffHand().getItemMeta();
        if (im == null && offhandIm == null) {
            return;
        }

        if (im != null && im.hasDisplayName() && im.getDisplayName().equalsIgnoreCase("Grappling Hook")) {
            grapple(p, e.getHook());
        }

        if (offhandIm != null && offhandIm.hasDisplayName() && offhandIm.getDisplayName().equalsIgnoreCase("Grappling Hook")) {
            grapple(p, e.getHook());
        }
    }
    public void grapple(Player p, Entity hook) {
        //if (!PlayerUtils.checkOP(p)) return;
        Location loc = hook.getLocation();
        Vector velocity;
        if (loc.getY() > p.getLocation().getY())
            velocity = p.getLocation().getDirection().setY(1.5).normalize();
        else
            velocity = p.getLocation().getDirection().setY(-0.5).normalize();
        p.setVelocity(velocity);
    }

}