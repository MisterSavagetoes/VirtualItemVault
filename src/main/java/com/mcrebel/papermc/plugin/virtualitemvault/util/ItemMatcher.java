package com.mcrebel.papermc.plugin.virtualitemvault.util;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.mcrebel.papermc.plugin.virtualitemvault.config.ItemDefinition;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

public class ItemMatcher {

	public static boolean matches(ItemStack stack, ItemDefinition def) {
		if (stack == null || stack.getType() != def.material()) {
			return false;
		}
		
		ItemMeta meta = stack.getItemMeta();
		if (meta == null) {
			return (def.name() == null || def.name().isBlank()) && (def.lore() == null || def.lore().isEmpty()) && def.customModelData() < 0;
		}

		if (!def.name().isBlank()) {
			Component expected = Component.text(def.name().replace("&", "§"));
			if (!Objects.equals(meta.displayName(), expected)) {
				return false;
			}
		} else {
			if(meta.displayName() != null) {
				return false;
			}
		}
		
		if (!def.lore().isEmpty()) {
			List<TextComponent> expected = def.lore().stream().map(s -> Component.text(s.replace("&", "§"))).toList();
			if (!Objects.equals(meta.lore(), expected)) {
				return false;
			}
		} else {
			if(meta.lore() != null && meta.lore().size() > 0) {
				return false;
			}
		}
		
		if (def.customModelData() >= 0) {
			if (!meta.hasCustomModelData() || meta.getCustomModelData() != def.customModelData()) {
				return false;
			}
		} else {
			if(meta.hasCustomModelData()) {
				return false;
			}
		}
		
		if (def.matchEnchants()) {
			Map<Enchantment, Integer> expected = Map.of();
			if (!meta.getEnchants().equals(expected)) {
				return false;
			}
		}
		
		return true;
	}
}
