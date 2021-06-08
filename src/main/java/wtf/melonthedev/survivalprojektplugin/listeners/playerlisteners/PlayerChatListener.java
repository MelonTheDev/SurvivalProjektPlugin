package wtf.melonthedev.survivalprojektplugin.listeners.playerlisteners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import wtf.melonthedev.survivalprojektplugin.Main;
import wtf.melonthedev.survivalprojektplugin.commands.LockchestCommand;
import wtf.melonthedev.survivalprojektplugin.utils.GenericUtils;

import java.util.Objects;

import static wtf.melonthedev.survivalprojektplugin.Main.colorerror;
import static wtf.melonthedev.survivalprojektplugin.Main.serverprefix;

public class PlayerChatListener implements Listener {

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        //VARIABLES
        FileConfiguration config = Main.getPlugin().getConfig();
        Player messageSender = e.getPlayer();

        //INPUT
        if (config.getBoolean(messageSender.getName() + ".waitForInput.status")) {
            e.setCancelled(true);
            config.set(messageSender.getName() + ".waitForInput.status", null);
            if (config.getBoolean(messageSender.getName() + ".waitForInput.waitForLockChestTrustName")) {
                config.set(messageSender.getName() + ".waitForInput.waitForLockChestTrustName", null);
                Main.getPlugin().saveConfig();
                OfflinePlayer target = Bukkit.getOfflinePlayer(e.getMessage());
                Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> LockchestCommand.openTrustUpdateInventory(messageSender, target), 5);
            }
        }

        //RANKS
        //e.setFormat("[VIP] " + e.getFormat());

        //COLOR IN MESSAGES
        if (Objects.requireNonNull(GenericUtils.getCustomSettings("chat")).containsKey("translateAlternateColorCodes") && Objects.requireNonNull(GenericUtils.getCustomSettings("chat")).get("translateAlternateColorCodes") == ((Object) true))
            e.setMessage(ChatColor.translateAlternateColorCodes('&', e.getMessage()));

        //MUTE
        if (config.getBoolean(messageSender.getName() + ".isMuted")) {
            messageSender.sendMessage(colorerror + serverprefix + "Du bist gemuted und kannst keine Nachrichten senden.");
            e.setCancelled(true);
        }

        //FILTER
        String[] badWords = new String[] {"fuck", "bitch", "idiot", "hurensohn", "wichser", "fick dich", "huansohn", "nutte", "arsch", "hure"};
        String message = e.getMessage().toLowerCase();
        for (String word : badWords){
            if (!message.contains(word)){
                continue;
            }
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < word.length(); i++) {
                builder.append("#");
            }
            String newMessage = e.getMessage().replace(word, builder.toString());
            e.setMessage(newMessage);
            int fuckCount = config.getInt(messageSender.getName() + ".fuckCount");
            config.set(messageSender.getName() + ".fuckCount", fuckCount + 1);
            Main.getPlugin().saveConfig();
            if (fuckCount >= 5) {
                messageSender.sendMessage(colorerror + "Ey hör mal bitte auf böse sachen zu sagen.");
                config.set(messageSender.getName() + ".fuckCount", null);
                Main.getPlugin().saveConfig();
            }
        }
        if (e.getMessage().toLowerCase().contains("fuck")) {
            e.setMessage(message.replaceAll("fuck", "och nein"));
        }
    }
}
