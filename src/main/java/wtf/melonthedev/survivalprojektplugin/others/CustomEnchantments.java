package wtf.melonthedev.survivalprojektplugin.others;

import org.bukkit.enchantments.Enchantment;
import wtf.melonthedev.survivalprojektplugin.utils.EnchantmentWrapper;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Collectors;

public class CustomEnchantments {

    public static final Enchantment HOLY_SPAWNER = new EnchantmentWrapper("holy_spawner", "Holy Spawner", 1);

    public static void register() {
        boolean registered = Arrays.stream(Enchantment.values()).collect(Collectors.toList()).contains(HOLY_SPAWNER);
        if (!registered) {
            registerEnchantment(HOLY_SPAWNER);
        }
    }

    public static void registerEnchantment(Enchantment enchant) {
        try {
            Field f = Enchantment.class.getDeclaredField("acceptingNew");
            f.setAccessible(true);
            f.set(null, true);
            Enchantment.registerEnchantment(enchant);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
