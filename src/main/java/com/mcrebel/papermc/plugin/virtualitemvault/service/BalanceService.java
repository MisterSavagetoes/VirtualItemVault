package com.mcrebel.papermc.plugin.virtualitemvault.service;

import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.mcrebel.papermc.plugin.virtualitemvault.VirtualItemVaultPlugin;
import com.mcrebel.papermc.plugin.virtualitemvault.config.LoadListener;
import com.mcrebel.papermc.plugin.virtualitemvault.config.PluginConfig;
import com.mcrebel.papermc.plugin.virtualitemvault.db.BalanceRepository;
import com.mcrebel.papermc.plugin.virtualitemvault.db.Database;
import com.mcrebel.papermc.plugin.virtualitemvault.exception.NotEnoughFunds;
import com.mcrebel.papermc.plugin.virtualitemvault.util.InventoryUtils;

public class BalanceService implements LoadListener {
	private VirtualItemVaultPlugin plugin;
	private Database db;
	private BalanceRepository repo;
	private PluginConfig config;

	public BalanceService(VirtualItemVaultPlugin plugin, PluginConfig config) {
		this.plugin = plugin;
		this.config = config;

		init();

		config.listener(this);
	}

	@Override
	public void onLoad() {
		init();
	}

	public void init() {
		Database oldDb = db;
		db = new Database(plugin, config);
		repo = new BalanceRepository(db, config);

		if (oldDb != null) {
			oldDb.shutdown();
		}
	}

	public CompletableFuture<Integer> getBalance(UUID uuid) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return repo.getBalance(uuid);
			} catch (SQLException e) {
				plugin.getLogger().warning("getBalance failed: " + e.getMessage());
				return 0;
			}
		});
	}

	public void deposit(Player player, int amount, Runnable onNoItems, java.util.function.LongConsumer onSuccess) {
		Bukkit.getScheduler().runTask(plugin, () -> {
			int available = InventoryUtils.countMatching(player.getInventory(), config.itemDefinition());
			int toTake = (amount == -1 ? available : Math.min(amount, available));
			if (toTake <= 0) {
				onNoItems.run();
				return;
			}

			int removed = InventoryUtils.removeMatching(player.getInventory(), config.itemDefinition(),
					Math.min(Integer.MAX_VALUE, toTake));
			
			if (removed <= 0) {
				onNoItems.run();
				return;
			}

			CompletableFuture.runAsync(() -> {
				try {
					repo.addToBalance(player.getUniqueId(), removed);
				} catch (SQLException e) {
					plugin.getLogger().warning("deposit DB error: " + e.getMessage());
					Bukkit.getScheduler().runTask(plugin, () -> giveItems(player, removed));
					return;
				}
				
				Bukkit.getScheduler().runTask(plugin, () -> onSuccess.accept(removed));
			});
		});
	}

	public void withdraw(Player player, int amount, Runnable onNotEnough, java.util.function.IntConsumer onSuccess,
			java.util.function.IntConsumer onInventoryFull) 
	{
		CompletableFuture.runAsync(() -> {
			int stored;
			try {
				stored = repo.getBalance(player.getUniqueId());
			} catch (SQLException e) {
				plugin.getLogger().warning("withdraw DB read error: " + e.getMessage());
				stored = 0;
			}
			
			int toGive = (amount == -1) ? stored : Math.min(amount, stored);
			if (toGive <= 0) {
				Bukkit.getScheduler().runTask(plugin, onNotEnough);
				return;
			}

			try {
				repo.subtractFromBalance(player.getUniqueId(), toGive);
			} catch (SQLException | NotEnoughFunds e) {
				plugin.getLogger().warning("withdraw DB write error: " + e.getMessage());
				Bukkit.getScheduler().runTask(plugin, onNotEnough);
				return;
			}

			Bukkit.getScheduler().runTask(plugin, () -> {
				int leftover = giveItems(player, toGive);
				
				if (leftover > 0) {
					onInventoryFull.accept(leftover);
				}
				
				onSuccess.accept(toGive);
			});
		});
	}

	private int giveItems(Player player, int amount) {
		ItemStack template = config.itemDefinition().createStack(1);
		int maxStack = template.getMaxStackSize();
		int remaining = amount;
		
		while (remaining > 0) {
			int give = Math.min(remaining, maxStack);
			ItemStack part = template.clone();
			part.setAmount(give);
			
			Map<Integer, ItemStack>	left = player.getInventory().addItem(part);
			if (!left.isEmpty()) {
				give -= left.values().stream().mapToInt(ItemStack::getAmount).sum();
				return remaining - give;
			}
			
			remaining -= give;
		}
		return 0;
	}
	
	public void shutdown() {
		if(db != null) {
			db.shutdown();
		}
	}

}
