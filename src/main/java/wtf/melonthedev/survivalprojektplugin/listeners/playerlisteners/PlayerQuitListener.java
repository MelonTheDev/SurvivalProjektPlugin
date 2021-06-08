package wtf.melonthedev.survivalprojektplugin.listeners.playerlisteners;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import wtf.melonthedev.survivalprojektplugin.Main;
import wtf.melonthedev.survivalprojektplugin.commands.KitPvpCommand;
import wtf.melonthedev.survivalprojektplugin.utils.PacketReader;
import wtf.melonthedev.survivalprojektplugin.utils.PetUtils;
import wtf.melonthedev.survivalprojektplugin.utils.PlayerUtils;

import java.util.concurrent.ThreadLocalRandom;

import static wtf.melonthedev.survivalprojektplugin.Main.colorinfo;
import static wtf.melonthedev.survivalprojektplugin.Main.serverprefix;

public class PlayerQuitListener implements Listener {

    public String[] customLeaveMessages = new String[] {
            " hat Alt+F4 gedrückt.", " hatte keinen Bock mehr und ist gegangen.",
            " wurde zum Essen gerufen.", " wurde von Herobrine gebannt.",
            " ist gegangen ohne Bye zu sagen.", " dachte, Alt+F4 würde ihm Diamanten geben.",
            " wollte nicht sterben und hat sich ausgeloggt.", " left the cool SurvivalprojektServer.",
            " wird bald wiederkommen.", " muss jetzt off.", " ist jetzt plötzlich weg."
    };

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        FileConfiguration config = Main.getPlugin().getConfig();
        //QUITMESSAGE
        int randromInt =  ThreadLocalRandom.current().nextInt(0, customLeaveMessages.length);
        e.setQuitMessage(colorinfo + "<< " + serverprefix + p.getName() + customLeaveMessages[randromInt]);

        //PET
        ConfigurationSection petSection = config.getConfigurationSection(p.getName() + ".pet");
        if (petSection != null) {
            if (petSection.getBoolean("isSpawned")) {
                PetUtils.despawnPet(p);
                petSection.set("wasSpawned", true);
                petSection.set("uuid", null);
                petSection.set("others.passengerUUID", null);
                Main.getPlugin().saveConfig();
            }
        }

        //MSG
        config.set(p.getName() + ".conversationWith", null);
        Main.getPlugin().saveConfig();

        //INV BACKUP
        if (!config.getBoolean(p.getName() + ".kitpvp.inPvp")) PlayerUtils.makeInvBackup(p);

        //NPCs
        PacketReader reader = new PacketReader();
        reader.uninject(e.getPlayer());

        //VANISH
        if (config.getBoolean(p.getName() + ".vanish.enabled")) {
            PlayerUtils.setVanish(p, false);
            e.setQuitMessage(null);
        }
    }
}
