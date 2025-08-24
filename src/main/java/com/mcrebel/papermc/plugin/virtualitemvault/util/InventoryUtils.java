package com.mcrebel.papermc.plugin.virtualitemvault.util;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.mcrebel.papermc.plugin.virtualitemvault.config.ItemDefinition;

public class InventoryUtils {

	public static int countMatching(Inventory inv, ItemDefinition def) {
		int total = 0;
		
		for (ItemStack it : inv.getContents()) {
			if (it != null && ItemMatcher.matches(it, def)) {
				total += it.getAmount();
			}
		}
		
		return total;
	}

	public static int removeMatching(Inventory inv, ItemDefinition def, int amount) {
		int toRemove = amount;
		
		for (int i = 0; i < inv.getSize(); i++) {
			ItemStack it = inv.getItem(i);
			
			if (it == null || !ItemMatcher.matches(it, def)) {
				continue;
			}
			
			int take = Math.min(toRemove, it.getAmount());
			it.setAmount(it.getAmount() - take);
			
			if (it.getAmount() <= 0) {
				inv.setItem(i, null);
			}
			
			toRemove -= take;
			if (toRemove <= 0) {
				break;
			}
		}
		return amount - toRemove;
	}
	
}
