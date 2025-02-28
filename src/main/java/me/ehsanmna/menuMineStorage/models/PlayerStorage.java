package me.ehsanmna.menuMineStorage.models;

import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PlayerStorage {

    private final Player player;
    private List<StorageInventory> storages;
    private final Set<String> unlockedStorages = new HashSet<>();

    public PlayerStorage(Player player) {
        this.player = player;
        this.storages = new ArrayList<>();
    }

    public StorageInventory getStorage(String id) {
        return storages.stream()
                .filter(s -> s.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public List<StorageInventory> getStorages() {
        return new ArrayList<>(storages);
    }

    public void setStorages(List<StorageInventory> storages) {
        this.storages = new ArrayList<>(storages);
    }

    public void setUnlocked(String id, boolean unlocked) {
        if (unlocked) {
            unlockedStorages.add(id);
        } else {
            unlockedStorages.remove(id);
        }
    }

    public boolean isUnlocked(String id) {
        return unlockedStorages.contains(id);
    }

    public Set<String> getUnlockedStorages() {
        return new HashSet<>(unlockedStorages);
    }

    public Player getPlayer() {
        return player;
    }
}