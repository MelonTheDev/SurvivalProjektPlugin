package wtf.melonthedev.survivalprojektplugin.listeners.blocklisteners;

import org.bukkit.*;
import org.bukkit.Material;
import org.bukkit.block.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import wtf.melonthedev.survivalprojektplugin.Main;
import wtf.melonthedev.survivalprojektplugin.commands.LockchestCommand;
import wtf.melonthedev.survivalprojektplugin.utils.BlockUtils;
import wtf.melonthedev.survivalprojektplugin.others.Config;
import wtf.melonthedev.survivalprojektplugin.utils.PlayerUtils;

import java.util.Objects;

public class BlockPlaceListener implements Listener {

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {

        ItemStack handItem = e.getItemInHand();
        Block block = e.getBlock();

        if (block.getType() == Material.SPAWNER) {
            if (e.getPlayer().getGameMode() != GameMode.SURVIVAL) return;
            if (!(block instanceof CreatureSpawner)) return;
            CreatureSpawner spawner = (CreatureSpawner) e.getBlock().getState();
            if (!handItem.hasItemMeta() || !Objects.requireNonNull(handItem.getItemMeta()).hasLocalizedName()) return;
            String id = handItem.getItemMeta().getLocalizedName().split("-")[1];
            try {
                spawner.setSpawnedType(EntityType.valueOf(id));
                spawner.update();
            } catch (IllegalArgumentException exception) {
                PlayerUtils.sendCustomError(e.getPlayer(), "Ein INTERNER Fehler ist aufgetreten. Die Entity dieses Spawners wurde nicht gefunden.");
            }
        } else if (handItem.getType() == Material.PLAYER_HEAD) {
            if (!handItem.hasItemMeta() || !(handItem.getItemMeta() instanceof SkullMeta)) return;
            SkullMeta meta = (SkullMeta) handItem.getItemMeta();
            if (!meta.hasOwner() || !Objects.requireNonNull(meta.getOwner()).startsWith("MHF_")) return;
            if (!meta.hasLocalizedName()) return;
            if (!(block.getState() instanceof TileState)) return;
            TileState state = (TileState) block.getState();
            PersistentDataContainer container = state.getPersistentDataContainer();
            NamespacedKey key = new NamespacedKey(Main.getPlugin(), "localizedName");
            container.set(key, PersistentDataType.STRING, meta.getLocalizedName());
            state.update();
        } else if (block.getType() == Material.CHEST) {
            //CHECK IF THERE WAS ALREADY A DOUBLE CHEST
            if (isConflictingWithLockedChest(block.getRelative(0, 0, 1), block, e.getPlayer())) return;
            if (isConflictingWithLockedChest(block.getRelative(0, 0, -1), block, e.getPlayer())) return;
            if (isConflictingWithLockedChest(block.getRelative(1, 0, 0), block, e.getPlayer())) return;
            if (isConflictingWithLockedChest(block.getRelative(-1, 0, 0), block, e.getPlayer())) return;
            //CHECK IF THE EVENT MADE ONE
            Bukkit.getScheduler().runTask(Main.getPlugin(), () -> {
                if (isConflictingWithLockedChest(block.getRelative(0, 0, 1), block, e.getPlayer())) block.breakNaturally();
                if (isConflictingWithLockedChest(block.getRelative(0, 0, -1), block, e.getPlayer())) block.breakNaturally();
                if (isConflictingWithLockedChest(block.getRelative(1, 0, 0), block, e.getPlayer())) block.breakNaturally();
                if (isConflictingWithLockedChest(block.getRelative(-1, 0, 0), block, e.getPlayer())) block.breakNaturally();
            });
        } else if (handItem.getType() == Material.LODESTONE) {
            if (handItem.hasItemMeta() && Objects.requireNonNull(handItem.getItemMeta()).hasLocalizedName()) {
                ItemMeta meta = handItem.getItemMeta();
                String localizedName = meta.getLocalizedName();
                if (localizedName.equals("broken_lodestone")) {
                    block.setMetadata("brokenLodestone", new FixedMetadataValue(Main.getPlugin(), true));
                    return;
                }
            }
            block.setMetadata("functionalLodestone", new FixedMetadataValue(Main.getPlugin(), true));
            FileConfiguration blocks = Config.getCustomConfig("blocks.yml");
            blocks.set("lodestone." + block.getX() + "_" + block.getY() + "_" + block.getZ() + "_" + block.getWorld().getName(), true);
            Config.saveNewCustomConfig(blocks, "blocks.yml");
        }
    }
    public boolean isConflictingWithLockedChest(Block block, Block placedBlock, Player blockPlacer) {
        if (!(block.getState() instanceof Chest) || !BlockUtils.isLocked(block)) return false;
        Chest chest = (Chest) block.getState();
        if (!(chest.getInventory() instanceof DoubleChestInventory)) return false;
        if (!BlockUtils.hasAccess(block, blockPlacer)) {
            return true;
        }
        LockchestCommand.setContainer(placedBlock, blockPlacer, "lock");
        return false;
    }
}
