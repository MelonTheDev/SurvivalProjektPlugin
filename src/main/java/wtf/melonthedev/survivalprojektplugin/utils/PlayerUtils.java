package wtf.melonthedev.survivalprojektplugin.utils;

import com.mojang.datafixers.util.Pair;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import wtf.melonthedev.survivalprojektplugin.Main;
import wtf.melonthedev.survivalprojektplugin.commands.StatusCommand;
import wtf.melonthedev.survivalprojektplugin.others.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static wtf.melonthedev.survivalprojektplugin.Main.*;

public class PlayerUtils {

    //CHECKS
    public static boolean isPlayer(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(colorerror + serverprefix + "Du kannst dies nur als Spieler.");
            return false;
        }
        return true;
    }
    public static boolean isPlayerOnline(Player target) {
        if (target == null) return false;
        return target.isOnline();
    }
    public static boolean checkOP(Player p) {
        if (!p.isOp()) {
            p.sendMessage(colorerror + serverprefix + "Du hast keine Berechtigung. :^(");
            p.playSound(p.getLocation(), Sound.ENTITY_BAT_DEATH, SoundCategory.VOICE, 1, 0);
            return false;
        }
        return true;
    }
    public static boolean checkOP(CommandSender sender) {
        if (sender instanceof Player) {
            return checkOP((Player) sender);
        }
        if (!sender.isOp()) {
            sender.sendMessage(colorerror + serverprefix + "Du hast keine Berechtigung. :^(");
            return false;
        }
        return true;
    }

    //ERROR
    public static void sendError(Player player, String error) {
        player.sendMessage(colorerror + serverprefix + error);
    }
    public static void sendSyntaxError(Player p, String error) {
        p.sendMessage(colorerror + serverprefix + "Syntaxerror: " + error);
    }
    public static void sendSyntaxError(CommandSender sender, String error) {
        sender.sendMessage(colorerror + serverprefix + "Syntaxerror: " + error);
    }
    public static void sendCustomError(Player p, String error) {
        p.sendMessage(colorerror + serverprefix + error);
    }

    //HANDLE THINGS
    public static void setAfk(Player p, Boolean flag) {
        FileConfiguration config = Main.getPlugin().getConfig();
        if (flag) {
            p.setWalkSpeed(0);
            p.setGameMode(GameMode.ADVENTURE);
            p.setInvulnerable(true);
            p.setSleepingIgnored(true);
            p.setCollidable(false);
            Bukkit.broadcastMessage(colorinfo + serverprefix + p.getName() + " ist jetzt AFK!");
            p.setPlayerListName(ChatColor.RED + "[AFK] " + ChatColor.RESET + p.getName());
            p.setDisplayName(ChatColor.RED + "[AFK] " + ChatColor.RESET + p.getName());
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(colorinfo + "Du bist jetzt AFK! Springe, um den AFK-Modus zu deaktivieren."));
            config.set(p.getName() + ".isAfk", true);
        } else {
            p.setWalkSpeed(0.2F);
            p.setGameMode(GameMode.SURVIVAL);
            p.setInvulnerable(false);
            p.setSleepingIgnored(false);
            p.setCollidable(true);
            p.setPlayerListName(null);
            p.setDisplayName(null);
            StatusCommand.showDisplayName(p);
            Bukkit.broadcastMessage(colorinfo + serverprefix + p.getName() + " ist jetzt nicht mehr AFK!");
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent());
            config.set(p.getName() + ".isAfk", null);
        }
        Main.getPlugin().saveConfig();
    }
    public static void pinInventoryContents(Player p) {
        FileConfiguration config = Main.getPlugin().getConfig();
        config.set(p.getName() + ".Inv.isopen", true);
        Main.getPlugin().saveConfig();
    }
    public static void setVanish(Player target, Boolean flag) {
        if (flag) {
            target.setDisplayName(ChatColor.MAGIC + "Player55" + ChatColor.RESET);
            target.setSilent(true);
            target.setGameMode(GameMode.CREATIVE);
            target.setWalkSpeed(0.5F);
            target.setInvisible(true);
            target.setArrowsInBody(0);
            target.setSleepingIgnored(true);
            target.setFlySpeed(0.5F);
            target.setCollidable(false);
            target.setInvulnerable(true);
            PotionEffect e = new PotionEffect(PotionEffectType.NIGHT_VISION, 99999, 255, true, false, false);
            target.addPotionEffect(e);
            Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    EntityPlayer craftplayer = ((CraftPlayer) target).getHandle();
                    PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
                    connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, craftplayer));
                    connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.UPDATE_GAME_MODE, craftplayer));
                }
            }, 10);
            hideArmor(target);
        } else {
            target.setDisplayName(null);
            target.setSilent(false);
            target.setGameMode(GameMode.SURVIVAL);
            target.setWalkSpeed(0.2F);
            target.setInvisible(false);
            target.setSleepingIgnored(false);
            target.setFlySpeed(0.1F);
            target.setCollidable(true);
            target.setInvulnerable(false);
            target.getActivePotionEffects().forEach(potionEffect -> target.removePotionEffect(potionEffect.getType()));
            for (Player player : Bukkit.getOnlinePlayers()) {
                EntityPlayer craftplayer = ((CraftPlayer) target).getHandle();
                PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
                connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, craftplayer));
            }
            showArmor(target);
        }
    }
    public static void hideArmor(Player player) {
        final List<Pair<EnumItemSlot, net.minecraft.server.v1_16_R3.ItemStack>> equipmentList = new ArrayList<>();
        equipmentList.add(new Pair<>(EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(new ItemStack(Material.AIR))));
        equipmentList.add(new Pair<>(EnumItemSlot.CHEST, CraftItemStack.asNMSCopy(new ItemStack(Material.AIR))));
        equipmentList.add(new Pair<>(EnumItemSlot.LEGS, CraftItemStack.asNMSCopy(new ItemStack(Material.AIR))));
        equipmentList.add(new Pair<>(EnumItemSlot.FEET, CraftItemStack.asNMSCopy(new ItemStack(Material.AIR))));
        equipmentList.add(new Pair<>(EnumItemSlot.MAINHAND, CraftItemStack.asNMSCopy(new ItemStack(Material.AIR))));
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getName().equals(player.getName())) continue;
            ((CraftPlayer) p).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityEquipment(player.getEntityId(), equipmentList));
        }
    }
    public static void showArmor(Player player) {
        org.bukkit.inventory.PlayerInventory inv = player.getInventory();
        final List<Pair<EnumItemSlot, net.minecraft.server.v1_16_R3.ItemStack>> equipmentList = new ArrayList<>();
        equipmentList.add(new Pair<>(EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(inv.getHelmet())));
        equipmentList.add(new Pair<>(EnumItemSlot.CHEST, CraftItemStack.asNMSCopy(inv.getChestplate())));
        equipmentList.add(new Pair<>(EnumItemSlot.LEGS, CraftItemStack.asNMSCopy(inv.getLeggings())));
        equipmentList.add(new Pair<>(EnumItemSlot.FEET, CraftItemStack.asNMSCopy(inv.getBoots())));
        equipmentList.add(new Pair<>(EnumItemSlot.MAINHAND, CraftItemStack.asNMSCopy(inv.getItemInMainHand())));
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getName().equals(player.getName())) continue;
            ((CraftPlayer) p).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityEquipment(player.getEntityId(), equipmentList));
        }
    }
    public static ItemStack createBook(String value) {
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta bookMeta = (BookMeta) book.getItemMeta();
        if (bookMeta == null) throw new NullPointerException("BookMeta is null");
        bookMeta.setTitle("Blank");
        bookMeta.setAuthor("Blank");

        float pages = (float) value.length() / 255;
        for (int i = 0; i < pages; i++) {
            int start = i * 255;
            int end = start + 255;
            String page;
            if (value.substring(start).length() < 255) {
                page = value.substring(start);
            } else {
                page = value.substring(start, end);
            }
            bookMeta.addPage(page);
        }
        book.setItemMeta(bookMeta);
        return book;
    }
    public static void removeTempArrows(Player p) {
        for (ItemStack is : p.getInventory().getContents()) {
            if (is == null) continue;
            if (!is.hasItemMeta()) continue;
            if (!Objects.requireNonNull(is.getItemMeta()).hasDisplayName()) continue;
            if (is.getItemMeta().getDisplayName().equals(colorinfo + "TEMP ARROW")) is.setAmount(0);
        }
    }

    public static void makeInvBackup(Player player) {
        FileConfiguration invBackups = Config.getCustomConfig("invbackups.yml");
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            invBackups.set(player.getName() + "." + i, player.getInventory().getItem(i));
        }
        Config.saveNewCustomConfig(invBackups, "invbackups.yml");
    }
    public static ItemStack[] getInvBackup(Player player) {
        FileConfiguration invBackups = Config.getCustomConfig("invbackups.yml");
        if (!invBackups.contains(player.getName())) return null;
        ItemStack[] contents = new ItemStack[41];
        for (int i = 0; i < contents.length; i++) {
            if (!invBackups.contains(player.getName() + "." + i)) continue;
            contents[i] = invBackups.getItemStack(player.getName() + "." + i);
        }
        return contents;
    }
}
