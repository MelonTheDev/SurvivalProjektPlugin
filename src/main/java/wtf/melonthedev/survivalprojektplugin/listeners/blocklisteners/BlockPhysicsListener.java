package wtf.melonthedev.survivalprojektplugin.listeners.blocklisteners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Container;
import org.bukkit.block.TileState;
import org.bukkit.block.data.Directional;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import wtf.melonthedev.survivalprojektplugin.Main;
import wtf.melonthedev.survivalprojektplugin.utils.BlockUtils;

public class BlockPhysicsListener implements Listener {

    @EventHandler
    public void onBlockChange(BlockPhysicsEvent e) {
        Block block = e.getBlock();
        Location loc = block.getLocation();

        if (block.getType() == Material.STONECUTTER) {
            //CUTTER
            Block cuttedBlock = loc.add(0, 1, 0).getBlock();
            if (cuttedBlock.getType() == Material.AIR) return;
            if (BlockUtils.isLocked(cuttedBlock)) return;
            checkPowerAndRun(block, cuttedBlock);
        } else if (block.getType() == Material.END_ROD) {
            //BOHRER
            if (!(block.getBlockData() instanceof Directional)) return;
            Directional endrod = (Directional) block.getBlockData();
            Block cuttedBlock = block.getRelative(endrod.getFacing());
            if (BlockUtils.isLocked(cuttedBlock)) return;
            checkPowerAndRun(block, cuttedBlock);
        } else if (block.getType() == Material.SAND) {
            if (block.getRelative(BlockFace.NORTH).getType() == Material.LAVA || block.getRelative(BlockFace.SOUTH).getType() == Material.LAVA || block.getRelative(BlockFace.EAST).getType() == Material.LAVA || block.getRelative(BlockFace.WEST).getType() == Material.LAVA || block.getRelative(BlockFace.UP).getType() == Material.LAVA || block.getRelative(BlockFace.DOWN).getType() == Material.LAVA) {
                block.setType(Material.GLASS);
                block.getWorld().spawnParticle(Particle.ASH, loc, 10, 0.5, 0.5, 0.5, 5);
            }
        }
    }

    private void checkPowerAndRun(Block block, Block cuttedBlock) {
        if (!block.isBlockPowered() && !block.isBlockIndirectlyPowered() && !block.isBlockFacePowered(BlockFace.DOWN) && !block.isBlockFacePowered(BlockFace.UP) && !block.isBlockFacePowered(BlockFace.NORTH) && !block.isBlockFacePowered(BlockFace.SOUTH) && !block.isBlockFacePowered(BlockFace.EAST) && !block.isBlockFacePowered(BlockFace.WEST)) return;
        int blockPower = block.getBlockPower();
        if (blockPower >= 17) BlockUtils.runAnimation(cuttedBlock, block, 20);
        else if (blockPower >= 14) BlockUtils.runAnimation(cuttedBlock, block, 30);
        else if (blockPower >= 10) BlockUtils.runAnimation(cuttedBlock, block, 40);
        else if (blockPower >= 7) BlockUtils.runAnimation(cuttedBlock, block, 50);
        else if (blockPower >= 2) BlockUtils.runAnimation(cuttedBlock, block, 60);
        else BlockUtils.runAnimation(cuttedBlock, block, 70);
    }
}
