package wtf.melonthedev.survivalprojektplugin.utils;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataType;
import wtf.melonthedev.survivalprojektplugin.Main;
import wtf.melonthedev.survivalprojektplugin.others.Config;

import static wtf.melonthedev.survivalprojektplugin.Main.colorinfo;

public class ItemUtils {

    public static void registerCustomRecipes() {
        //BROKEN LODESTONE
        //item
        ItemStack brokenLodeStone = new ItemStack(Material.LODESTONE);
        ItemMeta brokenLodeStoneMeta = brokenLodeStone.getItemMeta();
        assert brokenLodeStoneMeta != null;
        brokenLodeStoneMeta.setDisplayName(ChatColor.RESET + "Broken Lodestone");
        brokenLodeStoneMeta.setLocalizedName("broken_lodestone");
        brokenLodeStone.setItemMeta(brokenLodeStoneMeta);
        //recipe
        NamespacedKey brokenLodeStoneKey = new NamespacedKey(Main.getPlugin(), "broken_lodestone");
        ShapedRecipe brokenLodeStoneRecipe = new ShapedRecipe(brokenLodeStoneKey, brokenLodeStone);
        brokenLodeStoneRecipe.shape("CCC", "CIC", "CCC");
        brokenLodeStoneRecipe.setIngredient('C', Material.CHISELED_STONE_BRICKS);
        brokenLodeStoneRecipe.setIngredient('I', Material.IRON_INGOT);
        //add recipe
        Bukkit.addRecipe(brokenLodeStoneRecipe);

        //INVISIBLE ITEM FRAME
        //item
        ItemStack invisItemFrame = new ItemStack(Material.ITEM_FRAME);
        ItemMeta invisItemFrameMeta = invisItemFrame.getItemMeta();
        assert invisItemFrameMeta != null;
        NamespacedKey invisItemFrameKey = new NamespacedKey(Main.getPlugin(), "invisible_item_frame");
        invisItemFrameMeta.getPersistentDataContainer().set(invisItemFrameKey, PersistentDataType.BYTE, (byte) 1);
        invisItemFrameMeta.setDisplayName(ChatColor.RESET + "Invisible Item Frame");
        invisItemFrameMeta.setLocalizedName("invisible_item_frame");
        invisItemFrame.setItemMeta(invisItemFrameMeta);

        //recipe
        ShapedRecipe invisItemFrameRecipe = new ShapedRecipe(invisItemFrameKey, invisItemFrame);
        invisItemFrameRecipe.shape("GGG", "GFG", "GGG");
        invisItemFrameRecipe.setIngredient('F', Material.ITEM_FRAME);
        invisItemFrameRecipe.setIngredient('G', Material.GLASS_PANE);
        //add recipe
        Bukkit.addRecipe(invisItemFrameRecipe);
    }

    public static void unregisterRecipes() {
        Bukkit.resetRecipes();
    }

    public static void loadLodeStones() {
        FileConfiguration blocks = Config.getCustomConfig("blocks.yml");
        ConfigurationSection lodeStones = blocks.getConfigurationSection("lodestone");
        if (lodeStones == null) return;
        lodeStones.getKeys(false).forEach(key -> {
            try {
                int x = Integer.parseInt(key.split("_")[0]);
                int y = Integer.parseInt(key.split("_")[1]);
                int z = Integer.parseInt(key.split("_")[2]);
                String world = key.split("_")[3];
                Location loc = new Location(Bukkit.getWorld(world), x, y, z);
                Block block = loc.getBlock();
                if (!block.hasMetadata("functionalLodestone")) block.setMetadata("functionalLodestone", new FixedMetadataValue(Main.getPlugin(), true));
            } catch (ArrayIndexOutOfBoundsException e) {
                lodeStones.set(key, null);
            }
        });
    }
    public static ItemStack tempArrow() {
        ItemStack arrow = new ItemStack(Material.ARROW);
        ItemMeta meta = arrow.getItemMeta();
        assert meta != null;
        meta.setDisplayName(colorinfo + "TEMP ARROW");
        arrow.setItemMeta(meta);
        return arrow;
    }

    public static void removeTempArrow(Player p) {
        FileConfiguration config = Main.getPlugin().getConfig();
        ItemStack arrow = ItemUtils.tempArrow();
        PlayerUtils.removeTempArrows(p);
        if (!config.getBoolean(p.getName() + ".arrowholder.holdArrow")) return;
        p.getInventory().removeItem(arrow);
        ItemUtils.loadReplacedArrowItemsBack(p);
        config.set(p.getName() + ".arrowholder.holdArrow", null);
        Main.getPlugin().saveConfig();
    }

    public static void loadReplacedArrowItemsBack(Player player) {
        FileConfiguration config = Main.getPlugin().getConfig();
        if (config.contains(player.getName() + ".arrowholder.replacedItem")) {
            player.getInventory().setItem(8, config.getItemStack(player.getName() + ".arrowholder.replacedItem"));
            config.set(player.getName() + ".arrowholder.replacedItem", null);
        }
        if (config.contains(player.getName() + ".arrowholder.replacedOffhandItem")) {
            player.getInventory().setItemInOffHand(config.getItemStack(player.getName() + ".arrowholder.replacedOffhandItem"));
            config.set(player.getName() + ".arrowholder.replacedOffhandItem", null);
        }
    }
}
