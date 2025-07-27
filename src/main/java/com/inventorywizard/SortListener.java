package com.inventorywizard;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

public class SortListener implements Listener {
    
    private final PlayerSortPreferences preferences;
    private final InventoryWizardPlugin plugin;
    
    public SortListener(InventoryWizardPlugin plugin) {
        this.preferences = plugin.getPlayerPreferences();
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        
        // Handle Shift+Right Click for main inventory/chest sorting
        if (event.getClick() == ClickType.SHIFT_RIGHT) {
            
            if (!player.hasPermission("inventorywizard.use")) {
                return;
            }
            
            Inventory clickedInventory = event.getClickedInventory();
            
            if (clickedInventory == null) {
                return;
            }
            
            // Check if clicking in hotbar slots (0-8) - sort hotbar or combined
            if (clickedInventory.getType() == InventoryType.PLAYER && 
                event.getSlot() >= 0 && event.getSlot() <= 8) {
                
                event.setCancelled(true);
                
                // Special case: Slot 4 (middle hotbar slot) cycles sorting modes
                if (event.getSlot() == 4) {
                    PlayerSortPreferences.SortMode newMode = preferences.cyclePlayerSortMode(player);
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.6f);
                    player.sendMessage("§e🔄 Sorting mode changed to: §6" + newMode.getDisplayName());
                    return;
                }
                
                // Check rate limiting first
                if (!plugin.getRateLimiter().canSort(player)) {
                    long timeRemaining = plugin.getRateLimiter().getTimeUntilNextSort(player);
                    int sortsUsed = plugin.getRateLimiter().getCurrentSortCount(player);
                    String rateLimitMessage = ErrorHandler.getRateLimitErrorMessage(timeRemaining, sortsUsed);
                    player.sendMessage("§c⏰ " + rateLimitMessage);
                    return;
                }
                
                long startTime = System.currentTimeMillis();
                
                // Check for combined sorting permission first
                if (player.hasPermission("inventorywizard.all")) {
                    PlayerSortPreferences.SortMode mode = preferences.getPlayerSortMode(player);
                    InventorySorter.sortPlayerInventory(player, mode);
                    InventorySorter.sortHotbar(player, mode);
                    
                    // Record the sort operation
                    plugin.getRateLimiter().recordSort(player);
                    
                    // Check if sort took too long
                    if (plugin.getRateLimiter().isSortTakingTooLong(startTime)) {
                        plugin.getLogger().warning("Sort operation took too long for player: " + player.getName());
                    }
                    
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
                    player.sendMessage("§b🧙✨ Complete inventory enchanted by the InventoryWizard! (" + mode.getDisplayName() + ")");
                }
                // Fall back to hotbar-only sorting
                else if (player.hasPermission("inventorywizard.hotbar")) {
                    PlayerSortPreferences.SortMode mode = preferences.getPlayerSortMode(player);
                    InventorySorter.sortHotbar(player, mode);
                    
                    // Record the sort operation
                    plugin.getRateLimiter().recordSort(player);
                    
                    // Check if sort took too long
                    if (plugin.getRateLimiter().isSortTakingTooLong(startTime)) {
                        plugin.getLogger().warning("Sort operation took too long for player: " + player.getName());
                    }
                    
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.4f);
                    player.sendMessage("§6✨ Hotbar organized by the InventoryWizard! (" + mode.getDisplayName() + ")");
                }
                return;
            }
            
            // Cancel the event to prevent normal shift-right-click behavior
            event.setCancelled(true);
            
            // Check if it's a chest inventory
            if (clickedInventory.getType() == InventoryType.CHEST && 
                player.hasPermission("inventorywizard.chest")) {
                
                // Check rate limiting first
                if (!plugin.getRateLimiter().canSort(player)) {
                    long timeRemaining = plugin.getRateLimiter().getTimeUntilNextSort(player);
                    int sortsUsed = plugin.getRateLimiter().getCurrentSortCount(player);
                    String rateLimitMessage = ErrorHandler.getRateLimitErrorMessage(timeRemaining, sortsUsed);
                    player.sendMessage("§c⏰ " + rateLimitMessage);
                    return;
                }
                
                long startTime = System.currentTimeMillis();
                PlayerSortPreferences.SortMode mode = preferences.getPlayerSortMode(player);
                InventorySorter.sortInventory(clickedInventory, mode);
                
                // Record the sort operation
                plugin.getRateLimiter().recordSort(player);
                
                // Check if sort took too long
                if (plugin.getRateLimiter().isSortTakingTooLong(startTime)) {
                    plugin.getLogger().warning("Sort operation took too long for player: " + player.getName());
                }
                
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.2f);
                player.sendMessage("§a✨ Chest magically sorted! (" + mode.getDisplayName() + ")");
                
            } 
            // Check if it's player inventory (main inventory slots, excluding hotbar)
            else if (clickedInventory.getType() == InventoryType.PLAYER && 
                     event.getSlot() > 8 && event.getSlot() < 36 &&
                     player.hasPermission("inventorywizard.inventory")) {
                
                // Check rate limiting first
                if (!plugin.getRateLimiter().canSort(player)) {
                    long timeRemaining = plugin.getRateLimiter().getTimeUntilNextSort(player);
                    int sortsUsed = plugin.getRateLimiter().getCurrentSortCount(player);
                    String rateLimitMessage = ErrorHandler.getRateLimitErrorMessage(timeRemaining, sortsUsed);
                    player.sendMessage("§c⏰ " + rateLimitMessage);
                    return;
                }
                
                long startTime = System.currentTimeMillis();
                PlayerSortPreferences.SortMode mode = preferences.getPlayerSortMode(player);
                InventorySorter.sortPlayerInventory(player, mode);
                
                // Record the sort operation
                plugin.getRateLimiter().recordSort(player);
                
                // Check if sort took too long
                if (plugin.getRateLimiter().isSortTakingTooLong(startTime)) {
                    plugin.getLogger().warning("Sort operation took too long for player: " + player.getName());
                }
                
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.2f);
                player.sendMessage("§a✨ Inventory organized with wizard magic! (" + mode.getDisplayName() + ")");
            }
        }
        
        // Handle Double Click for hotbar sorting (alternative method)
        else if (event.getClick() == ClickType.DOUBLE_CLICK) {
            
            if (!player.hasPermission("inventorywizard.hotbar")) {
                return;
            }
            
            Inventory clickedInventory = event.getClickedInventory();
            
            // Only if clicking in hotbar area
            if (clickedInventory != null && clickedInventory.getType() == InventoryType.PLAYER && 
                event.getSlot() >= 0 && event.getSlot() <= 8) {
                
                event.setCancelled(true);
                
                // Check rate limiting first
                if (!plugin.getRateLimiter().canSort(player)) {
                    long timeRemaining = plugin.getRateLimiter().getTimeUntilNextSort(player);
                    int sortsUsed = plugin.getRateLimiter().getCurrentSortCount(player);
                    String rateLimitMessage = ErrorHandler.getRateLimitErrorMessage(timeRemaining, sortsUsed);
                    player.sendMessage("§c⏰ " + rateLimitMessage);
                    return;
                }
                
                long startTime = System.currentTimeMillis();
                PlayerSortPreferences.SortMode mode = preferences.getPlayerSortMode(player);
                InventorySorter.sortHotbar(player, mode);
                
                // Record the sort operation
                plugin.getRateLimiter().recordSort(player);
                
                // Check if sort took too long
                if (plugin.getRateLimiter().isSortTakingTooLong(startTime)) {
                    plugin.getLogger().warning("Sort operation took too long for player: " + player.getName());
                }
                
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.4f);
                player.sendMessage("§6✨ Hotbar arranged by wizardry! (" + mode.getDisplayName() + ")");
            }
        }
        

    }
}