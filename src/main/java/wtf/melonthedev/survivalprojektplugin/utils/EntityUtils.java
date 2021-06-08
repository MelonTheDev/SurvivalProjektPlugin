package wtf.melonthedev.survivalprojektplugin.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Objects;

public class EntityUtils {

    public static void sendFireBall(Player p) {
        Location loc = p.getLocation();
        loc.setY(loc.getY() + 1);
        p.getWorld().spawn(loc, Fireball.class);
    }


    public static void dropMobHead(Location loc, String owner, String minecraftTag) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        assert meta != null;
        meta.setOwningPlayer(Bukkit.getOfflinePlayer("MHF_" + owner));
        meta.setDisplayName(ChatColor.YELLOW + minecraftTag.toLowerCase().replace('_', ' ') + " Head");
        meta.setLocalizedName(minecraftTag + "-MHF_" + owner);
        head.setItemMeta(meta);
        Objects.requireNonNull(loc.getWorld()).dropItem(loc, head);
    }
}
