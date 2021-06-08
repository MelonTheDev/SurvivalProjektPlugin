package wtf.melonthedev.survivalprojektplugin.listeners.blocklisteners;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.TileState;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import wtf.melonthedev.survivalprojektplugin.Main;
import wtf.melonthedev.survivalprojektplugin.others.CustomEnchantments;
import wtf.melonthedev.survivalprojektplugin.others.Config;
import wtf.melonthedev.survivalprojektplugin.utils.EntityUtils;

import java.util.ArrayList;
import java.util.List;

public class BlockBreakListener implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        FileConfiguration settings = Config.getCustomConfig("settings.yml");


        //HOLY SPAWNER ENCHANTMENT
        if (e.getBlock().getType() == Material.SPAWNER) {
            if (p.getInventory().getItemInMainHand().getType() == Material.AIR) return;
            if (!p.getInventory().getItemInMainHand().hasItemMeta()) return;
            ItemMeta meta = p.getInventory().getItemInMainHand().getItemMeta();
            assert meta != null;
            if (!meta.hasEnchant(CustomEnchantments.HOLY_SPAWNER)) return;
            if (p.getGameMode() != GameMode.SURVIVAL) return;
            CreatureSpawner spawner = (CreatureSpawner) e.getBlock().getState();
            ItemStack spawnerItem = new ItemStack(Material.SPAWNER);
            ItemMeta spawnerItemMeta = spawnerItem.getItemMeta();
            assert spawnerItemMeta != null;
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.AQUA + "SpawnType: " + ChatColor.DARK_AQUA + spawner.getSpawnedType().name());
            spawnerItemMeta.setLore(lore);
            spawnerItemMeta.setDisplayName(ChatColor.GOLD + spawner.getSpawnedType().name().replaceAll("_", " ") + " Spawner");
            spawnerItemMeta.setLocalizedName("spawner-" + spawner.getSpawnedType().name());
            spawnerItem.setItemMeta(spawnerItemMeta);
            p.getWorld().dropItem(e.getBlock().getLocation(), spawnerItem);
            e.setExpToDrop(0);
        } else if (e.getBlock().getType() == Material.PLAYER_HEAD) {
            if (!(e.getBlock().getState() instanceof TileState)) return;
            PersistentDataContainer container = ((TileState) e.getBlock().getState()).getPersistentDataContainer();
            NamespacedKey key = new NamespacedKey(Main.getPlugin(), "localizedName");
            if (container.has(key, PersistentDataType.STRING) && e.getPlayer().getGameMode() == GameMode.SURVIVAL) {
                e.setDropItems(false);
                String localizedName = container.get(key, PersistentDataType.STRING);
                assert localizedName != null;
                try {
                    String owner = localizedName.split("-")[1].split("_")[1];
                    String mcTag = localizedName.split("-")[0];
                    EntityUtils.dropMobHead(e.getBlock().getLocation(), owner, mcTag);
                } catch (ArrayIndexOutOfBoundsException ignored) {}
            }
        } else if (e.getBlock().getType() == Material.LODESTONE) {
            Block block = e.getBlock();
            if (block.hasMetadata("brokenLodestone")) {
                ItemStack item = new ItemStack(Material.LODESTONE);
                ItemMeta meta = item.getItemMeta();
                assert meta != null;
                meta.setDisplayName(ChatColor.RESET + "Broken Lodestone");
                meta.setLocalizedName("broken_lodestone");
                item.setItemMeta(meta);
                e.setDropItems(false);
                e.getBlock().getWorld().dropItem(e.getBlock().getLocation(), item);
            } else {
                FileConfiguration blocks = Config.getCustomConfig("blocks.yml");
                String coords = "lodestone." + block.getX() + "_" + block.getY() + "_" + block.getZ();
                blocks.set(coords, null);
                Config.saveNewCustomConfig(blocks, "blocks.yml");
            }
        }
    }
}
