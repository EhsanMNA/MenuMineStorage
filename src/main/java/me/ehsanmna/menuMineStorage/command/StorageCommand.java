package me.ehsanmna.menuMineStorage.command;

import me.ehsanmna.menuMineStorage.MenuMineStorage;
import me.ehsanmna.menuMineStorage.models.PlayerStorage;
import me.ehsanmna.menuMineStorage.models.StorageInventory;
import me.ehsanmna.menuMineStorage.utils.StorageGUI;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StorageCommand implements CommandExecutor, TabCompleter {

    private final MenuMineStorage plugin;
    private boolean menuMineUse = false;

    public StorageCommand(MenuMineStorage plugin) {
        this.plugin = plugin;
        menuMineUse = Bukkit.getPluginManager().isPluginEnabled("MenuMine");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getConfig().getString("messages.only-players"));
            return true;
        }

        PlayerStorage storage = plugin.getStorageManager().getPlayerStorage(player);

        if (args.length == 0) {
            if (menuMineUse) {
                StorageGUI.openGUI(player); // Open GUI instead of first storage
                return true;
            }
            if (storage != null && !storage.getStorages().isEmpty()) {
                StorageInventory inv = storage.getStorages().get(0);
                if (plugin.getStorageManager().canAccessStorage(player, inv.getId())) {
                    player.openInventory(inv.getInventory());
                } else {
                    player.sendMessage(plugin.getConfig().getString("messages.storage-locked"));
                }
            }
            return true;
        }

        StorageInventory inv = storage.getStorage(args[0]);
        if (inv != null) {
            if (plugin.getStorageManager().canAccessStorage(player, args[0])) {
                player.openInventory(inv.getInventory());
            } else {
                player.sendMessage(plugin.getConfig().getString("messages.storage-locked"));
            }
        } else {
            player.sendMessage(plugin.getConfig().getString("messages.storage-not-found").replace("{id}", args[0]));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player) || args.length != 1) return new ArrayList<>();

        Player player = (Player) sender;
        PlayerStorage storage = plugin.getStorageManager().getPlayerStorage(player);
        if (storage == null) return new ArrayList<>();

        return storage.getStorages().stream()
                .map(StorageInventory::getId)
                .filter(id -> id.startsWith(args[0]))
                .collect(Collectors.toList());
    }
}