package me.ehsanmna.menuMineStorage;

import me.ehsanmna.menuMineStorage.command.AdminStorageCommand;
import me.ehsanmna.menuMineStorage.command.StorageCommand;
import me.ehsanmna.menuMineStorage.listeners.PlayerHandlerListener;
import me.ehsanmna.menuMineStorage.manager.StorageManager;
import me.ehsanmna.menuMineStorage.utils.StorageGUI;
import org.bukkit.plugin.java.JavaPlugin;

public final class MenuMineStorage extends JavaPlugin {

    private StorageManager storageManager;
    private static MenuMineStorage instance;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        storageManager = new StorageManager(this);
        getServer().getPluginManager().registerEvents(new PlayerHandlerListener(storageManager), this);

        StorageGUI.setup(this); // Initialize GUI

        // Register commands with tab completers
        StorageCommand storageCmd = new StorageCommand(this);
        getCommand("storage").setExecutor(storageCmd);
        getCommand("storage").setTabCompleter(storageCmd);

        AdminStorageCommand adminCmd = new AdminStorageCommand(this);
        getCommand("storageadmin").setExecutor(adminCmd);
        getCommand("storageadmin").setTabCompleter(adminCmd);

    }

    @Override
    public void onDisable() {
        storageManager.saveAllOnlinePlayers();
    }

    public StorageManager getStorageManager() {
        return storageManager;
    }

    public static MenuMineStorage getInstance() {
        return instance;
    }
}
