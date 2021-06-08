package wtf.melonthedev.survivalprojektplugin.commands;

import net.minecraft.server.v1_16_R3.*;
import net.minecraft.server.v1_16_R3.Entity;
import org.bukkit.*;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import wtf.melonthedev.survivalprojektplugin.Main;
import wtf.melonthedev.survivalprojektplugin.gui.InterfaceApi;
import wtf.melonthedev.survivalprojektplugin.others.CustomEnchantments;
import wtf.melonthedev.survivalprojektplugin.gui.InterfaceType;
import wtf.melonthedev.survivalprojektplugin.utils.*;
import wtf.melonthedev.survivalprojektplugin.others.NPC;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class TestCommand implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (CommandUtils.isDisabled(sender, "test")) return true;
        if (!PlayerUtils.isPlayer(sender)) return true;
        Player p = (Player) sender;
        if (!PlayerUtils.checkOP(p)) return true;
        CraftPlayer cpl = (CraftPlayer) p;
        Entity e = cpl.getHandle();
        e.setPose(EntityPose.SLEEPING);
        System.out.println(e.getPose());
        System.out.println("lol");

        if (args.length == 1) {
            ItemStack item = new ItemStack(Material.DIAMOND_PICKAXE);
            item.addUnsafeEnchantment(CustomEnchantments.HOLY_SPAWNER, 1);
            ItemMeta meta = item.getItemMeta();
            assert meta != null;
            meta.setLore(Collections.singletonList(ChatColor.GRAY + "Holy Spawner I"));
            meta.addEnchant(CustomEnchantments.HOLY_SPAWNER, 1, true);
            item.setItemMeta(meta);
            p.getInventory().addItem(item);
            PlayerUtils.sendCustomError(p, "Du musst ein Spieler sein.");
        } else if (args.length == 2) {
            Location loc = p.getLocation();
            double x = loc.getBlockX();
            double y = loc.getBlockY();
            double z = loc.getBlockZ();
            BlockPosition pos = new BlockPosition(x, y, z);
            BlockPosition pos2 = new BlockPosition(x, y, z);
            EntityPlayer entityPlayerOben = NPC.createNpc(p, p.getName(), p.getName());
            EntityPlayer entityPlayerUnten = NPC.createNpc(p, p.getName(), p.getName());
            p.getWorld().getBlockAt(loc).setType(Material.BLACK_BED);
            //p.getWorld().getBlockAt(loc).setType(Material.BLACK_BED);
            entityPlayerOben.entitySleep(pos);
            //entityPlayerUnten.entitySleep(pos2);
            Bukkit.getScheduler().runTaskTimer(Main.getPlugin(), () -> {
                entityPlayerUnten.setPose(EntityPose.CROUCHING);
                entityPlayerOben.setPose(EntityPose.STANDING);
                for (Player player : Bukkit.getOnlinePlayers()) {
                    NPC.addJoinPacket(p);
                    PlayerConnection playerConnection = ((CraftPlayer)player).getHandle().playerConnection;
                    playerConnection.sendPacket(new PacketPlayOutEntityMetadata(entityPlayerUnten.getId(), entityPlayerUnten.getDataWatcher(), false));
                    playerConnection.sendPacket(new PacketPlayOutEntityMetadata(entityPlayerOben.getId(), entityPlayerOben.getDataWatcher(), false));
                }
                Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> {
                    entityPlayerUnten.setPose(EntityPose.STANDING);
                    entityPlayerOben.setPose(EntityPose.CROUCHING);
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        NPC.addJoinPacket(p);
                        PlayerConnection playerConnection = ((CraftPlayer)player).getHandle().playerConnection;
                        playerConnection.sendPacket(new PacketPlayOutEntityMetadata(entityPlayerUnten.getId(), entityPlayerUnten.getDataWatcher(), false));
                        playerConnection.sendPacket(new PacketPlayOutEntityMetadata(entityPlayerOben.getId(), entityPlayerOben.getDataWatcher(), false));
                    }
                }, 21);
            }, 0, 42);
            /*
            p.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
            for (ItemStack armorContent : p.getInventory().getArmorContents()) {
                try {
                    armorContent.setType(Material.AIR);
                } catch (NullPointerException ignored) {}

            }
            Bukkit.getScheduler().runTask(Main.getPlugin(), () -> {
                p.getWorld().getBlockAt(loc).setType(Material.AIR);
                for (Player player : Bukkit.getOnlinePlayers()) {
                    PlayerConnection playerConnection = ((CraftPlayer)player).getHandle().playerConnection;
                    playerConnection.sendPacket(new PacketPlayOutEntityMetadata(entityPlayerUnten.getId(), entityPlayerUnten.getDataWatcher(), false));
                    playerConnection.sendPacket(new PacketPlayOutEntityMetadata(entityPlayerOben.getId(), entityPlayerOben.getDataWatcher(), false));
                }
            });*/



        } else if (args.length == 3) {


            ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();
            assert meta != null;
            meta.addStoredEnchant(CustomEnchantments.HOLY_SPAWNER, 1, true);
            meta.addEnchant(CustomEnchantments.HOLY_SPAWNER, 1, true);
            book.setItemMeta(meta);
            book.addUnsafeEnchantment(CustomEnchantments.HOLY_SPAWNER, 1);
            p.getInventory().addItem(book);
        } else if (args.length == 4) {
            for (Player pl : p.getWorld().getPlayers()) pl.sendBlockChange(pl.getWorld().getBlockAt(205, 73, 3).getLocation(), Bukkit.createBlockData(Material.EMERALD_BLOCK));
            /*
            JFrame mainframe = new JFrame("Ah lol ein interface");
            mainframe.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            mainframe.setVisible(true);
            mainframe.setLocationRelativeTo(null);
            mainframe.setSize(300, 150);
            mainframe.setLayout(null);
            mainframe.setResizable(false);
            JLabel text = new JLabel("HELLO WORLD");
            text.setBounds(90, 15, 150, 10);
            mainframe.getContentPane().add(text);

            Location loc = p.getLocation();
            System.out.println(loc);
            p.sendMessage("kaka befindet sich bei " + loc.getBlockX() + loc);*/

        } else if (args.length == 5) {
            Sheep sheep = (Sheep) p.getWorld().spawnEntity(p.getLocation(), EntityType.SHEEP);
            Slime slime = (Slime) p.getWorld().spawnEntity(p.getLocation(), EntityType.SLIME);
            slime.setSize(Integer.parseInt(args[0]));
            sheep.setCustomNameVisible(true);
            slime.setRemoveWhenFarAway(false);
            slime.setCollidable(false);
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (sheep.isDead()) cancel();
                    DyeColor[] values = DyeColor.values();
                    ChatColor[] chatcolorValues = ChatColor.values();
                    int randrom = new Random().nextInt(values.length);
                    int chatcolorRandrom = new Random().nextInt(chatcolorValues.length);
                    if (chatcolorValues[chatcolorRandrom] == ChatColor.MAGIC || chatcolorValues[chatcolorRandrom] == ChatColor.BOLD || chatcolorValues[chatcolorRandrom] == ChatColor.STRIKETHROUGH) chatcolorValues[chatcolorRandrom] = ChatColor.AQUA;
                    sheep.setCustomName(chatcolorValues[chatcolorRandrom] + "Disco Sheep");
                    sheep.setColor(values[randrom]);
                }
            }.runTaskTimer(Main.getPlugin(), 0, 5);
        } else if (args.length == 6) {
            List<org.bukkit.entity.Entity> nearbyEntitys = p.getNearbyEntities(100, 100, 100);
            for (org.bukkit.entity.Entity entity : nearbyEntitys) {
                if (p.hasLineOfSight(entity)) {
                    entity.remove();
                }
            }

        } else if (args.length == 7) {
             LivingEntity entity = (LivingEntity) p.getWorld().spawnEntity(p.getLocation(), Objects.requireNonNull(EntityType.fromName(args[1])));
             Player target = Bukkit.getPlayer(args[0]);
             if (target == null) return true;
            followPlayer(target, entity, 1.75);
        } else if (args.length == 8) {
            Merchant merchant = Bukkit.createMerchant("LOL");
            p.openMerchant(merchant, true);
        } else if (args.length == 9) {
            InterfaceApi.createInterface(27, new ItemStack(Material.STRING), InterfaceType.NORMAL_ARROWLEFT);
        } else if (args.length == 10) {
            ((CraftPlayer) p).setPlayerListHeaderFooter("LOOOLLLL", "FOOOTER");
        }
        return false;
    }
    public void followPlayer(Player player, LivingEntity entity, double d) {
        final LivingEntity e = entity;
        final Player p = player;
        final float f = (float) d;
        Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(), new Runnable() {
            @Override
            public void run() {
                ((EntityInsentient) ((CraftEntity) e).getHandle()).getNavigation().a(p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ(), f);
            }
        }, 0,  10);
    }
}