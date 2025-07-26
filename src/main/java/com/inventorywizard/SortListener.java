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
    
    public SortListener(InventoryWizardPlugin plugin) {
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
                
                // Check for combined sorting permission first
                if (player.hasPermission("inventorywizard.all")) {
                    InventorySorter.sortPlayerInventory(player, InventoryWizardPlugin.allowPartialStacksInventory);
                    InventorySorter.sortHotbar(player);
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
                    player.sendMessage("§b🧙‍♂️ Complete inventory enchanted by the InventoryWizard!");
                }
                // Fall back to hotbar-only sorting
                else if (player.hasPermission("inventorywizard.hotbar")) {
                    InventorySorter.sortHotbar(player);
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.4f);
                    player.sendMessage("§6✨ Hotbar organized by the InventoryWizard!");
                }
                return;
            }
            
            // Cancel the event to prevent normal shift-right-click behavior
            event.setCancelled(true);
            
            // Check if it's a chest inventory
            if (clickedInventory.getType() == InventoryType.CHEST && 
                player.hasPermission("inventorywizard.chest")) {
                InventorySorter.sortInventory(clickedInventory, InventoryWizardPlugin.allowPartialStacksChest);
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.2f);
                player.sendMessage("§a✨ Chest magically sorted!");
            } 
            // Check if it's player inventory (main inventory slots, excluding hotbar)
            else if (clickedInventory.getType() == InventoryType.PLAYER && 
                     event.getSlot() > 8 && event.getSlot() < 36 &&
                     player.hasPermission("inventorywizard.inventory")) {
                InventorySorter.sortPlayerInventory(player, InventoryWizardPlugin.allowPartialStacksInventory);
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.2f);
                player.sendMessage("§a✨ Inventory organized with wizard magic!");
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
                InventorySorter.sortHotbar(player);
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.4f);
                player.sendMessage("§6✨ Hotbar arranged by wizardry!");
            }
        }
        

    }
}