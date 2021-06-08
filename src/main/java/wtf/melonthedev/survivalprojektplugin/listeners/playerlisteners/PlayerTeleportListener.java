package wtf.melonthedev.survivalprojektplugin.listeners.playerlisteners;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import wtf.melonthedev.survivalprojektplugin.Main;
import wtf.melonthedev.survivalprojektplugin.utils.PlayerUtils;

public class PlayerTeleportListener implements Listener {

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
        FileConfiguration config = Main.getPlugin().getConfig();
        if (config.getBoolean(e.getPlayer().getName() + ".isAfk")) {
            PlayerUtils.setAfk(e.getPlayer(), false);
        }
    }
}
