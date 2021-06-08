package wtf.melonthedev.survivalprojektplugin.listeners.entitylisteners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import wtf.melonthedev.survivalprojektplugin.utils.ItemUtils;

public class EntityShootListener implements Listener {

    @EventHandler
    public void onShoot(EntityShootBowEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        Player p = (Player) e.getEntity();
        ItemUtils.removeTempArrow(p);
    }
}
