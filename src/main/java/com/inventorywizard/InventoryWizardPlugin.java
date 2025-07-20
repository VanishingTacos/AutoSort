package com.inventorywizard;

import org.bukkit.plugin.java.JavaPlugin;

public class InventoryWizardPlugin extends JavaPlugin {
    
    @Override
    public void onEnable() {
        // Register event listener
        getServer().getPluginManager().registerEvents(new SortListener(this), this);
        
        // Register command
        SortCommand sortCommand = new SortCommand(this);
        getCommand("iwiz").setExecutor(sortCommand);
        getCommand("iwiz").setTabCompleter(sortCommand);
        
        // Save default config
        saveDefaultConfig();
        
        getLogger().info("InventoryWizard has awakened! ✨");
        getLogger().info("Commands: /iwiz [hotbar|inventory|all]");
        getLogger().info("Hotbar: Shift+Right-click in hotbar OR Double-click in hotbar");
        getLogger().info("Inventory: Shift+Right-click in main inventory");
        getLogger().info("Both: Middle-click anywhere in inventory");
        getLogger().info("Cast your sorting spells wisely! 🧙‍♂️");
    }
    
    @Override
    public void onDisable() {
        getLogger().info("InventoryWizard is resting... The magic will return! ✨");
    }
}