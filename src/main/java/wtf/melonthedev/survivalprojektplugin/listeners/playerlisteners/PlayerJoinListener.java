package wtf.melonthedev.survivalprojektplugin.listeners.playerlisteners;

import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import wtf.melonthedev.survivalprojektplugin.*;
import wtf.melonthedev.survivalprojektplugin.commands.KitPvpCommand;
import wtf.melonthedev.survivalprojektplugin.commands.StatusCommand;
import wtf.melonthedev.survivalprojektplugin.others.NPC;
import wtf.melonthedev.survivalprojektplugin.utils.*;

import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import static wtf.melonthedev.survivalprojektplugin.Main.*;

public class PlayerJoinListener implements Listener {

    FileConfiguration config = Main.getPlugin().getConfig();

    public String[] CustomJoinMessages = new String[] {
            "beigetreten und geht bald wieder.", "beigetreten und hofft auf einen neuen Tod.",
            "beigetreten aber hat keinen Plan was er tun soll.", "hier, weil er zum Villianischen Gott beten will.",
            "beigetreten, weil er sich in der Serverliste verklickt hat.", "beigetreten, einfach halt weil er cool ist. KA.",
            "beigetreten, weil er's kann.", "auf den Server gehopst.", "hier um zu sterben."
    };

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        //JOIN MESSAGE
        int randromInt =  ThreadLocalRandom.current().nextInt(0, CustomJoinMessages.length);
        e.setJoinMessage(colorinfo + ">> " + serverprefix + p.getDisplayName() + " ist " + CustomJoinMessages[randromInt]);

        //LOGINMESSAGECOMMAND
        if (config.contains(p.getName() + ".loginmessage")) {
            String loginMessage = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString(p.getName() + ".loginmessage")));
            ItemStack book = PlayerUtils.createBook(loginMessage);
            p.openBook(book);
            config.set(p.getName() + ".loginmessage", null);
            Main.getPlugin().saveConfig();
        }

        //FIXES
        p.resetTitle();

        //NPCs
        NPC.addJoinPacket(e.getPlayer());
        PacketReader reader = new PacketReader();
        reader.inject(e.getPlayer());

        //KITPVP
        if (config.getBoolean(p.getName() + ".kitpvp.inPvp")) {
            KitPvpCommand.setupScoreboard(p);
        }


        //STATUS
        StatusCommand.showDisplayName(p);

        //AFK
        if (config.contains(p.getName() + ".isAfk") && config.getBoolean(p.getName() + ".isAfk")) {
            p.setPlayerListName(ChatColor.RED + "[AFK] " + ChatColor.RESET + p.getName());
            p.setDisplayName(ChatColor.RED + "[AFK] " + ChatColor.RESET + p.getName());
        }

        //PETS
        ConfigurationSection petSection = config.getConfigurationSection(p.getName() + ".pet");
        if (petSection != null && petSection.getBoolean("wasSpawned"))
            PetUtils.spawnPet(p);

        //PLAYERLIST CUSTOMISATION
        HashMap<String, Object> customSettings = GenericUtils.getCustomSettings("tabList");
        if (customSettings.containsKey("customList") && (Boolean) customSettings.get("customList") && customSettings.containsKey("listHeader") && customSettings.containsKey("listFooter")) {
            String header = customSettings.get("listHeader").toString();
            String footer = customSettings.get("listFooter").toString();
            p.setPlayerListHeaderFooter(header, footer);
        }

        //VANISH
        if (config.getBoolean(p.getName() + ".vanish.enabled")) {
            e.setJoinMessage(null);
            PlayerUtils.setVanish(p, true);
            return;
        }
        //SOUND
        Bukkit.getOnlinePlayers().forEach(player -> {
            if (!config.getBoolean(player.getName() + ".joinSoundDisabled"))
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
        });


    }
}
