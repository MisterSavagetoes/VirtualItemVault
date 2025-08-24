package com.mcrebel.papermc.plugin.virtualitemvault.config;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import com.mcrebel.papermc.plugin.virtualitemvault.VirtualItemVaultPlugin;
import com.mcrebel.papermc.plugin.virtualitemvault.util.Color;

public class PluginConfig {
	private VirtualItemVaultPlugin plugin;
	
	private ItemDefinition item;
	private String singularName = "Diamond";
	private String pluralName = "Diamonds";
	private String prefix, msgNoItem, msgDeposited, msgWithdrew, msgBalance, msgNotNumber, msgNotEnoughStored,
			msgInventoryFull;

	private String host, database, username, password;
	private String tableName;
	private int port, maxPool;
	private boolean useSSL;
	
	private List<LoadListener> listeners = new ArrayList<>();

	public PluginConfig(VirtualItemVaultPlugin plugin) {
		this.plugin = plugin;
		loadConfig();
	}
	
	public void listener(LoadListener listener) {
		listeners.add(listener);
	}
	
	public void loadConfig() {
		
		FileConfiguration cfg = plugin.getConfig();

		ConfigurationSection mysql = cfg.getConfigurationSection("mysql");
		host = mysql.getString("host");
		port = mysql.getInt("port");
		database = mysql.getString("database");
		username = mysql.getString("username");
		password = mysql.getString("password");
		useSSL = mysql.getBoolean("useSSL");
		maxPool = mysql.getConfigurationSection("pool").getInt("maxPoolSize");
		tableName = mysql.getString("tableName", "virtual_item_balances");

		ConfigurationSection i = cfg.getConfigurationSection("item");
		item = ItemDefinition.fromConfig(i);
		singularName = i.getString("singularName", "Diamond");
		pluralName = i.getString("pluralName", "Diamonds");

		ConfigurationSection m = cfg.getConfigurationSection("messages");
		prefix = Color.color(m.getString("prefix"));
		msgNoItem = Color.color(m.getString("noItem"));
		msgDeposited = Color.color(m.getString("deposited"));
		msgWithdrew = Color.color(m.getString("withdrew"));
		msgBalance = Color.color(m.getString("balance"));
		msgNotNumber = Color.color(m.getString("notNumber"));
		msgNotEnoughStored = Color.color(m.getString("notEnoughStored"));
		msgInventoryFull = Color.color(m.getString("inventoryFull"));
		
		for(LoadListener listener : listeners) {
			listener.onLoad();
		}
	}

	public ItemDefinition itemDefinition() {
		return item;
	}

	public String tableName() {
		return tableName;
	}

	public String prefix() {
		return prefix;
	}

	public String msgNoItem() {
		return msgNoItem;
	}

	public String msgDeposited() {
		return msgDeposited;
	}

	public String msgWithdrew() {
		return msgWithdrew;
	}

	public String msgBalance() {
		return msgBalance;
	}

	public String msgNotNumber() {
		return msgNotNumber;
	}

	public String msgNotEnoughStored() {
		return msgNotEnoughStored;
	}

	public String msgInventoryFull() {
		return msgInventoryFull;
	}
	
	public String host() {
		return host;
	}
	
	public String database() {
		return database;
	}
	
	public String username() {
		return username;
	}
	
	public String password() {
		return password;
	}
	
	public int port() {
		return port;
	}
	
	public int maxPool() {
		return maxPool;
	}
	
	public boolean useSSL() {
		return useSSL;
	}
	
	public String singularName() {
		return singularName;
	}
	
	public String pluralName() {
		return pluralName;
	}
}
