package com.inventorywizard;

import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SortCommand implements CommandExecutor, TabCompleter {
    
    private final InventoryWizardPlugin plugin;
    
    public SortCommand(InventoryWizardPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§c🧙✨ The InventoryWizard's magic only works for players!");
            return true;
        }
        
        Player player = (Player) sender;
        
        // Default to inventory if no args
        String sortType = args.length > 0 ? args[0].toLowerCase() : "inventory";
        
        switch (sortType) {
            case "hotbar":
            case "hb":
                if (!player.hasPermission("inventorywizard.hotbar")) {
                    player.sendMessage("§c🧙✨" + ErrorHandler.getPermissionErrorMessage());
                    return true;
                }
                
                // Check rate limiting
                if (!plugin.getRateLimiter().canSort(player)) {
                    long timeRemaining = plugin.getRateLimiter().getTimeUntilNextSort(player);
                    int sortsUsed = plugin.getRateLimiter().getCurrentSortCount(player);
                    String rateLimitMessage = ErrorHandler.getRateLimitErrorMessage(timeRemaining, sortsUsed);
                    player.sendMessage("§c⏰ " + rateLimitMessage);
                    return true;
                }
                
                long startTime = System.currentTimeMillis();
                PlayerSortPreferences.SortMode mode = plugin.getPlayerPreferences().getPlayerSortMode(player);
                InventorySorter.sortHotbar(player, mode);
                
                // Record the sort operation
                plugin.getRateLimiter().recordSort(player);
                
                // Check if sort took too long
                if (plugin.getRateLimiter().isSortTakingTooLong(startTime)) {
                    plugin.getLogger().warning("Sort operation took too long for player: " + player.getName());
                }
                
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.4f);
                player.sendMessage("§6✨ Hotbar enchanted by the InventoryWizard! (" + mode.getDisplayName() + ")");
                break;
                
            case "inventory":
            case "inv":
                if (!player.hasPermission("inventorywizard.inventory")) {
                    player.sendMessage("§c🧙✨ " + ErrorHandler.getPermissionErrorMessage());
                    return true;
                }
                
                // Check rate limiting
                if (!plugin.getRateLimiter().canSort(player)) {
                    long timeRemaining = plugin.getRateLimiter().getTimeUntilNextSort(player);
                    int sortsUsed = plugin.getRateLimiter().getCurrentSortCount(player);
                    String rateLimitMessage = ErrorHandler.getRateLimitErrorMessage(timeRemaining, sortsUsed);
                    player.sendMessage("§c⏰ " + rateLimitMessage);
                    return true;
                }
                
                long invStartTime = System.currentTimeMillis();
                PlayerSortPreferences.SortMode invMode = plugin.getPlayerPreferences().getPlayerSortMode(player);
                InventorySorter.sortPlayerInventory(player, invMode);
                
                // Record the sort operation
                plugin.getRateLimiter().recordSort(player);
                
                // Check if sort took too long
                if (plugin.getRateLimiter().isSortTakingTooLong(invStartTime)) {
                    plugin.getLogger().warning("Sort operation took too long for player: " + player.getName());
                }
                
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.2f);
                player.sendMessage("§a✨ Inventory magically organized! (" + invMode.getDisplayName() + ")");
                break;
                
            case "all":
            case "both":
                if (!player.hasPermission("inventorywizard.all")) {
                    player.sendMessage("§c🧙✨ " + ErrorHandler.getPermissionErrorMessage());
                    return true;
                }
                
                // Check rate limiting
                if (!plugin.getRateLimiter().canSort(player)) {
                    long timeRemaining = plugin.getRateLimiter().getTimeUntilNextSort(player);
                    int sortsUsed = plugin.getRateLimiter().getCurrentSortCount(player);
                    String rateLimitMessage = ErrorHandler.getRateLimitErrorMessage(timeRemaining, sortsUsed);
                    player.sendMessage("§c⏰ " + rateLimitMessage);
                    return true;
                }
                
                long allStartTime = System.currentTimeMillis();
                PlayerSortPreferences.SortMode allMode = plugin.getPlayerPreferences().getPlayerSortMode(player);
                InventorySorter.sortPlayerInventory(player, allMode);
                InventorySorter.sortHotbar(player, allMode);
                
                // Record the sort operation
                plugin.getRateLimiter().recordSort(player);
                
                // Check if sort took too long
                if (plugin.getRateLimiter().isSortTakingTooLong(allStartTime)) {
                    plugin.getLogger().warning("Sort operation took too long for player: " + player.getName());
                }
                
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
                player.sendMessage("§b🧙✨ Complete inventory transformation complete! (" + allMode.getDisplayName() + ")");
                break;
                
            case "regen-credentials":
                if (!player.hasPermission("inventorywizard.admin")) {
                    player.sendMessage("§c🧙✨ " + ErrorHandler.getPermissionErrorMessage());
                    return true;
                }
                plugin.getPlayerPreferences().regenerateCredentials();
                player.sendMessage("§a🔐 Database credentials regenerated successfully!");
                player.sendMessage("§7New credentials have been saved to the configuration file.");
                break;
                
            case "rate-limit":
                if (!player.hasPermission("inventorywizard.admin")) {
                    player.sendMessage("§c🧙✨ " + ErrorHandler.getPermissionErrorMessage());
                    return true;
                }
                String stats = plugin.getRateLimiter().getPlayerStats(player);
                player.sendMessage("§6⏰ Rate Limiting Information:");
                for (String line : stats.split("\n")) {
                    player.sendMessage("§7" + line);
                }
                break;
                
            case "reset-rate-limit":
                if (!player.hasPermission("inventorywizard.admin")) {
                    player.sendMessage("§c🧙✨ " + ErrorHandler.getPermissionErrorMessage());
                    return true;
                }
                plugin.getRateLimiter().resetPlayer(player);
                player.sendMessage("§a⏰ Rate limiting reset for your account!");
                break;
                

                
            default:
                player.sendMessage("§e🧙✨ InventoryWizard Usage: §f/iwiz [hotbar|inventory|all]");
                player.sendMessage("§7Cast your sorting spells with: hotbar, inventory, or all");
                return true;
        }
        
        return true;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> options = Arrays.asList("hotbar", "inventory", "all");
            
            // Add admin commands for admins
            if (sender.hasPermission("inventorywizard.admin")) {
                options = Arrays.asList("hotbar", "inventory", "all", "regen-credentials", "rate-limit", "reset-rate-limit");
            }
            
            return options.stream()
                    .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return null;
    }
}