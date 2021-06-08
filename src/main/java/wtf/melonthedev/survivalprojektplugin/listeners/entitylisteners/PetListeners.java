package wtf.melonthedev.survivalprojektplugin.listeners.entitylisteners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import wtf.melonthedev.survivalprojektplugin.Main;
import wtf.melonthedev.survivalprojektplugin.commands.PetCommand;
import wtf.melonthedev.survivalprojektplugin.gui.ItemStacks;
import wtf.melonthedev.survivalprojektplugin.utils.PetUtils;
import wtf.melonthedev.survivalprojektplugin.utils.PlayerUtils;

import java.util.Objects;

import static wtf.melonthedev.survivalprojektplugin.Main.colorinfo;
import static wtf.melonthedev.survivalprojektplugin.Main.serverprefix;
import static wtf.melonthedev.survivalprojektplugin.utils.PetUtils.despawnPet;
import static wtf.melonthedev.survivalprojektplugin.utils.PetUtils.spawnPet;

public class PetListeners implements Listener {

    //EVENTS
    @EventHandler
    public void onInvClick(InventoryClickEvent e) {
        if (e.getClickedInventory() == null) return;
        String title = e.getView().getTitle();
        if (!title.startsWith(colorinfo + "PetMenu: ")) return;
        Player player = (Player) e.getWhoClicked();
        int slot = e.getSlot();
        ConfigurationSection petSection = Main.getPlugin().getConfig().getConfigurationSection(player.getName() + ".pet");
        assert petSection != null;
        if (title.equals(colorinfo + "PetMenu: Home (1/2)")) {
            if (Objects.requireNonNull(e.getClickedInventory()).getType() == InventoryType.PLAYER) return;
            switch (slot) {
                case 10:
                    if (PetUtils.teleportPet(player))
                        player.sendMessage(colorinfo + serverprefix + "Du hast dein Pet zu dir teleportiert.");
                    break;
                case 12:
                    PetCommand.openSelectTypeInterface(player);
                    break;
                case 14:
                    PetUtils.openSpeedInventory(player);
                    return;
                case 16:
                    PetUtils.openAgeInventory(player);
                    break;
                case 22:
                    PetUtils.handlePetSpawnItem(player, e.getCurrentItem());
                    break;
                case 26:
                    PetUtils.openNextMainPage(player);
                    break;
            }
        } else if (title.equals(colorinfo + "PetMenu: Select Type")) {
            if (petSection == null) Main.getPlugin().getConfig().createSection(player.getName() + ".pet");
            if (e.getCurrentItem() == null) return;
            ItemStack currentItem = e.getCurrentItem();
            if (e.getClickedInventory().getType() != InventoryType.PLAYER && slot == 4) {
                if (currentItem.isSimilar(ItemStacks.tempType)) return;
                PetCommand.giveHeadBack(player, currentItem);
                e.getInventory().setItem(4, ItemStacks.tempType);
                player.sendMessage(colorinfo + serverprefix + "Du hast deinen Kopf zurückerhalten.");
                petSection.set("type", null);
                petSection.set("MHFType", null);
                Main.getPlugin().saveConfig();
                despawnPet(player);
                spawnPet(player);
                return;
            }

            if (Objects.requireNonNull(e.getClickedInventory()).getType() != InventoryType.PLAYER) return;
            if (currentItem.getType() == Material.AIR) return;
            if (currentItem.getType() != Material.PLAYER_HEAD) {
                PlayerUtils.sendCustomError(player, "Du musst einen Kopf einlegen.");
                return;
            }
            if (!currentItem.hasItemMeta()) return;
            SkullMeta meta = (SkullMeta) currentItem.getItemMeta();
            if (meta == null) return;
            try {
                String type = meta.getLocalizedName().split("-")[0];
                String mhfType = meta.getLocalizedName().split("-")[1];

                try {
                    EntityType.valueOf(type);
                } catch (IllegalArgumentException exception) {
                    PlayerUtils.sendCustomError(player, "Dieser Kopf ist ungültig.");
                    player.closeInventory();
                    return;
                }
                petSection.set("type", type);
                petSection.set("MHFType", mhfType);
                Main.getPlugin().saveConfig();
                player.sendMessage(colorinfo + serverprefix + "Du hast dein Pet zum type '" + type + "' gemacht.");
                Objects.requireNonNull(player.getInventory().getItem(slot)).setAmount(Objects.requireNonNull(player.getInventory().getItem(slot)).getAmount() - 1);
                if (!Objects.requireNonNull(Objects.requireNonNull(e.getInventory().getItem(4)).getItemMeta()).getDisplayName().endsWith("(temporär)")) {
                    if (e.getInventory().getItem(4) == null) return;
                    PetCommand.giveHeadBack(player, Objects.requireNonNull(e.getInventory().getItem(4)));
                    player.sendMessage(colorinfo + serverprefix + "Du hast deinen Kopf zurückerhalten.");
                }
                PetCommand.openSelectTypeInterface(player);
                if (petSection.getBoolean("isSpawned")) {
                    despawnPet(player);
                    spawnPet(player);
                }
            } catch (ArrayIndexOutOfBoundsException exception) {
                PlayerUtils.sendCustomError(player, "Dieser Kopf ist ungültig.");
            }
        } else if (title.equals(colorinfo + "PetMenu: Set Age")) {
            if (slot == 11) {
                PetUtils.setAge(player, "baby");
                player.closeInventory();
            } else if (slot == 15) {
                PetUtils.setAge(player, "adult");
                player.closeInventory();
            } else if (slot == 22) {
                PetUtils.openMainPage(player);
            }

        } else if (title.equals(colorinfo + "PetMenu: Home (2/2)")) {
            if (slot == 18) {
                PetUtils.openMainPage(player);
            } else if (slot == 10) {
            } else if (slot == 22) {
                PetUtils.handlePetSpawnItem(player, e.getCurrentItem());
            } else if (slot == 12) {
                player.sendMessage(colorinfo + serverprefix + "Um den Namen deines Pets zu ändern gebe /pet rename <Name: String>");
                player.closeInventory();
            } else if (slot == 14) {
                PetUtils.toggleSilence(player);
                player.closeInventory();
            } else if (slot == 16) {

            }
        } else if (title.equals(colorinfo + "PetMenu: Set Follow Speed")) {
            PetUtils.handleSpeed(player, slot + 1);
        } else if (title.equals(colorinfo + "PetMenu: Others")) {
            if (slot == 0) {
                if (petSection.getBoolean("others.onFire")) {
                    petSection.set("others.onFire", null);
                } else {
                    petSection.set("others.onFire", true);
                }
            } else if (slot == 1) {
                if (petSection.getBoolean("others.jumpBoost")) {
                    petSection.set("others.jumpBoost", null);
                } else {
                    petSection.set("others.jumpBoost", true);
                }
            } else if (slot == 2) {
                if (petSection.getBoolean("others.particles")) {
                    petSection.set("others.particles", null);
                } else {
                    petSection.set("others.particles", true);
                }
            } else if (slot == 3) {
                player.sendMessage("BETA");
            } else if (slot == 4) {
                if (petSection.getBoolean("others.saddled")) {
                    petSection.set("others.saddled", null);
                } else {
                    petSection.set("others.saddled", true);
                }
            } else if (slot == 5) {
                if (petSection.getBoolean("others.passenger")) {
                    petSection.set("others.passenger", null);
                } else {
                    petSection.set("others.passenger", true);
                }
            } else if (slot == 6) {
                player.sendMessage("BETA");
            } else {
                return;
            }
            if (petSection.getBoolean("isSpawned")) {
                despawnPet(player);
                spawnPet(player);
            }
            player.closeInventory();
            Main.getPlugin().saveConfig();
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
        Player player = e.getPlayer();
        ConfigurationSection petSection = Main.getPlugin().getConfig().getConfigurationSection(player.getName() + ".pet");
        if (petSection == null) return;
        if (petSection.getBoolean("isSpawned")) {
            if (e.getCause() == PlayerTeleportEvent.TeleportCause.COMMAND || e.getCause() == PlayerTeleportEvent.TeleportCause.PLUGIN)
                Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> PetUtils.teleportPet(player), 12);
        }
    }
}
