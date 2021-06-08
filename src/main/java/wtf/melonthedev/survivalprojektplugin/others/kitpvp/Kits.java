package wtf.melonthedev.survivalprojektplugin.others.kitpvp;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import wtf.melonthedev.survivalprojektplugin.Main;
import wtf.melonthedev.survivalprojektplugin.commands.KitPvpCommand;

import java.util.Objects;

public class Kits {

    static FileConfiguration config = Main.getPlugin().getConfig();

    public static void giveKit(Player player) {
        player.getInventory().clear();
        String kit = getKit(player);
        switch (kit) {
            case "standard":
                getStandardKit(player);
                break;
            case "pro":
                getProKit(player);
                break;
            case "ultra":
                getUltraKit(player);
                break;
            case "epic":
                getEpicKit(player);
                break;
            case "sniper":
                getSniperKit(player);
                break;
            case "op":
                getOpKit(player);
                break;
            case "pearler":
                getPearlerKit(player);
                break;
        }
        KitPvpCommand.updateScoreboard();
        KitPvpCommand.oldAttackSpeed(player);
    }
    public static String getKit(Player player) {
        ConfigurationSection kitSection;
        if (config.isConfigurationSection(player.getName() + ".kitpvp")) kitSection = config.getConfigurationSection(player.getName() + ".kitpvp");
        else kitSection = config.createSection(player.getName() + ".kitpvp");
        assert kitSection != null;
        if (!kitSection.contains("kit")) {
            kitSection.set("kit", "standard");
            Main.getPlugin().saveConfig();
        }
        return kitSection.getString("kit");
    }

    //KITS
    private static void getStandardKit(Player player) {
        PlayerInventory inv = player.getInventory();
        setStandardHotbar(player, Material.IRON_SWORD, 1, Material.GOLDEN_CARROT, 10, getKitBuildBlock("standard"), 64);
        setTools(player, Material.STONE_PICKAXE, Material.STONE_SHOVEL);
        setArmor(player, leatherArmor, false);
    }

    private static void getProKit(Player player) {
        PlayerInventory inv = player.getInventory();
        setStandardHotbar(player, Material.IRON_SWORD, 1, Material.GOLDEN_CARROT, 16, getKitBuildBlock("pro"), 64);
        setTools(player, Material.STONE_PICKAXE, Material.STONE_SHOVEL);
        setLastHotbarSlot(player, Material.WATER_BUCKET, 1);
        setArmor(player, ironArmor, false);
    }

    private static void getUltraKit(Player player) {
        PlayerInventory inv = player.getInventory();
        //HOTBAR
        setStandardHotbar(player, Material.IRON_SWORD, 1, Material.FISHING_ROD, 1, Material.GOLDEN_CARROT, 16, Material.COBWEB, 16, getKitBuildBlock("ultra"), 64, Material.AIR, 0, Material.WATER_BUCKET, 1);
        //ARMOR
        setArmor(player, ironArmor, true);
        //UPPER INV
        setTools(player, Material.STONE_PICKAXE, Material.STONE_SHOVEL);
    }
    private static void getEpicKit(Player player) {
        PlayerInventory inv = player.getInventory();
        //HOTBAR
        setStandardHotbar(player, Material.IRON_SWORD, 1, Material.BOW, 1, Material.FISHING_ROD, 1, Material.GOLDEN_CARROT, 16, Material.WATER_BUCKET, 1);
        ItemStack sword = new ItemStack(Material.IRON_SWORD);
        sword.addEnchantment(Enchantment.DAMAGE_ALL, 1);
        inv.setItem(0, sword);
        inv.setItem(4, new ItemStack(Material.COBWEB, 16));
        inv.setItem(5, new ItemStack(getKitBuildBlock("epic"), 64));
        inv.setItem(7, new ItemStack(Material.ARROW, 16));
        //ARMOR
        setArmor(player, ironArmor, true);
        setTools(player, Material.STONE_PICKAXE, Material.STONE_SHOVEL);
    }
    private static void getSniperKit(Player player) {
        PlayerInventory inv = player.getInventory();
        setStandardHotbar(player, Material.IRON_SWORD, 1, Material.BOW, 1, Material.FISHING_ROD, 1, Material.GOLDEN_CARROT, 16, getKitBuildBlock("sniper"), 64, Material.SKELETON_SPAWN_EGG, 1, Material.WATER_BUCKET, 1);
        ItemStack bow = new ItemStack(Material.BOW);
        bow.addEnchantment(Enchantment.ARROW_KNOCKBACK, 1);
        bow.addEnchantment(Enchantment.ARROW_INFINITE, 1);
        inv.setItem(1, bow);
        setArmor(player, ironArmor, true);
        setTools(player, Material.STONE_PICKAXE, Material.STONE_SHOVEL, Material.ARROW);
    }
    private static void getOpKit(Player player) {
        PlayerInventory inv = player.getInventory();
        setStandardHotbar(player, Material.DIAMOND_SWORD, 1, getKitBuildBlock("op"), 64, Material.COBWEB, 8);
        setLastHotbarSlot(player, Material.WATER_BUCKET, 1);
        setArmor(player, leatherArmor, false);
        setTools(player, Material.STONE_PICKAXE, Material.STONE_SHOVEL);
    }
    private static void getPearlerKit(Player player) {
        PlayerInventory inv = player.getInventory();
        setStandardHotbar(player, Material.IRON_SWORD, 1, Material.FISHING_ROD, 1, Material.GOLDEN_CARROT, 16, getKitBuildBlock("pearler"), 64, Material.WATER_BUCKET, 1);
        inv.setItem(4, new ItemStack(Material.ENDER_PEARL, 4));
        setArmor(player, ironArmor, false);
        setTools(player, Material.STONE_PICKAXE, Material.STONE_SHOVEL);
    }



    private static final ItemStack[] ironArmor = new ItemStack[] {new ItemStack(Material.IRON_BOOTS), new ItemStack(Material.IRON_LEGGINGS), new ItemStack(Material.IRON_CHESTPLATE), new ItemStack(Material.IRON_HELMET)};
    private static final ItemStack[] leatherArmor = new ItemStack[] {new ItemStack(Material.LEATHER_BOOTS), new ItemStack(Material.LEATHER_LEGGINGS), new ItemStack(Material.LEATHER_CHESTPLATE), new ItemStack(Material.LEATHER_HELMET)};
    private static void setTools(Player player, Material tool1, Material tool2, Material tool3) {
        PlayerInventory inv = player.getInventory();
        inv.setItem(9, new ItemStack(tool1));
        inv.setItem(10, new ItemStack(tool2));
        inv.setItem(11, new ItemStack(tool3));
    }
    private static void setTools(Player player, Material tool1, Material tool2) {
        setTools(player, tool1, tool2, Material.AIR);
    }
    //Slot 0, 1, 2, 3, 4, 5, 9
    private static void setStandardHotbar(Player player, Material slot1, int amount1, Material slot2, int amount2, Material slot3, int amount3, Material slot4, int amount4, Material slot5, int amount5, Material slot6, int amount6, Material slot9, int amount9) {
        PlayerInventory inv = player.getInventory();
        //HOTBAR
        inv.setItem(0, new ItemStack(slot1, amount1));
        inv.setItem(1, new ItemStack(slot2, amount2));
        inv.setItem(2, new ItemStack(slot3, amount3));
        inv.setItem(3, new ItemStack(slot4, amount4));
        inv.setItem(4, new ItemStack(slot5, amount5));
        inv.setItem(5, new ItemStack(slot6, amount6));
        inv.setItem(8, new ItemStack(slot9, amount9));
    }
    //Slot 0, 1, 2, 3, 4, 9
    private static void setStandardHotbar(Player player, Material slot1, int amount1, Material slot2, int amount2, Material slot3, int amount3, Material slot4, int amount4, Material slot9, int amount9) {
        setStandardHotbar(player, slot1, amount1, slot2, amount2, slot3, amount3, slot4, amount4, Material.AIR, 0, Material.AIR, 0, slot9, amount9);
    }
    //Slot 0, 1, 2
    private static void setStandardHotbar(Player player, Material slot1, int amount1, Material slot2, int amount2, Material slot3, int amount3) {
        setStandardHotbar(player, slot1, amount1, slot2, amount2, slot3, amount3, Material.AIR, 0, Material.AIR, 0);
    }
    private static void setLastHotbarSlot(Player player, Material material, int amount) {
        player.getInventory().setItem(8, new ItemStack(material, amount));
    }
    private static void setArmor(Player player, ItemStack[] armor, boolean hasShield) {
        player.getInventory().setArmorContents(armor);
        if (hasShield) player.getInventory().setItemInOffHand(new ItemStack(Material.SHIELD));
    }

    public static Material getKitBuildBlock(String kit) {
        switch (kit) {
            case "standard":
                return Material.LIGHT_BLUE_CONCRETE;
            case "pro":
                return Material.CYAN_CONCRETE;
            case "ultra":
                return Material.PINK_CONCRETE;
            case "epic":
                return Material.PURPLE_CONCRETE;
            case "sniper":
                return Material.LIME_CONCRETE;
            case "op":
                return Material.RED_CONCRETE;
            case "pearler":
                return Material.BLUE_CONCRETE;
            default:
                return Material.MAGENTA_CONCRETE;
        }
    }
}
