package com.mcrebel.papermc.plugin.virtualitemvault.config;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.kyori.adventure.text.Component;

public record ItemDefinition(Material material, String name, List<String> lore, int customModelData, boolean matchEnchants) {

    public static ItemDefinition fromConfig(ConfigurationSection sec) {
        Material mat = Material.matchMaterial(sec.getString("material", "DIAMOND"));
        if (mat == null) {
        	mat = Material.DIAMOND;
        }
        
        String name = sec.getString("name", "");
        
        @SuppressWarnings("unchecked")
        List<String> lore = (List<String>) sec.getList("lore", List.of());
        
        int cmd = sec.getInt("customModelData", -1);
        
        boolean matchEnchants = sec.getBoolean("matchEnchants", false);
        
        return new ItemDefinition(mat, name, lore, cmd, matchEnchants);
    }

    public ItemStack createStack(int amount) {
        ItemStack stack = new ItemStack(material, amount);
        
        if (!name.isBlank() || !lore.isEmpty() || customModelData >= 0) {
            ItemMeta meta = stack.getItemMeta();
            
            if (!name.isBlank()) {
            	meta.displayName(Component.text(name.replace("&", "§")));
            }
            
            if (!lore.isEmpty()) {
            	meta.lore(lore.stream().map(s -> Component.text(s.replace("&", "§"))).toList());
            }
            
            if (customModelData >= 0) {
            	meta.setCustomModelData(customModelData);
            }
            
            stack.setItemMeta(meta);
        }
        
        return stack;
    }
    
}
