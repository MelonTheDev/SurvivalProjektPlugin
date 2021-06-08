package wtf.melonthedev.survivalprojektplugin.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootTable;

import java.io.File;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import static wtf.melonthedev.survivalprojektplugin.Main.colorinfo;
import static wtf.melonthedev.survivalprojektplugin.Main.serverprefix;

public class WorldUtils {

    public static void spawnNaturally(ItemStack item, Chest chest) {
        LootTable lootTable = Bukkit.getLootTable(NamespacedKey.minecraft("chest/jungle_temple"));
        chest.setLootTable(lootTable);
        chest.getInventory().addItem(item);
    }

    public static String[] newDayMessages = new String[] {
            ", und bringt neue Tode.", ", damit man wieder an den Villianischen gott glauben kann.",
            ", weil die Sonne halt schön ist.", ", damit die Monster verbrennen.",
            ". Ja, da gibt's nicht mehr zu sagen.", ", weil Bäume cool sind.",
            ", damit er wieder enden kann."
    };

    public static void beginNewDay() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.getWorld().setTime(100L);
            p.getWorld().setStorm(false);
            p.setSleepingIgnored(false);
        }
        int randromInt =  ThreadLocalRandom.current().nextInt(0, newDayMessages.length);
        Bukkit.broadcastMessage(colorinfo + serverprefix + "Ein neuer Tag hat begonnen" + newDayMessages[randromInt]);
    }

    public static void resetWorld(World world) {

    }
    public static void delete(File f) {
        if (f.isDirectory()) {
            for (File c : Objects.requireNonNull(f.listFiles())) {
                delete(c);
            }
        }
        f.delete();
    }
}
