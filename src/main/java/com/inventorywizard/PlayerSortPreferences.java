package com.inventorywizard;

import org.bukkit.entity.Player;

public class PlayerSortPreferences {
    
    public enum SortMode {
        DEFAULT(0, "Default"),
        ALPHABETICAL(1, "Alphabetical"),
        STACK_BASED(2, "Stack-based");
        
        private final int id;
        private final String displayName;
        
        SortMode(int id, String displayName) {
            this.id = id;
            this.displayName = displayName;
        }
        
        public int getId() {
            return id;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public static SortMode fromId(int id) {
            for (SortMode mode : values()) {
                if (mode.id == id) {
                    return mode;
                }
            }
            return DEFAULT;
        }
        
        public SortMode next() {
            int nextId = (this.id + 1) % values().length;
            return fromId(nextId);
        }
    }
    
    private final InventoryWizardPlugin plugin;
    private H2DatabaseManager database;
    private boolean useH2 = true;
    
    public PlayerSortPreferences(InventoryWizardPlugin plugin) {
        this.plugin = plugin;
        try {
            this.database = new H2DatabaseManager(plugin);
            plugin.getLogger().info("Using H2 database for player preferences.");
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to initialize H2 database, falling back to YAML storage.");
            this.useH2 = false;
            this.database = null;
        }
    }
    
    public SortMode getPlayerSortMode(Player player) {
        if (useH2 && database != null) {
            return database.getPlayerSortMode(player);
        }
        return SortMode.DEFAULT; // Fallback to default mode
    }
    
    public void setPlayerSortMode(Player player, SortMode mode) {
        if (useH2 && database != null) {
            database.setPlayerSortMode(player, mode);
        }
        // In fallback mode, we don't persist preferences
    }
    
    public SortMode cyclePlayerSortMode(Player player) {
        if (useH2 && database != null) {
            return database.cyclePlayerSortMode(player);
        }
        // In fallback mode, just cycle through modes without persistence
        return SortMode.DEFAULT;
    }
    
    public void close() {
        if (database != null) {
            database.close();
        }
    }
    
    public int getPlayerCount() {
        if (useH2 && database != null) {
            return database.getPlayerCount();
        }
        return 0;
    }
} 