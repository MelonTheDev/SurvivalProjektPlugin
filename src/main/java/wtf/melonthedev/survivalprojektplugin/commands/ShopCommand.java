package wtf.melonthedev.survivalprojektplugin.commands;

import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import wtf.melonthedev.survivalprojektplugin.Main;
import wtf.melonthedev.survivalprojektplugin.others.Config;
import wtf.melonthedev.survivalprojektplugin.others.NPC;
import wtf.melonthedev.survivalprojektplugin.gui.ItemStacks;
import wtf.melonthedev.survivalprojektplugin.utils.*;

import java.util.*;
import static wtf.melonthedev.survivalprojektplugin.Main.*;
import static wtf.melonthedev.survivalprojektplugin.gui.ItemStacks.*;

public class ShopCommand implements CommandExecutor, TabCompleter, Listener {
    FileConfiguration config = Main.getPlugin().getConfig();
    FileConfiguration settings = Config.getCustomConfig("settings.yml");
    String currency = "DIAMOND";
    String shopkeeperName;
    Player p;
    int emeraldPrice;
    int diamondPrice;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (CommandUtils.isDisabled(sender, "shop")) return true;
        if (!PlayerUtils.isPlayer(sender)) return true;
        p = (Player) sender;
        if (args.length != 1) {
            p.sendMessage(colorerror + serverprefix + "Syntaxerror: /shop <help/entrys/add/remove/Player: string>");
            return true;
        }

        if (args[0].equalsIgnoreCase("add")) {
            config.set(p.getName() + ".shop.shopkeeper", p.getName());
            Main.getPlugin().saveConfig();
            Inventory sell = Bukkit.createInventory(p, 27, colorinfo + "Shop: Sell Item");
            fillInventory(sell, "sell", p);
            p.openInventory(sell);
            pinInventoryContents(p);
            return true;
        } else if (args[0].equalsIgnoreCase("remove")) {
            config.set(p.getName() + ".shop.shopkeeper", p.getName());
            Main.getPlugin().saveConfig();
            Inventory removeInv = Bukkit.createInventory(p, 27, colorinfo + "Shop: Remove Item");
            fillInventory(removeInv, "remove", p);
            p.openInventory(removeInv);
            pinInventoryContents(p);
            return true;
        } else if (args[0].equalsIgnoreCase("prices") || args[0].equalsIgnoreCase("help")) {
            if (settings.getBoolean("settings.advanced.advancedshopitems")) {
                p.sendMessage(colorinfo + "----------Shoppy Shop Command v.1.0----------\n/shop addMending: \n" + colorsecondinfo + "    price: 128 emeraldos;\n    description: Adds mending to your hands item;(ikmyenglishisbad)\n" + colorinfo + "/shop knockbackslime: \n" + colorsecondinfo + "    price: 16 emeraldos;\n    description: Gives you a knockback V slime-ball;\n" + colorinfo + "/shop health: \n" + colorsecondinfo + "    price: 2 emeraldos;\n    description: Health you full;\n" + colorinfo + "/shop help bzw. price: \n" + colorsecondinfo + "    price: FREE;\n    description: Shows this menu;\n" + colorinfo + "/shop PlayerName: \n" + colorsecondinfo + "    price: FREE;\n    description: Shows the shop of the specific Player if exist;\n" + colorinfo + "---------------------------------------------");
                return true;
            }
            p.sendMessage(colorinfo + "----------Shoppy Shop Command v.1.0----------\n/shop PlayerName: \n" + colorsecondinfo + "    price: FREE;\n    description: Shows the shop of the specific Player if exist;\n" + colorinfo + "---------------------------------------------");
            return true;
        } else if (args[0].equalsIgnoreCase("setShopkeeper")) {
            if (config.get(p.getName() + ".shop.hasShopkeeperNPC") != null && config.getBoolean(p.getName() + ".shop.hasShopkeeperNPC")) {
                NPC.teleportNPC(ChatColor.AQUA + "Shopkeeper", p);
                p.sendMessage(colorinfo + serverprefix + "Dein persönlicher Shopkeeper wurde zu dir Teleportiert.");
                return true;
            }
            config.set(p.getName() + ".shop.hasShopkeeperNPC", true);
            Main.getPlugin().saveConfig();
            NPC.createNpc(p, p.getName(), colorinfo + "Shopkeeper");
            p.sendMessage(colorinfo + serverprefix + "Dein persönlicher Shopkeeper wurde erstellt.");
            return true;
        } else if (args[0].equalsIgnoreCase("addmending") || args[0].equalsIgnoreCase("health") || args[0].equalsIgnoreCase("knockbackSlime")) {
            if (!settings.getBoolean("settings.advanced.advancedshopitems")) {
                p.sendMessage(colorerror + serverprefix + "Dieser Command ist für diesen Server disabled.");
                return true;
            }
            switch (args[0]) {
                case "addmending":
                case "addMending":
                    if (currencyCounter(128, Material.EMERALD, p)) {
                        ItemStack mainhand = new ItemStack(Objects.requireNonNull(p.getInventory()).getItemInMainHand());
                        if (mainhand.getType() == Material.AIR) {
                            p.sendMessage(colorerror + serverprefix + "Du musst ein Item in deiner Hand haben.");
                            p.getInventory().addItem(new ItemStack(Material.EMERALD, 128));
                            return true;
                        }
                        mainhand.addUnsafeEnchantment(Enchantment.MENDING, 1);
                        p.getInventory().setItemInMainHand(mainhand);
                        p.sendMessage(colorinfo + serverprefix + "Du hast Mending für 2 Stacks Emeralds auf den Gegenstand '" + mainhand.getType().toString().toLowerCase().replace('_', ' ') + "' hinzugefügt.");
                        return true;
                    }
                    break;
                case "health":
                    if (currencyCounter(2, Material.EMERALD, p)) {
                        p.setHealth(20);
                        p.sendMessage(colorinfo + serverprefix + "Du hast dich für 2 Emeralds vollgeheilt.");
                        return true;
                    }
                    break;
                case "knockbackslime":
                case "knockbackSlime":
                    if (currencyCounter(16, Material.EMERALD, p)) {
                        ItemStack slime = new ItemStack(Material.SLIME_BALL);
                        ItemMeta im = slime.getItemMeta();
                        assert im != null;
                        im.setDisplayName(ChatColor.GREEN + "KnockbackSlime");
                        im.setLore(Collections.singletonList("Lustiger Slimeball, der andere Spieler so lustig rumbounzen lässt :)"));
                        slime.setItemMeta(im);
                        slime.addUnsafeEnchantment(Enchantment.KNOCKBACK, 5);
                        p.getInventory().addItem(slime);
                        p.sendMessage(colorinfo + serverprefix + "Du hast für 16 Emeraldos einen KnockbackSlime gekauft.");
                        return true;
                    }
                    break;
            }
            return true;
        }

        for (Player shopkeeper : Bukkit.getOnlinePlayers()) {
            //if (!p.isOp() || !shopkeeper.getName().equals(p.getName())) break;
            if (args[0].equalsIgnoreCase(shopkeeper.getName())) {
                this.shopkeeperName = shopkeeper.getName();
                config.set(p.getName() + ".shop.shopkeeper", shopkeeper.getName());
                config.set(p.getName() + ".shop.selectedPage", 1);
                Main.getPlugin().saveConfig();
                Inventory inv = Bukkit.createInventory(null, 27, colorinfo + "Shop: Overview");
                fillInventory(inv, "shopOverview", shopkeeper);
                p.openInventory(inv);
                pinInventoryContents(p);
                p.sendMessage(colorinfo + serverprefix + "Du hast den Shop von " + shopkeeper.getName() + " geöffnet!");
                return true;
            }
        }
        for (OfflinePlayer offlineshopkeeper : Bukkit.getOfflinePlayers()) {
            //if (!p.isOp() || !Objects.equals(offlineshopkeeper.getName(), p.getName())) break;
            if (args[0].equalsIgnoreCase(offlineshopkeeper.getName())) {
                this.shopkeeperName = offlineshopkeeper.getName();
                config.set(p.getName() + ".shop.shopkeeper", offlineshopkeeper.getName());
                config.set(p.getName() + ".shop.selectedPage", 1);
                Main.getPlugin().saveConfig();
                Inventory inv = Bukkit.createInventory(null, 27, colorinfo + "Shop: Overview");
                fillInventory(inv, "shopOverview", offlineshopkeeper.getPlayer());
                p.openInventory(inv);
                pinInventoryContents(p);
                p.sendMessage(colorinfo + serverprefix + "Du hast den Shop von " + offlineshopkeeper.getName() + " geöffnet!");
                return true;
            }
        }
        p.sendMessage(colorerror + serverprefix + "Syntaxerror: /shop <help/entrys/add/remove/Player: string>");
        System.out.println("debug");
        return false;
    }



    @EventHandler
    public void onInvClick(InventoryClickEvent e) {
        p = (Player) e.getWhoClicked();
        if (!(e.getView().getTitle().startsWith(colorinfo + "Shop: "))) {
            return;
        }
        if (e.getClickedInventory() == null) {
            return;
        }
        if (config.get(p.getName() + ".shop.shopkeeper") == null) {
            return;
        }
        Player shopkeeper = Bukkit.getPlayer(Objects.requireNonNull(config.getString(p.getName() + ".shop.shopkeeper")));
        int slot = e.getSlot();
        Inventory inv = e.getInventory();
        String title = e.getView().getTitle();
        ItemStack currentItem = e.getCurrentItem();
        ItemStacks.initializeShopkeeperInfos(shopkeeper);

        if (e.getClickedInventory().getType().equals(InventoryType.PLAYER)) {
            if (!(e.getView().getTitle().equals(colorinfo + "Shop: Sell Item"))) {
                return;
            }
            if (currentItem == null) {
                return;
            }
            inv.setItem(4, currentItem);
            return;
        }
        if (title.equals(colorinfo + "Shop: Overview")) {
            onShopItemClick(slot, currentItem, shopkeeper, inv);
        } else if (title.equals(colorinfo + "Shop: Buy Item")) {
            if (slot == 0) {
                this.shopkeeperName = config.getString(p.getName() + ".shop.shopkeeper");
                Inventory overview = Bukkit.createInventory(null, 27, colorinfo + "Shop: Overview");
                fillInventory(overview, "shopOverview", shopkeeper);
                p.openInventory(overview);
                pinInventoryContents(p);
            } else if (slot == 7) {
                assert currentItem != null;
                setCurrency(currentItem, Objects.requireNonNull(inv.getItem(8)), "DIAMOND");
                inv.setItem(13, new ItemStack(Objects.requireNonNull(Material.getMaterial(currency)), diamondPrice));
            } else if (slot == 8) {
                assert currentItem != null;
                setCurrency(currentItem, Objects.requireNonNull(inv.getItem(7)), "EMERALD");
                inv.setItem(13, new ItemStack(Objects.requireNonNull(Material.getMaterial(currency)), emeraldPrice));
            } else if (slot == 26) {
                p.sendMessage("BUY");
            }
        } else if (title.equals(colorinfo + "Shop: Sell Item")) {
            if (slot == 0) {
                handleCurrencyCounter("-", inv.getItem(1));
            } else if (slot == 2) {
                handleCurrencyCounter("+", inv.getItem(1));
            } else if (slot == 4) {
                if (Objects.requireNonNull(inv.getItem(4)).isSimilar(emptySlot)) return;
                inv.setItem(4, emptySlot);
            } else if (slot == 6) {
                handleCurrencyCounter("-", inv.getItem(7));
            } else if (slot == 8) {
                handleCurrencyCounter("+", inv.getItem(7));
            } else if (slot == 25) {
                p.closeInventory();
            } else if (slot == 26) {
                if (Objects.requireNonNull(inv.getItem(4)).isSimilar(emptySlot)) {
                    p.sendMessage(colorerror + serverprefix + "Du musst ein Item einstellen.");
                    return;
                }
                saveItemToConfig(p, inv.getItem(4), Objects.requireNonNull(inv.getItem(1)).getAmount(), Objects.requireNonNull(inv.getItem(7)).getAmount());
                p.getInventory().removeItem(inv.getItem(4));
                p.closeInventory();
                p.sendMessage(colorinfo + serverprefix + "Du hast dein/e Item/s erfolgreich in den Shop gestellt.");
            }
        } else if (title.equals(colorinfo + "Shop: Remove Item")) {

            List<Integer> itemSlots = new ArrayList<>();
            itemSlots.add(10);
            itemSlots.add(12);
            itemSlots.add(14);
            itemSlots.add(16);

            assert shopkeeper != null;
            int selectedPage = config.getInt(shopkeeper.getName() + ".shop.selectedPage");
            int items = config.getInt(p.getName() + ".shop.items");
            int pageitems = config.getInt(p.getName() + ".shop.pageitems");
            switch (slot) {
                case 10:
                case 12:
                case 14:
                case 16:
                    if (currentItem == null) return;
                    if (currentItem.isSimilar(emptySlot)) {
                        p.sendMessage(colorerror + serverprefix + "Du musst ein Item auswählen, was du verkaufst.");
                        return;
                    }
                    for (int i : itemSlots) {
                        ItemStack item = inv.getItem(i);
                        Objects.requireNonNull(item).removeEnchantment(Enchantment.VANISHING_CURSE);
                        ItemMeta im = Objects.requireNonNull(item).getItemMeta();
                        assert im != null;
                        List<String> lore = im.getLore();
                        if (lore == null) continue;
                        lore.remove("You've selected this Item!");
                        im.setLore(lore);
                        Objects.requireNonNull(item).setItemMeta(im);
                    }
                    currentItem.addUnsafeEnchantment(Enchantment.VANISHING_CURSE, 5);
                    ItemMeta im = currentItem.getItemMeta();
                    assert im != null;
                    List<String> lore = im.getLore();
                    assert lore != null;
                    lore.add("You've selected this Item!");
                    im.setLore(lore);
                    currentItem.setItemMeta(im);
                    config.set(p.getName() + ".shop.slottoremove", slot);
                    Main.getPlugin().saveConfig();
                    break;
                case 22:
                    if (!config.contains(p.getName() + ".shop.slottoremove")) {
                        p.sendMessage(colorerror + serverprefix + "Du musst ein Item auswählen");
                        return;
                    }
                    int slottoremove = config.getInt(p.getName() + ".shop.slottoremove");
                    config.set(p.getName() + ".shop.slottoremove", null);
                    int pageItem;
                    switch (slottoremove) {
                        case 10:
                            pageItem = 0;
                            break;
                        case 12:
                            pageItem = 1;
                            break;
                        case 14:
                            pageItem = 2;
                            break;
                        case 16:
                            pageItem = 3;
                            break;
                        default:
                            p.sendMessage(colorerror + serverprefix + "Es ist ein interner Fehler aufgetreten. Dieser Gegenstand kann nicht gelöscht werden.");
                            return;
                    }

                    ItemStack item = config.getItemStack(p.getName() + ".shop.entrys." + selectedPage + "." + pageItem + ".itemstack");
                    if (item == null) {
                        p.sendMessage(colorerror + serverprefix + "Es ist ein interner Fehler aufgetreten. Item kann nicht zurückerstattet werden.");
                        return;
                    }
                    ItemMeta meta = item.getItemMeta();
                    assert meta != null;
                    if (meta.hasLore()) {
                        meta.setLore(null);
                    }
                    item.setItemMeta(meta);
                    p.getInventory().addItem(item);

                    //int entrysINT = config.getConfigurationSection(shopkeeper.getName() + ".shop.entrys").getKeys(false);
                    ConfigurationSection entrys = config.getConfigurationSection(shopkeeper.getName() + ".shop.entrys");
                    assert entrys != null;
                    for (String key : entrys.getKeys(false)) {
                        config.set(shopkeeper.getName() + ".shop.entrys." + (Integer.parseInt(key) - 1), config.get(shopkeeper.getName() + ".shop.entrys." + key));
                        config.set(shopkeeper.getName() + ".shop.entrys." + key, null);
                    }

                    config.set(p.getName() + ".shop.entrys." + selectedPage + "." + pageItem, null);
                    config.set(p.getName() + ".shop.items", items - 1);
                    config.set(p.getName() + ".shop.pages", getPages(items - 1));
                    if (pageitems > 0)
                        config.set(p.getName() + ".shop.pageitems", pageitems - 1);
                    Main.getPlugin().saveConfig();
                    p.sendMessage(colorinfo + serverprefix + "Du hast Item " + (pageItem + 1) + " von Seite " + selectedPage + " entfernt.");
                    p.closeInventory();
                    break;
                case 18:
                    assert currentItem != null;
                    if (currentItem.isSimilar(arrowBack)) {
                        selectedPage = selectedPage - 1;
                        if (selectedPage >= 1) {
                            config.set(shopkeeper.getName() + ".shop.selectedPage", selectedPage);
                            Main.getPlugin().saveConfig();
                            this.shopkeeperName = shopkeeper.getName();
                            loadEntrys(e.getInventory());
                        }
                    }
                    break;
                case 26:
                    assert currentItem != null;
                    if (currentItem.isSimilar(arrowNext)) {
                        selectedPage = selectedPage + 1;
                        if (selectedPage <= config.getInt(shopkeeper.getName() + ".shop.pages")) {
                            config.set(shopkeeper.getName() + ".shop.selectedPage", selectedPage);
                            Main.getPlugin().saveConfig();
                            this.shopkeeperName = shopkeeper.getName();
                            loadEntrys(e.getInventory());
                        }
                    }
                    break;
            }
        }
    }




    public void onShopItemClick(int slot, ItemStack currentItem, Player shopkeeper, Inventory inv) {
        switch (slot) {
            case 10:
            case 12:
            case 14:
            case 16:
                assert currentItem != null;
                if (currentItem.isSimilar(emptySlot)) return;
                List<String> lore = Objects.requireNonNull(currentItem.getItemMeta()).getLore();
                assert lore != null;
                int emeraldPrice = Integer.parseInt(lore.get(1).split(": ")[1]);
                int diamondPrice = Integer.parseInt(lore.get(2).split(": ")[1]);
                openBuyItemInterface(currentItem, shopkeeper, p, emeraldPrice, diamondPrice);
                break;
            case 18:
                assert currentItem != null;
                if (currentItem.isSimilar(arrowBack)) {
                    assert shopkeeper != null;
                    int selectedPage = config.getInt(shopkeeper.getName() + ".shop.selectedPage") - 1;
                    if (selectedPage >= 1) {
                        config.set(shopkeeper.getName() + ".shop.selectedPage", selectedPage);
                        Main.getPlugin().saveConfig();
                        this.shopkeeperName = config.getString(p.getName() + ".shop.shopkeeper");
                        loadEntrys(inv);
                    }
                }
                break;
            case 26:
                assert currentItem != null;
                if (currentItem.isSimilar(arrowNext)) {
                    assert shopkeeper != null;
                    int selectedPage = config.getInt(shopkeeper.getName() + ".shop.selectedPage") + 1;
                    if (selectedPage <= config.getInt(shopkeeper.getName() + ".shop.pages")) {
                        config.set(shopkeeper.getName() + ".shop.selectedPage", selectedPage);
                        Main.getPlugin().saveConfig();
                        this.shopkeeperName = config.getString(p.getName() + ".shop.shopkeeper");
                        loadEntrys(inv);
                    }
                }
                break;
        }
    }

    public void fillInventory(Inventory inv, String type, Player shopkeeper) {
        ItemStacks.initializeShopkeeperInfos(shopkeeper);
        for (int i = 0; i < inv.getContents().length; i++) {
            inv.setItem(i, placeholder);
        }
        switch (type) {
            case "buy":
                inv.setItem(0, leaveShop);
                inv.setItem(1, playerinfos);
                inv.setItem(2, blackplaceholder);
                inv.setItem(4, emptySlot);
                inv.setItem(6, blackplaceholder);
                inv.setItem(7, currencyDiamond);
                inv.setItem(8, currencyEmerald);
                inv.setItem(9, blackplaceholder);
                inv.setItem(10, blackplaceholder);
                inv.setItem(11, blackplaceholder);
                inv.setItem(13, new ItemStack(Objects.requireNonNull(Material.getMaterial(currency)), diamondPrice));
                inv.setItem(15, blackplaceholder);
                inv.setItem(16, blackplaceholder);
                inv.setItem(17, blackplaceholder);
                inv.setItem(26, buyItem);
                break;
            case "sell":
                inv.setItem(0, minus);
                inv.setItem(1, priceInEmeralds);
                inv.setItem(2, plus);
                inv.setItem(3, blueplaceholder);
                inv.setItem(4, emptySlot);
                inv.setItem(5, blueplaceholder);
                inv.setItem(6, minus);
                inv.setItem(7, priceInDiamonds);
                inv.setItem(8, plus);
                inv.setItem(9, blackplaceholder);
                inv.setItem(10, blackplaceholder);
                inv.setItem(11, blackplaceholder);
                inv.setItem(12, blueplaceholder);
                inv.setItem(13, blueplaceholder);
                inv.setItem(14, blueplaceholder);
                inv.setItem(15, blackplaceholder);
                inv.setItem(16, blackplaceholder);
                inv.setItem(17, blackplaceholder);
                inv.setItem(25, cancel);
                inv.setItem(26, create);
                break;
            case "shopOverview":
                config.set(p.getName() + ".shop.selectedPage", 1);
                Main.getPlugin().saveConfig();
                loadEntrys(inv);
                break;
            case "remove":
                inv.setItem(0, removeInfo);
                loadEntrys(inv);
                inv.setItem(22, remove);
                break;
        }
    }
    public void loadEntrys(Inventory overview) {
        //VARIABLES
        int slot_playerhead = 4;
        int slot_1 = 10;
        int slot_2 = 12;
        int slot_3 = 14;
        int slot_4 = 16;
        int selectedPage = config.getInt(p.getName() + ".shop.selectedPage");
        int items = config.getInt(shopkeeperName + ".shop.items");
        int pages = getPages(items);

        //ADD NAVIGATE ARROWS TO SWITCH THE PAGE
        if (pages > 1) {
            if (selectedPage != 1) {
                overview.setItem(18, arrowBack);
            } else {
                overview.setItem(18, placeholder);
            }
            if (selectedPage != pages) {
                overview.setItem(26, arrowNext);
            } else {
                overview.setItem(26, placeholder);
            }
        }

        //LOAD ITEMS OUT OF CONFIG
        ItemStack[][] currentEntrys = new ItemStack[pages + 1][4];
        for (int i = 1; i <= pages; i++) {
            for (int y = 0; y < 4; y++) {
                ItemStack currentItem = config.getItemStack(shopkeeperName + ".shop.entrys." + i + "." + y + ".itemstack");
                if (currentItem == null) {
                    currentEntrys[i][y] = emptySlot;
                    continue;
                }
                ItemMeta im = currentItem.getItemMeta();
                assert im != null;
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.BLUE + "-------------------");
                lore.add(ChatColor.GREEN + "Kosten Emeralds: " + config.getInt(shopkeeperName + ".shop.entrys." + i + "." + y + ".emeraldprice"));
                lore.add(ChatColor.AQUA + "Kosten Diamonds: " + config.getInt(shopkeeperName + ".shop.entrys." + i + "." + y + ".diamondprice"));
                im.setLore(lore);
                currentItem.setItemMeta(im);
                currentEntrys[i][y] = currentItem;
            }
        }
        //SET THE ITEMS IN THE INVENTORY
        overview.setItem(slot_playerhead, playerinfos);
        overview.setItem(slot_1, currentEntrys[selectedPage][0]);
        overview.setItem(slot_2, currentEntrys[selectedPage][1]);
        overview.setItem(slot_3, currentEntrys[selectedPage][2]);
        overview.setItem(slot_4, currentEntrys[selectedPage][3]);
    }
    public void pinInventoryContents(Player p) {
        FileConfiguration config = Main.getPlugin().getConfig();
        config.set(p.getName() + ".Inv.isopen", true);
        Main.getPlugin().saveConfig();
    }
    public void handleCurrencyCounter(String operation, ItemStack count) {
        if (operation.equals("+")) {
            count.setAmount(count.getAmount() + 1);
            return;
        }
        if (operation.equals("-")) {
            if (count.getAmount() <= 1) {
                return;
            }
            count.setAmount(count.getAmount() - 1);
        }
    }
    public void saveItemToConfig(Player p, ItemStack item, int emeraldPrice, int diamondPrice) {
        if (!(config.contains(p.getName() + ".shop.items"))) {
            config.set(p.getName() + ".shop.items", 0);
        }
        if (!(config.contains(p.getName() + ".shop.pageitems"))) {
            config.set(p.getName() + ".shop.pageitems", 0);
        }
        config.set(p.getName() + ".shop.items", config.getInt(p.getName() + ".shop.items") + 1);
        if (config.getInt(p.getName() + ".shop.pageitems") > 3) {
            config.set(p.getName() + ".shop.pageitems", 0);
        }
        Main.getPlugin().saveConfig();

        int items = config.getInt(p.getName() + ".shop.items");
        int pageitems = config.getInt(p.getName() + ".shop.pageitems");
        int pages = getPages(items);
        config.set(p.getName() +".shop.pages", pages);
        p.sendMessage("Pages: " + pages);
        p.sendMessage("Items: " + items);

        config.set(p.getName() + ".shop.entrys." + pages + "." + pageitems + ".itemstack", item);
        config.set(p.getName() + ".shop.entrys." + pages + "." + pageitems + ".emeraldprice", emeraldPrice);
        config.set(p.getName() + ".shop.entrys." + pages + "." + pageitems + ".diamondprice", diamondPrice);
        config.set(p.getName() + ".shop.pageitems", config.getInt(p.getName() + ".shop.pageitems") + 1);
        Main.getPlugin().saveConfig();
    }
    public void setCurrency(ItemStack currentItem, ItemStack secondItem, String currencyString) {
        assert currentItem != null;
        currentItem.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1);
        ItemMeta im = currentItem.getItemMeta();
        assert im != null;
        im.setLore(Collections.singletonList(ChatColor.AQUA + "You've selected " + currencyString));
        currentItem.setItemMeta(im);
        assert secondItem != null;
        secondItem.removeEnchantment(Enchantment.ARROW_DAMAGE);
        im = secondItem.getItemMeta();
        assert im != null;
        im.setLore(null);
        secondItem.setItemMeta(im);
        currency = currencyString;
    }
    public void openBuyItemInterface(ItemStack item, Player shopkeeper, Player p, int itemPriceInEmeralds, int itemPriceInDiamonds) {
        if (item == null) {
            return;
        }
        if (item.isSimilar(emptySlot)) {
            return;
        }
        emeraldPrice = itemPriceInEmeralds;
        diamondPrice = itemPriceInDiamonds;
        Inventory inv = Bukkit.createInventory(null, 27, colorinfo + "Shop: Buy Item");
        fillInventory(inv, "buy", shopkeeper);
        inv.setItem(4, item);
        p.openInventory(inv);
        setCurrency(Objects.requireNonNull(inv.getItem(7)), Objects.requireNonNull(inv.getItem(8)), "DIAMOND");
        pinInventoryContents(p);
    }
    public static boolean currencyCounter(int count, Material currency, Player p) {
        int amount = 0;
        assert p != null;
        for (ItemStack is : p.getInventory().getContents()) {
            if (is == null) {
                continue;
            }
            if (is.getType() != currency) {
                continue;
            }
            for (int i = 0; i < is.getAmount(); i++) {
                amount++;
                if (amount == count) {
                    p.getInventory().removeItem(new ItemStack(currency, amount));
                    return true;
                }
            }
        }
        p.sendMessage(colorerror + serverprefix + "Du hast nicht genug " + currency.toString().toLowerCase() + "s. Es fehlen noch " + (count - amount) + ".");
        return false;
    }
    public int getPages(int items) {
        int pages;
        float pagesInFloat = (float) items / 4;
        if ((pagesInFloat - (int) pagesInFloat) != 0) pages = (int) pagesInFloat + 1;
        else pages = (int) pagesInFloat;
        if (pages == 0) pages = 1;
        return pages;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> tab = new ArrayList<>();
        tab.add("help");
        tab.add("add");
        tab.add("remove");
        tab.add("setShopkeeper");
        for (Player player : Bukkit.getOnlinePlayers()) {
            tab.add(player.getName());
        }
        return tab;
    }
}