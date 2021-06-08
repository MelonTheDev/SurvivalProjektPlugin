package wtf.melonthedev.survivalprojektplugin.listeners.playerlisteners;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import wtf.melonthedev.survivalprojektplugin.Main;

import java.util.Objects;
import java.util.UUID;

import static wtf.melonthedev.survivalprojektplugin.Main.colorerror;

public class PlayerDamageListener implements Listener {

    FileConfiguration config = Main.getPlugin().getConfig();

    @EventHandler
    public void onDamageGet(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        Player target = (Player) e.getEntity();
        ConfigurationSection petSection = config.getConfigurationSection(target.getName() + ".pet");
        UUID damagerUUID = e.getDamager().getUniqueId();
        if (petSection == null) return;
        if (petSection.contains("uuid") && damagerUUID.equals(UUID.fromString(Objects.requireNonNull(petSection.getString("uuid")))) || petSection.contains("others.passengerUUID") && damagerUUID.equals(UUID.fromString(Objects.requireNonNull(petSection.getString("others.passengerUUID")))))
            e.setCancelled(true);
    }

    @EventHandler
    public void onDamageDeal(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player)) return;
        Player damager = (Player) e.getDamager();
        if (config.getBoolean(damager.getName() + ".isAfk")) {
            damager.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(colorerror + "Du bist AFK und kannst nicht angreifen"));
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBowShoot(EntityShootBowEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        Player shooter = (Player) e.getEntity();
        if (config.getBoolean(shooter.getName() + ".isAfk")) {
            shooter.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(colorerror + "Du bist AFK und kannst nicht schießen"));
            e.setCancelled(true);
        }
    }
}
