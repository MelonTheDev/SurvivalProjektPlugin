package wtf.melonthedev.survivalprojektplugin.utils;

import net.minecraft.server.v1_16_R3.EntityInsentient;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.entity.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import wtf.melonthedev.survivalprojektplugin.Main;
import wtf.melonthedev.survivalprojektplugin.gui.InterfaceUtils;
import wtf.melonthedev.survivalprojektplugin.gui.ItemStacks;

import java.util.Objects;
import java.util.UUID;

import static wtf.melonthedev.survivalprojektplugin.Main.colorinfo;
import static wtf.melonthedev.survivalprojektplugin.Main.serverprefix;

public class PetUtils {

    static FileConfiguration config = Main.getPlugin().getConfig();

    //GENERAL THINGS
    public static boolean teleportPet(Player player) {
        if (getPet(player) == null) {
            PlayerUtils.sendCustomError(player, "Du musst dein Pet erst spawnen");
            player.closeInventory();
            return false;
        }
        LivingEntity pet = getPet(player);

        //IFs
        ConfigurationSection petSection = config.getConfigurationSection(player.getName() + ".pet");
        if (petSection == null) config.createSection(player.getName() + ".pet");
        assert petSection != null;
        if (!petSection.getBoolean("isSpawned")) {
            PlayerUtils.sendCustomError(player, "Du musst dein Pet erst spawnen");
            player.closeInventory();
            return false;
        }
        if (pet == null) {
            PlayerUtils.sendCustomError(player, "Das Pet wurde nicht gefunden.");
            setPetSectionEntry(player, "isSpawned", false);
            Main.getPlugin().saveConfig();
            player.closeInventory();
            return false;
        }
        if (!pet.getPassengers().isEmpty()) {
            pet.getPassengers().forEach(passenger -> {
                pet.removePassenger(passenger);
                passenger.remove();
                passenger.setSilent(true);
                passenger.setInvulnerable(false);
                ((LivingEntity) passenger).setHealth(0);
                if (passenger instanceof Pig) ((Pig) passenger).setSaddle(false);
            });
        }


        //TELEPORT
        pet.teleport(player.getLocation());

        //PASSENGER
        addPassenger(player, pet);
        //FOLLOW PLAYER
        handleFollowPlayer(player);
        player.closeInventory();
        return true;
    }

    public static void addPassenger(Player player, Entity pet) {
        ConfigurationSection petSection = Main.getPlugin().getConfig().getConfigurationSection(player.getName() + ".pet");
        assert petSection != null;
        if (petSection.getBoolean("others.passenger")) {
            if (petSection.contains("others.passengerUUID")) return;
            Entity passenger = createPet(player);
            if (passenger instanceof Ageable) ((Ageable) passenger).setBaby();
            pet.addPassenger(passenger);
            petSection.set("others.passengerUUID", passenger.getUniqueId().toString());
            Main.getPlugin().saveConfig();
        }
    }

    public static void setPlayerOnSaddle(Player player) {
        ConfigurationSection petSection = Main.getPlugin().getConfig().getConfigurationSection(player.getName() + ".pet");
        assert petSection != null;
        Entity pet = Bukkit.getEntity(UUID.fromString(Objects.requireNonNull(petSection.getString("uuid"))));
        assert pet != null;
        pet.addPassenger(player);
    }

    public static void updatePet(Player owner) {
        LivingEntity pet = getPet(owner);
        if (pet == null) return;
        String name = owner.getName() + "'s Pet";
        ChatColor nameColor = ChatColor.GREEN;
        if (getPetName(owner) != null) name = getPetName(owner);
        if (getPetNameColor(owner) != null) nameColor = getPetNameColor(owner);
        pet.setCustomName(nameColor + name);
        customizePet(pet, owner);
        handleFollowPlayer(owner);
    }

    public static void spawnPet(Player p) {
        //VARIABLES
        ConfigurationSection petSection = config.getConfigurationSection(p.getName() + ".pet");
        if (petSection == null) config.createSection(p.getName() + ".pet");
        assert petSection != null;
        LivingEntity pet = (LivingEntity) createPet(p);

        //IFs
        if (petSection.getBoolean("isSpawned")) {
            PlayerUtils.sendCustomError(p, "Du hast dein Pet bereits gespawnt.");
            return;
        }
        //CONFIG ENTRYS
        petSection.set("isSpawned", true);
        petSection.set("uuid", pet.getUniqueId().toString());
        if (!petSection.contains("walkspeed")) petSection.set("walkspeed", 1.25);
        Main.getPlugin().saveConfig();

        //GENERAL THINGS
        p.closeInventory();

        //CUSTOMIZE PET
        customizePet(pet, p);
        handleFollowPlayer(p);
    }

    public static void despawnPet(Player p) {
        ConfigurationSection petSection;
        if (config.isConfigurationSection(p.getName() + ".pet")) petSection = config.getConfigurationSection(p.getName() + ".pet");
        else petSection = config.createSection(p.getName() + ".pet");

        //IFs
        assert petSection != null;
        if (!petSection.getBoolean("isSpawned")) {
            PlayerUtils.sendCustomError(p, "Du musst dein Pet erst spawnen.");
            return;
        }
        if (!petSection.contains("uuid", true)) {
            PlayerUtils.sendCustomError(p, "Du musst ein Pet haben, um es zu despawnen.");
            p.closeInventory();
            return;
        }
        UUID uuid = UUID.fromString(Objects.requireNonNull(petSection.getString("uuid")));
        Entity pet = Bukkit.getEntity(uuid);
        if (pet != null) {
            if (!pet.getPassengers().isEmpty()) {
                pet.getPassengers().forEach(passenger -> {
                    if (passenger instanceof Player) passenger.leaveVehicle();
                    else {
                        passenger.remove();
                        ((LivingEntity) passenger).setHealth(0);
                    }
                });
            }
            //DESPAWN
            pet.remove();
        } else {
            PlayerUtils.sendCustomError(p, "Das Pet wurde nicht gefunden.");
        }
        //RESET OTHERS
        petSection.set("isSpawned", false);
        petSection.set("wasSpawned", false);
        Main.getPlugin().saveConfig();
        p.closeInventory();
    }

    public static void handleFollowPlayer(Player player) {
        if (getPetSectionEntry(player, "walkspeed") == null) return;
        double walkSpeed = (double) getPetSectionEntry(player, "walkspeed");
        if (walkSpeed == 0) walkSpeed = 1.25;
        LivingEntity pet = getPet(player);
        followPlayer(player, pet, walkSpeed);
    }

    public static void handleFollowAllPlayers() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            PetUtils.handleFollowPlayer(p);
        }
    }

    public static void followPlayer(Player player, LivingEntity pet, double walkspeed) {
        final LivingEntity entity = pet;
        final Player p = player;
        if (entity == null) return;
        Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(), () -> {
            float speed = (float) walkspeed;
            if (config.getDouble(p.getName() + ".pet.walkspeed") != speed) speed = (float) config.getDouble(p.getName() + ".pet.walkspeed");
            ((EntityInsentient) ((CraftEntity) entity).getHandle()).getNavigation().a(p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ(), speed);
        }, 0,  12);
    }

    public static void toggleSilence(Player player) {
        boolean status = config.getBoolean(player.getName() + ".pet.silent");
        if (status) {
            config.set(player.getName() + ".pet.silent", null);
            player.sendMessage(colorinfo + serverprefix + "Dein Pet kann jetzt wieder reden.");
        } else {
            config.set(player.getName() + ".pet.silent", true);
            player.sendMessage(colorinfo + serverprefix + "Dein Pet ist jetzt leise.");
        }
        Main.getPlugin().saveConfig();
        if (config.getBoolean(player.getName() + ".pet.isSpawned")) updatePet(player);
    }

    //SET THINGS
    public static void setPetNameColor(Player owner, String colorString) {
        //VARIABLES
        ConfigurationSection petSection = config.getConfigurationSection(owner.getName() + ".pet");
        assert petSection != null;
        try {
            //SET COLOR
            ChatColor color = ChatColor.valueOf(colorString.toUpperCase());
            petSection.set("name.color", color.name());
            Main.getPlugin().saveConfig();
            if (petSection.getBoolean("isSpawned")) updatePet(owner);
        } catch (IllegalArgumentException e) {
            //SEND ERROR
            PlayerUtils.sendCustomError(owner, "This Color is not valid.");
        }
    }

    public static void setPetName(Player owner, String name) {
        //VARIABLES
        ConfigurationSection petSection = Main.getPlugin().getConfig().getConfigurationSection(owner.getName() + ".pet");
        //IFs
        if (petSection == null) Main.getPlugin().getConfig().createSection(owner.getName() + ".pet");
        assert petSection != null;
        //CONFIG ENTRYS
        petSection.set("name.name", name);
        Main.getPlugin().saveConfig();
        if (petSection.getBoolean("isSpawned")) updatePet(owner);
    }

    public static void handleSpeed(Player owner, int speed) {
        ConfigurationSection petSection = Main.getPlugin().getConfig().getConfigurationSection(owner.getName() + ".pet");
        assert petSection != null;
        double finalSpeed;
        switch (speed) {
            case 1:
                finalSpeed = 0.5;
                break;
            case 2:
                finalSpeed = 0.75;
                break;
            case 3:
                finalSpeed = 1;
                break;
            case 4:
                finalSpeed = 1.25;
                break;
            case 5:
                finalSpeed = 1.5;
                break;
            case 6:
                finalSpeed = 1.75;
                break;
            case 7:
                finalSpeed = 2;
                break;
            case 8:
                finalSpeed = 2.25;
                break;
            case 9:
                finalSpeed = 2.5;
                break;
            default:
                PlayerUtils.sendCustomError(owner, "Du musst eine Zahl zwischen 1 und 9 angeben");
                return;
        }
        petSection.set("walkspeed", finalSpeed);
        Main.getPlugin().saveConfig();
        owner.sendMessage(colorinfo + serverprefix + "Die Geschwindigkeit deines Pets wurde auf " + speed + " gesetzt.");
        owner.closeInventory();
        if (petSection.getBoolean("isSpawned")) {
            updatePet(owner);
        }
    }

    public static void handlePetSpawnItem(Player owner, ItemStack spawnItem) {
        if (Objects.requireNonNull(spawnItem).getType() == Material.SHEEP_SPAWN_EGG) {
            spawnPet(owner);
            owner.sendMessage(colorinfo + serverprefix + "Du hast dein Pet gespawnt.");
        } else {
            despawnPet(owner);
            owner.sendMessage(colorinfo + serverprefix + "Du hast dein Pet despawnt.");
        }

    }

    public static void setAge(Player player, String age) {
        ConfigurationSection petSection = config.getConfigurationSection(player.getName() + ".pet");
        if (!age.equalsIgnoreCase("baby") && !age.equalsIgnoreCase("adult")) {
            PlayerUtils.sendSyntaxError(player, "/pet");
            return;
        }
        assert petSection != null;
        petSection.set("age", age.toLowerCase());
        Main.getPlugin().saveConfig();
        player.sendMessage(colorinfo + serverprefix + "Dein Pet ist jetzt ein " + age.toLowerCase());
        if (petSection.getBoolean("isSpawned")) updatePet(player);
    }

    public static void setPetSectionEntry(Player owner, String entry, Object value) {
        ConfigurationSection petSection = config.getConfigurationSection(owner.getName() + ".pet");
        if (petSection == null) return;
        petSection.set(entry, value);
        Main.getPlugin().saveConfig();
    }

    //GET THINGS
    public static LivingEntity getPet(Player owner) {
        if (!config.contains(owner.getName() + ".pet.uuid")) return null;
        return (LivingEntity) Bukkit.getEntity(UUID.fromString(Objects.requireNonNull(config.getString(owner.getName() + ".pet.uuid"))));
    }

    public static LivingEntity getPassengerPet(Player owner) {
        if (!config.contains(owner.getName() + ".pet.others.passengerUUID")) return null;
        return (LivingEntity) Bukkit.getEntity(UUID.fromString(Objects.requireNonNull(config.getString(owner.getName() + ".pet.others.passengerUUID"))));
    }

    public static String getPetName(Player owner) {
        ConfigurationSection petSection = Main.getPlugin().getConfig().getConfigurationSection(owner.getName() + ".pet");
        if (petSection == null) Main.getPlugin().getConfig().createSection(owner.getName() + ".pet");
        assert petSection != null;
        return petSection.getString("name.name");
    }

    public static ChatColor getPetNameColor(Player owner) {
        //VARIABLES
        ConfigurationSection petSection = Main.getPlugin().getConfig().getConfigurationSection(owner.getName() + ".pet");
        assert petSection != null;
        try {
            //RETURN COLOR
            return ChatColor.valueOf(petSection.getString("name.color"));
        } catch (IllegalArgumentException | NullPointerException e) {
            return ChatColor.GREEN;
        }
    }

    public static Object getPetSectionEntry(Player owner, String entry) {
        ConfigurationSection petSection = Main.getPlugin().getConfig().getConfigurationSection(owner.getName() + ".pet");
        if (petSection == null) return null;
        return petSection.get(entry);
    }

    //OPEN THINGS
    public static void openAgeInventory(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, colorinfo + "PetMenu: Set Age");
        InterfaceUtils.fillPlaceholders(inv, ItemStacks.placeholder);
        inv.setItem(11, ItemStacks.babyAge);
        inv.setItem(15, ItemStacks.adultAge);
        inv.setItem(22, ItemStacks.arrowUp);
        player.openInventory(inv);
        PlayerUtils.pinInventoryContents(player);
    }

    public static void openSpeedInventory(Player player) {
        Inventory inv = Bukkit.createInventory(null, 9, colorinfo + "PetMenu: Set Follow Speed");
        inv.setItem(0, ItemStacks.petSpeed1);
        inv.setItem(1, ItemStacks.petSpeed2);
        inv.setItem(2, ItemStacks.petSpeed3);
        inv.setItem(3, ItemStacks.petSpeed4);
        inv.setItem(4, ItemStacks.petSpeed5);
        inv.setItem(5, ItemStacks.petSpeed6);
        inv.setItem(6, ItemStacks.petSpeed7);
        inv.setItem(7, ItemStacks.petSpeed8);
        inv.setItem(8, ItemStacks.petSpeed9);
        player.openInventory(inv);
        PlayerUtils.pinInventoryContents(player);
    }

    public static void openNextMainPage(Player player) {
        ConfigurationSection petSection = Main.getPlugin().getConfig().getConfigurationSection(player.getName() + ".pet");
        Inventory inv = Bukkit.createInventory(null, 27, colorinfo + "PetMenu: Home (2/2)");
        InterfaceUtils.fillPlaceholders(inv, ItemStacks.placeholder);
        InterfaceUtils.setMainContents(inv, ItemStacks.petSitDown, ItemStacks.renamePet, ItemStacks.petSilentBell, ItemStacks.petOthersPassenger);
        inv.setItem(18, ItemStacks.arrowBack);
        //SET SPAWN/DESPAWN ITEM
        assert petSection != null;
        if (petSection.getBoolean("isSpawned")) {
            inv.setItem(22, ItemStacks.despawnPet);
        } else {
            inv.setItem(22, ItemStacks.spawnPet);
        }
        player.openInventory(inv);
        PlayerUtils.pinInventoryContents(player);
    }

    public static void openMainPage(Player player) {
        //INVENTORY
        ConfigurationSection petSection = Main.getPlugin().getConfig().getConfigurationSection(player.getName() + ".pet");
        Inventory inv = Bukkit.createInventory(null, 27, colorinfo + "PetMenu: Home (1/2)");
        InterfaceUtils.fillPlaceholders(inv, ItemStacks.placeholder);
        inv.setItem(26, ItemStacks.arrowNext);

        //SET CONTENTS
        assert petSection != null;
        if (petSection.contains("MHFType")) {
            String mhfType = petSection.getString("MHFType");
            ItemStack typeHead = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta sm = (SkullMeta) typeHead.getItemMeta();
            assert sm != null;
            sm.setOwner(mhfType);
            sm.setDisplayName(ChatColor.GOLD + "Set Type");
            typeHead.setItemMeta(sm);
            InterfaceUtils.setMainContents(inv, ItemStacks.teleportPet, typeHead, ItemStacks.petSetSpeed, ItemStacks.selectPetSize);
        } else
            InterfaceUtils.setMainContents(inv, ItemStacks.teleportPet, ItemStacks.selectPetType, ItemStacks.petSetSpeed, ItemStacks.selectPetSize);

        //SET SPAWN/DESPAWN ITEM
        if (petSection.getBoolean("isSpawned")) {
            inv.setItem(22, ItemStacks.despawnPet);
        } else {
            inv.setItem(22, ItemStacks.spawnPet);
        }
        //OPEN INVENTORY
        player.openInventory(inv);
        PlayerUtils.pinInventoryContents(player);
    }

    public static void openOthersInventory(Player player) {
        Inventory inv = Bukkit.createInventory(null, 9, colorinfo + "PetMenu: Others");
        inv.setItem(0, ItemStacks.petOthersOnFire);
        inv.setItem(1, ItemStacks.petOthersJumpBoost);
        inv.setItem(2, ItemStacks.petOthersParticle);
        inv.setItem(3, ItemStacks.petOthersItemInHand);
        inv.setItem(4, ItemStacks.petOthersSaddle);
        inv.setItem(5, ItemStacks.petOthersPassenger);
        inv.setItem(6, ItemStacks.petOthersFeed);
        inv.setItem(7, ItemStacks.petOthersEmpty);
        inv.setItem(8, ItemStacks.petOthersEmpty);

        player.openInventory(inv);
        PlayerUtils.pinInventoryContents(player);
    }

    public static Entity createPet(Player p) {
        ConfigurationSection petSection = config.getConfigurationSection(p.getName() + ".pet");
        //SPAWN "PET"
        assert petSection != null;
        String stringType = petSection.getString("type");
        EntityType type = EntityType.PIG;
        if (petSection.contains("type")) {
            try {
                type = EntityType.valueOf(stringType);
            } catch (IllegalArgumentException ignored) {}
        }
        Entity pet = p.getWorld().spawnEntity(p.getLocation().add(0, 0, 1), type);
        pet.setInvulnerable(true);
        pet.setPortalCooldown(Integer.MAX_VALUE);
        if (pet instanceof Breedable) ((Breedable) pet).setAgeLock(true);
        //name
        String name = p.getName() + "'s Pet";
        ChatColor nameColor = ChatColor.GREEN;
        pet.setCustomNameVisible(true);
        if (getPetName(p) != null) name = getPetName(p);
        if (getPetNameColor(p) != null) nameColor = getPetNameColor(p);
        pet.setCustomName(nameColor + name);
        return pet;
    }

    public static void customizePet(LivingEntity pet, Player owner) {
        ConfigurationSection petSection = config.getConfigurationSection(owner.getName() + ".pet");
        if (petSection == null) config.createSection(owner.getName() + ".pet");
        assert petSection != null;
        if (petSection.contains("age") && pet instanceof Ageable) {
            Ageable ageablePet = (Ageable) pet;
            String age = petSection.getString("age");
            assert age != null;
            if (age.equalsIgnoreCase("baby")) ageablePet.setBaby();
            else if (age.equalsIgnoreCase("adult")) ageablePet.setAdult();
        }
        if (petSection.getBoolean("silent")) pet.setSilent(true);
        else pet.setSilent(false);
        if (petSection.getBoolean("others.onFire")) pet.setFireTicks(Integer.MAX_VALUE);
        if (petSection.getBoolean("others.jumpBoost")) pet.addPotionEffect(PotionEffectType.JUMP.createEffect(99999, 10));
        else pet.removePotionEffect(PotionEffectType.JUMP);
        if (petSection.getBoolean("others.particles")) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!petSection.getBoolean("others.particles")) cancel();
                    pet.getWorld().spawnParticle(Particle.FLAME, pet.getLocation(), 10, 0, 0, 0);
                    pet.getWorld().spawnParticle(Particle.HEART, pet.getLocation(), 10, 0.5, 0.5, 0.5);
                    pet.getWorld().spawnParticle(Particle.PORTAL, pet.getLocation(), 30, 0.5, 0.5, 0.5);
                }
            }.runTaskTimer(Main.getPlugin(), 0, 21);
        }
        addPassenger(owner, pet);
    }
}
