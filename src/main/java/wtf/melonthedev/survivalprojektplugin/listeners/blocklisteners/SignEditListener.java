package wtf.melonthedev.survivalprojektplugin.listeners.blocklisteners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import java.util.Objects;

public class SignEditListener implements Listener {

    @EventHandler
    public void onSignEdit(SignChangeEvent e) {
        for (int i = 0; i < 4; i++) {
            if (e.getLine(i) == null) continue;
            e.setLine(i, ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(e.getLine(i))));
        }
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendSignChange(e.getBlock().getLocation(), e.getLines());
        }
    }
}
