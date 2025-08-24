package com.mcrebel.papermc.plugin.virtualitemvault.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import com.mcrebel.papermc.plugin.virtualitemvault.config.PluginConfig;
import com.mcrebel.papermc.plugin.virtualitemvault.exception.NotEnoughFunds;

public class BalanceRepository {
    private final Database db;
    private final PluginConfig config;

    public BalanceRepository(Database db, PluginConfig config) {
        this.db = db;
        this.config = config;

    }
    
    public Database getDatabase() {
    	return db;
    }

    public int getBalance(UUID uuid) throws SQLException {
        String sql = "SELECT amount FROM " + config.tableName() + " WHERE player_uuid=?";
        try (Connection c = db.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                	return rs.getInt(1);
                }
                
                return 0;
            }
        }
    }

    public void setBalance(UUID uuid, int amount) throws SQLException {
        String sql = "INSERT INTO " + config.tableName() + " (player_uuid, amount) VALUES (?, ?) ON DUPLICATE KEY UPDATE amount=VALUES(amount)";
        try (Connection c = db.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            ps.setInt(2, amount);
            ps.executeUpdate();
        }
    }
    
    public void subtractFromBalance(UUID uuid, int delta) throws SQLException, NotEnoughFunds {
    	addToBalance(uuid, delta * -1);
    	if(getBalance(uuid) < 0) {
    		addToBalance(uuid, delta);
    		throw new NotEnoughFunds();
    	}
    }

    public void addToBalance(UUID uuid, int delta) throws SQLException {
        String sql = "INSERT INTO " + config.tableName() + " (player_uuid, amount) VALUES (?, ?) ON DUPLICATE KEY UPDATE amount=amount+VALUES(amount)";
        try (Connection c = db.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            ps.setInt(2, delta);
            ps.executeUpdate();
        }
    }
}
