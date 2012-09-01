package com.github.vsams14.energycraft;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EMCCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String label, String[] args) {
		if (label.equalsIgnoreCase("emc") && (arg0 instanceof Player)) {
			Player p = (Player) arg0;
			if (p.getItemInHand() == null) {
				p.sendMessage("You are not holding anything.");
				return true;
			}
			p.sendMessage("The EMC value of: " + Main.instance.conf.getName(p.getItemInHand()) + " is " + Main.instance.conf.getEMC(p.getItemInHand()));
			return true;
		}
		return true;
	}
}