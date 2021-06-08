package wtf.melonthedev.survivalprojektplugin.listeners.entitylisteners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import wtf.melonthedev.survivalprojektplugin.others.CustomEnchantments;
import wtf.melonthedev.survivalprojektplugin.Main;
import wtf.melonthedev.survivalprojektplugin.others.Config;
import wtf.melonthedev.survivalprojektplugin.utils.EntityUtils;
import wtf.melonthedev.survivalprojektplugin.utils.PetUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class EntityDeathListener implements Listener {

    FileConfiguration config = Main.getPlugin().getConfig();

    @EventHandler
    public void onEntityKill(EntityDeathEvent e) {
        //WITHER
        if (e.getEntity().getType() == EntityType.WITHER && e.getEntity().getKiller() != null) {
            Player killer = e.getEntity().getKiller();
            boolean dropHolySpawner = ThreadLocalRandom.current().nextBoolean();
            if (dropHolySpawner) {
                ItemStack holySpawnerPicke = new ItemStack(Material.DIAMOND_PICKAXE);
                ItemMeta meta = holySpawnerPicke.getItemMeta();
                assert meta != null;
                meta.setLore(Collections.singletonList(ChatColor.GRAY + "Holy Spawner"));
                holySpawnerPicke.setItemMeta(meta);
                holySpawnerPicke.addUnsafeEnchantment(CustomEnchantments.HOLY_SPAWNER, 1);
                Damageable damageable = (Damageable) holySpawnerPicke.getItemMeta();
                damageable.setDamage(1061);
                holySpawnerPicke.setItemMeta((ItemMeta) damageable);
                killer.getWorld().dropItem(e.getEntity().getLocation(), holySpawnerPicke);
            }
        }
        //PETS
        Bukkit.getScheduler().runTask(Main.getPlugin(), () -> Bukkit.getOnlinePlayers().forEach(player -> {
            if (config.getBoolean(player.getName() + ".pet.isSpawned")) {
                if (config.getString(player.getName() + ".pet.uuid") != null) {
                    if (e.getEntity().getUniqueId().toString().equals(config.getString(player.getName() + ".pet.uuid"))) {
                        PetUtils.despawnPet(player);
                        e.getDrops().clear();
                        e.setDroppedExp(0);
                    }
                }
            }
        }));
        Player p = e.getEntity().getKiller();
        if (p == null) return;
        FileConfiguration settings = Config.getCustomConfig("settings.yml");

        //HEADS
        if (e.getEntity().getKiller() == null) return;
        if (e.getEntity() instanceof Player) return;
        if (!config.getBoolean(e.getEntity().getKiller().getName() + ".dropMobHeads")) return;
        int randromDropPosition = (int) (Math.random() * 100);
        int dropChance = 10;
        if (e.getEntity().getKiller().getInventory().getItemInMainHand().containsEnchantment(Enchantment.LOOT_BONUS_MOBS)) {
            switch (e.getEntity().getKiller().getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS)) {
                case 1:
                    dropChance = 20;
                    break;
                case 2:
                    dropChance = 30;
                    break;
                case 3:
                    dropChance = 40;
                    break;
            }
        }

        if (randromDropPosition > dropChance)
            return;
        switch (e.getEntityType()) {
            case BLAZE:
                EntityUtils.dropMobHead(e.getEntity().getLocation(), "Blaze", "BLAZE");
                break;
            case CAVE_SPIDER:
                EntityUtils.dropMobHead(e.getEntity().getLocation(), "CaveSpider", "CAVE_SPIDER");
                break;
            case CHICKEN:
                EntityUtils.dropMobHead(e.getEntity().getLocation(), "Chicken", "CHICKEN");
                break;
            case COW:
                EntityUtils.dropMobHead(e.getEntity().getLocation(), "Cow", "COW");
                break;
            case CREEPER:
                EntityUtils.dropMobHead(e.getEntity().getLocation(), "Creeper", "CREEPER");
                break;
            case ENDERMAN:
                EntityUtils.dropMobHead(e.getEntity().getLocation(), "Enderman", "ENDERMAN");
                break;
            case GHAST:
                EntityUtils.dropMobHead(e.getEntity().getLocation(), "Ghast", "GHAST");
                break;
            case IRON_GOLEM:
                EntityUtils.dropMobHead(e.getEntity().getLocation(), "Golem", "IRON_GOLEM");
                break;
            case MAGMA_CUBE:
                EntityUtils.dropMobHead(e.getEntity().getLocation(), "LavaSlime", "MAGMA_CUBE");
                break;
            case MUSHROOM_COW:
                EntityUtils.dropMobHead(e.getEntity().getLocation(), "MushroomCow", "MUSHROOM_COW");
                break;
            case OCELOT:
                EntityUtils.dropMobHead(e.getEntity().getLocation(), "Ocelot", "OCELOT");
                break;
            case PIG:
                EntityUtils.dropMobHead(e.getEntity().getLocation(), "Pig", "PIG");
                break;
            case ZOMBIFIED_PIGLIN:
                EntityUtils.dropMobHead(e.getEntity().getLocation(), "PigZombie", "ZOMBIFIED_PIGLIN");
                break;
            case SHEEP:
                EntityUtils.dropMobHead(e.getEntity().getLocation(), "Sheep", "SHEEP");
                break;
            case SKELETON:
                EntityUtils.dropMobHead(e.getEntity().getLocation(), "Skeleton", "SKELETON");
                break;
            case SLIME:
                EntityUtils.dropMobHead(e.getEntity().getLocation(), "Slime", "SLIME");
                break;
            case SPIDER:
                EntityUtils.dropMobHead(e.getEntity().getLocation(), "Spider", "SPIDER");
                break;
            case SQUID:
                EntityUtils.dropMobHead(e.getEntity().getLocation(), "Squid", "SQUID");
                break;
            case VILLAGER:
                EntityUtils.dropMobHead(e.getEntity().getLocation(), "Villager", "VILLAGER");
                break;
            case WITHER_SKELETON:
                EntityUtils.dropMobHead(e.getEntity().getLocation(), "WSkeleton", "WITHER_SKELETON");
                break;
            case ZOMBIE:
                EntityUtils.dropMobHead(e.getEntity().getLocation(), "Zombie", "ZOMBIE");
                break;
        }
    }
}
