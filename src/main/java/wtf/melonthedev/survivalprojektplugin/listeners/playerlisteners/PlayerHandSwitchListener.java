package wtf.melonthedev.survivalprojektplugin.listeners.playerlisteners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import wtf.melonthedev.survivalprojektplugin.utils.ItemUtils;

public class PlayerHandSwitchListener implements Listener {

    @EventHandler
    public void onHandSwitch(PlayerItemHeldEvent event) {
        Player p = event.getPlayer();
        ItemStack item = p.getInventory().getItem(event.getNewSlot());
        if (item != null && item.getType() == Material.BOW) return;
        ItemUtils.removeTempArrow(p);
    }
}