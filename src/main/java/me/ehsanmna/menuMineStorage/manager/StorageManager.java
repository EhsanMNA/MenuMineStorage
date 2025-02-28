package me.ehsanmna.menuMineStorage.manager;

import me.ehsanmna.menuMineStorage.MenuMineStorage;
import me.ehsanmna.menuMineStorage.models.PlayerStorage;
import me.ehsanmna.menuMineStorage.models.StorageDefinition;
import me.ehsanmna.menuMineStorage.models.StorageInventory;
import org.bukkit.entity.Player;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class StorageManager {

    private final MenuMineStorage plugin;
    private final Map<UUID, PlayerStorage> playerStorages = new HashMap<>();
    private final Map<String, StorageDefinition> definedStorages = new HashMap<>();

    public StorageManager(MenuMineStorage plugin) {
        this.plugin = plugin;
        loadDefinedStorages();
    }

    private void loadDefinedStorages() {
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("storages");
        if (section != null) {
            for (String key : section.getKeys(false)) {
                int size = section.getInt(key + ".size");
                String permission = section.getString(key + ".permission", "storage." + key);
                definedStorages.put(key, new StorageDefinition(size, permission));
            }
        }
    }

    public void loadPlayerData(Player player) {
        File file = new File(plugin.getDataFolder(), "data/" + player.getUniqueId() + ".yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        PlayerStorage storage = new PlayerStorage(player);
        List<StorageInventory> inventories = new ArrayList<>();
        Set<String> unlocked = new HashSet<>(config.getStringList("unlocked"));

        for (Map.Entry<String, StorageDefinition> entry : definedStorages.entrySet()) {
            String id = entry.getKey();
            int size = entry.getValue().size();
            if (config.contains(id)) {
                Inventory inv = StorageInventory.deserialize(config.getConfigurationSection(id));
                inventories.add(new StorageInventory(id, size, inv));
            } else {
                inventories.add(new StorageInventory(id, size));
            }
            storage.setUnlocked(id, unlocked.contains(id));
        }

        storage.setStorages(inventories);
        playerStorages.put(player.getUniqueId(), storage);
    }

    public void savePlayerData(Player player) {
        UUID uuid = player.getUniqueId();
        if (!playerStorages.containsKey(uuid)) return;

        File file = new File(plugin.getDataFolder(), "data/" + uuid + ".yml");
        YamlConfiguration config = new YamlConfiguration();

        PlayerStorage storage = playerStorages.get(uuid);
        for (StorageInventory inv : storage.getStorages()) {
            config.set(inv.getId(), inv.serialize());
        }
        config.set("unlocked", new ArrayList<>(storage.getUnlockedStorages()));

        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to save storage data for " + player.getName());
        }
    }

    public void saveAllOnlinePlayers() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            savePlayerData(player);
        }
    }

    public void unloadPlayerData(Player player) {
        playerStorages.remove(player.getUniqueId());
    }

    public PlayerStorage getPlayerStorage(Player player) {
        return playerStorages.get(player.getUniqueId());
    }

    public boolean unlockStorage(Player player, String id) {
        PlayerStorage storage = playerStorages.get(player.getUniqueId());
        if (storage != null && definedStorages.containsKey(id) && !storage.isUnlocked(id)) {
            storage.setUnlocked(id, true);
            savePlayerData(player);
            return true;
        }
        return false;
    }

    public Map<String, StorageDefinition> getDefinedStorages() {
        return new HashMap<>(definedStorages);
    }

    public boolean canAccessStorage(Player player, String id) {
        PlayerStorage storage = getPlayerStorage(player);
        StorageDefinition def = definedStorages.get(id);
        return def != null && (storage != null && storage.isUnlocked(id)) || player.hasPermission(def.permission());
    }
}