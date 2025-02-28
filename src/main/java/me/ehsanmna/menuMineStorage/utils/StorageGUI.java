package me.ehsanmna.menuMineStorage.utils;

import me.ehsanmna.menuMineStorage.MenuMineStorage;
import me.ehsanmna.menumine.Managers.ItemWrapper;
import me.ehsanmna.menumine.Managers.MenuAction;
import me.ehsanmna.menumine.Managers.MenuManager;
import me.ehsanmna.menumine.MenuMine;
import me.ehsanmna.menumine.models.Action;
import me.ehsanmna.menumine.models.MenuModel;
import me.ehsanmna.menumine.nbt.NBTItem;
import me.ehsanmna.menumine.nbt.NBTItemManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

public class StorageGUI {

    private static MenuMineStorage plugin;
    private static YamlConfiguration guiConfig;
    private static MenuModel storageMenu;

    public static void setup(MenuMineStorage pluginInstance) {
        plugin = pluginInstance;
        File guiFile = new File(plugin.getDataFolder(), "gui.yml");
        try {if (guiFile.createNewFile()) MenuMineStorage.getInstance().saveResource("gui.yml",true);
        } catch (IOException e) {throw new RuntimeException(e);}
        guiConfig = YamlConfiguration.loadConfiguration(guiFile);
        loadGUIModel();
    }

    private static void loadGUIModel() {
        storageMenu = new MenuModel();
        int rows = guiConfig.getInt("rows", 3); // Default to 3 rows (27 slots)
        String title = ChatColor.translateAlternateColorCodes('&', guiConfig.getString("title", "&6Storage Menu"));

        Inventory inv = Bukkit.createInventory(null, rows * 9, title);
        storageMenu.setInv(inv);
        storageMenu.setId("storage_menu");
        storageMenu.setName("storage_menu");
        storageMenu.setDisplayName(title);
        storageMenu.setCopy(true); // Allow dynamic updates per player

        MenuModel.addModel("storage_menu", storageMenu);
    }

    public static void openGUI(Player player) {
        MenuModel clonedMenu = (MenuModel) storageMenu.clone();
        Inventory inv = clonedMenu.getInv();

        int slot = 0;
        for (String storageId : plugin.getStorageManager().getDefinedStorages().keySet()) {
            if (slot >= inv.getSize()) break;

            ItemStack item = createStorageItem(player, storageId);
            NBTItem nbt = NBTItemManager.createNBTItem(item);
            nbt.setTag("MenuItem", true); // Required for Listeners to detect
            nbt.setTag("MenuModel", "storage_menu"); // Link to this model
            nbt.save();
            inv.setItem(slot, nbt.getItem());

            MenuAction openAction = new MenuAction(Action.COMMAND, "storage " + storageId);
            clonedMenu.addAction(slot, openAction);

            slot++;
        }

        MenuManager.openModel(clonedMenu, player, Collections.emptyList());
    }

    private static ItemStack createStorageItem(Player player, String storageId) {
        boolean canAccess = plugin.getStorageManager().canAccessStorage(player, storageId);
        String configPath = canAccess ? "items.unlocked" : "items.locked";
        YamlConfiguration config = guiConfig;

        ItemStack item = ItemWrapper.wrapItem(config.getConfigurationSection(configPath));
        ItemMeta meta = item.getItemMeta();
        String name = config.getString(configPath + ".name")
                .replace("{id}", storageId)
                .replace("{size}", String.valueOf(plugin.getStorageManager().getDefinedStorages().get(storageId).size()));
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));

        java.util.List<String> lore = new java.util.ArrayList<>();
        for (String line : config.getStringList(configPath + ".lore")) {
            lore.add(ChatColor.translateAlternateColorCodes('&', line
                    .replace("{id}", storageId)
                    .replace("{size}", String.valueOf(plugin.getStorageManager().getDefinedStorages().get(storageId).size()))
                    .replace("{permission}", plugin.getStorageManager().getDefinedStorages().get(storageId).permission())));
        }
        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
    }
}
