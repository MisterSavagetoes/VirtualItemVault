package com.mcrebel.papermc.plugin.virtualitemvault.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class VItemTabComplete implements TabCompleter  {
	
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (args.length == 1) {
            suggestions = Arrays.asList("deposit", "withdraw", "balance");
        }

        return suggestions;
    }

}
