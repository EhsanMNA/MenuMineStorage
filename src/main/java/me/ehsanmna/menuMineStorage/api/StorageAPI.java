package me.ehsanmna.menuMineStorage.api;

import me.ehsanmna.menuMineStorage.MenuMineStorage;
import me.ehsanmna.menuMineStorage.models.PlayerStorage;
import me.ehsanmna.menuMineStorage.models.StorageDefinition;
import me.ehsanmna.menuMineStorage.models.StorageInventory;
import org.bukkit.entity.Player;

import java.util.Map;

public class StorageAPI {

    private static MenuMineStorage getPlugin() {
        return MenuMineStorage.getInstance();
    }

    public static PlayerStorage getPlayerStorage(Player player) {
        return getPlugin().getStorageManager().getPlayerStorage(player);
    }

    public static StorageInventory getStorage(Player player, String id) {
        PlayerStorage storage = getPlayerStorage(player);
        return storage != null ? storage.getStorage(id) : null;
    }

    public static boolean unlockStorage(Player player, String id) {
        return getPlugin().getStorageManager().unlockStorage(player, id);
    }

    public static boolean isStorageUnlocked(Player player, String id) {
        PlayerStorage storage = getPlayerStorage(player);
        return storage != null && storage.isUnlocked(id);
    }

    public static boolean canAccessStorage(Player player, String id) {
        return getPlugin().getStorageManager().canAccessStorage(player, id);
    }

    public static Map<String, StorageDefinition> getDefinedStorages() {
        return getPlugin().getStorageManager().getDefinedStorages();
    }
}
