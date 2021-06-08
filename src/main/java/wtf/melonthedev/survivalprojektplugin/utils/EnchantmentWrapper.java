package wtf.melonthedev.survivalprojektplugin.utils;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;

public class EnchantmentWrapper extends Enchantment {
    private final String name;
    private final int maxLvl;

    public EnchantmentWrapper(String namespace, String name, int lvl) {
        super(NamespacedKey.minecraft(namespace));
        this.name = name;
        this.maxLvl = lvl;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getMaxLevel() {
        return maxLvl;
    }

    @Override
    public int getStartLevel() {
        return 1;
    }

    @Override
    public EnchantmentTarget getItemTarget() {
        return EnchantmentTarget.TOOL;
    }

    @Override
    public boolean isTreasure() {
        return false;
    }

    @Override
    public boolean isCursed() {
        return false;
    }

    @Override
    public boolean conflictsWith(Enchantment ench) {
        return ench == Enchantment.SILK_TOUCH;
    }

    @Override
    public boolean canEnchantItem(ItemStack item) {
        if (item.getType() == Material.DIAMOND_PICKAXE) return true;
        return item.getType() == Material.NETHERITE_PICKAXE;
    }
}
