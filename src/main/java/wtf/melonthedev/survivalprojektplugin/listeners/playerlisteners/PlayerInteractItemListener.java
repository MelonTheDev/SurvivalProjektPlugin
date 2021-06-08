package wtf.melonthedev.survivalprojektplugin.listeners.playerlisteners;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Container;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import wtf.melonthedev.survivalprojektplugin.Main;
import wtf.melonthedev.survivalprojektplugin.utils.BlockUtils;
import wtf.melonthedev.survivalprojektplugin.utils.EntityUtils;
import wtf.melonthedev.survivalprojektplugin.utils.ItemUtils;
import wtf.melonthedev.survivalprojektplugin.utils.PlayerUtils;

import java.util.Objects;

import static wtf.melonthedev.survivalprojektplugin.Main.colorerror;
import static wtf.melonthedev.survivalprojektplugin.Main.serverprefix;

public class PlayerInteractItemListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        //STANDARD
        if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (e.getItem() == null) return;
        if (e.getHand() == null) return;

        FileConfiguration config = Main.getPlugin().getConfig();
        Player p = e.getPlayer();
        PlayerInventory playerInventory = p.getInventory();
        ItemStack item = e.getItem();
        Action action = e.getAction();
        EquipmentSlot hand = e.getHand();

        //PARTS
        if (item.getType() == Material.FIRE_CHARGE) {
            if (p.getGameMode() == GameMode.CREATIVE) return;
            playerInventory.setItem(hand, new ItemStack(Material.FIRE_CHARGE, e.getItem().getAmount() - 1));
            EntityUtils.sendFireBall(p);
        } else if (item.getType() == Material.FIREWORK_ROCKET) {
            ItemStack chestplate = playerInventory.getChestplate();
            if (chestplate == null) return;
            if (chestplate.getType() != Material.ELYTRA) return;
            ItemMeta im = chestplate.getItemMeta();
            assert im != null;
            if (!im.getDisplayName().equals("TEMP")) return;
            e.setCancelled(true);
            p.sendMessage(colorerror + serverprefix + "Du kannst mit der Spawnelytra leider nicht fliegen.");
        } else if (item.getType() == Material.END_ROD) {
            if (action == Action.RIGHT_CLICK_BLOCK) return;
            p.getWorld().spawnParticle(Particle.CLOUD, p.getLocation(), 50, 0, 0, 0, 3);
        } else if (item.getType() == Material.TRIDENT) {
            if (!config.getBoolean(p.getName() + ".isAfk")) return;
            e.setCancelled(true);
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(colorerror + "Du bist AFK und kannst nicht tridenden"));
        } else if (item.getType().toString().contains("SHULKER_BOX")) {
            if (!p.isSneaking()) return;
            if (action == Action.RIGHT_CLICK_BLOCK) return;
            BlockUtils.showShulkerPreview(e.getItem(), p);
        } else if (item.getType() == Material.BOW) {
            ItemStack arrow = ItemUtils.tempArrow();
            PlayerUtils.removeTempArrows(p);
            if (!config.getBoolean(p.getName() + ".arrowholder.hasArrow")) return; //HAS ARROW
            if (playerInventory.contains(Material.ARROW)) return; //ALREADY ARROW IN INV
            if (!item.containsEnchantment(Enchantment.ARROW_INFINITE)) return; //ONLY INFINITY BOW
            if (e.getClickedBlock() != null) {
                if (e.getClickedBlock().getState() instanceof Container) return;
            }
            if (playerInventory.getItem(8) != null) config.set(p.getName() + ".arrowholder.replacedItem", playerInventory.getItem(8)); //SAVE ITEM IF THERE WAS ONE
            if (playerInventory.getItemInOffHand().getType() == Material.SHIELD) { //SAVE SHIELD IF THERE WAS ONE
                config.set(p.getName() + ".arrowholder.replacedOffhandItem", playerInventory.getItemInOffHand());
                playerInventory.setItemInOffHand(new ItemStack(Material.AIR));
            }
            playerInventory.setItem(8, arrow); //SET ARROW
            config.set(p.getName() + ".arrowholder.holdArrow", true);
            Main.getPlugin().saveConfig();
        }
    }
}
