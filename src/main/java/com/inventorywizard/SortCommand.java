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
            sender.sendMessage("§c🧙‍♂️ The InventoryWizard's magic only works for players!");
            return true;
        }
        
        Player player = (Player) sender;
        
        // Default to inventory if no args
        String sortType = args.length > 0 ? args[0].toLowerCase() : "inventory";
        
        switch (sortType) {
            case "hotbar":
            case "hb":
                if (!player.hasPermission("inventorywizard.hotbar")) {
                    player.sendMessage("§c🧙‍♂️ You lack the magical permission to organize your hotbar!");
                    return true;
                }
                InventorySorter.sortHotbar(player);
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.4f);
                player.sendMessage("§6✨ Hotbar enchanted by the InventoryWizard!");
                break;
                
            case "inventory":
            case "inv":
                if (!player.hasPermission("inventorywizard.inventory")) {
                    player.sendMessage("§c🧙‍♂️ Your magical privileges are insufficient for inventory sorting!");
                    return true;
                }
                InventorySorter.sortPlayerInventory(player, InventoryWizardPlugin.allowPartialStacksInventory);
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.2f);
                player.sendMessage("§a✨ Inventory magically organized!");
                break;
                
            case "all":
            case "both":
                if (!player.hasPermission("inventorywizard.all")) {
                    player.sendMessage("§c🧙‍♂️ You need master-level permissions for complete inventory wizardry!");
                    return true;
                }
                InventorySorter.sortPlayerInventory(player, InventoryWizardPlugin.allowPartialStacksInventory);
                InventorySorter.sortHotbar(player);
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
                player.sendMessage("§b🧙‍♂️ Complete inventory transformation complete!");
                break;
                
            case "db":
                if (!player.hasPermission("inventorywizard.admin")) {
                    player.sendMessage("§c🧙‍♂️ You need admin permissions to view database information!");
                    return true;
                }
                int playerCount = plugin.getPlayerPreferences().getPlayerCount();
                player.sendMessage("§6🗄️ Database Statistics:");
                player.sendMessage("§eTotal players with preferences: §f" + playerCount);
                player.sendMessage("§eH2 Console: §fhttp://localhost:8082");
                player.sendMessage("§eDatabase file: §fplugins/InventoryWizard/player_preferences.mv.db");
                break;
                
            default:
                player.sendMessage("§e🧙‍♂️ InventoryWizard Usage: §f/iwiz [hotbar|inventory|all]");
                player.sendMessage("§7Cast your sorting spells with: hotbar, inventory, or all");
                return true;
        }
        
        return true;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> options = Arrays.asList("hotbar", "inventory", "all");
            
            // Add db command for admins
            if (sender.hasPermission("inventorywizard.admin")) {
                options = Arrays.asList("hotbar", "inventory", "all", "db");
            }
            
            return options.stream()
                    .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return null;
    }
}