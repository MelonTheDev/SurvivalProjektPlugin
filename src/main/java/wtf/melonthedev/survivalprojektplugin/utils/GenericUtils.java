package wtf.melonthedev.survivalprojektplugin.utils;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import wtf.melonthedev.survivalprojektplugin.Main;
import wtf.melonthedev.survivalprojektplugin.others.Config;
import wtf.melonthedev.survivalprojektplugin.others.CustomEnchantments;
import wtf.melonthedev.survivalprojektplugin.others.NPC;

import java.util.HashMap;
import java.util.Objects;
import java.util.logging.Level;

import static wtf.melonthedev.survivalprojektplugin.Main.serverName;

public class GenericUtils {

    public static void loadStandardSettings() {
        FileConfiguration config = Config.getCustomConfig("settings.yml");
        ConfigurationSection commandSection = config.createSection("settings.commands");
        //CONFIGURATION
        config.set("serverprefix", "Survivalprojekt");
        config.set("servername", "Survivalprojekt");
        config.set("tabList.customList", true);
        config.set("tabList.listHeader", ChatColor.AQUA.toString() + ChatColor.BOLD.toString() + ChatColor.MAGIC.toString() + "A " + ChatColor.RESET.toString() + ChatColor.AQUA.toString() + ChatColor.BOLD.toString() + serverName + ChatColor.MAGIC.toString() + " A");
        config.set("tabList.listFooter", ChatColor.GOLD.toString() + "❤ Survivalmap | Tolle Community lol ❤");
        config.set("colorInfo", "AQUA");
        config.set("colorSecondInfo", "DARK_AQUA");
        config.set("colorError", "DARK_RED");
        config.set("chat.translateAlternateColorCodes", true);
        config.set("gameplay.resetFalldistanceOnPortalEnter", true);
        config.set("gameplay.randromizer", false);
        config.set("gameplay.disableTnt", false);

        //COMMANDS
        commandSection.set("afk.enabled", true);
        commandSection.set("arrowholder.enabled", true);
        commandSection.set("blamieren.enabled", true);
        commandSection.set("colorcodes.enabled", true);
        commandSection.set("craft.enabled", true);
        commandSection.set("danke.enabled", true);
        commandSection.set("date.enabled", true);
        commandSection.set("enderchest.enabled", true);
        commandSection.set("fly.enabled", true);
        commandSection.set("home.enabled", true);
        commandSection.set("killban.enabled", true);
        commandSection.set("kitpvp.enabled", true);
        commandSection.set("location.enabled", true);
        commandSection.set("lockchest.enabled", true);
        commandSection.set("marry.enabled", true);
        commandSection.set("npc.enabled", true);
        commandSection.set("combine.enabled", true);
        commandSection.set("pet.enabled", true);
        commandSection.set("reply.enabled", true);
        commandSection.set("settings.enabled", true);
        commandSection.set("shop.enabled", true);
        commandSection.set("status.enabled", true);
        commandSection.set("test.enabled", true);
        commandSection.set("tpa.enabled", true);
        commandSection.set("trash.enabled", true);
        commandSection.set("votekick.enabled", true);

        //GENERIC
        config.set("isLoaded", true);
        Config.saveCustomConfig(config);
    }

     public static void loadSettings() {
         FileConfiguration config = Config.getCustomConfig("settings.yml");
         if (!config.getBoolean("isLoaded")) {
             GenericUtils.loadStandardSettings();
         }
         //SERVERPREFIX & COLORS
         if (config.contains("serverprefix"))
             Main.serverprefix = "[" + config.getString("serverprefix") + "] ";
         if (config.contains("servername"))
             Main.serverName = config.getString("servername");
         if (config.contains("colorInfo")) {
             try {
                 Main.colorinfo = ChatColor.valueOf(config.getString("colorInfo"));
             } catch (IllegalArgumentException e) {
                 Main.getPlugin().getLogger().log(Level.WARNING, "Invalid color in 'settings.yml' at 'colorInfo'");
             }
         }
         if (config.contains("colorError")) {
             try {
                 Main.colorerror = ChatColor.valueOf(config.getString("colorError"));
             } catch (IllegalArgumentException e) {
                 Main.getPlugin().getLogger().log(Level.WARNING, "Invalid color in 'settings.yml' at 'colorError'");
             }

         }
     }

    public static HashMap<String, Object> getCustomSettings(String section) {
        HashMap<String, Object> customSettings = new HashMap<>();
        FileConfiguration settings = Config.getCustomConfig("settings.yml");
        if (section == null) {
            settings.getValues(false).forEach(customSettings::put);
        } else {
            if (!settings.isConfigurationSection(section)) {
                System.out.println("[DEBUG] Section wasn't found");
                settings.createSection(section);
            }
            Objects.requireNonNull(settings.getConfigurationSection(section)).getValues(false).forEach(customSettings::put);
        }
        return customSettings;
    }

    public static HashMap<String, Object> getCustomSettings() {
        HashMap<String, Object> customSettings = new HashMap<>();
        FileConfiguration settings = Config.getCustomConfig("settings.yml");
        settings.getValues(false).forEach(customSettings::put);
        return customSettings;
    }

    public static void cleanUp() {
        //NPCs
        NPC.disableNpcs();
        //CUSTOM EVENTS
        PacketReader.uninjectForAll();
        //CUSTOM RECIPES
        ItemUtils.unregisterRecipes();
    }

    public static void load() {
        //CONFIGS
        GenericUtils.loadSettings();
        //ENCHANTMENTS
        CustomEnchantments.register();
        //PETS
        PetUtils.handleFollowAllPlayers();
        //NPCs
        NPC.loadNPCs();
        //CUSTOM EVENTS
        PacketReader.injectForAll();
        //CUSTOM RECIPES
        ItemUtils.registerCustomRecipes();
        //LODESTONES
        ItemUtils.loadLodeStones();
    }
}
