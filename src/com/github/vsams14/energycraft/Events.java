package com.github.vsams14.energycraft;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class Events implements Listener {
	private Main main;

	public Events(Main main) {
		main.getServer().getPluginManager().registerEvents(this, main);
		this.main = main;
	}

	@EventHandler
	public void blockPlace(BlockPlaceEvent event) {
		Block b = event.getBlock();
		if ((b.getType() == Material.IRON_BLOCK) || (b.getType() == Material.OBSIDIAN) || (b.getType() == Material.DIAMOND_BLOCK)) {
			if (main.util.allowBuild(b) && event.getPlayer().hasPermission("ec.build")){
				main.util.setCondenser(b, event.getPlayer().getName());
			}else{
				event.setCancelled(true);
			}
		}else if ((b.getType() == Material.CHEST) || (b.getType() == Material.ENCHANTMENT_TABLE)) {
			Block x = b.getWorld().getBlockAt(b.getX(), b.getY() - 1, b.getZ());
			if (main.util.allowBuild(x) && event.getPlayer().hasPermission("ec.build")){
				main.util.setCondenser(x, event.getPlayer().getName());
			}else{
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void blockBreak(BlockBreakEvent event) {
		Block b = event.getBlock();
		Condenser s;
		Player p = event.getPlayer();
		if ((s = main.util.getCondenser(b)) != null) {
			if((p.getName().equals(s.owner)&&p.hasPermission("ec.build"))||
					(p.hasPermission("ec.edit")&&p.hasPermission("ec.build"))){
				if(b.getType()==Material.WALL_SIGN){
					event.setCancelled(true);
				}else{
					main.getServer().broadcastMessage("Broke Condenser " + s.bString());
					main.util.breakCondenser(s);
					main.con.remove(s.toString());
					s = null;
				}	
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void enchant(PlayerInteractEvent event) {
		if (event.getAction()==Action.RIGHT_CLICK_BLOCK) {
			Block b = event.getClickedBlock();
			if ((b.getType() == Material.WALL_SIGN) || (b.getType() == Material.ENCHANTMENT_TABLE)) {
				Player p = event.getPlayer();
				Condenser c;
				if ((c = main.util.getCondenser(b)) != null) {
					if(p.getName().equals(c.owner)||p.hasPermission("ec.edit")){
						ItemStack i = p.getItemInHand();
						if(i.getEnchantments().isEmpty()){
							if (main.conf.getEMC(i) > 0) {
								ItemStack x = i.clone();
								x.setAmount(1);
								c.makesign();
								c.getChests();
								c.out.getBlockInventory().addItem(x);
								p.getInventory().removeItem(x);
								p.updateInventory();
								c.setTarget(i.clone());
								c.updateSign();
							}else if (i.getAmount() <= 0) {
								if (c.pause) {
									c.pause = false;
								}else{
									c.pause = true;
								}
								c.updateSign();
							}	
						}
						event.setCancelled(true);
					}
				}
			}
		}else if(event.getAction() == Action.RIGHT_CLICK_AIR){
			//line of sight blocks = losb
			List<Block> losb = event.getPlayer().getLineOfSight(null, 10);
			for(Block b : losb){
				if(b.getType()==Material.WALL_SIGN){
					Player p = event.getPlayer();
					Condenser c;
					if ((c = main.util.getCondenser(b)) != null) {
						if(p.getName().equals(c.owner)||p.hasPermission("ec.edit")){
							ItemStack i = p.getItemInHand();
							if(i.getEnchantments().isEmpty()){
								if (main.conf.getEMC(i) > 0) {
									ItemStack x = i.clone();
									x.setAmount(1);
									c.makesign();
									c.getChests();
									c.out.getBlockInventory().addItem(x);
									p.getInventory().removeItem(x);
									p.updateInventory();
									c.setTarget(i.clone());
									c.updateSign();
								}else if (i.getAmount() <= 0) {
									if (c.pause) {
										c.pause = false;
									}else{
										c.pause = true;
									}
									c.updateSign();
								}	
							}

							event.setCancelled(true);
							break;	
						}
					}
				}
			}
		}else if(event.getAction() == Action.LEFT_CLICK_BLOCK){
			Block b = event.getClickedBlock();
			if(b.getType() == Material.WALL_SIGN){
				Player p = event.getPlayer();
				Condenser c;
				if ((c = main.util.getCondenser(b)) != null) {
					if(p.getName().equals(c.owner)||p.hasPermission("ec.edit")){
						c.reset(event.getPlayer().getName());
					}
				}
			}
		}
	}
}