package wtf.melonthedev.survivalprojektplugin.utils;

import net.minecraft.server.v1_16_R3.BlockPosition;
import net.minecraft.server.v1_16_R3.PacketPlayOutBlockBreakAnimation;
import net.minecraft.server.v1_16_R3.PlayerConnection;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import wtf.melonthedev.survivalprojektplugin.Main;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class BlockUtils {

    public static boolean isLocked(Block container) {
        if (!(container.getState() instanceof TileState)) return false;
        TileState state = (TileState) container.getState();
        PersistentDataContainer dataContainer = state.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(Main.getPlugin(), "locked-chests");
        return dataContainer.has(key, PersistentDataType.STRING);
    }

    public static boolean hasAccess(Block container, Player player) {
        if (!isLocked(container)) return false;
        PersistentDataContainer dataContainer = ((TileState) container.getState()).getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(Main.getPlugin(), "locked-chests");
        String[] value = Objects.requireNonNull(dataContainer.get(key, PersistentDataType.STRING)).split(",");
        List<String> permissionedPlayers = Arrays.asList(value);
        return permissionedPlayers.contains(player.getUniqueId().toString());
    }

    public static void addChestAccess(Block container, OfflinePlayer player) {
        if (!isLocked(container)) return;
        if (!(container.getState() instanceof TileState)) return;
        TileState state = (TileState) container.getState();
        PersistentDataContainer dataContainer = state.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(Main.getPlugin(), "locked-chests");
        String[] value = Objects.requireNonNull(dataContainer.get(key, PersistentDataType.STRING)).split(",");
        List<String> permissionedPlayers = Arrays.asList(value);
        if (permissionedPlayers.contains(player.getUniqueId().toString())) return;
        String newValue = Objects.requireNonNull(dataContainer.get(key, PersistentDataType.STRING)) + "," + player.getUniqueId();
        dataContainer.set(key, PersistentDataType.STRING, newValue);
        state.update();
    }

    public static void removeChestAccess(Block container, OfflinePlayer player) {
        if (!isLocked(container)) return;
        if (!(container.getState() instanceof TileState)) return;
        TileState state = (TileState) container.getState();
        PersistentDataContainer dataContainer = state.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(Main.getPlugin(), "locked-chests");
        String oldValue = Objects.requireNonNull(dataContainer.get(key, PersistentDataType.STRING));
        String value = oldValue.replace("," + player.getUniqueId(), "");
        dataContainer.set(key, PersistentDataType.STRING, value);
        state.update();
    }

    public static String getContainerStateAsStirng(BlockState containerState) {
        if (containerState instanceof Chest) return "Chest";
        else if (containerState instanceof Barrel) return "Barrel";
        else if (containerState instanceof Hopper) return "Hopper";
        else if (containerState instanceof Dropper) return "Dropper";
        else if (containerState instanceof Dispenser) return "Dispenser";
        else if (containerState instanceof BrewingStand) return "BrewingStand";
        else if (containerState instanceof BlastFurnace) return "BlastFurnace";
        else if (containerState instanceof Smoker) return "Smoker";
        else if (containerState instanceof Furnace) return "Furnace";
        else if (containerState instanceof ShulkerBox) return "ShulkerBox";
        else return "Container";
    }

    public static void showShulkerPreview(ItemStack item, Player p) {
        if (!item.hasItemMeta()) return;
        if (!(item.getItemMeta() instanceof BlockStateMeta)) return;
        BlockStateMeta bsMeta = (BlockStateMeta) item.getItemMeta();
        if (!(bsMeta.getBlockState() instanceof ShulkerBox)) return;
        ShulkerBox shulker = (ShulkerBox) bsMeta.getBlockState();
        String title = ChatColor.AQUA + "Shukler Preview: Shulker Box";
        if (item.getItemMeta().hasDisplayName()) title = ChatColor.AQUA + "Shukler Preview: " + item.getItemMeta().getDisplayName();
        Inventory inv = Bukkit.createInventory(null, 27, title);
        inv.setContents(shulker.getInventory().getContents());
        p.openInventory(inv);
    }

    public static void runAnimation(Block animatedBlock, Block breaker, int speed) {
        new BukkitRunnable() {
            final Random random = new Random();
            int status = 0;
            final int id = random.nextInt(2000);
            @Override
            public void run() {
                if (animatedBlock.getType() == Material.AIR || breaker.getType() == Material.AIR || !breaker.isBlockPowered()) {
                    status = 10;
                    sendPacket(animatedBlock, status, id);
                    cancel();
                    return;
                }
                if (status == 7) {
                    status = 0;
                    animatedBlock.breakNaturally(new ItemStack(Material.DIAMOND_PICKAXE));
                    sendPacket(animatedBlock, 0, id);
                    cancel();
                    return;
                }
                sendPacket(animatedBlock, status, id);
                status++;
            }
        }.runTaskTimer(Main.getPlugin(), 1, speed);
    }

    public static void sendPacket(Block animatedBlock, int status, int id)  {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getLocation().distance(animatedBlock.getLocation()) > 200) continue;
            PlayerConnection connection = ((CraftPlayer) p).getHandle().playerConnection;
            connection.sendPacket(new PacketPlayOutBlockBreakAnimation(id, new BlockPosition(animatedBlock.getX(), animatedBlock.getY(), animatedBlock.getZ()), status));
        }
    }

}
