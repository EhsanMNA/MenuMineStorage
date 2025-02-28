package me.ehsanmna.menuMineStorage.models;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class StorageInventory {

    private final String id;
    private final int size;
    private final Inventory inventory;

    public StorageInventory(String id, int size) {
        this.id = id;
        this.size = Math.min(Math.max(size, 9), 54); // Limit between 9 and 54
        this.inventory = Bukkit.createInventory(null, this.size, "Storage: " + id);
    }

    public StorageInventory(String id, int size, Inventory inventory) {
        this.id = id;
        this.size = size;
        this.inventory = inventory;
    }

    public ConfigurationSection serialize() {
        ConfigurationSection section = new YamlConfiguration();
        section.set("size", size);
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);
            if (item != null) {
                section.set("items." + i, item);
            }
        }
        return section;
    }

    public static Inventory deserialize(ConfigurationSection section) {
        int size = section.getInt("size");
        Inventory inv = Bukkit.createInventory(null, size, "Storage: " + section.getName());

        ConfigurationSection items = section.getConfigurationSection("items");
        if (items != null) {
            for (String key : items.getKeys(false)) {
                int slot = Integer.parseInt(key);
                ItemStack item = items.getItemStack(key);
                inv.setItem(slot, item);
            }
        }
        return inv;
    }

    public String getId() {
        return id;
    }

    public int getSize() {
        return size;
    }

    public Inventory getInventory() {
        return inventory;
    }
}
