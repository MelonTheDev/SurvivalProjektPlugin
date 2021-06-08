package wtf.melonthedev.survivalprojektplugin.listeners.playerlisteners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;

public class PlayerKickListener implements Listener {

    @EventHandler
    public void onPlayerKick(PlayerKickEvent e) {
        System.out.println("Player " + e.getPlayer().getName() + " was kicked for: " + e.getReason());
    }
}
