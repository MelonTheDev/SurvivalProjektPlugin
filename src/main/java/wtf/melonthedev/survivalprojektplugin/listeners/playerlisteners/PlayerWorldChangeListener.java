package wtf.melonthedev.survivalprojektplugin.listeners.playerlisteners;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import wtf.melonthedev.survivalprojektplugin.Main;
import wtf.melonthedev.survivalprojektplugin.others.NPC;
import wtf.melonthedev.survivalprojektplugin.utils.PacketReader;

public class PlayerWorldChangeListener implements Listener {

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent e) {
        FileConfiguration config = Main.getPlugin().getConfig();
        if (e.getPlayer().getWorld().getName().equalsIgnoreCase("world_the_end")) {
            e.getPlayer().sendMessage(ChatColor.DARK_GRAY + "You entered THE END!");
            if (!config.getBoolean(e.getPlayer().getName() + ".wasInEnd")) {
                e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1, 1);
                config.set(e.getPlayer().getName() + ".wasInEnd", true);
                Main.getPlugin().saveConfig();
            }
        }
        if (NPC.getNPCs() != null && !(NPC.getNPCs().isEmpty())) {
            NPC.addJoinPacket(e.getPlayer());
        }
        PacketReader reader = new PacketReader();
        reader.inject(e.getPlayer());
    }
}
