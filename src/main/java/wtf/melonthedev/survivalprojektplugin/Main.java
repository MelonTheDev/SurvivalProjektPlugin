package wtf.melonthedev.survivalprojektplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import wtf.melonthedev.survivalprojektplugin.commands.*;
import wtf.melonthedev.survivalprojektplugin.listeners.blocklisteners.*;
import wtf.melonthedev.survivalprojektplugin.listeners.entitylisteners.*;
import wtf.melonthedev.survivalprojektplugin.listeners.playerlisteners.*;
import wtf.melonthedev.survivalprojektplugin.utils.GenericUtils;

public final class Main extends JavaPlugin {

    //TODO: /settings interface
    //TODO: finish /shop
    //TODO: fix /votekick

    private static Main plugin;
    public static String serverprefix = "[Survivalprojekt] ";
    public static String serverName = "Survivalprojekt";
    public static ChatColor colorinfo = ChatColor.AQUA;
    public static ChatColor colorsecondinfo = ChatColor.DARK_AQUA;
    public static ChatColor colorerror = ChatColor.DARK_RED;

    @Override
    public void onEnable() {
        plugin = this;
        listenerRegistration();
        commandRegistration();
        tabCompleterRegistration();
        GenericUtils.load();
    }

    @Override
    public void onDisable() {
        GenericUtils.cleanUp();
    }

    public static Main getPlugin() {
        return plugin;
    }

    private void listenerRegistration() {
        PluginManager pluginmanager = Bukkit.getPluginManager();
        //PLAYER-EVENTS
        pluginmanager.registerEvents(new PlayerJoinListener(), this);
        pluginmanager.registerEvents(new PlayerQuitListener(), this);
        pluginmanager.registerEvents(new PlayerInteractChestListener(), this);
        pluginmanager.registerEvents(new PlayerDeathListener(), this);
        pluginmanager.registerEvents(new PlayerInteractItemListener(), this);
        pluginmanager.registerEvents(new PlayerInteractNpcListener(), this);
        pluginmanager.registerEvents(new PlayerMoveListener(), this);
        pluginmanager.registerEvents(new PlayerGrapplingListener(), this);
        pluginmanager.registerEvents(new PlayerTeleportListener(), this);
        pluginmanager.registerEvents(new PlayerChatListener(), this);
        pluginmanager.registerEvents(new PlayerWorldChangeListener(), this);
        pluginmanager.registerEvents(new PlayerDamageListener(), this);
        pluginmanager.registerEvents(new PlayerBedListeners(), this);
        pluginmanager.registerEvents(new PlayerKickListener(), this);
        pluginmanager.registerEvents(new PlayerHandSwitchListener(), this);
        pluginmanager.registerEvents(new PlayerLoginListener(), this);
        //ENTITY-EVENTS
        pluginmanager.registerEvents(new EntitySpawnListener(), this);
        pluginmanager.registerEvents(new EntityClickListener(), this);
        pluginmanager.registerEvents(new EntityDeathListener(), this);
        pluginmanager.registerEvents(new EntityExplodeListener(), this);
        pluginmanager.registerEvents(new EntityPortalListener(), this);
        pluginmanager.registerEvents(new EntityTargetListener(), this);
        pluginmanager.registerEvents(new EntityPickupItemListener(), this);
        pluginmanager.registerEvents(new EntityHangingListeners(), this);
        pluginmanager.registerEvents(new EntityShootListener(), this);
        //BLOCK-EVENTS
        pluginmanager.registerEvents(new BlockBreakListener(), this);
        pluginmanager.registerEvents(new BlockPlaceListener(), this);
        pluginmanager.registerEvents(new BlockPhysicsListener(), this);
        pluginmanager.registerEvents(new BlockClickListener(), this);
        //OTHER-EVENTS
        pluginmanager.registerEvents(new ServerPingListener(), this);
        pluginmanager.registerEvents(new InventoryListeners(), this);
        pluginmanager.registerEvents(new PetListeners(), this);
        pluginmanager.registerEvents(new ItemMoveListener(), this);
        pluginmanager.registerEvents(new SignEditListener(), this);
        //COMMAND-EVENTS
        pluginmanager.registerEvents(new ShopCommand(), this);
        pluginmanager.registerEvents(new LockchestCommand(), this);
        pluginmanager.registerEvents(new VotePlotCommand(), this);
        pluginmanager.registerEvents(new MarryCommand(), this);
        pluginmanager.registerEvents(new ArrowHolderCommand(), this);
        pluginmanager.registerEvents(new KitPvpCommand(), this);
    }

    private void commandRegistration() {
        try {
            //INFO-COMMANDS
            getCommand("date").setExecutor(new DateCommand());
            getCommand("settings").setExecutor(new SettingsCommand());
            getCommand("status").setExecutor(new StatusCommand());
            getCommand("location").setExecutor(new LocationCommand());

            //TP-COMMANDS
            getCommand("home").setExecutor(new HomeCommand());
            getCommand("tpa").setExecutor(new TpaCommand());

            //USABLE-COMMANDS
            getCommand("enderchest").setExecutor(new EnderChestCommand());
            getCommand("lockchest").setExecutor(new LockchestCommand());
            getCommand("shop").setExecutor(new ShopCommand());
            getCommand("votekick").setExecutor(new VoteKickCommand());
            getCommand("killban").setExecutor(new KillBanCommand());
            getCommand("combine").setExecutor(new CombineCommand());
            getCommand("npc").setExecutor(new NpcCommand());
            getCommand("afk").setExecutor(new AfkCommand());
            getCommand("voteplot").setExecutor(new VotePlotCommand()); //HERE
            getCommand("arrowholder").setExecutor(new ArrowHolderCommand());

            //FUNCOMMANDS
            getCommand("marry").setExecutor(new MarryCommand());
            getCommand("danke").setExecutor(new DankeCommand());
            getCommand("test").setExecutor(new TestCommand());
            getCommand("pet").setExecutor(new PetCommand()); //AUCH
            getCommand("kitpvp").setExecutor(new KitPvpCommand()); //HERE

            //ADMINESTRATIVE COMMANDS
            getCommand("restoreinv").setExecutor(new RestoreInvCommand()); //HERE

        } catch (NullPointerException ex) {
            System.out.println(ChatColor.RED + "COMMAND NOT FOUND! Registered in plugin.yml?");
        }
    }

    private void tabCompleterRegistration() {
        try {
            getCommand("status").setTabCompleter(new StatusCommand());
            getCommand("home").setTabCompleter(new HomeCommand());
            getCommand("votekick").setTabCompleter(new VoteKickCommand());
            getCommand("killban").setTabCompleter(new KillBanCommand());
            getCommand("voteplot").setTabCompleter(new VotePlotCommand());
            getCommand("shop").setTabCompleter(new ShopCommand());
            getCommand("pet").setTabCompleter(new PetCommand());
            getCommand("kitpvp").setTabCompleter(new KitPvpCommand());
        } catch (NullPointerException ex) {
            System.out.println(ChatColor.RED + "COMMAND NOT FOUND! Registered in plugin.yml?");
        }
    }
}
