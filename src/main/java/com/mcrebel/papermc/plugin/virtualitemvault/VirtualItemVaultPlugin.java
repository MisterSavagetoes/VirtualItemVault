package com.mcrebel.papermc.plugin.virtualitemvault;

import org.bukkit.plugin.java.JavaPlugin;

import com.mcrebel.papermc.plugin.virtualitemvault.commands.VItemCommand;
import com.mcrebel.papermc.plugin.virtualitemvault.commands.VItemTabComplete;
import com.mcrebel.papermc.plugin.virtualitemvault.config.PluginConfig;
import com.mcrebel.papermc.plugin.virtualitemvault.service.BalanceService;

public class VirtualItemVaultPlugin extends JavaPlugin {
    private PluginConfig config;
    private BalanceService service;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        
        this.config = new PluginConfig(this);

        this.service = new BalanceService(this, config);

        if (getCommand("vitem") != null) {
            getCommand("vitem").setExecutor(new VItemCommand(this, service, config));
            getCommand("vitem").setTabCompleter(new VItemTabComplete());
        }
        getLogger().info("VirtualItemVault enabled.");
    }

    @Override
    public void onDisable() {
        if (service != null) {
        	service.shutdown();
        }
        
        getLogger().info("VirtualItemVault disabled.");
    }
}
