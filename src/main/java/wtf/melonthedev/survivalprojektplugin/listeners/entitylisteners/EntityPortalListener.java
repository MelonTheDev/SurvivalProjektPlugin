package wtf.melonthedev.survivalprojektplugin.listeners.entitylisteners;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import wtf.melonthedev.survivalprojektplugin.Main;

import java.util.Objects;

public class EntityPortalListener implements Listener {

    @EventHandler
    public void onPortal(EntityPortalEnterEvent e) {
        e.getEntity().setFallDistance(0);
        for (Player p : Bukkit.getOnlinePlayers()) {
            ConfigurationSection petSection = Main.getPlugin().getConfig().getConfigurationSection(p.getName() + ".pet");
            if (petSection == null) return;
            if (Objects.equals(petSection.getString("uuid"), e.getEntity().getUniqueId().toString())) {
                e.getEntity().setPortalCooldown(Integer.MAX_VALUE);
            }
        }

    }
}
