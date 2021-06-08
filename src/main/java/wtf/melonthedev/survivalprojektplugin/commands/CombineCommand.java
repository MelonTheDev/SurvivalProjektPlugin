package wtf.melonthedev.survivalprojektplugin.commands;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.EnchantingTable;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import wtf.melonthedev.survivalprojektplugin.utils.CommandUtils;
import wtf.melonthedev.survivalprojektplugin.utils.PlayerUtils;

import java.util.*;

import static wtf.melonthedev.survivalprojektplugin.Main.*;

public class CombineCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        //IF-CAUSES
        if (CommandUtils.isDisabled(sender, "combine")) return true;
        if (!PlayerUtils.isPlayer(sender)) return true;
        Player p = (Player) sender;
        if (args.length != 0) {
            p.sendMessage(colorerror + serverprefix + "Syntaxerror: /offhandenchant <null>");
            return true;
        }
        //if (Objects.requireNonNull(p.getEquipment()).getItemInMainHand().getType() != Material.ENCHANTED_BOOK) {
        //    p.sendMessage(colorerror + serverprefix + "Du musst ein enchantetes Buch in der Hand halten.");
        //    return true;
        //}
        ItemStack mainHand = p.getInventory().getItemInMainHand();
        ItemStack offhand = p.getInventory().getItemInOffHand();
        if (offhand.getType() == Material.AIR || mainHand.getType() == Material.AIR) {
            p.sendMessage(colorerror + serverprefix + "Du musst ein Item in deine Offhand legen.");
            return true;
        }
        if (p.getGameMode() != GameMode.CREATIVE) {
            if (p.getLevel() <= 14) {
                p.sendMessage(colorerror + serverprefix + "Du benötigst mindestens 15 Experience level.");
                return true;
            }
            if (!ShopCommand.currencyCounter(3, Material.IRON_INGOT, p)) {
                return true;
            }
        }
        if (mainHand.getType() == Material.ENCHANTED_BOOK) {
            EnchantmentStorageMeta sm = (EnchantmentStorageMeta) mainHand.getItemMeta();
            assert sm != null;
            for (Map.Entry<Enchantment, Integer> entry : sm.getStoredEnchants().entrySet()) {
                enchant(entry, offhand, p);
            }

        } else {
            if (offhand.getType() != mainHand.getType()) return true;
            if (mainHand.hasItemMeta()) {
                ItemMeta meta = mainHand.getItemMeta();
                assert meta != null;
                for (Map.Entry<Enchantment, Integer> entry : meta.getEnchants().entrySet()) {
                    enchant(entry, offhand, p);
                }
            }
        }
        p.sendMessage(colorinfo + serverprefix + "Du hast das Item " + offhand.getType().toString().toLowerCase().replace('_', ' ') + " in deiner Offhand kombiniert.");
        p.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
        if (offhand instanceof Damageable && mainHand instanceof Damageable)
            ((Damageable) offhand).setDamage(((Damageable) offhand).getDamage() + ((Damageable) mainHand).getDamage());
        p.getInventory().setItemInOffHand(offhand);
        if (p.getGameMode() != GameMode.CREATIVE && p.getLevel() >= 15) p.setLevel(p.getLevel() - 15);
        p.updateInventory();
        return true;
    }

    public void enchant(Map.Entry<Enchantment, Integer> entry, ItemStack offhand, Player p) {
        Enchantment enchantment = entry.getKey();
        Integer level = entry.getValue();
        if (offhand.containsEnchantment(enchantment)) {
            int offhandlevel = offhand.getEnchantmentLevel(enchantment);
            if (offhandlevel == level) level++;
            if (offhandlevel > level) return;
        }
        try {
            offhand.addEnchantment(enchantment, level);
        } catch (IllegalArgumentException e) {
            p.sendMessage(colorerror + serverprefix + "Dieses Enchantment kannst du nicht auf dieses Item machen. Falls weitere enchantments auf dem Buch sind, werden diese noch hinzugefügt.");
        }
    }
}
