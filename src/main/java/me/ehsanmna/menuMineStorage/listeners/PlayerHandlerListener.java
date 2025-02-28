package me.ehsanmna.menuMineStorage.listeners;

import me.ehsanmna.menuMineStorage.manager.StorageManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerHandlerListener implements Listener {

    StorageManager storageManager;

    public PlayerHandlerListener(StorageManager storageManager){
        this.storageManager = storageManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        storageManager.loadPlayerData(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        storageManager.savePlayerData(event.getPlayer());
        storageManager.unloadPlayerData(event.getPlayer());
    }


}
