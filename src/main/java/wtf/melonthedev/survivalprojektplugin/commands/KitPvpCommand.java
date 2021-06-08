package wtf.melonthedev.survivalprojektplugin.commands;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.RenderType;
import org.bukkit.scoreboard.Scoreboard;
import wtf.melonthedev.survivalprojektplugin.Main;
import wtf.melonthedev.survivalprojektplugin.gui.InterfaceUtils;
import wtf.melonthedev.survivalprojektplugin.gui.ItemStacks;
import wtf.melonthedev.survivalprojektplugin.others.kitpvp.Kits;
import wtf.melonthedev.survivalprojektplugin.utils.*;

import java.util.*;

import static wtf.melonthedev.survivalprojektplugin.Main.*;

public class KitPvpCommand implements CommandExecutor, Listener, TabCompleter {

    static FileConfiguration config = Main.getPlugin().getConfig();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (CommandUtils.isDisabled(sender, "kitpvp")) return true;
        if (!PlayerUtils.isPlayer(sender)) return true;
        Player p = (Player) sender;

        //Main menu
        if (args.length == 0) {
            openMainMenu(p);
            return true;
        }
        if (args.length <= 1) {
            PlayerUtils.sendSyntaxError(p, "/kitpvp");
            return true;
        }

        if (!PlayerUtils.checkOP(p)) return true;
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            PlayerUtils.sendCustomError(p, "Dieser spieler ist nicht online.");
            return true;
        }
        //ADMIN COMMANDS
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("addPlayer")) {
                enableKitPvp(target);
                p.sendMessage(colorinfo + serverprefix + "Du hast den Spieler " + target.getName() + " zu der PVP Liste hinzugefügt.");
            } else if (args[0].equalsIgnoreCase("removePlayer")) {
                disableKitPvp(target);
                p.sendMessage(colorinfo + serverprefix + "Du hast den Spieler " + target.getName() + " von der PVP Liste entfernt.");
            } else if (args[0].equalsIgnoreCase("setKit")) {
                openKitSelector(p, target);
            } else if (args[0].equalsIgnoreCase("resetKit")) {
                enableKitPvp(target);
                Kits.giveKit(target);
            } else if (args[0].equalsIgnoreCase("getCoins")) {
                p.sendMessage(colorinfo + serverprefix + "Balance from Player " + target.getName() + ": " + ChatColor.BOLD + getCoins(target));
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("setCoins")) {
                p.sendMessage(colorinfo + serverprefix + "New balance from Player " + target.getName() + ": " + ChatColor.BOLD + setCoins(target, Integer.parseInt(args[2])));
            }
        }
        updateScoreboard();
        return false;
    }

    public static void enableKitPvp(Player player) {
        PlayerUtils.makeInvBackup(player);
        config.set(player.getName() + ".kitpvp.inPvp", true);
        Main.getPlugin().saveConfig();
        player.getInventory().clear();
        Kits.giveKit(player);
        prepareForPvp(player);
        setupScoreboard(player);
        if (config.getBoolean(player.getName() + ".pet.isSpawned")) PetUtils.despawnPet(player);
        updateScoreboard();
        oldAttackSpeed(player);
        Bukkit.broadcastMessage(colorinfo + serverprefix + player.getName() + " hat den PVP-Modus betreten.");
    }

    public static void disableKitPvp(Player player) {
        config.set(player.getName() + ".kitpvp.inPvp", null);
        Main.getPlugin().saveConfig();
        player.getInventory().clear();
        hideScoreboard(player);
        if (PlayerUtils.getInvBackup(player) != null) player.getInventory().setContents(PlayerUtils.getInvBackup(player));
        newAttackSpeed(player);
        Bukkit.broadcastMessage(colorinfo + serverprefix + player.getName() + " hat den PVP-Modus verlassen.");
    }

    public static void setupScoreboard(Player player) {
        Scoreboard scoreboard = Objects.requireNonNull(Bukkit.getScoreboardManager()).getNewScoreboard();
        Objective pvpStats = scoreboard.registerNewObjective("KitPvpScoreboard", "dummy", colorinfo + ChatColor.BOLD.toString() + "<< KitPVP Statistics>>");
        pvpStats.setDisplaySlot(DisplaySlot.SIDEBAR);
        pvpStats.setDisplayName(colorinfo + ChatColor.BOLD.toString() + "<< KitPVP Statistics >>");
        pvpStats.getScore(ChatColor.AQUA + ChatColor.BOLD.toString() + "=-=-=-=-=-=-=-=-=-=-=").setScore(8);
        pvpStats.getScore(ChatColor.BOLD + "Your Kit: " + Kits.getKit(player)).setScore(7);
        pvpStats.getScore("  ").setScore(6);
        pvpStats.getScore(ChatColor.WHITE + "Your Kill Count: " + ChatColor.BOLD + config.getInt(player.getName() + ".kitpvp.kills")).setScore(5);
        pvpStats.getScore(ChatColor.GOLD + "Coins: " + getCoins(player) + "$").setScore(4);
        pvpStats.getScore(" ").setScore(3);
        pvpStats.getScore(" ").setScore(2);
        HashMap<String, Integer> kills = new HashMap<>();
        for (String name : config.getKeys(false)) kills.put(name, getKills(name));
        int mostKills = 0;
        String mostKiller = "NOBODY";
        for (Map.Entry<String, Integer> kill : kills.entrySet()) {
            if (kill.getValue() <= mostKills) continue;
            mostKiller = kill.getKey();
            mostKills = kill.getValue();
        }
        pvpStats.getScore(colorsecondinfo + "Most Kills: " + ChatColor.BOLD + mostKills).setScore(1);
        pvpStats.getScore(colorsecondinfo + "by " + mostKiller).setScore(0);
        Objective heathDisplay = scoreboard.registerNewObjective("KitPvpHeath", "health", ChatColor.RED + "❤");
        heathDisplay.setDisplaySlot(DisplaySlot.BELOW_NAME);
        heathDisplay.setRenderType(RenderType.HEARTS);
        player.setScoreboard(scoreboard);
    }

    public static void updateScoreboard() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!isInPVP(player)) continue;
            setupScoreboard(player);
        }
    }

    public static void hideScoreboard(Player player) {
        Scoreboard scoreboard = player.getScoreboard();
        scoreboard.clearSlot(DisplaySlot.SIDEBAR);
    }




    public static void openKitSelector(Player player, Player target) {
        Inventory kitInv = Bukkit.createInventory(null, 27, colorinfo + "KitPVP: Select kit: " + target.getName());
        InterfaceUtils.fillPlaceholders(kitInv, ItemStacks.placeholder);
        kitInv.setItem(10, ItemStacks.kitPVPkitStandard);
        kitInv.setItem(11, ItemStacks.kitPVPkitPro);
        kitInv.setItem(12, ItemStacks.kitPVPkitUltra);
        kitInv.setItem(13, ItemStacks.kitPVPkitEpic);
        kitInv.setItem(14, ItemStacks.kitPVPkitSniper);
        kitInv.setItem(15, ItemStacks.kitPVPkitOp);
        kitInv.setItem(16, ItemStacks.kitPVPkitPearl);
        kitInv.setItem(22, ItemStacks.arrowUp);
        kitInv.setItem(26, ItemStacks.arrowNext);
        player.openInventory(kitInv);
        PlayerUtils.pinInventoryContents(player);
    }

    public static void openKitSelector(Player player) {
        openKitSelector(player, player);
    }

    public void openMainMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, InventoryType.HOPPER, colorinfo + "KitPVP: Main Menu");
        InterfaceUtils.fillPlaceholders(inv, ItemStacks.placeholder);
        ItemStack stats = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) stats.getItemMeta();
        assert meta != null;
        meta.setOwningPlayer(player);
        meta.setDisplayName(ChatColor.GREEN + "Your Stats");
        List<String> lore = new ArrayList<>();
        //STATS
        lore.add(ChatColor.RED + "  Kills: " + getKills(player.getName()));
        lore.add(ChatColor.GOLD + " Coins: " + getCoins(player));
        lore.add(ChatColor.YELLOW + " Unlocked Kits: " + "0/6");
        lore.add(ChatColor.YELLOW + " Times hittet: " + "0");
        lore.add(ChatColor.YELLOW + " Times shooted: " + "0");
        meta.setLore(lore);
        stats.setItemMeta(meta);
        inv.setItem(0, stats);
        inv.setItem(2, ItemStacks.createItem(Material.DROPPER, colorinfo + "Select Kit", null, 1));
        if (isInPVP(player)) inv.setItem(4, ItemStacks.createItem(Material.RED_CONCRETE, ChatColor.RED + "STOP PVP", null, 1));
        else inv.setItem(4, ItemStacks.createItem(Material.LIME_CONCRETE, ChatColor.GREEN + "START PVP", null, 1));
        player.openInventory(inv);
        PlayerUtils.pinInventoryContents(player);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player deadPlayer = event.getEntity();
        Player killer = deadPlayer.getKiller();
        if (!isInPVP(deadPlayer)) return;
        Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> {
            deadPlayer.getNearbyEntities(50, 10, 50).forEach(entity -> {
                if (entity.getType() == EntityType.DROPPED_ITEM) entity.remove();
            });
            if (killer == null || !isInPVP(killer)) return;
            killer.setHealth(20);
            killer.setFoodLevel(20);
            Kits.giveKit(killer);
            killer.sendMessage(colorinfo + ChatColor.BOLD.toString() + "+1 Kill");
            addKills(killer.getName(), 1);
            addCoins(killer, 5);
            updateScoreboard();
        }, 10);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        if (!isInPVP(event.getPlayer())) return;
        Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> {
            Kits.giveKit(event.getPlayer());
            for (Player p : Bukkit.getOnlinePlayers()) p.setHealth(p.getHealth() - 0.0001);
        }, 10);
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        Player damager = (Player) event.getDamager();
        if (!isInPVP(damager) || !isInPVP(player)) return;
        //ADD HIT
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (!isInPVP(event.getPlayer())) return;
        Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> {
            Kits.giveKit(event.getPlayer());
            for (Player p : Bukkit.getOnlinePlayers()) p.setHealth(p.getHealth() - 0.0001);
        }, 10);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!isInPVP(event.getPlayer())) return;
        Material brokenMaterial = event.getBlock().getType();
        if (brokenMaterial.toString().endsWith("_CONCRETE")) return;
        if (brokenMaterial == Material.COBWEB) return;
        if (brokenMaterial.toString().endsWith("GRASS") || brokenMaterial.toString().contains("TORCH")) return;
        event.setCancelled(true);
        PlayerUtils.sendCustomError(event.getPlayer(), "You can't break this block");
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        for (Entity nearbyEntity : event.getEntity().getNearbyEntities(7, 7, 7)) {
            if (!(nearbyEntity instanceof Player)) continue;
            Player player = (Player) nearbyEntity;
            if (isInPVP(player)) event.blockList().clear();
            return;
        }
    }

    @EventHandler
    public void onFishing(PlayerFishEvent event) {
        if (!isInPVP(event.getPlayer())) return;
        if (event.getHook().getState() == FishHook.HookState.HOOKED_ENTITY && event.getHook().getHookedEntity() != null && event.getHook().getHookedEntity() instanceof Player) {
            event.getHook().remove();
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getHitEntity() == null) return;
        if (event.getEntity().getType() != EntityType.FISHING_HOOK) return;
        if (!(event.getHitEntity() instanceof Player)) return;
        Player hooked = (Player) event.getHitEntity();
        if (!isInPVP(hooked)) return;
        hooked.damage(1);
        hooked.getLocation().add(0, 0.4, 0);
        hooked.setVelocity(hooked.getLocation().getDirection().multiply(-1));
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!isInPVP(event.getPlayer())) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (!event.hasItem()) return;
        Player player = event.getPlayer();
        if (Objects.requireNonNull(event.getItem()).getType() == Material.SKELETON_SPAWN_EGG) {
            event.setCancelled(true);
            Entity skeleton = player.getWorld().spawnEntity(Objects.requireNonNull(event.getClickedBlock()).getLocation().add(0, 1, 0), EntityType.SKELETON);
            skeleton.setCustomNameVisible(true);
            skeleton.setCustomName(ChatColor.GREEN + player.getName() + "'s SKELETON");
            player.getInventory().getItem(Objects.requireNonNull(event.getHand())).setAmount(player.getInventory().getItem(event.getHand()).getAmount() - 1);
            config.set(player.getName() + ".kitpvp.skeleton", skeleton.getUniqueId().toString());
            Main.getPlugin().saveConfig();
        }
    }

    @EventHandler
    public void onTarget(EntityTargetEvent event) {
        if (!(event.getTarget() instanceof Player)) return;
        Player target = (Player) event.getTarget();
        if (event.getEntity().getType() != EntityType.SKELETON) return;
        Entity skeleton = event.getEntity();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!config.contains(player.getName() + ".kitpvp.skeleton") || !Objects.equals(config.getString(player.getName() + ".kitpvp.skeleton"), skeleton.getUniqueId().toString())) return;
            if (target == player) event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInvClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;
        if (event.getClickedInventory().getType() == InventoryType.PLAYER) return;
        if (event.getCurrentItem() == null) return;
        String title = event.getView().getTitle();
        int slot = event.getSlot();
        ItemStack currentItem = event.getCurrentItem();
        Player p = (Player) event.getWhoClicked();
        if (title.startsWith(colorinfo + "KitPVP: Main Menu")) {
            if (slot == 2) {
                if (isInPVP(p)) {
                    PlayerUtils.sendCustomError(p, "Du musst PVP erst beenden bevor du dein kit auswählen kannst.");
                    p.closeInventory();
                    return;
                }
                openKitSelector(p);
            } else if (slot == 4) {
                if (currentItem.getType() == Material.LIME_CONCRETE) {
                    enableKitPvp(p);
                    p.sendMessage(colorinfo + serverprefix + "Du hast KitPVP eingeschaltet.");
                } else {
                    disableKitPvp(p);
                    p.sendMessage(colorinfo + serverprefix + "Du hast KitPVP ausgeschaltet.");
                }
                p.closeInventory();
            }
        } else if (title.startsWith(colorinfo + "KitPVP: Select kit: ")) {
            String playerName = title.substring(22);
            ConfigurationSection kitSection;
            if (config.isConfigurationSection(playerName + ".kitpvp")) kitSection = config.getConfigurationSection(playerName + ".kitpvp");
            else kitSection = config.createSection(playerName + ".kitpvp");
            assert kitSection != null;
            switch (slot) {
                case 10:
                    kitSection.set("kit", "standard");
                    break;
                case 11:
                    kitSection.set("kit", "pro");
                    break;
                case 12:
                    kitSection.set("kit", "ultra");
                    break;
                case 13:
                    kitSection.set("kit", "epic");
                    break;
                case 14:
                    kitSection.set("kit", "sniper");
                    break;
                case 15:
                    kitSection.set("kit", "op");
                    break;
                case 16:
                    kitSection.set("kit", "pearler");
                    break;
                case 22:
                    openMainMenu(p);
                    break;
            }
            if (!currentItem.isSimilar(ItemStacks.placeholder) && !currentItem.isSimilar(ItemStacks.arrowNext) && !currentItem.isSimilar(ItemStacks.arrowUp)) {
                Main.getPlugin().saveConfig();
                p.closeInventory();
                enableKitPvp(p);
                Player target = Bukkit.getPlayer(playerName);
                if (target != null) Kits.giveKit(target);
            }
        }
    }

    public static void prepareForPvp(Player player) {
        player.setGameMode(GameMode.SURVIVAL);
        player.setAllowFlight(false);
        player.setHealthScale(20);
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setInvisible(false);
        player.setInvulnerable(false);
    }


    //ATTACKSPEED
    public static void oldAttackSpeed(Player player) {
        Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_ATTACK_SPEED)).setBaseValue(100);
    }
    public static void newAttackSpeed(Player player) {
        Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_ATTACK_SPEED)).setBaseValue(4);
    }

    //KILLS
    public static int getKills(String playerName) {
        return config.getInt(playerName + ".kitpvp.kills");
    }

    public static void setKills(String playerName, int kills) {
        config.set(playerName + ".kitpvp.kills", kills);
        Main.getPlugin().saveConfig();
    }

    public static int addKills(String playerName, int kills) {
        setKills(playerName, getKills(playerName) + kills);
        return getKills(playerName);
    }

    //IS IN PVP
    public static boolean isInPVP(Player player) {
        return config.getBoolean(player.getName() + ".kitpvp.inPvp");
    }


    //COINS
    public static int getCoins(Player player) {
        return config.getInt(player.getName() + ".kitpvp.coins");
    }

    public static int addCoins(Player player, int coins) {
        return setCoins(player, getCoins(player) + coins);
    }

    public static int setCoins(Player player, int coins) {
        config.set(player.getName() + ".kitpvp.coins", coins);
        Main.getPlugin().saveConfig();
        return getCoins(player);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> tab = new ArrayList<>();
        if (!sender.isOp()) return tab;
        if (args.length == 1) {
            tab.add("addPlayer");
            tab.add("removePlayer");
            tab.add("setKit");
            tab.add("setCoins");
            tab.add("getCoins");
            tab.add("resetKit");
        } else if (args.length == 2)
            Bukkit.getOnlinePlayers().forEach(player -> tab.add(player.getName()));
        return tab;
    }
}