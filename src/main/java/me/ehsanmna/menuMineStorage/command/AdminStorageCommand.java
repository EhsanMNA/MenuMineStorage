package me.ehsanmna.menuMineStorage.command;

import me.ehsanmna.menuMineStorage.MenuMineStorage;
import me.ehsanmna.menuMineStorage.models.PlayerStorage;
import me.ehsanmna.menuMineStorage.models.StorageInventory;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AdminStorageCommand implements CommandExecutor, TabCompleter {

    private final MenuMineStorage plugin;

    public AdminStorageCommand(MenuMineStorage plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("storage.admin")) {
            sender.sendMessage(plugin.getConfig().getString("messages.no-permission"));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(plugin.getConfig().getString("messages.admin-usage"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(plugin.getConfig().getString("messages.player-not-found").replace("{player}", args[1]));
            return true;
        }

        PlayerStorage storage = plugin.getStorageManager().getPlayerStorage(target);
        if (storage == null) {
            sender.sendMessage(plugin.getConfig().getString("messages.player-no-data").replace("{player}", args[1]));
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "unlock":
                if (args.length != 3) {
                    sender.sendMessage(plugin.getConfig().getString("messages.admin-usage"));
                    return true;
                }
                if (plugin.getStorageManager().unlockStorage(target, args[2])) {
                    sender.sendMessage(plugin.getConfig().getString("messages.storage-unlocked")
                            .replace("{player}", args[1])
                            .replace("{id}", args[2]));
                } else {
                    sender.sendMessage(plugin.getConfig().getString("messages.storage-unlock-failed")
                            .replace("{id}", args[2]));
                }
                break;
            case "view":
                if (args.length != 3) {
                    sender.sendMessage(plugin.getConfig().getString("messages.admin-usage"));
                    return true;
                }
                StorageInventory inv = storage.getStorage(args[2]);
                if (inv != null && sender instanceof Player) {
                    ((Player) sender).openInventory(inv.getInventory());
                } else {
                    sender.sendMessage(plugin.getConfig().getString("messages.storage-not-found")
                            .replace("{id}", args[2]));
                }
                break;
            default:
                sender.sendMessage(plugin.getConfig().getString("messages.admin-usage"));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("storage.admin")) return new ArrayList<>();

        if (args.length == 1) {
            return Arrays.asList("unlock", "view").stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (args.length == 2) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (args.length == 3) {
            Player target = Bukkit.getPlayer(args[1]);
            if (target != null) {
                PlayerStorage storage = plugin.getStorageManager().getPlayerStorage(target);
                if (storage != null) {
                    return storage.getStorages().stream()
                            .map(StorageInventory::getId)
                            .filter(id -> id.startsWith(args[2]))
                            .collect(Collectors.toList());
                }
            }
        }
        return new ArrayList<>();
    }
}
