package com.mcrebel.papermc.plugin.virtualitemvault.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import com.mcrebel.papermc.plugin.virtualitemvault.VirtualItemVaultPlugin;
import com.mcrebel.papermc.plugin.virtualitemvault.config.PluginConfig;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class Database {
	private HikariDataSource ds;

	public Database(VirtualItemVaultPlugin plugin, PluginConfig cfg) {
		HikariConfig hc = new HikariConfig();
		String jdbc = "jdbc:mysql://" + cfg.host() + ":" + cfg.port() + "/" + cfg.database() + "?useSSL=" + cfg.useSSL()
				+ "&allowPublicKeyRetrieval=true&serverTimezone=UTC";
		hc.setJdbcUrl(jdbc);
		hc.setUsername(cfg.username());
		hc.setPassword(cfg.password());
		hc.setMaximumPoolSize(cfg.maxPool());
		hc.setPoolName("VIV-Hikari");
		ds = new HikariDataSource(hc);

		try (Connection c = getConnection(); Statement st = c.createStatement()) {
			st.executeUpdate(("CREATE TABLE IF NOT EXISTS %s (" + "  player_uuid CHAR(36) PRIMARY KEY, "
					+ "  amount BIGINT NOT NULL "
					+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4")
					.formatted(cfg.tableName()));
		} catch (SQLException e) {
			plugin.getLogger().severe("Failed to init database: " + e.getMessage());
		}
	}

	public Connection getConnection() throws SQLException {
		return ds.getConnection();
	}

	public void shutdown() {
		if (ds != null)
			ds.close();
	}
}
