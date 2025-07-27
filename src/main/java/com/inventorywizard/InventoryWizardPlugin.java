package com.inventorywizard;

import org.bukkit.plugin.java.JavaPlugin;

public class InventoryWizardPlugin extends JavaPlugin {
    
    private PlayerSortPreferences playerPreferences;
    private RateLimiter rateLimiter;
    
    @Override
    public void onEnable() {
        // Initialize rate limiter
        rateLimiter = new RateLimiter();
        
        // Initialize player preferences
        playerPreferences = new PlayerSortPreferences(this);
        
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
        getLogger().info("Both: Shift+Right-click in hotbar (with all permission)");
        getLogger().info("New: Shift+Right-click in hotbar slot 4 to cycle sorting modes!");
        getLogger().info("Storage: H2 database for optimal performance! 🚀");
        getLogger().info("Rate limiting: Enabled to prevent resource exhaustion! 🛡️");
        getLogger().info("Cast your sorting spells wisely! 🧙‍♂️");
    }
    
    public PlayerSortPreferences getPlayerPreferences() {
        return playerPreferences;
    }
    
    public RateLimiter getRateLimiter() {
        return rateLimiter;
    }
    
    @Override
    public void onDisable() {
        if (playerPreferences != null) {
            playerPreferences.close();
        }
        getLogger().info("InventoryWizard is resting... The magic will return! ✨");
    }
}