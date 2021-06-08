package wtf.melonthedev.survivalprojektplugin.listeners.playerlisteners;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.server.ServerListPingEvent;
import wtf.melonthedev.survivalprojektplugin.others.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ServerPingListener implements Listener {

    public String[] CustomMOTDMessages = new String[] {
            "Survivalprojekt 2.00000000000000000000000000",
            "Survivalprojekt 2.0 | Welcome to the BEST SERVER",
            "Survivalprojekt 2.0 | Meine Katze stinkt",
            "Survivalprojekt 2.0 | BEST SERVER EVER",
            "Survivalprojekt 2.0 | FREE FOR ALL",
            "Survivalprojekt 2.0 | *wow* that's a custom MOTD",
            "Survivalprojekt 2.0: Hello there",
            "Survivalprojekt 2.0 | How are you?",
            "Survivalprojekt 2.0 | BEST PROJECT EVER",
            "Survivalprojekt 2.0 | IDK what to write here",
            "Survivalprojekt 2.0 | These MOTDs have always a different color",
            "Survivalprojekt 2.0 | discord.mcsurvivalprojekt.de will invite you to our DC",
            "Survivalprojekt 2.0 | null",
            "Survivalprojekt 2.0 | The cow in my base is cringe",
            "Survivalprojekt 2.0 | CREEPER! AWWW MAN",
            "Survivalprojekt 2.0 | Don't feed Avocados to Parrots. So the Minecraft splashes...",
            "Survivalprojekt 2.0 | PLS PLS PLS try to swim in lava. It'll be good for you.",
            "Survivalprojekt 2.0 | MOIN SERVUS MOIN",
            "Survivalprojekt 2.0, also known as 'ausgestorben'",
            "Survivalprojekt 2.0 | DON'T CLICK ON HYPIXEL",
            "Survivalprojekt 2.0 | CLICK HERE TO GET FREE DIRT",
            "Survivalprojekt 2.0 | pls pls join pls pls",
            "Survivalprojekt 2.0 | DEVELOPER: *ich hehe*",
            "Survivalprojekt 2.0 | Falls du vorschläge für MOTDs hast, schreib sie in unseren discord",
            "Survivalprojekt 2.0 | this server is hostet by contabo. 100% no advertsing",
            "Survivalprojekt 2.0 | Players ONLINE: 20/20                        what? you think that's a joke?",
            "Survivalprojekt 2.0 | Avocados from Mexico"
    };
    public String[] CustomMOTDColors = new String[] {
        "WHITE", "RED", "DARK_RED", "AQUA", "DARK_AQUA", "GREEN", "GOLD", "BLUE", "DARK_GRAY", "DARK_BLUE", "DARK_GREEN", "LIGHT_PURPLE", "DARK_PURPLE", "MAGIC", "YELLOW"
    };

    @EventHandler
    public void onPing(ServerListPingEvent e) {
        int randromIntForMessages =  ThreadLocalRandom.current().nextInt(0, CustomMOTDMessages.length);
        int randromIntForColors =  ThreadLocalRandom.current().nextInt(0, CustomMOTDColors.length);
        e.setMotd(ChatColor.valueOf(CustomMOTDColors[randromIntForColors]) + CustomMOTDMessages[randromIntForMessages]);

    }
}
