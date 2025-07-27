package com.inventorywizard;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.sql.*;
import java.util.UUID;
import java.util.logging.Level;

public class H2DatabaseManager {
    
    private final Plugin plugin;
    private final String dbPath;
    private Connection connection;
    
    public H2DatabaseManager(Plugin plugin) {
        this.plugin = plugin;
        this.dbPath = new File(plugin.getDataFolder(), "player_preferences").getAbsolutePath();
        initializeDatabase();
        startH2Console();
    }
    
    private void startH2Console() {
        try {
            // Start H2 console with basic parameters for better compatibility
            int port = 8082;
            String[] args = {"-web", "-webAllowOthers", "-webPort", String.valueOf(port)};
            org.h2.tools.Server server = org.h2.tools.Server.createWebServer(args);
            server.start();
            
            plugin.getLogger().info("H2 Console started at: http://localhost:" + port);
            plugin.getLogger().info("Database URL: jdbc:h2:" + dbPath);
            plugin.getLogger().info("Username: sa, Password: (empty)");
            plugin.getLogger().info("Note: Console may only be accessible from localhost for security");
            
        } catch (Exception e) {
            plugin.getLogger().warning("Could not start H2 console: " + e.getMessage());
            plugin.getLogger().info("Database is still working - you can use /iwiz db to view stats");
        }
    }
    
    private void initializeDatabase() {
        try {
            // Create database directory if it doesn't exist
            plugin.getDataFolder().mkdirs();
            
            // Load H2 driver explicitly (relocated in shaded JAR)
            try {
                Class.forName("org.h2.Driver");
            } catch (ClassNotFoundException e) {
                // Try the relocated version
                Class.forName("com.inventorywizard.libs.h2.Driver");
            }
            
            // Connect to H2 database with minimal configuration
            String url = "jdbc:h2:" + dbPath;
            connection = DriverManager.getConnection(url, "sa", "");
            
            // Create table if it doesn't exist
            createTable();
            
            plugin.getLogger().info("H2 database initialized successfully!");
            
        } catch (ClassNotFoundException e) {
            plugin.getLogger().log(Level.SEVERE, "H2 driver not found. Please ensure the plugin is properly built.", e);
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to initialize H2 database: " + e.getMessage(), e);
        }
    }
    
    private void createTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS player_preferences (" +
                    "uuid VARCHAR(36) PRIMARY KEY, " +
                    "sort_mode INT DEFAULT 0, " +
                    "last_updated BIGINT DEFAULT 0)";
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }
    
    public PlayerSortPreferences.SortMode getPlayerSortMode(Player player) {
        String uuid = player.getUniqueId().toString();
        
        String sql = "SELECT sort_mode FROM player_preferences WHERE uuid = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, uuid);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int modeId = rs.getInt("sort_mode");
                    return PlayerSortPreferences.SortMode.fromId(modeId);
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.WARNING, "Failed to get sort mode for player: " + player.getName(), e);
        }
        
        return PlayerSortPreferences.SortMode.DEFAULT;
    }
    
    public void setPlayerSortMode(Player player, PlayerSortPreferences.SortMode mode) {
        String uuid = player.getUniqueId().toString();
        long timestamp = System.currentTimeMillis();
        
        // Try INSERT first, if it fails due to duplicate key, use UPDATE
        String insertSql = "INSERT INTO player_preferences (uuid, sort_mode, last_updated) VALUES (?, ?, ?)";
        String updateSql = "UPDATE player_preferences SET sort_mode = ?, last_updated = ? WHERE uuid = ?";
        
        try (PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
            insertStmt.setString(1, uuid);
            insertStmt.setInt(2, mode.getId());
            insertStmt.setLong(3, timestamp);
            
            insertStmt.executeUpdate();
            
        } catch (SQLException e) {
            // If insert fails (duplicate key), try update
            try (PreparedStatement updateStmt = connection.prepareStatement(updateSql)) {
                updateStmt.setInt(1, mode.getId());
                updateStmt.setLong(2, timestamp);
                updateStmt.setString(3, uuid);
                
                updateStmt.executeUpdate();
                
            } catch (SQLException updateException) {
                plugin.getLogger().log(Level.WARNING, "Failed to set sort mode for player: " + player.getName(), updateException);
            }
        }
    }
    
    public PlayerSortPreferences.SortMode cyclePlayerSortMode(Player player) {
        PlayerSortPreferences.SortMode currentMode = getPlayerSortMode(player);
        PlayerSortPreferences.SortMode nextMode = currentMode.next();
        setPlayerSortMode(player, nextMode);
        return nextMode;
    }
    
    public void close() {
        if (connection != null) {
            try {
                connection.close();
                plugin.getLogger().info("H2 database connection closed.");
            } catch (SQLException e) {
                plugin.getLogger().log(Level.WARNING, "Error closing database connection", e);
            }
        }
    }
    
    // Utility method to get database statistics
    public int getPlayerCount() {
        String sql = "SELECT COUNT(*) FROM player_preferences";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.WARNING, "Failed to get player count", e);
        }
        
        return 0;
    }
    
    // Method to migrate from YAML if needed
    public void migrateFromYaml(PlayerSortPreferences yamlPreferences) {
        plugin.getLogger().info("Starting migration from YAML to H2...");
        
        // This would be implemented if we want to migrate existing YAML data
        // For now, we'll start fresh with H2
        plugin.getLogger().info("H2 database ready for new data!");
    }
} 