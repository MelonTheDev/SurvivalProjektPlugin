package wtf.melonthedev.survivalprojektplugin.commands;

import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import wtf.melonthedev.survivalprojektplugin.Main;
import wtf.melonthedev.survivalprojektplugin.gui.InterfaceUtils;
import wtf.melonthedev.survivalprojektplugin.gui.ItemStacks;
import wtf.melonthedev.survivalprojektplugin.utils.*;
import java.util.*;
import static wtf.melonthedev.survivalprojektplugin.Main.*;
import static wtf.melonthedev.survivalprojektplugin.utils.PetUtils.*;

public class PetCommand implements CommandExecutor, Listener, TabCompleter {

    FileConfiguration config = Main.getPlugin().getConfig();

    //ON COMMAND
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (CommandUtils.isDisabled(sender, "pet")) return true;
        //GENERAL
        if (!PlayerUtils.isPlayer(sender)) return true;
        Player p = (Player) sender;
        ConfigurationSection petSection = config.getConfigurationSection(p.getName() + ".pet");
        if (petSection == null) {
            config.createSection(p.getName() + ".pet");
            Main.getPlugin().saveConfig();
        }
        assert petSection != null;

        //HANDLE OTHER ARGUMENTS
        if (args.length != 0) {
            if (args.length == 1) {
                //1 ARGUMENTS
                handleOneArgumentCommands(args[0], p);
                return true;
            } else if (args[0].equalsIgnoreCase("rename")) {
                //RENAME PET
                StringBuilder name = new StringBuilder();
                for (String arg : args) {
                    if (arg.equalsIgnoreCase("rename")) continue;
                    name.append(arg).append(" ");
                }
                setPetName(p, name.toString());
                p.sendMessage(colorinfo + serverprefix + "Du hast den Namen deines Pets zu '" + name.toString() + "' geändert.");
                return true;
            } else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("nameColor")) {
                    PetUtils.setPetNameColor(p, args[1]);
                    p.sendMessage(colorinfo + serverprefix + "Du hast die Farbe des Namen deines Pets zu '" + args[1].toLowerCase() + "' geändert.");
                    return true;
                } else if (args[0].equalsIgnoreCase("setAge")) {
                    PetUtils.setAge(p, args[1]);
                    return true;
                } else if (args[0].equalsIgnoreCase("setSpeed")) {
                    try {
                        int speed = Integer.parseInt(args[1]);
                        PetUtils.handleSpeed(p, speed);
                    } catch (NumberFormatException e) {
                        PlayerUtils.sendCustomError(p, "Du musst eine Zahl zwischen 1 und 9 angeben");
                    }
                    return true;
                }
            }
            PlayerUtils.sendSyntaxError(p, "/pet");
            return true;
        }
        openMainPage(p);
        return false;
    }



    //PRIVATE TEMP METHODS
    public static void openSelectTypeInterface(Player p) {
        ConfigurationSection petSection = Main.getPlugin().getConfig().getConfigurationSection(p.getName() + ".pet");
        //INVENTORY
        Inventory inv = Bukkit.createInventory(null, InventoryType.DROPPER, colorinfo + "PetMenu: Select Type");
        InterfaceUtils.fillPlaceholders(inv, ItemStacks.placeholder);
        inv.setItem(4, ItemStacks.tempType);

        //SET TYPE HEAD
        if (petSection != null && petSection.contains("type") && petSection.contains("MHFType")) {
            String type = petSection.getString("type");
            String mhfType = petSection.getString("MHFType");
            try {
                EntityType.valueOf(type);
            } catch (IllegalArgumentException exception) {
                return;
            }

            ItemStack typeHead = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta sm = (SkullMeta) typeHead.getItemMeta();
            assert sm != null;
            sm.setOwner(mhfType);
            sm.setDisplayName(ChatColor.GOLD + "Type: " + type);
            sm.setLocalizedName(type + "-" + mhfType + "-head");
            typeHead.setItemMeta(sm);
            inv.setItem(4, typeHead);
        }
        p.openInventory(inv);
        PlayerUtils.pinInventoryContents(p);
    }

    public static void giveHeadBack(Player player, ItemStack head) {
        if (!head.hasItemMeta()) return;
        //IF HEAD IS THE STANDARD HEAD RETURN
        if (Objects.requireNonNull(head.getItemMeta()).getDisplayName().endsWith("(temporär)")) return;
        ItemStack headBack = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta currentMeta = (SkullMeta) head.getItemMeta();
        String type = currentMeta.getLocalizedName().split("-")[0];
        currentMeta.setDisplayName(ChatColor.RESET + type.toLowerCase() + " Head");
        headBack.setItemMeta(currentMeta);
        if (player.getInventory().firstEmpty() == -1)
            player.getWorld().dropItem(player.getLocation(), headBack);
        else
            player.getInventory().addItem(headBack);
    }

    public void handleOneArgumentCommands(String arg, Player sender) {
        switch (arg) {
            case "teleport":
                if (teleportPet(sender))
                    sender.sendMessage(colorinfo + serverprefix + "Du hast dein Pet zu dir teleportiert.");
                break;
            case "spawn":
                sender.sendMessage(colorinfo + serverprefix + "Du hast dein Pet gespawnt.");
                spawnPet(sender);
                break;
            case "despawn":
                despawnPet(sender);
                sender.sendMessage(colorinfo + serverprefix + "Du hast dein Pet despawnt.");
                break;
            case "ride":
                PetUtils.setPlayerOnSaddle(sender);
                break;
            default:
                PlayerUtils.sendSyntaxError(sender, "/pet");
                break;
        }
    }

    //TABCOMPLETER
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> tab = new ArrayList<>();
        if (args.length == 1) {
            tab.add("rename");
            tab.add("ride");
            tab.add("nameColor");
            tab.add("teleport");
            tab.add("setAge");
            tab.add("setSpeed");
            if (config.getBoolean(sender.getName() + ".pet.isSpawned")) tab.add("despawn");
            else tab.add("spawn");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("setAge")) {
            tab.add("baby");
            tab.add("adult");
        }
        return tab;
    }
}
