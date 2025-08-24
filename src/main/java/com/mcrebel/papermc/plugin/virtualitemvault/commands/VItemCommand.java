package com.mcrebel.papermc.plugin.virtualitemvault.commands;

import com.mcrebel.papermc.plugin.virtualitemvault.VirtualItemVaultPlugin;
import com.mcrebel.papermc.plugin.virtualitemvault.config.PluginConfig;
import com.mcrebel.papermc.plugin.virtualitemvault.service.BalanceService;
import com.mcrebel.papermc.plugin.virtualitemvault.util.Text;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VItemCommand implements CommandExecutor {
    private final BalanceService service;
    private final PluginConfig cfg;

    public VItemCommand(VirtualItemVaultPlugin plugin, BalanceService service, PluginConfig cfg) {
        this.service = service;
        this.cfg = cfg;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage("Players only.");
            return true;
        }
        if (!p.hasPermission("viv.use")) {
            p.sendMessage(cfg.prefix() + "§cNo permission.");
            return true;
        }
        if (args.length == 0) {
            p.sendMessage(cfg.prefix() + "§7Usage: /" + label + " <deposit|withdraw|balance> [amount|all]");
            return true;
        }

        String sub = args[0].toLowerCase();
        switch (sub) {
            case "balance" -> handleBalance(p);
            case "deposit" -> handleDeposit(p, args, label);
            case "withdraw" -> handleWithdraw(p, args, label);
            case "reload" -> handleReload(p);
            default -> p.sendMessage(cfg.prefix() + "§7Usage: /" + label + " <deposit|withdraw|balance> [amount|all]");
        }
        return true;
    }

    private void handleBalance(Player p) {
        service.getBalance(p.getUniqueId()).thenAccept(bal ->
            p.sendMessage(cfg.prefix() + cfg.msgBalance().replace("%amount%", Text.formatLong(bal)))
        );
    }

    private void handleDeposit(Player p, String[] args, String label) {
        Integer amount = parseAmountArg(args);
        
        if (amount == null) {
            p.sendMessage(cfg.prefix() + cfg.msgNotNumber());
            p.sendMessage(cfg.prefix() + "§7Usage: /" + label + " deposit <amount|all>");
            return;
        }
        
        service.deposit(
            p,
            amount,
            () -> p.sendMessage(cfg.prefix() + cfg.msgNoItem()),
            deposited -> p.sendMessage(cfg.prefix() + cfg.msgDeposited().replace("%amount%", Text.formatLong(deposited)))
        );
    }

    private void handleWithdraw(Player p, String[] args, String label) {
        Integer amount = parseAmountArg(args);
        if (amount == null) {
            p.sendMessage(cfg.prefix() + cfg.msgNotNumber());
            p.sendMessage(cfg.prefix() + "§7Usage: /" + label + " withdraw <amount|all>");
            return;
        }
        
        service.withdraw(
            p,
            amount,
            () -> p.sendMessage(cfg.prefix() + cfg.msgNotEnoughStored()),
            withdrew -> p.sendMessage(cfg.prefix() + cfg.msgWithdrew().replace("%amount%", Text.formatLong(withdrew))),
            leftover -> p.sendMessage(cfg.prefix() + cfg.msgInventoryFull().replace("%leftover%", Text.formatLong(leftover)))
        );
    }
    
    private void handleReload(Player p) {
        if (!p.hasPermission("viv.use.reload")) {
            p.sendMessage(cfg.prefix() + "§cNo permission.");
        }

        cfg.loadConfig();
    	service.init();
    }

    private Integer parseAmountArg(String[] args) {
        if (args.length < 2) {
        	return null;
        }
        
        String s = args[1];
        if (s.equalsIgnoreCase("all")) {
        	return -1;
        }
        
        try {
            int n = Integer.parseInt(s);
            return (n > 0) ? n : null;
        } catch (NumberFormatException e) {
            return null;
		}
	}
}
