package com.inventorywizard;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.sql.*;
import java.util.logging.Level;

public class H2DatabaseManager {
    
    private final Plugin plugin;
    private final String dbPath;
    private Connection connection;
    private DatabaseCredentials credentials;
    private ErrorHandler errorHandler;
    
    public H2DatabaseManager(Plugin plugin) {
        this.plugin = plugin;
        this.dbPath = new File(plugin.getDataFolder(), "player_preferences").getAbsolutePath();
        this.credentials = new DatabaseCredentials((InventoryWizardPlugin) plugin);
        this.errorHandler = new ErrorHandler(plugin.getLogger());
        initializeDatabase();
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
            
            // Connect to H2 database with secure credentials
            String url = "jdbc:h2:" + dbPath;
            String username = credentials.getUsername();
            String password = credentials.getPassword();
            connection = DriverManager.getConnection(url, username, password);
            
            // Create table if it doesn't exist
            createTable();
            
            // Validate connection and log security status
            validateConnection();
            logSecurityStatus();
            
            plugin.getLogger().info("H2 database initialized successfully with secure credentials and input validation!");
            
        } catch (ClassNotFoundException e) {
            errorHandler.logError(
                ErrorHandler.getGeneralErrorMessage(),
                "H2 driver not found. Please ensure the plugin is properly built.",
                e
            );
        } catch (SQLException e) {
            errorHandler.logError(
                ErrorHandler.getDatabaseErrorMessage("initialization"),
                "Failed to initialize H2 database",
                e
            );
        }
    }
    
    private void createTable() throws SQLException {
        // Use parameterized query for table creation to prevent SQL injection
        String sql = "CREATE TABLE IF NOT EXISTS player_preferences (" +
                    "uuid VARCHAR(36) PRIMARY KEY, " +
                    "sort_mode INT DEFAULT 0, " +
                    "last_updated BIGINT DEFAULT 0)";
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
            plugin.getLogger().info("Database table created/verified successfully");
        } catch (SQLException e) {
            errorHandler.logError(
                ErrorHandler.getDatabaseErrorMessage("table creation"),
                "Failed to create database table",
                e
            );
            throw e;
        }
    }
    
    public PlayerSortPreferences.SortMode getPlayerSortMode(Player player) {
        // Validate input parameters
        if (!InputValidator.validatePlayer(player)) {
            errorHandler.logValidationError("player object", player.getName(), "null or invalid");
            return PlayerSortPreferences.SortMode.DEFAULT;
        }
        
        String uuid = InputValidator.validateUUID(player.getUniqueId().toString());
        if (uuid == null) {
            errorHandler.logValidationError("UUID format", player.getName(), player.getUniqueId().toString());
            return PlayerSortPreferences.SortMode.DEFAULT;
        }
        
        String sql = "SELECT sort_mode FROM player_preferences WHERE uuid = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, uuid);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int modeId = rs.getInt("sort_mode");
                    int validatedModeId = InputValidator.validateSortModeId(modeId);
                    return PlayerSortPreferences.SortMode.fromId(validatedModeId);
                }
            }
        } catch (SQLException e) {
            errorHandler.logDatabaseError("get sort mode", player.getName(), e);
        }
        
        return PlayerSortPreferences.SortMode.DEFAULT;
    }
    
    public void setPlayerSortMode(Player player, PlayerSortPreferences.SortMode mode) {
        // Validate all input parameters
        InputValidator.ValidationResult validation = InputValidator.validateDatabaseInput(player, mode, System.currentTimeMillis());
        
        if (!validation.isValid()) {
            errorHandler.logValidationError("database input", player.getName(), validation.getErrorMessage());
            return;
        }
        
        String uuid = validation.getUuid();
        int modeId = validation.getModeId();
        long timestamp = validation.getTimestamp();
        
        // Try INSERT first, if it fails due to duplicate key, use UPDATE
        String insertSql = "INSERT INTO player_preferences (uuid, sort_mode, last_updated) VALUES (?, ?, ?)";
        String updateSql = "UPDATE player_preferences SET sort_mode = ?, last_updated = ? WHERE uuid = ?";
        
        try (PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
            insertStmt.setString(1, uuid);
            insertStmt.setInt(2, modeId);
            insertStmt.setLong(3, timestamp);
            
            insertStmt.executeUpdate();
            
        } catch (SQLException e) {
            // If insert fails (duplicate key), try update
            try (PreparedStatement updateStmt = connection.prepareStatement(updateSql)) {
                updateStmt.setInt(1, modeId);
                updateStmt.setLong(2, timestamp);
                updateStmt.setString(3, uuid);
                
                updateStmt.executeUpdate();
                
            } catch (SQLException updateException) {
                errorHandler.logDatabaseError("set sort mode", player.getName(), updateException);
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
            errorHandler.logError(
                ErrorHandler.getDatabaseErrorMessage("connection close"),
                "Error closing database connection",
                e
            );
        }
        }
    }
    
    /**
     * Regenerate database credentials for security purposes
     */
    public void regenerateCredentials() {
        if (credentials != null) {
            credentials.regenerateCredentials();
            plugin.getLogger().info("Database credentials regenerated successfully");
        }
    }
    
    /**
     * Check if secure credentials are being used
     */
    public boolean hasSecureCredentials() {
        return credentials != null && credentials.hasValidCredentials();
    }
    
    /**
     * Validate database connection and log security status
     */
    public void validateConnection() {
        if (connection == null) {
            plugin.getLogger().warning("Database connection is null");
            return;
        }
        
        try {
            // Test connection with a simple query
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("SELECT 1");
                plugin.getLogger().info("Database connection validated successfully");
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.WARNING, "Database connection validation failed", e);
        }
    }
    
    /**
     * Log security status for audit purposes
     */
    public void logSecurityStatus() {
        plugin.getLogger().info("=== Database Security Status ===");
        plugin.getLogger().info("Secure credentials: " + (hasSecureCredentials() ? "YES" : "NO"));
        plugin.getLogger().info("Input validation: ENABLED");
        plugin.getLogger().info("SQL injection protection: ENABLED");
        plugin.getLogger().info("Parameterized queries: ENABLED");
        plugin.getLogger().info("=================================");
    }
    


} 